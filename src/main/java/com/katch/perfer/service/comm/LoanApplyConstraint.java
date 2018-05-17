package com.katch.perfer.service.comm;

import java.util.ArrayList;
import java.util.List;

import com.katch.perfer.mybatis.model.LoanConditionDefine;
import com.katch.perfer.service.comm.loan.LoanCondition;

/**
 * @author Administrator
 *
 */
public class LoanApplyConstraint {

	/**
	 * 商品ID
	 */
	private final long spid;

	/**
	 * 申请定义
	 */
	private List<LoanCondition> loanConditions;

	public LoanApplyConstraint(long spid) {
		this.spid = spid;
	}

	public long getSpid() {
		return spid;
	}

	/**
	 * @param loanConditionDefines
	 */
	public synchronized void loadLoanConditionDefines(List<LoanConditionDefine> loanConditionDefines) {
		if (loanConditions == null) {
			loanConditions = new ArrayList<LoanCondition>();
		}
		loanConditions.clear();
		for (LoanConditionDefine define : loanConditionDefines) {
			loanConditions.add(new LoanCondition(define));
		}
	}

}
