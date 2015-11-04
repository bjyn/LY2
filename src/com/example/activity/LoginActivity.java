package com.example.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.bean.FTFBBaseInfo;
import com.example.bean.FanBrand;
import com.example.bean.FanType;
import com.example.bean.UnfeedbackFT;
import com.example.bean.UserInfo;
import com.example.common.EncryptUtil;
import com.example.common.HttpUtils;
import com.example.common.MD5Encoder;
import com.example.common.OfflineDataUploadTool;
import com.example.lyfaultdiagnosissystem.R;
import com.example.singleton.UserSingleton;
import com.example.sqlite.DBDao;
import com.example.sqlite.MyDatabaseHelper;

/**
 * @author steven 登录界面
 */
public class LoginActivity extends Activity {
	public static final String TAG = "LoginActivity";
	private EditText userCodeEt;// 用户名输入
	private EditText pwdEt;// 用户密码输入
	private Button loginButton;// 登录按钮
//	private TextView versionTv;// 显示版本
	private ProgressDialog progressDialog;// 环形进度条
	private DBDao dbDao;// dao
	private HttpUtils httpUtils = new HttpUtils(this);
	private Handler handler = new Handler();
	private UserSingleton userSingleton = UserSingleton.getInstance();

	// Temp
	private String tokenBase64;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		userCodeEt = (EditText) findViewById(R.id.user_code_et);
		pwdEt = (EditText) findViewById(R.id.user_pwd_et);
		loginButton = (Button) findViewById(R.id.load_btn);
//		versionTv = (TextView) findViewById(R.id.load_version_tv);
//		versionTv.setText("当前版本号：" + getAppVersion());
		// 初始化数据库
		initiaDatabase();
		dbDao = new DBDao(LoginActivity.this);

		// 登录点击事件
		OnClickListener loadOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				progressDialog = ProgressDialog.show(LoginActivity.this,
						"正在访问网络", "请稍后");
				progressDialog.setCancelable(false);
				final String userCode = userCodeEt.getText().toString();
				final String userPwd = pwdEt.getText().toString();
				if (userCode == null || userCode == "" || userPwd == null
						|| userPwd == "") {
					Toast.makeText(LoginActivity.this, "登陆信息不能为空",
							Toast.LENGTH_SHORT).show();
					return;
				}
				final String pwdMD5 = MD5Encoder.get32lowerMD5(userPwd);
				final String userCodeMD5 = MD5Encoder.get32lowerMD5(userCode);
				String tokenAfterMD5 = userCodeMD5
						+ "&"
						+ pwdMD5
						+ "&"
						+ MD5Encoder
								.get32lowerMD5(((TelephonyManager) LoginActivity.this
										.getSystemService(TELEPHONY_SERVICE))
										.getDeviceId());
				

				try {
					String tokenAfterDES = EncryptUtil.encryptDES(
							tokenAfterMD5, "bjynsyyn");
					tokenBase64 = com.example.common.Base64
							.encode(tokenAfterDES.getBytes());
				} catch (Exception e) {
					Log.i("ft", "DES加密异常");
					e.printStackTrace();
				}

