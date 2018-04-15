package com.katch.perfer.service.kettle;

import java.util.UUID;

import org.pentaho.di.core.Condition;
import org.pentaho.di.core.NotePadMeta;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaAndData;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.job.JobHopMeta;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.special.JobEntrySpecial;
import org.pentaho.di.job.entries.trans.JobEntryTrans;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.csvinput.CsvInputMeta;
import org.pentaho.di.trans.steps.delete.DeleteMeta;
import org.pentaho.di.trans.steps.dummytrans.DummyTransMeta;
import org.pentaho.di.trans.steps.filterrows.FilterRowsMeta;
import org.pentaho.di.trans.steps.mergerows.MergeRowsMeta;
import org.pentaho.di.trans.steps.sort.SortRowsMeta;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;
import org.pentaho.di.trans.steps.tableoutput.TableOutputMeta;
import org.pentaho.di.trans.steps.textfileinput.TextFileInputField;
import org.pentaho.di.trans.steps.update.UpdateMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.katch.perfer.config.ConsumerExportCSVProperties;
import com.katch.perfer.kettle.bean.KettleJobEntireDefine;

/**
 * Kettle数据库同步构建器
 * 
 * @author Administrator
 *
 */
@Component()
@SuppressWarnings("deprecation")
public class UserRecommendCSV2DBBuild {

    @Autowired
    private ConsumerExportCSVProperties consumerExportCSVProperties;

