package com.katch.perfer.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.katch.perfer.mahout.model.UserRecommender;

/**
 * @author Administrator
 *
 */
@Mapper
public interface UserRecommenderMapper {

	UserRecommender queryRecommender(@Param("userID") long userID);

    void deleteRecommender(@Param("userID") long userID);

    void insertRecommender(UserRecommender userRecommender);
}
