package com.example.bean;


/**
 * 反馈页面的列表子项。包括信息和树的更新情况。
 * @author ynkjmacmini4
 *
 */
public class FeedbackListItem {
	private FTFBBaseInfo ftfbBaseInfo;
	private String downloadStatus;
	private int latestVersion;
	
	
	
	public FeedbackListItem(FTFBBaseInfo ftfbBaseInfo, String downloadStatus,
			int latestVersion) {
		super();
		this.ftfbBaseInfo = ftfbBaseInfo;
		this.downloadStatus = downloadStatus;
		this.latestVersion = latestVersion;
	}
	public int getLatestVersion() {
		return latestVersion;
	}
	public void setLatestVersion(int latestVersion) {
		this.latestVersion = latestVersion;
	}
	public FTFBBaseInfo getFtfbBaseInfo() {
		return ftfbBaseInfo;
	}
	public void setFtfbBaseInfo(FTFBBaseInfo ftfbBaseInfo) {
		this.ftfbBaseInfo = ftfbBaseInfo;
	}
	public String getDownloadStatus() {
		return downloadStatus;
	}
	public void setDownloadStatus(String downloadStatus) {
		this.downloadStatus = downloadStatus;
	}
	
	
}
