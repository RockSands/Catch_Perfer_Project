package com.katch.perfer.service;

import org.pentaho.di.core.exception.KettleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.katch.perfer.kettle.bean.KettleResult;
import com.katch.perfer.kettle.consist.KettleVariables;
import com.katch.perfer.kettle.service.KettleNorthService;

@Component
public class ConsumerKettlePolling {

	private static Logger logger = LoggerFactory.getLogger(ConsumerKettlePolling.class);

	private String kettleUUID;

	@Autowired
	private KettleNorthService kettleNorthService;

	@Autowired
	private ConsumerExportService consumerExportService;

	/**
	 * 执行
	 * @throws KettleException
	 */
	public String excute() {
		try {
			Thread.sleep(5000L);
			KettleResult result;
			while (true) {
				result = kettleNorthService.queryJob(kettleUUID);
				if (KettleVariables.RECORD_STATUS_FINISHED.equals(result.getStatus())) {
					break;
				}
				if (KettleVariables.RECORD_STATUS_ERROR.equals(result.getStatus())) {
					logger.error("Kettle导出消费记录失败!!!");
				}
				Thread.sleep(10000L);
			}
			return "error";
		} catch (Exception e) {
			logger.error("Kettle导出消费记录失败!!!");
			return "error";
		}
	}

	public void setKettleUUID(String kettleUUID) {
		this.kettleUUID = kettleUUID;
	}

	public KettleNorthService getKettleNorthService() {
		return kettleNorthService;
	}

	public void setKettleNorthService(KettleNorthService kettleNorthService) {
		this.kettleNorthService = kettleNorthService;
	}

	public ConsumerExportService getConsumerExportService() {
		return consumerExportService;
	}

	public void setConsumerExportService(ConsumerExportService consumerExportService) {
		this.consumerExportService = consumerExportService;
	}
}
