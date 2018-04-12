package com.katch.perfer.mahout.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.katch.perfer.config.ConsumerExportCSVProperties;
import com.katch.perfer.mahout.model.Recommender;
import com.katch.perfer.mybatis.mapper.RecommenderMapper;

public abstract class MahoutRecommenderService {

	@Autowired
	protected RecommenderMapper recommenderMapper;

	@Autowired
	protected ConsumerExportCSVProperties consumerExportCSVProperties;

	/**
	 * 执行
	 * 
	 * @throws Exception
	 */
	public abstract void excute() throws Exception;

	/**
	 * 查询
	 * 
	 * @param userId
	 * @return
	 */
	public List<Recommender> queryRecommenders(long userId) {
		return recommenderMapper.queryRecommenders(userId);
	}

	/**
	 * 保存
	 * 
	 * @param userID
	 * @param recommendedItems
	 */
	@Transactional()
	protected void saveRecommender(long userID, List<RecommendedItem> recommendedItems) {
		List<Recommender> recommenders = new ArrayList<Recommender>(recommendedItems.size());
		Recommender index = null;
		Date now = new Date();
		for (RecommendedItem recommendedItem : recommendedItems) {
			if (recommendedItem == null) {
				continue;
			}
			index = new Recommender();
			index.setUpdate_time(now);
			index.setItemID(recommendedItem.getItemID());
			index.setUserId(userID);
			recommenders.add(index);
		}
		recommenderMapper.deleteRecommenders(userID);
		if (recommenders != null && !recommenders.isEmpty()) {
			recommenderMapper.insertRecommenders(recommenders);
		}
	}

	public ConsumerExportCSVProperties getConsumerExportCSVProperties() {
		return consumerExportCSVProperties;
	}

	public void setConsumerExportCSVProperties(ConsumerExportCSVProperties consumerExportCSVProperties) {
		this.consumerExportCSVProperties = consumerExportCSVProperties;
	}

	public RecommenderMapper getRecommenderMapper() {
		return recommenderMapper;
	}

	public void setRecommenderMapper(RecommenderMapper recommenderMapper) {
		this.recommenderMapper = recommenderMapper;
	}
}
