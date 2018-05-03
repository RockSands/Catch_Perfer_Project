package com.katch.perfer.control;

public class RecommedRequest {
	/*
	 * 用户ID
	 */
	private Long yhid;
	/*
	 * 企业ID
	 */
	private String qyid;
	
	/*
	 * 区域
	 */
	private String qy;

	public Long getYhid() {
		return yhid;
	}

	public void setYhid(Long yhid) {
		this.yhid = yhid;
	}

	public String getQyid() {
		return qyid;
	}

	public void setQyid(String qyid) {
		this.qyid = qyid;
	}

	public String getQy() {
		return qy;
	}

	public void setQy(String qy) {
		this.qy = qy;
	}
}
