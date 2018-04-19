package com.katch.perfer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RecommendRestProperties {
	@Value("${recommend.return.size}")
	private int returnSize;

	@Value("${recommend.weight.size}")
	private int weightSize;

	@Value("${recommend.new.item.open}")
	private boolean newItemOpen;

	@Value("${recommend.new.item.weight.score}")
	private double newItemWeight;

	@Value("${recommend.new.item.size}")
	private int newItemSize;

	@Value("${recommend.new.item.time.out}")
	private int newItemTimeOut;

	public int getReturnSize() {
		return returnSize;
	}

	public void setReturnSize(int returnSize) {
		this.returnSize = returnSize;
	}

	public int getWeightSize() {
		return weightSize;
	}

	public void setWeightSize(int weightSize) {
		this.weightSize = weightSize;
	}

	public boolean isNewItemOpen() {
		return newItemOpen;
	}

	public void setNewItemOpen(boolean newItemOpen) {
		this.newItemOpen = newItemOpen;
	}

	public double getNewItemWeight() {
		return newItemWeight;
	}

	public void setNewItemWeight(double newItemWeight) {
		this.newItemWeight = newItemWeight;
	}

	public int getNewItemSize() {
		return newItemSize;
	}

	public void setNewItemSize(int newItemSize) {
		this.newItemSize = newItemSize;
	}

	public int getNewItemTimeOut() {
		return newItemTimeOut;
	}

	public void setNewItemTimeOut(int newItemTimeOut) {
		this.newItemTimeOut = newItemTimeOut;
	}
}
