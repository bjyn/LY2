package com.example.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxParams;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.bean.Company;
import com.example.bean.Wind;
import com.example.common.HttpUtils;
import com.example.common.OfflineDataUploadTool;
import com.example.lyfaultdiagnosissystem.R;
import com.example.singleton.UserSingleton;

public class FeedbackActivity extends Activity {

	// constants
	public static final String SYS_SOLUTION = "1";
	public static final String NEW_SOLUTION = "2";
	public static final String TAG = "FeedbackActivity";
	private HttpUtils httpUtils = new HttpUtils(this);
	private UserSingleton userSingleton = UserSingleton.getInstance();
	private OfflineDataUploadTool offlineDataUploadTool = new OfflineDataUploadTool(
			this);
	private Handler handler = new Handler();

	// widgets
	private Button feedbackButton;
	private TextView fanTypeTextView;
	private TextView windTextView;
	private TextView companyTextView;
	private TextView solutionCodeTextView;
	private View windView;
	private View companyView;
	private View newSolutionView;
	private View sysSolutionView;
	private EditText newSolutionDes;
	private RadioButton sysSolutionButton;
	private RadioButton newSolutionButton;
	private List<Company> companies;
	// private List<Wind> winds;
	private Dialog dialog;
	// TODO implement the dialog when loading or uploading

