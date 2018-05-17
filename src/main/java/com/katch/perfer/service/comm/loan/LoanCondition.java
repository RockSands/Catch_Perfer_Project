package com.katch.perfer.service.comm.loan;

import com.katch.perfer.mybatis.model.LoanConditionDefine;
import com.katch.perfer.mybatis.model.TaxEnterpriseInfo;

public abstract class LoanCondition {
	
	public abstract void init(LoanConditionDefine loanCondition);
	
	/**
	 * 约束
	 * @param info
	 * @return
	 */
	public abstract boolean constraint(TaxEnterpriseInfo info);
}
