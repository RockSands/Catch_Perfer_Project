package com.katch.perfer.mahout.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

import au.com.bytecode.opencsv.CSVWriter;

@Service()
@ConditionalOnProperty(name = "consumer.mahout.type", havingValue = "item", matchIfMissing = true)
public class MahoutItemExportService extends MahoutExportService {

	private static Logger logger = LoggerFactory.getLogger(MahoutItemExportService.class);

	@Override
	public void export() throws Exception {
		ItemSimilarity similarity = new LogLikelihoodSimilarity(dataModel);
		ItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel, similarity);
		saveItemRecommenderFile(dataModel, recommender);
	}

	private void saveItemRecommenderFile(DataModel dataModel, ItemBasedRecommender recommender) throws Exception {
		logger.debug("基于商品的消费推荐文件导出准备!");
		LongPrimitiveIterator it = dataModel.getItemIDs();
		long itemID = 0;
		Path path = Paths.get(recommendPropeties.getItemRecommendFileName());
		CSVWriter writer = new CSVWriter(Files.newBufferedWriter(path), ',', '\0');
		double maxVal = 4.5;
		double declineVal = 0.1;
		int index = 0;
		List<String[]> lines = new ArrayList<String[]>(2500);
		while (it.hasNext()) {
			itemID = it.next();
			maxVal = 4.5;
			index = 0;
			for (RecommendedItem recommendedItem : recommender.mostSimilarItems(itemID, 30)) {
				if (recommendedItem == null || recommendedItem.getValue() == 0.00) {
					continue;
				}
				lines.add(new String[] { String.valueOf(itemID), String.valueOf(recommendedItem.getItemID()),
						df.format(maxVal - declineVal * index) });
				index++;
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
