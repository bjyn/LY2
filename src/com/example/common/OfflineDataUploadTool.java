package com.example.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxParams;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.bean.AbsOfflineBean;
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
	private PriorityBlockingQueue<AbsOfflineBean> pQueue;
	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");

	public OfflineDataUploadTool(Context context) {
		super();
		dbDao = new DBDao(context);
		httpUtils = new HttpUtils(context);
	}

	/**
	 * 上传在离线状态下置为未反馈或已反馈的数据至服务器中同步。 需要在新线程中进行调用。
	 */
	public boolean uploadSyncOfflineData() {
		List<AbsOfflineBean> offlineUnfeedbacksByTime = dbDao
				.getAllOfflineUnfeedbacksByTime();
		List<AbsOfflineBean> offlineFeedbackedsByTime = dbDao
				.getAllOfflineFeedbackedsByTime();
		pQueue = new PriorityBlockingQueue<AbsOfflineBean>(10,
				new Comparator<AbsOfflineBean>() {
					@Override
					public int compare(AbsOfflineBean lhs, AbsOfflineBean rhs) {
						try {
							Date lhsDate = simpleDateFormat.parse(lhs
									.getRecordTime());
							Date rhsDate = simpleDateFormat.parse(rhs
									.getRecordTime());
							if (lhsDate.before(rhsDate)) {
								return -1;
							} else if (lhsDate.after(rhsDate)) {
								return 1;
							} else {
								return 0;
							}
						} catch (ParseException e) {
							e.printStackTrace();
							return 0;
						}
					}
				});

		for (AbsOfflineBean absOfflineBean : offlineFeedbackedsByTime) {
			pQueue.add(absOfflineBean);
		}
		for (AbsOfflineBean absOfflineBean : offlineUnfeedbacksByTime) {
			pQueue.add(absOfflineBean);
		}

		while (!pQueue.isEmpty()) {
			AbsOfflineBean absOfflineBean = pQueue.peek();
			if (absOfflineBean instanceof OfflineUnfeedback) {
				OfflineUnfeedback offlineUnfeedback = (OfflineUnfeedback) absOfflineBean;
				if (uploadUnfeedback(offlineUnfeedback)) {
					// succeed.
					pQueue.poll();
				} else {
					Log.e(TAG, "上传离线数据失败：Unfeedback记录：Code:"
							+ offlineUnfeedback.getCode() + "/Time:"
							+ offlineUnfeedback.getTime());
					return false;
				}
			} else {
				OfflineFeedbacked offlineFeedbacked=(OfflineFeedbacked)absOfflineBean;
				uploadFeedbacked(offlineFeedbacked);
			}
		}
		return true;
	}

	private boolean uploadUnfeedback(OfflineUnfeedback offlineUnfeedback) {
		FinalHttp finalHttp = new FinalHttp();
		Map<String, String> params = new HashMap<>();
		params.put("token", userSingleton.getValidateToken());
		params.put("treeId", offlineUnfeedback.getCode());
		String resultString = (String) finalHttp.getSync(httpUtils.URL
				+ httpUtils.SET_UFB_STATUS, new AjaxParams(params));
		if (resultString == null || resultString.equals("")) {
			Log.e(TAG, "离线上传，回传为空:" + offlineUnfeedback.getCode());
			return false;
		} else {
			JSONObject resultObject = JSON.parseObject(resultString);
			String statusCode = resultObject.getString("statusCode");
			String feedbackCode = resultObject.getJSONObject("obj").getString(
					"feedbackCode");
			switch (statusCode) {
			case "200":
				Log.i(TAG, "200:离线上传成功：" + offlineUnfeedback.getCode());
				break;
			case "300":
				Log.e(TAG, "300:离线上传失败：" + offlineUnfeedback.getCode());
				return false;
			case "304":
				Log.e(TAG, "304:重复提交未反馈状态变更：" + offlineUnfeedback.getCode());
				break;
			default:
				return false;
			}
			// complete upload . delete the record.
			
			// TODO Database:1.删除离线表中的信息
			
			// 更新feedbackCode
			if (feedbackCode != null && !feedbackCode.equals("")) {
				// TODO Database:更新反馈表中的feedback字段（304的情况下不更新，在重新同步数据的时候会将记录删除）
				// TODO Logic:
				ArrayList<AbsOfflineBean> temp=new ArrayList<>();
				// 检验剩余的队列中是否有
				while (!pQueue.isEmpty()) {
					AbsOfflineBean absOfflineBean=pQueue.peek();
					if (absOfflineBean instanceof OfflineFeedbacked ) {
						OfflineFeedbacked offlineFeedbacked=(OfflineFeedbacked) absOfflineBean;
						if (offlineFeedbacked.getCode().equals(offlineUnfeedback.getCode())) {
							offlineFeedbacked.setFeedbackCode(feedbackCode);
							break;
						}
					}
					temp.add(pQueue.poll());
				}
				
				// 重新从temp中装入上传的任务
				for (AbsOfflineBean absOfflineBean : temp) {
					pQueue.add(absOfflineBean);
				}
				
			}
			return true;
		}
	}
	
	
	private boolean uploadFeedbacked(OfflineFeedbacked	offlineFeedbacked) {
		FinalHttp finalHttp = new FinalHttp();
		Map<String, String> params = new HashMap<>();
		params.put("token", userSingleton.getValidateToken());
		params.put("feedbackcode", offlineFeedbacked.getFeedbackCode());
		params.put("type", offlineFeedbacked.getFeedbackType());
		params.put("companyCode", offlineFeedbacked.getFeedbackCompanyCode());
		params.put("windCode", offlineFeedbacked.getFeedbackWindCode());
		params.put("systemMethod", offlineFeedbacked.getHandleFaultCode());
		params.put("newMethodDes", offlineFeedbacked.getDescription());
		params.put("feedbackTime", offlineFeedbacked.getFeedbackTime());
		String resultString = (String) finalHttp.getSync(httpUtils.URL
				+ httpUtils.FEEDBACK_FT, new AjaxParams(params));
		if (resultString == null || resultString.equals("")) {
			Log.e(TAG, "离线上传，回传为空:" + offlineFeedbacked.getCode());
			return false;
		} else {
			JSONObject resultObject = JSON.parseObject(resultString);
			String statusCode = resultObject.getString("statusCode");
			switch (statusCode) {
			case "200":
				Log.i(TAG, "200:离线上传成功：" + offlineFeedbacked.getCode());
				break;
			case "300":
				Log.e(TAG, "300:离线上传失败：" + offlineFeedbacked.getCode());
				return false;
			case "305":
				Log.e(TAG, "305:重复提交反馈：" + offlineFeedbacked.getCode());
				break;
			default:
				return false;
			}
			// complete upload . delete the record.
			// TODO Database:1.删除离线表中的信息
			return true;
		}
	}

}
