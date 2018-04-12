package com.katch.perfer.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.katch.perfer.mahout.model.Recommender;


/**
 * @author Administrator
 *
 */
@Mapper
public interface RecommenderMapper {
	
	List<Recommender> queryRecommenders(@Param("userID") long userId);
	
	void deleteRecommenders(@Param("userID") long userId);
	
	void insertRecommenders(@Param("list") List<Recommender> recommenders);
}
