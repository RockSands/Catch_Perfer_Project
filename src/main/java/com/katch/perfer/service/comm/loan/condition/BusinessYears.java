package com.katch.perfer.service.comm.loan.condition;

import java.util.Calendar;
import java.util.TimeZone;

import org.springframework.stereotype.Component;

import com.katch.perfer.mybatis.model.LoanConditionDefine;
import com.katch.perfer.mybatis.model.TaxEnterpriseInfo;
import com.katch.perfer.service.comm.loan.LoanCondition;

@Component
public class BusinessYears extends LoanCondition {

	private int value;
	
	private BusinessYears() {
		super();
	}
	
	@Override
	public LoanCondition create(LoanConditionDefine loanCondition) {
		if ("1".equals(loanCondition.getType())) {
			BusinessYears businessYears = new BusinessYears();
			businessYears.value = Integer.valueOf(loanCondition.getVal());
			return businessYears;
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
