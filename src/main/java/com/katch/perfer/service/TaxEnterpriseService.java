package com.katch.perfer.service;

import org.springframework.stereotype.Service;

import com.katch.perfer.mybatis.mapper.TaxEnterPriseMapper;
import com.katch.perfer.mybatis.model.TaxEnterpriseInfo;

@Service
public class TaxEnterpriseService {
	private TaxEnterPriseMapper taxEnterPriseMapper;

	/**
	 * @param nsrsbh
	 * @return
	 */
	public TaxEnterpriseInfo queryTaxEnterpriseInfo(String nsrsbh) {
		TaxEnterpriseInfo taxEnterprise = taxEnterPriseMapper.queryOne(nsrsbh);
		if (taxEnterprise == null) {
			taxEnterprise = taxEnterPriseMapper.queryThirdOne(nsrsbh);
		}
		if (taxEnterprise != null) {
			insertNE(taxEnterprise);
		}
		return taxEnterprise;
	}

	/**
	 * 插入
	 * @param taxEnterprise
	 */
	private void insertNE(TaxEnterpriseInfo taxEnterprise) {
		try {
			taxEnterPriseMapper.insert(taxEnterprise);
		} catch (Exception ex) {
		}
	}
}