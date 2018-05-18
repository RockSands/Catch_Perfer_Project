package com.katch.perfer.service.comm.loan.condition;

import org.springframework.stereotype.Component;

import com.katch.perfer.mybatis.model.LoanConditionDefine;
import com.katch.perfer.mybatis.model.TaxEnterpriseInfo;
import com.katch.perfer.service.comm.loan.LoanCondition;

@Component
public class BusinessDuration extends LoanCondition {

	private double value;

	private BusinessDuration() {
		super();
	}

	@Override
	public LoanCondition create(LoanConditionDefine loanCondition) {
		if ("2".equals(loanCondition.getType())) {
			BusinessDuration businessDuration = new BusinessDuration();
			businessDuration.value = Double.valueOf(loanCondition.getVal());
			return businessDuration;
		}
		return null;
	}

	@Override
	public boolean constraint(TaxEnterpriseInfo info) {
		return info.getSnyye() > value;
	}

}
