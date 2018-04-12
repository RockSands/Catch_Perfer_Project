package com.katch.perfer.mahout.model;

import java.util.Date;

/**
 * 用户商品推荐
 * 
 * @author Administrator
 *
 */
public class Recommender {
	private long userId;

	private long itemID;

	private double score;

	private Date updateTime;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getItemID() {
		return itemID;
	}

	public void setItemID(long itemID) {
		this.itemID = itemID;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
