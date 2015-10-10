package com.example.activity;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.PrivateCredentialPermission;

import com.example.adapter.MyFragmentPagerAdapter;
import com.example.fragment.FaultFeedbackFragment;
import com.example.fragment.FaultQueryFragment;
import com.example.fragment.SettingFragment;
import com.example.lyfaultdiagnosissystem.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {
	private Button faultTreeQueryBtn;// 故障树查询页按钮
	private Button faultTreeFeedbackBtn;// 故障树反馈页按钮
	private Button meSettingBtn;// “我”设置页按钮
	private FaultFeedbackFragment faultFeedbackFragment;
	private FaultQueryFragment faultQueryFragment;
	private SettingFragment meSettingFragment;// “我”设置界面
	private List<Fragment> fragments;
	private ViewPager mViewPager;
	private MyFragmentPagerAdapter myFragmentPagerAdapter;
	private PopupWindow showNoticePopup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 配置actionbar
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.actionbar);
		((TextView) actionBar.getCustomView().findViewById(R.id.actionbar_tv))
				.setText("故障树查询");
		// 加载其他控件
		fragments = new ArrayList<Fragment>();
		faultTreeQueryBtn = (Button) findViewById(R.id.fault_query_page_btn);
		meSettingBtn = (Button) findViewById(R.id.me_page_btn);
		faultTreeFeedbackBtn = (Button) findViewById(R.id.fault_feedback_page_btn);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		// 初始化fragmentlist
		faultQueryFragment = new FaultQueryFragment();
		faultFeedbackFragment = new FaultFeedbackFragment();
		meSettingFragment = new SettingFragment();
		fragments.add(faultQueryFragment);
		fragments.add(faultFeedbackFragment);
		fragments.add(meSettingFragment);
		// 设置viewPager的adapter
		myFragmentPagerAdapter = new MyFragmentPagerAdapter(
				getSupportFragmentManager(), fragments);
		mViewPager.setAdapter(myFragmentPagerAdapter);
		mViewPager.setCurrentItem(0);
		faultTreeQueryBtn.setTextColor(Color.rgb(39, 142, 255));
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					// 改变对应按钮的颜色
					faultTreeQueryBtn.setTextColor(Color.rgb(39, 142, 255));
					faultTreeFeedbackBtn.setTextColor(Color.rgb(141, 139, 136));
					meSettingBtn.setTextColor(Color.rgb(141, 139, 136));
					((TextView) actionBar.getCustomView().findViewById(
							R.id.actionbar_tv)).setText("故障树查询");
					break;
				case 1:
					// 改变对应按钮的颜色
					faultTreeQueryBtn.setTextColor(Color.rgb(141, 139, 136));
					faultTreeFeedbackBtn.setTextColor(Color.rgb(39, 142, 255));
					meSettingBtn.setTextColor(Color.rgb(141, 139, 136));
					((TextView) actionBar.getCustomView().findViewById(
							R.id.actionbar_tv)).setText("故障反馈查询");
					break;
				case 2:
					// 改变对应按钮的颜色
					faultTreeQueryBtn.setTextColor(Color.rgb(141, 139, 136));
					faultTreeFeedbackBtn.setTextColor(Color.rgb(141, 139, 136));
					meSettingBtn.setTextColor(Color.rgb(39, 142, 255));
					((TextView) actionBar.getCustomView().findViewById(
							R.id.actionbar_tv)).setText("我");
					break;
				default:
					break;
				}
			}

			// 页面滑动时调用
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			// 状态改变时调用
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		// 设置下方按钮点击事件
		OnClickListener buttonBarOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.fault_query_page_btn:
					mViewPager.setCurrentItem(0);
					faultTreeQueryBtn.setTextColor(Color.rgb(39, 142, 255));
					faultTreeFeedbackBtn.setTextColor(Color.rgb(141, 139, 136));
					meSettingBtn.setTextColor(Color.rgb(141, 139, 136));
					((TextView) actionBar.getCustomView().findViewById(
							R.id.actionbar_tv)).setText("故障树查询");
					break;
				case R.id.fault_feedback_page_btn:
					mViewPager.setCurrentItem(1);
					faultTreeQueryBtn.setTextColor(Color.rgb(141, 139, 136));
					faultTreeFeedbackBtn.setTextColor(Color.rgb(39, 142, 255));
					meSettingBtn.setTextColor(Color.rgb(141, 139, 136));
					((TextView) actionBar.getCustomView().findViewById(
							R.id.actionbar_tv)).setText("故障反馈查询");
					break;
				case R.id.me_page_btn:
					mViewPager.setCurrentItem(2);
					faultTreeQueryBtn.setTextColor(Color.rgb(141, 139, 136));
					faultTreeFeedbackBtn.setTextColor(Color.rgb(141, 139, 136));
					meSettingBtn.setTextColor(Color.rgb(39, 142, 255));
					((TextView) actionBar.getCustomView().findViewById(
							R.id.actionbar_tv)).setText("我");
					break;
				default:
					break;
				}
			}
		};
		faultTreeQueryBtn.setOnClickListener(buttonBarOnClickListener);
		faultTreeFeedbackBtn.setOnClickListener(buttonBarOnClickListener);
		meSettingBtn.setOnClickListener(buttonBarOnClickListener);
		// 展示公告
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewInPopup = layoutInflater.inflate(R.layout.popupwindow_notice,
				null);
		showNoticePopup = new PopupWindow(viewInPopup,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		TextView contentTv = (TextView) viewInPopup
				.findViewById(R.id.notice_content_tv);
		Button closeNoticeBtn = (Button) viewInPopup
				.findViewById(R.id.notice_close_button);
		Button confirmBtn = (Button) viewInPopup
				.findViewById(R.id.notice_confirm_btn);
		//TODO 从网络获取公告信息
		String notice="dagjey";
		contentTv.setText(notice);
		OnClickListener clickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				showNoticePopup.dismiss();
			}
		};
		closeNoticeBtn.setOnClickListener(clickListener);
		confirmBtn.setOnClickListener(clickListener);
		// popupWindow.setContentView();
	}

}
