package com.katch.perfer.kettle.service.record;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.special.JobEntrySpecial;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.katch.perfer.kettle.bean.KettleJobEntireDefine;
import com.katch.perfer.kettle.consist.KettleVariables;
import com.katch.perfer.kettle.model.KettleRecord;
import com.katch.perfer.kettle.record.KettleRecordPool;
import com.katch.perfer.kettle.repository.KettleRecordRepository;
import com.katch.perfer.kettle.repository.KettleRepoRepository;

@Service
public abstract class KettleJobService {
    private static Logger logger = LoggerFactory.getLogger(KettleJobService.class);

    @Autowired
    protected KettleRecordRepository kettleRecordRepository;
    @Autowired
    protected KettleRepoRepository kettleRepoRepository;
    @Autowired
    protected KettleRecordPool kettleRecordPool;

    /**
     * 检查KettleJobEntireDefine的定义
     * 
     * @param jobEntire
     * @throws KettleException
     */
    private void checkKettleJobEntireDefine(KettleJobEntireDefine jobEntire) throws KettleException {
	JobEntryCopy jec = jobEntire.getMainJob().getStart();
	if (jec == null) {
	    throw new KettleException("JobMeta的核心Job[" + jobEntire.getMainJob().getName() + "]没有定义Start,无法受理!");
	}
	JobEntrySpecial jobStart = (JobEntrySpecial) jec.getEntry();
	if (!jobEntire.getDependentJobs().isEmpty()) {
	    for (JobMeta meta : jobEntire.getDependentJobs()) {
		if (meta.getStart() != null) {
		    throw new KettleException(
			    "JobMeta[" + jobEntire.getMainJob().getName() + "]的依赖Job[" + meta.getName() + "]存在Start!");
		}
	    }
	}
	if (jobStart.isRepeat() || jobStart.getSchedulerType() != JobEntrySpecial.NOSCHEDULING) {
	    throw new KettleException("JobMeta的核心Job[" + jobEntire.getMainJob().getName() + "]必须是即时任务!");
	}
    }

    /**
     * 将KettleJobEntireDefine保存到Kettle资源库
     * 
     * @param jobEntire
     * @return
     * @throws KettleException
     */
    private KettleRecord savejobEntire2KettleRepo(KettleJobEntireDefine jobEntire, String path) throws KettleException {
	if (path == null) {
	    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
	    jobEntire.setUuid(UUID.randomUUID().toString().replace("-", ""));
	    path = df.format(new Date());
	}
	jobEntire.setUuid(UUID.randomUUID().toString().replace("-", ""));
	KettleRecord record = kettleRepoRepository.saveJobEntireDefine(jobEntire, path);
	return record;
    }

    /**
     * 立即执行
     * 
     * @param jobEntire
     * @return
     * @throws KettleException
     */
    public KettleRecord excuteJobOnce(KettleJobEntireDefine jobEntire) throws KettleException {
	checkKettleJobEntireDefine(jobEntire);
	KettleRecord record = savejobEntire2KettleRepo(jobEntire, null);
	record.setStatus(KettleVariables.RECORD_STATUS_APPLY);
	record.setExecutionType(KettleVariables.RECORD_EXECUTION_TYPE_ONCE);
	if (kettleRecordPool.addRecord(record)) {
	    try {
		kettleRecordRepository.insertRecord(record);
	    } catch (Exception ex) {
		kettleRecordPool.deleteRecord(record.getUuid());
		throw new KettleException("Job申请执行失败!", ex);
	    }
	} else {
	    throw new KettleException("Job申请执行失败,任务池已满!");
	}
	return record;
    }

    /**
     * 注册任务
     * 
     * @param jobEntire
     * @return
     * @throws KettleException
     */
    public KettleRecord registeJob(KettleJobEntireDefine jobEntire) throws KettleException {
	checkKettleJobEntireDefine(jobEntire);
	KettleRecord record = savejobEntire2KettleRepo(jobEntire, KettleVariables.RECORD_EXECUTION_TYPE_PERSISTENT);
	record.setStatus(KettleVariables.RECORD_STATUS_REGISTE);
	record.setExecutionType(KettleVariables.RECORD_EXECUTION_TYPE_PERSISTENT);
	try {
	    kettleRecordRepository.insertRecord(record);
	} catch (Exception ex) {
	    logger.error("Job[" + jobEntire.getMainJob().getName() + "]执行注册操作发生异常!", ex);
	    kettleRepoRepository.deleteJobEntireDefine(record);
	    throw new KettleException("Job[" + jobEntire.getMainJob().getName() + "]执行注册操作发生异常!");
	}
	return record;
    }

