package com.katch.perfer.init;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;

/**
 * 
 */
public class ApplicationPreparedEventListener implements ApplicationListener<ApplicationPreparedEvent> {

	@Override
	public void onApplicationEvent(ApplicationPreparedEvent event) {
		try {
			System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
					"com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
			KettleEnvironment.init();
			//ConfigurableApplicationContext context = event.getApplicationContext();
		} catch (KettleException e) {
			throw new RuntimeException("KettleEnvironment初始化失败!");
		}
	}
}
