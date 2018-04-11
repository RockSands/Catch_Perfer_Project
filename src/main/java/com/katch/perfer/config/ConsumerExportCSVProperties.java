package com.katch.perfer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:/config/consumerExport.properties")
public class ConsumerExportCSVProperties {

	/**
	 * 行数据分隔符
	 */
	@Value("${consumer.export.separator}")
	private String separator = ",";

	/**
	 * 文件名称(包含完整路径)
	 */
	@Value("${consumer.export.fileName}")
	private String fileName = "";

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
