package com.katch.perfer.service.schedule.item;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.katch.perfer.kettle.bean.KettleResult;
import com.katch.perfer.kettle.service.KettleNorthService;
import com.katch.perfer.mybatis.model.RecommendTaskTrack;
import com.katch.perfer.service.kettle.ItemRecommendCSV2DBBuild;
import com.katch.perfer.service.schedule.Recommend2DBService;

@Service()
// @ConditionalOnProperty(name = "consumer.mahout.type", havingValue = "item",
// matchIfMissing = false)
public class ItemRecommend2DBService extends Recommend2DBService {

	@Autowired
	private KettleNorthService kettleNorthService;

	@Autowired
	private ItemRecommendCSV2DBBuild itemRecommendCSV2DBBuild;

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
		KettleResult result = kettleNorthService.excuteJobOnce(itemRecommendCSV2DBBuild.createJob());
		if (StringUtils.isNotEmpty(result.getErrMsg())) {
			throw new Exception("基于商品写入数据库的Kettle任务发生错误:" + result.getErrMsg());
		}
		track.setJobUuid(result.getUuid());
		track.setUpdateTime(new Date());
		recommendTaskTrackMapper.updateRecommendTaskTrack(track);
	}

}
