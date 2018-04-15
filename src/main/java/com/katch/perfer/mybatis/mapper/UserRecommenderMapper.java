package com.katch.perfer.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.katch.perfer.mybatis.model.UserRecommender;

/**
 * @author Administrator
 *
 */
@Mapper
public interface UserRecommenderMapper {
    UserRecommender queryRecommender(@Param("userID") long userID);
}
