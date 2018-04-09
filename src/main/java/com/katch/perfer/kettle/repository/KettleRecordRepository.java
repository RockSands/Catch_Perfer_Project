package com.katch.perfer.kettle.repository;

import java.util.Date;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.katch.perfer.kettle.model.KettleRecord;
import com.katch.perfer.kettle.model.KettleRecordRelation;
import com.katch.perfer.mybatis.mapper.KettleRecordMapper;

@Component
public class KettleRecordRepository {
	@Autowired
	private KettleRecordMapper kettleRecordMapper;

	/**
	 * 获取一个
	 * 
	 * @param uuid
	 * @return
	 * @throws KettleException
	 */
	@Transactional(readOnly = true)
	public KettleRecord queryRecord(String uuid) {
		return kettleRecordMapper.queryRecord(uuid);
	}

	/**
	 * 补充依赖
	 * 
	 * @param record
	 * @return
	 */
	@Transactional(readOnly = true)
	public KettleRecord queryRecordRelations(KettleRecord record) {
		List<KettleRecordRelation> kettleRecordRelations = kettleRecordMapper.queryRecordRelations(record.getJobid());
		record.getRelations().clear();
		record.getRelations().addAll(kettleRecordRelations);
		return record;
	}

	/**
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<KettleRecord> allWaitingRecords() {
		return kettleRecordMapper.allWaitingRecords();
	}

	/**
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<KettleRecord> allStopRecords() {
		return kettleRecordMapper.allStopRecords();
	}

	/**
	 * 保存
	 * 
	 * @param record
	 */
	@Transactional()
	public void insertRecord(KettleRecord record) {
		Date now = new Date();
		record.setCreateTime(now);
		record.setUpdateTime(now);
		kettleRecordMapper.insertRecord(record);
		kettleRecordMapper.insertRecordRelations(record.getRelations());
	}

	/**
	 * 更新非状态
	 * 
	 * @param record
	 */
	@Transactional()
	public void updateRecord(KettleRecord record) {
		record.setUpdateTime(new Date());
		kettleRecordMapper.updateRecord(record);
	}

	/**
	 * 删除Record
	 * 
	 * @param record
	 */
	@Transactional()
	public void deleteRecord(String uuid) {
		kettleRecordMapper.deleteRecord(uuid);
		kettleRecordMapper.deleteRecordRelations(uuid);
	}
}
