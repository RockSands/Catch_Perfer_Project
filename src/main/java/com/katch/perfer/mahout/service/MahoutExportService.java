package com.katch.perfer.mahout.service;

import java.text.DecimalFormat;

import org.apache.mahout.cf.taste.model.DataModel;
import org.springframework.beans.factory.annotation.Autowired;

import com.katch.perfer.config.RecommendProperties;

public abstract class MahoutExportService {

	@Autowired
	protected RecommendProperties recommendPropeties;

	/**
	 * Mahout计算
	 */
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
	public void excute() throws Exception{
		if(dataModel == null) {
			throw new Exception("推荐记录导出发生异常,DataModel未找到!");
		}
		export();
	}
	
	public abstract void export() throws Exception;

	public DataModel getDataModel() {
		return dataModel;
	}

	public void setDataModel(DataModel dataModel) {
		this.dataModel = dataModel;
	}

	public RecommendProperties getRecommendPropeties() {
		return recommendPropeties;
	}

	public void setRecommendPropeties(RecommendProperties recommendPropeties) {
		this.recommendPropeties = recommendPropeties;
	}
}
