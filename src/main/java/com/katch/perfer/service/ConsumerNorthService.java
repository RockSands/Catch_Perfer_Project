package com.katch.perfer.service;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.katch.perfer.config.RecommendRestProperties;
import com.katch.perfer.control.RecommedRequest;

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
	public abstract List<Long> queryAllRecommend(RecommedRequest request);

	/**
	 * 获取推荐信息
	 * 
	 * @param yhid
	 * @param qy
	 * @return
	 */
	public List<Long> queryRecommends(RecommedRequest request) {
		List<Long> allRecommends = queryAllRecommend(request);
		List<Long> topItems = priorityService.queryTopSortItems(request.getQy());
		List<Long> randomAllItems = priorityService.queryAllRandomSortItems(request.getQy());
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