    private TransMeta createTrans() throws KettleException {
	String uuid = UUID.randomUUID().toString().replace("-", "");
	TransMeta transMeta = null;
	transMeta = new TransMeta();
	transMeta.setName("CTIS-" + uuid);
	DatabaseMeta targetDatabase = new DatabaseMeta("192.168.80.138_kettle", "MySql", "Native", "192.168.80.138",
		"kettle", "3306", "root", "123456");
	transMeta.addDatabase(targetDatabase);
	/*
	 * 条件
	 */
	String[] conditions = new String[2];
	for (int i = 0; i < conditions.length; i++) {
	    conditions[i] = "=";
	}
	/*
	 * Note
	 */
	String startNote = "Start " + transMeta.getName();
	NotePadMeta ni = new NotePadMeta(startNote, 150, 10, -1, -1);
	transMeta.addNote(ni);
	/*
	 * source
	 */
	CsvInputMeta ci = new CsvInputMeta();
	ci.setDelimiter(",");
	ci.setEncoding("UTF-8");
	ci.setFilename(consumerExportCSVProperties.getUserRecommendFileName());
	ci.setIncludingFilename(false);
	ci.setAddResultFile(false);
	ci.setBufferSize("50000");
	TextFileInputField field_0 = new TextFileInputField();
	field_0.setName("YH_ID");
	field_0.setType(ValueMetaInterface.TYPE_INTEGER);
	field_0.setTrimType(ValueMetaInterface.TRIM_TYPE_BOTH);
	TextFileInputField field_1 = new TextFileInputField();
	field_1.setName("SP_ID");
	field_1.setType(ValueMetaInterface.TYPE_INTEGER);
	field_1.setTrimType(ValueMetaInterface.TRIM_TYPE_BOTH);
	TextFileInputField field_2 = new TextFileInputField();
	field_2.setName("SCORE");
	field_2.setType(ValueMetaInterface.TYPE_NUMBER);
	field_2.setTrimType(ValueMetaInterface.TRIM_TYPE_BOTH);
	field_2.setFormat("#######.###");
	TextFileInputField[] inputFields = new TextFileInputField[] { field_0, field_1, field_2 };
	ci.setInputFields(inputFields);
	StepMeta source = new StepMeta("source", ci);
	source.setLocation(150, 100);
	source.setDraw(true);
	source.setDescription("STEP-SOURCE");
	transMeta.addStep(source);

	/*
	 * 排序
	 */
	SortRowsMeta sourceSR = new SortRowsMeta();
	sourceSR.setFieldName(new String[] { "YH_ID", "SP_ID" });
	sourceSR.setDirectory("%%java.io.tmpdir%%");
	sourceSR.setPrefix("sourceSortOut");
	sourceSR.setSortSize("1000000");
	sourceSR.setAscending(new boolean[] { true, true });
	sourceSR.setCaseSensitive(new boolean[] { true, true });
	sourceSR.setCollatorStrength(new int[] { 0, 0 });
	sourceSR.setCollatorEnabled(new boolean[] { false, false });
	sourceSR.setPreSortedField(new boolean[] { false, false });
	StepMeta sourceSort = new StepMeta("sourceSort", sourceSR);
	sourceSort.setLocation(350, 100);
	sourceSort.setDraw(true);
	sourceSort.setDescription("STEP-SOURCETARGET");
	transMeta.addStep(sourceSort);
	transMeta.addTransHop(new TransHopMeta(source, sourceSort));

	/*
	 * target
	 */
	TableInputMeta targettii = new TableInputMeta();
	targettii.setDatabaseMeta(targetDatabase);
	targettii.setSQL("SELECT YH_ID,SP_ID,SCORE FROM SQY_RZDK_JYYH_SPTJ");
	StepMeta targetQuery = new StepMeta("target", targettii);
	transMeta.addStep(targetQuery);
	targetQuery.setLocation(150, 300);
	targetQuery.setDraw(true);
	targetQuery.setDescription("STEP-TARGET");
	/*
	 * 排序
	 */
	SortRowsMeta targetSR = new SortRowsMeta();
	targetSR.setFieldName(new String[] { "YH_ID", "SP_ID" });
	targetSR.setDirectory("%%java.io.tmpdir%%");
	targetSR.setPrefix("sourceSortOut");
	targetSR.setSortSize("1000000");
	targetSR.setAscending(new boolean[] { true, true });
	targetSR.setCaseSensitive(new boolean[] { true, true });
	targetSR.setCollatorStrength(new int[] { 0, 0 });
	targetSR.setCollatorEnabled(new boolean[] { false, false });
	targetSR.setPreSortedField(new boolean[] { false, false });
	StepMeta targetSort = new StepMeta("targetSort", targetSR);
	targetSort.setLocation(350, 300);
	targetSort.setDraw(true);
	targetSort.setDescription("STEP-TARGETSORT");
	transMeta.addStep(targetSort);
	transMeta.addTransHop(new TransHopMeta(targetQuery, targetSort));

	/*
	 * merage
	 */
	MergeRowsMeta mrm = new MergeRowsMeta();
	mrm.setFlagField("flagfield");
	mrm.setValueFields(new String[] { "SCORE" });
	mrm.setKeyFields(new String[] { "YH_ID", "SP_ID" });
	mrm.getStepIOMeta().setInfoSteps(new StepMeta[] { targetSort, sourceSort });
	StepMeta merage = new StepMeta("merage", mrm);
	transMeta.addStep(merage);
	merage.setLocation(650, 100);
	merage.setDraw(true);
	merage.setDescription("STEP-MERAGE");
	transMeta.addTransHop(new TransHopMeta(targetSort, merage));
	transMeta.addTransHop(new TransHopMeta(sourceSort, merage));

	/*
	 * noChange
	 */
	FilterRowsMeta frm_nochange = new FilterRowsMeta();
	frm_nochange.setCondition(
		new Condition("flagfield", Condition.FUNC_EQUAL, null, new ValueMetaAndData("constant", "identical")));
	StepMeta nochang = new StepMeta("nochang", frm_nochange);
	nochang.setLocation(950, 100);
	nochang.setDraw(true);
	nochang.setDescription("STEP-NOCHANGE");
	transMeta.addStep(nochang);
	transMeta.addTransHop(new TransHopMeta(merage, nochang));
	/*
	 * nothing
	 */
	StepMeta nothing = new StepMeta("nothing", new DummyTransMeta());
	nothing.setLocation(950, 300);
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
	isNew.setLocation(1250, 100);
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
	toi.setTableName("SQY_RZDK_JYYH_SPTJ");
	toi.setCommitSize(100);
	toi.setTruncateTable(false);
	toi.setSpecifyFields(true);
	toi.setFieldDatabase(new String[] { "YH_ID", "SP_ID", "SCORE" });
	toi.setFieldStream(new String[] { "YH_ID", "SP_ID", "SCORE" });
	StepMeta insert = new StepMeta("insert", toi);
	insert.setLocation(1250, 300);
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
	isChange.setLocation(1550, 100);
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
	um.setTableName("SQY_RZDK_JYYH_SPTJ");
	um.setCommitSize("100");
	um.setKeyLookup(new String[] { "YH_ID", "SP_ID" });
	um.setKeyStream(new String[] { "YH_ID", "SP_ID" });
	um.setKeyCondition(conditions);
	um.setKeyStream2(new String[2]);
	um.setUseBatchUpdate(true);
	um.setUpdateLookup(new String[] { "YH_ID", "SP_ID", "SCORE" });
	um.setUpdateStream(new String[] { "YH_ID", "SP_ID", "SCORE" });

	StepMeta update = new StepMeta("update", um);
	update.setLocation(1850, 300);
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
	dm.setTableName("SQY_RZDK_JYYH_SPTJ");
	dm.setCommitSize("100");
	dm.setKeyCondition(conditions);
	dm.setKeyLookup(new String[] { "YH_ID", "SP_ID" });
	dm.setKeyStream2(new String[2]);
	dm.setKeyStream(new String[] { "YH_ID", "SP_ID" });
	StepMeta delete = new StepMeta("delete", dm);
	delete.setLocation(1550, 300);
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

    public ConsumerExportCSVProperties getConsumerExportCSVProperties() {
	return consumerExportCSVProperties;
    }

    public void setConsumerExportCSVProperties(ConsumerExportCSVProperties consumerExportCSVProperties) {
	this.consumerExportCSVProperties = consumerExportCSVProperties;
    }
}
