package com.katch.perfer.service.schedule.resoluter;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.katch.perfer.consist.Consist;
import com.katch.perfer.mahout.service.MahoutExportService;
import com.katch.perfer.mybatis.model.RecommendTaskTrack;
import com.katch.perfer.service.schedule.ConsumerRecommendResoluter;

/**
 * 将Mahout的记录导出
 * @author Administrator
 *
 */
@Component
public class MahoutExportResoluter extends ConsumerRecommendResoluter {
	private static Logger logger = LoggerFactory.getLogger(MahoutExportResoluter.class);

	@Autowired
	private MahoutExportService mahoutExportService;

	private Deamon deamon = null;

	@Override
	protected boolean canResolve(RecommendTaskTrack track) {
		return Consist.RECOM_TASK_TRACK_STEP_WRITE_FILE.equals(track.getStep());
	}

	@Override
	protected void resolve(RecommendTaskTrack track) throws Exception {
		if (deamon == null) {
			deamon = new Deamon();
			deamon.setPriority(8);
			deamon.start();
		}
		if ("finish".equals(deamon.status)) {
			track.setStep(Consist.RECOM_TASK_TRACK_STEP_WRITE_DB);
			track.setUpdateTime(new Date());
			recommendTaskTrackMapper.updateRecommendTaskTrack(track);
			mahoutExportService.setDataModel(null);
			deamon = null;
		} else if ("error".equals(deamon.status)) {
			throw new Exception("消费推荐文件导出失败!");
		} else {// 运行中 - 不设置超时
			// if (System.currentTimeMillis() - track.getUpdateTime().getTime() > 3L * 60L *
			// 60L * 1000L) {
			// throw new Exception("消费推荐文件导出超时!");
			// }
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void clear(RecommendTaskTrack track) {
		mahoutExportService.setDataModel(null);
		if (deamon != null) {
			deamon.stop();
			deamon = null;
		}
	}

	private class Deamon extends Thread {

		private String status = null;

		@Override
		public void run() {
			status = "start";
			try {
				mahoutExportService.excute();
				status = "finish";
			} catch (Exception e) {
				logger.error("消费记录Mahout计算发生错误!", e);
				status = "error";
			}
		}
	}
}
