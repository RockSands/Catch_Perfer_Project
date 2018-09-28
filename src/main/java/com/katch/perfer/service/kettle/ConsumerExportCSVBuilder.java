package com.katch.perfer.service.kettle;

import java.util.UUID;

import org.pentaho.di.core.NotePadMeta;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.job.JobHopMeta;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.special.JobEntrySpecial;
import org.pentaho.di.job.entries.trans.JobEntryTrans;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;
import org.pentaho.di.trans.steps.textfileoutput.TextFileField;
import org.pentaho.di.trans.steps.textfileoutput.TextFileOutputMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.katch.perfer.config.ConsumerProperties;
import com.katch.perfer.kettle.bean.KettleJobEntireDefine;

/**
 * 将消费数据输出到CSV文件,等待系统读取计算
 *
 */
@Component()
public class ConsumerExportCSVBuilder {
	@Autowired
	private ConsumerProperties consumerProperties;

	public TransMeta createTrans() throws KettleException {
		final String uuid = UUID.randomUUID().toString().replace("-", "");
		TransMeta transMeta = null;
		transMeta = new TransMeta();
		transMeta.setName("CEC-" + uuid);
		final DatabaseMeta sourceDataBase = new DatabaseMeta(
				consumerProperties.getSourceHost() + "_" + consumerProperties.getSourcePort() + "_"
						+ consumerProperties.getSourceDatabase() + "_" + consumerProperties.getSourceUser(),
				consumerProperties.getSourceType(), "Native", consumerProperties.getSourceHost(),
				consumerProperties.getSourceDatabase(), consumerProperties.getSourcePort(),
				consumerProperties.getSourceUser(), consumerProperties.getSourcePasswd());
		transMeta.addDatabase(sourceDataBase);
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
		tii.setDatabaseMeta(sourceDataBase);
		final String selectSQL = consumerProperties.getSourceSql();
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
		tfom.setSeparator(consumerProperties.getTargetSeparator());
		tfom.setExtension(consumerProperties.getTargetExtension());
		tfom.setHeaderEnabled(false);
		tfom.setFooterEnabled(false);
		tfom.setAddToResultFiles(false);
		tfom.setFileName(consumerProperties.getTargetPath());
		tfom.setEncoding("UTF-8");
		TextFileField field_0 = new TextFileField();
		field_0.setName("YHID");
		field_0.setType(ValueMetaInterface.TYPE_NUMBER);
		field_0.setTrimType(ValueMetaInterface.TRIM_TYPE_BOTH);
		field_0.setFormat("#");
		TextFileField field_1 = new TextFileField();
		field_1.setName("SPID_1");
		field_1.setType(ValueMetaInterface.TYPE_NUMBER);
		field_1.setTrimType(ValueMetaInterface.TRIM_TYPE_BOTH);
		field_1.setFormat("#");
		TextFileField field_2 = new TextFileField();
		field_2.setName("PF");
		field_2.setType(ValueMetaInterface.TYPE_NUMBER);
		field_2.setTrimType(ValueMetaInterface.TRIM_TYPE_BOTH);
		field_2.setFormat("#######.###");

		TextFileField[] outputFields = new TextFileField[] { field_0, field_1, field_2 };
		tfom.setOutputFields(outputFields);
		final StepMeta export = new StepMeta("export", tfom);
		export.setLocation(350, 100);
		export.setDraw(true);
		export.setDescription("STEP-EXPORT");
		transMeta.addStep(export);
		transMeta.addTransHop(new TransHopMeta(query, export));
		return transMeta;
	}

	public KettleJobEntireDefine createJob() throws KettleException {
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
