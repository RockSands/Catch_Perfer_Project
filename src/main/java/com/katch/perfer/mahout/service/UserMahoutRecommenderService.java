package com.katch.perfer.mahout.service;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.katch.perfer.config.ConsumerExportCSVProperties;

@Service("mahoutRecommenderService")
@ConditionalOnProperty(name = "consumer.mahout.type", havingValue = "user", matchIfMissing = true)
public class UserMahoutRecommenderService extends MahoutRecommenderService {
	private static Logger logger = LoggerFactory.getLogger(UserMahoutRecommenderService.class);

	@Autowired
	private ConsumerExportCSVProperties consumerExportCSVProperties;

	@Override
	public void excute() throws Exception {
		UserSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
		UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(100, similarity, dataModel);
		Recommender recommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, similarity);
		logger.info("用户消费记录计算完成,准备入库!");
		saveUserRecommenderFile(dataModel, recommender);
		logger.info("用户消费记录计算完成,入库完成!");
	}

	/**
	 * 
	 * @param dataModel
	 * @throws Exception
	 */
	private void saveUserRecommenderFile(DataModel dataModel, Recommender recommender) throws Exception {
		logger.debug("消费推荐导出文件准备导出!");
		LongPrimitiveIterator it = dataModel.getUserIDs();
		Long userID = null;
		Path path = Paths.get(consumerExportCSVProperties.getUserRecommendFileName());
		BufferedWriter writer = Files.newBufferedWriter(path);
		while (it.hasNext()) {
			userID = it.next();
			if (userID == null) {
				continue;
			}
			for (RecommendedItem recommendedItem : recommender.recommend(userID, 50)) {
				if (recommendedItem == null || recommendedItem.getValue() == 0.00) {
					continue;
				}
				writer.write(userID + "," + recommendedItem.getItemID() + "," + df.format(recommendedItem.getValue()));
				writer.newLine();
			}
		}
		writer.close();
	}
}
