package com.katch.perfer.service.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TaxEnterpriseOffImport {
	@Scheduled(cron = "${consumer.mahout.cron}")
	public void excute() {
		
	}
}
