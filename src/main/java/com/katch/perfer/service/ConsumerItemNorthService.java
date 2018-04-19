package com.katch.perfer.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.katch.perfer.mybatis.mapper.BaseItemRecommendMapper;
import com.katch.perfer.mybatis.mapper.UserConsumptionMapper;
import com.katch.perfer.mybatis.model.BaseItemRecommend;
import com.katch.perfer.mybatis.model.RecommendItemScore;
import com.katch.perfer.mybatis.model.UserConsumption;

@Service("consumerNorthService")
@ConditionalOnProperty(name = "consumer.mahout.type", havingValue = "item", matchIfMissing = false)
public class ConsumerItemNorthService extends ConsumerNorthService {
	@Autowired
	private BaseItemRecommendMapper baseItemRecommendMapper;

	@Autowired
	private UserConsumptionMapper userConsumptionMapper;

	@Override
	public List<Long> queryRecommend(long yhid, String qy) {
		List<Long> recommendList = new ArrayList<Long>();
		// 增加权重
		List<RecommendItemScore> weightScores = priorityService.queryItemWeight(qy);
		RecommendItemScore itemScore = null;
		if (recommendRestProperties.getWeightSize() > 0) {
			int index = recommendRestProperties.getWeightSize();
			for (Iterator<RecommendItemScore> it = weightScores.iterator(); it.hasNext();) {
				if (index < 1) {
					break;
				}
				itemScore = it.next();
				recommendList.add(itemScore.getItemId());
				it.remove();
				index--;
			}
		}
		// 新物品
		List<RecommendItemScore> newItemScores = new ArrayList<RecommendItemScore>();
		if (recommendRestProperties.isNewItemOpen()) {
			newItemScores.addAll(priorityService.queryNewItems(recommendRestProperties.getNewItemTimeOut()));
			if (recommendRestProperties.getNewItemSize() > 0) {
				int index = recommendRestProperties.getNewItemSize();
				for (Iterator<RecommendItemScore> it = newItemScores.iterator(); it.hasNext();) {
					if (index < 1) {
						break;
					}
					itemScore = it.next();
					if (!recommendList.contains(itemScore.getItemId())) {
						recommendList.add(itemScore.getItemId());
						it.remove();
						index--;
					}
				}
			}
		}
		// 消费记录
		List<UserConsumption> consumptions = userConsumptionMapper.queryUserConsumptions(yhid);
		Map<Long, Double> map = new LinkedHashMap<Long, Double>();
		int index = 0;
		double score = 0.0;
		for (UserConsumption consumption : consumptions.subList(0, 10)) {
			for (BaseItemRecommend itemRecommend : baseItemRecommendMapper.queryRecommenders(consumption.getItmeId())) {
				if (recommendList.contains(itemRecommend.getItemId2())) {
					continue;
				}
				if (!map.containsKey(itemRecommend.getItemId2())) {
					map.put(itemRecommend.getItemId2(), itemRecommend.getScore() - 0.1 * index);
				} else {
					score = map.get(itemRecommend.getItemId2());
					if (score > 4.95D) {
						map.put(itemRecommend.getItemId2(), 5.0D);
					} else if (score > 4.5) {
						map.put(itemRecommend.getItemId2(), 0.05D);
					} else if (score > 4) {
						map.put(itemRecommend.getItemId2(), 0.1D);
					} else if (score > 3) {
						map.put(itemRecommend.getItemId2(), 0.3D);
					} else {
						map.put(itemRecommend.getItemId2(), 0.5D);
					}
				}
			}
			index++;
		}
		for (RecommendItemScore roll : weightScores) {
			if (recommendList.contains(roll.getItemId())) {
				continue;
			}
			if (!map.containsKey(roll.getItemId())) {
				map.put(roll.getItemId(), roll.getScore());
			} else {
				map.put(roll.getItemId(), roll.getScore() + map.get(roll.getItemId()));
			}
		}
		for (RecommendItemScore roll : newItemScores) {
			if (recommendList.contains(roll.getItemId())) {
				continue;
			}
			if (!map.containsKey(roll.getItemId())) {
				map.put(roll.getItemId(), roll.getScore());
			} else {
				map.put(roll.getItemId(), roll.getScore() + map.get(roll.getItemId()));
			}
		}
		List<Map.Entry<Long, Double>> mapList = new ArrayList<Map.Entry<Long, Double>>(map.entrySet());
		Collections.sort(mapList, new Comparator<Map.Entry<Long, Double>>() {
			@Override
			public int compare(Entry<Long, Double> o1, Entry<Long, Double> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		for (Map.Entry<Long, Double> entry : mapList) {
			recommendList.add(entry.getKey());
		}
		return recommendList;
	}

}
