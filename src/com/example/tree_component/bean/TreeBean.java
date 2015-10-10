package com.example.tree_component.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 树模型
 * 
 * @author ynkjmacmini4
 * 
 */
public class TreeBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private TreeRoot rootBean;
	private List<FaultTreeNode> faultReasonNodes;
	private List<FaultTreeNode> reasonCheckNodes;
	private List<FaultTreeNode> handleFaultNodes;

	public TreeBean(TreeRoot rootBean, List<FaultTreeNode> faultReasonNodes,
			List<FaultTreeNode> reasonCheckNodes,
			List<FaultTreeNode> handleFaultNodes) {
		super();
		this.rootBean = rootBean;
		this.faultReasonNodes = faultReasonNodes;
		this.reasonCheckNodes = reasonCheckNodes;
		this.handleFaultNodes = handleFaultNodes;
	}

	public List<FaultTreeNode> getFaultReasonNodes() {
		return faultReasonNodes;
	}

	public void setFaultReasonNodes(List<FaultTreeNode> faultReasonNodes) {
		this.faultReasonNodes = faultReasonNodes;
	}

	public List<FaultTreeNode> getReasonCheckNodes() {
		return reasonCheckNodes;
	}

	public void setReasonCheckNodes(List<FaultTreeNode> reasonCheckNodes) {
		this.reasonCheckNodes = reasonCheckNodes;
	}

	public List<FaultTreeNode> getHandleFaultNodes() {
		return handleFaultNodes;
	}

	public void setHandleFaultNodes(List<FaultTreeNode> handleFaultNodes) {
		this.handleFaultNodes = handleFaultNodes;
	}

	public TreeRoot getRootBean() {
		return rootBean;
	}

	public void setRootBean(TreeRoot rootBean) {
		this.rootBean = rootBean;
	}

}
