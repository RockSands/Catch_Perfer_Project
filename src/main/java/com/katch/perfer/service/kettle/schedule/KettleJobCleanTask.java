package com.katch.perfer.service.kettle.schedule;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.katch.perfer.kettle.config.KettleRecordProperties;
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
public class KettleJobCleanTask {
	@Autowired
	private KettleRecordProperties kettleRecordProperties;
	@Autowired
	private KettleRecordRepository kettleRecordRepository;
	@Autowired
	private KettleRepoRepository kettleRepoRepository;

	/**
	 * 每小时执行一次 仅清理Once执行类型的任务
	 */
	@Scheduled(cron = "0 0 0/3 * * ?")
	// @Scheduled(cron = "0 0/1 * * * ?")
	public void autoCleanJob() {
		try {
			/*
			 * 清理任务
			 */
			List<KettleRecord> records = kettleRecordRepository.allCanDelRecords();
			Long current = System.currentTimeMillis();
			for (KettleRecord record : records) {
				if (current - record.getUpdateTime().getTime() > kettleRecordProperties.getOnceRecordSavePeriod() * 60L
						* 60L * 1000L) {
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
