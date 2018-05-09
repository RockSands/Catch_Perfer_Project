package com.katch.perfer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:/config/recommend.properties")
public class RecommendProperties {
	
	@Value("${recommend.insert.target.db.type}")
	private String targetType;

	@Value("${recommend.insert.target.db.host}")
	private String targetHost;

	@Value("${recommend.insert.target.db.port}")
	private String targetPort;

	@Value("${recommend.insert.target.db.database}")
	private String targetDatabase;
	
	@Value("${recommend.insert.target.db.user}")
	private String targetUser;

	@Value("${recommend.insert.target.db.passwd}")
	private String targetPasswd;
	
	@Value("${recommend.save.item.fileName}")
	private String itemRecommendFileName;
	
	@Value("${recommend.save.user.fileName}")
	private String userRecommendFileName;
	
	@Value("${recommend.tax.enterprise.select.sql}")
	private String taxEnterpriseSelectSql;
	
	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getTargetHost() {
		return targetHost;
	}

	public void setTargetHost(String targetHost) {
		this.targetHost = targetHost;
	}

	public String getTargetPort() {
		return targetPort;
	}

	public void setTargetPort(String targetPort) {
		this.targetPort = targetPort;
	}

	public String getTargetDatabase() {
		return targetDatabase;
	}

	public void setTargetDatabase(String targetDatabase) {
		this.targetDatabase = targetDatabase;
	}

	public String getTargetUser() {
		return targetUser;
	}

	public void setTargetUser(String targetUser) {
		this.targetUser = targetUser;
	}

	public String getTargetPasswd() {
		return targetPasswd;
	}

	public void setTargetPasswd(String targetPasswd) {
		this.targetPasswd = targetPasswd;
	}

	public String getItemRecommendFileName() {
		return itemRecommendFileName;
	}

	public void setItemRecommendFileName(String itemRecommendFileName) {
		this.itemRecommendFileName = itemRecommendFileName;
	}

	public String getUserRecommendFileName() {
		return userRecommendFileName;
	}

	public void setUserRecommendFileName(String userRecommendFileName) {
		this.userRecommendFileName = userRecommendFileName;
	}

	public String getTaxEnterpriseSelectSql() {
		return taxEnterpriseSelectSql;
	}

	public void setTaxEnterpriseSelectSql(String taxEnterpriseSelectSql) {
		this.taxEnterpriseSelectSql = taxEnterpriseSelectSql;
	}
}
