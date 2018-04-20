package com.katch.perfer.mybatis.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.katch.perfer.mybatis.config.DataSourceEnum;
import com.katch.perfer.mybatis.config.DataSourceTypeAnno;
import com.katch.perfer.mybatis.model.RecommendItemScore;

public interface ItemWeighMapper {

	@DataSourceTypeAnno(DataSourceEnum.secondary)
	@Select("SELECT SPID_1 AS itemId,0 AS score,LRRQ AS createTime FROM SQY_RZDK_SP")
	@Results({ @Result(property = "itemId", column = "itemId", javaType = Long.class),
			@Result(property = "score", column = "score", javaType = Double.class),
			@Result(property = "createTime", column = "createTime", javaType = Date.class) })
	List<RecommendItemScore> queryAllItems();

	@DataSourceTypeAnno(DataSourceEnum.secondary)
	@Select("SELECT SPID_1 AS itemId,0 AS score,LRRQ AS createTime "
			+ "FROM SQY_RZDK_SP WHERE LRRQ IS NOT NULL ORDER BY LRRQ DESC")
	@Results({ @Result(property = "itemId", column = "itemId", javaType = Long.class),
			@Result(property = "score", column = "score", javaType = Double.class),
			@Result(property = "createTime", column = "createTime", javaType = Date.class) })
	List<RecommendItemScore> queryNewItems();

	@DataSourceTypeAnno(DataSourceEnum.secondary)
	@Select("SELECT SPID_1 AS itemId,PJFS/20 AS score FROM SQY_RZDK_SPQYGXB WHERE PJFS IS NOT NULL " + "AND QY_DM = #{qy}")
	@Results({ @Result(property = "itemId", column = "itemId", javaType = Long.class),
			@Result(property = "score", column = "score", javaType = Double.class) })
	List<RecommendItemScore> queryWeightItems(@Param("qy") String qy);

	@DataSourceTypeAnno(DataSourceEnum.secondary)
	@Select("SELECT SPID_1 AS itemId,COUNT('x') AS score FROM SQY_RZDK_DD GROUP BY SPID_1 ORDER BY score DESC")
	@Results({ @Result(property = "itemId", column = "itemId", javaType = Long.class),
			@Result(property = "score", column = "score", javaType = Double.class) })
	List<RecommendItemScore> queryTopItems();
}
