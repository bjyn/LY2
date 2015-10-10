package com.example.tree_component.bean;

/**
 * 每个节点单击时弹出对话框中显示的name——url列表
 * 
 * @author steven
 * 
 */
public class FaultTreeURLBean {
	private String rootCode;// 所属故障树编码
	private String code;// 所属故障树节点code
	private String name;// url名称
	private String url;// url地址

	public FaultTreeURLBean() {
	}

	public FaultTreeURLBean(String rootCode, String code, String name,
			String url) {
		super();
		this.rootCode = rootCode;
		this.code = code;
		this.name = name;
		this.url = url;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getRootCode() {
		return rootCode;
	}

	public void setRootCode(String rootCode) {
		this.rootCode = rootCode;
	}

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
}
