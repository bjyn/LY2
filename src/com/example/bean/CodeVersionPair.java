package com.example.bean;


/**
 * 存放从网络接口传回的最新详情版本。
 * @author ynkjmacmini4
 *
 */
public class CodeVersionPair {
	private String code;
	private int version;// 故障树版本
	private int proVersion;// 概率版本
	public CodeVersionPair(String code, int version, int proVersion) {
		super();
		this.code = code;
		this.version = version;
		this.proVersion = proVersion;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
	
	
	
}
