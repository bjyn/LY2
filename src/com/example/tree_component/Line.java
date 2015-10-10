package com.example.tree_component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Line extends View {
	private int thick;// 线的粗细
	private int horizontalLength;// 水平线长度
	private int verticalHeight;// 中间线的高度
	private int startLocation[];// 开始位置
	private int endLocation[];
	private int verticalStartLocation[];// 中间线起点坐标
	private int verticalEndLocation[];// 中间显得终点坐标
	private int verticalColor[];// 竖直线的颜色
	private int horizontalColor[];// 水平线颜色
	private Paint paint;


	public Line(Context context) {
		super(context);
	}

	public Line(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public Line(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public Line(Context context, int x, int y, int length, int verticalHeight) {
		super(context);
		startLocation = new int[2];
		endLocation = new int[2];
		verticalColor = new int[3];
		horizontalColor = new int[3];
		horizontalColor = new int[3];
		verticalStartLocation = new int[2];
		verticalEndLocation = new int[2];
		startLocation[0] = x;
		startLocation[1] = y;
		endLocation[0] = x + length;
		endLocation[1] = y;
		verticalStartLocation[0] = startLocation[0] + horizontalLength / 2;
		verticalStartLocation[1] = startLocation[1];
		verticalEndLocation[0] = startLocation[0] + horizontalLength / 2;
		verticalEndLocation[1] = startLocation[1] - verticalHeight;
		this.verticalHeight = verticalHeight;
		this.horizontalLength = length;
		this.thick = 3;
		paint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 画水平线
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(thick);
		paint.setColor(Color.rgb(horizontalColor[0], horizontalColor[1],
				horizontalColor[2]));
		canvas.drawLine(startLocation[0], startLocation[1], startLocation[0]
				+ horizontalLength, startLocation[1], paint);
		// 画竖直线
		paint.setColor(Color.rgb(verticalColor[0], verticalColor[1],
				verticalColor[2]));
		canvas.drawLine(startLocation[0]+horizontalLength/2, startLocation[1],
				startLocation[0]+horizontalLength/2, startLocation[1]-verticalHeight, paint);
		super.onDraw(canvas);
	}
	public int getHorizontalLength() {
		return horizontalLength;
	}
	
	public void setHorizontalLength(int horizontalLength) {
		//长度改变时，需要重新计算垂直线位置
		this.horizontalLength = horizontalLength;
		verticalStartLocation[0] = startLocation[0] + horizontalLength / 2;
		verticalEndLocation[0] = startLocation[0] + horizontalLength / 2;
		endLocation[0]=startLocation[0]+horizontalLength;
	}

	public int getThick() {
		return thick;
	}

	public void setThick(int thick) {
		this.thick = thick;
	}

	public int getVerticalLength() {
		return verticalHeight;
	}

	public void setVerticalLength(int verticalLength) {
		//高度改变时，需要重新计算垂直线高度
		this.verticalHeight = verticalLength;
	}

	public int[] getVerticalColor() {
		return verticalColor;
	}

	public void setVerticalColor(int[] verticalColor) {
		this.verticalColor = verticalColor;
	}

	public int[] getHorizontalColor() {
		return horizontalColor;
	}

	public void setHorizontalColor(int[] horizontalColor) {
		this.horizontalColor = horizontalColor;
	}

	public int[] getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(int[] startLocation) {
		//起始坐标改变时，需要重新计算水平终点坐标和竖直线位置
		this.startLocation = startLocation;
		endLocation[0]=startLocation[0]+horizontalLength;
		endLocation[1]=startLocation[1];
		verticalStartLocation[0] = startLocation[0] + horizontalLength / 2;
		verticalStartLocation[1] = startLocation[1];
		verticalEndLocation[0] = startLocation[0] + horizontalLength / 2;
		verticalEndLocation[1] = startLocation[1] - verticalHeight;
	}

	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}

	public int[] getMiddleLocation() {
		return new int[] { verticalEndLocation[0], verticalEndLocation[1] };
	}
}
