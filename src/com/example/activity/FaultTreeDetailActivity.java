package com.example.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lyfaultdiagnosissystem.R;
import com.example.singleton.UserSingleton;
import com.example.tree_component.DrawUtils;
import com.example.tree_component.bean.FaultTreeNode;
import com.example.tree_component.bean.ImageResource;
import com.example.tree_component.bean.PartInfo;
import com.example.tree_component.bean.TreeBean;
import com.example.tree_component.bean.TreeRoot;

/**
 * 故障树展示页面
 * 
 * @author steven
 * 
 */
@SuppressWarnings("deprecation")
public class FaultTreeDetailActivity extends Activity implements
		OnClickListener {

	// constants
	public static final String TAG = "FaultTreeDetailActivity";
	public static final int SHOW_MODE = 0;
	public static final int FEEDBACK_MODE = 1;
	public static final int GROUP_MODEL = 0;
	public static final int COMP_MODEL = 1;

	// temporaries
	private int status;// 0:show model 1:feedback model
	private int percentageModel = GROUP_MODEL;
	private TreeBean treeBean;
	private AbsoluteLayout showingTree;
	private Button chosedButton;// for feedback

	// widgets
	private Button groupButton;
	private Button compButton;
	private ImageView groupFlag;
	private ImageView compFlag;
	private LinearLayout buttonsGroup;
	private Button okButton;
	private Button cancelButton;
	@SuppressWarnings("unused")
	private Button rootButton;
	private List<Button> faultReasonButtons;
	private List<Button> faultPointButtons;
	private List<Button> faultHandleButtons;
	private List<Button> allNodeButtons;
	private List<Button> choiceButtons;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fault_tree_detail);
		groupButton = (Button) this.findViewById(R.id.tree_group_btn);
		compButton = (Button) this.findViewById(R.id.tree_comp_btn);
		groupFlag = (ImageView) this.findViewById(R.id.tree_group_flag);
		compFlag = (ImageView) this.findViewById(R.id.tree_comp_flag);
		buttonsGroup = (LinearLayout) this
				.findViewById(R.id.tree_feedback_btns);
		okButton = (Button) this.findViewById(R.id.tree_ok_btn);
		cancelButton = (Button) this.findViewById(R.id.tree_cancel_btn);
		groupButton.setOnClickListener(this);
		compButton.setOnClickListener(this);
		groupFlag.setVisibility(View.VISIBLE);
		compFlag.setVisibility(View.INVISIBLE);
		okButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		treeBean = getFakeData();
		sortByGroup(treeBean);
		status = getIntent().getIntExtra("status", SHOW_MODE);
		switch (status) {
		case SHOW_MODE:
			// 进行树展示
			showingTree = draw(treeBean, false);
			buttonsGroup.setVisibility(View.INVISIBLE);
			buttonsGroup.setFocusable(false);
			break;
		case FEEDBACK_MODE:
			showingTree = draw(treeBean, true);
			break;
		default:
			break;
		}
	}

	/**
	 * 绘制树
	 * 
	 * @param treeBean
	 */
	private AbsoluteLayout draw(TreeBean treeBean, boolean isFeedbacking) {
		AbsoluteLayout absoluteLayout = new AbsoluteLayout(this);
		DrawUtils drawUtils = new DrawUtils(absoluteLayout, treeBean,
				FaultTreeDetailActivity.this, isFeedbacking);
		absoluteLayout = drawUtils.getDrawedLayout();
		absoluteLayout.setBackgroundColor(Color.WHITE);
		// 在此处输入绝对布局的长宽
		LayoutParams absoluteLayoutParams = drawUtils.getLayoutParams();
		LinearLayout treeLayout = (LinearLayout) this
				.findViewById(R.id.tree_layout);
		treeLayout.addView(absoluteLayout, absoluteLayoutParams);
		rootButton = drawUtils.getRootButton();
		faultHandleButtons = drawUtils.getFaultHandleButtons();
		faultPointButtons = drawUtils.getFaultPointButtons();
		faultReasonButtons = drawUtils.getFaultReasonButtons();
		choiceButtons = drawUtils.getChoiceButtons();
		allNodeButtons = new ArrayList<>();
		allNodeButtons.addAll(faultHandleButtons);
		allNodeButtons.addAll(faultPointButtons);
		allNodeButtons.addAll(faultReasonButtons);
		NodeOnClickListener nodeOnClickListener = new NodeOnClickListener();
		for (Button button : allNodeButtons) {
			button.setOnClickListener(nodeOnClickListener);
		}
		if (isFeedbacking) {
			ChoiceOnClickListener choiceOnClickListener = new ChoiceOnClickListener();
			for (Button button : choiceButtons) {
				button.setOnClickListener(choiceOnClickListener);
			}
		}
		return absoluteLayout;
	}

	private void deleteDraw() {
		LinearLayout treeLayout = (LinearLayout) this
				.findViewById(R.id.tree_layout);
		treeLayout.removeView(showingTree);
	}

	private TreeBean getFakeData() {
		// constants
		String url = "http://b.hiphotos.baidu.com/baike/w%3D268/sign=f4ee6366b2b7d0a27bc9039bf3ed760d/ae51f3deb48f8c54cd34cafb3a292df5e1fe7f7a.jpg";
		String url2 = "http://b.hiphotos.baidu.com/baike/w%3D268/sign=ac60c17472f082022d92963973fafb8a/4bed2e738bd4b31c862ab1ec86d6277f9e2ff856.jpg";

		List<ImageResource> images = new ArrayList<>();
		images.add(new ImageResource("code", "name", url,
				ImageResource.IMAGE_TYPE));
		images.add(new ImageResource("code", "name", url,
				ImageResource.IMAGE_TYPE));
		images.add(new ImageResource("code", "name", url,
				ImageResource.IMAGE_TYPE));
		images.add(new ImageResource("code", "name", url,
				ImageResource.IMAGE_TYPE));
		images.add(new ImageResource("code", "name", url,
				ImageResource.IMAGE_TYPE));

		List<ImageResource> papers = new ArrayList<>();
		papers.add(new ImageResource("code", "name", url2,
				ImageResource.PAPER_TYPE));
		papers.add(new ImageResource("code", "name", url2,
				ImageResource.PAPER_TYPE));
		papers.add(new ImageResource("code", "name", url2,
				ImageResource.PAPER_TYPE));
		papers.add(new ImageResource("code", "name", url2,
				ImageResource.PAPER_TYPE));
		papers.add(new ImageResource("code", "name", url2,
				ImageResource.PAPER_TYPE));
		PartInfo partInfo = new PartInfo("Hehehheheh", papers, images);

		TreeRoot treeRoot = new TreeRoot("T1", "1.0", "10", "111", "故障",
				"error", "unknown");
		FaultTreeNode handlerP1A1 = new FaultTreeNode("T1",
				FaultTreeNode.HANDLE_LAVEL, "P1", "P1-H1", "重新输入参数", 10, 20,
				null, null, partInfo);
		FaultTreeNode handlerP1A2 = new FaultTreeNode("T1",
				FaultTreeNode.HANDLE_LAVEL, "P1", "P1-H2", "更换参数", 15, 10,
				null, null, partInfo);
		FaultTreeNode handlerP2A1 = new FaultTreeNode("T1",
				FaultTreeNode.HANDLE_LAVEL, "P2", "P2-H1", "停止运作", 22, 88,
				null, null, partInfo);
		FaultTreeNode handlerP3A1 = new FaultTreeNode("T1",
				FaultTreeNode.HANDLE_LAVEL, "P3", "P3-H1", "P3对策", 56, 44,
				null, null, partInfo);
		FaultTreeNode handlerP4A1 = new FaultTreeNode("T1",
				FaultTreeNode.HANDLE_LAVEL, "P4", "P4-H1", "P4对策", 13, 88,
				null, null, partInfo);
		FaultTreeNode handlerP4A2 = new FaultTreeNode("T1",
				FaultTreeNode.HANDLE_LAVEL, "P4", "P4-H2", "不知道啊~", 20, 15,
				null, null, partInfo);
		List<FaultTreeNode> P1Handle = new ArrayList<>();
		P1Handle.add(handlerP1A1);
		P1Handle.add(handlerP1A2);
		List<FaultTreeNode> P2Handle = new ArrayList<>();
		P2Handle.add(handlerP2A1);
		List<FaultTreeNode> P3Handle = new ArrayList<>();
		P3Handle.add(handlerP3A1);
		List<FaultTreeNode> P4Handle = new ArrayList<>();
		P4Handle.add(handlerP4A1);
		P4Handle.add(handlerP4A2);

		List<FaultTreeNode> allHandle = new ArrayList<>();
		allHandle.add(handlerP1A1);
		allHandle.add(handlerP1A2);
		allHandle.add(handlerP2A1);
		allHandle.add(handlerP3A1);
		allHandle.add(handlerP4A1);
		allHandle.add(handlerP4A2);

		FaultTreeNode pointP1 = new FaultTreeNode("T1",
				FaultTreeNode.POINT_LEVEL, "R1", "P1", "参数错误", 56, 40,
				P1Handle, null, partInfo);
		FaultTreeNode pointP2 = new FaultTreeNode("T1",
				FaultTreeNode.POINT_LEVEL, "R1", "P2", "零件有问题", 11, 60,
				P2Handle, null, partInfo);
		FaultTreeNode pointP3 = new FaultTreeNode("T1",
				FaultTreeNode.POINT_LEVEL, "R2", "P3", "P3有问题", 58, 23,
				P3Handle, null, partInfo);
		FaultTreeNode pointP4 = new FaultTreeNode("T1",
				FaultTreeNode.POINT_LEVEL, "R2", "P4", "P4有问题", 46, 60,
				P4Handle, null, partInfo);

		List<FaultTreeNode> R1Points = new ArrayList<>();
		R1Points.add(pointP1);
		R1Points.add(pointP2);
		List<FaultTreeNode> R2Points = new ArrayList<>();
		R2Points.add(pointP3);
		R2Points.add(pointP4);

		List<FaultTreeNode> allPoints = new ArrayList<>();
		allPoints.add(pointP1);
		allPoints.add(pointP2);
		allPoints.add(pointP3);
		allPoints.add(pointP4);

		FaultTreeNode reasonR1 = new FaultTreeNode("T1",
				FaultTreeNode.REASON_LAVEL, null, "R1", "风机故障啦！", 10, 50,
				R1Points, null, partInfo);
		FaultTreeNode reasonR2 = new FaultTreeNode("T1",
				FaultTreeNode.REASON_LAVEL, null, "R2", "不知道的故障.", 20, 40,
				R2Points, null, partInfo);
		List<FaultTreeNode> reasons = new ArrayList<>();
		reasons.add(reasonR1);
		reasons.add(reasonR2);

		treeRoot.setChildCodes(reasons);

		return new TreeBean(treeRoot, reasons, allPoints, allHandle);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tree_group_btn:
			if (percentageModel == GROUP_MODEL) {
				return;
			}
			groupFlag.setVisibility(View.VISIBLE);
			compFlag.setVisibility(View.INVISIBLE);
			deleteDraw();
			sortByGroup(treeBean);
			switch (status) {
			case SHOW_MODE:
				// 进行树展示
				showingTree = draw(treeBean, false);
				break;
			case FEEDBACK_MODE:
				showingTree = draw(treeBean, true);
				break;
			default:
				break;
			}
			percentageModel = GROUP_MODEL;
			break;
		case R.id.tree_comp_btn:
			if (percentageModel == COMP_MODEL) {
				return;
			}
			groupFlag.setVisibility(View.INVISIBLE);
			compFlag.setVisibility(View.VISIBLE);
			deleteDraw();
			sortByComp(treeBean);
			switch (status) {
			case SHOW_MODE:
				// 进行树展示
				showingTree = draw(treeBean, false);
				break;
			case FEEDBACK_MODE:
				showingTree = draw(treeBean, true);
				break;
			default:
				break;
			}
			percentageModel = COMP_MODEL;
			break;
		case R.id.tree_ok_btn: {
			Intent intent = new Intent();
			if (chosedButton == null) {
				Toast.makeText(this, "请选择一个方案", Toast.LENGTH_SHORT).show();
				return;
			}
			intent.putExtra("handleCode", (String) chosedButton.getTag());
			setResult(0, intent);
			finish();
			break;
		}
		case R.id.tree_cancel_btn:
			finish();
			break;
		}
	}

	private TreeBean sortByGroup(TreeBean treeBean) {
		// 显示龙源集团概率
		Collections.sort(treeBean.getFaultReasonNodes(),
				new GroupPercentageComparator());
		for (FaultTreeNode reasonNode : treeBean.getFaultReasonNodes()) {
			for (FaultTreeNode faultPointNode : reasonNode.getChildCodes()) {
				Collections.sort(faultPointNode.getChildCodes(),
						new GroupPercentageComparator());
			}
			Collections.sort(reasonNode.getChildCodes(),
					new GroupPercentageComparator());
		}
		return treeBean;
	}

	private TreeBean sortByComp(TreeBean treeBean) {
		// 显示项目公司概率
		Collections.sort(treeBean.getFaultReasonNodes(),
				new CompPercentageComparator());
		for (FaultTreeNode reasonNode : treeBean.getFaultReasonNodes()) {
			for (FaultTreeNode faultPointNode : reasonNode.getChildCodes()) {
				Collections.sort(faultPointNode.getChildCodes(),
						new CompPercentageComparator());
			}
			Collections.sort(reasonNode.getChildCodes(),
					new CompPercentageComparator());
		}
		return treeBean;
	}

	class NodeOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Button clickedButton = (Button) v;
			FaultTreeNode clickNode = (FaultTreeNode) clickedButton.getTag();
			for (Button button : allNodeButtons) {
				FaultTreeNode temp = (FaultTreeNode) button.getTag();
				if (temp.getCode().equals(clickNode.getCode())) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							FaultTreeDetailActivity.this);
					final String items[] = { "作业指导书", "部件信息" };
					final PartInfo partInfo = ((FaultTreeNode) button.getTag())
							.getPartInfo();
					Log.i("test", ((FaultTreeNode) button.getTag()).getCode());
					builder.setItems(items,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									switch (which) {
									case 0: {
										Toast.makeText(
												FaultTreeDetailActivity.this,
												"PDF", Toast.LENGTH_SHORT)
												.show();
										break;
									}
									case 1:
										Intent intent = new Intent(
												FaultTreeDetailActivity.this,
												PartActivity.class);
										UserSingleton.getInstance()
												.setPartInfo(partInfo);
										FaultTreeDetailActivity.this
												.startActivity(intent);
										break;
									default:
										break;
									}
								}
							})
							.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									}).show();

					break;
				}
			}
		}
	}

	class ChoiceOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String clickedCode = (String) v.getTag();
			if (chosedButton != null) {
				if (clickedCode.equals((String) chosedButton.getTag())) {
					return;
				}
			}
			for (Button button : choiceButtons) {
				String tempCode = (String) button.getTag();
				if (clickedCode.equals(tempCode)) {
					if (chosedButton != null) {
						chosedButton
								.setBackgroundResource(R.drawable.tree_root_shape);
					}
					chosedButton = button;
					chosedButton
							.setBackgroundResource(R.drawable.tree_choice_chosed);
					break;
				}
			}

		}

	}

}

class GroupPercentageComparator implements Comparator<FaultTreeNode> {

	@Override
	public int compare(FaultTreeNode lhs, FaultTreeNode rhs) {
		if (lhs.getGroupPercentage() > rhs.getGroupPercentage()) {
			return -1;
		} else {
			return 1;
		}
	}
}

class CompPercentageComparator implements Comparator<FaultTreeNode> {

	@Override
	public int compare(FaultTreeNode lhs, FaultTreeNode rhs) {
		if (lhs.getComPercentage() > rhs.getComPercentage()) {
			return -1;
		} else {
			return 1;
		}
	}
}
