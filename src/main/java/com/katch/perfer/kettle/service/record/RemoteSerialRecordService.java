package com.katch.perfer.kettle.service.record;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.pentaho.di.core.exception.KettleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.katch.perfer.kettle.model.KettleRecord;
import com.katch.perfer.kettle.record.remote.KettleRemoteClient;
import com.katch.perfer.kettle.record.remote.KettleRemotePool;
import com.katch.perfer.kettle.record.remote.RemoteSerialRecordHandler;

/**
 * @author Administrator
 *
 */
@Service
public class RemoteSerialRecordService extends KettleJobService {

	/**
	 * 日志
	 */
	private static Logger logger = LoggerFactory.getLogger(RemoteSerialRecordService.class);

	@Autowired
	private KettleRemotePool kettleRemotePool;
	/**
	 * 远程池
	 */
	private List<RemoteSerialRecordHandler> handlers = new LinkedList<RemoteSerialRecordHandler>();;

	/**
	 * 线程池
	 */
	private ScheduledExecutorService threadPool;

	/**
	 * 运行中
	 */
	private Boolean hasStart;

	/**
	 * 构造器
	 */
	public RemoteSerialRecordService() {

	}

	@PostConstruct
	public void init() throws KettleException {
		threadPool = Executors.newScheduledThreadPool(kettleRemotePool.getRemoteclients().size());
		List<KettleRecord> oldRecords = super.getAllWaitingRecords();
		Map<String, List<KettleRecord>> oldRecordMap = new HashMap<String, List<KettleRecord>>();
		for (KettleRecord record : oldRecords) {
			if (record == null) {
				continue;
			}
			if (record.isApply()) {
				super.kettleRecordPool.addPrioritizeRecord(record);
			} else if (record.getHostname() != null) {
				if (!oldRecordMap.containsKey(record.getHostname())) {
					oldRecordMap.put(record.getHostname(), new LinkedList<KettleRecord>());
				}
				oldRecordMap.get(record.getHostname()).add(record);
			}
		}
		RemoteSerialRecordHandler recordHandler = null;
		for (KettleRemoteClient remoteClient : kettleRemotePool.getRemoteclients()) {
			recordHandler = new RemoteSerialRecordHandler(remoteClient, super.kettleRecordRepository,
					super.kettleRecordPool, oldRecordMap.get(remoteClient.getHostName()));
			handlers.add(recordHandler);
		}
		start();
	}

	/**
	 * 启动
	 * 
	 * @param remotePool
	 */
	private void start() {
		if (hasStart == null || !hasStart.booleanValue()) {
			for (int index = 0; index < handlers.size(); index++) {
				threadPool.scheduleAtFixedRate(handlers.get(index), 2 * index, 10, TimeUnit.SECONDS);
			}
			logger.info("Kettle远程任务关系系统的线程启动完成,个数:" + handlers.size());
		} else {
			logger.info("Kettle远程任务关系系统的线程已经启动,无法再次启动!");
		}
	}

	@Override
	protected void jobMustDie(KettleRecord record) throws KettleException {
		for (RemoteSerialRecordHandler handler : handlers) {
			if (handler.tryRemoveRecord(record)) {
				break;
			}
		}
		// 直接清除
		kettleRecordRepository.deleteRecord(record.getUuid());
	}
}
