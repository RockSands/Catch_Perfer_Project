package com.katch.perfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.katch.perfer.init.ApplicationFailedEventListener;
import com.katch.perfer.init.ApplicationPreparedEventListener;
import com.katch.perfer.init.ApplicationReadyEventListener;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@EnableScheduling
@EnableTransactionManagement
public class Application {

	public static void main(String[] args) throws Exception {
		SpringApplication app = new SpringApplication(Application.class);
		app.addListeners(new ApplicationFailedEventListener());
		app.addListeners(new ApplicationPreparedEventListener());
		app.addListeners(new ApplicationReadyEventListener());
		app.run(args);
	}
}
