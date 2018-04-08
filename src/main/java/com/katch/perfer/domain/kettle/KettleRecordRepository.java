package com.katch.perfer.domain.kettle;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface KettleRecordRepository extends CrudRepository<KettleRecord, String> {
	@Modifying
	@Query(value = "UPDATE R_RECORD_JOB SET "
			+ "ID_JOB = :jobid , ID_RUN = :runID , HOSTNAME = :hostname , CRON_EXPRESSION = :cronExpression , UPDATE_TIME = :updateTime"
			+ "WHERE UUID = :uuid", nativeQuery = true)
	void updateRecordNoStatus(String jobid, String runID, String hostname, String cronExpression, Date updateTime, String uuid);

	@Modifying
	@Query(value = "UPDATE R_RECORD_JOB SET "
			+ "STATUS = :status , ID_RUN = :runID , HOSTNAME = :hostname , CRON_EXPRESSION = :cronExpression , UPDATE_TIME = :updateTime"
			+ "WHERE UUID = :uuid", nativeQuery = true)
	void updateRecordStatus(String status, String runID, String hostname, String cronExpression, Date updateTime, String uuid);
}
