package com.katch.perfer.mahout.model;

/**
 * 用户商品推荐
 * 
 * @author Administrator
 *
 */
public class UserRecommender {
    private long userId;

    private long itemId;

    private double score;

    public long getUserId() {
	return userId;
    }

    public void setUserId(long userId) {
	this.userId = userId;
    }

    public long getItemId() {
	return itemId;
    }

    public void setItemId(long itemId) {
	this.itemId = itemId;
    }

    public double getScore() {
	return score;
    }

    public void setScore(double score) {
	this.score = score;
    }
}
