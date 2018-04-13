package com.katch.perfer.mahout.service;

import java.io.File;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component("mahoutRecommenderService")
@ConditionalOnProperty(name = "consumer.mahout.type", havingValue = "item", matchIfMissing = false)
public class ItemMahoutRecommenderService extends MahoutRecommenderService {
	
	private static Logger logger = LoggerFactory.getLogger(ItemMahoutRecommenderService.class);

	@Override
	public void excute() throws Exception {
		logger.info("用户消费记录准备计算!");
		File file = new File(consumerExportCSVProperties.getFileName());
		DataModel dataModel = new FileDataModel(file);
		ItemSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
		ItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel, similarity);
		logger.info("用户消费记录计算完成,准备入库!");
		saveUserRecommender(dataModel, recommender);
		logger.info("用户消费记录计算完成,入库完成!");
	}

}
