package com.katch.perfer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:/config/consumerExport.properties")
public class ConsumerExportDBProperties {
	/**
	 * 执行的sql
	 */
	@Value("${consumer.db.selectsql}")
	private String sql;

	/**
	 * 数据库类别
	 */
	@Value("${consumer.db.type}")
	private String type;

	/**
	 * 数据库IP
	 */
	@Value("${consumer.db.host}")
	private String host;

	/**
	 * 端口
	 */
	@Value("${consumer.db.port}")
	private String port;

	/**
	 * DataBase
	 */
	@Value("${consumer.db.database}")
	private String database;

	/**
	 * 数据库登入用户
	 */
	@Value("${consumer.db.user}")
	private String user;

	/**
	 * 数据库登入密码
	 */
	@Value("${consumer.db.passwd}")
	private String passwd;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
}
