package com.katch.perfer.mybatis.mapper;

import java.util.List;

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

    void insertRecommender(@Param("userID") long userID);
    
    void deleteRecommenders(@Param("list")List<UserRecommender> recommenders);

    void insertRecommenders(@Param("list")List<UserRecommender> recommenders);
}
