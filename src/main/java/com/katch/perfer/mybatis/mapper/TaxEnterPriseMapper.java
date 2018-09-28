package com.katch.perfer.mybatis.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.katch.perfer.mybatis.config.DataSourceEnum;
import com.katch.perfer.mybatis.config.DataSourceTypeAnno;
import com.katch.perfer.mybatis.model.TaxEnterpriseInfo;

public interface TaxEnterPriseMapper {

	@DataSourceTypeAnno(DataSourceEnum.primary)
	@Select("SELECT DJXH,SNYYE,QSYYSJ FROM TAX_ENTERPRISE_INFO WHERE djxh = #{djxh}")
	@Results({ @Result(property = "djxh", column = "DJXH", javaType = String.class),
			@Result(property = "snyye", column = "SNYYE", javaType = Double.class),
			@Result(property = "qsyysj", column = "QSYYSJ", javaType = Date.class) })
	TaxEnterpriseInfo queryOne(@Param("djxh") String djxh);

	@DataSourceTypeAnno(DataSourceEnum.primary)
	@Insert("INSERT INTO TAX_ENTERPRISE_INFO (DJXH,SNYYE,QSYYSJ) VALUES(#{djxh}, #{snyye}, #{qsyysj})")
	TaxEnterpriseInfo insert(TaxEnterpriseInfo taxEnterpriseInfo);

	@DataSourceTypeAnno(DataSourceEnum.primary)
	@Update("UPDATE TAX_ENTERPRISE_INFO SET SNYYE=#{snyye},QSYYSJ=#{qsyysj} WHERE DJXH = #{djxh}")
	TaxEnterpriseInfo update(TaxEnterpriseInfo taxEnterpriseInfo);

	@DataSourceTypeAnno(DataSourceEnum.thirdary)
	@Select("SELECT 	T3.djxh DJXH, 	t4.QSYYSJ, 	"
			+ "NVL ( SUM( NVL ( t1.ynse, 0 ) ) + SUM( NVL ( t2.ynsdse, 0 ) ), 0 ) SNYYE "
			+ "FROM 	hx_sb.sb_sbb T, 	hx_sb.SB_SDS_JMCZ_14ND_QYSDSNDNSSBZB t1, 	"
			+ "hx_sb.SB_SDS_JMHD_YJND t2, 	dj_nsrxx t3, 	"
			+ "( SELECT djxh, min( SBRQ_1 ) QSYYSJ FROM hx_sb.sb_sbb GROUP BY djxh ) t4 "
			+ "WHERE 	T.sbuuid = t1.sbuuid ( + ) 	AND T.sbuuid = t2.sbuuid ( + ) 	"
			+ "AND T.djxh = T3.djxh 	AND T.djxh = t4.djxh 	AND T.zfbz_1 = 'N' 	"
			+ "AND T.gzlx_dm_1 IN ( '1', '4', '5' ) 	AND T.yzpzzl_dm != 'BDA0610922' 	"
			+ "AND ( T.skssqz - T.skssqq ) > 300 	AND t.SKSSQQ > add_months ( sysdate,- 12 ) 	"
			+ "AND T3.djxh = #{djxh} GROUP BY 	T3.djxh, 	t4.QSYYSJ")
	@Results({ @Result(property = "djxh", column = "DJXH", javaType = String.class),
			@Result(property = "snyye", column = "SNYYE", javaType = Double.class),
			@Result(property = "qsyysj", column = "QSYYSJ", javaType = Date.class) })
	TaxEnterpriseInfo queryThirdOne(@Param("djxh") String djxh);
}
