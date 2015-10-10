package com.example.bean;

/**
 * 风机类型bean
 * 
 * @author steven
 * 
 */
public class FanType {
	private String name;// 风机类型名称
	private String code;// 风机类型编号
	private String parentCode;// 风机所属品牌


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

	public String getBrandCode() {
		return parentCode;
	}

	public void setBrandCode(String brandCode) {
		this.parentCode = brandCode;
	}

	/**
	 * 从数据库构造对象的方法
	 * 
	 * @param name
	 * @param code
	 * @param brandCode
	 */
	public FanType(String parentCode,String name, String code) {
		super();
		this.name = name;
		this.code = code;
		this.parentCode = parentCode;
	}

	// Json解析专用setter
	public void setParentCode(String brandCode) {
		this.parentCode = brandCode;
	}
}
