package com.example.tree_component;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsoluteLayout;
import android.widget.Button;

import com.example.tree_component.bean.FaultTreeNode;
import com.example.tree_component.bean.TreeBean;
import com.example.tree_component.bean.TreeRoot;

public class DrawUtils {
	private AbsoluteLayout canvas;// 画布
	private TreeBean treeBean;
	private Context context;
	private int canvasWidth;
	private Canvas drawCanvas;
	private Paint paint;
	// 页面总布局
	public static final int canvasMarginTop = 50;
	public static final int canvasMarginBottom = 50;
	public static final int canvasMarginRight = 50;
	public static final int canvasMarginLeft = 50;
	// 第四层规格
	public static final int lv4Height = 60;
	public static final int lv4Width = 100;
	public static final int lv4IntervalInGroup = 30;
	public static final int lv4IntervalBetweenGroups = 50;
	// 第三层规格
	public static final int lv3Height = 60;
	public static final int lv3Width = 150;
	private static final int lv3Fix = 14;// 显示修正
	// 第二层规格
	public static final int lv2Height = 60;
	public static final int lv2Width = 150;
	// 第一层规格
	public static final int rootHeight = 60;
	public static final int rootWidth = 150;
	private static final int lv1HoriFix = 60;
	// 层之间高度差
	public static final int intervalBetweenLayers = 150;
	private static final int betweenLins = intervalBetweenLayers - 20;

	public DrawUtils(@SuppressWarnings("deprecation") AbsoluteLayout canvas, TreeBean treeBean, Context context) {
		super();
		this.canvas = canvas;
		this.treeBean = treeBean;
		this.context = context;

		drawCanvas = new Canvas();
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		paint.setColor(Color.rgb(0, 0, 0));
	}

	@SuppressWarnings("deprecation")
	public AbsoluteLayout getDrawedLayout() {
		Bitmap bitmap = Bitmap
				.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(bitmap);
		drawHandleFaultLayer();
		drawReasonCheckLayer();
		drawLv2();
		drawRoot();
		return canvas;
	}

	public LayoutParams getLayoutParams() {
		int canvasHeight = canvasMarginTop + canvasMarginBottom + 3
				* intervalBetweenLayers + rootHeight + lv2Height + lv3Height
				+ lv4Height;
		return new LayoutParams(canvasWidth, canvasHeight);
	}

	private void drawHandleFaultLayer() {
		int X = canvasMarginLeft;
		int Y = canvasMarginTop + 3 * intervalBetweenLayers + rootHeight
				+ lv2Height + lv3Height;
		List<FaultTreeNode> reasonCheckFaultTreeNodes = treeBean
				.getReasonCheckNodes();
		for (int i = 0; i < reasonCheckFaultTreeNodes.size(); i++) {
			int begin = X;
			List<FaultTreeNode> handleFaultTreeNodes = reasonCheckFaultTreeNodes
					.get(i).getChildCodes();
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
					line = new VerticalLine(context, X + lv4Width / 2, Y,
							betweenLins / 2);
					if (lineBegin == 0) {
						lineBegin = line.getEndLocation()[0];// 第三行线的起点
					}
					canvas.addView(line);
					canvas.addView(button);
					canvas.invalidate();
					X += lv4Width + lv4IntervalInGroup;
				}
				X -= lv4IntervalInGroup;
				Line HorLine = new Line(context, lineBegin,
						line.getEndLocation()[1], line.getEndLocation()[0]
								- lineBegin, betweenLins / 2);
				canvas.addView(HorLine);
				if ((X - begin) <= lv3Width) {
					X = begin + lv3Width;
				}
			}
			reasonCheckFaultTreeNodes.get(i).setLocationX(
					((X + begin) / 2) - (lv3Width / 2) + lv3Fix);
			X += lv4IntervalBetweenGroups;
		}

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
							line = new VerticalLine(context, X + lv4Width / 2,
									Y, betweenLins / 2);
							if (lineBegin == 0) {
								lineBegin = line.getEndLocation()[0];// 第三行线的起点
							}
							canvas.addView(line);
							canvas.addView(button);
							canvas.invalidate();
							X += lv4Width + lv4IntervalInGroup;
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
					reasonCheckNode.setLocationX(((X + begin) / 2)
							- (lv3Width / 2) + lv3Fix);
					X += lv4IntervalBetweenGroups;
				}
			}
		}
		canvasWidth = X + canvasMarginRight;
	}

	private void drawReasonCheckLayer() {
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
				line = new VerticalLine(context, reasonCheckNode.getLocationX()
						+ lv3Width / 2 - lv3Fix, Y, betweenLins / 2);
				if (lineBegin == 0) {
					lineBegin = line.getEndLocation()[0];// 第三行线的起点
				}
				canvas.addView(line);
				canvas.addView(button);
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

	private void drawLv2() {
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
			line = new VerticalLine(context, faultReasonNode.getLocationX()
					+ lv2Width / 2 - lv3Fix, Y, betweenLins / 2);
			if (lineBegin == 0) {
				lineBegin = line.getEndLocation()[0];// 第三行线的起点
			}

			canvas.addView(line);
			canvas.addView(button);
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
		canvas.addView(button);
	}

}
