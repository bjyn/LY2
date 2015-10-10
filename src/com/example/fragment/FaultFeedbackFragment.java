package com.example.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.activity.FaultTreeDetailActivity;
import com.example.adapter.FeedbackListViewAdapter;
import com.example.adapter.QueryListViewAdapter;
import com.example.bean.CodeVersionPair;
import com.example.bean.FTFBBaseInfo;
import com.example.bean.FanBrand;
import com.example.bean.FanType;
import com.example.bean.FeedbackListItem;
import com.example.common.HttpUtils;
import com.example.common.HttpUtils.FTDownloadListener;
import com.example.lyfaultdiagnosissystem.R;
import com.example.singleton.UserSingleton;
import com.example.sqlite.DBDao;
import com.example.tree_component.bean.TreeBean;

public class FaultFeedbackFragment extends FaultFragment {
	public static final String TAG = "FaultFeedbackFragment";
	private EditText chooseFeedBackStateTv;// 把EditText当做TextView来用
	private ListView chooseFeedbackStateLv;// 选择反馈状态的listview
	private PopupWindow chooseFeedbackStatePopupWindow;
	private List<Map<String, String>> feedbackStatusStrings;
	private String chosedFeedbackStatus = null;
	private DBDao dbDao = new DBDao(getActivity());
	private Handler handler = new Handler();
	private List<FeedbackListItem> feedbackListItems = new ArrayList<>();
	private HttpUtils httpUtils = new HttpUtils(getActivity());
	private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		faultPheEt.setFocusable(false);
		chooseFeedBackStateTv = faultPheEt;
		chooseFeedBackStateTv.setHint("请输入反馈状态");
		setfeedbackStateSpinner(inflater);

		queryConditionCleanBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				faultPheEt.setText("");
				faultTreeCodeEt.setText("");
				chooseFanBrandTv.setText("");
				chooseFanTypeTv.setText("");
				chosedFanBrandCode = null;
				chosedFanTypeCode = null;
				chosedFeedbackStatus = null;
			}
		});

		queryConfirmBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UserSingleton.getInstance().isSearchable()) {
					final String faultCode = faultTreeCodeEt.getText()
							.toString();
					// 是否有至少一个查询条件
					if (faultCode == "" && chosedFeedbackStatus == null
							&& chosedFanBrandCode == null
							&& chosedFanTypeCode == null) {
						Toast.makeText(getActivity(), "请输入至少一个查询条件",
								Toast.LENGTH_SHORT).show();
					} else {
						// 是网络查询还是本地查询
						if (httpUtils.getNetStatus() > 0) {
							progressDialog = ProgressDialog.show(getActivity(),
									"正在查询", "请稍后");
							FinalHttp finalHttp = new FinalHttp();
							Map<String, String> params = new HashMap<>();
							params.put("token",
									userSingleton.getValidateToken());
							params.put("code", "");
							params.put("faultCode", faultCode);
							if (chosedFeedbackStatus != null) {
								switch (chosedFeedbackStatus) {
								case "1":
									params.put("feedBack", "0");
									break;
								case "2":
									params.put("feedBack", "1");
									break;
								default:
									break;
								}
							} else {
								params.put("feedBack", "");
							}
							if (chosedFanBrandCode != null) {
								params.put("fanBrandCode", chosedFanBrandCode);
							} else {
								params.put("fanBrandCode", "");
							}
							if (chosedFanTypeCode != null) {
								params.put("fanTypeCode", chosedFanTypeCode);
							} else {
								params.put("fanTypeCode", null);
							}
							finalHttp.get(httpUtils.URL
									+ httpUtils.QUERY_FBFTLIST, new AjaxParams(
									params), new AjaxCallBack<Object>() {

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
//													FTFBBaseInfo ftfbBaseInfo = new FTFBBaseInfo(
//															info.getString("feedbackcode"),
//															info.getString("code"),
//															info.getString("mainFaultCode"),
//															info.getString("followFaultCode"),
//															info.getString("CName"),
//															info.getString("EName"),
//															new FanBrand(
//																	info.getString("fanBrandName"),
//																	info.getString("fanBrandCode")),
//															new FanType(
//																	info.getString("fanBrandCode"),
//																	info.getString("fanTypeName"),
//																	info.getString("fanTypeCode")),
//															info.getString("feedbackType"),
//															info.getString("feedbackResult"),
//															info.getString("triggerCondition"),
//															info.getString("faultPhe"),
//															info.getInteger("version"),
//															info.getString("remark"),
//															info.getString("queryTime"),
//															0);// 获取的网络基本信息中不含有详细版本号。先置为0
//													ftfbBaseInfos
//															.add(ftfbBaseInfo);
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
													// 对比版本，获取展示列表的数据
													feedbackListItems = compareDetailVersions(
															detailVersions,
															ftfbBaseInfos);
													// UI线程更新展示列表
													handler.post(new Runnable() {

														@Override
														public void run() {
															resultListView
																	.setAdapter(new FeedbackListViewAdapter(
																			feedbackListItems,
																			getActivity(),
																			handler));
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
											.getFtfbBaseInfosByConditions(
													faultCode,
													chosedFeedbackStatus,
													chosedFanBrandCode,
													chosedFanTypeCode);

									for (FTFBBaseInfo ftfbBaseInfo : resultBaseInfos) {
										if (ftfbBaseInfo.getProVersion() == 0) {
											feedbackListItems
													.add(new FeedbackListItem(
															ftfbBaseInfo,
															FeedbackListViewAdapter.STATUS_TO_DOWNLOAD,
															ftfbBaseInfo
																	.getProVersion()));
										} else {
											feedbackListItems
													.add(new FeedbackListItem(
															ftfbBaseInfo,
															FeedbackListViewAdapter.STATUS_DOWNLOADED,
															ftfbBaseInfo
																	.getProVersion()));
										}
									}

									handler.post(new Runnable() {

										@Override
										public void run() {
											resultListView
													.setAdapter(new FeedbackListViewAdapter(
															feedbackListItems,
															getActivity(),
															handler));
										}
									});

								}
							}).start();

						}

					}
				} else {
					Toast.makeText(getActivity(), "未反馈任务较多，无法继续查询",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		return view;
	}

	/**
	 * 自定义spinner载入函数，通过popup listview的方式实现
	 * 
	 * @param inflater
	 *            布局填充器
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	private void setfeedbackStateSpinner(LayoutInflater inflater) {
		{
			feedbackStatusStrings = new ArrayList<>();
			Map<String, String> map1 = new HashMap<>();
			map1.put("name", "未反馈");
			Map<String, String> map2 = new HashMap<>();
			map2.put("name", "已反馈");
			feedbackStatusStrings.add(map1);
			feedbackStatusStrings.add(map2);
			View viewInFeedBackStatePopup = inflater.inflate(
					R.layout.popupwindow_within_feedback_state_list, null);
			chooseFeedbackStateLv = (ListView) viewInFeedBackStatePopup
					.findViewById(R.id.feedback_state_listview_in_popup);
			SimpleAdapter feedbackStateAdapter = new SimpleAdapter(
					getActivity(), feedbackStatusStrings,
					R.layout.item_listview_in_popup, new String[] { "name" },
					new int[] { R.id.popup_listview_item_tv });
			chooseFeedbackStateLv.setAdapter(feedbackStateAdapter);
			chooseFeedbackStateLv
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int position, long arg3) {
							switch (position) {
							case 1:
								chosedFeedbackStatus = "1";
								chooseFeedBackStateTv.setText("未反馈");
								break;
							case 2:
								chosedFeedbackStatus = "2";
								chooseFeedBackStateTv.setText("已反馈");
								break;
							default:
								break;
							}
							chooseFeedbackStatePopupWindow.dismiss();
						}
					});
			chooseFeedbackStatePopupWindow = new PopupWindow(
					viewInFeedBackStatePopup, 310, LayoutParams.WRAP_CONTENT);
			chooseFeedbackStatePopupWindow
					.setAnimationStyle(R.style.popupwindow_anim_style);
			// 只有在设置了popupwindow的背景后，点击popupwindow外面才会消失
			chooseFeedbackStatePopupWindow
					.setBackgroundDrawable(new BitmapDrawable());
			chooseFeedbackStatePopupWindow.setFocusable(true);
			// 设置自定义下拉列表打开关闭事件
			OnClickListener chooseFeedbackStateClickListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.i("ftview", "点击事件相应");
					if (chooseFeedbackStatePopupWindow.isShowing()) {
						chooseFeedbackStatePopupWindow.dismiss();
					} else {
						chooseFeedbackStatePopupWindow
								.showAsDropDown(chooseFeedBackStateTv);
					}
				}
			};
			chooseFeedBackStateTv
					.setOnClickListener(chooseFeedbackStateClickListener);
		}
	}

	/**
	 * item监听器
	 */
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			final FTFBBaseInfo ftfbBaseInfo = feedbackListItems.get(position)
					.getFtfbBaseInfo();
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

	/**
	 * 对比缓存的详情版本号和最新的详情版本号。生成ListView使用的数据。
	 * 
	 * @param ftBaseInfos
	 *            网络获取的基本信息
	 * @return ListView的数据
	 */
	private List<FeedbackListItem> compareDetailVersions(
			List<CodeVersionPair> detailVersions, List<FTFBBaseInfo> ftfbBaseInfos) {
		for (FTFBBaseInfo ftfbBaseInfo : ftfbBaseInfos) {
			// 找到缓存的版本号
			FTFBBaseInfo cached = dbDao.getFtfbBaseInfoByCode(
					ftfbBaseInfo.getCode(), ftfbBaseInfo.getLookTime());
			// 找到最新的版本号
			int latestVersion = 0;
			for (CodeVersionPair detailVersion : detailVersions) {
				if (detailVersion.getCode().equals(ftfbBaseInfo.getCode())) {
					// TODO 加入版本号对比
					break;
				}
			}
			// 对比
			if (cached == null) {
				// 数据库中不存在该数据
				Log.i(TAG, "出错。找不到该数据！");
			} else {
				// ftfb存放的是没有版本号的网络基本信息。最新版本号存放于latestVersion中
				if (cached.getProVersion() == 0) {
					// 树不存在，
					feedbackListItems.add(new FeedbackListItem(ftfbBaseInfo,
							QueryListViewAdapter.STATUS_TO_DOWNLOAD,
							latestVersion));
				} else if (cached.getProVersion() < latestVersion) {
					feedbackListItems.add(new FeedbackListItem(ftfbBaseInfo,
							QueryListViewAdapter.STATUS_TO_UPDATE,
							latestVersion));
				} else if (cached.getProVersion() == latestVersion) {
					feedbackListItems.add(new FeedbackListItem(ftfbBaseInfo,
							QueryListViewAdapter.STATUS_DOWNLOADED,
							latestVersion));
				}
			}
		}
		return feedbackListItems;
	}

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