package com.katch.perfer.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Administrator
 */
@ControllerAdvice
public class GlobalExceptionHandler {
	private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * 处理Exception以及其子类
	 * 
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	@ResponseBody
	String handleException(Exception e) {
		logger.error("RestFul接口发生错误!", e);
		return "发生异常!";
	}
}
