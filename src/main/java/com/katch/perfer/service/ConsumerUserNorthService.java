package com.katch.perfer.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.katch.perfer.mybatis.mapper.BaseUserRecommendMapper;
import com.katch.perfer.mybatis.model.BaseUserRecommend;

@Service("consumerNorthService")
@ConditionalOnProperty(name = "consumer.mahout.type", havingValue = "user", matchIfMissing = true)
public class ConsumerUserNorthService implements ConsumerNorthService {
	@Autowired
	private BaseUserRecommendMapper baseUserRecommendMapper;

	@Override
	public List<Long> queryRecommend(long yhid, String qy) {
		List<BaseUserRecommend> recommends = baseUserRecommendMapper.queryRecommender(yhid);
		List<Long> recommendList = new ArrayList<Long>();
		for (BaseUserRecommend recommend : recommends) {
			recommendList.add(recommend.getItemId());
		}
		return recommendList;
	}

}
