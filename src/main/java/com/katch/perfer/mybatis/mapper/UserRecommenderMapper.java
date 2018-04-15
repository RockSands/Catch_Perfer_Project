package com.katch.perfer.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.katch.perfer.mahout.model.UserRecommender;
import com.katch.perfer.mybatis.config.DataSourceEnum;
import com.katch.perfer.mybatis.config.DataSourceTypeAnno;

/**
 * @author Administrator
 *
 */
@Mapper
public interface UserRecommenderMapper {
    @DataSourceTypeAnno(DataSourceEnum.primary)
    UserRecommender queryRecommender(@Param("userID") long userID);
}
