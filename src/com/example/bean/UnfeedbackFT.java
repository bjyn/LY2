package com.example.bean;

/**
 * 未反馈故障树的Code和FeedbackCode
 * @author ynkjmacmini4
 *
 */
public class UnfeedbackFT {
	private String code;
	private String feedbackCode;
	
	
	public UnfeedbackFT(String code, String feedbackCode) {
		super();
		this.code = code;
		this.feedbackCode = feedbackCode;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getFeedbackCode() {
		return feedbackCode;
	}
	public void setFeedbackCode(String feedbackCode) {
		this.feedbackCode = feedbackCode;
	}
	
	

	

	
}
