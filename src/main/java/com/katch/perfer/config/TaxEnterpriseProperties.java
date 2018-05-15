package com.katch.perfer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:/config/taxes_enterprise.properties")
public class TaxEnterpriseProperties {
	@Value("${taxes.db.type}")
	private String dbType;

	@Value("${taxes.db.host}")
	private String dbHost;

	@Value("${taxes.db.port}")
	private String dbPort;

	@Value("${taxes.db.database}")
	private String dbDatabase;

	@Value("${taxes.db.user}")
	private String dbUser;

	@Value("${taxes.db.passwd}")
	private String dbPasswd;

	@Value("${taxes.enterprise.import.sql}")
	private String taxesEnterpriseImportSQL;

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getDbHost() {
		return dbHost;
	}

	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}

	public String getDbPort() {
		return dbPort;
	}

	public void setDbPort(String dbPort) {
		this.dbPort = dbPort;
	}

	public String getDbDatabase() {
		return dbDatabase;
	}

	public void setDbDatabase(String dbDatabase) {
		this.dbDatabase = dbDatabase;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPasswd() {
		return dbPasswd;
	}

	public void setDbPasswd(String dbPasswd) {
		this.dbPasswd = dbPasswd;
	}

	public String getTaxesEnterpriseImportSQL() {
		return taxesEnterpriseImportSQL;
	}

	public void setTaxesEnterpriseImportSQL(String taxesEnterpriseImportSQL) {
		this.taxesEnterpriseImportSQL = taxesEnterpriseImportSQL;
	}
}
