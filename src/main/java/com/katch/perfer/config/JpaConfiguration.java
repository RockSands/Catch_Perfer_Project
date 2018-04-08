package com.katch.perfer.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA的配置文件
 * 
 * @EnableJpaRepositories
 * @author Administrator
 *
 */
@Configuration
@EnableJpaRepositories("com.katch.perfer.domain")
// 开启事物
@EnableTransactionManagement 
public class JpaConfiguration {
    
    /**
     * 如果显示声明,则Springboot不会自动加载默认配置的事物
     * @param dataSource
     * @return
     */
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
	JpaTransactionManager transaction= new JpaTransactionManager();
	transaction.setDataSource(dataSource);
        return transaction;
    }
}
