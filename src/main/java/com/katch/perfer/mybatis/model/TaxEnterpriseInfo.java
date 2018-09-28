package com.katch.perfer.mybatis.model;

import java.util.Date;

/**
 * 企业涉税信息
 * 
 * @author Administrator
 *
 */
public class TaxEnterpriseInfo {
	/**
	 * 登记序号
	 */
	private String djxh;

	/**
	 * 企业最早涉税
	 */
	private Date qsyysj;

	/**
	 * 上年营业额
	 */
	private Double snyye;

	/**
	 * @return the djxh
	 */
	public String getDjxh() {
		return djxh;
	}

	/**
	 * @param djxh
	 *            the djxh to set
	 */
	public void setDjxh(String djxh) {
		this.djxh = djxh;
	}

	public Date getQsyysj() {
		return qsyysj;
	}

	public void setQsyysj(Date qsyysj) {
		this.qsyysj = qsyysj;
	}

	public Double getSnyye() {
		return snyye;
	}

	public void setSnyye(Double snyye) {
		this.snyye = snyye;
	}
}
