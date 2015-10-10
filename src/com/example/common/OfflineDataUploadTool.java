package com.example.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxParams;
import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.bean.OfflineFeedbacked;
import com.example.bean.OfflineUnfeedback;
import com.example.singleton.UserSingleton;
import com.example.sqlite.DBDao;

/**
 * 将离线数据上传到服务器中。尽可能快的同步服务器和本地数据库。
 * 
 * @author ynkjmacmini4
 * 
 */
public class OfflineDataUploadTool {
	public static final String TAG = "OfflineDataUploadTool";
	private DBDao dbDao;
	private HttpUtils httpUtils;
	private UserSingleton userSingleton = UserSingleton.getInstance();

	public OfflineDataUploadTool(Context context) {
		super();
		dbDao = new DBDao(context);
		httpUtils = new HttpUtils(context);
	}

	/**
	 * 上传在离线状态下置为未反馈或已反馈的数据至服务器中同步。 需要在新线程中进行调用。
	 */
	public boolean uploadSyncOfflineData() {
		final List<OfflineUnfeedback> offlineUnfeedbacksByTime = dbDao
				.getAllOfflineUnfeedbacksByTime();
		final List<OfflineFeedbacked> offlineFeedbackedsByTime = dbDao
				.getAllOfflineFeedbackedsByTime();

		// 1.先上传在在线状态下被置为未反馈，并在离线情况下被置为已反馈的纪录
		for (OfflineFeedbacked offlineFeedbacked : offlineFeedbackedsByTime) {
			if (!offlineFeedbacked.getFeedbackCode().equals("")) {
				// 反馈码存在：即该树在在线状态下被置为未反馈
				// 同步上传。
				FinalHttp finalHttp = new FinalHttp();
				finalHttp.configTimeout(10000);// 设置超时。如果返回失败result就会为null
				Map<String, String> params = new HashMap<>();
				params.put("token", userSingleton.getValidateToken());
				params.put("feedbackcode", offlineFeedbacked.getFeedbackCode());
				params.put("feedbackType", offlineFeedbacked.getFeedbackType());
				params.put("handleFaultCode",
						offlineFeedbacked.getHandleFaultCode());
				params.put("description", offlineFeedbacked.getDescription());
				params.put("feedbackWindCode",
						offlineFeedbacked.getFeedbackWindCode());
				String result = (String) finalHttp.getSync(httpUtils.URL
						+ httpUtils.FEEDBACK_FT, new AjaxParams(params));
				if (result.equals("") || result == null) {
					// 出错。
					Log.e(HttpUtils.TAG,
							"Error:同步上传失败："
									+ offlineFeedbacked.getFeedbackCode());
					return false;
				} else {
					JSONObject resultObject = JSON.parseObject(result);
					String statusCode = resultObject.getString("statusCode");
					if (statusCode.equals("300")) {
						Log.e(HttpUtils.TAG,
								"300:" + resultObject.getString("message"));
						return false;
					} else if (statusCode.equals("200")) {
						// 成功.
						// 1.删除离线已反馈表中的纪录
						dbDao.deleteOfflineFeedbacked(offlineFeedbacked);
						// 2.完成
					} else {
						Log.e(HttpUtils.TAG, "未知错误。成功返回。但是解析错误？");
						return false;
					}
				}
			}
		}

		// 2.
		// 再按时间顺序上传在离线状态下被置为未反馈的纪录。并检查该纪录是否被反馈。若已被反馈，紧接着上传该已反馈纪录。（在其他未反馈纪录上传前）
		for (OfflineUnfeedback offlineUnfeedback : offlineUnfeedbacksByTime) {
			FinalHttp finalHttp = new FinalHttp();
			finalHttp.configTimeout(10000);
			Map<String, String> params = new HashMap<>();
			params.put("token", userSingleton.getValidateToken());
			params.put("code", offlineUnfeedback.getCode());
			params.put("queryTime", offlineUnfeedback.getTime());
			String result = (String) finalHttp.getSync(httpUtils.URL
					+ httpUtils.SET_UFB_STATUS, new AjaxParams(params));
			if (result.equals("") || result == null) {
				// 出错。
				Log.e(HttpUtils.TAG,
						"Error:未反馈记录同步上传失败：" + offlineUnfeedback.getCode()
								+ offlineUnfeedback.getTime());
				return false;
			} else {
				JSONObject resultObject = JSON.parseObject(result);
				String statusCode = resultObject.getString("statusCode");
				if (statusCode.equals("300")) {
					Log.e(HttpUtils.TAG,
							"300:" + resultObject.getString("message"));
					return false;
				} else if (statusCode.equals("200")) {
					// 1.将返回的feedbackCode填补到FBFT中
					dbDao.updateOfflineFBFTFeedbackCode(
							resultObject.getString("feedbackcode"),
							offlineUnfeedback.getTime());
					// 2.删除离线未反馈纪录
					dbDao.deleteOfflineUnfeedback(offlineUnfeedback);

					// 3.检查该纪录是否被反馈
					for (OfflineFeedbacked offlineFeedbacked : offlineFeedbackedsByTime) {
						if (offlineFeedbacked.getFeedbackTime().equals(
								offlineUnfeedback.getTime())) {
							// 已反馈：上传该纪录。
							FinalHttp finalHttp2 = new FinalHttp();
							finalHttp2.configTimeout(10000);
							Map<String, String> params2 = new HashMap<>();
							params2.put("token",
									userSingleton.getValidateToken());
							params2.put("feedbackcode",
									offlineFeedbacked.getFeedbackCode());
							params2.put("feedbackType",
									offlineFeedbacked.getFeedbackType());
							params2.put("handleFaultCode",
									offlineFeedbacked.getHandleFaultCode());
							params2.put("description",
									offlineFeedbacked.getDescription());
							params2.put("feedback_wind_code",
									offlineFeedbacked.getFeedbackWindCode());
							String result2 = (String) finalHttp2.getSync(
									httpUtils.URL + httpUtils.FEEDBACK_FT,
									new AjaxParams(params2));
							
							if (result2.equals("") || result2 == null) {
								// 出错。
								Log.e(HttpUtils.TAG, "Error:同步上传失败："
										+ offlineFeedbacked.getFeedbackCode());
								return false;
							} else {
								JSONObject resultObject1 = JSON
										.parseObject(result2);
								String statusCode1 = resultObject1
										.getString("statusCode");
								if (statusCode1.equals("300")) {
									Log.e(HttpUtils.TAG,
											"300:"
													+ resultObject1
															.getString("message"));
									return false;
								} else if (statusCode1.equals("200")) {
									// 成功.
									// 1.删除离线已反馈表中的纪录
									dbDao.deleteOfflineFeedbacked(offlineFeedbacked);
								} else {
									Log.e(HttpUtils.TAG, "未知错误。成功返回。但是解析错误？");
									return false;
								}
							}
						}
					}
				} else {
					Log.e(HttpUtils.TAG, "未知错误。成功返回。但是解析错误？");
					return false;
				}
			}
		}

		// 3.最后检测是否已经全部上传成功
		List<OfflineUnfeedback> afterUploadedOfflineUnfeedbacks = dbDao
				.getAllOfflineUnfeedbacksByTime();
		List<OfflineFeedbacked> afterUploadedOfflineFeedbackeds = dbDao
				.getAllOfflineFeedbackedsByTime();

		if (afterUploadedOfflineFeedbackeds.size() == 0
				&& afterUploadedOfflineUnfeedbacks.size() == 0) {
			Log.i(TAG, "离线纪录同步上传完成");
			return true;
		} else {
			Log.e(TAG, "离线纪录同步上传异常");
			return false;
		}
	}

}
