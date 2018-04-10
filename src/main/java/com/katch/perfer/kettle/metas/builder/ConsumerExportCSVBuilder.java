package com.katch.perfer.kettle.metas.builder;

import java.util.UUID;

import org.pentaho.di.core.NotePadMeta;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.job.JobHopMeta;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.special.JobEntrySpecial;
import org.pentaho.di.job.entries.trans.JobEntryTrans;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;
import org.pentaho.di.trans.steps.textfileoutput.TextFileOutputMeta;

import com.katch.perfer.kettle.bean.KettleJobEntireDefine;
import com.katch.perfer.kettle.metas.KettleSelectSQLMeta;

public class ConsumerExportCSVBuilder {
	/**
	 * SQL
	 */
	private KettleSelectSQLMeta consumer;

	public static ConsumerExportCSVBuilder newBuilder() {
		return new ConsumerExportCSVBuilder();
	}

	public ConsumerExportCSVBuilder consumer(KettleSelectSQLMeta consumer) {
		this.consumer = consumer;
		return this;
	}

	public TransMeta createTrans() throws Exception {
		final String uuid = UUID.randomUUID().toString().replace("-", "");
		TransMeta transMeta = null;
		transMeta = new TransMeta();
		transMeta.setName("CEC-" + uuid);
		final DatabaseMeta consumerDataBase = new DatabaseMeta(
				consumer.getHost() + "_" + consumer.getDatabase() + "_" + consumer.getUser(), consumer.getType(), "Native",
				consumer.getHost(), consumer.getDatabase(), consumer.getPort(), consumer.getUser(), consumer.getPasswd());
		transMeta.addDatabase(consumerDataBase);
		/*
		 * Note
		 */
		final String startNote = "Start " + transMeta.getName();
		final NotePadMeta ni = new NotePadMeta(startNote, 150, 10, -1, -1);
		transMeta.addNote(ni);
		/*
		 * source
		 */
		final TableInputMeta tii = new TableInputMeta();
		tii.setDatabaseMeta(consumerDataBase);
		final String selectSQL = consumer.getSql();
		tii.setSQL(selectSQL);
		final StepMeta query = new StepMeta("source", tii);
		query.setLocation(150, 100);
		query.setDraw(true);
		query.setDescription("STEP-SOURCE");
		transMeta.addStep(query);
		/*
		 * Export
		 */
		final TextFileOutputMeta tfom = new TextFileOutputMeta();
		tfom.setDefault();
		tfom.setSeparator(",");
		tfom.setExtension("");
		tfom.setAddToResultFiles(false);
		tfom.setFileName("${Internal.Entry.Current.Directory}/111.csv");
		final StepMeta export = new StepMeta("export", tfom);
		export.setLocation(350, 100);
		export.setDraw(true);
		export.setDescription("STEP-EXPORT");
		transMeta.addStep(export);
		transMeta.addTransHop(new TransHopMeta(query, export));
		return transMeta;
	}

	public KettleJobEntireDefine createJob() throws Exception {
		KettleJobEntireDefine kettleJobEntireDefine = new KettleJobEntireDefine();
		TransMeta transMeta = createTrans();
		kettleJobEntireDefine.getDependentTrans().add(transMeta);

		JobMeta mainJob = new JobMeta();
		mainJob.setName(UUID.randomUUID().toString().replace("-", ""));
		// 启动
		JobEntryCopy start = new JobEntryCopy(new JobEntrySpecial("START", true, false));
		start.setLocation(150, 100);
		start.setDrawn(true);
		start.setDescription("START");
		mainJob.addJobEntry(start);
		// 主执行
		JobEntryTrans trans = new JobEntryTrans(transMeta.getName());
		trans.setTransObjectId(transMeta.getObjectId());
		trans.setWaitingToFinish(true);
		// 当前目录,即job的同级目录
		trans.setDirectory("${Internal.Entry.Current.Directory}");
		trans.setTransname(transMeta.getName());
		JobEntryCopy excuter = new JobEntryCopy(trans);
		excuter.setLocation(300, 100);
		excuter.setDrawn(true);
		excuter.setDescription("MAINJOB");
		mainJob.addJobEntry(excuter);
		// 连接
		JobHopMeta hop = new JobHopMeta(start, excuter);
		mainJob.addJobHop(hop);
		kettleJobEntireDefine.setMainJob(mainJob);
		return kettleJobEntireDefine;
	}
}
