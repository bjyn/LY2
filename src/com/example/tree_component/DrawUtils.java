package com.example.tree_component;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsoluteLayout;
import android.widget.Button;

import com.example.lyfaultdiagnosissystem.R;
import com.example.tree_component.bean.FaultTreeNode;
import com.example.tree_component.bean.TreeBean;
import com.example.tree_component.bean.TreeRoot;

@SuppressWarnings("deprecation")
public class DrawUtils {
	private AbsoluteLayout canvas;// 画布
	private TreeBean treeBean;
	private Context context;
	private boolean isFeedbacking;
	private int canvasWidth;
	@SuppressWarnings("unused")
	private Canvas drawCanvas;
	private Paint paint;
	
	// widgets
	private Button rootButton;
	private List<Button> faultReasonButtons;
	private List<Button> faultPointButtons;
	private List<Button> faultHandleButtons;
	private List<Button> choiceButtons;
	
	// 页面总布局
	public static final int canvasMarginTop = 200;
	public static final int canvasMarginBottom = 300;
	public static final int canvasMarginRight = 150;
	public static final int canvasMarginLeft = 150;

	// 第四层规格
	public static final int lv4Height = 60;
	public static final int lv4Width = 200;
	public static final int lv4IntervalInGroup = 30;
	public static final int lv4IntervalBetweenGroups = 50;

	// 选项层规格
	public static final int choiceHeight = lv4Height;
	public static final int choiceWidth = lv4Width;

	// 第三层规格
	public static final int lv3Height = 60;
	public static final int lv3Width = 200;
	private static final int lv3Fix = 14;// 显示修正
	// 第二层规格
	public static final int lv2Height = 60;
	public static final int lv2Width = 200;
	// 第一层规格
	public static final int rootHeight = 60;
	public static final int rootWidth = 200;
	private static final int lv1HoriFix = 60;
	// 层之间高度差
	public static final int intervalBetweenLayers = 200;// 100
	private static final int betweenLins = intervalBetweenLayers - 20;

	public DrawUtils(AbsoluteLayout canvas, TreeBean treeBean, Context context,
			boolean isFeedbacking) {
		super();
		this.canvas = canvas;
		this.treeBean = treeBean;
		this.context = context;
		this.isFeedbacking = isFeedbacking;

		drawCanvas = new Canvas();
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		paint.setColor(Color.rgb(0, 0, 0));
	}

	public AbsoluteLayout getDrawedLayout() {
		Bitmap bitmap = Bitmap
				.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(bitmap);
		drawHandleFaultLayer();
		drawReasonCheckLayer();
		drawFaultReasonLayer();
		drawRoot();
		if (isFeedbacking) {
			drawChoicesLayer();
			
		}
		return canvas;
	}

	public LayoutParams getLayoutParams() {
		int canvasHeight = canvasMarginTop + canvasMarginBottom + 3
				* intervalBetweenLayers + rootHeight + lv2Height + lv3Height
				+ lv4Height;
		return isFeedbacking ? new LayoutParams(canvasWidth, canvasHeight
				+ choiceHeight + intervalBetweenLayers) : new LayoutParams(
				canvasWidth, canvasHeight);
	}

