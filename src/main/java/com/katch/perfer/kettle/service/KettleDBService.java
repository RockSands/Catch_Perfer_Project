package com.katch.perfer.kettle.service;

import java.util.Date;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.katch.perfer.domain.kettle.KettleRecord;
import com.katch.perfer.domain.kettle.KettleRecordRelation;
import com.katch.perfer.domain.kettle.KettleRecordRelationRepository;
import com.katch.perfer.domain.kettle.KettleRecordRepository;

@Service
public class KettleDBService {
	@Autowired
	private KettleRecordRelationRepository kettleRecordRelationRepository;
	@Autowired
	private KettleRecordRepository kettleRecordRepository;

	/**
	 * 获取一个
	 * 
	 * @param uuid
	 * @return
	 * @throws KettleException
	 */
	@Transactional(readOnly = true)
	public KettleRecord queryRecord(String uuid) {
		return kettleRecordRepository.findOne(uuid);
	}

	/**
	 * 补充依赖
	 * 
	 * @param record
	 * @return
	 */
	@Transactional(readOnly = true)
	public KettleRecord queryRecordRelations(KettleRecord record) {
		List<KettleRecordRelation> kettleRecordRelations = kettleRecordRelationRepository
				.queryByMasterUUID(record.getJobid());
		record.getRelations().addAll(kettleRecordRelations);
		return record;
	}

	/**
	 * 保存
	 * 
	 * @param record
	 */
	@Transactional()
	public void SaveKettleRecord(KettleRecord record) {
		Date now = new Date();
		record.setCreateTime(now);
		record.setUpdateTime(now);
		kettleRecordRepository.save(record);
		kettleRecordRelationRepository.save(record.getRelations());
	}

	/**
	 * 更新非状态
	 * 
	 * @param record
	 */
	public void updateRecordNoStatus(KettleRecord record) {
		record.setUpdateTime(new Date());
		kettleRecordRepository.updateRecordNoStatus(record.getJobid(), record.getRunID(), record.getHostname(),
				record.getCronExpression(), record.getUpdateTime(), record.getUuid());
	}
	
	public void updateRecordStatus(KettleRecord record) {
		record.setUpdateTime(new Date());
		kettleRecordRepository.updateRecordStatus(record.getStatus(), record.getRunID(), record.getHostname(),
				record.getCronExpression(), record.getUpdateTime(), record.getUuid());
	}
}
