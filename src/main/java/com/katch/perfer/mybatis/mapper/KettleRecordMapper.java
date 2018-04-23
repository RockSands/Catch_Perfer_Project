package com.katch.perfer.mybatis.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.katch.perfer.mybatis.model.KettleRecord;
import com.katch.perfer.mybatis.model.KettleRecordRelation;

@Mapper
public interface KettleRecordMapper {
    KettleRecord queryRecord(@Param("uuid") String uuid);

    List<KettleRecordRelation> queryRecordRelations(@Param("masterUuid") String masterUuid);
    
    List<KettleRecord> allUnassignedRecords();
    
	List<KettleRecord> allCanDelRecords();
    
    List<KettleRecord> queryRunningRecordsByHostName(@Param("hostname")String hostname);
    
    List<KettleRecord> queryApplyRecordsByHostName(@Param("hostname")String hostname);
    
    int insertRecord(KettleRecord record);

    int insertRecordRelations(@Param("list") List<KettleRecordRelation> recordRelations);

    int updateRecord(KettleRecord record);
    
    int assignedRecord(KettleRecord record);

    int updateRecordRelationID(@Param("newID") String newID, @Param("createTime") Date createTime,
	    @Param("oldID") String oldID);

    int deleteRecord(@Param("uuid") String uuid);

    int deleteRecordRelations(@Param("masterUUID") String masterUUID);

}
