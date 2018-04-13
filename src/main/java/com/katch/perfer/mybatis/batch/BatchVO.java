package com.katch.perfer.mybatis.batch;

public class BatchVO {
	
	public final static String OPERATION_INSERT = "ADD";
	public final static String OPERATION_DELET = "DEL";
	public final static String OPERATION_UPDATE = "MOD";
	
	// 操作类型:ADD,DEL,MOD
	private String operate;

	// 实际操作的DAO的传入VO
	private Object object;

	// 调用ibatis的sql语句ID
	private String string;

	/**
	 * @return Returns the operate.
	 */
	public String getOperate() {
		return operate;
	}

	/**
	 * @param operate
	 *            The operate to set.
	 */
	public void setOperate(String operate) {
		this.operate = operate;
	}

	/**
	 * @param operate
	 * @param object
	 * @param string
	 */
	public BatchVO(String operate, String string, Object object) {
		super();
		this.operate = operate;
		this.object = object;
		this.string = string;
	}

	/**
	 * @return Returns the object.
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * @param object
	 *            The object to set.
	 */
	public void setObject(Object object) {
		this.object = object;
	}

	/**
	 * @return Returns the string.
	 */
	public String getString() {
		return string;
	}

	/**
	 * @param string
	 *            The string to set.
	 */
	public void setString(String string) {
		this.string = string;
	}
}
