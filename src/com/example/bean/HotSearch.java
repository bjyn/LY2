package com.example.bean;

import android.R.integer;

public class HotSearch {

	private FanBrand fanBrand;
	private FanType fanType;
	private integer queryCount;

	public HotSearch(FanBrand fanBrand, FanType fanType, integer queryCount) {
		super();
		this.fanBrand = fanBrand;
		this.fanType = fanType;
		this.queryCount = queryCount;
	}

	/**
	 * @return the fanBrand
	 */
	public FanBrand getFanBrand() {
		return fanBrand;
	}

	/**
	 * @param fanBrand
	 *            the fanBrand to set
	 */
	public void setFanBrand(FanBrand fanBrand) {
		this.fanBrand = fanBrand;
	}

	/**
	 * @return the fanType
	 */
	public FanType getFanType() {
		return fanType;
	}

	/**
	 * @param fanType
	 *            the fanType to set
	 */
	public void setFanType(FanType fanType) {
		this.fanType = fanType;
	}

	/**
	 * @return the queryCount
	 */
	public integer getQueryCount() {
		return queryCount;
	}

	/**
	 * @param queryCount
	 *            the queryCount to set
	 */
	public void setQueryCount(integer queryCount) {
		this.queryCount = queryCount;
	}

}
