package com.example.tree_component.bean;

import java.util.List;

public class TreeRoot {
	private String code;// 故障树编码
	private String version;// 故障树版本
	private String mainFaultCode;// 主故障代码
	private String followFaultCode;// 伴随故障代码
	private String ChineseName;// 中文名称
	private String EnglishName;// 英文名称
	private String triggerCondition;// 故障触发条件
	private int locationX;// 节点的横坐标
	private List<FaultTreeNode> childCodes;// 第三行节点数组

	/**
	 * 从数据库中加载对象的构造方法
	 * 
	 * @param code
	 * @param version
	 * @param mainFaultCode
	 * @param followFaultCode
	 * @param chineseName
	 * @param englishName
	 * @param triggerCondition
	 */
	public TreeRoot(String code, String version, String mainFaultCode,
			String followFaultCode, String chineseName, String englishName,
			String triggerCondition) {
		super();
		this.code = code;
		this.version = version;
		this.mainFaultCode = mainFaultCode;
		this.followFaultCode = followFaultCode;
		ChineseName = chineseName;
		EnglishName = englishName;
		this.triggerCondition = triggerCondition;
	}

	public List<FaultTreeNode> getChildCodes() {
		return childCodes;
	}

	public void setChildCodes(List<FaultTreeNode> childCodes) {
		this.childCodes = childCodes;
	}


	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getMainFaultCode() {
		return mainFaultCode;
	}

	public void setMainFaultCode(String mainFaultCode) {
		this.mainFaultCode = mainFaultCode;
	}

	public String getFollowFaultCode() {
		return followFaultCode;
	}

	public void setFollowFaultCode(String followFaultCode) {
		this.followFaultCode = followFaultCode;
	}

	public String getChineseName() {
		return ChineseName;
	}

	public void setChineseName(String chineseName) {
		ChineseName = chineseName;
	}

	public String getEnglishName() {
		return EnglishName;
	}

	public void setEnglishName(String englishName) {
		EnglishName = englishName;
	}

	public String getTriggerCondition() {
		return triggerCondition;
	}

	public void setTriggerCondition(String triggerCondition) {
		this.triggerCondition = triggerCondition;
	}

	public int getLocationX() {
		return locationX;
	}

	public void setLocationX(int locationX) {
		this.locationX = locationX;
	}

	// json专用setter
	public void setCName(String chineseName) {
		ChineseName = chineseName;
	}

	public void setEName(String englishName) {
		EnglishName = englishName;
	}

}
