package com.katch.perfer;

import org.pentaho.di.core.exception.KettleException;

import com.katch.perfer.kettle.bean.KettleJobEntireDefine;
import com.katch.perfer.kettle.bean.KettleResult;
import com.katch.perfer.kettle.consist.KettleVariables;
import com.katch.perfer.kettle.metas.KettleTableMeta;
import com.katch.perfer.kettle.metas.builder.SyncTablesDatasBuilder;
import com.katch.perfer.kettle.service.KettleNorthService;

public class CreateSTDThread implements Runnable {
    KettleTableMeta source = null;

    KettleTableMeta target = null;

    String cron = null;

    KettleResult result = null;

    KettleJobEntireDefine kjed = null;

    KettleNorthService kettleNorthService;

    CreateSTDThread(KettleNorthService kettleNorthService, KettleTableMeta source, KettleTableMeta target,
	    String cron) {
	this.kettleNorthService = kettleNorthService;
	this.source = source;
	this.target = target;
	this.cron = cron;
    }

    @Override
    public void run() {
	try {
	    if (result != null) {
		result = kettleNorthService.queryJob(result.getUuid());
		System.out.println("==>[" + result.getUuid() + "]状态: " + result.getStatus());
	    }
	    if (result == null) {
		kjed = SyncTablesDatasBuilder.newBuilder().source(source).target(target).createJob();
		long now = System.currentTimeMillis();
		result = kettleNorthService.registeJob(kjed);
		now = System.currentTimeMillis();
		kettleNorthService.excuteJob(result.getUuid());
		System.out.println("==>apply used: " + (System.currentTimeMillis() - now));
	    }
	    if (KettleVariables.RECORD_STATUS_ERROR.equals(result.getStatus())
		    || KettleVariables.RECORD_STATUS_FINISHED.equals(result.getStatus())) {
		kettleNorthService.deleteJob(result.getUuid());
	    }
	} catch (KettleException e) {
	    e.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public KettleResult getResult() {
	return result;
    }

}
