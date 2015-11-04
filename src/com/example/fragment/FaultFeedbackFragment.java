package com.example.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.activity.FaultTreeDetailActivity;
import com.example.adapter.FeedbackListViewAdapter;
import com.example.adapter.QueryListViewAdapter;
import com.example.bean.CodeVersionPair;
import com.example.bean.FTFBBaseInfo;
import com.example.bean.FeedbackListItem;
import com.example.common.HttpUtils;
import com.example.common.HttpUtils.FTDownloadListener;
import com.example.singleton.UserSingleton;
import com.example.sqlite.DBDao;
import com.example.tree_component.bean.TreeBean;

public class FaultFeedbackFragment extends FaultFragment {
	// TODO 未反馈和已反馈对应的代码是什么
	public static final String UN_FEEDBACK = "0";
	public static final String FEEDBACKED = "1";
	public static final String TAG = "FaultFeedbackFragment";
	private DBDao dbDao = new DBDao(getActivity());
	private Handler handler = new Handler();
	private List<FTFBBaseInfo> feedbackListItems = new ArrayList<>();
	private HttpUtils httpUtils = new HttpUtils(getActivity());
	private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		searchByFaultCodeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.fragment.FaultFragment#queryFunction(java.lang.String,
	 * java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void queryFunction(final String faultCode,
			final Object feedbackStatus, final Object fanBrandCodeObject,
			final Object fanTypeCodeObject) {
		super.queryFunction(faultCode, feedbackStatus, fanBrandCodeObject,
				fanTypeCodeObject);
		if (UserSingleton.getInstance().isSearchable()) {
			// 是否有至少一个查询条件
			if (faultCode == "" && feedbackStatus == null
					&& fanBrandCodeObject == null && fanTypeCodeObject == null) {
				Toast.makeText(getActivity(), "请输入至少一个查询条件", Toast.LENGTH_SHORT)
						.show();
			} else {
				// 是网络查询还是本地查询
				if (httpUtils.getNetStatus() > 0) {
					progressDialog = ProgressDialog.show(getActivity(), "正在查询",
							"请稍后");
					FinalHttp finalHttp = new FinalHttp();
					Map<String, String> params = new HashMap<>();
					params.put("token", userSingleton.getValidateToken());
					params.put("code", "");
					params.put("faultCode", faultCode);
					if (feedbackStatus != null) {
						params.put("feedBack", (String) feedbackStatus);
					} else {
						params.put("feedBack", "");
					}
					// null值强制类型转换后仍未null
					params.put("fanBrandCode", (String) fanBrandCodeObject);
					params.put("fanTypeCode", (String) fanTypeCodeObject);
					finalHttp.get(httpUtils.URL + httpUtils.QUERY_FBFTLIST,
							new AjaxParams(params), new AjaxCallBack<Object>() {

								@Override
								public void onFailure(Throwable t, int errorNo,
										String strMsg) {
									httpUtils.doFailure(t, errorNo, strMsg,
											getActivity(), TAG);
									Toast.makeText(getActivity(),
											"查询失败。请检查网络设置。", Toast.LENGTH_SHORT)
											.show();
									super.onFailure(t, errorNo, strMsg);
								}

								@Override
								public void onSuccess(Object t) {
									JSONObject resultObject = JSON
											.parseObject((String) t);
									String status = resultObject
											.getString("statusCode");
									final JSONObject obj = resultObject
											.getJSONObject("obj");
									if (status.equals("300")) {
										Toast.makeText(
												getActivity(),
												"查询失败。"
														+ resultObject
																.getString("message"),
												Toast.LENGTH_SHORT).show();
									} else if (status.equals("200")) {
										// 新线程解析。插入或更新数据库。注意更新时不能覆盖原有版本。
										new Thread(new Runnable() {

											@Override
											public void run() {
												// 手动解析。。。。。。
												JSONArray infoArray = obj
														.getJSONArray("FTInfo");
												List<FTFBBaseInfo> ftfbBaseInfos = new ArrayList<FTFBBaseInfo>();
												for (Object object : infoArray) {
													JSONObject info = ((JSONObject) object);
													// TODO 手动解析啊啊啊啊啊
													// FTFBBaseInfo ftfbBaseInfo
													// = new FTFBBaseInfo(
													// info.getString("feedbackcode"),
													// info.getString("code"),
													// info.getString("mainFaultCode"),
													// info.getString("followFaultCode"),
													// info.getString("CName"),
													// info.getString("EName"),
													// new FanBrand(
													// info.getString("fanBrandName"),
													// info.getString("fanBrandCode")),
													// new FanType(
													// info.getString("fanBrandCode"),
													// info.getString("fanTypeName"),
													// info.getString("fanTypeCode")),
													// info.getString("feedbackType"),
													// info.getString("feedbackResult"),
													// info.getString("triggerCondition"),
													// info.getString("faultPhe"),
													// info.getInteger("version"),
													// info.getString("remark"),
													// info.getString("queryTime"),
													// 0);//
													// 获取的网络基本信息中不含有详细版本号。先置为0
													// ftfbBaseInfos
													// .add(ftfbBaseInfo);
												}
												// 更新或插入数据库
												dbDao.updateFaultTreeFBBaseInfo(ftfbBaseInfos);
												// 同步获取最新的版本列表
												List<CodeVersionPair> detailVersions = getLatestDetailVersions();

												if (detailVersions == null) {
													handler.post(new Runnable() {

														@Override
														public void run() {
															Toast.makeText(
																	getActivity(),
																	"查询失败",
																	Toast.LENGTH_SHORT)
																	.show();
														}
													});
													return;
												} else {
													// 将网络访问得到的数组保存在本地,设置onClickListener时使用
													feedbackListItems = ftfbBaseInfos;
													// UI线程更新展示列表
													handler.post(new Runnable() {

														@Override
														public void run() {
															resultListView
																	.setAdapter(new FeedbackListViewAdapter(
																			feedbackListItems,
																			getActivity()));
														}
													});
												}

											}
										}).start();
									}
									super.onSuccess(t);
								}

							});
				} else {
					new Thread(new Runnable() {

						@Override
						public void run() {
							// 离线状态：数据库查询
							List<FTFBBaseInfo> resultBaseInfos = dbDao
									.getFtfbBaseInfosByConditions(faultCode,
											(String) feedbackStatus,
											(String) fanBrandCodeObject,
											(String) fanTypeCodeObject);
							// 将数据库访问得到的数组保存在本地,设置onClickListener时使用
							feedbackListItems = resultBaseInfos;
							handler.post(new Runnable() {
								@Override
								public void run() {
									resultListView
											.setAdapter(new FeedbackListViewAdapter(
													feedbackListItems,
													getActivity()));
								}
							});
						}
					}).start();

				}

			}
		} else {
			Toast.makeText(getActivity(), "未反馈任务较多，无法继续查询", Toast.LENGTH_SHORT)
					.show();
		}

	}

