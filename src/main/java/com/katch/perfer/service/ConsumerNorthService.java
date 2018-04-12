package com.katch.perfer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.katch.perfer.mahout.service.MahoutRecommenderService;

/**
 * 消费记录导出
 * 
 * @author Administrator
 *
 */
@Service
public class ConsumerNorthService {
	
	@Autowired
	private ConsumerExportService consumerExportService;

	@Autowired
	@Qualifier("mahoutRecommenderService")
	private MahoutRecommenderService mahoutRecommenderService;
	
	/**
	 * 修正
	 * @throws Exception 
	 */
	public void excute() throws Exception {
		consumerExportService.excute();
		mahoutRecommenderService.excute();
	}
}
