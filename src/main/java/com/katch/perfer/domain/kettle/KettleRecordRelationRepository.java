package com.katch.perfer.domain.kettle;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface KettleRecordRelationRepository extends CrudRepository<KettleRecordRelation, Long> {
	public List<KettleRecordRelation> queryByMasterUUID(String masterUUID);
}
