package com.example.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;

import com.example.common.HttpUtils;
import com.example.lyfaultdiagnosissystem.R;
import com.example.singleton.UserSingleton;
import com.example.tree_component.DrawUtils;
import com.example.tree_component.bean.TreeBean;

/**
 * 故障树展示页面
 * 
 * @author steven
 * 
 */
public class FaultTreeDetailActivity extends Activity {
	public static final String TAG = "FaultTreeDetailActivity";
	private String status;// 0:visitor model 1:unFeedback model 2.feedbacked
							// model
	private TreeBean treeBean;
	private HttpUtils httpUtils = new HttpUtils(FaultTreeDetailActivity.this);
	private UserSingleton userSingleton = UserSingleton.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fault_tree_detail);
		treeBean = (TreeBean) getIntent().getExtras().getSerializable("tree");
		status = getIntent().getStringExtra("status");
		// 进行树展示
		draw(treeBean);
	}

	/**
	 * 绘制树
	 * 
	 * @param treeBean
	 */
	private void draw(TreeBean treeBean) {
		@SuppressWarnings("deprecation")
		AbsoluteLayout absoluteLayout = new AbsoluteLayout(this);
		DrawUtils drawUtils = new DrawUtils(absoluteLayout, treeBean,
				FaultTreeDetailActivity.this);
		absoluteLayout = drawUtils.getDrawedLayout();
		absoluteLayout.setBackgroundColor(Color.WHITE);
		// 在此处输入绝对布局的长宽
		LayoutParams absoluteLayoutParams = drawUtils.getLayoutParams();
		LinearLayout treeLayout = (LinearLayout) this
				.findViewById(R.id.tree_layout);
		treeLayout.addView(absoluteLayout, absoluteLayoutParams);
	}

}
