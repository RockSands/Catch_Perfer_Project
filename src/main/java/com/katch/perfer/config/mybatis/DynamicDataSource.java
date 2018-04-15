package com.katch.perfer.config.mybatis;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源
 * 
 * @author Administrator
 *
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    protected Object determineCurrentLookupKey() {
	return DataSourceContextHolder.getDataSourceType();
    }
}
