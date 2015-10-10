package com.example.bean;

import java.io.Serializable;

/**
 * 故障树的基本信息
 * 
 * @author steven
 * 
 */
public class FTBaseInfo implements Serializable {
	private static final long serialVersionUID = -1165879243662073469L;
	private String code;// 故障树编码
	private String mainFaultCode;// 主故障编码
	private String followFaultCode;// 伴随故障编码
	private String chineseName;// 故障中文名称
	private String englishName;// 故障英文名称
	private FanBrand fanBrand;// 风机品牌
	private FanType fanType;// 风机类型
	private String triggerCondition;// 触发条件
	private String faultPhe;// 故障现象
	private int version;// 基本版本
	private String remark;// 备注
	private int proVersion;// 详细信息版本
	private String createUser;
	private String createTime;
	private String modifyTime;

	public FTBaseInfo() {
	}

	// 获取故障树基本信息

	public FTBaseInfo(String code, String mainFaultCode,
			String followFaultCode, String chineseName, String englishName,
			FanBrand fanBrand, FanType fanType, String triggerCondition,
			String faultPhe, int version, String remark, int proVersion,
			String createUser, String createTime, String modifyTime) {
		super();
		this.code = code;
		this.mainFaultCode = mainFaultCode;
		this.followFaultCode = followFaultCode;
		this.chineseName = chineseName;
		this.englishName = englishName;
		this.fanBrand = fanBrand;
		this.fanType = fanType;
		this.triggerCondition = triggerCondition;
		this.faultPhe = faultPhe;
		this.version = version;
		this.remark = remark;
		this.proVersion = proVersion;
		this.createUser = createUser;
		this.createTime = createTime;
		this.modifyTime = modifyTime;
		this.fanBrand = new FanBrand(fanBrand.getName(), fanBrand.getCode());
		this.fanType = new FanType(fanBrand.getCode(), fanType.getName(),
				fanType.getCode());
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public FanBrand getFanBrand() {
		return fanBrand;
	}

	public void setFanBrand(FanBrand fanBrand) {
		this.fanBrand = fanBrand;
	}

	public FanType getFanType() {
		return fanType;
	}

	public void setFanType(FanType fanType) {
		this.fanType = fanType;
	}

	public String getTriggerCondition() {
		return triggerCondition;
	}

	public void setTriggerCondition(String triggerCondition) {
		this.triggerCondition = triggerCondition;
	}

	public String getFaultPhe() {
		return faultPhe;
	}

	public void setFaultPhe(String faultPhe) {
		this.faultPhe = faultPhe;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	// JSon解析专用setter
	public void setCName(String chineseName) {
		this.chineseName = chineseName;
	}

	public void setEName(String englishName) {
		this.englishName = englishName;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getProVersion() {
		return proVersion;
	}

	public void setProVersion(int proVersion) {
		this.proVersion = proVersion;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}

}
