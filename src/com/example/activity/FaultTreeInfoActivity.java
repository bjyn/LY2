package com.example.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.example.bean.FTBaseInfo;
import com.example.bean.FTFBBaseInfo;
import com.example.lyfaultdiagnosissystem.R;

/**
 * 列表样式显示故障树的基本信息
 * 
 * @author steven
 * 
 */
public class FaultTreeInfoActivity extends Activity {
	private TextView CnameTv;
	private TextView EnameTv;
	private TextView fanBrandTv;
	private TextView fanTypeTv;
	private TextView versionTv;
	private TextView mainFaultTv;
	private TextView followFaultTv;
	private TextView triggerConditionTv;
	private TextView faultPheTv;
	private TextView remarkTv;
	private TextView feedbackStatusTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_fault_tree_base);
		CnameTv = (TextView) findViewById(R.id.fault_cname_in_detail);
		EnameTv = (TextView) findViewById(R.id.fault_ename_in_detail);
		fanBrandTv = (TextView) findViewById(R.id.fan_brand_in_detail_tv);
		fanTypeTv = (TextView) findViewById(R.id.fan_type_in_detail_tv);
		mainFaultTv = (TextView) findViewById(R.id.main_fault_code_in_detail_tv);
		followFaultTv = (TextView) findViewById(R.id.follow_fault_code_in_detail_tv);
		triggerConditionTv = (TextView) findViewById(R.id.trigger_condition_in_detail_tv);
		faultPheTv = (TextView) findViewById(R.id.fault_phe_in_detail_tv);
		versionTv = (TextView) findViewById(R.id.version_base_tv);
		remarkTv = (TextView) findViewById(R.id.remark_in_detail_tv);
		feedbackStatusTv = (TextView) findViewById(R.id.feedback_status_tv);

		switch (getIntent().getStringExtra("type")) {
		case "FTBaseInfo":
			FTBaseInfo ftBaseInfo = (FTBaseInfo) getIntent().getExtras()
					.getSerializable("obj");
			CnameTv.setText(ftBaseInfo.getChineseName());
			EnameTv.setText(ftBaseInfo.getEnglishName());
			fanBrandTv.setText(ftBaseInfo.getFanBrand().getName());
			fanTypeTv.setText(ftBaseInfo.getFanType().getName());
			mainFaultTv.setText(ftBaseInfo.getMainFaultCode());
			followFaultTv.setText(ftBaseInfo.getFollowFaultCode());
			triggerConditionTv.setText(ftBaseInfo.getTriggerCondition());
			faultPheTv.setText(ftBaseInfo.getFaultPhe());
			remarkTv.setText(ftBaseInfo.getRemark());
			versionTv.setText("" + ftBaseInfo.getVersion());
			feedbackStatusTv.setText("");
			break;
		case "FBFTBaseInfo":
			FTFBBaseInfo ftfbBaseInfo = (FTFBBaseInfo) getIntent().getExtras()
					.getSerializable("obj");
			CnameTv.setText(ftfbBaseInfo.getChineseName());
			EnameTv.setText(ftfbBaseInfo.getEnglishName());
			fanBrandTv.setText(ftfbBaseInfo.getFanBrand().getName());
			fanTypeTv.setText(ftfbBaseInfo.getFanType().getName());
			mainFaultTv.setText(ftfbBaseInfo.getMainFaultCode());
			followFaultTv.setText(ftfbBaseInfo.getFollowFaultCode());
			triggerConditionTv.setText(ftfbBaseInfo.getTriggerCondition());
			faultPheTv.setText(ftfbBaseInfo.getFaultPhe());
			remarkTv.setText(ftfbBaseInfo.getRemark());
			versionTv.setText("" + ftfbBaseInfo.getVersion());
			if (ftfbBaseInfo.getFeedbackStatus().equals("1")) {
				feedbackStatusTv.setText("未反馈");
			}else {
				feedbackStatusTv.setText("已反馈");
			}
			break;
		default:
			break;
		}

	}

}