	// temporary
	private String feedbackCode;
	private String code;
	private String fanType;
	private Company chosedCompany = null;
	private Wind chosedWind = null;
	private String solutionType = SYS_SOLUTION;
	private String systemSolutionCode = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);

		// get the params
		feedbackCode = getIntent().getStringExtra("feedbackCode");
		code = getIntent().getStringExtra("code");
		fanType = getIntent().getStringExtra("fanType");
		companies = userSingleton.getCompanies();

		// initiate the widgets
		feedbackButton = (Button) this.findViewById(R.id.feedback_btn);
		fanTypeTextView = (TextView) this.findViewById(R.id.fan_type);
		windTextView = (TextView) this.findViewById(R.id.chosed_wind);
		companyTextView = (TextView) this.findViewById(R.id.chosed_company);
		solutionCodeTextView = (TextView) this
				.findViewById(R.id.chosed_solution);
		newSolutionDes = (EditText) this.findViewById(R.id.new_solution_des);
		windView = this.findViewById(R.id.wind_layout);
		companyView = this.findViewById(R.id.comp_layout);
		newSolutionView = this.findViewById(R.id.new_des_layout);
		sysSolutionView = this.findViewById(R.id.sys_choice_layout);
		sysSolutionButton = (RadioButton) this.findViewById(R.id.sys_solution);
		newSolutionButton = (RadioButton) this.findViewById(R.id.new_solution);
		feedbackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// check if the company / wind / solutions info
				if (chosedCompany != null && chosedWind != null) {
					switch (solutionType) {
					case SYS_SOLUTION:
						if (systemSolutionCode.equals("")) {
							Toast.makeText(FeedbackActivity.this, "请选择方案",
									Toast.LENGTH_SHORT).show();
							return;
						}
						break;
					case NEW_SOLUTION:
						if (newSolutionDes.getEditableText().toString()
								.equals("")) {
							Toast.makeText(FeedbackActivity.this, "请填写新方案描述",
									Toast.LENGTH_SHORT).show();
							return;
						}
						break;
					default:
						break;
					}
				} else
					return;

				if (httpUtils.getNetStatus() > 0) {
					// 有网络
					new Thread(new Runnable() {

						@Override
						public void run() {
							if (offlineDataUploadTool.uploadSyncOfflineData()) {
								// 离线上传成功
								// TODO:再读取一次数据库获取最新的feedbackCode
								feedbackCode = "????";
								// 进行上传流程
								if (uploadFeedbackInfo()) {
									// 上传成功
									handler.post(new Runnable() {
										@Override
										public void run() {
											finish();
										}
									});
								} else {
									// 上传失败
									return;
								}
							} else {
								// 离线上传失败
								Toast.makeText(FeedbackActivity.this,
										"离线上传流程出错！", Toast.LENGTH_SHORT).show();
								return;
							}
						}
					}).start();
				} else {
					// TODO 离线操作
				}

			}
		});

		fanTypeTextView.setText(fanType);

		final String[] companiesStrings = new String[companies.size()];
		for (int i = 0; i < companiesStrings.length; i++) {
			companiesStrings[i] = companies.get(i).getName();
		}
		companyView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(FeedbackActivity.this)
						.setTitle("请选择项目公司")
						.setSingleChoiceItems(companiesStrings, 0,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										chosedCompany = companies.get(which);
										companyTextView.setText(chosedCompany
												.getName());
										chosedWind=null;
										windTextView.setText("请选择风场");
										dialog.dismiss();
									}
								}).create().show();
			}
		});

		windView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (chosedCompany == null) {
					Toast.makeText(FeedbackActivity.this, "请选择项目公司",
							Toast.LENGTH_SHORT).show();
					return;
				}
				final String[] windsStrings = new String[chosedCompany
						.getWindArray().size()];
				for (int i = 0; i < windsStrings.length; i++) {
					windsStrings[i] = chosedCompany.getWindArray().get(i)
							.getName();
				}
				new AlertDialog.Builder(FeedbackActivity.this)
						.setTitle("请选择风场")
						.setSingleChoiceItems(windsStrings, 0,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										chosedWind = chosedCompany
												.getWindArray().get(which);
										windTextView.setText(chosedWind
												.getName());
										dialog.dismiss();
									}
								}).create().show();
			}
		});

		sysSolutionButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							// newSolutionView.setClickable(false);
							// newSolutionView.setFocusable(false);
							newSolutionDes.setClickable(false);
							// newSolutionDes.setFocusable(false);
							newSolutionDes.setEnabled(false);
							sysSolutionView.setClickable(true);
							sysSolutionView.setFocusable(true);
							solutionType = SYS_SOLUTION;
						}
					}
				});

		newSolutionButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							// newSolutionView.setClickable(true);
							// newSolutionView.setFocusable(true);
							newSolutionDes.setClickable(true);
							// newSolutionDes.setFocusable(true);
							newSolutionDes.setEnabled(true);
							sysSolutionView.setClickable(false);
							sysSolutionView.setFocusable(false);
							solutionType = NEW_SOLUTION;
						}
					}
				});

		sysSolutionView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FeedbackActivity.this,
						FaultTreeDetailActivity.class);
				intent.putExtra("status", FaultTreeDetailActivity.FEEDBACK_MODE);
				startActivityForResult(intent, 0);
			}
		});

		// default radio button:system solution
		sysSolutionButton.setChecked(true);
	}

	/**
	 * 在有网络的情况下上传反馈信息
	 * 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	private boolean uploadFeedbackInfo() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss");
		String time = simpleDateFormat.format(new Date());
		FinalHttp finalHttp = new FinalHttp();
		Map<String, String> params = new HashMap<>();
		params.put("token", userSingleton.getValidateToken());
		params.put("feedbackcode", feedbackCode);
		params.put("type", solutionType);
		params.put("companyCode", chosedCompany.getId());
		params.put("windCode", chosedWind.getId());
		params.put("systemMethod", systemSolutionCode);
		params.put("newMethodDes", newSolutionDes.getEditableText().toString());
		// TODO 用什么格式？？
		params.put("feedbackTime", time);
		String resultString = (String) finalHttp.getSync(httpUtils.URL
				+ httpUtils.FEEDBACK_FT, new AjaxParams(params));
		if (resultString == null || resultString.equals("")) {
			Log.e(TAG, "离线上传，回传为空:" + code);
			return false;
		} else {
			JSONObject resultObject = JSON.parseObject(resultString);
			String statusCode = resultObject.getString("statusCode");
			switch (statusCode) {
			case "200":
				Toast.makeText(this, "反馈成功！", Toast.LENGTH_SHORT).show();
				Log.i(TAG, "200:上传成功：" + code);
				// TODO Database:1.更新记录表中的记录
				// TODO database:2.unFeedback number-1
				userSingleton.setUnFeedbackNumber((userSingleton
						.getUnFeedbackNumber()) - 1);
				break;
			case "300":
				Toast.makeText(this, "反馈失败！", Toast.LENGTH_SHORT).show();
				Log.e(TAG, "300:上传失败：" + code);
				return false;
			case "305":
				Toast.makeText(this, "重复提交反馈！", Toast.LENGTH_SHORT).show();
				Log.e(TAG, "305:重复提交反馈：" + code);
				// TODO database: 1.delete the record.
				// TODO database:2.unFeedback number-1
				userSingleton.setUnFeedbackNumber((userSingleton
						.getUnFeedbackNumber()) - 1);
				break;
			default:
				return false;
			}

			return true;
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			if (data.getStringExtra("handleCode")!=null) {
				String result = data.getStringExtra("handleCode");
				Toast.makeText(FeedbackActivity.this, result,
						Toast.LENGTH_SHORT).show();
				systemSolutionCode = result;
				solutionCodeTextView.setText(systemSolutionCode);
			}
		}
	}

}
