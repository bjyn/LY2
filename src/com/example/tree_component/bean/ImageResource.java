package com.example.tree_component.bean;

public class ImageResource {
	public static final int PAPER_TYPE=1;
	public static final int IMAGE_TYPE=2;
	private String code;
	private String name;
	private String url;
	private int type;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public ImageResource(String code, String name, String url, int type) {
		super();
		this.code = code;
		this.name = name;
		this.url = url;
		this.type = type;
	}

	
	
	
	
	
}
