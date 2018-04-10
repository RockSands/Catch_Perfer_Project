package com.katch.perfer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@MapperScan("com.katch.perfer.mybatis.mapper")
public class ExcuteMain {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(ExcuteMain.class, args);
	}
}
