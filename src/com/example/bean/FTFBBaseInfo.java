package com.example.bean;

import java.io.Serializable;

/**
 * 未反馈状态的故障树的基本信息
 * 
 * @author steven
 * 
 */
public class FTFBBaseInfo implements Serializable {

	private static final long serialVersionUID = -5468863700092723280L;
	private String feedbackCode;// 故障树反馈code，是反馈与与未反馈记录的主键
	private String code;// 故障树编码
	private String mainFaultCode;// 主故障编码
	private String followFaultCode;// 伴随故障编码
	private String chineseName;// 故障中文名称
	private String englishName;// 故障英文名称
	private FanBrand fanBrand;// 风机品牌
	private FanType fanType;// 风机类型
	private String feedbackStatus;// 反馈状态，即为json中的feedbacktype  反馈／未反馈
	private String checkStatus;// 反馈结果 未审核／未采纳／已采纳
	private String triggerCondition;// 触发条件
	private String faultPhe;// 故障现象
	private int version;// 版本号
	private String remark;// 备注
	private String lookTime;// 将故障树标记为未反馈时由服务器生成，在故障反馈查询时作为排序依据。
	private String feedbackTime;// 反馈时间
	private int proVersion;// 故障树反馈编码，只有在状态被置为未反馈时才会得到
	private Company company;
	private Wind wind;
	private String createUser;
	private String createTime;
	private String handleMethodType;
	private String systemMethod;
	private String newMethodDes;
	
	public FTFBBaseInfo(String feedbackCode, String code, String mainFaultCode,
			String followFaultCode, String chineseName, String englishName,
			FanBrand fanBrand, FanType fanType, String feedbackStatus,
			String checkStatus, String triggerCondition, String faultPhe,
			int version, String remark, String lookTime, String feedbackTime,
			int proVersion, Company company, Wind wind, String createUser,
			String createTime, String handleMethodType, String systemMethod,
			String newMethodDes) {
		super();
		this.feedbackCode = feedbackCode;
		this.code = code;
		this.mainFaultCode = mainFaultCode;
		this.followFaultCode = followFaultCode;
		this.chineseName = chineseName;
		this.englishName = englishName;
		this.fanBrand = fanBrand;
		this.fanType = fanType;
		this.feedbackStatus = feedbackStatus;
		this.checkStatus = checkStatus;
		this.triggerCondition = triggerCondition;
		this.faultPhe = faultPhe;
		this.version = version;
		this.remark = remark;
		this.lookTime = lookTime;
		this.feedbackTime = feedbackTime;
		this.proVersion = proVersion;
		this.company = company;
		this.wind = wind;
		this.createUser = createUser;
		this.createTime = createTime;
		this.handleMethodType = handleMethodType;
		this.systemMethod = systemMethod;
		this.newMethodDes = newMethodDes;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getFeedbackStatus() {
		return feedbackStatus;
	}

	public void setFeedbackStatus(String feedbackStatus) {
		this.feedbackStatus = feedbackStatus;
	}


	public String getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}

	public int getProVersion() {
		return proVersion;
	}

	public void setProVersion(int proVersion) {
		this.proVersion = proVersion;
	}

	public String getFeedbackCode() {
		return feedbackCode;
	}

	public void setFeedbackCode(String feedbackCode) {
		this.feedbackCode = feedbackCode;
	}

	public String getLookTime() {
		return lookTime;
	}

	public void setLookTime(String lookTime) {
		this.lookTime = lookTime;
	}

	public String getFeedbackTime() {
		return feedbackTime;
	}

	public void setFeedbackTime(String feedbackTime) {
		this.feedbackTime = feedbackTime;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Wind getWind() {
		return wind;
	}

	public void setWind(Wind wind) {
		this.wind = wind;
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

	public String getHandleMethodType() {
		return handleMethodType;
	}

	public void setHandleMethodType(String handleMethodType) {
		this.handleMethodType = handleMethodType;
	}

	public String getSystemMethod() {
		return systemMethod;
	}

	public void setSystemMethod(String systemMethod) {
		this.systemMethod = systemMethod;
	}

	public String getNewMethodDes() {
		return newMethodDes;
	}

	public void setNewMethodDes(String newMethodDes) {
		this.newMethodDes = newMethodDes;
	}

	
}
