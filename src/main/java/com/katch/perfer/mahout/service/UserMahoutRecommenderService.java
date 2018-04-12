package com.katch.perfer.mahout.service;

import java.io.File;
import java.util.List;

import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.katch.perfer.config.ConsumerExportCSVProperties;

@Component("mahoutRecommenderService")
@ConditionalOnProperty(name = "consumer.mahout.type", havingValue = "user", matchIfMissing = true)
public class UserMahoutRecommenderService extends MahoutRecommenderService {
	@Autowired
	private ConsumerExportCSVProperties consumerExportCSVProperties;

	@Override
	public void excute() throws Exception {
		File file = new File(consumerExportCSVProperties.getFileName());
		DataModel dataModel = new FileDataModel(file);
		UserSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
		UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(100, similarity, dataModel);
		Recommender recommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, similarity);
		LongPrimitiveIterator it = dataModel.getUserIDs();
		Long userID = null;
		List<RecommendedItem> recommendedItems = null;
		while (it.hasNext()) {
			userID = it.next();
			if (userID == null) {
				continue;
			}
			recommendedItems = recommender.recommend(userID, 100);
			saveRecommender(userID, recommendedItems);
		}
	}
}
