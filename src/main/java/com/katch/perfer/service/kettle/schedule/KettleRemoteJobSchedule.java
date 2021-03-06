package com.katch.perfer.service.kettle.schedule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.pentaho.di.core.exception.KettleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.katch.perfer.kettle.remote.KettleRemoteClient;
import com.katch.perfer.kettle.remote.KettleRemotePool;
import com.katch.perfer.kettle.service.KettleJobService;
import com.katch.perfer.mybatis.model.KettleRecord;
import com.katch.perfer.utils.SpringContextUtils;

@Service
public class KettleRemoteJobSchedule extends KettleJobService {
	private static Logger logger = LoggerFactory.getLogger(KettleRemoteJobSchedule.class);

	@Autowired
	private KettleRemotePool kettleRemotePool;

	private ExecutorService fixedThreadPool;

	private final Map<String, KettleRemoteJobDaemon> daemons = new ConcurrentHashMap<String, KettleRemoteJobDaemon>();

	@Scheduled(initialDelay = 5000, fixedRate = 10000)
	public void schedule() {
		fixedThreadPool = Executors.newFixedThreadPool(kettleRemotePool.getRemoteclients().size());
		for (KettleRemoteClient client : kettleRemotePool.getRemoteclients()) {
			if (!daemons.containsKey(client.getHostName())) {
				daemons.put(client.getHostName(), SpringContextUtils.getBean(KettleRemoteJobDaemon.class, client));
			}
			fixedThreadPool.execute(daemons.get(client.getHostName()));
		}
		try {
			fixedThreadPool.shutdown();
			fixedThreadPool.awaitTermination(5, TimeUnit.MINUTES);
		} catch (Exception e) {
			logger.error("线程池执行超时!", e);
		}
	}

	@Override
	protected void jobMustDie(KettleRecord record) throws KettleException {
		KettleRemoteClient client = kettleRemotePool.getRemoteclient(record.getHostname());
		if (client == null) {
			return;
		}
		client.remoteStopJobNE(record);
		client.remoteRemoveJobNE(record);
	}

	@Override
	protected void checkStatus() throws KettleException {
		if (!kettleRemotePool.isRunning()) {
			throw new KettleException("Kettle没有可用的远端节点!");
		}
	}
}
