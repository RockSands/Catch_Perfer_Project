package com.katch.perfer.domain.kettle;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Kettle记录依赖
 * @author Administrator
 *
 */
@Entity
public class KettleRecordRelation {
	
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Id
	@Column(name = "ID")
	private long id;
	
	/**
	 * 主JobUUID
	 */
	@Column(name = "MASTER_UUID_ID")
	private String masterUUID;

	/**
	 * Kettle对象ID
	 */
	@Column(name = "META_ID")
	private String metaid;

	/**
	 * 类型
	 */
	@Column(name = "META_TYPE")
	private String type;

	/**
	 * 创建时间
	 */
	@Column(name = "CREATE_TIME")
	private Date createTime;

	public String getMasterUUID() {
		return masterUUID;
	}

	public void setMasterUUID(String masterUUID) {
		this.masterUUID = masterUUID;
	}

	public String getMetaid() {
		return metaid;
	}

	public void setMetaid(String metaid) {
		this.metaid = metaid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
