package com.katch.perfer.kettle.record;

import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.katch.perfer.kettle.consist.KettleVariables;
import com.katch.perfer.kettle.model.KettleRecord;
import com.katch.perfer.kettle.repository.KettleRecordRepository;

@Component
@Scope("prototype")
public abstract class BaseRecordOperator implements IRecordOperator {

	/**
	 * 任务
	 */
	protected KettleRecord record;

	/**
	 * 数据库
	 */
	@Autowired
	protected KettleRecordRepository kettleRecordRepository;

	/**
	 * 构造器
	 */
	public BaseRecordOperator() {
		
	}

	@Override
	public KettleRecord getRecord() {
		return record;
	}

	/**
	 * @return
	 */
	public String getRecordStatus() {
		return record.getStatus();
	}

	@Override
	public synchronized boolean attachRecord(KettleRecord record) {
		if (record == null) {
			return false;
		}
		if (this.record != null) {
			return false;
		}
		this.record = record;
		return true;
	}

	@Override
	public KettleRecord detachRecord() {
		KettleRecord kettleRecord = record;
		record = null;
		return kettleRecord;
	}

	@Override
	public boolean isAttached() {
		return record != null;
	}

	/**
	 * 是否运行状态
	 * 
	 * @return
	 */
	public boolean isRunning() {
		if (!isAttached()) {
			return false;
		}
		return KettleVariables.RECORD_STATUS_RUNNING.equals(record.getStatus());
	}

	/**
	 * 是否完成中
	 * 
	 * @return
	 */
	public boolean isFinished() {
		if (!isAttached()) {
			return false;
		}
		return KettleVariables.RECORD_STATUS_FINISHED.equals(record.getStatus())
				|| KettleVariables.RECORD_STATUS_ERROR.equals(record.getStatus());
	}

	/**
	 * 处理任务
	 * 
	 * @param record
	 * @throws KettleException
	 */
	public void dealRecord() throws KettleException {
		if (record.isApply()) {
			dealApply();
		} else if (record.isRegiste()) {
			dealRegiste();
		} else if (record.isRunning()) {
			dealRunning();
		} else if (record.isFinished()) {
			dealFinished();
		} else {
			dealError();
		}
	}
}
