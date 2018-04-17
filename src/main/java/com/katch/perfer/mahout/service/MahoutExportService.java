package com.katch.perfer.mahout.service;

import java.text.DecimalFormat;

import org.apache.mahout.cf.taste.model.DataModel;
import org.springframework.beans.factory.annotation.Autowired;

import com.katch.perfer.config.ConsumerExportCSVProperties;

public abstract class MahoutExportService {

	@Autowired
	protected ConsumerExportCSVProperties consumerExportCSVProperties;

	protected DataModel dataModel;

	/**
	 * 格式化
	 */
	protected DecimalFormat df = new DecimalFormat("##0.###");

	/**
	 * 执行
	 * 
	 * @throws Exception
	 */
	public abstract void excute() throws Exception;

	public ConsumerExportCSVProperties getConsumerExportCSVProperties() {
		return consumerExportCSVProperties;
	}

	public void setConsumerExportCSVProperties(ConsumerExportCSVProperties consumerExportCSVProperties) {
		this.consumerExportCSVProperties = consumerExportCSVProperties;
	}

	public DataModel getDataModel() {
		return dataModel;
	}

	public void setDataModel(DataModel dataModel) {
		this.dataModel = dataModel;
	}

}
