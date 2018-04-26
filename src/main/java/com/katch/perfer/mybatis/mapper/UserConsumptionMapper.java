package com.katch.perfer.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.katch.perfer.mybatis.config.DataSourceEnum;
import com.katch.perfer.mybatis.config.DataSourceTypeAnno;
import com.katch.perfer.mybatis.model.UserConsumption;

@Mapper
public interface UserConsumptionMapper {
	@DataSourceTypeAnno(DataSourceEnum.secondary)
	List<UserConsumption> queryUserConsumptions(@Param("userID") long userID, @Param("qy")String qy);
}
