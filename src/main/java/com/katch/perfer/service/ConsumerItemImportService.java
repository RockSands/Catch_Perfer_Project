package com.katch.perfer.service;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.exception.KettleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.katch.perfer.kettle.bean.KettleResult;
import com.katch.perfer.kettle.consist.KettleVariables;
import com.katch.perfer.kettle.service.KettleNorthService;
import com.katch.perfer.mahout.service.UserMahoutRecommenderService;
import com.katch.perfer.service.kettle.UserRecommendCSV2DBBuild;

public class ConsumerItemImportService {

	private static Logger logger = LoggerFactory.getLogger(UserMahoutRecommenderService.class);

	@Autowired
	private KettleNorthService kettleNorthService;

	/**
	 * 修正
	 * 
	 * @throws KettleException
	 * @throws InterruptedException
	 * 
	 * @throws Exception
	 */
	public String excute() throws KettleException, InterruptedException {
		String uuid = doImport();
		kettleRecordDaemon thread = new kettleRecordDaemon();
		thread.kettleUUID = uuid;
		thread.start();
		thread.join();
		logger.info("商品推荐表导入完成!");
		return thread.errorMsg;
	}

	/**
	 * 导出
	 * 
	 * @return
	 * @throws KettleException
	 */
	private String doImport() throws KettleException {
		logger.info("商品推荐表记录开始导入!");
		UserRecommendCSV2DBBuild cSVTablesIncSyncBuilder = new UserRecommendCSV2DBBuild();
		KettleResult result = kettleNorthService.excuteJobOnce(cSVTablesIncSyncBuilder.createJob());
		if (StringUtils.isNotEmpty(result.getErrMsg())) {
			throw new KettleException("Kettle商品推荐表记录导入失败,kettle发生问题:" + result.getErrMsg());
		}
		return result.getUuid();
	}

	private class kettleRecordDaemon extends Thread {

		private String kettleUUID;

		private String errorMsg;

		@Override
		public void run() {
			try {
				long startTime = System.currentTimeMillis();
				while (true) {
					Thread.sleep(10000L);
					KettleResult result = kettleNorthService.queryJob(kettleUUID);
					System.out.println("=导入Status=>" + result.getStatus());
					if (KettleVariables.RECORD_STATUS_FINISHED.equals(result.getStatus())) {
						break;
					} else if (KettleVariables.RECORD_STATUS_ERROR.equals(result.getStatus())) {
						errorMsg = result.getErrMsg();
						break;
					} else if (!KettleVariables.RECORD_STATUS_RUNNING.equals(result.getStatus())) {
						errorMsg = "Kettle的任务[" + kettleUUID + "]状态[" + result.getStatus() + "]异常!";
						break;
					} else if (System.currentTimeMillis() - startTime > 10L * 1000L * 60L) {
						errorMsg = "Kettle的任务执行超时!";
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