				if (httpUtils.getNetStatus() > 0) {

					// 1.上传离线纪录
					UserInfo userInfo = dbDao.getUserInfo();
					if (userInfo != null) {
						// 如果不为空
						String token = MD5Encoder.get32lowerMD5(userInfo
								.getUserCode())
								+ "&"
								+ userInfo.getUserPassword()
								+ "&"
								+ MD5Encoder
										.get32lowerMD5(((TelephonyManager) LoginActivity.this
												.getSystemService(TELEPHONY_SERVICE))
												.getDeviceId());
						userSingleton.setValidateToken(token);
						final OfflineDataUploadTool offlineDataUploadTool = new OfflineDataUploadTool(
								LoginActivity.this);
						progressDialog = ProgressDialog.show(
								LoginActivity.this, "正在进行网络访问", "请稍后");
						new Thread(new Runnable() {
							@Override
							public void run() {
								if (offlineDataUploadTool
										.uploadSyncOfflineData() == true) {
									// 离线纪录全部成功上传前。不能登录。
									// 2.
									// validate the code and password online
									// when net available
									Map<String, String> params = new HashMap<>();
									params.put("token", tokenBase64);
									FinalHttp finalHttp = new FinalHttp();
									String result = (String) finalHttp.getSync(
											httpUtils.URL + httpUtils.LOGIN,
											new AjaxParams(params));
									if (result.equals("") || result == null) {
										// 出错。
										handler.post(new Runnable() {

											@Override
											public void run() {
												if (progressDialog.isShowing()) {
													progressDialog.dismiss();
												}
												Toast.makeText(
														LoginActivity.this,
														"登陆失败：请检查网络。",
														Toast.LENGTH_SHORT)
														.show();
											}
										});
									} else {
										JSONObject resultObject = JSON
												.parseObject(result);
										String statusCode = resultObject
												.getString("statusCode");
										if (statusCode.equals("300")) {
											Log.e(HttpUtils.TAG,
													"300:"
															+ resultObject
																	.getString("message"));
											handler.post(new Runnable() {

												@Override
												public void run() {
													if (progressDialog
															.isShowing()) {
														progressDialog
																.dismiss();
													}
													Toast.makeText(
															LoginActivity.this,
															"登陆失败：请检查网络。",
															Toast.LENGTH_SHORT)
															.show();
												}
											});
										} else if (statusCode.equals("200")) {
											// 成功.
											final JSONObject resultJsonObject = JSON
													.parseObject(result);
											if (resultJsonObject
													.getString("statusCode") == "200") {
												// validate
												// succeed.cached the
												// userCode
												// and password
												userSingleton
														.setValidateToken(tokenBase64);
												JSONObject obj = resultJsonObject
														.getJSONObject("obj");

												// 1.cache the info in
												// memory
												String userName = obj
														.getString("username");
												int limitUFBNum = obj
														.getIntValue("limitUFBNum");
												int currentUFBNum = obj
														.getIntValue("currentUFBNum");

												JSONArray brandArray = resultJsonObject
														.getJSONArray("fanBrand");
												List<FanBrand> fanBrands = new ArrayList<>();
												for (Object brandJsonObject : brandArray) {
													FanBrand fanBrand = new FanBrand(
															((JSONObject) brandJsonObject)
																	.getString("code"),
															((JSONObject) brandJsonObject)
																	.getString("name"));
													fanBrands.add(fanBrand);
												}

												JSONArray fanArray = resultJsonObject
														.getJSONArray("fanType");
												List<FanType> fanList = new ArrayList<>();
												for (Object fanObject : fanArray) {
													FanType fanType = new FanType(
															((JSONObject) fanObject)
																	.getString("name"),
															((JSONObject) fanObject)
																	.getString("code"),
															((JSONObject) fanObject)
																	.getString("parentCode"));
													fanList.add(fanType);
												}

												List<UnfeedbackFT> latestUnfeedbackFTs = new ArrayList<>();
												JSONArray unfeedbackArray = resultJsonObject
														.getJSONArray("unFBFTArray");
												// TODO +66666666666666
//												for (Object unfeedbackObject : unfeedbackArray) {
//													UnfeedbackFT unfeedbackFT = new UnfeedbackFT(
//															((JSONObject) unfeedbackObject)
//																	.getString("code"),
//															((JSONObject) unfeedbackObject)
//																	.getIntValue("version"));
//													latestUnfeedbackFTs
//															.add(unfeedbackFT);
//												}

												userSingleton
														.setUserName(userName);
												userSingleton
														.setUnFeedbackNumber(currentUFBNum);
												userSingleton
														.setLimitFeedbackNumber(limitUFBNum);

												// 2.cache to database
												String currentTime = (String) DateFormat
														.format("yyyy-MM-dd HH:mm:SS",
																new Date());
												// 插入或更新用户信息
												// FIXME USER LAST TIME
												// TODO　666666666666666666666
												dbDao.updateUserInfo(null);

												dbDao.insertFanBrandInfo(fanBrands);
												dbDao.insertFanTypeInfo(fanList);

												// 3.compared the latest
												// unfeedback
												// version to the local
												// ones
												updateUnfeedbackBaseInfo(latestUnfeedbackFTs);

												// 4.jump to the main
												// activity
												handler.post(new Runnable() {

													@Override
													public void run() {
														if (progressDialog
																.isShowing()) {
															progressDialog
																	.dismiss();
														}

														Intent intent = new Intent(
																LoginActivity.this,
																MainActivity.class);
														intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
														startActivity(intent);
													}
												});

											} else {
												handler.post(new Runnable() {

													@Override
													public void run() {
														Toast.makeText(
																LoginActivity.this,
																resultJsonObject
																		.getString("message"),
																Toast.LENGTH_SHORT)
																.show();
													}
												});

											}

											handler.post(new Runnable() {

												@Override
												public void run() {
													if (progressDialog
															.isShowing()) {
														progressDialog
																.dismiss();
													}
												}
											});
										} else {
											Log.e(HttpUtils.TAG,
													"未知错误。成功返回。但是解析错误？");
										}
									}
								} else {
									handler.post(new Runnable() {
										@Override
										public void run() {
											if (progressDialog.isShowing()) {
												progressDialog.dismiss();
											}
											Toast.makeText(LoginActivity.this,
													"离线纪录上传失败，请稍后重试",
													Toast.LENGTH_SHORT).show();
										}
									});

								}
							}
						}).start();
					}

				} else {
					// 离线
					List<String> codeAndPwd = dbDao.getCodeAndPwd();
					if (codeAndPwd.get(0).equals(userCode)
							&& codeAndPwd.get(1).equals(pwdMD5)) {
						// 1.生成身份令牌
						userSingleton.setValidateToken(tokenBase64);
						// 2.加载用户信息,品牌与风机型号
						UserInfo userInfo = dbDao.getUserInfo();
						userSingleton.setUserName(userInfo.getUserName());
						userSingleton.setUnFeedbackNumber(userInfo
								.getUserCurrentUnfeedbackCount());
						userSingleton.setLimitFeedbackNumber(userInfo
								.getUserLimitUnfeedbackCount());
						List<FanBrand> fanBrands = dbDao.getFanBrand();
						if (fanBrands.size() == 0) {
							Log.i(DBDao.TAG, "获取品牌列表为空！");
						} else {
							userSingleton.setFanBrands(fanBrands);
						}

						List<FanType> fanTypes = dbDao.getFanType();
						if (fanTypes.size() == 0) {
							Log.i(DBDao.TAG, "获取风机型号列表为空");
						} else {
							userSingleton.setFanTypes(fanTypes);
						}

						Intent intent = new Intent(LoginActivity.this,
								MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						startActivity(intent);
					} else {
						Toast.makeText(LoginActivity.this, "用户名或密码错误",
								Toast.LENGTH_SHORT).show();
					}

				}

			}
		};
		loginButton.setOnClickListener(loadOnClickListener);
	}

	/**
	 * 初始化数据库，将assetss中创建好的数据库文件复制到应用程序包中去
	 * 
	 */
	private void initiaDatabase() {
		MyDatabaseHelper databaseHelper = new MyDatabaseHelper(
				LoginActivity.this);
		try {
			databaseHelper.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		databaseHelper.openDataBase();
	}

	/**
	 * 得到应用版本号
	 * 
	 * @return 应用的版本号
	 */
	private String getAppVersion() {
		PackageManager packageManager = getPackageManager();
		String version;
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					this.getPackageName(), 0);
			version = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			version = "获取包名出错";
		}
		return version;
	}

	/**
	 * 对比并异步更新未反馈树的基本信息
	 * 
	 * @param latestUnfeedbackFTs
	 */
	private void updateUnfeedbackBaseInfo(List<UnfeedbackFT> latestUnfeedbackFTs) {
		List<FTFBBaseInfo> cachedUnfeedbackBaseInfos = dbDao
				.getUnfeedbackBaseInfos();
		for (UnfeedbackFT latestUnfeedbackFT : latestUnfeedbackFTs) {
			for (FTFBBaseInfo cachedFtfbBaseInfo : cachedUnfeedbackBaseInfos) {
				if (cachedFtfbBaseInfo.getCode().equals(
						latestUnfeedbackFT.getCode())) {
					// compare
					final String code = cachedFtfbBaseInfo.getCode();
					final String fanBrand = cachedFtfbBaseInfo.getFanBrand()
							.getCode();
					final String fanType = cachedFtfbBaseInfo.getFanType()
							.getCode();
					// TODO 66666666666666666
					if (/*latestUnfeedbackFT.getVersion() > cachedFtfbBaseInfo
							.getVersion()*/true) {
						new Thread(new Runnable() {

							@Override
							public void run() {
								FinalHttp finalHttp = new FinalHttp();
								Map<String, String> params = new HashMap<>();
								params.put("token",
										userSingleton.getValidateToken());
								params.put("code", code);
								params.put("feedBack", "0");// 设置未反馈
								params.put("fanBrandCode", fanBrand);
								params.put("fanTypeCode", fanType);
								params.put("faultCode", "");
								finalHttp.get(httpUtils.URL
										+ httpUtils.QUERY_FBFTLIST,
										new AjaxParams(params),
										new AjaxCallBack<Object>() {

											@Override
											public void onFailure(Throwable t,
													int errorNo, String strMsg) {
												httpUtils
														.doFailure(
																t,
																errorNo,
																strMsg,
																LoginActivity.this,
																TAG);
												super.onFailure(t, errorNo,
														strMsg);
											}

											@Override
											public void onSuccess(Object t) {
												// parse the JSON
												JSONObject resultJsonObject = JSON
														.parseObject((String) t);
												if (!resultJsonObject
														.getString("statusCode")
														.equals("200")) {
													Log.i(HttpUtils.TAG,
															"300:最新数据下载失败");
												} else if (resultJsonObject
														.getString("statusCode")
														.equals("300")) {
													Log.i(HttpUtils.TAG, "200:"
															+ (String) t);
													JSONArray resultArray = resultJsonObject
															.getJSONArray("FTInfo");
													if (resultArray.size() != 0) {
														JSONObject resultObject = resultArray
																.getJSONObject(0);
														FTFBBaseInfo ftfbBaseInfo = null;
														// “获取反馈故障树信息”的网络接口中未提供故障树的版本.当作0。在更新数据库的时候不修改详情版本号。
														// 也无法获得该反馈故障树的阅读时间，置为“null”
														// 更新数据库
														dbDao.updateUnfeedbackBaseInfo(ftfbBaseInfo);
													} else {
														Log.i(TAG,
																"array is empty!");
													}
												}

												super.onSuccess(t);
											}
										});

							}
						}).start();

					}
				}
			}
		}
	}

}
