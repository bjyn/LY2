package com.example.tree_component.bean;

import java.util.List;

/**
 * 故障树各层的节点
 * 
 * @author steven
 * 
 */
public class FaultTreeNode {
	public static final int REASON_LAVEL = 2;
	public static final int POINT_LEVEL = 3;
	public static final int HANDLE_LAVEL = 4;
	private String rootCode;// 所属故障树编码
	private int level;// 所属故障树层数
	private String parentCode;// 父code
	private String code;// 该节点code
	private String name;// 该节点名称
	private int comPercentage;// 公司概率
	private int groupPercentage;// 集团概率
	private List<FaultTreeNode> childCodes;// 节点的子节点
	private int locationX;// 节点的横坐标
	private OperationInstruction operationInstruction;// 作业指导书
	private PartInfo partInfo;

	public FaultTreeNode(String rootCode, int level, String parentCode,
			String code, String name, int comPercentage, int groupPercentage,
			List<FaultTreeNode> childCodes,
			OperationInstruction operationInstruction, PartInfo partInfo) {
		super();
		this.rootCode = rootCode;
		this.level = level;
		this.parentCode = parentCode;
		this.code = code;
		this.name = name;
		this.comPercentage = comPercentage;
		this.groupPercentage = groupPercentage;
		this.childCodes = childCodes;
		this.operationInstruction = operationInstruction;
		this.partInfo = partInfo;
	}

	public PartInfo getPartInfo() {
		return partInfo;
	}

	public void setPartInfo(PartInfo partInfo) {
		this.partInfo = partInfo;
	}

	public int getComPercentage() {
		return comPercentage;
	}

	public void setComPercentage(int comPercentage) {
		this.comPercentage = comPercentage;
	}

	public int getGroupPercentage() {
		return groupPercentage;
	}

	public void setGroupPercentage(int groupPercentage) {
		this.groupPercentage = groupPercentage;
	}

	public OperationInstruction getOperationInstruction() {
		return operationInstruction;
	}

	public void setOperationInstruction(
			OperationInstruction operationInstruction) {
		this.operationInstruction = operationInstruction;
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
