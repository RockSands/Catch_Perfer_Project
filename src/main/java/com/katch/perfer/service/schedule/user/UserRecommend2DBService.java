package com.katch.perfer.service.schedule.user;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.katch.perfer.kettle.bean.KettleResult;
import com.katch.perfer.kettle.service.KettleNorthService;
import com.katch.perfer.mybatis.model.RecommendTaskTrack;
import com.katch.perfer.service.kettle.UserRecommendCSV2DBBuild;
import com.katch.perfer.service.schedule.Recommend2DBService;

/**
 * 消费记录导出
 * 
 * @author Administrator
 *
 */
@Service()
@ConditionalOnProperty(name = "consumer.mahout.type", havingValue = "user", matchIfMissing = true)
public class UserRecommend2DBService extends Recommend2DBService {

	@Autowired
	private KettleNorthService kettleNorthService;

	@Autowired
	private UserRecommendCSV2DBBuild userRecommendCSV2DBBuild;

	/**
	 * 修正
	 * 
	 * @throws KettleException
	 * @throws InterruptedException
	 * 
	 * @throws Exception
	 */
	@Override
	public void excute(RecommendTaskTrack track) throws Exception {
		KettleResult result = kettleNorthService.excuteJobOnce(userRecommendCSV2DBBuild.createJob());
		if (StringUtils.isNotEmpty(result.getErrMsg())) {
			throw new Exception("基于商品写入数据库的Kettle任务发生错误:" + result.getErrMsg());
		}
		track.setJobUuid(track.getJobUuid());
		track.setUpdateTime(new Date());
		recommendTaskTrackMapper.updateRecommendTaskTrack(track);
	}
}
