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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.activity.FaultTreeInfoActivity;
import com.example.bean.FTBaseInfo;
import com.example.lyfaultdiagnosissystem.R;

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
	private List<FTBaseInfo> queryListItems;
	private Context context;

	public QueryListViewAdapter(List<FTBaseInfo> queryListItems, Context context) {
		super();
		this.queryListItems = queryListItems;
		this.context = context;
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
		final FTBaseInfo ftBaseInfo = queryListItems.get(position);
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
								queryListItems.get(position));
						intent.putExtras(bundle);
						intent.putExtra("type", "FTBaseInfo");
						context.startActivity(intent);
					}
				};
			}
		});
		return linearLayout;
	}

}
