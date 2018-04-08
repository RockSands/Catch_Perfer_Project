package com.katch.perfer;

import org.pentaho.di.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExcuteMain {
	
	@Autowired
	@Qualifier("KettleRepo")
	private static Repository repository;
	
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(ExcuteMain.class, args);
	}
}
