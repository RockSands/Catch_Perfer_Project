package com.katch.perfer.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.katch.perfer.mybatis.model.RecommendItemScore;

@Mapper
public interface BaseItemRecommendMapper {
	List<RecommendItemScore> queryRecommenders(@Param("itemID") long itemID);
}
