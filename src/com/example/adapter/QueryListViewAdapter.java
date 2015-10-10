package com.example.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.activity.FaultTreeInfoActivity;
import com.example.bean.FTBaseInfo;
import com.example.bean.QueryListItem;
import com.example.common.HttpUtils;
import com.example.common.HttpUtils.FTDownloadListener;
import com.example.lyfaultdiagnosissystem.R;
import com.example.tree_component.bean.TreeBean;

/**
 * 查询页面的适配器
 * 
 * @author ynkjmacmini4
 * 
 */
public class QueryListViewAdapter extends BaseAdapter {
	public static final String STATUS_DOWNLOADED = "已下载";
	public static final String STATUS_TO_UPDATE = "可更新";
	public static final String STATUS_TO_DOWNLOAD = "下载";
	private List<QueryListItem> queryListItems;
	private Context context;
	private HttpUtils httpUtils;
	private Handler handler;

	public QueryListViewAdapter(List<QueryListItem> queryListItems,
			Context context, Handler handler) {
		super();
		this.queryListItems = queryListItems;
		this.context = context;
		this.handler = handler;
		this.httpUtils = new HttpUtils(context);
	}

	@Override
	public int getCount() {
		return queryListItems.size();
	}

	@Override
	public Object getItem(int position) {
		return queryListItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(
				R.layout.item_fault_tree_query, null);
		TextView mainFaulTextView = (TextView) linearLayout
				.findViewById(R.id.main_fault_code_tv);
		TextView followFaultTextView = (TextView) linearLayout
				.findViewById(R.id.follow_fault_code_tv);
		TextView chineseNameTextView = (TextView) linearLayout
				.findViewById(R.id.chinese_name_tv);
		TextView fanBrandNameTextView = (TextView) linearLayout
				.findViewById(R.id.fan_brand_name_tv);
		TextView fanTypeNameTextView = (TextView) linearLayout
				.findViewById(R.id.fan_type_name_tv);
		TextView faultPheTextView = (TextView) linearLayout
				.findViewById(R.id.fault_phe_tv);
		Button showMoreButton = (Button) linearLayout
				.findViewById(R.id.show_more_detail_btn);
		final Button downloadButton = (Button) linearLayout
				.findViewById(R.id.download_fault_tree_btn);
		final FTBaseInfo ftBaseInfo = queryListItems.get(position)
				.getFtBaseInfo();
		final int latestVersion = queryListItems.get(position)
				.getLatestVersion();
		mainFaulTextView.setText(ftBaseInfo.getMainFaultCode());
		followFaultTextView.setText(ftBaseInfo.getFollowFaultCode());
		chineseNameTextView.setText(ftBaseInfo.getChineseName());
		fanBrandNameTextView.setText(ftBaseInfo.getFanBrand().getName());
		fanTypeNameTextView.setText(ftBaseInfo.getFanType().getName());
		faultPheTextView.setText(ftBaseInfo.getFaultPhe());

		// 查看更多基本信息
		showMoreButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context,
								FaultTreeInfoActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("obj",
								queryListItems.get(position).getFtBaseInfo());
						intent.putExtras(bundle);
						intent.putExtra("type", "FTBaseInfo");
						context.startActivity(intent);
					}
				};
			}
		});

		String downloadStatus = queryListItems.get(position)
				.getDownloadStatus();
		if (downloadStatus.equals(STATUS_DOWNLOADED)) {
			downloadButton.setText("已下载");
			downloadButton.setClickable(false);
		} else if (downloadStatus.equals(STATUS_TO_DOWNLOAD)) {
			downloadButton.setText("下载");
			downloadButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					downloadButton.setText("下载中");
					if (httpUtils.getNetStatus() > 0) {
						new Thread(new Runnable() {

							@Override
							public void run() {
								httpUtils.downloadFT(ftBaseInfo.getCode(),
										
										new FTDownloadListener() {
											@Override
											public void onFTDownloadListener(
													String downloadStatus,
													TreeBean treeBean) {
												if (downloadStatus
														.equals(HttpUtils.DOWNLOAD_SUCCEED)) {
													handler.post(new Runnable() {
														public void run() {
															downloadButton
																	.setText("已下载");
															downloadButton
																	.setClickable(false);
														}
													});

												} else if (downloadStatus
														.equals(HttpUtils.DOWNLOAD_FAILED)) {
													handler.post(new Runnable() {
														public void run() {
															downloadButton
																	.setText("下载");
															downloadButton
																	.setClickable(true);
															Toast.makeText(
																	context,
																	"下载失败",
																	Toast.LENGTH_SHORT)
																	.show();
														}
													});

												}
											}
										});

							}
						}).start();
					} else {
						Toast.makeText(context, "离线状态下不可更新。检查网络状态。",
								Toast.LENGTH_SHORT).show();
					}

				}
			});
		} else if (downloadStatus.equals(STATUS_TO_UPDATE)) {
			downloadButton.setText("可更新");
			downloadButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					downloadButton.setText("可更新");
					if (httpUtils.getNetStatus() > 0) {
						new Thread(new Runnable() {

							@Override
							public void run() {
								httpUtils.downloadFT(ftBaseInfo.getCode(),
										
										new FTDownloadListener() {

											@Override
											public void onFTDownloadListener(
													String downloadStatus,
													TreeBean treeBean) {
												if (downloadStatus
														.equals(HttpUtils.DOWNLOAD_SUCCEED)) {
													handler.post(new Runnable() {

														@Override
														public void run() {
															downloadButton
																	.setText("已下载");
															downloadButton
																	.setClickable(false);
														}
													});

												} else if (downloadStatus
														.equals(HttpUtils.DOWNLOAD_FAILED)) {
													handler.post(new Runnable() {

														@Override
														public void run() {
															downloadButton
																	.setText("可更新");
															downloadButton
																	.setClickable(true);
															Toast.makeText(
																	context,
																	"更新失败",
																	Toast.LENGTH_SHORT)
																	.show();
														}
													});

												}
											}
										});
							}
						}).start();
					} else {
						Toast.makeText(context, "离线状态下不可更新。检查网络状态。",
								Toast.LENGTH_SHORT).show();
					}

				}
			});
		}

		return linearLayout;
	}

}
