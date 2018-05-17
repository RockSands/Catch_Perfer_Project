package com.katch.perfer.service.comm.loan.condition;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.katch.perfer.mybatis.model.LoanConditionDefine;
import com.katch.perfer.mybatis.model.TaxEnterpriseInfo;
import com.katch.perfer.service.comm.loan.LoanCondition;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class BusinessDuration extends LoanCondition {

    private double value;

    @Override
    public LoanCondition init(LoanConditionDefine loanCondition) {
	if ("2".equals(loanCondition.getType())) {
	    value = Double.valueOf(loanCondition.getVal());
	    return this;
	}
	return null;
    }

    @Override
    public boolean constraint(TaxEnterpriseInfo info) {
	return info.getSnyye() > value;
    }

}
