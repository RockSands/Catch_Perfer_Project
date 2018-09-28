package com.katch.perfer.control;

import com.katch.perfer.mybatis.model.TaxEnterpriseInfo;

public class RecommedRequest {
	/*
	 * 用户ID
	 */
	private Long yhid;
	/*
	 * 企业
	 */
	private TaxEnterpriseInfo taxEnterpriseInfo;
	
	/*
	 * 区域
	 */
	private String qy;
	
	/*
	 * 贷款类型,1位混合,2为个人
	 */
	private String dklx;

	public Long getYhid() {
		return yhid;
	}

	public void setYhid(Long yhid) {
		this.yhid = yhid;
	}

	public TaxEnterpriseInfo getTaxEnterpriseInfo() {
		return taxEnterpriseInfo;
	}

	public void setTaxEnterpriseInfo(TaxEnterpriseInfo taxEnterpriseInfo) {
		this.taxEnterpriseInfo = taxEnterpriseInfo;
	}

	public String getQy() {
		return qy;
	}

	public void setQy(String qy) {
		this.qy = qy;
	}

	/**
	 * @return the dklx
	 */
	public String getDklx() {
		return dklx;
	}

	/**
	 * @param dklx the dklx to set
	 */
	public void setDklx(String dklx) {
		this.dklx = dklx;
	}
}
