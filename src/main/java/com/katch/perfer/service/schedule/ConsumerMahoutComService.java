package com.katch.perfer.service.schedule;

import java.io.File;
import java.util.Date;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.katch.perfer.config.ConsumerExportCSVProperties;
import com.katch.perfer.consist.Consist;
import com.katch.perfer.kettle.bean.KettleResult;
import com.katch.perfer.kettle.consist.KettleVariables;
import com.katch.perfer.kettle.service.KettleNorthService;
import com.katch.perfer.mahout.service.MahoutRecommenderService;
import com.katch.perfer.mybatis.mapper.RecommendTaskTrackMapper;
import com.katch.perfer.mybatis.model.RecommendTaskTrack;

/**
 * 消费记录导出
 * 
 * @author Administrator
 *
 */
@Service
public class ConsumerMahoutComService {
    private static Logger logger = LoggerFactory.getLogger(ConsumerMahoutComService.class);

    @Autowired
    private KettleNorthService kettleNorthService;

    @Autowired
    private RecommendTaskTrackMapper recommendTaskTrackMapper;

    @Autowired
    private ConsumerExportCSVProperties consumerExportCSVProperties;

    @Autowired
    @Qualifier("mahoutRecommenderService")
    private MahoutRecommenderService mahoutRecommenderService;

    @Scheduled(cron = "*/2 * * * * ?")
    public void excute() {
	RecommendTaskTrack track = recommendTaskTrackMapper.queryRecommendTaskTrack("SQY00001");
	if (!Consist.RECOM_TASK_TRACK_STEP_CSV_EXPORT.equals(track.getStep())) {
	    return;
	}
	try {
	    KettleResult kettleResult = kettleNorthService.queryJob(track.getJobUuid());
	    if (KettleVariables.RECORD_STATUS_ERROR.equals(kettleResult.getStatus())) {
		logger.error("消费记录导出CSV发生错误，Kettle[" + track.getJobUuid() + "]执行错误!");
		track.setStep(Consist.RECOM_TASK_TRACK_STEP_FREE);
		track.setUpdateTime(new Date());
		track.setStatus(Consist.RECOM_TASK_TRACK_STATUS_ERROR);
		recommendTaskTrackMapper.updateRecommendTaskTrack(track);
	    } else if (KettleVariables.RECORD_STATUS_RUNNING.equals(kettleResult.getStatus())) {
		if (System.currentTimeMillis() - track.getUpdateTime().getTime() > 60L * 60L * 1000L) {
		    logger.error("消费记录导出CSV超时，Kettle[" + track.getJobUuid() + "]执行错误!");
		    track.setStep(Consist.RECOM_TASK_TRACK_STEP_FREE);
		    track.setUpdateTime(new Date());
		    track.setStatus(Consist.RECOM_TASK_TRACK_STATUS_ERROR);
		    recommendTaskTrackMapper.updateRecommendTaskTrack(track);
		    // 强制关闭
		    kettleNorthService.deleteJobForce(track.getJobUuid());
		}
	    } else if (KettleVariables.RECORD_STATUS_FINISHED.equals(kettleResult.getStatus())) {
		logger.info("消费记录导出CSV完成!");
		track.setStep(Consist.RECOM_TASK_TRACK_STEP_MAHOUT_COM);
		track.setUpdateTime(new Date());
		recommendTaskTrackMapper.updateRecommendTaskTrack(track);
		File file = new File(consumerExportCSVProperties.getExportFileName());
		DataModel dataModel = new FileDataModel(file);
		track.setStep(Consist.RECOM_TASK_TRACK_STEP_WRITE_FILE);
		recommendTaskTrackMapper.updateRecommendTaskTrack(track);
		mahoutRecommenderService.setDataModel(dataModel);
	    } else {
		logger.error("消费记录导出CSV失败!Kettle[" + track.getJobUuid() + "]状态非法[" + kettleResult.getStatus() + "]!");
		track.setStep(Consist.RECOM_TASK_TRACK_STEP_FREE);
		track.setUpdateTime(new Date());
		track.setStatus(Consist.RECOM_TASK_TRACK_STATUS_ERROR);
		recommendTaskTrackMapper.updateRecommendTaskTrack(track);
		// 强制关闭
		kettleNorthService.deleteJobForce(track.getJobUuid());
	    }
	} catch (Exception ex) {
	    logger.error("消费记录导出CSV发生错误，Kettle[" + track.getJobUuid() + "]执行错误!");
	    track.setStep(Consist.RECOM_TASK_TRACK_STEP_FREE);
	    track.setUpdateTime(new Date());
	    track.setStatus(Consist.RECOM_TASK_TRACK_STATUS_ERROR);
	    recommendTaskTrackMapper.updateRecommendTaskTrack(track);
	}
    }
}
