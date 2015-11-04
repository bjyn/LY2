package com.example.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.adapter.MyFragmentPagerAdapter;
import com.example.bean.FanBrand;
import com.example.bean.FanType;
import com.example.common.FanDataTest;
import com.example.common.HttpUtils;
import com.example.fragment.FaultFeedbackFragment;
import com.example.fragment.FaultFragment;
import com.example.fragment.FaultQueryFragment;
import com.example.fragment.SettingFragment;
import com.example.lyfaultdiagnosissystem.R;
import com.example.mywidget.HintSpinner;
import com.example.singleton.UserSingleton;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	private Button seniorSearchBtn;// 高级查询按钮
	private Button faultTreeQueryBtn;// 故障树查询页按钮
	private Button faultTreeFeedbackBtn;// 故障树反馈页按钮
	private Button meSettingBtn;// “我”设置页按钮
	private HintSpinner fanBrandSp;
	private HintSpinner fanTypeSp;
	private HintSpinner feedBackStatusSpinner;
	private ViewPager mViewPager;
	private EditText faultTreePheEt;// 故障树现象输入框
	private Dialog queryDialog;
	private AutoCompleteTextView faultTreeCodeEt;

	private FaultFragment faultFeedbackFragment;
	private FaultFragment faultQueryFragment;
	private SettingFragment meSettingFragment;// “我”设置界面
	private List<Fragment> fragments;

	private MyFragmentPagerAdapter myFragmentPagerAdapter;
	private HttpUtils httpUtils = new HttpUtils(MainActivity.this);
	private static final String TAG = "MainActivity";
	private ArrayAdapter<String> fanBrandAdapter;
	private ArrayAdapter<String> fanTypeAdapter;

	// private PopupWindow seniorSearchPopupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		FanDataTest.fanDataTest();
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.actionbar);
		initiFragments();
		// 动态设置actionbar
		setActionBar(actionBar);
		// 初始化所有fragment和导航的button
		loadViewPager(actionBar);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * 初始化所有fragment以及相关button
	 * 
	 */
	private void initiFragments() {
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
	}

	/**
	 * 配置该activity的actionbar
	 * 
	 * @param actionBar
	 */
	private void setActionBar(ActionBar actionBar) {

		if (mViewPager.getCurrentItem() != 2) {
			if (mViewPager.getCurrentItem() == 0) {
				((TextView) actionBar.getCustomView().findViewById(
						R.id.actionbar_tv)).setText("故障树查询");
			} else {
				((TextView) actionBar.getCustomView().findViewById(
						R.id.actionbar_tv)).setText("故障树反馈");
			}
			setQueryActionbar(actionBar);
		} else {
			actionBar.getCustomView().findViewById(R.id.senior_search_btn)
					.setVisibility(View.INVISIBLE);
			TextView actionbarTitleTv = (TextView) actionBar.getCustomView()
					.findViewById(R.id.actionbar_tv);
			actionbarTitleTv.setText("我");
		}
	}

	/**
	 * 当fragment的item为0或者1时,加载有高级查询按钮的actionbar
	 * 
	 * @param actionBar
	 */
	private void setQueryActionbar(ActionBar actionBar) {
		seniorSearchBtn = (Button) actionBar.getCustomView().findViewById(
				R.id.senior_search_btn);
		seniorSearchBtn.setVisibility(View.VISIBLE);
		seniorSearchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "点击了高级查询", Toast.LENGTH_SHORT)
						.show();
				switch (mViewPager.getCurrentItem()) {
				case 0:
					setQueryPopup();
					break;
				case 1:
					setFbQueryPopup();
					break;
				default:
					break;
				}
			}
		});
	}

	/**
	 * 加载故障树查询fragment的dialog
	 * 
	 */
	private void setQueryPopup() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewInSearchDia = inflater.inflate(
				R.layout.popupwindow_senior_query, null);
		queryDialog = new AlertDialog.Builder(MainActivity.this).create();
		queryDialog.show();
		queryDialog.setCanceledOnTouchOutside(false);
		queryDialog.getWindow().setContentView(viewInSearchDia);
		// 故障代码查询框
		faultTreeCodeEt = (AutoCompleteTextView) viewInSearchDia
				.findViewById(R.id.fault_tree_code_in_query_popup_et);
		Button faultTreeCodeEtCleanBtn = (Button) viewInSearchDia
				.findViewById(R.id.fault_tree_code_in_query_popup_clean_btn);
		faultTreeCodeEtCleanBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				faultTreeCodeEt.setText("");
			}
		});

		faultTreePheEt = (AutoCompleteTextView) viewInSearchDia
				.findViewById(R.id.fault_phe_in_query_popup_et);
		Button faultTreePheEtCleanBtn = (Button) viewInSearchDia
				.findViewById(R.id.fault_phe_in_query_popup_clean_btn);
		faultTreePheEtCleanBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				faultTreePheEt.setText("");
			}
		});

		fanBrandSp = (HintSpinner) viewInSearchDia
				.findViewById(R.id.choose_fan_brand_in_query_popup_sp);
		fanTypeSp = (HintSpinner) viewInSearchDia
				.findViewById(R.id.choose_fan_type_in_query_popup_sp);
		setSpinnerEvent(fanBrandSp, fanTypeSp);
		Button queryConfirmBtn = (Button) viewInSearchDia
				.findViewById(R.id.query_confirm_in_query_popup_btn);
		queryConfirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setSearchEvent(faultTreeCodeEt.getText().toString(),
						faultTreePheEt.getText().toString(),
						fanBrandSp.getTag(), fanTypeSp.getTag());
			}
		});

		Button queryConditionCleanBtn = (Button) viewInSearchDia
				.findViewById(R.id.query_condition_clean_in_query_btn);
		// 清空按钮不对spinner起作用
		queryConditionCleanBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				faultTreeCodeEt.setText("");
				faultTreePheEt.setText("");
				queryDialog.cancel();
			}
		});

	}

	/**
	 * 加载故障反馈查询fragment的dialog
	 * 
	 */
	private void setFbQueryPopup() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewInSearchDia = inflater.inflate(
				R.layout.popupwindow_senior_fb_query, null);
		queryDialog = new AlertDialog.Builder(MainActivity.this).create();
		queryDialog.show();
		queryDialog.setCanceledOnTouchOutside(false);
		queryDialog.getWindow().setContentView(viewInSearchDia);
		// 故障代码查询框
		faultTreeCodeEt = (AutoCompleteTextView) viewInSearchDia
				.findViewById(R.id.fault_tree_code_in_fbquery_popup_et);
		Button faultTreeCodeEtCleanBtn = (Button) viewInSearchDia
				.findViewById(R.id.fault_tree_code_in_fbquery_popup_clean_btn);
		faultTreeCodeEtCleanBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				faultTreeCodeEt.setText("");
			}
		});

		// 未反馈为0，已反馈为1
		feedBackStatusSpinner = (HintSpinner) viewInSearchDia
				.findViewById(R.id.choose_feedback_status_in_fbquery_popup_sp);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
				MainActivity.this,
				android.R.layout.simple_spinner_dropdown_item, new String[] {
						"未反馈", "已反馈" });
		fanTypeSp.setAdapter(arrayAdapter);
		fanTypeSp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				arg0.setTag(arg2);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		fanBrandSp = (HintSpinner) viewInSearchDia
				.findViewById(R.id.choose_fan_brand_in_fbquery_popup_sp);
		fanTypeSp = (HintSpinner) viewInSearchDia
				.findViewById(R.id.choose_fan_type_in_fbquery_popup_sp);
		setSpinnerEvent(fanBrandSp, fanTypeSp);
		fanBrandSp.setTag(null);
		fanTypeSp.setTag(null);
		Button queryConfirmBtn = (Button) viewInSearchDia
				.findViewById(R.id.query_confirm_in_fbquery_popup_btn);
		queryConfirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setSearchEvent(faultTreeCodeEt.getText().toString(),
						faultTreePheEt.getText().toString(),
						fanBrandSp.getTag(), fanTypeSp.getTag());
			}
		});

		Button queryConditionCleanBtn = (Button) viewInSearchDia
				.findViewById(R.id.query_condition_clean_in_fbquery_btn);
		// 清空按钮不对spinner起作用
		queryConditionCleanBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				faultTreeCodeEt.setText("");
				faultTreePheEt.setText("");
				queryDialog.cancel();
			}
		});
	}

	/**
	 * 设定spinner事件
	 * 
	 * @param fanBrandSp
	 * @param fanTypeSp
	 */
	private void setSpinnerEvent(final HintSpinner fanBrandSp,
			final HintSpinner fanTypeSp) {
		List<String> brandNames = new ArrayList<String>();
		final List<String> typeNames = new ArrayList<String>();
		fanBrandAdapter = new ArrayAdapter<String>(MainActivity.this,
				android.R.layout.simple_spinner_dropdown_item);
		fanTypeAdapter = new ArrayAdapter<String>(MainActivity.this,
				android.R.layout.simple_spinner_dropdown_item);
		for (FanBrand fanBrand : UserSingleton.getInstance().getFanBrands()) {
			brandNames.add(fanBrand.getName());
		}
		fanBrandAdapter = new ArrayAdapter<String>(MainActivity.this,
				android.R.layout.simple_spinner_dropdown_item, brandNames);
		for (FanType fanType : UserSingleton.getInstance().getFanTypes()) {
			typeNames.add(fanType.getName());
		}
		// fanBrandSp的工作只是缩小fanTypeSp的范围,settag在fanTypeSp中设置
		fanBrandSp.setAdapter(fanBrandAdapter);
		fanBrandSp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO 修复两个spinner
				// 选择品牌后，类型候选为该品牌下的所有类型
				final FanBrand brand = UserSingleton.getInstance()
						.getFanBrands().get(arg2);
				arg0.setTag(brand.getCode());
				fanTypeSp.setTag(null);
				List<String> typeNames = new ArrayList<String>();
				for (FanType fanType : brand.getFanTypes()) {
					typeNames.add(fanType.getName());
				}
				fanTypeAdapter = new ArrayAdapter<>(MainActivity.this,
						android.R.layout.simple_spinner_dropdown_item,
						typeNames);
				fanTypeSp.setAdapter(fanTypeAdapter);
				fanTypeSp
						.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								FanType fanType = null;
								// 已经选了风机品牌了
								if (fanBrandSp.getTag() != null) {
									fanType = brand.getFanTypes().get(arg2);
								} else {
									// 还没有选风机品牌
									fanType = UserSingleton.getInstance()
											.getFanTypes().get(arg2);
									// 设定风机型号
									for (int i = 0; i < UserSingleton
											.getInstance().getFanBrands()
											.size(); i++) {
										if (fanType.getBrandCode().equals(
												UserSingleton.getInstance()
														.getFanBrands().get(i)
														.getCode())) {
											fanBrandSp.setSelection(i);
											fanBrandSp.setTag(fanType
													.getBrandCode());
											break;
										}
									}
								}
								arg0.setTag(fanType.getCode());
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
							}

						});
				// fanTypeAdapter.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// 没有选择任何品牌的时候，类型候选为所有品牌下的所有类型
				List<String> typeNames = new ArrayList<String>();
				for (FanType fanType : UserSingleton.getInstance()
						.getFanTypes()) {
					typeNames.add(fanType.getName());
				}
				fanTypeAdapter = new ArrayAdapter<>(MainActivity.this,
						android.R.layout.simple_spinner_dropdown_item,
						typeNames);
				fanTypeSp.setAdapter(fanTypeAdapter);
				fanTypeAdapter.notifyDataSetChanged();
			}

		});

	}

	private void setSearchEvent(String faultCode, Object faultKeyword,
			Object fanBrandCodeObject, Object fanTypeCodeObject) {
		FaultFragment faultFragment = (FaultFragment) fragments.get(mViewPager
				.getCurrentItem());
		// 将查询过程委托给fragment中的queryFunction;
		faultFragment.queryFunction(faultCode, faultKeyword,
				fanBrandCodeObject, fanTypeCodeObject);
	}

	/**
	 * 加载viewpager
	 * 
	 * @param actionBar
	 */
	private void loadViewPager(final ActionBar actionBar) {
		myFragmentPagerAdapter = new MyFragmentPagerAdapter(
				getSupportFragmentManager(), fragments);
		mViewPager.setAdapter(myFragmentPagerAdapter);
		mViewPager.setCurrentItem(0);
		faultTreeQueryBtn.setTextColor(Color.rgb(39, 142, 255));
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				// TODO 每次改变每个按键上的图标
				case 0:
					// 改变对应按钮的字体颜色
					faultTreeQueryBtn.setTextColor(Color.parseColor("#66b62e"));
					faultTreeFeedbackBtn.setTextColor(Color
							.parseColor("#cccccc"));
					meSettingBtn.setTextColor(Color.parseColor("#cccccc"));
					setActionBar(actionBar);
					break;
				case 1:
					// 改变对应按钮的颜色
					faultTreeQueryBtn.setTextColor(Color.parseColor("#cccccc"));
					faultTreeFeedbackBtn.setTextColor(Color
							.parseColor("#66b62e"));
					meSettingBtn.setTextColor(Color.parseColor("#cccccc"));
					setActionBar(actionBar);
					break;
				case 2:
					// 改变对应按钮的颜色
					faultTreeQueryBtn.setTextColor(Color.parseColor("#cccccc"));
					faultTreeFeedbackBtn.setTextColor(Color
							.parseColor("#cccccc"));
					meSettingBtn.setTextColor(Color.parseColor("#66b62e"));
					setActionBar(actionBar);
					break;
				default:
					break;
				}
				setActionBar(actionBar);
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
				// TODO 每次点击改变每个按钮上的图片
				case R.id.fault_query_page_btn:
					mViewPager.setCurrentItem(0);
					faultTreeQueryBtn.setTextColor(Color.parseColor("#66b62e"));
					faultTreeFeedbackBtn.setTextColor(Color
							.parseColor("#cccccc"));
					meSettingBtn.setTextColor(Color.parseColor("#cccccc"));
					setActionBar(actionBar);
					break;
				case R.id.fault_feedback_page_btn:
					mViewPager.setCurrentItem(1);
					faultTreeQueryBtn.setTextColor(Color.parseColor("#cccccc"));
					faultTreeFeedbackBtn.setTextColor(Color
							.parseColor("#66b62e"));
					meSettingBtn.setTextColor(Color.parseColor("#cccccc"));
					setActionBar(actionBar);
					break;
				case R.id.me_page_btn:
					mViewPager.setCurrentItem(2);
					faultTreeQueryBtn.setTextColor(Color.parseColor("#cccccc"));
					faultTreeFeedbackBtn.setTextColor(Color
							.parseColor("#cccccc"));
					meSettingBtn.setTextColor(Color.parseColor("#66b62e"));
					setActionBar(actionBar);
					break;
				default:
					break;
				}
			}
		};
		faultTreeQueryBtn.setOnClickListener(buttonBarOnClickListener);
		faultTreeFeedbackBtn.setOnClickListener(buttonBarOnClickListener);
		meSettingBtn.setOnClickListener(buttonBarOnClickListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

}
