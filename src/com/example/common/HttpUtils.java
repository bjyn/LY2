package com.example.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.singleton.UserSingleton;
import com.example.sqlite.DBDao;
import com.example.tree_component.bean.FaultTreeNode;
import com.example.tree_component.bean.FaultTreeURLBean;
import com.example.tree_component.bean.TreeBean;
import com.example.tree_component.bean.TreeRoot;

public class HttpUtils {
	// constants
	public static final String TAG = "HttpUtils";
	public final String URL = "http://192.168.88.200:7384/lyfds/";
	public final String LOGIN = "login";
	public final String GET_APP_VERSION = "getAppVersion";
	public final String GET_NOTICE = "getNotice";
	public final String CHANGE_PWD = "changePWD";
	public final String GET_FT_VERSION_LIST = "getFTVersionList";
	public final String QUERY_FT_LIST = "queryFTList";
	public final String QUERY_FBFTLIST = "queryFBFTList";
	public final String GET_FT_DETAIL = "getFTDetail";
	public final String FEEDBACK_FT = "feedback_FT";
	public final String SET_UFB_STATUS = "setUFBStatus";
	public final int NO_NET_AVAILABLE = 0;
	public final int MOBILE_TYPE = 1;
	public final int WIFI_TYPE = 2;
	public final static String DOWNLOAD_SUCCEED = "download succeed";
	public final static String DOWNLOAD_FAILED = "download failed";

	// private attribute
	private Context context;

	public HttpUtils(Context context) {
		this.context = context;
	}