    /**
     * 执行任务
     * 
     * @param uuid
     * @throws KettleException
     */
    public void excuteJob(String uuid) throws KettleException {
	KettleRecord record = kettleRecordRepository.queryRecord(uuid);
	if (record == null) {
	    throw new KettleException("Job[" + uuid + "]未找到,请先注册!");
	}
	if (KettleVariables.RECORD_EXECUTION_TYPE_PERSISTENT.equals(record.getExecutionType())) {
	    throw new KettleException("Job[" + uuid + "]不是持久化任务,无法手动执行!");
	}
	if (record.isRunning()) {
	    throw new KettleException("Job[" + uuid + "]执行中,无法再次执行!");
	}
	if (record.isApply()) {
	    throw new KettleException("Job[" + uuid + "]已经在执行队列中,无法再次执行!");
	}
	if (kettleRecordPool.addRecord(record)) {
	    record.setStatus(KettleVariables.RECORD_STATUS_APPLY);
	    KettleRecord update = new KettleRecord();
	    update.setStatus(KettleVariables.RECORD_STATUS_APPLY);
	    kettleRecordRepository.updateRecord(update);
	} else {
	    throw new KettleException("Job[" + uuid + "]申请执行失败,被任务池已满或任务已经存在!");
	}
    }

    /**
     * 删除JOB
     * 
     * @param uuid
     * @throws KettleException
     */
    public void deleteJob(String uuid) throws KettleException {
	KettleRecord record = kettleRecordRepository.queryRecord(uuid);
	if (record == null) {
	    return;
	}
	if (record.isError() || record.isFinished() || record.isRegiste()) {
	    kettleRecordRepository.queryRecordRelations(record);
	    kettleRecordPool.deleteRecord(uuid);
	    kettleRecordRepository.deleteRecord(uuid);
	    kettleRepoRepository.deleteJobEntireDefine(record);
	    return;
	}
	throw new KettleException("Record[" + uuid + "]已被受理,无法删除!");
    }

    /**
     * 立即删除
     * 
     * @param uuid
     * @throws KettleException
     */
    public void deleteJobImmediately(String uuid) throws KettleException {
	KettleRecord record = kettleRecordRepository.queryRecord(uuid);
	if (record == null) {
	    return;
	}
	kettleRecordRepository.queryRecordRelations(record);
	if (record.isError() || record.isFinished() || record.isRegiste()) {
	    kettleRecordPool.deleteRecord(uuid);
	    kettleRecordRepository.deleteRecord(uuid);
	    kettleRepoRepository.deleteJobEntireDefine(record);
	    return;
	}
	jobMustDie(record);
    }

    /**
     * @return
     */
    protected List<KettleRecord> getAllWaitingRecords() {
	return kettleRecordRepository.allWaitingRecords();
    }

    /**
     * 继承需要实现强制停止
     * 
     * @param record
     * @throws KettleException
     */
    protected abstract void jobMustDie(KettleRecord record) throws KettleException;

    /**
     * 查询JOB
     * 
     * @param uuid
     * @return
     */
    public KettleRecord queryJob(String uuid) {
	return kettleRecordRepository.queryRecord(uuid);
    }

    public KettleRecordRepository getKettleRecordRepository() {
	return kettleRecordRepository;
    }

    public void setKettleRecordRepository(KettleRecordRepository kettleRecordRepository) {
	this.kettleRecordRepository = kettleRecordRepository;
    }

    public KettleRepoRepository getKettleRepoRepository() {
	return kettleRepoRepository;
    }

    public void setKettleRepoRepository(KettleRepoRepository kettleRepoRepository) {
	this.kettleRepoRepository = kettleRepoRepository;
    }

    public KettleRecordPool getKettleRecordPool() {
	return kettleRecordPool;
    }

    public void setKettleRecordPool(KettleRecordPool kettleRecordPool) {
	this.kettleRecordPool = kettleRecordPool;
    }
}
