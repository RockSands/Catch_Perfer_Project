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
    private ConsumerImportService consumerImportService;

    @Autowired
    @Qualifier("mahoutRecommenderService")
    private MahoutRecommenderService mahoutRecommenderService;

    /**
     * 状态
     */
    private Long lastUpdateTime;

    private String status = "FREE";

    /**
     * 自动执行
     * 
     * @throws Exception
     */
    @Scheduled(cron = "${consumer.mahout.cron}")
    public void excute() {
	synchronized (status) {
	    if (!"FREE".equals(status)) {
		logger.error("用户喜好记录刷新中!");
		return;
	    }
	    if (lastUpdateTime != null && System.currentTimeMillis() - lastUpdateTime < 5L * 1000L * 60L) {
		logger.error("用户喜好记录刷新间隔必须超过5分钟!");
		return;
	    }
	    lastUpdateTime = System.currentTimeMillis();
	    status = "RUNNING";
	}
	try {
	    String errMsg = consumerExportService.excute();
	    if (errMsg != null) {
		logger.error("消费记录计算发生异常:\n" + errMsg);
		return;
	    }
	    mahoutRecommenderService.excute();
	    errMsg = consumerImportService.excute();
	    if (errMsg != null) {
		logger.error("消费记录计算发生异常:\n" + errMsg);
		return;
	    }
	    lastUpdateTime = System.currentTimeMillis();
	    status = "FREE";
	} catch (Exception ex) {
	    logger.error("用户喜好记录刷新发生错误!", ex);
	}
    }
}
