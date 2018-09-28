package com.katch.perfer.mahout.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.katch.perfer.mybatis.mapper.UserConsumptionMapper;

@Service
// @ConditionalOnProperty(name = "consumer.mahout.type", havingValue = "item",
// matchIfMissing = true)
public class MahoutItemExportService extends MahoutExportService {

	@Autowired
	private UserConsumptionMapper userConsumptionMapper;

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
		long itemID = 0L;
		Path mixpath = Paths.get(recommendPropeties.getItemMixRecommendFileName());
		/*
		 * 写入所有推荐
		 */
		Files.deleteIfExists(mixpath);
		Files.write(mixpath, "".getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		double maxVal = 4.5;
		double declineVal = 0.1;
		int index = 0;
		int lines = 0;
		StringBuffer buffer = new StringBuffer();
		while (it.hasNext()) {
			itemID = it.next();
			maxVal = 4.5;
			index = 0;
			for (RecommendedItem recommendedItem : recommender.mostSimilarItems(itemID, 30)) {
				if (recommendedItem == null || recommendedItem.getValue() == 0.00) {
					continue;
				}
				buffer.append(itemID + "," + recommendedItem.getItemID() + "," + df.format(maxVal - declineVal * index)
						+ "\r\n");
				index++;
				lines++;
			}
			if (lines > 10000) {
				logger.debug("基于商品的消费推荐文件书写数据行数:" + lines);
				Files.write(mixpath, buffer.toString().getBytes("UTF-8"), StandardOpenOption.APPEND);
				buffer.delete(0, buffer.length());
				lines = 0;
			}
		}
		if (buffer.length() > 0) {
			Files.write(mixpath, buffer.toString().getBytes("UTF-8"), StandardOpenOption.APPEND);
		}
		/*
		 * 写入个人用户推荐
		 */
		Path gryhPath = Paths.get(recommendPropeties.getItemGrRecommendFileName());
		Files.deleteIfExists(gryhPath);
		Files.write(mixpath, "".getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		maxVal = 4.5;
		declineVal = 0.1;
		index = 0;
		lines = 0;
		buffer = new StringBuffer();
		List<Long> excludeItemIDs = userConsumptionMapper.querySpidsByDklxEqual1();
		while (it.hasNext()) {
			itemID = it.next();
			maxVal = 4.5;
			index = 0;
			for (RecommendedItem recommendedItem : recommender.mostSimilarItems(itemID, 30,
					new FilterItemRescorer(excludeItemIDs))) {
				if (recommendedItem == null || recommendedItem.getValue() == 0.00) {
					continue;
				}
				buffer.append(itemID + "," + recommendedItem.getItemID() + "," + df.format(maxVal - declineVal * index)
						+ "\r\n");
				index++;
				lines++;
			}
			if (lines > 10000) {
				logger.debug("基于商品的消费推荐文件书写数据行数:" + lines);
				Files.write(mixpath, buffer.toString().getBytes("UTF-8"), StandardOpenOption.APPEND);
				buffer.delete(0, buffer.length());
				lines = 0;
			}
		}
		if (buffer.length() > 0) {
			Files.write(mixpath, buffer.toString().getBytes("UTF-8"), StandardOpenOption.APPEND);
		}
		logger.debug("基于商品的消费推荐文件导出完成!");
	}

}
