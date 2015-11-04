package com.example.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.activity.FaultTreeInfoActivity;
import com.example.bean.FTFBBaseInfo;
import com.example.lyfaultdiagnosissystem.R;

public class FeedbackListViewAdapter extends BaseAdapter {
	public static final String STATUS_DOWNLOADED = "已下载";
	public static final String STATUS_TO_UPDATE = "可更新";
	public static final String STATUS_TO_DOWNLOAD = "下载";
	private List<FTFBBaseInfo> ftfbBaseInfos;
	private Context context;

	public FeedbackListViewAdapter(List<FTFBBaseInfo> feedbackListItems,
			Context context) {
		super();
		this.ftfbBaseInfos = feedbackListItems;
		this.context = context;
	}

	@Override
	public int getCount() {
		return ftfbBaseInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return ftfbBaseInfos.get(position);
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
		final FTFBBaseInfo ftfbBaseInfo = ftfbBaseInfos.get(position);
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
						bundle.putSerializable("obj", ftfbBaseInfo);
						intent.putExtras(bundle);
						intent.putExtra("type", "FBFTBaseInfo");
						context.startActivity(intent);
					}
				};
			}
		});
		return linearLayout;
	}

}
