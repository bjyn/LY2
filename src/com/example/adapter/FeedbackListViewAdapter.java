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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.activity.FaultTreeInfoActivity;
import com.example.bean.FTFBBaseInfo;
import com.example.bean.FeedbackListItem;
import com.example.common.HttpUtils;
import com.example.common.HttpUtils.FTDownloadListener;
import com.example.lyfaultdiagnosissystem.R;
import com.example.tree_component.bean.TreeBean;

public class FeedbackListViewAdapter extends BaseAdapter {
	public static final String STATUS_DOWNLOADED = "已下载";
	public static final String STATUS_TO_UPDATE = "可更新";
	public static final String STATUS_TO_DOWNLOAD = "下载";
	private List<FeedbackListItem> feedbackListItems;
	private Context context;
	private HttpUtils httpUtils;
	private Handler handler;

	public FeedbackListViewAdapter(List<FeedbackListItem> feedbackListItems,
			Context context, Handler handler) {
		super();
		this.feedbackListItems = feedbackListItems;
		this.context = context;
		this.handler = handler;
		httpUtils = new HttpUtils(context);
	}

	@Override
	public int getCount() {
		return feedbackListItems.size();
	}

	@Override
	public Object getItem(int position) {
		return feedbackListItems.get(position);
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
				R.layout.item_fault_tree_feedback, null);
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
		ImageView feedbackStatus = (ImageView) linearLayout
				.findViewById(R.id.feedback_status_pic);
		final Button downloadButton = (Button) linearLayout
				.findViewById(R.id.download_fault_tree_btn);
		final FTFBBaseInfo ftfbBaseInfo = feedbackListItems.get(position)
				.getFtfbBaseInfo();
		mainFaulTextView.setText(ftfbBaseInfo.getMainFaultCode());
		followFaultTextView.setText(ftfbBaseInfo.getFollowFaultCode());
		chineseNameTextView.setText(ftfbBaseInfo.getChineseName());
		fanBrandNameTextView.setText(ftfbBaseInfo.getFanBrand().getName());
		fanTypeNameTextView.setText(ftfbBaseInfo.getFanType().getName());
		faultPheTextView.setText(ftfbBaseInfo.getFaultPhe());
		if (ftfbBaseInfo.getFeedbackStatus().equals("1")) {
			feedbackStatus.setImageResource(R.drawable.un_feedback_type);
		} else if (ftfbBaseInfo.getFeedbackStatus().equals("2")) {
			feedbackStatus.setImageResource(R.drawable.feedbacked_type);
		}

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
								ftfbBaseInfo);
						intent.putExtras(bundle);
						intent.putExtra("type","FBFTBaseInfo");
						context.startActivity(intent);
					}
				};
			}
		});

		String downloadStatus = feedbackListItems.get(position)
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
								httpUtils.downloadFT(ftfbBaseInfo.getCode(),
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
								httpUtils.downloadFT(ftfbBaseInfo.getCode(),
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
