package com.katch.perfer.kettle.record;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.katch.perfer.kettle.repository.KettleRecordRepository;
import com.katch.perfer.mybatis.model.KettleRecord;

@Component
public class KettleRecordPool {

	@Autowired
	private KettleRecordRepository kettleRecordRepository;

	public List<KettleRecord> next(int size, String hostName) {
		List<KettleRecord> records = kettleRecordRepository.allUnassignedRecords();
		List<KettleRecord> applyRecords = new ArrayList<KettleRecord>(size);
		for (KettleRecord record : records) {
			if (kettleRecordRepository.assignedRecord(record.getUuid(),hostName) == 1) {
				record.setHostname(hostName);
				applyRecords.add(record);
				if (applyRecords.size() == size) {
					return applyRecords;
				}
			}
		}
		return applyRecords;
	}
}
