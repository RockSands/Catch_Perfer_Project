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
		for (UserRecommender userRecommender : saveRecommenders) {
			vos.add(new BatchVO(BatchVO.OPERATION_DELET,
					"com.katch.perfer.mybatis.mapper.UserRecommenderMapper.deleteRecommender",
					userRecommender.getUserId()));
			if (!StringUtils.isEmpty(userRecommender.getItemRecommedns())) {
				vos.add(new BatchVO(BatchVO.OPERATION_INSERT,
						"com.katch.perfer.mybatis.mapper.UserRecommenderMapper.insertRecommender",
						userRecommender));
			}
		}
	}
	
//	@Autowired
//	private JdbcTemplate jdbcTemplate;
//	
//	public void saveBatch(List<UserRecommender> saveRecommenders) {
//		List<Object[]> deleteParams = new ArrayList<Object[]>(1000);
//		List<Object[]> insertParams = new ArrayList<Object[]>(1000);
//		for (UserRecommender userRecommender : saveRecommenders) {
//			deleteParams.add(new Object[] { userRecommender.getUserId() });
//			if (StringUtils.isEmpty(userRecommender.getItemRecommedns())) {
//				continue;
//			}
//			insertParams.add(new Object[] { userRecommender.getUserId(), userRecommender.getItemRecommedns(),
//					userRecommender.getUpdateTime() });
//		}
//		jdbcTemplate.batchUpdate("DELETE FROM SQY_RZDK_SPTJ WHERE YH_ID = ?", deleteParams);
//		jdbcTemplate.batchUpdate("INSERT INTO SQY_RZDK_SPTJ "
//				+ "(YH_ID, SP_RECOMMENDS, UPDATE_TIME) "
//				+ "VALUES (?,?,?)",
//				insertParams);
//	}

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
