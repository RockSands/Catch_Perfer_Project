package com.katch.perfer.service.schedule;

import org.springframework.beans.factory.annotation.Autowired;

import com.katch.perfer.mybatis.mapper.RecommendTaskTrackMapper;
import com.katch.perfer.mybatis.model.RecommendTaskTrack;

public abstract class Recommend2DBService {
	@Autowired
	protected RecommendTaskTrackMapper recommendTaskTrackMapper;
	
	public abstract void excute(RecommendTaskTrack track) throws Exception;
}
