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
import org.pentaho.di.trans.steps.delete.DeleteMeta;
import org.pentaho.di.trans.steps.dummytrans.DummyTransMeta;
import org.pentaho.di.trans.steps.filterrows.FilterRowsMeta;
import org.pentaho.di.trans.steps.mergejoin.MergeJoinMeta;
import org.pentaho.di.trans.steps.mergerows.MergeRowsMeta;
import org.pentaho.di.trans.steps.sort.SortRowsMeta;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;
import org.pentaho.di.trans.steps.tableoutput.TableOutputMeta;
import org.pentaho.di.trans.steps.update.UpdateMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.katch.perfer.config.ConsumerProperties;
import com.katch.perfer.config.RecommendProperties;
import com.katch.perfer.config.TaxEnterpriseProperties;
import com.katch.perfer.kettle.bean.KettleJobEntireDefine;

@Component()
public class TaxEnterpriseImportBuilder {
    @Autowired
    private ConsumerProperties consumerProperties;

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
	final DatabaseMeta joinDataBase = new DatabaseMeta(
		consumerProperties.getSourceHost() + "_" + consumerProperties.getSourcePort() + "_"
			+ consumerProperties.getSourceDatabase() + "_" + consumerProperties.getSourceUser(),
		consumerProperties.getSourceType(), "Native", consumerProperties.getSourceHost(),
		consumerProperties.getSourceDatabase(), consumerProperties.getSourcePort(),
		consumerProperties.getSourceUser(), consumerProperties.getSourcePasswd());
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
	String[] targetAllColumns = new String[] { "NSRSBH","SNYYE","QSYYSJ" };
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
	sourceSort.setDescription("STEP-SOURCETARGET");
	transMeta.addStep(sourceSort);
	transMeta.addTransHop(new TransHopMeta(source, sourceSort));
	/*
	 * Join
	 */
	final TableInputMeta tii2 = new TableInputMeta();
	tii2.setDatabaseMeta(joinDataBase);
	final String selectSQL2 = consumerProperties.getTaxEnterpriseSql();
	tii2.setSQL(selectSQL2);
	final StepMeta join = new StepMeta("join", tii2);
	join.setLocation(150, 300);
	join.setDraw(true);
	join.setDescription("STEP-JOIN");
	transMeta.addStep(join);
	/*
	 * joinSort
	 */
	SortRowsMeta joinSR = new SortRowsMeta();
	joinSR.setFieldName(pkColumns);
	joinSR.setDirectory("%%java.io.tmpdir%%");
	joinSR.setPrefix("joinSortOut");
	joinSR.setSortSize("1000000");
	joinSR.setAscending(new boolean[] { true });
	joinSR.setCaseSensitive(new boolean[] { true });
	joinSR.setCollatorStrength(new int[] { 0 });
	joinSR.setCollatorEnabled(new boolean[] { false });
	joinSR.setPreSortedField(new boolean[] { false });
	StepMeta joinSort = new StepMeta("joinSort", joinSR);
	joinSort.setLocation(350, 300);
	joinSort.setDraw(true);
	joinSort.setDescription("STEP-JOINSORT");
	transMeta.addStep(joinSort);
	transMeta.addTransHop(new TransHopMeta(join, joinSort));
	/*
	 * JoinMerage
	 */
	MergeJoinMeta mjm = new MergeJoinMeta();
	mjm.setJoinType("INNER");
	mjm.setKeyFields1(pkColumns);
	mjm.setKeyFields2(pkColumns);
	mjm.getStepIOMeta().setInfoSteps(new StepMeta[] { sourceSort, joinSort });
	StepMeta JoinMerage = new StepMeta("JoinMerage", mjm);
	JoinMerage.setLocation(550, 200);
	JoinMerage.setDraw(true);
	JoinMerage.setDescription("STEP-JoinMerage");
	transMeta.addStep(JoinMerage);
	transMeta.addTransHop(new TransHopMeta(sourceSort, JoinMerage));
	transMeta.addTransHop(new TransHopMeta(joinSort, JoinMerage));
	/*
	 * target
	 */
	TableInputMeta targettii = new TableInputMeta();
	targettii.setDatabaseMeta(targetDatabase);
	targettii.setSQL("SELECT NSRSBH,SNYYE,QSYYSJ FROM tax_enterprise_info ORDER BY NSRSBH");
	StepMeta target = new StepMeta("target", targettii);
	transMeta.addStep(target);
	target.setLocation(750, 400);
	target.setDraw(true);
	target.setDescription("STEP-TARGET");
	/*
	 * merage
	 */
	MergeRowsMeta mrm = new MergeRowsMeta();
	mrm.setFlagField("flagfield");
	mrm.setValueFields(targetAllColumns);
	mrm.setKeyFields(pkColumns);
	mrm.getStepIOMeta().setInfoSteps(new StepMeta[] { target, sourceSort });
	StepMeta merage = new StepMeta("merage", mrm);
	transMeta.addStep(merage);
	merage.setLocation(650, 300);
	merage.setDraw(true);
	merage.setDescription("STEP-MERAGE");
	transMeta.addTransHop(new TransHopMeta(target, merage));
	transMeta.addTransHop(new TransHopMeta(JoinMerage, merage));
	/*
	 * noChange
	 */
	FilterRowsMeta frm_nochange = new FilterRowsMeta();
	frm_nochange.setCondition(
			new Condition("flagfield", Condition.FUNC_EQUAL, null, new ValueMetaAndData("constant", "identical")));
	StepMeta nochang = new StepMeta("nochang", frm_nochange);
	nochang.setLocation(850, 300);
	nochang.setDraw(true);
	nochang.setDescription("STEP-NOCHANGE");
	transMeta.addStep(nochang);
	transMeta.addTransHop(new TransHopMeta(merage, nochang));
	/*
	 * nothing
	 */
	StepMeta nothing = new StepMeta("nothing", new DummyTransMeta());
	nothing.setLocation(850, 500);
	nothing.setDraw(true);
	nothing.setDescription("STEP-NOTHING");
	transMeta.addStep(nothing);
	transMeta.addTransHop(new TransHopMeta(nochang, nothing));
	frm_nochange.getStepIOMeta().getTargetStreams().get(0).setStepMeta(nothing);
	/*
	 * isNew
	 */
	FilterRowsMeta frm_new = new FilterRowsMeta();
	frm_new.setCondition(
			new Condition("flagfield", Condition.FUNC_EQUAL, null, new ValueMetaAndData("constant", "new")));
	StepMeta isNew = new StepMeta("isNew", frm_new);
	isNew.setLocation(1050, 300);
	isNew.setDraw(true);
	isNew.setDescription("STEP-ISNEW");
	transMeta.addStep(isNew);
	transMeta.addTransHop(new TransHopMeta(nochang, isNew));
	frm_nochange.getStepIOMeta().getTargetStreams().get(1).setStepMeta(isNew);
	/*
	 * insert
	 */
	TableOutputMeta toi = new TableOutputMeta();
	toi.setDatabaseMeta(targetDatabase);
	toi.setTableName("TAX_ENTERPRISE_INFO");
	toi.setCommitSize(100);
	toi.setTruncateTable(false);
	toi.setSpecifyFields(true);
	toi.setFieldDatabase(targetAllColumns);
	toi.setFieldStream(targetAllColumns);
	StepMeta insert = new StepMeta("insert", toi);
	insert.setLocation(1050, 500);
	insert.setDraw(true);
	insert.setDescription("STEP-INSERT");
	transMeta.addStep(insert);
	transMeta.addTransHop(new TransHopMeta(isNew, insert));
	frm_new.getStepIOMeta().getTargetStreams().get(0).setStepMeta(insert);
	/*
	 * isChange
	 */
	FilterRowsMeta frm_isChange = new FilterRowsMeta();
	frm_isChange.setCondition(
			new Condition("flagfield", Condition.FUNC_EQUAL, null, new ValueMetaAndData("constant", "changed")));
	StepMeta isChange = new StepMeta("isChange", frm_isChange);
	isChange.setLocation(1250, 300);
	isChange.setDraw(true);
	isChange.setDescription("STEP-ISCHANGE");
	transMeta.addStep(isChange);
	transMeta.addTransHop(new TransHopMeta(isNew, isChange));
	frm_new.getStepIOMeta().getTargetStreams().get(1).setStepMeta(isChange);
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
	um.setKeyStream2(new String[2]);
	um.setUseBatchUpdate(true);
	um.setUpdateLookup(targetAllColumns);
	um.setUpdateStream(targetAllColumns);

	StepMeta update = new StepMeta("update", um);
	update.setLocation(1250, 500);
	update.setDraw(true);
	update.setDescription("STEP-UPDATE");
	transMeta.addStep(update);
	transMeta.addTransHop(new TransHopMeta(isChange, update));
	frm_isChange.getStepIOMeta().getTargetStreams().get(0).setStepMeta(update);
	/*
	 * delete
	 */
	DeleteMeta dm = new DeleteMeta();
	dm.setDatabaseMeta(targetDatabase);
	dm.setTableName("TAX_ENTERPRISE_INFO");
	dm.setCommitSize("100");
	dm.setKeyCondition(conditions);
	dm.setKeyLookup(pkColumns);
	dm.setKeyStream2(new String[2]);
	dm.setKeyStream(pkColumns);
	StepMeta delete = new StepMeta("delete", dm);
	delete.setLocation(1450, 300);
	delete.setDraw(true);
	delete.setDescription("STEP-DELETE");
	transMeta.addStep(delete);
	transMeta.addTransHop(new TransHopMeta(isChange, delete));
	frm_isChange.getStepIOMeta().getTargetStreams().get(1).setStepMeta(delete);
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
