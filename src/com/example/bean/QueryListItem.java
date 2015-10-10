package com.example.bean;

/**
 * 查询页面的列表子项。包括信息和树的更新情况。
 * @author ynkjmacmini4
 *
 */
public class QueryListItem {
	private FTBaseInfo ftBaseInfo;
	private String downloadStatus;
	private int latestVersion;
	

	public QueryListItem(FTBaseInfo ftBaseInfo, String downloadStatus,
			int latestVersion) {
		super();
		this.ftBaseInfo = ftBaseInfo;
		this.downloadStatus = downloadStatus;
		this.latestVersion = latestVersion;
	}

	public FTBaseInfo getFtBaseInfo() {
		return ftBaseInfo;
	}

	public void setFtBaseInfo(FTBaseInfo ftBaseInfo) {
		this.ftBaseInfo = ftBaseInfo;
	}

	public String getDownloadStatus() {
		return downloadStatus;
	}

	public void setDownloadStatus(String downloadStatus) {
		this.downloadStatus = downloadStatus;
	}

	public int getLatestVersion() {
		return latestVersion;
	}

	public void setLatestVersion(int latestVersion) {
		this.latestVersion = latestVersion;
	}
	
	
}
