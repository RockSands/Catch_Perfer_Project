package com.katch.perfer.service.schedule;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.katch.perfer.config.ConsumerProperties;
import com.katch.perfer.consist.Consist;
import com.katch.perfer.mybatis.mapper.RecommendTaskTrackMapper;
import com.katch.perfer.mybatis.model.RecommendTaskTrack;

public abstract class ConsumerRecommendResoluter {
	private static Logger logger = LoggerFactory.getLogger(ConsumerRecommendResoluter.class);

	@Autowired
	protected RecommendTaskTrackMapper recommendTaskTrackMapper;

	@Autowired
	protected ConsumerProperties consumerProperties;

	/**
	 * 外面调用
	 * 
	 * @param track
	 */
	public synchronized final void excuteResolve(RecommendTaskTrack track) {
		try {
			if (canResolve(track)) {
				resolve(track);
			}
			if (System.currentTimeMillis() - track.getStartTime().getTime() > 2L * 60L * 60L * 1000L) {
				throw new Exception("消费信息推荐进程失败，执行超时!");
			}
		} catch (Exception ex) {
			logger.error("消费信息推荐进程失败!", ex);
			clean(track);
		}
	}

	private void clean(RecommendTaskTrack track) {
		try {
			clear(track);
		} catch (Exception ex) {
			logger.error("消费信息推荐进程-清理方法执行失败", ex);
		}
		track.setStep(Consist.RECOM_TASK_TRACK_STEP_FREE);
		track.setUpdateTime(new Date());
		track.setStatus(Consist.RECOM_TASK_TRACK_STATUS_ERROR);
		recommendTaskTrackMapper.updateRecommendTaskTrack(track);
	}

	/**
	 * 判断是否能被处理
	 * 
	 * @return
	 */
	protected abstract boolean canResolve(RecommendTaskTrack track);

	/**
	 * 处理
	 */
	protected abstract void resolve(RecommendTaskTrack track) throws Exception;

	/**
	 * 失败的时候调用
	 */
	protected abstract void clear(RecommendTaskTrack track);
}
