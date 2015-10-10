package com.example.bean;

public class OfflineFeedbacked {
	private String feedbackCode;// 可以为空
	private String code;
	private String feedbackType;
	private String handleFaultCode;
	private String description;
	private String feedbackTime;
	private String feedbackCompanyCode;
	private String feedbackWindCode;
	

	
// 当没有feedbackCode的时候，应存入“”
	

	public String getFeedbackWindCode() {
		return feedbackWindCode;
	}

	public OfflineFeedbacked(String feedbackCode, String code,
			String feedbackType, String handleFaultCode, String description,
			String feedbackTime, String feedbackCompanyCode,
			String feedbackWindCode) {
		super();
		this.feedbackCode = feedbackCode;
		this.code = code;
		this.feedbackType = feedbackType;
		this.handleFaultCode = handleFaultCode;
		this.description = description;
		this.feedbackTime = feedbackTime;
		this.feedbackCompanyCode = feedbackCompanyCode;
		this.feedbackWindCode = feedbackWindCode;
	}

	public void setFeedbackWindCode(String feedbackWindCode) {
		this.feedbackWindCode = feedbackWindCode;
	}

	public String getFeedbackCode() {
		return feedbackCode;
	}

	public void setFeedbackCode(String feedbackCode) {
		this.feedbackCode = feedbackCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getFeedbackType() {
		return feedbackType;
	}

	public void setFeedbackType(String feedbackType) {
		this.feedbackType = feedbackType;
	}

	public String getHandleFaultCode() {
		return handleFaultCode;
	}

	public void setHandleFaultCode(String handleFaultCode) {
		this.handleFaultCode = handleFaultCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFeedbackTime() {
		return feedbackTime;
	}

	public void setFeedbackTime(String feedbackTime) {
		this.feedbackTime = feedbackTime;
	}

	public String getFeedbackCompanyCode() {
		return feedbackCompanyCode;
	}

	public void setFeedbackCompanyCode(String feedbackCompanyCode) {
		this.feedbackCompanyCode = feedbackCompanyCode;
	}
	
	

}
