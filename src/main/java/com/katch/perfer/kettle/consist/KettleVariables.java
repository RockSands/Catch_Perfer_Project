package com.katch.perfer.kettle.consist;

import org.pentaho.di.core.util.EnvUtil;

/**
 * Kettle的常量
 * 
 * @author Administrator
 *
 */
public class KettleVariables {

	/**
	 * Record任务最大保持数量
	 */
	public static Integer KETTLE_RECORD_POOL_MAX = NVLInt("KETTLE_RECORD_POOL_MAX",
			KettleEnvDefault.KETTLE_RECORD_POOL_MAX);

	/**
	 * Record任务保留最长时间
	 */
	public static Integer KETTLE_RECORD_PERSIST_MAX_HOUR = NVLInt("KETTLE_RECORD_PERSIST_MAX_HOUR",
			KettleEnvDefault.KETTLE_RECORD_PERSIST_MAX_HOUR);

	/**
	 * Record任务个数
	 */
	public static int KETTLE_RECORD_MAX_PER_REMOTE = NVLInt("KETTLE_RECORD_MAX_PER_REMOTE",
			KettleEnvDefault.KETTLE_RECORD_MAX_PER_REMOTE);

	/**
	 * Record任务运行超时时间
	 */
	public static Integer KETTLE_RECORD_RUNNING_TIMEOUT = NVLInt("KETTLE_RECORD_RUNNING_TIMEOUT",
			KettleEnvDefault.KETTLE_RECORD_RUNNING_TIMEOUT);

	/**
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static String NVLStr(String key, String defaultVal) {
		return EnvUtil.getSystemProperty(key) == null ? defaultVal : EnvUtil.getSystemProperty(key);
	}

	/**
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static Integer NVLInt(String key, Integer defaultVal) {
		return EnvUtil.getSystemProperty(key) == null ? defaultVal : Integer.valueOf(EnvUtil.getSystemProperty(key));
	}

	/**
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static Long NVLLong(String key, Long defaultVal) {
		return EnvUtil.getSystemProperty(key) == null ? defaultVal : Long.valueOf(EnvUtil.getSystemProperty(key));
	}

	/**
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static Double NVLDouble(String key, Double defaultVal) {
		return EnvUtil.getSystemProperty(key) == null ? defaultVal : Double.valueOf(EnvUtil.getSystemProperty(key));
	}

	/**
	 * 记录的运行状态:运行中
	 */
	public static final String RECORD_STATUS_RUNNING = "RUNNING";

	/**
	 * 记录的运行状态:注册
	 */
	public static final String RECORD_STATUS_REGISTE = "REGISTE";

	/**
	 * 记录的运行状态:受理
	 */
	public static final String RECORD_STATUS_APPLY = "APPLY";

	/**
	 * 记录的运行状态:异常
	 */
	public static final String RECORD_STATUS_ERROR = "ERROR";

	/**
	 * 记录的运行状态:完成
	 */
	public static final String RECORD_STATUS_FINISHED = "FINISHED";

	/**
	 * 远端的运行状态:异常
	 */
	public static final String REMOTE_STATUS_ERROR = "ERROR";

	/**
	 * 远端的运行状态:正常
	 */
	public static final String REMOTE_STATUS_RUNNING = "Online";

	/**
	 * 历史记录的TYPE:JOB
	 */
	public static final String RECORD_TYPE_JOB = "JOB";

	/**
	 * 转换记录的元数据ID:唯一
	 */
	public static final String RECORD_TYPE_TRANS = "TRANS";
}
