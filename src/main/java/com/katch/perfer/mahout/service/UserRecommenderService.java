package com.katch.perfer.mahout.service;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.katch.perfer.mahout.model.UserRecommender;
import com.katch.perfer.mybatis.mapper.UserRecommenderMapper;

@Service
public class UserRecommenderService {
	@Autowired
	protected UserRecommenderMapper recommenderMapper;

	@Transactional()
	public void saveBatch(List<UserRecommender> saveRecommenders) {
		recommenderMapper.deleteRecommenders(saveRecommenders);
		Iterator<UserRecommender> it = saveRecommenders.iterator();
		while (it.hasNext()) {
			if (StringUtils.isEmpty(it.next().getItemRecommedns())) {
				it.remove();
			}
		}
		if (!saveRecommenders.isEmpty()) {
			recommenderMapper.insertRecommenders(saveRecommenders);
		}
	}
}
