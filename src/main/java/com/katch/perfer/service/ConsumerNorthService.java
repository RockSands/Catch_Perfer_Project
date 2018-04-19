package com.katch.perfer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.katch.perfer.config.RecommendRestProperties;

/**
 * 消费记录导出
 * 
 * @author Administrator
 *
 */
public abstract class ConsumerNorthService {
	@Autowired
	protected RecommendRestProperties recommendRestProperties;
	
	@Autowired
	protected RecommendPriorityService priorityService;
	
	public abstract List<Long> queryRecommend(long yhid, String qy);
}
