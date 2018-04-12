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
	 * 修正
	 * 
	 * @throws Exception
	 */
	public void excute() throws Exception {
		String kettleUUID = doExport();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						Thread.sleep(10000L);
						KettleResult result = kettleNorthService.queryJob(kettleUUID);
						System.out.println("=Status=>" + result.getStatus());
						if (KettleVariables.RECORD_STATUS_FINISHED.equals(result.getStatus())) {
							break;
						} else if (KettleVariables.RECORD_STATUS_ERROR.equals(result.getStatus())) {
							throw new Exception(result.getErrMsg());
						} else if (!KettleVariables.RECORD_STATUS_RUNNING.equals(result.getStatus())) {
							throw new Exception("Kettle的任务[" + kettleUUID + "]状态[" + result.getStatus() + "]异常!");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
		thread.start();
		thread.join();
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
}
