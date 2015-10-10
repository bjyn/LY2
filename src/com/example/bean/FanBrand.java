package com.example.bean;

import java.util.ArrayList;

/**
 * 风机品牌bean
 * 
 * @author steven
 * 
 */
public class FanBrand {
	private String name;// 风机品牌名称
	private String code;// 风机品牌编号
	private ArrayList<FanType> fanTypes;// 风机品牌下拥有的风机类型数组

	public FanBrand() {
		super();
	}

	/**
	 * 从数据库构造对象的方法
	 * 
	 * @param name
	 * @param code
	 */
	public FanBrand(String name, String code) {
		super();
		this.name = name;
		this.code = code;
	}

	public ArrayList<FanType> getFanTypes() {
		return fanTypes;
	}

	public void setFanTypes(ArrayList<FanType> fanTypes) {
		this.fanTypes = fanTypes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
