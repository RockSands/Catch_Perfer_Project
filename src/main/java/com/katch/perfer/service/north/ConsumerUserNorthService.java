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
import com.katch.perfer.mybatis.mapper.BaseUserRecommendMapper;
import com.katch.perfer.mybatis.model.RecommendItemScore;

@Service()
@ConditionalOnProperty(name = "consumer.mahout.type", havingValue = "user", matchIfMissing = true)
public class ConsumerUserNorthService extends ConsumerNorthService {
	@Autowired
	private BaseUserRecommendMapper baseUserRecommendMapper;

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
		// 商品推荐计算
		Map<Long, Double> map = new HashMap<Long, Double>();
		if (request.getYhid() != null) {
			List<RecommendItemScore> userItemScores = baseUserRecommendMapper.queryRecommenders(request.getYhid());
			for (RecommendItemScore recommend : userItemScores) {
				returnList.add(recommend.getItemId());
				if (returnList.contains(recommend.getItemId())) {
					continue;
				}
				map.put(recommend.getItemId(), recommend.getScore());
			}
		}
		for (RecommendItemScore index : weightScores) {// 新物品是否加权
			if (returnList.contains(index.getItemId())) {
				continue;
			}
			if (!map.containsKey(index.getItemId())) {
				map.put(index.getItemId(), index.getScore());
			} else {
				map.put(index.getItemId(), index.getScore() + map.get(index.getItemId()));
			}
		}
		for (RecommendItemScore index : newItemScores) {
			if (returnList.contains(index.getItemId())) {
				continue;
			}
			if (!map.containsKey(index.getItemId())) {
				map.put(index.getItemId(), index.getScore());
			} else {
				map.put(index.getItemId(), index.getScore() + map.get(index.getItemId()));
			}
		}
		// 这里将map.entrySet()转换成list
		List<Map.Entry<Long, Double>> mapList = new ArrayList<Map.Entry<Long, Double>>(map.entrySet());
		// 然后通过比较器来实现排序
		Collections.sort(mapList, new Comparator<Map.Entry<Long, Double>>() {
			@Override
			public int compare(Entry<Long, Double> o1, Entry<Long, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		for (Map.Entry<Long, Double> entry : mapList) {
			returnList.add(entry.getKey());
			if (returnList.size() >= recommendRestProperties.getReturnSize()) {
				break;
			}
		}
		return returnList;
	}
}