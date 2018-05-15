package com.katch.perfer.mybatis.model;

import java.util.Date;

/**
 * 企业涉税信息
 * @author Administrator
 *
 */
public class TaxEnterpriseInfo {
	/**
	 * 纳税人识别号
	 */
	private String nsrsbh;

	/**
	 * 企业最早涉税
	 */
	private Date qsyysj;

	/**
	 * 上年营业额
	 */
	private Double snyye;

	public String getNsrsbh() {
		return nsrsbh;
	}

	public void setNsrsbh(String nsrsbh) {
		this.nsrsbh = nsrsbh;
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
