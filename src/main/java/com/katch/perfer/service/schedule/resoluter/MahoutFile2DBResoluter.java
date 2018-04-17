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
import com.katch.perfer.service.schedule.Recommend2DBService;

@Component
public class MahoutFile2DBResoluter extends ConsumerRecommendResoluter {
	private static Logger logger = LoggerFactory.getLogger(MahoutFile2DBResoluter.class);

	@Autowired
	private KettleNorthService kettleNorthService;

	@Autowired
	private Recommend2DBService recommend2DBService;

	private KettleResult kettleResult;

	@Override
	protected boolean canResolve(RecommendTaskTrack track) {
		return Consist.RECOM_TASK_TRACK_STEP_WRITE_DB.equals(track.getStep());
	}

	@Override
	protected void resolve(RecommendTaskTrack track) throws Exception {
		if (kettleResult == null) {
			recommend2DBService.excute(track);
		}
		String jobUuid = track.getJobUuid();
		kettleResult = kettleNorthService.queryJob(jobUuid);
		logger.debug("消费记录导出CSV的Kettle任务[" + jobUuid + "]状态为[" + kettleResult.getStatus() + "]");
		if (KettleVariables.RECORD_STATUS_ERROR.equals(kettleResult.getStatus())) {
			logger.error("推荐信息导入数据库发生错误，Kettle[" + jobUuid + "]执行错误!\n" + kettleResult.getErrMsg());
			throw new Exception("推荐信息导入数据库发生错误，Kettle[" + jobUuid + "]执行错误!");
		} else if (KettleVariables.RECORD_STATUS_RUNNING.equals(kettleResult.getStatus())
				|| KettleVariables.RECORD_STATUS_APPLY.equals(kettleResult.getStatus())) {
			if (System.currentTimeMillis() - track.getUpdateTime().getTime() > 30L * 60L * 1000L) {
				throw new Exception("推荐信息导入数据库超时，Kettle[" + track.getJobUuid() + "]执行错误!");
			}
		} else if (KettleVariables.RECORD_STATUS_FINISHED.equals(kettleResult.getStatus())) {
			logger.info("推荐信息导入数据库完成!");
			track.setStep(Consist.RECOM_TASK_TRACK_STEP_FREE);
			track.setStatus(Consist.RECOM_TASK_TRACK_STATUS_FINISHED);
			track.setUpdateTime(new Date());
			track.setJobUuid(null);
			recommendTaskTrackMapper.updateRecommendTaskTrack(track);
		} else {
			throw new Exception("推荐信息导入数据库失败!Kettle[" + jobUuid + "]状态非法[" + kettleResult.getStatus() + "]!");
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
