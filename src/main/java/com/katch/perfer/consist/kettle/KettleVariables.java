package com.katch.perfer.consist.kettle;

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

	// /**
	// * 工作记录的表名
	// */
	// public static final String R_JOB_RECORD = "R_RECORD_JOB";
	//
	// /**
	// * 历史记录的表名
	// */
	// public static final String R_HISTORY_RECORD = "R_RECORD_HISTORY";
	//
	// /**
	// * 关系表的表名
	// */
	// public static final String R_RECORD_DEPENDENT = "R_RECORD_DEPENDENT";
	//
	// /**
	// * 关系表的主ID
	// */
	// public static final String R_RECORD_DEPENDENT_MASTER_UUID_ID =
	// "MASTER_UUID_ID";
	//
	// /**
	// * 关系表的关联ID
	// */
	// public static final String R_RECORD_DEPENDENT_META_ID = "META_ID";
	//
	// /**
	// * 关系表的关联类型
	// */
	// public static final String R_RECORD_DEPENDENT_META_TYPE = "META_TYPE";
	//
	// /**
	// * 历史记录的ID
	// */
	// public static final String R_HISTORY_RECORD_ID = "ID";
	//
	// /**
	// * 历史记录的NAME
	// */
	// public static final String R_HISTORY_RECORD_NAME = "NAME";
	//
	// /**
	// * 记录的RECORD_TYPE
	// */
	// public static final String R_RECORD_RECORD_TYPE = "RECORD_TYPE";
	//
	// /**
	// * 记录的CRON表达式
	// */
	// public static final String R_RECORD_CRON_EXPRESSION = "CRON_EXPRESSION";
	//
	// /**
	// * 工作记录的元数据ID:唯一
	// */
	// public static final String R_JOB_RECORD_UUID = "UUID";
	//
	// /**
	// * 工作记录的元数据ID:唯一
	// */
	// public static final String R_JOB_RECORD_ID_JOB = "ID_JOB";
	//
	// /**
	// * 转换记录的元数据名称
	// */
	// public static final String R_TRANS_RECORD_NAME_TRANS = "NAME_TRANSFORMATION";
	//
	// /**
	// * 工作记录的元数据名称
	// */
	// public static final String R_JOB_RECORD_NAME_JOB = "NAME_JOB";
	//
	// /**
	// * 工作或转换记录的运行ID:唯一
	// */
	// public static final String R_RECORD_ID_RUN = "ID_RUN";
	//
	// /**
	// * 工作或转换记录的主机名
	// */
	// public static final String R_RECORD_HOSTNAME = "HOSTNAME";
	//
	// /**
	// * 工作或转换记录的创建时间
	// */
	// public static final String R_RECORD_CREATETIME = "CREATE_TIME";
	//
	// /**
	// * 工作或转换记录的更新时间
	// */
	// public static final String R_RECORD_UPDATETIME = "UPDATE_TIME";
	//
	// /**
	// * 工作或转换记录的异常信息
	// */
	// public static final String R_RECORD_ERRORMSG = "ERROR_MSG";
	//
	// /**
	// * 工作或转换记录的状态
	// */
	// public static final String R_RECORD_STATUS = "STATUS";
	//
	// /**
	// * 工作记录的类型
	// */
	// public static final String R_JOB_RECORD_TYPE = "TYPE";

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
