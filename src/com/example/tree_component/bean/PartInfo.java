package com.example.tree_component.bean;

import java.util.List;


public class PartInfo  {
	
	private String baseInfo;
	private List<ImageResource> papers;
	private List<ImageResource> images;
	
	
	public PartInfo(String baseInfo, List<ImageResource> papers,
			List<ImageResource> images) {
		super();
		this.baseInfo = baseInfo;
		this.papers = papers;
		this.images = images;
	}
	public String getBaseInfo() {
		return baseInfo;
	}
	public void setBaseInfo(String baseInfo) {
		this.baseInfo = baseInfo;
	}
	public List<ImageResource> getPapers() {
		return papers;
	}
	public void setPapers(List<ImageResource> papers) {
		this.papers = papers;
	}
	public List<ImageResource> getImages() {
		return images;
	}
	public void setImages(List<ImageResource> images) {
		this.images = images;
	}
	
	
}
