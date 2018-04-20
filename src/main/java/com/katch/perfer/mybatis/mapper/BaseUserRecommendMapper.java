package com.katch.perfer.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.katch.perfer.mybatis.model.RecommendItemScore;

/**
 * @author Administrator
 *
 */
@Mapper
public interface BaseUserRecommendMapper {
	List<RecommendItemScore> queryRecommenders(@Param("userID") long userID);
}
