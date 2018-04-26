package com.katch.perfer.service;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.katch.perfer.config.RecommendRestProperties;

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

	/**
	 * @param yhid
	 * @param qy
	 * @return
	 */
	public abstract List<Long> queryAllRecommend(long yhid, String qy);

	public List<Long> queryRecommends(long yhid, String qy) {
		List<Long> allRecommends = queryAllRecommend(yhid, qy);
		List<Long> topItems = priorityService.queryTopSortItems(qy);
		List<Long> randomAllItems = priorityService.queryAllRandomSortItems(qy);
		Long recommendItemID;
		for (Iterator<Long> it = allRecommends.iterator(); it.hasNext();) {
			recommendItemID = it.next();
			if (!randomAllItems.contains(recommendItemID)) {
				it.remove();
			}
		}
		// 如果条数不足,使用TOP补充
		if (allRecommends.size() < recommendRestProperties.getReturnSize()) {
			for (Long itemId : topItems) {
				if (!allRecommends.contains(itemId)) {
					allRecommends.add(itemId);
					if (allRecommends.size() == recommendRestProperties.getReturnSize()) {
						break;
					}
				}
			}
		}
		// 如果条数不足,使用随机凑
		if (allRecommends.size() < recommendRestProperties.getReturnSize()) {
			for (Long itemId : randomAllItems) {
				if (!allRecommends.contains(itemId)) {
					allRecommends.add(itemId);
					if (allRecommends.size() == recommendRestProperties.getReturnSize()) {
						break;
					}
				}
			}
		}
		return allRecommends;
	}
}
