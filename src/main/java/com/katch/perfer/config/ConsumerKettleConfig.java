package com.katch.perfer.config;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.katch.perfer.kettle.bean.KettleJobEntireDefine;
import com.katch.perfer.kettle.metas.KettleSelectSQLMeta;
import com.katch.perfer.kettle.metas.KettleTextOutputMeta;
import com.katch.perfer.kettle.metas.builder.SqlDataExportBuilder;
import com.katch.perfer.service.kettle.ItemRecommendCSV2DBBuild;
import com.katch.perfer.service.kettle.UserRecommendCSV2DBBuild;

@Configuration
@EnableAutoConfiguration
public class ConsumerKettleConfig {
	
	@Autowired
	private ConsumerExportDBProperties consumerExportDBProperties;

	@Autowired
	private ConsumerExportCSVProperties consumerExportCSVProperties;

	/**
	 * 消费记录导出
	 * 
	 * @return
	 * @throws Exception
	 */
	@Bean
	public KettleJobEntireDefine consumerExportJobDefine() throws Exception {
		KettleSelectSQLMeta consumer = new KettleSelectSQLMeta();
		KettleTextOutputMeta textExport = new KettleTextOutputMeta();
		BeanUtils.copyProperties(consumerExportDBProperties, consumer);
		BeanUtils.copyProperties(consumerExportCSVProperties, textExport);
		return SqlDataExportBuilder.newBuilder().sqlData(consumer).txtExport(textExport).createJob();
	}
	
	@Bean
	public KettleJobEntireDefine itemRecommendCSV2DBBuild() throws Exception {
		ItemRecommendCSV2DBBuild itemRecommendCSV2DBBuild = new ItemRecommendCSV2DBBuild();
		return itemRecommendCSV2DBBuild.createJob();
	}
	
	@Bean
	public KettleJobEntireDefine userRecommendCSV2DBBuild() throws Exception {
		UserRecommendCSV2DBBuild userRecommendCSV2DBBuild = new UserRecommendCSV2DBBuild();
		return userRecommendCSV2DBBuild.createJob();
	}
}
