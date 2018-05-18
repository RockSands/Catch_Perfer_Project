package com.katch.perfer.service.comm.loan;

import org.springframework.stereotype.Component;

import com.katch.perfer.mybatis.model.LoanConditionDefine;
import com.katch.perfer.mybatis.model.TaxEnterpriseInfo;

@Component
public abstract class LoanCondition {

	public abstract LoanCondition create(LoanConditionDefine loanCondition);

	/**
	 * 约束
	 * 
	 * @param info
	 * @return
	 */
	public abstract boolean constraint(TaxEnterpriseInfo info);
}