	private void drawHandleFaultLayer() {
		faultHandleButtons=new ArrayList<>();
		int X = canvasMarginLeft;
		int Y = canvasMarginTop + 3 * intervalBetweenLayers + rootHeight
				+ lv2Height + lv3Height;
		List<FaultTreeNode> faultReasonFaultTreeNodes = treeBean
				.getFaultReasonNodes();
		for (FaultTreeNode faultReasonFaultTreeNode : faultReasonFaultTreeNodes) {
			List<FaultTreeNode> reasonCheckNodes = faultReasonFaultTreeNode
					.getChildCodes();
			if (reasonCheckNodes.size() == 0 || reasonCheckNodes == null) {
				faultReasonFaultTreeNode.setLocationX(X);
				X += lv2Width + lv4IntervalInGroup;
			} else {
				for (FaultTreeNode reasonCheckNode : reasonCheckNodes) {
					int begin = X;
					List<FaultTreeNode> handleFaultTreeNodes = reasonCheckNode
							.getChildCodes();
					if (handleFaultTreeNodes.size() == 0
							|| handleFaultTreeNodes == null) {
						X += lv3Width;
					} else {
						// 叶节点不为空的时候
						int lineBegin = 0;
						VerticalLine line = null;
						for (FaultTreeNode handleFaultTreeNode : handleFaultTreeNodes) {
							Button button = new Button(context);
							button.setWidth(lv4Width);
							button.setHeight(lv4Height);
							button.setText(handleFaultTreeNode.getName());
							button.setTextSize(12);
							button.setX(X);
							button.setY(Y);
							button.setBackgroundResource(R.drawable.tree_lv4_shape);
							button.setTextColor(Color.WHITE);
							button.setTag(handleFaultTreeNode);
							handleFaultTreeNode.setLocationX(X);
							Log.i("tree", "L4:" + X);
							line = new VerticalLine(context, X + lv4Width / 2
									- lv3Fix, Y, betweenLins / 2);
							if (lineBegin == 0) {
								lineBegin = line.getEndLocation()[0];// 第三行线的起点
							}
							canvas.addView(line);
							canvas.addView(button);
							canvas.invalidate();
							X += lv4Width + lv4IntervalInGroup;
							faultHandleButtons.add(button);
						}
						X -= lv4IntervalInGroup;
						Line HorLine = new Line(context, lineBegin,
								line.getEndLocation()[1],
								line.getEndLocation()[0] - lineBegin,
								betweenLins / 2);
						canvas.addView(HorLine);
						if ((X - begin) <= lv3Width) {
							X = begin + lv3Width;
						}
					}

					if (handleFaultTreeNodes.size() == 1) {
						reasonCheckNode.setLocationX(((X + begin) / 2)
								- (lv3Width / 2));
					} else {
						reasonCheckNode.setLocationX(((X + begin) / 2)
								- (lv3Width / 2) /* + lv3Fix */);
					}

					Log.i("tree", "L3:" + reasonCheckNode.getLocationX());
					X += lv4IntervalBetweenGroups;
				}
			}
		}
		canvasWidth = X + canvasMarginRight;
	}

	private void drawReasonCheckLayer() {
		faultPointButtons=new ArrayList<>();
		int Y = canvasMarginTop + rootHeight + lv2Height + 2
				* intervalBetweenLayers;
		List<FaultTreeNode> faultReasonFaultTreeNodes = treeBean
				.getFaultReasonNodes();
		for (FaultTreeNode faultReasonNode : faultReasonFaultTreeNodes) {
			List<FaultTreeNode> reasonCheckFaultTreeNodes = faultReasonNode
					.getChildCodes();
			VerticalLine line = null;
			int lineBegin = 0;
			for (FaultTreeNode reasonCheckNode : reasonCheckFaultTreeNodes) {
				Button button = new Button(context);
				button.setWidth(lv3Width);
				button.setHeight(lv3Height);
				button.setText(reasonCheckNode.getName());
				button.setTextSize(12);
				button.setX(reasonCheckNode.getLocationX());
				button.setY(Y);
				button.setBackgroundResource(R.drawable.tree_lv3_shape);
				button.setTextColor(Color.WHITE);
				button.setTag(reasonCheckNode);
				line = new VerticalLine(context, reasonCheckNode.getLocationX()
						+ lv3Width / 2 - lv3Fix, Y, betweenLins / 2);
				if (lineBegin == 0) {
					lineBegin = line.getEndLocation()[0];// 第三行线的起点
				}
				canvas.addView(line);
				canvas.addView(button);
				faultPointButtons.add(button);
			}
			if (line != null) {
				Line HorLine = new Line(context, lineBegin,
						line.getEndLocation()[1], line.getEndLocation()[0]
								- lineBegin, betweenLins / 2);
				canvas.addView(HorLine);
			}

			if (reasonCheckFaultTreeNodes == null
					|| reasonCheckFaultTreeNodes.size() == 0) {
				// ??
			} else {
				// 设置上一级的坐标X
				int begin = reasonCheckFaultTreeNodes.get(0).getLocationX();
				int end = reasonCheckFaultTreeNodes.get(
						reasonCheckFaultTreeNodes.size() - 1).getLocationX()
						+ lv3Width;
				faultReasonNode.setLocationX(((begin + end) / 2)
						- (lv2Width / 2));
			}

		}
	}

