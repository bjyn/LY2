package com.example.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxParams;
import android.annotation.SuppressLint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.bean.CodeVersionPair;
import com.example.bean.FanBrand;
import com.example.bean.FanType;
import com.example.common.HttpUtils;
import com.example.lyfaultdiagnosissystem.R;
import com.example.singleton.UserSingleton;

public class FaultFragment extends Fragment {

	protected EditText faultTreeCodeEt;
	protected EditText faultPheEt;// 故障树查询页面中，输入的是故障现象； 在故障树反馈页面中，输入的是反馈状态.
	protected TextView chooseFanBrandTv;
	protected TextView chooseFanTypeTv;
	protected Button queryConditionCleanBtn;
	protected Button queryConfirmBtn;// 不同页面中，点击触发的事件不同
	protected ListView resultListView;// 不同页面中的显示不同
	private PopupWindow chooseFanBrandPopupWindow;
	private PopupWindow chooseFanTypePopupWindow;
	protected ListView chooseFanBrandLv;// 不同页面中，候选项可能不同
	protected ListView chooseFanTypeLv;// 不同页面中，候选项可能不同
	protected String chosedFanBrandCode = null;
	protected String chosedFanTypeCode = null;
	protected List<FanBrand> allFanBrands;
	protected List<FanType> allFanTypes;
	protected UserSingleton userSingleton = UserSingleton.getInstance();
	protected HttpUtils httpUtils = new HttpUtils(getActivity());
	@SuppressLint("SimpleDateFormat")
	protected SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式

	public FaultFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		allFanBrands = userSingleton.getFanBrands();
		allFanTypes = userSingleton.getFanTypes();
		View view = inflater.inflate(R.layout.fragment_fault_tree_query,
				container, false);
		faultTreeCodeEt = (EditText) view.findViewById(R.id.fault_tree_code);
		faultPheEt = (EditText) view
				.findViewById(R.id.fault_phe_or_fb_status_et);
		queryConditionCleanBtn = (Button) view
				.findViewById(R.id.query_condition_clean_btn);
		
		queryConfirmBtn = (Button) view.findViewById(R.id.query_confirm_btn);
		resultListView = (ListView) view.findViewById(R.id.listView);
		chooseFanBrandTv = (TextView) view
				.findViewById(R.id.choose_fan_brand_code_tv);
		chooseFanTypeTv = (TextView) view
				.findViewById(R.id.choose_fan_type_code_tv);

