package com.katch.perfer.service.comm;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.katch.perfer.control.RecommedRequest;
import com.katch.perfer.mybatis.mapper.ItemWeighMapper;
import com.katch.perfer.mybatis.model.RecommendItemScore;

@Service()
public class RecommendPriorityService {
	@Autowired
	protected ItemWeighMapper itemWeighMapper;

	/**
	 * 获取新的商品
	 * @param qy 
	 * 
	 * @return
	 */
	public List<RecommendItemScore> queryNewItems(String qy, int timeout) {
		Date now = new Date();
		List<RecommendItemScore> itemScores = itemWeighMapper.queryNewItems(qy);
		List<RecommendItemScore> newItems = new ArrayList<RecommendItemScore>();
		RecommendItemScore itemScore;
		for (Iterator<RecommendItemScore> it = itemScores.iterator(); it.hasNext();) {
			itemScore = it.next();
			if (itemScore.getCreateTime() == null) {
				continue;
			}
			if ((itemScore.getCreateTime().getTime() - now.getTime()) / 3600 / 1000 < timeout) {
				itemScore.setScore(10D);
				newItems.add(itemScore);
			}
		}
		return newItems;
	}

	/**
	 * 获取随机Item
	 * 
	 * @return
	 */
	public List<Long> queryAllItems(RecommedRequest request) {
		return itemWeighMapper.queryAllItemsWithQy(request.getQy(),request.getDklx());
	}
	
	/**
	 * 获取热门Item
	 * 
	 * @return
	 */
	public List<Long> queryHotSortItems(RecommedRequest request) {
		return itemWeighMapper.queryHotItems(request.getQy(),request.getDklx());
	}
	
	/**
	 * 获取TOP,Item
	 * 
	 * @return
	 */
	public List<Long> queryTopSortItems(RecommedRequest request) {
//		RecommedRequest request;
		return itemWeighMapper.queryTopItemsWithQy(request.getQy(),request.getDklx());
	}

	/**
	 * 获取所有Item
	 * 
	 * @return
	 */
	public List<RecommendItemScore> queryAllItems() {
		return itemWeighMapper.queryAllItems();
	}

	/**
	 * 获取权重
	 * 
	 * @param qy
	 * @return
	 */
	public List<RecommendItemScore> queryItemWeight(String qy) {
		return itemWeighMapper.queryWeightItems(qy);
	}
}
