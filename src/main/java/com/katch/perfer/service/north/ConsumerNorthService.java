package com.katch.perfer.service.north;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.katch.perfer.config.RecommendRestProperties;
import com.katch.perfer.control.RecommedRequest;
import com.katch.perfer.mybatis.model.TaxEnterpriseInfo;
import com.katch.perfer.service.comm.LoanConditionService;
import com.katch.perfer.service.comm.RecommendPriorityService;

/**
 * 消费记录导出
 * 
 * @author Administrator
 *
 */
public abstract class ConsumerNorthService {
	@Autowired
	protected RecommendRestProperties recommendRestProperties;

	@Autowired
	protected RecommendPriorityService priorityService;

	@Autowired
	protected LoanConditionService loanConditionService;

	/**
	 * @param yhid
	 * @param qy
	 * @return
	 */
	public abstract List<Long> queryAllRecommend(RecommedRequest request);

	/**
	 * 获取推荐信息
	 * 
	 * @param yhid
	 * @param qy
	 * @return
	 */
	public List<Long> queryRecommends(RecommedRequest request) {
		List<Long> recommends = queryAllRecommend(request);
		List<Long> topItems = priorityService.queryTopSortItems(request);
		List<Long> hotItems = priorityService.queryHotSortItems(request);
		List<Long> allItems = priorityService.queryAllItems(request);
		for (Long itemId : topItems) {
			recommends.add(0, itemId);
		}
		// 如果条数不足,使用TOP补充
		if (recommends.size() < recommendRestProperties.getReturnSize()) {
			for (Long itemId : hotItems) {
				if (!recommends.contains(itemId) && loanApplyConstraint(itemId, request.getTaxEnterpriseInfo())) {
					recommends.add(itemId);
					if (recommends.size() == recommendRestProperties.getReturnSize()) {
						return recommends;
					}
				}
			}
		}
		// 如果条数不足,使用随机凑
		for (Long itemId : allItems) {
			if (!recommends.contains(itemId) && loanApplyConstraint(itemId, request.getTaxEnterpriseInfo())) {
				recommends.add(itemId);
				if (recommends.size() == recommendRestProperties.getReturnSize()) {
					return recommends;
				}
			}
		}
		return recommends.subList(0, recommendRestProperties.getReturnSize());
	}

	protected boolean loanApplyConstraint(Long spid, TaxEnterpriseInfo info) {
		if (spid == null) {
			return false;
		}
		if (info == null) {
			return true;
		}
		return loanConditionService.getLoanApplyConstraint(spid).constraint(info);
	}
}
