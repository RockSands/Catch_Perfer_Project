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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.katch.perfer.control.RecommedRequest;
import com.katch.perfer.mybatis.mapper.BaseItemRecommendMapper;
import com.katch.perfer.mybatis.mapper.UserConsumptionMapper;
import com.katch.perfer.mybatis.model.RecommendItemScore;
import com.katch.perfer.mybatis.model.UserConsumption;

@Service()
@ConditionalOnProperty(name = "consumer.mahout.type", havingValue = "item", matchIfMissing = false)
public class ConsumerItemNorthService extends ConsumerNorthService {

	@Autowired
	private BaseItemRecommendMapper baseItemRecommendMapper;

	@Autowired
	private UserConsumptionMapper userConsumptionMapper;

	@Override
	public List<Long> queryAllRecommend(RecommedRequest request) {
		List<Long> returnList = new ArrayList<Long>();
		// 增加权重
		List<RecommendItemScore> weightScores = priorityService.queryItemWeight(request.getQy());
		RecommendItemScore itemScore = null;
		if (recommendRestProperties.getWeightSize() > 0) {
			int index = recommendRestProperties.getWeightSize();
			for (Iterator<RecommendItemScore> it = weightScores.iterator(); it.hasNext();) {
				if (index < 1) {
					break;
				}
				itemScore = it.next();
				returnList.add(itemScore.getItemId());
				it.remove();
				index--;
			}
		}
		// 新物品
		List<RecommendItemScore> newItemScores = new ArrayList<RecommendItemScore>();
		if (recommendRestProperties.isNewItemOpen()) {
			newItemScores.addAll(
					priorityService.queryNewItems(request.getQy(), recommendRestProperties.getNewItemTimeOut()));
			if (recommendRestProperties.getNewItemSize() > 0) {
				int index = recommendRestProperties.getNewItemSize();
				for (Iterator<RecommendItemScore> it = newItemScores.iterator(); it.hasNext();) {
					if (index < 1) {
						break;
					}
					itemScore = it.next();
					if (!returnList.contains(itemScore.getItemId())) {
						returnList.add(itemScore.getItemId());
						it.remove();
						index--;
					}
				}
			}
		}
		// 消费记录推荐
		Map<Long, Double> scoreMap = new HashMap<Long, Double>();
		if (request.getYhid() != null) {
			List<UserConsumption> consumptions = userConsumptionMapper.queryUserConsumptions(request.getYhid(),
					request.getQy());
			double initScore = 4.5;
			double scortTmp = 0.0;
			List<RecommendItemScore> baseItemScores;
			RecommendItemScore itemRoll;
			for (UserConsumption consumption : consumptions.subList(0, Math.min(consumptions.size(), 10))) {
				initScore = initScore - 0.2;
				baseItemScores = baseItemRecommendMapper.queryRecommenders(consumption.getItmeId());
				for (int i = 0; i < baseItemScores.size(); i++) {
					itemRoll = baseItemScores.get(i);
					if (returnList.contains(itemRoll.getItemId())) {
						continue;
					}
					if (!scoreMap.containsKey(itemRoll.getItemId())) {
						scoreMap.put(itemRoll.getItemId(), itemRoll.getScore() - 0.1 * i);
					} else {
						scortTmp = scoreMap.get(itemRoll.getItemId());
						if (scortTmp > 4.95D) {
							scoreMap.put(itemRoll.getItemId(), 5.0D);
						} else if (scortTmp > 4) {
							scoreMap.put(itemRoll.getItemId(), scortTmp + 0.15D);
						} else if (scortTmp > 3) {
							scoreMap.put(itemRoll.getItemId(), scortTmp + 0.3D);
						} else {
							scoreMap.put(itemRoll.getItemId(), scortTmp + 0.5D);
						}
					}
				}
			}
		}
		// 开始加权并排序
		for (RecommendItemScore roll : weightScores) {
			if (returnList.contains(roll.getItemId())) {
				continue;
			}
			if (!scoreMap.containsKey(roll.getItemId())) {
				scoreMap.put(roll.getItemId(), roll.getScore());
			} else {
				scoreMap.put(roll.getItemId(), roll.getScore() + scoreMap.get(roll.getItemId()));
			}
		}
		for (RecommendItemScore roll : newItemScores) {
			if (returnList.contains(roll.getItemId())) {
				continue;
			}
			if (!scoreMap.containsKey(roll.getItemId())) {
				scoreMap.put(roll.getItemId(), roll.getScore());
			} else {
				scoreMap.put(roll.getItemId(), roll.getScore() + scoreMap.get(roll.getItemId()));
			}
		}
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
}