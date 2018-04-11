package com.katch.perfer.service;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.katch.perfer.kettle.bean.KettleJobEntireDefine;
import com.katch.perfer.kettle.bean.KettleResult;
import com.katch.perfer.kettle.service.KettleNorthService;
import com.katch.perfer.mahout.service.MahoutConsumerService;

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

	@Autowired
	private MahoutConsumerService mahoutConsumerService;
	
	/**
	 * 导出
	 * 
	 * @return
	 * @throws KettleException
	 */
	public String doExport() throws KettleException {
		KettleResult result = kettleNorthService.excuteJobOnce(exportJobDefine);
		if (StringUtils.isNotEmpty(result.getErrMsg())) {
			throw new KettleException("Kettle导出消费记录失败,kettle发生问题:" + result.getErrMsg());
		}
		return result.getUuid();
	}

	/**
	 * 计算
	 */
	public void doCompute() {
	}
}
