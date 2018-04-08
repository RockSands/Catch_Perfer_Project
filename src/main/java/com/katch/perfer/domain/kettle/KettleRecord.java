package com.katch.perfer.domain.kettle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.katch.perfer.consist.kettle.KettleVariables;

/**
 * Kettle记录
 * 
 * @author Administrator
 *
 */
@Entity
public class KettleRecord {

	/**
	 * UUID
	 */
	@Id
	@Column(name = "UUID", unique = true, nullable = false, length = 11)
	private String uuid;

	/**
	 * 任务ID
	 */
	@Column(name = "ID_JOB")
	private String jobid;

	/**
	 * 名称
	 */
	@Column(name = "NAME_JOB")
	private String name;

	/**
	 * 运行ID-远程的执行ObjectID
	 */
	@Column(name = "ID_RUN")
	private String runID;
	/**
	 * 状态
	 */
	@Column(name = "STATUS")
	private String status;

	/**
	 * 主机信息
	 */
	@Column(name = "HOSTNAME")
	private String hostname;

	/**
	 * CRON表达式
	 */
	@Column(name = "CRON_EXPRESSION")
	private String cronExpression;

	/**
	 * 创建时间
	 */
	@Column(name = "CREATE_TIME")
	private Date createTime;

	/**
	 * 更新时间
	 */
	@Column(name = "UPDATE_TIME")
	private Date updateTime;

	/**
	 * 异常信息
	 */
	@Column(name = "ERROR_MSG")
	private String errMsg;

	/**
	 * 依赖
	 */
	private List<KettleRecordRelation> relations;

	public KettleRecord() {
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getJobid() {
		return jobid;
	}

	public void setJobid(String jobid) {
		this.jobid = jobid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRunID() {
		return runID;
	}

	public void setRunID(String runID) {
		this.runID = runID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		if (errMsg != null) {
			if (errMsg.length() > 500) {
				this.errMsg = errMsg.trim().substring(0, 500);
			} else {
				this.errMsg = errMsg;
			}
		}
	}

	/**
	 * @return the relations
	 */
	public List<KettleRecordRelation> getRelations() {
		if (relations == null) {
			relations = new ArrayList<KettleRecordRelation>();
		}
		return relations;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * 是否运行状态
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return KettleVariables.RECORD_STATUS_RUNNING.equals(this.getStatus());
	}

	/**
	 * 是否受理状态
	 * 
	 * @return
	 */
	public boolean isApply() {
		return KettleVariables.RECORD_STATUS_APPLY.equals(this.getStatus());
	}

	/**
	 * 是否异常状态
	 * 
	 * @return
	 */
	public boolean isError() {
		return KettleVariables.RECORD_STATUS_ERROR.equals(this.getStatus());
	}

	/**
	 * 是否完成中
	 * 
	 * @return
	 */
	public boolean isFinished() {
		return KettleVariables.RECORD_STATUS_FINISHED.equals(this.getStatus());
	}

	/**
	 * 是否注册
	 * 
	 * @return
	 */
	public boolean isRegiste() {
		return KettleVariables.RECORD_STATUS_REGISTE.equals(this.getStatus());
	}
}
