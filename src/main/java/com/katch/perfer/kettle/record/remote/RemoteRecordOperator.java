package com.katch.perfer.kettle.record.remote;

import org.pentaho.di.core.exception.KettleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.katch.perfer.kettle.consist.KettleVariables;
import com.katch.perfer.kettle.record.BaseRecordOperator;
import com.katch.perfer.kettle.repository.KettleRecordRepository;
import com.katch.perfer.mybatis.model.KettleRecord;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RemoteRecordOperator extends BaseRecordOperator {

	/**
	 * 日志
	 */
	private static Logger logger = LoggerFactory.getLogger(RemoteRecordOperator.class);

	/**
	 * 远端
	 */
	private final KettleRemoteClient remoteClient;

	/**
	 * 远端
	 */
	@Autowired
	private KettleRecordRepository kettleRecordRepository;

	/**
	 * @param remoteClient
	 */
	public RemoteRecordOperator(KettleRemoteClient remoteClient) {
		this.remoteClient = remoteClient;
	}

	@Override
	public boolean attachRecord(KettleRecord record) {
		if (remoteClient.isRunning() && super.attachRecord(record)) {
			return true;
		}
		return false;
	}

	/**
	 * 强制加载,无视远端状态
	 * 
	 * @param record
	 * @return
	 */
	public boolean attachRecordForce(KettleRecord record) {
		return super.attachRecord(record);
	}

	/**
	 * @throws KettleException
	 */
	@Override
	public void dealRecord() throws KettleException {
		if (isAttached()) {
			if (remoteClient.isRunning()) {
				super.dealRecord();
			} else if (!record.isApply()) {
				dealErrorRemoteRecord();
			}
		}
	}

	/**
	 * @throws KettleException
	 */
	private void updateRecord() throws KettleException {
		try {
			kettleRecordRepository.updateRecord(record);
		} catch (Exception ex) {
			throw new KettleException("remote[" + remoteClient.getHostName() + "]持久化更新Job[" + record.getUuid() + "]失败!",
					ex);
		}
	}

	@Override
	public void dealApply() throws KettleException {
		String runID = null;
		try {
			runID = remoteClient.remoteSendJob(record);
			record.setRunID(runID);
			record.setStatus(KettleVariables.RECORD_STATUS_RUNNING);
		} catch (Exception ex) {
			record.setStatus(KettleVariables.RECORD_STATUS_ERROR);
			record.setErrMsg("remote[" + remoteClient.getHostName() + "]发送Job[" + record.getUuid() + "]发生异常");
			logger.error("remote[" + remoteClient.getHostName() + "]发送Job[" + record.getUuid() + "]发生异常!", ex);
		}
		record.setHostname(remoteClient.getHostName());
		updateRecord();
	}

	@Override
	public void dealRegiste() throws KettleException {
		throw new KettleException("Record[" + record.getUuid() + "] 状态为[Registe],无法远程执行!");
	}

	@Override
	public void dealError() throws KettleException {
		try {
			kettleRecordRepository.updateRecord(record);
		} catch (Exception e) {
			throw new KettleException("Record[" + record.getUuid() + "] 状态为[Error],数据库发生异常!", e);
		}
	}

	@Override
	public void dealFinished() throws KettleException {
		try {
			kettleRecordRepository.updateRecord(record);
			remoteClient.remoteRemoveJobNE(record);
		} catch (Exception e) {
			throw new KettleException("Record[" + record.getUuid() + "] 状态为[Finished],数据库发生异常!", e);
		}
	}

	@Override
	public void dealRunning() throws KettleException {
		try {
			remoteClient.remoteJobStatus(record);
			checkJobRunOvertime();
		} catch (Exception e) {
			record.setStatus(KettleVariables.RECORD_STATUS_ERROR);
			record.setErrMsg("Record[" + record.getUuid() + "] 在Remote[" + remoteClient.getHostName() + "]中同步状态发生异常!");
			logger.error("Record[" + record.getUuid() + "] 在Remote[" + remoteClient.getHostName() + "]中同步状态发生异常!", e);
		} finally {
			if (!record.isRunning()) {
				// 重新处理
				super.dealRecord();
			}
		}
	}

	/**
	 * 处理远端无法连接的记录
	 * 
	 * @throws KettleException
	 */
	private void dealErrorRemoteRecord() throws KettleException {
		record.setStatus(KettleVariables.RECORD_STATUS_ERROR);
		record.setErrMsg("Remote[" + remoteClient.getHostName() + "]状态异常,Record[" + record.getUuid() + "]");
		updateRecord();
	}

	/**
	 * 是否超时
	 * 
	 */
	private void checkJobRunOvertime() {
		if (record.isRunning() && KettleVariables.KETTLE_RECORD_RUNNING_TIMEOUT != null
				&& KettleVariables.KETTLE_RECORD_RUNNING_TIMEOUT > 0) {
			if ((System.currentTimeMillis() - record.getUpdateTime().getTime()) / 1000
					/ 60 > KettleVariables.KETTLE_RECORD_RUNNING_TIMEOUT) {
				remoteClient.remoteStopJobNE(record);
				record.setStatus(KettleVariables.RECORD_STATUS_ERROR);
				record.setErrMsg("Record[" + record.getUuid() + "]执行超时,异常状态!");
			}
		}
	}

	/**
	 * 返回Client
	 * 
	 * @return
	 */
	public KettleRemoteClient getRemoteClient() {
		return remoteClient;
	}
}
