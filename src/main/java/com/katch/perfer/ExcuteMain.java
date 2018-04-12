package com.katch.perfer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.katch.perfer.init.ApplicationEnvironmentPreparedEventListener;
import com.katch.perfer.init.ApplicationFailedEventListener;
import com.katch.perfer.init.ApplicationPreparedEventListener;
import com.katch.perfer.init.ApplicationStartedEventListener;
import com.katch.perfer.service.ConsumerAutoDealService;

@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
@EnableTransactionManagement
@MapperScan("com.katch.perfer.mybatis.mapper")
public class ExcuteMain {

	public static void main(String[] args) throws Exception {
		SpringApplication app = new SpringApplication(ExcuteMain.class);
		app.addListeners(new ApplicationEnvironmentPreparedEventListener());
		app.addListeners(new ApplicationFailedEventListener());
		app.addListeners(new ApplicationPreparedEventListener());
		app.addListeners(new ApplicationStartedEventListener());
		ConfigurableApplicationContext context = app.run(args);
		// ConsumerAutoDealService consumerAutoDealService =
		// context.getBean(ConsumerAutoDealService.class);
		// consumerAutoDealService.excute();
		// MahoutConsumerService mahoutConsumerService =
		// (MahoutConsumerService)context.getBean("mahoutConsumerService");
		// System.out.println("===>" + mahoutConsumerService);
		// KettleNorthService KettleNorthService =
		// context.getBean(KettleNorthService.class);
		// String uuid = consumerExportService.doExport();
		// while (true) {
		// KettleResult result = KettleNorthService.queryJob(uuid);
		// System.out.println("===>" + result.getStatus());
		// Thread.sleep(10000l);
		// }
	}
}
