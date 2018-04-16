package com.katch.perfer.mybatis.model;

import java.util.Date;

/**
 * 用户消费记录
 * @author Administrator
 *
 */
public class UserConsumption {
	private long userId;
	
	private long itmeId;
	
	private Date createTime;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getItmeId() {
		return itmeId;
	}

	public void setItmeId(long itmeId) {
		this.itmeId = itmeId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
