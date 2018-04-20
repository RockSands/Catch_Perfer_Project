package com.katch.perfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.katch.perfer.init.ApplicationEnvironmentPreparedEventListener;
import com.katch.perfer.init.ApplicationFailedEventListener;
import com.katch.perfer.init.ApplicationPreparedEventListener;
import com.katch.perfer.init.ApplicationStartedEventListener;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableScheduling
@EnableTransactionManagement
public class Application extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication app = new SpringApplication(Application.class);
		app.addListeners(new ApplicationEnvironmentPreparedEventListener());
		app.addListeners(new ApplicationFailedEventListener());
		app.addListeners(new ApplicationPreparedEventListener());
		app.addListeners(new ApplicationStartedEventListener());
		app.run(args);
	}
}
