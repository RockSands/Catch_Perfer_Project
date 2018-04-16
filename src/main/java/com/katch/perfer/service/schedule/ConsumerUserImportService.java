package com.katch.perfer.service.schedule;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.exception.KettleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.katch.perfer.kettle.bean.KettleResult;
import com.katch.perfer.kettle.consist.KettleVariables;
import com.katch.perfer.kettle.service.KettleNorthService;
import com.katch.perfer.service.kettle.UserRecommendCSV2DBBuild;

/**
 * 消费记录导出
 * 
 * @author Administrator
 *
 */
@Service()
@ConditionalOnProperty(name = "consumer.mahout.type", havingValue = "user", matchIfMissing = true)
public class ConsumerUserImportService implements ConsumerImportService {
	private static Logger logger = LoggerFactory.getLogger(ConsumerUserImportService.class);

	@Autowired
	private KettleNorthService kettleNorthService;

	@Autowired
	private UserRecommendCSV2DBBuild userRecommendCSV2DBBuild;

	/**
	 * 修正
	 * 
	 * @throws KettleException
	 * @throws InterruptedException
	 * 
	 * @throws Exception
	 */
	@Override
	public String excute() {
		try {
			String uuid = doImport();
			kettleRecordDaemon thread = new kettleRecordDaemon();
			thread.kettleUUID = uuid;
			thread.start();
			thread.join();
			logger.info("用户商品推荐表导入完成!");
			return thread.errorMsg;
		} catch (Exception e) {
			logger.error("用户商品推荐表导入失败!", e);
			return "用户商品推荐表导入失败!";
		}

	}

	/**
	 * 导出
	 * 
	 * @return
	 * @throws KettleException
	 */
	private String doImport() throws KettleException {
		logger.info("用户商品推荐表记录开始导入!");
		KettleResult result = kettleNorthService.excuteJobOnce(userRecommendCSV2DBBuild.createJob());
		if (StringUtils.isNotEmpty(result.getErrMsg())) {
			throw new KettleException("Kettle用户商品推荐表记录导入失败,kettle发生问题:" + result.getErrMsg());
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