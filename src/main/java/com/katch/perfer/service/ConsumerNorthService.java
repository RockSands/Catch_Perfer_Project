package com.katch.perfer.service;

import java.util.List;

/**
 * 消费记录导出
 * 
 * @author Administrator
 *
 */
public abstract interface ConsumerNorthService {
	List<Long> queryRecommend(long yhid, String qy);
}
