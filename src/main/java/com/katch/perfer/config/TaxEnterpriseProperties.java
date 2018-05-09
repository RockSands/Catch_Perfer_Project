package com.katch.perfer.config;

import org.springframework.beans.factory.annotation.Value;

public class TaxEnterpriseProperties {
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
}