		// 设置自定义spinner
		setFanBrandSpinner(inflater);
		setFanTypeSpinner(inflater, allFanTypes);
		chosedFanBrandCode = null;
		chosedFanTypeCode = null;
		return view;
	}

	/**
	 * 自定义spinner载入函数，通过popup listview的方式实现
	 * 
	 * @param inflater
	 *            布局填充器
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams") private void setFanBrandSpinner(final LayoutInflater inflater) {
		{
			View viewInFanBrandPopup = inflater.inflate(
					R.layout.popupwindow_within_fan_brand_list, null);
			chooseFanBrandLv = (ListView) viewInFanBrandPopup
					.findViewById(R.id.fan_brand_listview_in_popup);
			List<Map<String, String>> fanBrandDataList = new ArrayList<>();
			for (FanBrand fanBrand : allFanBrands) {
				Map<String, String> map = new HashMap<>();
				map.put("name", fanBrand.getName());
				fanBrandDataList.add(map);
			}
			SimpleAdapter fanBrandAdapter = new SimpleAdapter(getActivity(),
					fanBrandDataList, R.layout.item_listview_in_popup,
					new String[] { "name" },
					new int[] { R.id.popup_listview_item_tv });
			chooseFanBrandLv.setAdapter(fanBrandAdapter);
			chooseFanBrandLv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					// 每次选定品牌都会清空
					chosedFanBrandCode = allFanBrands.get(position).getCode();
					chooseFanBrandTv.setText(allFanBrands.get(position)
							.getName());
					// 候选的型号列表
					List<FanType> candidateFanTypes = new ArrayList<>();
					for (FanType fanType : allFanTypes) {
						if (fanType.getBrandCode().equals(chosedFanBrandCode)) {
							candidateFanTypes.add(fanType);
						}
					}
					setFanTypeSpinner(inflater, candidateFanTypes);
				}
			});
			chooseFanBrandPopupWindow = new PopupWindow(viewInFanBrandPopup,
					310, 400);
			chooseFanBrandPopupWindow
					.setAnimationStyle(R.style.popupwindow_anim_style);
			// 只有在设置了popupwindow的背景后，点击popupwindow外面才会消失
			chooseFanBrandPopupWindow
					.setBackgroundDrawable(new BitmapDrawable());
			chooseFanBrandPopupWindow.setFocusable(true);

			// 设置自定义下拉列表打开关闭事件
			OnClickListener chooseFanTVClickListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					switch (v.getId()) {
					case R.id.choose_fan_brand_code_tv:
						if (chooseFanBrandPopupWindow.isShowing()) {
							chooseFanBrandPopupWindow.dismiss();
						} else {
							chooseFanBrandPopupWindow
									.showAsDropDown(chooseFanBrandTv);
						}
						break;
					case R.id.choose_fan_type_code_tv:
						if (chooseFanTypePopupWindow.isShowing())
							chooseFanTypePopupWindow.dismiss();
						else {
							chooseFanTypePopupWindow
									.showAsDropDown(chooseFanTypeTv);
						}
						break;
					default:
						break;
					}
				}
			};
			chooseFanBrandTv.setOnClickListener(chooseFanTVClickListener);
		}
	}

	/**
	 * 设置风扇型号的下拉
	 * 
	 * @param inflater
	 * @param fanTypes
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	protected void setFanTypeSpinner(LayoutInflater inflater,
			final List<FanType> fanTypes) {
		// 风机类型spinner：
		View viewInFanTypePopup = inflater.inflate(
				R.layout.popupwindow_within_fan_type_list, null);
		chooseFanTypeLv = (ListView) viewInFanTypePopup
				.findViewById(R.id.fan_type_listview_in_popup);
		List<Map<String, String>> fanTypeDataList = new ArrayList<>();
		for (FanType fanType : fanTypes) {
			Map<String, String> map = new HashMap<>();
			map.put("name", fanType.getName());
			fanTypeDataList.add(map);
		}
		SimpleAdapter fanTypeAdapter = new SimpleAdapter(getActivity(),
				fanTypeDataList, R.layout.item_listview_in_popup,
				new String[] { "name" },
				new int[] { R.id.popup_listview_item_tv });
		chooseFanTypeLv.setAdapter(fanTypeAdapter);
		chooseFanTypeLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// 当一个型号选定的时候，获取它的品牌。更改品牌。
				chosedFanTypeCode = fanTypes.get(position).getCode();
				chooseFanTypeTv.setText(fanTypes.get(position).getName());
				for (FanBrand fanBrand : allFanBrands) {
					if (fanBrand.getCode().equals(
							fanTypes.get(position).getBrandCode())) {
						chosedFanBrandCode = fanBrand.getCode();
						chooseFanBrandTv.setText(fanBrand.getName());
						break;
					}
				}
			}
		});

		chooseFanTypePopupWindow = new PopupWindow(viewInFanTypePopup, 310, 400);
		chooseFanTypePopupWindow.setFocusable(true);
		chooseFanTypePopupWindow
				.setAnimationStyle(R.style.popupwindow_anim_style);
		chooseFanTypePopupWindow.setBackgroundDrawable(new BitmapDrawable());

		// 设置自定义下拉列表打开关闭事件
		OnClickListener chooseFanTVClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.choose_fan_brand_code_tv:
					if (chooseFanBrandPopupWindow.isShowing()) {
						chooseFanBrandPopupWindow.dismiss();
					} else {
						chooseFanBrandPopupWindow
								.showAsDropDown(chooseFanBrandTv);
					}
					break;
				case R.id.choose_fan_type_code_tv:
					if (chooseFanTypePopupWindow.isShowing())
						chooseFanTypePopupWindow.dismiss();
					else {
						chooseFanTypePopupWindow
								.showAsDropDown(chooseFanTypeTv);
					}
					break;
				default:
					break;
				}
			}
		};
		chooseFanTypeTv.setOnClickListener(chooseFanTVClickListener);
	}

	/**
	 * 同步线程：获取全部最新的详情版本号
	 * 
	 * @return
	 */
	protected List<CodeVersionPair> getLatestDetailVersions() {
		FinalHttp finalHttp = new FinalHttp();
		Map<String, String> params = new HashMap<>();
		params.put("token", userSingleton.getValidateToken());
		params.put("code", "");
		String result = (String) finalHttp.getSync(httpUtils.URL
				+ httpUtils.GET_FT_VERSION_LIST, new AjaxParams(params));
		JSONObject resultJsonObject = JSON.parseObject(result);
		String status = resultJsonObject.getString("statusCode");
		if (status.equals("200")) {
			List<CodeVersionPair> detailVersions = new ArrayList<>();
			JSONArray versionArray = resultJsonObject.getJSONObject("obj")
					.getJSONArray("FTVersionArray");
			for (Object object : versionArray) {
				JSONObject version = (JSONObject) object;
//				DetailVersion detailVersion = new DetailVersion(
//						version.getString("code"), version.getString("version"));
//				detailVersions.add(detailVersion);
			}
			return detailVersions;
		} else {
			Log.i(HttpUtils.TAG, "下载最新版本列表出错。");
			return null;
		}
	}

	/**
	 * 获取最新的版本号 同步网络访问。
	 * 
	 * @param code
	 *            故障树代码
	 * @return -1:error
	 */
	protected int getLatestVersion(String code) {
		FinalHttp finalHttp = new FinalHttp();
		Map<String, String> params = new HashMap<>();
		params.put("token", userSingleton.getValidateToken());
		params.put("code", code);
		String t = (String) finalHttp.getSync(httpUtils.URL
				+ httpUtils.GET_FT_VERSION_LIST, new AjaxParams(params));
		if (t == null || t.equals("")) {
			return -1;
		} else {
			JSONObject resultObject = JSON.parseObject((String) t);
			if (resultObject.getString("statusCode").equals("300")) {
				return -1;
			} else if (resultObject.getString("statusCode").equals("200")) {
				return resultObject.getJSONObject("obj")
						.getJSONArray("FTVersionArray").getJSONObject(0)
						.getIntValue("version");
			} else {
				return -1;
			}

		}
	}

}
