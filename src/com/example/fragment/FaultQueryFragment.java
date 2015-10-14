package com.example.fragment;

import java.util.ArrayList;
import java.util.Date;
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
import com.example.adapter.QueryListViewAdapter;
import com.example.bean.CodeVersionPair;
import com.example.bean.FTBaseInfo;
import com.example.bean.FTFBBaseInfo;
import com.example.bean.OfflineUnfeedback;
import com.example.bean.QueryListItem;
import com.example.common.HttpUtils;
import com.example.common.HttpUtils.FTDownloadListener;
import com.example.singleton.UserSingleton;
import com.example.sqlite.DBDao;
import com.example.tree_component.bean.TreeBean;

public class FaultQueryFragment extends FaultFragment {
	public static final String TAG = "FaultQueryFragment";
	private DBDao dbDao;
	List<QueryListItem> queryListItems = new ArrayList<>();
	private Handler handler = new Handler();
	private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);// 继承父类
		dbDao = new DBDao(getActivity());
		queryConditionCleanBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				faultPheEt.setText("");
				faultTreeCodeEt.setText("");
//				chooseFanBrandTv.setText("");
//				chooseFanTypeTv.setText("");
				chosedFanBrandCode = null;
				chosedFanTypeCode = null;
			}
		});
		queryConfirmBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UserSingleton.getInstance().isSearchable()) {
					final String faultCode = faultTreeCodeEt.getText()
							.toString();
					final String faultKeyword = faultPheEt.getText().toString();
					// 是否有至少一个查询条件
					if (faultCode == "" && faultKeyword == null
							&& chosedFanBrandCode == null
							&& chosedFanTypeCode == null) {
						Toast.makeText(getActivity(), "请输入至少一个查询条件",
								Toast.LENGTH_SHORT).show();
					} else {
						// 是网络查询还是本地查询
						if (httpUtils.getNetStatus() > 0) {
							FinalHttp finalHttp = new FinalHttp();
							Map<String, String> params = new HashMap<>();
							params.put("token",
									userSingleton.getValidateToken());
							params.put("code", "");
							params.put("faultCode", faultCode);
							params.put("faultPhe", faultPheEt.getText()
									.toString());
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
									+ httpUtils.QUERY_FT_LIST, new AjaxParams(
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
												List<FTBaseInfo> ftBaseInfos = new ArrayList<FTBaseInfo>();
												for (Object object : infoArray) {
													JSONObject info = ((JSONObject) object);
													// TODO 666666
													FTBaseInfo ftBaseInfo = null;
													ftBaseInfos.add(ftBaseInfo);
												}
												// 更新或插入数据库，不带详情版本号的。
												dbDao.updateFTBaseInfo(ftBaseInfos);

												// 同步获取最新的版本列表。
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
													queryListItems = compareDetailVersions(
															detailVersions,
															ftBaseInfos);
													// UI线程更新展示列表
													handler.post(new Runnable() {

														@Override
														public void run() {
															resultListView
																	.setAdapter(new QueryListViewAdapter(
																			queryListItems,
																			getActivity(),
																			handler));
															resultListView
																	.setOnItemClickListener(onItemClickListener);
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
									List<FTBaseInfo> resultBaseInfos = dbDao
											.getFtBaseInfosByConditions(
													faultCode, faultKeyword,
													chosedFanBrandCode,
													chosedFanTypeCode);

									for (FTBaseInfo ftBaseInfo : resultBaseInfos) {
										if (ftBaseInfo.getProVersion() == 0) {
											queryListItems
													.add(new QueryListItem(
															ftBaseInfo,
															QueryListViewAdapter.STATUS_TO_DOWNLOAD,
															ftBaseInfo
																	.getProVersion()));
										} else {
											queryListItems
													.add(new QueryListItem(
															ftBaseInfo,
															QueryListViewAdapter.STATUS_DOWNLOADED,
															ftBaseInfo
																	.getProVersion()));
										}
									}

									handler.post(new Runnable() {

										@Override
										public void run() {
											resultListView
													.setAdapter(new QueryListViewAdapter(
															queryListItems,
															getActivity(),
															handler));
											resultListView
													.setOnItemClickListener(onItemClickListener);
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
	 * 对比缓存的详情版本号和最新的详情版本号。生成ListView使用的数据。
	 * 
	 * @param ftBaseInfos
	 *            网络获取的基本信息
	 * @return ListView的数据
	 */
	private List<QueryListItem> compareDetailVersions(
			List<CodeVersionPair> detailVersions, List<FTBaseInfo> ftBaseInfos) {
		List<QueryListItem> queryListItems = new ArrayList<>();
		for (FTBaseInfo ftBaseInfo : ftBaseInfos) {
			// 找到缓存的版本号
			FTBaseInfo cached = dbDao
					.getFTQBaseInfoByCode(ftBaseInfo.getCode());
			// 找到最新的版本号
			int latestVersion = 0;
			for (CodeVersionPair detailVersion : detailVersions) {
				if (detailVersion.getCode().equals(ftBaseInfo.getCode())) {
					// TODO 加入版本号对比
//					latestVersion = Integer.parseInt(detailVersion
//							.getDetailVersion());
					break;
				}
			}
			// 对比
			if (cached == null) {
				// 数据库中不存在该数据
				Log.i(TAG, "出错。找不到该数据！");
			} else {
				// ftBaseInfo存放的是没有版本号的网络基本信息。最新版本号存放于latestVersion中
				if (cached.getProVersion() == 0) {
					// 树不存在，
					queryListItems.add(new QueryListItem(ftBaseInfo,
							QueryListViewAdapter.STATUS_TO_DOWNLOAD,
							latestVersion));
				} else if (cached.getProVersion() < latestVersion) {
					queryListItems.add(new QueryListItem(ftBaseInfo,
							QueryListViewAdapter.STATUS_TO_UPDATE,
							latestVersion));
				} else if (cached.getProVersion() == latestVersion) {
					queryListItems.add(new QueryListItem(ftBaseInfo,
							QueryListViewAdapter.STATUS_DOWNLOADED,
							latestVersion));
				}
			}
		}
		return queryListItems;
	}

	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			final FTBaseInfo ftBaseInfo = queryListItems.get(position)
					.getFtBaseInfo();
			progressDialog = ProgressDialog.show(getActivity(), "正在处理", "请稍后");
			progressDialog.setCancelable(false);
			// 防止耗时操作造成主线程卡顿。之后的操作在子线程中。
			new Thread(new Runnable() {

				@Override
				public void run() {
					FTBaseInfo cachedBaseInfo = dbDao
							.getFTQBaseInfoByCode(ftBaseInfo.getCode());
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
							int latestVersion = getLatestVersion(ftBaseInfo
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
								downloadTree(ftBaseInfo.getCode(),
										latestVersion, 1);
							}
						}
					} else {
						// 数据库中存在树
						if (httpUtils.getNetStatus() == 0) {
							// 没有网,读取缓存树
							TreeBean cachedTreeBean = dbDao
									.getTreeByCode(ftBaseInfo.getCode());
							checkFeedbackStatusAndJump(cachedTreeBean,
									ftBaseInfo.getCode());
						} else {
							// 有网络
							int latestVersion = getLatestVersion(ftBaseInfo
									.getCode());
							if (latestVersion == -1) {
								// 获取失败,不下载，直接显示缓存的树。
								TreeBean cachedTreeBean = dbDao
										.getTreeByCode(ftBaseInfo.getCode());
								checkFeedbackStatusAndJump(cachedTreeBean,
										ftBaseInfo.getCode());
							} else {
								// 获取成功，下载最新的树。
								downloadTree(ftBaseInfo.getCode(),
										latestVersion, 2);
							}
						}
					}
				}
			}).start();

		}
	};

	/**
	 * 下载树 .同步网络线程。这一步，已经确定好最新的版本。
	 * 
	 * @param code
	 *            故障树代码
	 * @param version
	 *            版本
	 * @param model
	 *            1：数据库中不存在树。下载失败时，提示用户。结束。 2.数据库中本来存在树。下载失败时，提示用户，展示旧版本的树。
	 */
	public void downloadTree(final String code, int version, final int model) {
		// 版本获取成功，开始下载故障树
		httpUtils.downloadFT(code,new FTDownloadListener() {

			@Override
			public void onFTDownloadListener(String downloadStatus,
					final TreeBean treeBean) {
				switch (downloadStatus) {
				case HttpUtils.DOWNLOAD_SUCCEED: {
					checkFeedbackStatusAndJump(treeBean, code);
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
								code);
						break;
					}
					break;
				}
				}
			}
		});
	}

	/**
	 * 跳转到故障树展示页面
	 * 
	 * @param treeBean
	 */
	private void jumpToDetailActivity(TreeBean treeBean) {
		final Intent intent = new Intent(getActivity(),
				FaultTreeDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("tree", treeBean);
		intent.putExtras(bundle);
		intent.putExtra("status", "0");
		handler.post(new Runnable() {

			@Override
			public void run() {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				startActivity(intent);
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
	private void checkFeedbackStatusAndJump(TreeBean treeBean, String code) {
		// 截取当前时间
		String time = df.format(new Date(System.currentTimeMillis()));
		// 由于故障树可能已经更新过，重新读一遍基本信息。
		FTBaseInfo baseInfo = dbDao.getFTQBaseInfoByCode(code);
		if (dbDao.isFTUnfeedback(baseInfo.getCode())) {
			// yes
			jumpToDetailActivity(treeBean);
		} else {
			if (httpUtils.getNetStatus() > 0) {
				// 有网
				FinalHttp finalHttp = new FinalHttp();
				Map<String, String> params = new HashMap<>();
				params.put("token", userSingleton.getValidateToken());
				params.put("code", baseInfo.getCode());
				params.put("queryTime", time);
				String t = (String) finalHttp.getSync(httpUtils.URL
						+ httpUtils.SET_UFB_STATUS, new AjaxParams(params));
				if (t == null || t.equals("")) {
					// 访问失败。离线处理。
					offlineCached(baseInfo, time);
					jumpToDetailActivity(treeBean);
				} else {
					JSONObject resultObject = JSON.parseObject((String) t);
					if (resultObject.getString("statusCode").equals("300")) {
						handler.post(new Runnable() {

							@Override
							public void run() {
								if (progressDialog.isShowing()) {
									progressDialog.dismiss();
								}
								Toast.makeText(getActivity(), "服务器拒绝了你的访问：300",
										Toast.LENGTH_SHORT).show();
							}
						});

					} else if (resultObject.getString("statusCode").equals(
							"200")) {
						// 1.加入FTFB表
						// TODO 666666666
						FTFBBaseInfo ftfbBaseInfo =null;
						List<FTFBBaseInfo> ftfbBaseInfos = new ArrayList<>();
						ftfbBaseInfos.add(ftfbBaseInfo);
						dbDao.updateFaultTreeFBBaseInfo(ftfbBaseInfos);
						// 2.更改单例中/DB 未反馈数
						userSingleton.setUnFeedbackNumber(userSingleton
								.getUnFeedbackNumber() + 1);
						dbDao.updateUserUnfeedbackCount(userSingleton
								.getUnFeedbackNumber());
						// 3.jump
						jumpToDetailActivity(treeBean);
					}

				}
			} else {
				// 离线
				offlineCached(baseInfo, time);
				jumpToDetailActivity(treeBean);
			}
		}
	}

	/**
	 * 离线缓存未反馈信息
	 * 
	 * @param baseInfo
	 * @param time
	 */
	private void offlineCached(FTBaseInfo baseInfo, String time) {

		// 1.加入FTFB表
		// TODO 
		FTFBBaseInfo ftfbBaseInfo=null;
//		FTFBBaseInfo ftfbBaseInfo = new FTFBBaseInfo("", baseInfo.getCode(),
//				baseInfo.getMainFaultCode(), baseInfo.getFollowFaultCode(),
//				baseInfo.getChineseName(), baseInfo.getEnglishName(),
//				baseInfo.getFanBrand(), baseInfo.getFanType(), "1", "",
//				baseInfo.getTriggerCondition(), baseInfo.getFaultPhe(),
//				baseInfo.getVersion(), baseInfo.getRemark(), time,
//				baseInfo.getProVersion());
		List<FTFBBaseInfo> ftfbBaseInfos = new ArrayList<>();
		ftfbBaseInfos.add(ftfbBaseInfo);
		dbDao.updateFaultTreeFBBaseInfo(ftfbBaseInfos);
		// 2.加入SET TO UNFEEDBACK表
		OfflineUnfeedback offlineUnfeedback = new OfflineUnfeedback(
				ftfbBaseInfo.getCode(), time);
		dbDao.insertOfflineUnfeedback(offlineUnfeedback);
		// 3.更改单例中/DB 未反馈数
		userSingleton
				.setUnFeedbackNumber(userSingleton.getUnFeedbackNumber() + 1);
		dbDao.updateUserUnfeedbackCount(userSingleton.getUnFeedbackNumber());
	}
}
