package com.katch.perfer.kettle.service.schedule;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.katch.perfer.kettle.consist.KettleVariables;
import com.katch.perfer.kettle.repository.KettleRecordRepository;
import com.katch.perfer.kettle.repository.KettleRepoRepository;
import com.katch.perfer.mybatis.model.KettleRecord;

/**
 * 每天凌晨处理任务
 * 
 * @author Administrator
 *
 */
@Service
public class KettleRecordTasks {

	@Autowired
	private KettleRecordRepository kettleRecordRepository;
	@Autowired
	private KettleRepoRepository kettleRepoRepository;

	/**
	 * 每天凌晨1点执行清理垃圾任务 仅清理Once执行类型的任务
	 */
	@Scheduled(cron = "0 0 1 * * ?")
	public void AutoCleanJob() {
		try {
			/*
			 * 清理任务
			 */
			List<KettleRecord> records = kettleRecordRepository.allStopRecords();
			Long current = System.currentTimeMillis();
			for (KettleRecord record : records) {
				if ((current - record.getUpdateTime().getTime()) / 1000 / 60
						/ 60 > KettleVariables.KETTLE_RECORD_PERSIST_MAX_HOUR) {
					kettleRecordRepository.queryRecordRelations(record);
					kettleRepoRepository.deleteJobEntireDefine(record);
					kettleRecordRepository.deleteRecord(record.getUuid());
				}
			}
			/*
			 * 清理目录
			 */
			kettleRepoRepository.deleteEmptyRepoPath(Arrays.asList(KettleVariables.RECORD_EXECUTION_TYPE_PERSISTENT,
					KettleVariables.RECORD_EXECUTION_TYPE_CRON));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
