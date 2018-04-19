package com.katch.perfer.mahout.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import au.com.bytecode.opencsv.CSVWriter;

@Service()
@ConditionalOnProperty(name = "consumer.mahout.type", havingValue = "user", matchIfMissing = true)
public class MahoutUserExportService extends MahoutExportService {
	private static Logger logger = LoggerFactory.getLogger(MahoutUserExportService.class);

	@Override
	public void export() throws Exception {
		UserSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
		UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(100, similarity, dataModel);
		Recommender recommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, similarity);
		saveUserRecommenderFile(dataModel, recommender);
	}

	/**
	 * 
	 * @param dataModel
	 * @throws Exception
	 */
	private void saveUserRecommenderFile(DataModel dataModel, Recommender recommender) throws Exception {
		logger.debug("基于用户的消费推荐文件导出准备!");
		LongPrimitiveIterator it = dataModel.getUserIDs();
		Long userID = null;
		Path path = Paths.get(recommendPropeties.getUserRecommendFileName());
		CSVWriter writer = new CSVWriter(Files.newBufferedWriter(path), ',', '\0');
		List<String[]> lines = new ArrayList<String[]>(2500);
		while (it.hasNext()) {
			userID = it.next();
			if (userID == null) {
				continue;
			}
			for (RecommendedItem recommendedItem : recommender.recommend(userID, 50)) {
				if (recommendedItem == null || recommendedItem.getValue() == 0.00) {
					continue;
				}
				lines.add(new String[] { String.valueOf(userID), String.valueOf(recommendedItem.getItemID()),
						df.format(recommendedItem.getValue()) });
			}
			if (lines.size() > 2500) {
				writer.writeAll(lines);
				lines.clear();
			}
		}
		if (lines.size() > 0) {
			writer.writeAll(lines);
			lines.clear();
		}
		writer.close();
		logger.debug("基于商品的消费推荐文件导出完成!");
	}
}
