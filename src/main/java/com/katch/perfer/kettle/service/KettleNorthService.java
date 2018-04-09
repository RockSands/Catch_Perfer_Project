package com.katch.perfer.kettle.service;

import org.pentaho.di.core.exception.KettleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.katch.perfer.kettle.bean.KettleJobEntireDefine;
import com.katch.perfer.kettle.bean.KettleResult;
import com.katch.perfer.kettle.model.KettleRecord;
import com.katch.perfer.kettle.service.record.KettleJobService;

@Service
public class KettleNorthService {

	private static Logger logger = LoggerFactory.getLogger(KettleNorthService.class);

	@Autowired
	@Qualifier("remoteSerialRecordService")
	private KettleJobService kettleJobService;

	/**
	 * 
	 * @param jobEntire
	 * @return
	 * @throws KettleException.
	 */
	public KettleResult excuteJobOnce(KettleJobEntireDefine jobEntire) throws KettleException {
		KettleRecord record = kettleJobService.excuteJobOnce(jobEntire);
		KettleResult result = new KettleResult();
		result.setUuid(record.getUuid());
		result.setStatus(record.getStatus());
		result.setErrMsg(record.getErrMsg());
		return result;
	}

	/**
	 * 注册一个Job,只有调用Excute才开始执行
	 *
	 * @param jobEntire
	 * @return
	 * @throws KettleException.
	 */
	public KettleResult registeJob(KettleJobEntireDefine jobEntire) throws KettleException {
		KettleRecord record = kettleJobService.registeJob(jobEntire);
		KettleResult result = new KettleResult();
		result.setUuid(record.getUuid());
		result.setStatus(record.getStatus());
		result.setErrMsg(record.getErrMsg());
		return result;
	}

	/**
	 * 申请执行
	 * 
	 * @param uuid
	 * @return
	 * @throws KettleException
	 */
	public void excuteJob(String uuid) throws KettleException {
		kettleJobService.excuteJob(uuid);
	}

	/**
	 * 查询Job
	 * 
	 * @param uuid
	 * @return
	 * @throws KettleException
	 */
	public KettleResult queryJob(String uuid) throws KettleException {
		KettleRecord record = kettleJobService.queryJob(uuid);
		KettleResult result = new KettleResult();
		result.setUuid(record.getUuid());
		result.setStatus(record.getStatus());
		result.setErrMsg(record.getErrMsg());
		return result;
	}

	/**
	 * 删除Job
	 * 
	 * @param uuid
	 * @throws KettleException
	 */
	public void deleteJob(String uuid) throws KettleException {
		kettleJobService.deleteJob(uuid);
	}

	/**
	 * 强制删除Job,运行中的任务直接被停止并删除
	 * 
	 * @param uuid
	 * @throws KettleException
	 */
	public void deleteJobForce(String uuid) throws KettleException {
		kettleJobService.deleteJobImmediately(uuid);
	}

	public KettleJobService getKettleJobService() {
		return kettleJobService;
	}

	public void setKettleJobService(KettleJobService kettleJobService) {
		this.kettleJobService = kettleJobService;
	}
}
