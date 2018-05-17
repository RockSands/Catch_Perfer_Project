package com.katch.perfer.service.comm.loan;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.katch.perfer.mybatis.model.LoanConditionDefine;
import com.katch.perfer.mybatis.model.TaxEnterpriseInfo;

public abstract class LoanCondition {

    @Autowired
    private static List<LoanCondition> loanConditions;

    public static LoanCondition getLoanCondition(LoanConditionDefine loanConditionDefine) {
	LoanCondition loanCondition = null;
	for (LoanCondition condeition : loanConditions) {
	    loanCondition = condeition.init(loanConditionDefine);
	    if (loanCondition != null) {
		return loanCondition;
	    }
	}
	return loanCondition;
    }

    public abstract LoanCondition init(LoanConditionDefine loanCondition);

    /**
     * 约束
     * 
     * @param info
     * @return
     */
    public abstract boolean constraint(TaxEnterpriseInfo info);
}
