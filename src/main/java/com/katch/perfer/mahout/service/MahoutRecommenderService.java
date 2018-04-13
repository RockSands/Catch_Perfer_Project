package com.katch.perfer.mahout.service;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.katch.perfer.config.ConsumerExportCSVProperties;
import com.katch.perfer.mahout.model.UserRecommender;

public abstract class MahoutRecommenderService {
	private static Logger logger = LoggerFactory.getLogger(UserMahoutRecommenderService.class);

	private DecimalFormat df = new DecimalFormat("##0.000");

	@Autowired
	private UserRecommenderService userRecommenderService;

	@Autowired
	protected ConsumerExportCSVProperties consumerExportCSVProperties;

	/**
	 * 执行
	 * 
	 * @throws Exception
	 */
	public abstract void excute() throws Exception;

	/**
	 * 
	 * @param dataModel
	 * @throws TasteException
	 */
	protected void saveUserRecommender(DataModel dataModel, Recommender recommender) throws TasteException {
		List<UserRecommender> saveRecommenders = new LinkedList<UserRecommender>();
		Date now = new Date();
		LongPrimitiveIterator it = dataModel.getUserIDs();
		Long userID = null;
		UserRecommender index = null;
		StringBuffer content = new StringBuffer();
		while (it.hasNext()) {
			if (saveRecommenders.size() == 150) {
				saveBatch(saveRecommenders);
				saveRecommenders.clear();
			}
			userID = it.next();
			if (userID == null) {
				continue;
			}
			for (RecommendedItem recommendedItem : recommender.recommend(userID, 100)) {
				if (recommendedItem == null || recommendedItem.getValue() == 0.00) {
					continue;
				}
				if (content.length() != 0) {
					content.append(",");
				}
				content.append(recommendedItem.getItemID()).append(":").append(df.format(recommendedItem.getValue()));
			}
			index = new UserRecommender();
			index.setUserId(userID);
			index.setItemRecommedns(content.toString());
			index.setUpdateTime(now);
			saveRecommenders.add(index);
		}
		if (!saveRecommenders.isEmpty()) {
			userRecommenderService.saveBatch(saveRecommenders);
		}
	}

	/**
	 * @param saveRecommenders
	 */
	public void saveBatch(List<UserRecommender> saveRecommenders) {
		logger.debug("数据库刷新执行批量操作!");
		userRecommenderService.saveBatch(saveRecommenders);
	}

	public ConsumerExportCSVProperties getConsumerExportCSVProperties() {
		return consumerExportCSVProperties;
	}

	public void setConsumerExportCSVProperties(ConsumerExportCSVProperties consumerExportCSVProperties) {
		this.consumerExportCSVProperties = consumerExportCSVProperties;
	}

	public UserRecommenderService getUserRecommenderService() {
		return userRecommenderService;
	}

	public void setUserRecommenderService(UserRecommenderService userRecommenderService) {
		this.userRecommenderService = userRecommenderService;
	}
}
