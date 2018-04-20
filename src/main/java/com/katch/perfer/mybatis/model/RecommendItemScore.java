package com.katch.perfer.mybatis.model;

import java.util.Date;

/**
 * 推荐物品评分-可用于加权 新物品加权
 * 
 * @author Administrator
 *
 */
public class RecommendItemScore {
	/**
	 * 物品ID
	 */
	private long itemId;

	/**
	 * 评分
	 */
	private double score;

	/**
	 * 策略
	 */
	private int policy;

	/**
	 * 创建时间
	 */
	private Date createTime;

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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getPolicy() {
		return policy;
	}

	public void setPolicy(int policy) {
		this.policy = policy;
	}
}
