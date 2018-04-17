package com.katch.perfer.mahout.service;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service()
@ConditionalOnProperty(name = "consumer.mahout.type", havingValue = "item", matchIfMissing = true)
public class MahoutItemExportService extends MahoutExportService {

	private static Logger logger = LoggerFactory.getLogger(MahoutItemExportService.class);

	@Override
	public void excute() throws Exception {
		ItemSimilarity similarity = new LogLikelihoodSimilarity(dataModel);
		ItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel, similarity);
		saveItemRecommenderFile(dataModel, recommender);
	}

	private void saveItemRecommenderFile(DataModel dataModel, ItemBasedRecommender recommender) throws Exception {
		logger.debug("基于商品的消费推荐文件导出准备!");
		LongPrimitiveIterator it = dataModel.getItemIDs();
		long itemID = 0;
		Path path = Paths.get(consumerExportCSVProperties.getItemRecommendFileName());
		BufferedWriter writer = Files.newBufferedWriter(path);
		double maxVal = 4.5;
		double declineVal = 0.1;
		int index = 0;
		while (it.hasNext()) {
			itemID = it.next();
			maxVal = 4.5;
			index = 0;
			for (RecommendedItem recommendedItem : recommender.mostSimilarItems(itemID, 25)) {
				if (recommendedItem == null || recommendedItem.getValue() == 0.00) {
					continue;
				}
				writer.write(itemID + "," + recommendedItem.getItemID() + "," + df.format(maxVal - declineVal * index));
				writer.newLine();
				index++;
			}
		}
		writer.close();
		logger.debug("基于商品的消费推荐文件导出完成!");
	}

}
