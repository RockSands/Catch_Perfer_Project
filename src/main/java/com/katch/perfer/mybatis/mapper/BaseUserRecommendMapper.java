package com.katch.perfer.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.katch.perfer.mybatis.model.BaseUserRecommend;

/**
 * @author Administrator
 *
 */
@Mapper
public interface BaseUserRecommendMapper {
	List<BaseUserRecommend> queryRecommender(@Param("userID") long userID);
}
