package com.example.singleton;

import java.util.ArrayList;
import java.util.List;

import com.example.bean.Company;
import com.example.bean.FanBrand;
import com.example.bean.FanType;
import com.example.bean.UnfeedbackFT;

/**
 * 程序运行过程中保存用户相关信息的单例
 * 
 * @author steven
 * 
 */
public final class UserSingleton {
	private String validateToken;// 身份令牌
	private String userName;// 用户姓名
	private String organization;
	private int unFeedbackNumber;// 未反馈的条目数
	private int limitFeedbackNumber;// 未反馈的限制条目数
	private List<FanBrand> fanBrands = new ArrayList<FanBrand>();// 品牌列表
	private List<FanType> fanTypes = new ArrayList<FanType>();// 风机列表
	private List<Company> companies = new ArrayList<Company>();// 项目公司列表
	private List<UnfeedbackFT> unfeedbackFTs = new ArrayList<UnfeedbackFT>();// 未反馈列表
	private static UserSingleton userSingleton;

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public List<Company> getCompanies() {
		return companies;
	}

	public void setCompanies(List<Company> companies) {
		this.companies = companies;
	}

	public String getValidateToken() {
		return validateToken;
	}

	public void setValidateToken(String validateToken) {
		this.validateToken = validateToken;
	}

	public List<FanBrand> getFanBrands() {
		return fanBrands;
	}

	public void setFanBrands(List<FanBrand> fanBrands) {
		this.fanBrands = fanBrands;
	}

	public List<FanType> getFanTypes() {
		return fanTypes;
	}

	public void setFanTypes(List<FanType> fanTypes) {
		this.fanTypes = fanTypes;
	}

	public List<UnfeedbackFT> getUnfeedbackFTs() {
		return unfeedbackFTs;
	}

	public void setUnfeedbackFTs(List<UnfeedbackFT> unfeedbackFTs) {
		this.unfeedbackFTs = unfeedbackFTs;
	}

	private UserSingleton() {
		// TODO TEst
		// TODO TEst
		fanBrands.add(new FanBrand("华创", "hua"));
		fanBrands.get(0).setFanTypes((ArrayList<FanType>) fanTypes);
	}

	public static UserSingleton getInstance() {
		if (userSingleton == null)
			userSingleton = new UserSingleton();
		return userSingleton;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getUnFeedbackNumber() {
		return unFeedbackNumber;
	}

	public void setUnFeedbackNumber(int unFeedbackNumber) {
		this.unFeedbackNumber = unFeedbackNumber;
	}

	public int getLimitFeedbackNumber() {
		return limitFeedbackNumber;
	}

	public void setLimitFeedbackNumber(int limitFeedbackNumber) {
		this.limitFeedbackNumber = limitFeedbackNumber;
	}

	/**
	 * 判断是否可以继续查询
	 * 
	 * @return 返回true为可查询
	 */
	public boolean isSearchable() {
		return limitFeedbackNumber > unFeedbackNumber;
	}

}
