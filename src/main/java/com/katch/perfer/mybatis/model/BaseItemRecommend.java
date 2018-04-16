package com.katch.perfer.mybatis.model;

/**
 * 基于商品推荐
 * @author Administrator
 *
 */
public class BaseItemRecommend {
	private long itemId;
	
	private long itemId2;
	
	private double score;

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public long getItemId2() {
		return itemId2;
	}

	public void setItemId2(long itemId2) {
		this.itemId2 = itemId2;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
}
