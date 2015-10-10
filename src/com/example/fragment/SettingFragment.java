package com.example.fragment;

import com.example.common.HttpUtils;
import com.example.lyfaultdiagnosissystem.R;
import com.example.singleton.UserSingleton;
import com.example.sqlite.DBDao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

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
	
	private DBDao dbDao=new DBDao(getActivity());
	private UserSingleton userSingleton=UserSingleton.getInstance();
	private HttpUtils httpUtils=new HttpUtils(getActivity());

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
