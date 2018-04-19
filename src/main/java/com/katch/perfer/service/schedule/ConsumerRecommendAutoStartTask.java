package com.katch.perfer.service.schedule;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.exception.KettleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.katch.perfer.consist.Consist;
import com.katch.perfer.kettle.bean.KettleResult;
import com.katch.perfer.kettle.service.KettleNorthService;
import com.katch.perfer.mybatis.mapper.RecommendTaskTrackMapper;
import com.katch.perfer.mybatis.model.RecommendTaskTrack;
import com.katch.perfer.service.kettle.ConsumerExportCSVBuilder;

@Service
public class ConsumerRecommendAutoStartTask {
	private static Logger logger = LoggerFactory.getLogger(ConsumerRecommendAutoStartTask.class);

	@Autowired
	private RecommendTaskTrackMapper recommendTaskTrackMapper;

	@Autowired
	private ConsumerExportCSVBuilder consumerExportCSVBuilder;

	@Autowired
	private KettleNorthService kettleNorthService;

	/**
	 * 自动执行
	 * 
	 * @throws Exception
	 */
	@Scheduled(cron = "${consumer.mahout.cron}")
	public void excute() {
		RecommendTaskTrack track = recommendTaskTrackMapper.queryRecommendTaskTrack("SQY00001");
		logger.info("消费推荐同步信息启动!");
		if (Consist.RECOM_TASK_TRACK_STEP_FREE.equals(track.getStep())
				|| (Consist.RECOM_TASK_TRACK_STATUS_ERROR.equals(track.getStep()))) {
			// TODO 设置间隔
			if (track.getUpdateTime() == null || System.currentTimeMillis() - track.getUpdateTime().getTime() > 1000L) {
				excute(track);
			}
		}
	}

	/**
	 * 修正
	 * 
	 * @param track
	 */
	private void excute(RecommendTaskTrack track) {
		logger.info("用户消费记录导出CSV启动!");
		String uuid = null;
		try {
			uuid = doExport();
			track.setJobUuid(uuid);
			track.setStep(Consist.RECOM_TASK_TRACK_STEP_CSV_EXPORT);
			track.setStartTime(new Date());
			track.setUpdateTime(new Date());
			track.setStatus(Consist.RECOM_TASK_TRACK_STATUS_RUNNING);
			recommendTaskTrackMapper.updateRecommendTaskTrack(track);
		} catch (Exception e) {
			logger.error("用户消费记录导出CSV启动异常!", e);
			track.setStep(Consist.RECOM_TASK_TRACK_STEP_FREE);
			track.setUpdateTime(new Date());
			track.setStatus(Consist.RECOM_TASK_TRACK_STATUS_ERROR);
			recommendTaskTrackMapper.updateRecommendTaskTrack(track);
		}
	}

	/**
	 * 导出
	 * 
	 * @return
	 * @throws KettleException
	 */
	private String doExport() throws KettleException {
		logger.info("用户消费记录开始导出!");
		KettleResult result = kettleNorthService.excuteJobOnce(consumerExportCSVBuilder.createJob());
		if (StringUtils.isNotEmpty(result.getErrMsg())) {
			throw new KettleException("Kettle导出消费记录失败,kettle发生问题:" + result.getErrMsg());
		}
		return result.getUuid();
	}
}
