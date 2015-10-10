package com.example.tree_component.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 故障树各层的节点
 * 
 * @author steven
 * 
 */
public class FaultTreeNode {
	private List<FaultTreeURLBean> urls;// 存储一个节点的所有url
	private String rootCode;// 所属故障树编码
	private int level;// 所属故障树层数
	private String parentCode;// 父code
	private String code;// 该节点code
	private String name;// 该节点名称
	private int percentage;// 该节点概率
	private List<FaultTreeNode> childCodes;// 节点的子节点
	private int locationX;// 节点的横坐标
	
	
	
	
	
public FaultTreeNode(List<FaultTreeURLBean> urls, String rootCode,
			int level, String parentCode, String code, String name,
			int percentage) {
		super();
		this.urls = urls;
		this.rootCode = rootCode;
		this.level = level;
		this.parentCode = parentCode;
		this.code = code;
		this.name = name;
		this.percentage = percentage;
	}

	public List<FaultTreeURLBean> getUrls() {
		return urls;
	}

	public void setUrls(List<FaultTreeURLBean> urls) {
		this.urls = urls;
	}

	public String getRootCode() {
		return rootCode;
	}

	public void setRootCode(String rootCode) {
		this.rootCode = rootCode;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = Integer.parseInt(level);
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public List<FaultTreeNode> getChildCodes() {
		return childCodes;
	}

	public void setChildCodes(List<FaultTreeNode> childCodes) {
		this.childCodes = childCodes;
	}

	public int getLocationX() {
		return locationX;
	}

	public void setLocationX(int locationX) {
		this.locationX = locationX;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
