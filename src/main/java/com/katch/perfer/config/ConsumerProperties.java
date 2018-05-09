package com.katch.perfer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:/config/consumer.properties")
public class ConsumerProperties {
	@Value("${consumer.db.type}")
	private String sourceType;

	@Value("${consumer.db.host}")
	private String sourceHost;

	@Value("${consumer.db.port}")
	private String sourcePort;

	@Value("${consumer.db.database}")
	private String sourceDatabase;
	
	@Value("${consumer.db.user}")
	private String sourceUser;

	@Value("${consumer.db.passwd}")
	private String sourcePasswd;

	@Value("${consumer.score.export.sql}")
	private String sourceSql;
	
	@Value("${consumer.score.export.target.file.path}")
	private String targetPath;
	
	@Value("${consumer.score.export.target.file.separator}")
	private String targetSeparator;
	
	@Value("${consumer.score.export.target.file.extension}")
	private String targetExtension;
	
	@Value("${consumer.tax.enterprise.sql}")
	private String taxEnterpriseSql;

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getSourceHost() {
		return sourceHost;
	}

	public void setSourceHost(String sourceHost) {
		this.sourceHost = sourceHost;
	}

	public String getSourcePort() {
		return sourcePort;
	}

	public void setSourcePort(String sourcePort) {
		this.sourcePort = sourcePort;
	}

	public String getSourceDatabase() {
		return sourceDatabase;
	}

	public void setSourceDatabase(String sourceDatabase) {
		this.sourceDatabase = sourceDatabase;
	}

	public String getSourcePasswd() {
		return sourcePasswd;
	}

	public void setSourcePasswd(String sourcePasswd) {
		this.sourcePasswd = sourcePasswd;
	}

	public String getSourceSql() {
		return sourceSql;
	}

	public void setSourceSql(String sourceSql) {
		this.sourceSql = sourceSql;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	public String getTargetSeparator() {
		return targetSeparator;
	}

	public void setTargetSeparator(String targetSeparator) {
		this.targetSeparator = targetSeparator;
	}

	public String getTargetExtension() {
		return targetExtension;
	}

	public void setTargetExtension(String targetExtension) {
		this.targetExtension = targetExtension;
	}

	public String getSourceUser() {
		return sourceUser;
	}

	public void setSourceUser(String sourceUser) {
		this.sourceUser = sourceUser;
	}

	public String getTaxEnterpriseSql() {
		return taxEnterpriseSql;
	}

	public void setTaxEnterpriseSql(String taxEnterpriseSql) {
		this.taxEnterpriseSql = taxEnterpriseSql;
	}
}
