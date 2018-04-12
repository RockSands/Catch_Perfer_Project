package com.katch.perfer.mahout.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.katch.perfer.config.ConsumerExportCSVProperties;

public abstract class MahoutRecommenderService {
	@Autowired
	protected ConsumerExportCSVProperties consumerExportCSVProperties;

	public abstract void excute() throws Exception;

	public ConsumerExportCSVProperties getConsumerExportCSVProperties() {
		return consumerExportCSVProperties;
	}

	public void setConsumerExportCSVProperties(ConsumerExportCSVProperties consumerExportCSVProperties) {
		this.consumerExportCSVProperties = consumerExportCSVProperties;
	}
}
