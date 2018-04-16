package com.katch.perfer.service.schedule;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.katch.perfer.consist.Consist;
import com.katch.perfer.mahout.service.MahoutRecommenderService;
import com.katch.perfer.mybatis.mapper.RecommendTaskTrackMapper;
import com.katch.perfer.mybatis.model.RecommendTaskTrack;

@Service
public class ItemRecommendExportService {

    private static Logger logger = LoggerFactory.getLogger(RecommendExportService.class);

    @Autowired
    private RecommendTaskTrackMapper recommendTaskTrackMapper;

    @Autowired
    @Qualifier("mahoutRecommenderService")
    private MahoutRecommenderService mahoutRecommenderService;

    @Scheduled(cron = "*/2 * * * * ?")
    public void excute() {
	RecommendTaskTrack track = recommendTaskTrackMapper.queryRecommendTaskTrack("SQY00001");
	if (!Consist.RECOM_TASK_TRACK_STEP_WRITE_FILE.equals(track.getStep())) {
	    return;
	}
	try {
	    if (mahoutRecommenderService.getDataModel() != null) {
		mahoutRecommenderService.excute();
	    }
	} catch (Exception ex) {
	    logger.error("推荐信息导出发生错误!", ex);
	    track.setStep(Consist.RECOM_TASK_TRACK_STEP_FREE);
	    track.setUpdateTime(new Date());
	    track.setStatus(Consist.RECOM_TASK_TRACK_STATUS_ERROR);
	    recommendTaskTrackMapper.updateRecommendTaskTrack(track);
	}
    }
}
