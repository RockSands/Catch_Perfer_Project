package com.katch.perfer.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.katch.perfer.mybatis.config.DataSourceEnum;
import com.katch.perfer.mybatis.config.DataSourceTypeAnno;
import com.katch.perfer.mybatis.model.LoanConditionDefine;

public interface LoanConditionsMapper {
	@DataSourceTypeAnno(DataSourceEnum.secondary)
	@Select("SELECT TJLX_1 type,VALUE val FROM SQY_RZDK_SPSQTJ WHERE YXBZ = 'Y' AND SPID_1 = #{spid}")
	@Results({ @Result(property = "type", column = "type", javaType = String.class),
			@Result(property = "val", column = "val", javaType = String.class) })
	List<LoanConditionDefine> queryLoanCondition(@Param("spid") long spid);
}
