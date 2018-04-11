package com.katch.perfer.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

/**
 * spring boot 启动监听类
 */
public class ApplicationStartedEventListener implements ApplicationListener<ApplicationStartingEvent> {

	private Logger logger = LoggerFactory.getLogger(ApplicationStartedEventListener.class);

	@Override
	public void onApplicationEvent(ApplicationStartingEvent event) {
		logger.error("Catch_Perfer_Project项目Boot启动成功!");
	}

}