	private void drawFaultReasonLayer() {
		faultReasonButtons=new ArrayList<>();
		int Y = canvasMarginTop + rootHeight + 1 * intervalBetweenLayers;
		TreeRoot rootBean = treeBean.getRootBean();
		List<FaultTreeNode> faultReasonNodes = rootBean.getChildCodes();
		VerticalLine line = null;
		int lineBegin = 0;
		for (FaultTreeNode faultReasonNode : faultReasonNodes) {
			Button button = new Button(context);
			button.setWidth(lv2Width);
			button.setHeight(lv2Height);
			button.setText(faultReasonNode.getName());
			button.setTextSize(12);
			button.setX(faultReasonNode.getLocationX());
			button.setY(Y);
			button.setBackgroundResource(R.drawable.tree_lv2_shape);
			button.setTextColor(Color.WHITE);
			button.setTag(faultReasonNode);
			line = new VerticalLine(context, faultReasonNode.getLocationX()
					+ lv2Width / 2 - lv3Fix, Y, betweenLins / 2);
			if (lineBegin == 0) {
				lineBegin = line.getEndLocation()[0];// 第三行线的起点
			}

			canvas.addView(line);
			canvas.addView(button);
			faultReasonButtons.add(button);
		}
		Line HorLine = new Line(context, lineBegin, line.getEndLocation()[1],
				line.getEndLocation()[0] - lineBegin, betweenLins / 2);
		canvas.addView(HorLine);
		int begin = faultReasonNodes.get(0).getLocationX();
		int end = faultReasonNodes.get(faultReasonNodes.size() - 1)
				.getLocationX() + lv2Width;
		// 设置上一级的坐标X
		rootBean.setLocationX(((begin + end) / 2) - (rootWidth / 2));
	}

	private void drawRoot() {
		TreeRoot rootBean = treeBean.getRootBean();
		int Y = canvasMarginTop + rootHeight - lv1HoriFix;
		Button button = new Button(context);
		button.setWidth(rootWidth);
		button.setHeight(rootHeight);
		button.setText(rootBean.getMainFaultCode());
		button.setTextSize(12);
		button.setX(rootBean.getLocationX());
		button.setY(Y);
		button.setBackgroundResource(R.drawable.tree_root_shape);
		button.setTextColor(Color.WHITE);
		canvas.addView(button);
		rootButton=button;
	}

	private void drawChoicesLayer() {
		choiceButtons=new ArrayList<>();
		int Y = canvasMarginTop + 4 * intervalBetweenLayers + rootHeight
				+ lv2Height + lv3Height + lv4Height;
		for (FaultTreeNode handleNode : treeBean.getHandleFaultNodes()) {
			Button button = new Button(context);
			button.setWidth(choiceWidth);
			button.setHeight(choiceHeight);
			button.setText(handleNode.getCode());
			button.setTextSize(12);
			button.setX(handleNode.getLocationX());
			button.setY(Y);
			button.setBackgroundResource(R.drawable.tree_root_shape);
			button.setTextColor(Color.WHITE);
			button.setTag(handleNode.getCode());
			canvas.addView(button);
			choiceButtons.add(button);
		}
	}

	public Button getRootButton() {
		return rootButton;
	}

	public List<Button> getFaultReasonButtons() {
		return faultReasonButtons;
	}

	public List<Button> getFaultPointButtons() {
		return faultPointButtons;
	}

	public List<Button> getFaultHandleButtons() {
		return faultHandleButtons;
	}

	public List<Button> getChoiceButtons() {
		return choiceButtons;
	}
	

}

