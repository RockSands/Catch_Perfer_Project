package com.katch.perfer.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.katch.perfer.mybatis.model.RecommendTaskSubTrack;
import com.katch.perfer.mybatis.model.RecommendTaskTrack;

@Mapper
public interface RecommendTaskTrackMapper {
	
	RecommendTaskTrack queryRecommendTaskTrack(@Param("id") String id);
	
	int updateRecommendTaskTrack(RecommendTaskTrack track);
	
	RecommendTaskSubTrack queryRecommendSubTaskTrack(RecommendTaskSubTrack subtrack);
	
	int insertRecommendTaskSubTrack(RecommendTaskSubTrack subtrack);
	
	int updateRecommendTaskSubTrack(RecommendTaskSubTrack subtrack);
	
	int deleteRecommendTaskSubTrack(@Param("id") String id);
}
