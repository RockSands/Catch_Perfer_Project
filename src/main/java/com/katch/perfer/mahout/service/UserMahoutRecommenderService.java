package com.katch.perfer.mahout.service;

import java.io.File;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.katch.perfer.config.ConsumerExportCSVProperties;

@Component("mahoutRecommenderService")
@ConditionalOnProperty(name = "consumer.mahout.type", havingValue = "user", matchIfMissing = true)
public class UserMahoutRecommenderService extends MahoutRecommenderService {
	private static Logger logger = LoggerFactory.getLogger(UserMahoutRecommenderService.class);

	@Autowired
	private ConsumerExportCSVProperties consumerExportCSVProperties;

	@Override
	public void excute() throws Exception {
		logger.info("用户消费记录准备计算!");
		File file = new File(consumerExportCSVProperties.getExportFileName());
		DataModel dataModel = new FileDataModel(file);
		UserSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
		UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(100, similarity, dataModel);
		Recommender recommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, similarity);
		logger.info("用户消费记录计算完成,准备入库!");
		saveUserRecommender(dataModel, recommender);
		logger.info("用户消费记录计算完成,入库完成!");
	}
}
