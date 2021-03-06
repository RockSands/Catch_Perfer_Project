package com.katch.perfer.service.north;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.katch.perfer.control.RecommedRequest;
import com.katch.perfer.mybatis.mapper.BaseItemRecommendMapper;
import com.katch.perfer.mybatis.mapper.UserConsumptionMapper;
import com.katch.perfer.mybatis.model.RecommendItemScore;
import com.katch.perfer.mybatis.model.UserConsumption;

@Service()
//@ConditionalOnProperty(name = "consumer.mahout.type", havingValue = "item", matchIfMissing = false)
public class ConsumerItemNorthService extends ConsumerNorthService {

	@Autowired
	private BaseItemRecommendMapper baseItemRecommendMapper;

	@Autowired
	private UserConsumptionMapper userConsumptionMapper;
	
	@Override
	public List<Long> queryAllRecommend(RecommedRequest request) {
		List<Long> returnList = new ArrayList<Long>();
		Map<Long, Double> scoreMap = new HashMap<Long, Double>();
		// 处理权重
		dealWeightItems(request, returnList, scoreMap);
		// 处理新物品
		dealNewItems(request, returnList, scoreMap);
		// 处理推荐物品
		dealRecommendItems(request, returnList, scoreMap);
		// 开始排序
		List<Map.Entry<Long, Double>> mapList = new ArrayList<Map.Entry<Long, Double>>(scoreMap.entrySet());
		Collections.sort(mapList, new Comparator<Map.Entry<Long, Double>>() {
			@Override
			public int compare(Entry<Long, Double> o1, Entry<Long, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		for (Map.Entry<Long, Double> entry : mapList) {
			returnList.add(entry.getKey());
		}
		return returnList;
	}

	/**
	 * 处理权重商品
	 * 
	 * @param request
	 * @param returnList
	 * @param scoreMap
	 */
	private void dealWeightItems(RecommedRequest request, List<Long> returnList, Map<Long, Double> scoreMap) {
		List<RecommendItemScore> weightScores = priorityService.queryItemWeight(request.getQy());
		RecommendItemScore itemScore = null;
		// 过滤
		int count = recommendRestProperties.getWeightSize();
		for (Iterator<RecommendItemScore> it = weightScores.iterator(); it.hasNext();) {
			itemScore = it.next();
			if (returnList.contains(itemScore.getItemId())) {
				continue;
			}
			if (!loanApplyConstraint(itemScore.getItemId(), request.getTaxEnterpriseInfo())) {
				continue;
			}
			if (count > 0) {
				returnList.add(itemScore.getItemId());
				scoreMap.remove(itemScore.getItemId());
				count--;
			} else if (!scoreMap.containsKey(itemScore.getItemId())) {
				scoreMap.put(itemScore.getItemId(), itemScore.getScore());
			} else {
				scoreMap.put(itemScore.getItemId(), itemScore.getScore() + scoreMap.get(itemScore.getItemId()));
			}
		}
	}

	/**
	 * 处理新商品
	 * 
	 * @param request
	 * @param returnList
	 * @param scoreMap
	 */
	private void dealNewItems(RecommedRequest request, List<Long> returnList, Map<Long, Double> scoreMap) {
		RecommendItemScore itemScore = null;
		if (!recommendRestProperties.isNewItemOpen()) {
			return;
		}
		int count = recommendRestProperties.getNewItemSize();
		List<RecommendItemScore> allNewItems = priorityService.queryNewItems(request.getQy(),
				recommendRestProperties.getNewItemTimeOut());
		for (Iterator<RecommendItemScore> it = allNewItems.iterator(); it.hasNext();) {
			itemScore = it.next();
			if (returnList.contains(itemScore.getItemId())) {
				continue;
			}
			if (!loanApplyConstraint(itemScore.getItemId(), request.getTaxEnterpriseInfo())) {
				continue;
			}
			if (count > 0) {
				returnList.add(itemScore.getItemId());
				scoreMap.remove(itemScore.getItemId());
				count--;
			} else if (!scoreMap.containsKey(itemScore.getItemId())) {
				scoreMap.put(itemScore.getItemId(), itemScore.getScore());
			} else {
				scoreMap.put(itemScore.getItemId(), itemScore.getScore() + scoreMap.get(itemScore.getItemId()));
			}
		}
	}

	/**
	 * 处理正常商品
	 * 
	 * @param request
	 * @param returnList
	 * @param scoreMap
	 */
	private void dealRecommendItems(RecommedRequest request, List<Long> returnList, Map<Long, Double> scoreMap) {
		if (request.getYhid() == null) {
			return;
		}
		// 消费记录
		List<UserConsumption> consumptions = userConsumptionMapper.queryUserConsumptions(request.getYhid(),
				request.getQy());
		double devalue = 0.0D;
		double scortTmp;
		Map<Long, Double> recommendScoreMap = new HashMap<Long, Double>();
		List<RecommendItemScore> baseItemScores;
		// 计算推荐评分
		for (UserConsumption consumption : consumptions.subList(0, Math.min(consumptions.size(), 10))) {
			devalue = devalue - 0.2D;
			baseItemScores = baseItemRecommendMapper.queryRecommenders(consumption.getItmeId());
			for (RecommendItemScore itemScore : baseItemScores) {
				if (returnList.contains(itemScore.getItemId())) {
					continue;
				}
				if (!loanApplyConstraint(itemScore.getItemId(), request.getTaxEnterpriseInfo())) {
					continue;
				}
				if (!recommendScoreMap.containsKey(itemScore.getItemId())) {
					recommendScoreMap.put(itemScore.getItemId(), itemScore.getScore() + devalue);
				} else {
					scortTmp = recommendScoreMap.get(itemScore.getItemId());
					if (scortTmp >= 4.9D) {
						recommendScoreMap.put(itemScore.getItemId(), 5.0D);
					} else if (scortTmp > 4) {
						recommendScoreMap.put(itemScore.getItemId(), scortTmp + 0.15D);
					} else if (scortTmp > 3) {
						recommendScoreMap.put(itemScore.getItemId(), scortTmp + 0.3D);
					} else {
						recommendScoreMap.put(itemScore.getItemId(), scortTmp + 0.5D);
					}
				}
			}
		}
		/*
		 * 过滤区域
		 */
		if (recommendScoreMap.size() == 0) {
			return;
		}
		List<Long> spids = userConsumptionMapper.recommednQYFilter(recommendScoreMap.keySet(), request.getQy());
		for (Map.Entry<Long, Double> entry : recommendScoreMap.entrySet()) {
			if (!spids.contains(entry.getKey())) {
				continue;
			}
			if (!scoreMap.containsKey(entry.getKey())) {
				scoreMap.put(entry.getKey(), entry.getValue());
			} else {
				scoreMap.put(entry.getKey(), entry.getValue() + scoreMap.get(entry.getKey()));
			}
		}
	}
}
