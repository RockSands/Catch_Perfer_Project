package com.katch.perfer.kettle.metas.builder;

import org.apache.commons.beanutils.BeanUtils;

import com.katch.perfer.kettle.metas.KettleSQLSMeta;

public class KettleSelectSQLMetaBuilder {
    /**
     * 数据库类别
     */
    private String type;

    /**
     * 数据库IP
     */
    private String host;

    /**
     * 端口
     */
    private String port;

    /**
     * DataBase
     */
    private String database;

    /**
     * 数据库登入用户
     */
    private String user;

    /**
     * 数据库登入密码
     */
    private String passwd;

    /**
     * 执行的SQL
     */
    private String sql;

    /**
     * 对应的列
     */
    private String[] conlumns;

    /**
     * 构造器
     */
    private KettleSelectSQLMetaBuilder() {

    }

    /**
     * @return
     */
    public static KettleSelectSQLMetaBuilder newBuilder() {
	return new KettleSelectSQLMetaBuilder();
    }

    public String getType() {
	return type;
    }

    public KettleSelectSQLMetaBuilder dbtype(String type) {
	this.type = type;
	return this;
    }

    public String getHost() {
	return host;
    }

    public KettleSelectSQLMetaBuilder dbHost(String host) {
	this.host = host;
	return this;
    }

    public String getPort() {
	return port;
    }

    public KettleSelectSQLMetaBuilder dbPort(String port) {
	this.port = port;
	return this;
    }

    public String getDatabase() {
	return database;
    }

    public KettleSelectSQLMetaBuilder dbDatabase(String database) {
	this.database = database;
	return this;
    }

    public String getUser() {
	return user;
    }

    public KettleSelectSQLMetaBuilder dbUser(String user) {
	this.user = user;
	return this;
    }

    public String getPasswd() {
	return passwd;
    }

    public KettleSelectSQLMetaBuilder dbPasswd(String passwd) {
	this.passwd = passwd;
	return this;
    }

    public String getSql() {
	return sql;
    }

    public KettleSelectSQLMetaBuilder excuteSql(String sql) {
	this.sql = sql;
	return this;
    }

    public String[] getConlumns() {
	return conlumns;
    }

    public KettleSelectSQLMetaBuilder setConlumns(String[] conlumns) {
	this.conlumns = conlumns;
	return this;
    }

    public KettleSQLSMeta build() {
	KettleSQLSMeta sqlsMeta = new KettleSQLSMeta();
	try {
	    BeanUtils.copyProperties(sqlsMeta, this);
	} catch (Exception e) {
	    throw new RuntimeException("KettleTableMetaBuilder使用BeanUtils失败!", e);
	}
	return sqlsMeta;
    }
}
