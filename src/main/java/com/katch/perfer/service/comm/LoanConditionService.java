package com.katch.perfer.service.comm;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.katch.perfer.mybatis.mapper.LoanConditionsMapper;
import com.katch.perfer.mybatis.model.LoanConditionDefine;
import com.katch.perfer.service.comm.loan.LoanCondition;

@Service
@CacheConfig(cacheNames = "LoanApplyConstraintCache")
public class LoanConditionService {
	@Autowired
	private LoanConditionsMapper loanConditionsMapper;

	@Autowired
	private List<LoanCondition> registerLoanConditions;

	@Cacheable(key = "#spid")
	public LoanApplyConstraint getLoanApplyConstraint(long spid) {
		List<LoanConditionDefine> loanConditionDefines = loanConditionsMapper.queryLoanCondition(spid);
		List<LoanCondition> loanConditions = getLoanConditions(loanConditionDefines);
		LoanApplyConstraint loanApplyConstraint = new LoanApplyConstraint(spid, loanConditions);
		return loanApplyConstraint;
	}

	/**
	 * 获取贷款条件
	 * 
	 * @param loanConditionDefines
	 * @return
	 */
	private List<LoanCondition> getLoanConditions(List<LoanConditionDefine> loanConditionDefines) {
		List<LoanCondition> loanConditionList = new ArrayList<LoanCondition>();
		if (loanConditionDefines == null || loanConditionDefines.isEmpty()) {
			return loanConditionList;
		}
		LoanCondition loanCondition = null;
		for (LoanConditionDefine define : loanConditionDefines) {
			for (LoanCondition lc : registerLoanConditions) {
				loanCondition = lc.create(define);
				if (loanCondition != null) {
					loanConditionList.add(loanCondition);
					break;
				}
			}
		}
		return loanConditionList;
	}
}