	/**
	 * item监听器
	 */
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			final FTFBBaseInfo ftfbBaseInfo = feedbackListItems.get(position);
			progressDialog = ProgressDialog.show(getActivity(), "正在处理", "请稍后");
			progressDialog.setCancelable(false);
			// 防止耗时操作造成主线程卡顿。之后的操作在子线程中。
			new Thread(new Runnable() {

				@Override
				public void run() {
					FTFBBaseInfo cachedBaseInfo = dbDao.getFtfbBaseInfoByCode(
							ftfbBaseInfo.getCode(), ftfbBaseInfo.getLookTime());
					// 判断有没有树
					if (cachedBaseInfo.getProVersion() == 0) {
						// 树不存在
						if (httpUtils.getNetStatus() == 0) {
							// 无网
							handler.post(new Runnable() {

								@Override
								public void run() {
									if (progressDialog.isShowing()) {
										progressDialog.dismiss();
									}
									Toast.makeText(getActivity(),
											"故障树未下载。请检查网络。", Toast.LENGTH_SHORT)
											.show();
								}
							});
							return;// 跳出这个线程
						} else {
							// 有网
							int latestVersion = getLatestVersion(ftfbBaseInfo
									.getCode());
							if (latestVersion == -1) {
								// 获取失败
								handler.post(new Runnable() {

									@Override
									public void run() {
										if (progressDialog.isShowing()) {
											progressDialog.dismiss();
										}
										Toast.makeText(getActivity(),
												"获取版本号失败。请稍后重试。",
												Toast.LENGTH_SHORT).show();
									}
								});
								return;
							} else {
								downloadTree(ftfbBaseInfo.getCode(),
										ftfbBaseInfo.getLookTime(),
										latestVersion, 1);
							}
						}
					} else {
						// 数据库中存在树
						if (httpUtils.getNetStatus() == 0) {
							// 没有网,读取缓存树
							TreeBean cachedTreeBean = dbDao
									.getTreeByCode(ftfbBaseInfo.getCode());
							checkFeedbackStatusAndJump(cachedTreeBean,
									ftfbBaseInfo.getCode(),
									ftfbBaseInfo.getLookTime());
						} else {
							// 有网络
							int latestVersion = getLatestVersion(ftfbBaseInfo
									.getCode());
							if (latestVersion == -1) {
								// 获取失败,不下载，直接显示缓存的树。
								TreeBean cachedTreeBean = dbDao
										.getTreeByCode(ftfbBaseInfo.getCode());
								checkFeedbackStatusAndJump(cachedTreeBean,
										ftfbBaseInfo.getCode(),
										ftfbBaseInfo.getLookTime());
							} else {
								// 获取成功，下载最新的树。
								downloadTree(ftfbBaseInfo.getCode(),
										ftfbBaseInfo.getLookTime(),
										latestVersion, 2);
							}
						}
					}
				}
			}).start();

		}
	};

	// TODO 此方法只在有detailVersion时有用
	/**
	 * 对比缓存的详情版本号和最新的详情版本号。生成ListView使用的数据。
	 * 
	 * @param ftBaseInfos
	 *            网络获取的基本信息
	 * @return ListView的数据
	 */
	// private List<FeedbackListItem> compareDetailVersions(
	// List<CodeVersionPair> detailVersions,
	// List<FTFBBaseInfo> ftfbBaseInfos) {
	// for (FTFBBaseInfo ftfbBaseInfo : ftfbBaseInfos) {
	// // 找到缓存的版本号
	// FTFBBaseInfo cached = dbDao.getFtfbBaseInfoByCode(
	// ftfbBaseInfo.getCode(), ftfbBaseInfo.getLookTime());
	// // 找到最新的版本号
	// int latestVersion = 0;
	// for (CodeVersionPair detailVersion : detailVersions) {
	// if (detailVersion.getCode().equals(ftfbBaseInfo.getCode())) {
	// // TODO 加入版本号对比
	// break;
	// }
	// }
	// // 对比
	// if (cached == null) {
	// // 数据库中不存在该数据
	// Log.i(TAG, "出错。找不到该数据！");
	// } else {
	// // ftfb存放的是没有版本号的网络基本信息。最新版本号存放于latestVersion中
	// if (cached.getProVersion() == 0) {
	// // 树不存在，
	// feedbackListItems.add(new FeedbackListItem(ftfbBaseInfo,
	// QueryListViewAdapter.STATUS_TO_DOWNLOAD,
	// latestVersion));
	// } else if (cached.getProVersion() < latestVersion) {
	// feedbackListItems.add(new FeedbackListItem(ftfbBaseInfo,
	// QueryListViewAdapter.STATUS_TO_UPDATE,
	// latestVersion));
	// } else if (cached.getProVersion() == latestVersion) {
	// feedbackListItems.add(new FeedbackListItem(ftfbBaseInfo,
	// QueryListViewAdapter.STATUS_DOWNLOADED,
	// latestVersion));
	// }
	// }
	// }
	// return feedbackListItems;
	// }

	/**
	 * 下载树 .同步网络线程。这一步，已经确定好最新的版本。
	 * 
	 * @param code
	 *            故障树代码
	 * @param version
	 *            时间
	 * @param version
	 *            版本
	 * 
	 * @param model
	 *            1：数据库中不存在树。下载失败时，提示用户。结束。 2.数据库中本来存在树。下载失败时，提示用户，展示旧版本的树。
	 */
	public void downloadTree(final String code, final String time, int version,
			final int model) {
		// 版本获取成功，开始下载故障树
		httpUtils.downloadFT(code, new FTDownloadListener() {

			@Override
			public void onFTDownloadListener(String downloadStatus,
					final TreeBean treeBean) {
				switch (downloadStatus) {
				case HttpUtils.DOWNLOAD_SUCCEED: {
					checkFeedbackStatusAndJump(treeBean, code, time);
					break;
				}
				case HttpUtils.DOWNLOAD_FAILED: {
					switch (model) {
					case 1:
						// 1：数据库中不存在树。下载失败时，提示用户。结束。
						handler.post(new Runnable() {

							@Override
							public void run() {
								if (progressDialog.isShowing()) {
									progressDialog.dismiss();
								}
								Toast.makeText(getActivity(), "故障树下载失败。请稍后重试。",
										Toast.LENGTH_SHORT).show();
							}
						});
						break;
					case 2:
						// 2.数据库中本来存在树。下载失败时，提示用户，展示旧版本的树。
						checkFeedbackStatusAndJump(dbDao.getTreeByCode(code),
								code, time);
						break;
					}
					break;
				}
				}
			}
		});
	}

	/**
	 * 检查反馈情况后跳转到展示页面。已经确定了数据库中有树，而且树已经更新到最新（如果可以的话）。
	 * 
	 * @param treeBean
	 *            树模型
	 * @param code
	 *            故障树代码
	 */
	private void checkFeedbackStatusAndJump(TreeBean treeBean, String code,
			String time) {
		// 由于故障树可能已经更新过，重新读一遍基本信息。
		FTFBBaseInfo ftfbBaseInfo = dbDao.getFtfbBaseInfoByCode(code, time);
		if (ftfbBaseInfo.getFeedbackStatus().equals("1")) {
			// 未反馈：
			Intent intent = new Intent(getActivity(),
					FaultTreeDetailActivity.class);
			intent.putExtra("status", "1");
			Bundle bundle = new Bundle();
			bundle.putSerializable("tree", treeBean);
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			startActivity(intent);

		} else {
			// 已反馈：
			Intent intent = new Intent(getActivity(),
					FaultTreeDetailActivity.class);
			intent.putExtra("status", "2");
			Bundle bundle = new Bundle();
			bundle.putSerializable("tree", treeBean);
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			startActivity(intent);
		}
	}

}