package com.katch.perfer.service.schedule;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.katch.perfer.consist.Consist;
import com.katch.perfer.mybatis.mapper.RecommendTaskTrackMapper;
import com.katch.perfer.mybatis.model.RecommendTaskTrack;

@Service
public class ConsumerRecommendProcess {

	@Autowired
	private RecommendTaskTrackMapper recommendTaskTrackMapper;
	
	@Autowired
	private List<ConsumerRecommendResoluter> resoluters;

	@Scheduled(cron = "*/10 * * * * ?")
	public void excute() {
		RecommendTaskTrack track = recommendTaskTrackMapper.queryRecommendTaskTrack("SQY00001");
		if(!Consist.RECOM_TASK_TRACK_STATUS_RUNNING.equals(track.getStatus())) {
			return;
		}
		for(ConsumerRecommendResoluter resoluter : resoluters) {
			resoluter.excuteResolve(track);
		}
	}
}
