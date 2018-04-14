package com.katch.perfer.config.mybatis;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class DataSourceAspect {
	@Pointcut("execution(* com.mybatis.mapper..*.*(..))")
	public void dataSourcePointcut() {
	}

	@Before("dataSourcePointcut()")
	public void doBefore(JoinPoint point) {
		MethodSignature methodSignature = (MethodSignature) point.getSignature();
		Method method = methodSignature.getMethod();
		DataSourceTypeAnno typeAnno = method.getAnnotation(DataSourceTypeAnno.class);
		if (typeAnno != null && typeAnno.value() == DataSourceEnum.secondary) {
			DataSourceContextHolder.setDataSourceType(DataSourceEnum.secondary);
		} else {
			DataSourceContextHolder.setDataSourceType(DataSourceEnum.primary);
		}
	}

	@After("dataSourcePointcut()")
	public void doAfter(JoinPoint point) {
		DataSourceContextHolder.resetDataSourceType();
	}
}
