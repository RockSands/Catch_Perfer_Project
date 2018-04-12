package com.katch.perfer.mahout.service;

import java.io.File;
import java.util.List;

import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component("mahoutRecommenderService")
@ConditionalOnProperty(name = "consumer.mahout.type", havingValue = "item", matchIfMissing = false)
public class ItemMahoutRecommenderService extends MahoutRecommenderService {

	@Override
	public void excute() throws Exception {
		File file = new File(consumerExportCSVProperties.getFileName());
		DataModel dataModel = new FileDataModel(file);
		ItemSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
		ItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel, similarity);
		LongPrimitiveIterator it = dataModel.getUserIDs();
		Long userID = null;
		List<RecommendedItem> recommendedItems = null;
		while (it.hasNext()) {
			userID = it.next();
			if (userID == null) {
				continue;
			}
			recommendedItems = recommender.recommend(userID, 100);
			for (RecommendedItem item : recommendedItems) {
				System.out.println("=" + userID + "=>" + item.getItemID() + "[" + item.getValue() + "]");
			}
		}
	}

}
