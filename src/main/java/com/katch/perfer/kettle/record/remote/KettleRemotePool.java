package com.katch.perfer.kettle.record.remote;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.katch.perfer.kettle.consist.KettleVariables;
import com.katch.perfer.kettle.repository.KettleRepoRepository;
import com.katch.perfer.utils.SpringContextUtils;

/**
 * Kettle远程池,仅维护远端的状态
 * 
 * @author Administrator
 *
 */
@Service
@Lazy
public class KettleRemotePool {
	/**
	 * 日志
	 */
	private static Logger logger = LoggerFactory.getLogger(KettleRemotePool.class);

	/**
	 * 远程连接
	 */
	private final Map<String, KettleRemoteClient> remoteclients = new HashMap<String, KettleRemoteClient>();

	/**
	 * 设备名称
	 */
	private final List<String> hostNames = new LinkedList<String>();

	/**
	 * 池状态
	 */
	private String poolStatus = KettleVariables.RECORD_STATUS_RUNNING;

	/**
	 * Kettle资源库
	 */
	@Autowired
	private KettleRepoRepository kettleRepoRepository;

	@PostConstruct
	public void init() throws KettleException {
		for (SlaveServer server : kettleRepoRepository.getSlaveServers()) {
			server.getLogChannel().setLogLevel(LogLevel.ERROR);
			addRemoteClient(SpringContextUtils.getBean(KettleRemoteClient.class, server));
			hostNames.add(server.getHostname());
		}
		logger.info("Kettle远程池已经加载Client" + remoteclients.keySet());
	}

	/**
	 * 添加Kettle远端
	 * 
	 * @param remoteClient
	 */
	private void addRemoteClient(KettleRemoteClient remoteClient) {
		if (remoteclients.containsKey(remoteClient.getHostName())) {
			logger.error("Kettle的远程池添加Client[" + remoteClient.getHostName() + "]失败,该主机已存在!");
		} else {
			remoteclients.put(remoteClient.getHostName(), remoteClient);
			logger.info("Kettle的远程池添加Client[" + remoteClient.getHostName() + "]成功!");
		}
	}

	/**
	 * 获取所有Client
	 * 
	 * @return
	 */
	public Collection<KettleRemoteClient> getRemoteclients() {
		return remoteclients.values();
	}

	/**
	 * 查看状态
	 * 
	 * @return
	 */
	public String getPoolStatus() {
		return poolStatus;
	}

	@Scheduled(initialDelay = 10000, fixedDelay = 30000)
	public void AutoRemoteDeamon() {
		Collection<KettleRemoteClient> clients = remoteclients.values();
		for (KettleRemoteClient client : clients) {
			client.refreshRemoteStatus();
		}
		String status = KettleVariables.REMOTE_STATUS_ERROR;
		for (KettleRemoteClient client : clients) {
			if (client.isRunning()) {
				status = KettleVariables.REMOTE_STATUS_RUNNING;
			} else {
				logger.error("Remote[" + client.getHostName() + "]异常状态!");
			}
		}
		poolStatus = status;
	}

	public KettleRepoRepository getKettleRepoRepository() {
		return kettleRepoRepository;
	}

	public void setKettleRepoRepository(KettleRepoRepository kettleRepoRepository) {
		this.kettleRepoRepository = kettleRepoRepository;
	}
}
