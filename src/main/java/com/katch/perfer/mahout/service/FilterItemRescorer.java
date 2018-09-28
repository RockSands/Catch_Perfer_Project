package com.katch.perfer.mahout.service;

import java.util.Collection;

import org.apache.mahout.cf.taste.recommender.Rescorer;
import org.apache.mahout.common.LongPair;

/**
 * 过滤ItemID
 */
public class FilterItemRescorer implements Rescorer<LongPair> {

	private Collection<Long> excludeItemIDs;

	public FilterItemRescorer(Collection<Long> excludeItemIDs) {
		this.excludeItemIDs = excludeItemIDs;
	}

	@Override
	public double rescore(LongPair thing, double originalScore) {
		return isFiltered(thing) ? Double.NaN : originalScore;
	}

	@Override
	public boolean isFiltered(LongPair thing) {
		return excludeItemIDs.contains(thing.getFirst()) || excludeItemIDs.contains(thing.getSecond());
	}
}
