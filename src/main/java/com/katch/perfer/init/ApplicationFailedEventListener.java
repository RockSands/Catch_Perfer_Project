package com.katch.perfer.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

/**
 * 
 */
public class ApplicationFailedEventListener implements ApplicationListener<ApplicationFailedEvent> {

	private Logger logger = LoggerFactory.getLogger(ApplicationFailedEventListener.class);

	@Override
	public void onApplicationEvent(ApplicationFailedEvent event) {
		Throwable throwable = event.getException();
		handleThrowable(throwable);
	}

	/* 处理异常 */
	private void handleThrowable(Throwable throwable) {
		logger.error("Catch_Perfer_Project项目启动发生异常!", throwable);
	}

}
