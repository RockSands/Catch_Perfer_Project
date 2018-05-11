package com.katch.perfer.service.kettle;

import java.util.UUID;

import org.pentaho.di.core.Condition;
import org.pentaho.di.core.NotePadMeta;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaAndData;
import org.pentaho.di.job.JobHopMeta;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.special.JobEntrySpecial;
import org.pentaho.di.job.entries.trans.JobEntryTrans;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.dummytrans.DummyTransMeta;
import org.pentaho.di.trans.steps.filterrows.FilterRowsMeta;
import org.pentaho.di.trans.steps.mergerows.MergeRowsMeta;
import org.pentaho.di.trans.steps.sort.SortRowsMeta;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;
import org.pentaho.di.trans.steps.update.UpdateMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.katch.perfer.config.RecommendProperties;
import com.katch.perfer.config.TaxEnterpriseProperties;
import com.katch.perfer.kettle.bean.KettleJobEntireDefine;

@Component
public class TaxEnterpriseImportBuilder {
	@Autowired
	private RecommendProperties recommendProperties;

	@Autowired
	private TaxEnterpriseProperties taxEnterpriseProperties;

	public TransMeta createTrans() throws KettleException {
		final String uuid = UUID.randomUUID().toString().replace("-", "");
		TransMeta transMeta = null;
		transMeta = new TransMeta();
		transMeta.setName("TEIB-" + uuid);
		final DatabaseMeta sourceDataBase = new DatabaseMeta(
				taxEnterpriseProperties.getDbHost() + "_" + taxEnterpriseProperties.getDbPort() + "_"
						+ taxEnterpriseProperties.getDbDatabase() + "_" + taxEnterpriseProperties.getDbUser(),
				taxEnterpriseProperties.getDbType(), "Native", taxEnterpriseProperties.getDbHost(),
				taxEnterpriseProperties.getDbDatabase(), taxEnterpriseProperties.getDbPort(),
				taxEnterpriseProperties.getDbUser(), taxEnterpriseProperties.getDbPasswd());
		final DatabaseMeta targetDatabase = new DatabaseMeta(
				recommendProperties.getTargetHost() + "_" + recommendProperties.getTargetPort() + "_"
						+ recommendProperties.getTargetDatabase() + "_" + recommendProperties.getTargetUser(),
				recommendProperties.getTargetType(), "Native", recommendProperties.getTargetHost(),
				recommendProperties.getTargetDatabase(), recommendProperties.getTargetPort(),
				recommendProperties.getTargetUser(), recommendProperties.getTargetPasswd());
		transMeta.addDatabase(sourceDataBase);
		/*
		 * 构建
		 */
		String[] pkColumns = new String[] { "NSRSBH" };
		String[] targetAllColumns = new String[] { "NSRSBH", "SNYYE", "QSYYSJ" };
		String[] conditions = new String[1];
		for (int i = 0; i < conditions.length; i++) {
			conditions[i] = "=";
		}
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
		final String selectSQL = taxEnterpriseProperties.getTaxesEnterpriseImportSQL();
		tii.setSQL(selectSQL);
		final StepMeta source = new StepMeta("source", tii);
		source.setLocation(150, 100);
		source.setDraw(true);
		source.setDescription("STEP-SOURCE");
		transMeta.addStep(source);
		/*
		 * sourceSort
		 */
		SortRowsMeta sourceSR = new SortRowsMeta();
		sourceSR.setFieldName(pkColumns);
		sourceSR.setDirectory("%%java.io.tmpdir%%");
		sourceSR.setPrefix("sourceSortOut");
		sourceSR.setSortSize("1000000");
		sourceSR.setAscending(new boolean[] { true });
		sourceSR.setCaseSensitive(new boolean[] { true });
		sourceSR.setCollatorStrength(new int[] { 0 });
		sourceSR.setCollatorEnabled(new boolean[] { false });
		sourceSR.setPreSortedField(new boolean[] { false });
		StepMeta sourceSort = new StepMeta("sourceSort", sourceSR);
		sourceSort.setLocation(350, 100);
		sourceSort.setDraw(true);
		sourceSort.setDescription("STEP-sourceSort");
		transMeta.addStep(sourceSort);
		transMeta.addTransHop(new TransHopMeta(source, sourceSort));
		/*
		 * Target
		 */
		TableInputMeta targettii = new TableInputMeta();
		targettii.setDatabaseMeta(targetDatabase);
		targettii.setSQL("SELECT NSRSBH,SNYYE,QSYYSJ FROM tax_enterprise_info");
		StepMeta target = new StepMeta("target", targettii);
		transMeta.addStep(target);
		target.setLocation(150, 300);
		target.setDraw(true);
		target.setDescription("STEP-TARGET");
		/*
		 * targetSort
		 */
		SortRowsMeta targetSR = new SortRowsMeta();
		targetSR.setFieldName(pkColumns);
		targetSR.setDirectory("%%java.io.tmpdir%%");
		targetSR.setPrefix("targetSortOut");
		targetSR.setSortSize("1000000");
		targetSR.setAscending(new boolean[] { true });
		targetSR.setCaseSensitive(new boolean[] { true });
		targetSR.setCollatorStrength(new int[] { 0 });
		targetSR.setCollatorEnabled(new boolean[] { false });
		targetSR.setPreSortedField(new boolean[] { false });
		StepMeta targetSort = new StepMeta("targetSort", targetSR);
		targetSort.setLocation(350, 300);
		targetSort.setDraw(true);
		targetSort.setDescription("STEP-TARGETSORT");
		transMeta.addStep(targetSort);
		transMeta.addTransHop(new TransHopMeta(target, targetSort));
		/*
		 * merage
		 */
		MergeRowsMeta mrm = new MergeRowsMeta();
		mrm.setFlagField("flagfield");
		mrm.setValueFields(targetAllColumns);
		mrm.setKeyFields(pkColumns);
		mrm.getStepIOMeta().setInfoSteps(new StepMeta[] { targetSort, sourceSort });
		StepMeta merage = new StepMeta("merage", mrm);
		transMeta.addStep(merage);
		merage.setLocation(550, 200);
		merage.setDraw(true);
		merage.setDescription("STEP-MERAGE");
		transMeta.addTransHop(new TransHopMeta(targetSort, merage));
		transMeta.addTransHop(new TransHopMeta(sourceSort, merage));
		/*
		 * isChange
		 */
		FilterRowsMeta frm_isChange = new FilterRowsMeta();
		frm_isChange.setCondition(
				new Condition("flagfield", Condition.FUNC_EQUAL, null, new ValueMetaAndData("constant", "changed")));
		StepMeta isChange = new StepMeta("isChange", frm_isChange);
		isChange.setLocation(750, 200);
		isChange.setDraw(true);
		isChange.setDescription("STEP-ISCHANGE");
		transMeta.addStep(isChange);
		transMeta.addTransHop(new TransHopMeta(merage, isChange));
		/*
		 * update
		 */
		UpdateMeta um = new UpdateMeta();
		um.setDatabaseMeta(targetDatabase);
		um.setUseBatchUpdate(true);
		um.setTableName("TAX_ENTERPRISE_INFO");
		um.setCommitSize("100");
		um.setKeyLookup(pkColumns);
		um.setKeyStream(pkColumns);
		um.setKeyCondition(conditions);
		um.setKeyStream2(new String[targetAllColumns.length]);
		um.setUseBatchUpdate(true);
		um.setUpdateLookup(targetAllColumns);
		um.setUpdateStream(targetAllColumns);

		StepMeta update = new StepMeta("update", um);
		update.setLocation(950, 200);
		update.setDraw(true);
		update.setDescription("STEP-UPDATE");
		transMeta.addStep(update);
		transMeta.addTransHop(new TransHopMeta(isChange, update));
		frm_isChange.getStepIOMeta().getTargetStreams().get(0).setStepMeta(update);
		/*
		 * nothing
		 */
		StepMeta nothing = new StepMeta("nothing", new DummyTransMeta());
		nothing.setLocation(750, 400);
		nothing.setDraw(true);
		nothing.setDescription("STEP-NOTHING");
		transMeta.addStep(nothing);
		transMeta.addTransHop(new TransHopMeta(isChange, nothing));
		frm_isChange.getStepIOMeta().getTargetStreams().get(1).setStepMeta(nothing);
		return transMeta;
	}

	/**
	 * 创建任务
	 * 
	 * @return
	 * @throws KettleException
	 */
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
