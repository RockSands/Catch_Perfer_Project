package com.katch.perfer.service.schedule;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.katch.perfer.config.ConsumerExportCSVProperties;
import com.katch.perfer.consist.Consist;
import com.katch.perfer.kettle.bean.KettleResult;
import com.katch.perfer.kettle.consist.KettleVariables;
import com.katch.perfer.kettle.service.KettleNorthService;
import com.katch.perfer.mybatis.mapper.RecommendTaskTrackMapper;
import com.katch.perfer.mybatis.model.RecommendTaskTrack;

/**
 * 消费记录导出
 * 
 * @author Administrator
 *
 */
@Service
public class ConsumerExportTrackService {
	private static Logger logger = LoggerFactory.getLogger(ConsumerExportTrackService.class);

	@Autowired
	private KettleNorthService kettleNorthService;

	@Autowired
	private RecommendTaskTrackMapper recommendTaskTrackMapper;
	
	/**
	 * 格式化
	 */
	private DecimalFormat df = new DecimalFormat("##0.###");

	@Autowired
	private ConsumerExportCSVProperties consumerExportCSVProperties;

	@Scheduled(cron = "*/10 * * * * ?")
	public void excute() {
		try {
			RecommendTaskTrack track = recommendTaskTrackMapper.queryRecommendTaskTrack("SQY00001");
			if (Consist.RECOM_TASK_TRACK_STEP_CSV_EXPORT.equals(track.getStep())) {
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
					UserSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
					UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(100, similarity, dataModel);
					Recommender recommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, similarity);
				}
			}
		} catch (Exception ex) {

		}
	}
}
