package com.katch.perfer.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.katch.perfer.mahout.model.Recommender;

/**
 * @author Administrator
 *
 */
@Mapper
public interface RecommenderMapper {

    Recommender queryRecommender(@Param("userID") long userID);

    void deleteRecommender(@Param("userID") long userID);

    void insertRecommender(Recommender recommenders);
}
