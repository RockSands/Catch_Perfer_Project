package com.katch.perfer.service.comm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.katch.perfer.mybatis.mapper.LoanConditionsMapper;
import com.katch.perfer.mybatis.model.LoanConditionDefine;

@Service
public class LoanConditionService {
	@Autowired
	private LoanConditionsMapper loanConditionsMapper;

	public LoanApplyConstraint getLoanApplyConstraint(long spid) {
		List<LoanConditionDefine> loanConditionDefines = loanConditionsMapper.queryLoanCondition(spid);
		LoanApplyConstraint loanApplyConstraint = new LoanApplyConstraint(spid);
		loanApplyConstraint.loadLoanConditionDefines(loanConditionDefines);
		return loanApplyConstraint;
	}

}
