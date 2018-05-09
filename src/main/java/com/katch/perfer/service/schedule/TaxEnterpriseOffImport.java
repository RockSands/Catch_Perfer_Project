package com.katch.perfer.service.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.katch.perfer.consist.Consist;
import com.katch.perfer.mybatis.mapper.RecommendTaskTrackMapper;
import com.katch.perfer.mybatis.model.RecommendTaskTrack;
import com.katch.perfer.service.kettle.TaxEnterpriseImportBuilder;

@Service
public class TaxEnterpriseOffImport {
    private static Logger logger = LoggerFactory.getLogger(TaxEnterpriseOffImport.class);
    @Autowired
    private RecommendTaskTrackMapper recommendTaskTrackMapper;
    
    @Autowired
    private TaxEnterpriseImportBuilder taxEnterpriseImportBuilder;

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void excute() {
	RecommendTaskTrack track = recommendTaskTrackMapper.queryRecommendTaskTrack("SQY00002");
	logger.info("消费推荐同步信息准备启动!");
	if (Consist.RECOM_TASK_TRACK_STEP_FREE.equals(track.getStep())
		|| (Consist.RECOM_TASK_TRACK_STATUS_ERROR.equals(track.getStep()))) {
	}
    }
}
