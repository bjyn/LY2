package com.example.bean;


/**
 * 用户信息
 * @author ynkjmacmini4
 *
 */
public class UserInfo {
	private String userCode;
	private String userPassword;
	private String userName;
	private String userLastTime;
	private int userCurrentUnfeedbackCount;
	private int userLimitUnfeedbackCount;
	private String organization;
	
	
	
	public UserInfo(String userCode, String userPassword, String userName,
			String userLastTime, int userCurrentUnfeedbackCount,
			int userLimitUnfeedbackCount, String organization) {
		super();
		this.userCode = userCode;
		this.userPassword = userPassword;
		this.userName = userName;
		this.userLastTime = userLastTime;
		this.userCurrentUnfeedbackCount = userCurrentUnfeedbackCount;
		this.userLimitUnfeedbackCount = userLimitUnfeedbackCount;
		this.organization = organization;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserLastTime() {
		return userLastTime;
	}
	public void setUserLastTime(String userLastTime) {
		this.userLastTime = userLastTime;
	}
	public int getUserCurrentUnfeedbackCount() {
		return userCurrentUnfeedbackCount;
	}
	public void setUserCurrentUnfeedbackCount(int userCurrentUnfeedbackCount) {
		this.userCurrentUnfeedbackCount = userCurrentUnfeedbackCount;
	}
	public int getUserLimitUnfeedbackCount() {
		return userLimitUnfeedbackCount;
	}
	public void setUserLimitUnfeedbackCount(int userLimitUnfeedbackCount) {
		this.userLimitUnfeedbackCount = userLimitUnfeedbackCount;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	
	
	  
}
