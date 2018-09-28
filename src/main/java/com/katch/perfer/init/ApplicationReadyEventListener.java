package com.katch.perfer.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/**
 * spring boot 启动监听类
 */
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

	private Logger logger = LoggerFactory.getLogger(ApplicationReadyEventListener.class);

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		logger.info("Catch_Perfer_Project项目Boot启动成功!");
	}

}