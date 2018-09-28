package com.katch.perfer.service.comm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.katch.perfer.mybatis.mapper.TaxEnterPriseMapper;
import com.katch.perfer.mybatis.model.TaxEnterpriseInfo;

@Service
public class TaxEnterpriseService {
	@Autowired
	private TaxEnterPriseMapper taxEnterPriseMapper;

	/**
	 * @param djxh
	 * @return
	 */
	public TaxEnterpriseInfo queryTaxEnterpriseInfo(String djxh) {
		TaxEnterpriseInfo taxEnterprise = taxEnterPriseMapper.queryOne(djxh);
		if (taxEnterprise == null) {
			taxEnterprise = taxEnterPriseMapper.queryThirdOne(djxh);
		}
		if (taxEnterprise != null) {
			insertNE(taxEnterprise);
		}
		return taxEnterprise;
	}

	/**
	 * 插入
	 * 
	 * @param taxEnterprise
	 */
	private void insertNE(TaxEnterpriseInfo taxEnterprise) {
		try {
			taxEnterPriseMapper.insert(taxEnterprise);
		} catch (Exception ex) {
		}
	}
}