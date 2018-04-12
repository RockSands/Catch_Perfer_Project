package com.katch.perfer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.katch.perfer.mahout.service.MahoutRecommenderService;

/**
 * 消费记录导出
 * 
 * @author Administrator
 *
 */
@Service
public class ConsumerAutoDealService {

	private static Logger logger = LoggerFactory.getLogger(ConsumerAutoDealService.class);
	
	@Autowired
	private ConsumerExportService consumerExportService;

	@Autowired
	@Qualifier("mahoutRecommenderService")
	private MahoutRecommenderService mahoutRecommenderService;

	/**
	 * 自动执行
	 * 
	 * @throws Exception
	 */
	@Scheduled(cron = "${consumer.mahout.cron}")
	public void excute() throws Exception {
		String errMsg = consumerExportService.excute();
		logger.error("==1111==>" + errMsg);
		if(errMsg != null) {
			logger.error("消费记录计算发生异常:\n" + errMsg);
			return;
		}
		logger.error("==2222==>" + errMsg);
		mahoutRecommenderService.excute();
	}
}
