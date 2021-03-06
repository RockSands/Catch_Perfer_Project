package com.katch.perfer.kettle.config;

import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.repository.filerep.KettleFileRepository;
import org.pentaho.di.repository.filerep.KettleFileRepositoryMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.katch.perfer.kettle.record.KettleRecordPool;
import com.katch.perfer.kettle.remote.KettleRemotePool;
import com.katch.perfer.kettle.repository.KettleRepoRepository;

@Configuration
@EnableAutoConfiguration
public class KettleConfig {
	/**
	 * 日志
	 */
	private static Logger logger = LoggerFactory.getLogger(KettleConfig.class);

	@Autowired
	private KettleRepoProperties repoProperties;

	@Bean
	public Repository kettleRepo() throws KettleException {
		logger.info("Kettle的客户端正在初始化....");
		KettleFileRepository repository = new KettleFileRepository();
		RepositoryMeta repositoryMeta = new KettleFileRepositoryMeta(repoProperties.getId(), repoProperties.getName(),
				repoProperties.getDescription(), repoProperties.getBaseDirectory());
		repository.init(repositoryMeta);
		repository.connect(repoProperties.getUser(), repoProperties.getPasswd());
		StringBuffer slaves = new StringBuffer("");
		for (SlaveServer slaveServer : repository.getSlaveServers()) {
			if (slaves.length() > 0) {
				slaves.append(",");
			}
			slaves.append(slaveServer.getName());
		}
		logger.info("Kettle初始化发现注册的服务端[" + slaves + "]!");
		return repository;
	}

	@Bean
	public KettleRemotePool kettleRemotePool(KettleRepoRepository kettleRepoRepository) throws KettleException {
		return new KettleRemotePool(kettleRepoRepository);
	}

	@Bean
	public KettleRecordPool kettleRecordPool() throws KettleException {
		return new KettleRecordPool();
	}
}
