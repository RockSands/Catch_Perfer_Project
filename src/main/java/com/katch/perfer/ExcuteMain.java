package com.katch.perfer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.katch.perfer.kettle.metas.KettleSelectSQLMeta;
import com.katch.perfer.kettle.metas.builder.ConsumerExportCSVBuilder;
import com.katch.perfer.kettle.service.KettleNorthService;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@MapperScan("com.katch.perfer.mybatis.mapper")
public class ExcuteMain {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(ExcuteMain.class, args);
		KettleNorthService kettleNorthService= context.getBean(KettleNorthService.class);
		KettleSelectSQLMeta consumer = new KettleSelectSQLMeta();
		consumer.setType("MySQL");
		consumer.setHost("192.168.80.138");
		consumer.setPort("3306");
		consumer.setDatabase("employees");
		consumer.setUser("root");
		consumer.setPasswd("123456");
		consumer.setSql("SELECT * FROM employees");
		kettleNorthService.excuteJobOnce(ConsumerExportCSVBuilder.newBuilder().consumer(consumer).createJob());
	}
}
