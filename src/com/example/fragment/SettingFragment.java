package com.example.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.activity.FaultTreeDetailActivity;
import com.example.activity.FeedbackActivity;
import com.example.bean.Company;
import com.example.bean.FanBrand;
import com.example.bean.FanType;
import com.example.bean.Wind;
import com.example.common.HttpUtils;
import com.example.lyfaultdiagnosissystem.R;
import com.example.singleton.UserSingleton;
import com.example.sqlite.DBDao;

/**
 * 设置界面
 * 
 * @author steven
 * 
 */
public class SettingFragment extends Fragment {
	private Button changePwdBtn;// 修改密码按钮
	private Button cleanDbBtn;// 按钮括号显示数据库大小，同时点击清理
	private Button refreshBtn;// 更新数据库按钮
	private TextView userNameTv;// 用户姓名
	private TextView userIDTv;// 用户龙源id

	private DBDao dbDao = new DBDao(getActivity());
	private UserSingleton userSingleton = UserSingleton.getInstance();
	private HttpUtils httpUtils = new HttpUtils(getActivity());

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_setting, container,
				false);
		changePwdBtn = (Button) view.findViewById(R.id.change_pwd_btn);
		cleanDbBtn = (Button) view.findViewById(R.id.clean_db_btn);
		refreshBtn = (Button) view.findViewById(R.id.refresh_db_btn);
		userNameTv = (TextView) view.findViewById(R.id.user_name_tv);
		userIDTv = (TextView) view.findViewById(R.id.user_id_tv);

		// XXX DMC Test can be deleted
		// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		((Button) view.findViewById(R.id.testTreeFeedback))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// Intent intent = new Intent(getActivity(),
						// FaultTreeDetailActivity.class);
						// intent.putExtra("status",
						// FaultTreeDetailActivity.FEEDBACK_MODE);
						// startActivityForResult(intent, 0);
						List<Wind> winds = new ArrayList<>();
						winds.add(new Wind("w1", "wwwww"));
						winds.add(new Wind("w2", "bbbbb"));
						List<Wind> winds2 = new ArrayList<>();
						winds2.add(new Wind("a2", "999"));
						winds2.add(new Wind("a3", "jjj"));
						List<Company> companies = new ArrayList<Company>();
						companies.add(new Company("ferfe", "fegd", winds));
						companies.add(new Company("gegrege", "TTT", winds2));
						List<FanBrand> fanBrands = new ArrayList<FanBrand>();
						fanBrands.add(new FanBrand("fwfewf", "123132"));
						fanBrands.add(new FanBrand("OOOO", "666666"));
						List<FanType> fanTypes = new ArrayList<FanType>();
						fanTypes.add(new FanType("123132", "fafwfw", "frebd"));
						fanTypes.add(new FanType("123132", "AAADDD", "1235225"));
						fanTypes.add(new FanType("666666", "PP", "551"));
						fanTypes.add(new FanType("666666", "LL", "4861"));
						userSingleton.setCompanies(companies);
						userSingleton.setFanBrands(fanBrands);
						userSingleton.setFanTypes(fanTypes);
						userSingleton.setLimitFeedbackNumber(10);
						userSingleton.setUnFeedbackNumber(3);

						Intent intent = new Intent(getActivity(),
								FeedbackActivity.class);
						intent.putExtra("feedbackCode", "20151104221100A123");
						intent.putExtra("code", "A123");
						intent.putExtra("fanType", "华锐A315");
						startActivity(intent);
					}
				});

		((Button) view.findViewById(R.id.testTreeShow))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								FaultTreeDetailActivity.class);
						intent.putExtra("status",
								FaultTreeDetailActivity.SHOW_MODE);
						startActivity(intent);
					}
				});

		// DMC Test can be deleted
		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

		final Dialog dialog = new Dialog(getActivity(),
				R.style.version_update_dialpg_style);
		dialog.setContentView(R.layout.dialog_change_pwd);
		OnClickListener clickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.show();
			}
		};
		changePwdBtn.setOnClickListener(clickListener);
		return view;
	}

	

}
