package com.katch.perfer.service.comm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.katch.perfer.mybatis.mapper.LoanConditionsMapper;
import com.katch.perfer.mybatis.model.TaxEnterpriseInfo;

@Service
public class LoanConditionService {
	@Autowired
	private LoanConditionsMapper loanConditionsMapper;

	public boolean check(long spid,TaxEnterpriseInfo info) {
		loanConditionsMapper.queryLoanCondition(spid);
	}
}
