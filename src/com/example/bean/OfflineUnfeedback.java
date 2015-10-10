package com.example.bean;

/**
 * 离线状态下被置为未反馈的纪录
 * @author ynkjmacmini4
 *
 */
public class OfflineUnfeedback {
	private String code;
	private String time;
	
	
	public OfflineUnfeedback(String code, String time) {
		super();
		this.code = code;
		this.time = time;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	
}
