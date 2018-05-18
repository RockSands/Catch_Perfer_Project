package com.katch.perfer.service.comm;

import java.util.List;

import com.katch.perfer.mybatis.model.TaxEnterpriseInfo;
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
	private final List<LoanCondition> loanConditions;

	public LoanApplyConstraint(long spid, List<LoanCondition> loanConditions) {
		this.spid = spid;
		this.loanConditions = loanConditions;
	}

	public long getSpid() {
		return spid;
	}

	public boolean constraint(TaxEnterpriseInfo info) {
		if (loanConditions == null) {
			return true;
		}
		for (LoanCondition loanCondition : loanConditions) {
			if (!loanCondition.constraint(info)) {
				return false;
			}
		}
		return true;
	}

}
