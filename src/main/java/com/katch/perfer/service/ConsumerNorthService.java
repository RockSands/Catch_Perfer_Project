package com.katch.perfer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

/**
 * 消费记录导出
 * 
 * @author Administrator
 *
 */
@Service
public class ConsumerNorthService {
	
	Random random = new Random();

	public List<Long> queryRecommend(long yhid, String qy) {
		List<Long> list = new ArrayList<Long>();
		for(int i=0;i<100;i++) {
			list.add(random.nextLong());
		}
		return list;
	}
	
}