	/**
	 * 判断网络状况
	 * 
	 * @return 见常量
	 */
	public int getNetStatus() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activityNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		int status = 0;
		if (activityNetworkInfo == null) {
			status = NO_NET_AVAILABLE;
		} else if (connectivityManager.getNetworkInfo(
				ConnectivityManager.TYPE_WIFI).isConnected()) {
			status = WIFI_TYPE;
		} else if (connectivityManager.getNetworkInfo(
				ConnectivityManager.TYPE_MOBILE).isConnected()) {
			status = MOBILE_TYPE;
		}
		return status;
	}

	/**
	 * 当网络请求失败时，用于记录失败信息并Toast出不同网络错误。
	 * 
	 * @param t
	 *            抛出
	 * @param errorNo
	 *            错误代码
	 * @param strMsg
	 *            错误信息
	 * @param context
	 *            环境
	 * @param classTag
	 *            类标签(哪个类)
	 */
	public void doFailure(Throwable t, int errorNo, String strMsg,
			Context context, String classTag) {
		Log.i(TAG, classTag + ":FinalHttp Error:" + strMsg + ";Error No:"
				+ errorNo);
		if (null == strMsg) {
			Log.e(TAG, "strmsg null 错误");
			if (context != null)
				Toast.makeText(context, "未知错误。请稍候重试或联系技术人员。",
						Toast.LENGTH_SHORT).show();
		} else {
			switch (strMsg) {
			case "response status error code:404":
				Log.e(TAG, "404错误");
				Toast.makeText(context, "请求资源不存在。请稍候重试或联系技术人员。",
						Toast.LENGTH_SHORT).show();
				break;

			default:
				Log.e(TAG, "请检查网络连接。");
				Toast.makeText(context, "网络出错。请检查网络连接或联系技术人员。",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}

	/**
	 * 下载一棵树。包含了下载／缓存树／更新版本的动作。
	 * ！：这是一个同步访问。需要在新线程中新建。
	 * @param code
	 * @param listener
	 */
	public void downloadFT(final String code,
			 final FTDownloadListener listener) {
//		UserSingleton userSingleton = UserSingleton.getInstance();
//		final DBDao dbDao = new DBDao(context);
//		FinalHttp finalHttp = new FinalHttp();
//		Map<String, String> params = new HashMap<>();
//		params.put("token", userSingleton.getValidateToken());
//		params.put("code", code);
//		params.put("version", version + "");
//		finalHttp.get(URL + GET_FT_DETAIL, new AjaxParams(params),
//				new AjaxCallBack<Object>() {
//
//					@Override
//					public void onFailure(Throwable t, int errorNo,
//							String strMsg) {
//						doFailure(t, errorNo, strMsg, context, TAG);
//						listener.onFTDownloadListener(DOWNLOAD_FAILED, null);
//						super.onFailure(t, errorNo, strMsg);
//					}
//
//					@Override
//					public void onSuccess(Object t) {
//						final JSONObject resultJsonObject = JSON
//								.parseObject((String) t);
//						String statusCode = resultJsonObject
//								.getString("statusCode");
//						if (statusCode.equals("300")) {
//							listener.onFTDownloadListener(DOWNLOAD_FAILED, null);
//							Log.i(TAG, "下载失败");
//						} else {
//							new Thread(new Runnable() {
//
//								@Override
//								public void run() {
//									// 1.Parse the JSON
//									JSONObject obj = resultJsonObject
//											.getJSONObject("obj");
//									TreeRoot treeRoot = new TreeRoot(code,
//											version + "",
//											obj.getString("mainFaultCode"),
//											obj.getString("followFaultCode"),
//											obj.getString("CName"), obj
//													.getString("EName"),
//											obj.getString("triggerCondition"));
//									List<FaultTreeNode> faultReasonNodes = JSON
//											.parseArray(
//													obj.getJSONArray(
//															"faultReasonArray")
//															.toJSONString(),
//													FaultTreeNode.class);
//									List<FaultTreeNode> reasonCheckNodes = JSON
//											.parseArray(
//													obj.getJSONArray(
//															"reasonCheck")
//															.toJSONString(),
//													FaultTreeNode.class);
//									List<FaultTreeNode> handleFaultNodes = JSON
//											.parseArray(
//													obj.getJSONArray(
//															"handleFault")
//															.toJSONString(),
//													FaultTreeNode.class);
//									List<FaultTreeURLBean> faultReasonURLs = JSON
//											.parseArray(
//													obj.getJSONArray(
//															"faultReasonUrl")
//															.toJSONString(),
//													FaultTreeURLBean.class);
//									List<FaultTreeURLBean> reasonCheckURLs = JSON
//											.parseArray(
//													obj.getJSONArray(
//															"reasonCheckUrl")
//															.toJSONString(),
//													FaultTreeURLBean.class);
//									List<FaultTreeURLBean> handleFaultURLs = JSON
//											.parseArray(
//													obj.getJSONArray(
//															"handleFaultUrl")
//															.toJSONString(),
//													FaultTreeURLBean.class);
//									
//									for (FaultTreeNode faultReasonNode : faultReasonNodes) {
//										List<FaultTreeURLBean> urlBeans=new ArrayList<>();
//										for (FaultTreeURLBean faultReasonUrlBean : faultReasonURLs) {
//											if (faultReasonUrlBean.getCode().equals(faultReasonNode.getCode())) {
//												urlBeans.add(faultReasonUrlBean);
//											}
//										}
//										faultReasonNode.setUrls(urlBeans);
//									}
//									
//									for (FaultTreeNode reasonCheckNode : reasonCheckNodes) {
//										List<FaultTreeURLBean> urlBeans=new ArrayList<>();
//										for (FaultTreeURLBean faultReasonUrlBean : reasonCheckURLs) {
//											if (faultReasonUrlBean.getCode().equals(reasonCheckNode.getCode())) {
//												urlBeans.add(faultReasonUrlBean);
//											}
//										}
//										reasonCheckNode.setUrls(urlBeans);
//									}
//									
//									for (FaultTreeNode handleFaultNode : handleFaultNodes) {
//										List<FaultTreeURLBean> urlBeans=new ArrayList<>();
//										for (FaultTreeURLBean handleFaultUrlBean : handleFaultURLs) {
//											if (handleFaultUrlBean.getCode().equals(handleFaultNode.getCode())) {
//												urlBeans.add(handleFaultUrlBean);
//											}
//										}
//										handleFaultNode.setUrls(urlBeans);
//									}
//
//									final Map<String, List<FaultTreeNode>> treeNodeMap = new HashMap<>();
//									treeNodeMap.put("faultReasonNode",
//											faultReasonNodes);
//									treeNodeMap.put("reasonCheckNode",
//											reasonCheckNodes);
//									treeNodeMap.put("handleFaultNode",
//											handleFaultNodes);
//									final Map<String, List<FaultTreeURLBean>> nodeURLMap = new HashMap<>();
//									nodeURLMap.put("faultReasonURL",
//											faultReasonURLs);
//									nodeURLMap.put("reasonCheckURL",
//											reasonCheckURLs);
//									nodeURLMap.put("handleFaultURL",
//											handleFaultURLs);
//
//									// 2.Cache in DB / Delete the old Version
//									dbDao.deleteFTByCode(code);
//									dbDao.insertFT(code, treeNodeMap,
//											nodeURLMap);
//
//									// 3.Update detail_version in Q_FT
//									dbDao.updateFBFTDetailVersion(code, version);
//									// 4.Update detail_version in FB_FT
//									dbDao.updateFTDetailVersion(code, version);
//
//									// 5.complete
//									listener.onFTDownloadListener(
//											DOWNLOAD_SUCCEED, new TreeBean(
//													treeRoot, faultReasonNodes,
//													reasonCheckNodes,
//													handleFaultNodes));
//
//								}
//							}).start();
//
//						}
//						super.onSuccess(t);
//					}
//				});
	}

	/**
	 * 下载状态监听
	 * @author ynkjmacmini4
	 *
	 */
	public interface FTDownloadListener {
		public void onFTDownloadListener(String downloadStatus,
				TreeBean treeBean);
	}

}
