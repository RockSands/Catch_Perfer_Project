package com.katch.perfer.service.schedule.resoluter;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.katch.perfer.consist.Consist;
import com.katch.perfer.kettle.bean.KettleResult;
import com.katch.perfer.kettle.consist.KettleVariables;
import com.katch.perfer.kettle.service.KettleNorthService;
import com.katch.perfer.mybatis.model.RecommendTaskTrack;
import com.katch.perfer.service.schedule.ConsumerRecommendResoluter;

@Component
public class CSVExportTrackResoluter extends ConsumerRecommendResoluter {

	private static Logger logger = LoggerFactory.getLogger(CSVExportTrackResoluter.class);

	@Autowired
	private KettleNorthService kettleNorthService;

	private KettleResult kettleResult;

	@Override
	protected boolean canResolve(RecommendTaskTrack track) {
		return Consist.RECOM_TASK_TRACK_STEP_CSV_EXPORT.equals(track.getStep());
	}

	@Override
	protected void resolve(RecommendTaskTrack track) throws Exception {
		String jobUuid = track.getJobUuid();
		kettleResult = kettleNorthService.queryJob(jobUuid);
		logger.debug("消费记录导出CSV的Kettle任务[" + jobUuid + "]状态为[" + kettleResult.getStatus() + "]");
		if (KettleVariables.RECORD_STATUS_ERROR.equals(kettleResult.getStatus())) {
			logger.error("消费记录导出CSV发生错误，Kettle[" + jobUuid + "]执行错误!\n" + kettleResult.getErrMsg());
			throw new Exception("消费记录导出CSV发生错误，Kettle[" + jobUuid + "]执行错误!");
		} else if (KettleVariables.RECORD_STATUS_RUNNING.equals(kettleResult.getStatus())) {
			if (System.currentTimeMillis() - track.getUpdateTime().getTime() > 30L * 60L * 1000L) {
				throw new Exception("消费记录导出CSV超时，Kettle[" + track.getJobUuid() + "]执行错误!");
			}
		} else if (KettleVariables.RECORD_STATUS_FINISHED.equals(kettleResult.getStatus())) {
			logger.info("消费记录导出CSV完成!");
			track.setStep(Consist.RECOM_TASK_TRACK_STEP_MAHOUT_COM);
			track.setUpdateTime(new Date());
			track.setJobUuid(null);
			recommendTaskTrackMapper.updateRecommendTaskTrack(track);
		} else {
			throw new Exception("消费记录导出CSV失败!Kettle[" + jobUuid + "]状态非法[" + kettleResult.getStatus() + "]!");
		}
	}

	private void deleteJob(String jobId) {
		try {
			kettleNorthService.deleteJob(jobId);
		} catch (Exception e) {
			logger.error("关闭Kettle任务[" + jobId + "]失败!", e);
		}
	}

	private void deleteJobForce(String jobId) {
		try {
			kettleNorthService.deleteJobForce(jobId);
		} catch (Exception e) {
			logger.error("强制关闭Kettle任务[" + jobId + "]失败!", e);
		}
	}

	@Override
	protected void clear(RecommendTaskTrack track) {
		if (kettleResult == null) {
			return;
		}
		if (KettleVariables.RECORD_STATUS_FINISHED.equals(kettleResult.getStatus())
				|| KettleVariables.RECORD_STATUS_ERROR.equals(kettleResult.getStatus())) {
			deleteJob(kettleResult.getUuid());
		} else {
			deleteJobForce(track.getId());
		}
		kettleResult = null;
	}
}
