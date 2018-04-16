package com.katch.perfer.control;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 使用@ControllerAdvice + @ExceptionHandler 完成对每个@Controller的异常处理
 * 注意的是该Handler只能处理Exception以及其子类.无法处理Throwable等高级别异常
 * 
 * @author Administrator
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 返回Json
	 * @param req
	 * @param e
	 * @return
	 * @throws Exception
	 */
	@ExceptionHandler(value = MyException.class)
	@ResponseBody
	public ErrorInfo<String> jsonErrorHandler(HttpServletRequest req, MyException e) throws Exception {
		ErrorInfo<String> r = new ErrorInfo<>();
		r.setMessage(e.getMessage());
		r.setCode(ErrorInfo.ERROR);
		r.setData("Some Data");
		r.setUrl(req.getRequestURL().toString());
		return r;
	}
	
	/**
	 * 处理Exception以及其子类
	 * 
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	@ResponseBody
	String handleException(Exception e) {
		return "发生异常!";
	}
}
