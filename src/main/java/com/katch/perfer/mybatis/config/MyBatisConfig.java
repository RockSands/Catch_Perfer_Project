package com.katch.perfer.mybatis.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * springboot集成mybatis的基本入口 1）创建数据源(如果采用的是默认的tomcat-jdbc数据源，则不需要)
 * 2）创建SqlSessionFactory 3）配置事务管理器，除非需要使用事务，否则不用配置
 *
 */
@Configuration
@MapperScan("com.katch.perfer.mybatis.mapper")
public class MyBatisConfig {
    /**
     * @return
     * @throws Exception
     * @Primary 该注解表示在同一个接口有多个实现类可以注入的时候，默认选择哪一个，而不是让@autowire注解报错
     */
    @Primary
    @Bean("primaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource primaryDataSource() throws Exception {
	DataSourceBuilder builder = DataSourceBuilder.create();
	return builder.build();
    }

    @Bean("secondaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource secondaryDataSource() throws Exception {
	DataSourceBuilder builder = DataSourceBuilder.create();
	return builder.build();
    }

    /**
     * @Qualifier 根据名称进行注入，通常是在具有相同的多个类型的实例的一个注入（例如有多个DataSource类型的实例）
     */
    @Bean("dynamicDataSource")
    public DynamicDataSource dynamicDataSource(@Qualifier("primaryDataSource") DataSource primaryDataSource,
	    @Qualifier("secondaryDataSource") DataSource secondaryDataSource) {
	Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
	targetDataSources.put(DataSourceEnum.primary, primaryDataSource);
	targetDataSources.put(DataSourceEnum.secondary, secondaryDataSource);

	DynamicDataSource dataSource = new DynamicDataSource();
	dataSource.setTargetDataSources(targetDataSources);// 该方法是AbstractRoutingDataSource的方法
	dataSource.setDefaultTargetDataSource(primaryDataSource);// 默认的datasource设置为myTestDbDataSource

	return dataSource;
    }

    /**
     * 根据数据源创建SqlSessionFactory
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dynamicDataSource") DynamicDataSource dynamicDataSource)
	    throws Exception {
	SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
	factoryBean.setDataSource(dynamicDataSource);// 指定数据源(这个必须有，否则报错)
	factoryBean.setTypeAliasesPackage("com.katch.perfer.mybatis.model");// 指定基包
	factoryBean
		.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/*.xml"));
	return factoryBean.getObject();
    }

    /**
     * 配置事务管理器
     */
    @Bean
    public DataSourceTransactionManager transactionManager(DynamicDataSource dataSource) throws Exception {
	return new DataSourceTransactionManager(dataSource);
    }
}
