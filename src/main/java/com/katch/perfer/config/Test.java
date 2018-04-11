package com.katch.perfer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration 
@PropertySource("classpath:/config/consumerExport.properties")
@ConditionalOnProperty(value = "consumer.mahout.type", havingValue="user")
public class Test {
	/**
	 * 行数据分隔符
	 */
	@Value("${consumer.mahout.type}")
	private String separator = ",";

	public String getSeparator() {
	    return separator;
	}

	public void setSeparator(String separator) {
	    this.separator = separator;
	}
}
