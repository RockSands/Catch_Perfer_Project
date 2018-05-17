package com.katch.perfer.service.comm.loan.condition;

import com.katch.perfer.mybatis.model.LoanConditionDefine;
import com.katch.perfer.mybatis.model.TaxEnterpriseInfo;
import com.katch.perfer.service.comm.loan.LoanCondition;

public class BusinessDuration extends LoanCondition {

	private double value;

	@Override
	public void init(LoanConditionDefine loanCondition) {
		value = Double.valueOf(loanCondition.getVal());
	}

	@Override
	public boolean constraint(TaxEnterpriseInfo info) {
		return info.getSnyye() > value;
	}

}
