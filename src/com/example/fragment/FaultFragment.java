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
import com.example.mywidget.HintSpinner;
import com.example.singleton.UserSingleton;

public class FaultFragment extends Fragment {
	private int currentPage;//当前页页码
	private int currentPageCount;//当前页记录条数
	protected Button searchByFaultCodeBtn;// 确认查询条件，查询触发按钮
	protected EditText searchByFaultCodeEt;// 不同页面中，点击触发的事件不同
	protected ListView resultListView;// 不同页面中的显示不同
	protected UserSingleton userSingleton = UserSingleton.getInstance();
	protected HttpUtils httpUtils = new HttpUtils(getActivity());
	@SuppressLint("SimpleDateFormat")
	protected SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式

	public FaultFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		List<FanBrand> allFanBrands = userSingleton.getFanBrands();
		List<FanType> allFanTypes = userSingleton.getFanTypes();
		View view = inflater.inflate(R.layout.fragment_fault_tree_query,
				container, false);
		searchByFaultCodeBtn = (Button) view
				.findViewById(R.id.search_by_fault_code_confirm_btn);
		searchByFaultCodeEt = (EditText) view
				.findViewById(R.id.search_by_fault_code_et);
		resultListView = (ListView) view.findViewById(R.id.listview);
		return view;
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
				// DetailVersion detailVersion = new DetailVersion(
				// version.getString("code"), version.getString("version"));
				// detailVersions.add(detailVersion);
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

	/**
	 * 从MainActivity中接受的委托查询
	 * 
	 * @param faultCode
	 *            故障代码
	 * @param faultKeyword
	 *            故障现象
	 * @param fanBrandCodeObject
	 *            风机品牌code
	 * @param fanTypeCodeObject
	 *            风机类型code
	 */
	public void queryFunction(String faultCode, Object faultKeyword,
			Object fanBrandCodeObject, Object fanTypeCodeObject) {
		// TODO Auto-generated method stub

	}

}
