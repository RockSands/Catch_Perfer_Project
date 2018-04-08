package com.katch.perfer.kettle.schedule;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.katch.perfer.domain.kettle.KettleRecord;

/**
 * 每天凌晨处理任务
 * @author Administrator
 *
 */
@Component
public class KettleRecordTasks {
    /**
     * 每天凌晨1点执行清理垃圾任务
     */
    @Scheduled(cron="0 0 1 * * ?")
    public void AutoCleanJob() {
    	try {
    		/*
    		 * 清理任务
    		 */
    		List<KettleRecord> records = recordService.queryStopedJobs();
    		kettleMgrEnvironment.getDbClient().allStopRecord();
    		Long current = System.currentTimeMillis();
    		for (KettleRecord record : records) {
    		    if ((current - record.getUpdateTime().getTime()) / 1000 / 60
    			    / 60 > KettleMgrEnvironment.KETTLE_RECORD_PERSIST_MAX_HOUR) {
    			deleteJob(record.getUuid());
    		    }
    		}
    		/*
    		 * 清理目录
    		 */
    		recordService.deleteEmptyRepoPath();
    	    } catch (Exception e) {
    		e.printStackTrace();
    	    }
    }
}
