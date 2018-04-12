package com.katch.perfer.service;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.katch.perfer.kettle.bean.KettleJobEntireDefine;
import com.katch.perfer.kettle.bean.KettleResult;
import com.katch.perfer.kettle.consist.KettleVariables;
import com.katch.perfer.kettle.service.KettleNorthService;

/**
 * 消费记录导出
 * 
 * @author Administrator
 *
 */
@Service
public class ConsumerExportService {

	@Autowired
	@Qualifier("consumerExportJobDefine")
	private KettleJobEntireDefine exportJobDefine;

	@Autowired
	private KettleNorthService kettleNorthService;

	/**
	 * 状态
	 */
	private Long lastUpdateTime;
	
	private String status = "FREE";

	/**
	 * 修正
	 * @throws KettleException 
	 * @throws InterruptedException 
	 * 
	 * @throws Exception
	 */
	public String excute() throws KettleException, InterruptedException {
		synchronized(status) {
			if(!"FREE".equals(status)) {
				return "用户喜好记录刷新中!";
			}
			if (lastUpdateTime != null && System.currentTimeMillis() - lastUpdateTime < 10L * 1000L * 60L) {
				return "用户喜好记录刷新间隔必须超过5分钟!";
			}
			lastUpdateTime = System.currentTimeMillis();
			status = "RUNNING";
		}
		String uuid = doExport();
		kettleRecordDaemon thread = new kettleRecordDaemon();
		thread.kettleUUID = uuid;
		thread.start();
		thread.join();
		lastUpdateTime = System.currentTimeMillis();
		status = "FREE";
		return thread.errorMsg;
	}

	/**
	 * 导出
	 * 
	 * @return
	 * @throws KettleException
	 */
	private String doExport() throws KettleException {
		KettleResult result = kettleNorthService.excuteJobOnce(exportJobDefine);
		if (StringUtils.isNotEmpty(result.getErrMsg())) {
			throw new KettleException("Kettle导出消费记录失败,kettle发生问题:" + result.getErrMsg());
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
					System.out.println("=Status=>" + result.getStatus());
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
