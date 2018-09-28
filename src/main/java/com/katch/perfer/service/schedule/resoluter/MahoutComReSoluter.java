package com.katch.perfer.service.schedule.resoluter;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.katch.perfer.consist.Consist;
import com.katch.perfer.mahout.service.MahoutExportService;
import com.katch.perfer.mybatis.model.RecommendTaskTrack;
import com.katch.perfer.service.schedule.ConsumerRecommendResoluter;

/**
 * 使用Mahout读取CSV消费记录
 * @author Administrator
 *
 */
@Component
public class MahoutComReSoluter extends ConsumerRecommendResoluter {
	private static Logger logger = LoggerFactory.getLogger(MahoutComReSoluter.class);

	@Autowired
	private MahoutExportService mahoutExportService;

	private Deamon deamon = null;

	@Override
	protected boolean canResolve(RecommendTaskTrack track) {
		return Consist.RECOM_TASK_TRACK_STEP_MAHOUT_COM.equals(track.getStep());
	}

	@Override
	protected void resolve(RecommendTaskTrack track) throws Exception {
		if (deamon == null) {
			deamon = new Deamon();
			deamon.start();
		}
		if ("finish".equals(deamon.status)) {
			track.setStep(Consist.RECOM_TASK_TRACK_STEP_WRITE_FILE);
			track.setUpdateTime(new Date());
			recommendTaskTrackMapper.updateRecommendTaskTrack(track);
			mahoutExportService.setDataModel(deamon.dataMode);
			deamon = null;
		} else if ("error".equals(deamon.status)) {
			throw new Exception("消费记录Mahout计算发生错误!");
		} else {// 运行中
			if (System.currentTimeMillis() - track.getUpdateTime().getTime() > 20L * 60L * 1000L) {
				throw new Exception("消费记录Mahout计算超时!");
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void clear(RecommendTaskTrack track) {
		mahoutExportService.setDataModel(null);
		if (deamon != null) {
			deamon.stop();
		}
	}

	private class Deamon extends Thread {

		private DataModel dataMode;

		private String status = null;

		@Override
		public void run() {
			status = "start";
			File file = new File(consumerProperties.getTargetPath());
			try {
				dataMode = new FileDataModel(file);
				status = "finish";
			} catch (IOException e) {
				logger.error("消费记录Mahout计算发生错误!", e);
				status = "error";
			}
		}
	}
}
