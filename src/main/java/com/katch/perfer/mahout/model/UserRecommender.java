package com.katch.perfer.mahout.model;

import java.util.Date;

/**
 * 用户商品推荐
 * 
 * @author Administrator
 *
 */
public class UserRecommender {
	private long userId;

	private String itemRecommedns;

	private Date updateTime;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getItemRecommedns() {
	    return itemRecommedns;
	}

	public void setItemRecommedns(String itemRecommedns) {
	    this.itemRecommedns = itemRecommedns;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
