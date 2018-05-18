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
	@Select("SELECT NSRSBH,SNYYE,QSYYSJ FROM TAX_ENTERPRISE_INFO WHERE NSRSBH = #{nsrsbh}")
	@Results({ @Result(property = "nsrsbh", column = "NSRSBH", javaType = String.class),
			@Result(property = "snyye", column = "SNYYE", javaType = Double.class),
			@Result(property = "qsyysj", column = "QSYYSJ", javaType = Date.class) })
	TaxEnterpriseInfo queryOne(@Param("nsrsbh") String nsrsbh);

	@DataSourceTypeAnno(DataSourceEnum.primary)
	@Insert("INSERT INTO TAX_ENTERPRISE_INFO (NSRSBH,SNYYE,QSYYSJ) VALUES(#{nsrsbh}, #{snyye}, #{qsyysj})")
	TaxEnterpriseInfo insert(TaxEnterpriseInfo taxEnterpriseInfo);

	@DataSourceTypeAnno(DataSourceEnum.primary)
	@Update("UPDATE TAX_ENTERPRISE_INFO SET SNYYE=#{snyye},QSYYSJ=#{qsyysj} WHERE NSRSBH = #{nsrsbh}")
	TaxEnterpriseInfo update(TaxEnterpriseInfo taxEnterpriseInfo);

	@DataSourceTypeAnno(DataSourceEnum.thirdary)
	@Select("SELECT T3.nsrsbh NSRSBH, t4.QSYYSJ, "
			+ "NVL (SUM (NVL(t1.ynse, 0)) + SUM (NVL(t2.ynsdse, 0)), 0 ) SNYYE "
			+ "FROM hx_sb.sb_sbb T, hx_sb.SB_SDS_JMCZ_14ND_QYSDSNDNSSBZB t1, hx_sb.SB_SDS_JMHD_YJND t2, "
			+ "dj_nsrxx t3, (SELECT nsrsbh,min(SBRQ_1) QSYYSJ FROM hx_sb.sb_sbb GROUP BY nsrsbh) t4 "
			+ "WHERE T .sbuuid = t1.sbuuid (+) AND T .sbuuid = t2.sbuuid (+) AND T .djxh = T3.djxh "
			+ "AND T.nsrsbh = t4.nsrsbh AND T .zfbz_1 = 'N' AND T .gzlx_dm_1 IN ('1', '4', '5') "
			//+ "AND T .yzpzzl_dm != 'BDA0610922' AND (T .skssqz - T .skssqq) > 300 "
			+ "AND t.SKSSQQ > add_months(sysdate,-12) AND T3.nsrsbh = #{nsrsbh} GROUP BY T3.nsrsbh,t4.QSYYSJ")
	@Results({ @Result(property = "nsrsbh", column = "NSRSBH", javaType = String.class),
			@Result(property = "snyye", column = "SNYYE", javaType = Double.class),
			@Result(property = "qsyysj", column = "QSYYSJ", javaType = Date.class) })
	TaxEnterpriseInfo queryThirdOne(@Param("nsrsbh") String nsrsbh);
}
