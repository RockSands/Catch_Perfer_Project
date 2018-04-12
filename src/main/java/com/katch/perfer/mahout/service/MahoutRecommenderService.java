package com.katch.perfer.mahout.service;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.katch.perfer.config.ConsumerExportCSVProperties;
import com.katch.perfer.mahout.model.Recommender;
import com.katch.perfer.mybatis.mapper.RecommenderMapper;

public abstract class MahoutRecommenderService {
    private DecimalFormat df = new DecimalFormat("##0.000");

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
    public Recommender queryRecommenders(long userId) {
	return recommenderMapper.queryRecommender(userId);
    }

    /**
     * 保存
     * 
     * @param userID
     * @param recommendedItems
     */
    @Transactional()
    protected void saveRecommender(long userID, List<RecommendedItem> recommendedItems) {
	Recommender recommender = new Recommender();
	Date now = new Date();
	recommender.setUpdateTime(now);
	recommender.setUserId(userID);
	StringBuffer content = new StringBuffer();
	for (RecommendedItem recommendedItem : recommendedItems) {
	    if (recommendedItem == null || recommendedItem.getValue() == 0.00) {
		continue;
	    }
	    if (content.length() != 0) {
		content.append(",");
	    }
	    content.append(recommendedItem.getItemID()).append(":").append(df.format(recommendedItem.getValue()));
	}
	recommender.setItemRecommedns(content.toString());
	recommenderMapper.deleteRecommender(userID);
	if (content.length() > 0) {
	    recommenderMapper.insertRecommender(recommender);
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
