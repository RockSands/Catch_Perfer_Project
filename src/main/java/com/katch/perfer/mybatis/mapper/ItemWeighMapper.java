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
	@Select("SELECT SPID_1 AS itemId,0 AS score,LRRQ AS createTime FROM SQY_RZDK_SP "
			+ "WHERE YXBZ = 'Y' AND NVL (XYBZ, 'Y') = 'Y' AND PX IS NULL")
	@Results({ @Result(property = "itemId", column = "itemId", javaType = Long.class),
			@Result(property = "score", column = "score", javaType = Double.class),
			@Result(property = "createTime", column = "createTime", javaType = Date.class) })
	List<RecommendItemScore> queryAllItems();

	@DataSourceTypeAnno(DataSourceEnum.secondary)
	@Select("SELECT * FROM (SELECT sp.SPID_1 AS itemId,0 AS score,spqy.LRRQ AS createTime "
			+ "FROM SQY_RZDK_SP sp, SQY_RZDK_SPQYGXB spqy "
			+ "WHERE sp.SPID_1 = spqy.SPID_1 AND spqy.LRRQ IS NOT NULL AND sp.YXBZ = 'Y' "
			+ "AND NVL (sp.XYBZ, 'Y') = 'Y' AND sp.PX IS NULL AND spqy.QY_DM = #{qy} "
			+ "ORDER BY spqy.LRRQ DESC ) where rownum < 101")
	@Results({ @Result(property = "itemId", column = "itemId", javaType = Long.class),
			@Result(property = "score", column = "score", javaType = Double.class),
			@Result(property = "createTime", column = "createTime", javaType = Date.class) })
	List<RecommendItemScore> queryNewItems(@Param("qy") String qy);

	@DataSourceTypeAnno(DataSourceEnum.secondary)
	@Select("SELECT spqy.SPID_1 AS itemId,spqy.PJFS/20 AS score FROM SQY_RZDK_SPQYGXB spqy "
			+ "INNER JOIN SQY_RZDK_SP sp ON spqy.spid_1 = sp.spid_1 AND sp.YXBZ = 'Y' AND NVL (sp.XYBZ, 'Y') = 'Y' "
			+ "WHERE spqy.PJFS IS NOT NULL AND sp.PX IS NULL AND spqy.QY_DM = #{qy} ORDER BY score DESC")
	@Results({ @Result(property = "itemId", column = "itemId", javaType = Long.class),
			@Result(property = "score", column = "score", javaType = Double.class) })
	List<RecommendItemScore> queryWeightItems(@Param("qy") String qy);

	@DataSourceTypeAnno(DataSourceEnum.secondary)
	@Select("SELECT sp.SPID_1 AS itemId FROM SQY_RZDK_SP sp,SQY_RZDK_SPQYGXB spqy "
			+ "WHERE SP.SPID_1 = spqy.SPID_1 AND sp.YXBZ = 'Y' AND NVL (sp.XYBZ, 'Y') = 'Y' "
			+ "AND sp.PX IS NULL  AND spqy.QY_DM = #{qy} ORDER BY TRUNC (dbms_random. VALUE(0, 1000))")
	@Results({ @Result(property = "itemId", column = "itemId", javaType = Long.class) })
	List<Long> queryAllRandomSortItems(@Param("qy") String qy);

	@DataSourceTypeAnno(DataSourceEnum.secondary)
	@Select("SELECT sp.SPID_1 AS itemId FROM SQY_RZDK_SP sp,SQY_RZDK_SPQYGXB spqy "
			+ "WHERE SP.SPID_1 = spqy.SPID_1 AND sp.YXBZ = 'Y' AND NVL (sp.XYBZ, 'Y') = 'Y' "
			+ "<if test=''2' eq dklx'>" + "AND sp.dklx = #{dklx}" + "</if>"
			+ "AND sp.PX IS NULL AND spqy.QY_DM = #{qy}")
	@Results({ @Result(property = "itemId", column = "itemId", javaType = Long.class) })
	List<Long> queryAllItemsWithQy(@Param("qy") String qy, @Param("dklx") String dklx);
	
	@DataSourceTypeAnno(DataSourceEnum.secondary)
	@Select("SELECT dd.SPID_1 AS itemId,count('x') score FROM SQY_RZDK_DD dd "
			+ "INNER JOIN SQY_RZDK_SPQYGXB spqy ON dd.SPID_1 = spqy.SPID_1 AND spqy.QY_DM = #{qy} "
			+ "INNER JOIN SQY_RZDK_SP sp ON spqy.spid_1 = sp.spid_1 AND sp.PX IS NULL AND sp.YXBZ = 'Y' AND NVL (sp.XYBZ, 'Y') = 'Y' "
			+ "<if test=''2' eq dklx'>" + "AND sp.dklx = #{dklx}" + "</if>"
			+ "GROUP BY dd.SPID_1 ORDER BY score DESC")
	@Results({ @Result(property = "itemId", column = "itemId", javaType = Long.class) })
	List<Long> queryHotItems(@Param("qy") String qy, @Param("dklx") String dklx);
	
	@DataSourceTypeAnno(DataSourceEnum.secondary)
	@Select("SELECT sp.SPID_1 AS itemId FROM SQY_RZDK_SP sp,SQY_RZDK_SPQYGXB spqy "
			+ "WHERE SP.SPID_1 = spqy.SPID_1 AND sp.YXBZ = 'Y' AND NVL (sp.XYBZ, 'Y') = 'Y' " + "AND sp.PX IS NOT NULL "
			+ "<if test=''2' eq dklx'>" + "AND sp.dklx = #{dklx}" + "</if>" + "AND spqy.QY_DM = #{qy} ORDER BY sp.PX DESC")
	@Results({ @Result(property = "itemId", column = "itemId", javaType = Long.class) })
	List<Long> queryTopItemsWithQy(@Param("qy") String qy, @Param("dklx") String dklx);


	@DataSourceTypeAnno(DataSourceEnum.secondary)
	@Select(" SELECT sp.SPID_1 AS itemId "
			+ " FROM SQY_RZDK_SP sp,SQY_RZDK_SPQYGXB spqy " 
			+ " WHERE sp.SPID_1 = spqy.SPID_1 AND sp.DKLX = '1'")
	@Results({ @Result(property = "itemId", column = "itemId", javaType = Long.class) })
	List<Long> querySpidsByDklxEqual1();

}
