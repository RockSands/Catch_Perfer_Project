package com.katch.perfer.mybatis.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.katch.perfer.config.mybatis.DataSourceEnum;
import com.katch.perfer.config.mybatis.DataSourceTypeAnno;
import com.katch.perfer.kettle.model.KettleRecord;
import com.katch.perfer.kettle.model.KettleRecordRelation;

@Mapper
public interface KettleRecordMapper {
    @DataSourceTypeAnno(DataSourceEnum.primary)
    KettleRecord queryRecord(@Param("uuid") String uuid);

    @DataSourceTypeAnno(DataSourceEnum.primary)
    List<KettleRecord> allWaitingRecords();

    @DataSourceTypeAnno(DataSourceEnum.primary)
    List<KettleRecord> allStopRecords();

    @DataSourceTypeAnno(DataSourceEnum.primary)
    List<KettleRecordRelation> queryRecordRelations(@Param("masterUuid") String masterUuid);

    @DataSourceTypeAnno(DataSourceEnum.primary)
    void insertRecord(KettleRecord record);

    @DataSourceTypeAnno(DataSourceEnum.primary)
    void insertRecordRelations(@Param("list") List<KettleRecordRelation> recordRelations);

    @DataSourceTypeAnno(DataSourceEnum.primary)
    void updateRecord(KettleRecord record);

    @DataSourceTypeAnno(DataSourceEnum.primary)
    void updateRecordRelationID(@Param("newID") String newID, @Param("createTime") Date createTime,
	    @Param("oldID") String oldID);

    @DataSourceTypeAnno(DataSourceEnum.primary)
    void deleteRecord(@Param("uuid") String uuid);

    @DataSourceTypeAnno(DataSourceEnum.primary)
    void deleteRecordRelations(@Param("masterUUID") String masterUUID);
}
