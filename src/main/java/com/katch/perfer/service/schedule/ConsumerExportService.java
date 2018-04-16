package com.katch.perfer.service.schedule;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.exception.KettleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.katch.perfer.kettle.bean.KettleResult;
import com.katch.perfer.kettle.consist.KettleVariables;
import com.katch.perfer.kettle.metas.builder.SqlDataExportBuilder;
import com.katch.perfer.kettle.service.KettleNorthService;
import com.katch.perfer.mahout.service.UserMahoutRecommenderService;

/**
 * 消费记录导出
 * 
 * @author Administrator
 *
 */
@Service
public class ConsumerExportService {
    private static Logger logger = LoggerFactory.getLogger(UserMahoutRecommenderService.class);

    @Autowired
    @Qualifier("consumerExportJobBuilder")
    private SqlDataExportBuilder consumerExportJobBuilder;

    @Autowired
    private KettleNorthService kettleNorthService;

    /**
     * 修正
     * 
     * @throws KettleException
     * @throws InterruptedException
     * 
     * @throws Exception
     */
    public String excute() throws KettleException, InterruptedException {
	String uuid = doExport();
	kettleRecordDaemon thread = new kettleRecordDaemon();
	thread.kettleUUID = uuid;
	thread.start();
	thread.join();
	logger.info("用户消费记录导出完成!");
	return thread.errorMsg;
    }

    /**
     * 导出
     * 
     * @return
     * @throws KettleException
     */
    private String doExport() throws KettleException {
	logger.info("用户消费记录开始导出!");
	KettleResult result = kettleNorthService.excuteJobOnce(consumerExportJobBuilder.createJob());
	if (StringUtils.isNotEmpty(result.getErrMsg())) {
	    throw new KettleException("Kettle导出消费记录失败,kettle发生问题:" + result.getErrMsg());
	}
	return result.getUuid();
    }

    private class kettleRecordDaemon extends Thread {

	private String kettleUUID;

	private String errorMsg;

	@Override
	public void run() {
	    try {
		long startTime = System.currentTimeMillis();
		while (true) {
		    Thread.sleep(10000L);
		    KettleResult result = kettleNorthService.queryJob(kettleUUID);
		    if (KettleVariables.RECORD_STATUS_FINISHED.equals(result.getStatus())) {
			break;
		    } else if (KettleVariables.RECORD_STATUS_ERROR.equals(result.getStatus())) {
			errorMsg = result.getErrMsg();
			break;
		    } else if (!KettleVariables.RECORD_STATUS_RUNNING.equals(result.getStatus())) {
			errorMsg = "Kettle的任务[" + kettleUUID + "]状态[" + result.getStatus() + "]异常!";
			break;
		    } else if (System.currentTimeMillis() - startTime > 10L * 1000L * 60L) {
			errorMsg = "Kettle的任务执行超时!";
			break;
		    }
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }
}
