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
import com.katch.perfer.service.kettle.TaxEnterpriseImportBuilder;

@Service
public class TaxEnterpriseOffImport {
	private static Logger logger = LoggerFactory.getLogger(TaxEnterpriseOffImport.class);
	@Autowired
	private RecommendTaskTrackMapper recommendTaskTrackMapper;

	@Autowired
	private TaxEnterpriseImportBuilder taxEnterpriseImportBuilder;

	@Autowired
	private KettleNorthService kettleNorthService;

	@Scheduled(cron = "0 0 0/1 * * ?")
	public void excute() {
		RecommendTaskTrack track = recommendTaskTrackMapper.queryRecommendTaskTrack("SQY_TAX_ENTERPRISE_OFF_IMPORT");
		logger.info("纳税企业信息同步信息准备启动!");
		excute(track);
	}

	private void excute(RecommendTaskTrack track) {
		logger.info("纳税企业信息同步信息Kettle启动!");
		String uuid = null;
		try {
			uuid = doImport();
			track.setJobUuid(uuid);
			track.setStep(Consist.RECOM_TASK_TRACK_STEP_CSV_EXPORT);
			track.setStartTime(new Date());
			track.setUpdateTime(new Date());
			track.setStatus(Consist.RECOM_TASK_TRACK_STATUS_RUNNING);
			recommendTaskTrackMapper.updateRecommendTaskTrack(track);
		} catch (Exception e) {
			logger.error("纳税企业信息同步信息Kettle启动!", e);
			track.setStep(Consist.RECOM_TASK_TRACK_STEP_FREE);
			track.setUpdateTime(new Date());
			track.setStatus(Consist.RECOM_TASK_TRACK_STATUS_ERROR);
			recommendTaskTrackMapper.updateRecommendTaskTrack(track);
		}
	}

	/**
	 * 导入
	 * 
	 * @return
	 * @throws KettleException
	 */
	private String doImport() throws KettleException {
		logger.info("纳税企业信息同步信息准备启动!");
		KettleResult result = kettleNorthService.excuteJobOnce(taxEnterpriseImportBuilder.createJob());
		if (StringUtils.isNotEmpty(result.getErrMsg())) {
			throw new KettleException("Kettle导出消费记录失败,kettle发生问题:" + result.getErrMsg());
		}
		return result.getUuid();
	}
}
