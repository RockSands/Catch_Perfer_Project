package com.katch.perfer.service.comm.loan.condition;

import java.util.Calendar;
import java.util.TimeZone;

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
public class BusinessYears extends LoanCondition {
    
    public BusinessYears() {
	super();
    }

    private int value;

    @Override
    public LoanCondition init(LoanConditionDefine loanCondition) {
	if ("1".equals(loanCondition.getType())) {
	    value = Integer.valueOf(loanCondition.getVal());
	}
	return null;
    }

    @Override
    public boolean constraint(TaxEnterpriseInfo info) {
	if (info != null && info.getQsyysj() != null) {
	    // 上海时间
	    java.util.Calendar now = java.util.Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
	    java.util.Calendar start = java.util.Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
	    start.setTime(info.getQsyysj());
	    start.add(Calendar.YEAR, value);
	    return start.before(now);
	}
	return true;
    }
}
