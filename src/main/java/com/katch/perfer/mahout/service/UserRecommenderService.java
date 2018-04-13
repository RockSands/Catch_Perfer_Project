package com.katch.perfer.mahout.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.katch.perfer.mahout.model.UserRecommender;
import com.katch.perfer.mybatis.batch.BatchVO;
import com.katch.perfer.mybatis.batch.MybatisBatchRepoRepository;
import com.katch.perfer.mybatis.mapper.UserRecommenderMapper;

@Service
public class UserRecommenderService {
	@Autowired
	private UserRecommenderMapper recommenderMapper;

	@Autowired
	private MybatisBatchRepoRepository mybatisBatchRepoRepository;

	public void saveBatch(List<UserRecommender> saveRecommenders) {
		List<BatchVO> vos = new ArrayList<BatchVO>(200);
		BatchVO vo = null;
		for (UserRecommender userRecommender : saveRecommenders) {
			vos.add(new BatchVO(BatchVO.OPERATION_DELET,
					"com.katch.perfer.mybatis.mapper.UserRecommenderMapper.deleteRecommender",
					userRecommender.getUserId()));
			if(!StringUtils.isEmpty(userRecommender.getItemRecommedns())) {
				vos.add(new BatchVO(BatchVO.OPERATION_INSERT,
						"com.katch.perfer.mybatis.mapper.UserRecommenderMapper.insertRecommender",
						userRecommender.getUserId()));
			}
		}
	}

	public UserRecommenderMapper getRecommenderMapper() {
		return recommenderMapper;
	}

	public void setRecommenderMapper(UserRecommenderMapper recommenderMapper) {
		this.recommenderMapper = recommenderMapper;
	}

	public MybatisBatchRepoRepository getMybatisBatchRepoRepository() {
		return mybatisBatchRepoRepository;
	}

	public void setMybatisBatchRepoRepository(MybatisBatchRepoRepository mybatisBatchRepoRepository) {
		this.mybatisBatchRepoRepository = mybatisBatchRepoRepository;
	}
}
