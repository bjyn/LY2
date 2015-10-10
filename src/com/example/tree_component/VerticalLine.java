package com.example.tree_component;

import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @author steven
 * 
 */
public class VerticalLine extends View {
	private int thick;
	private int startPoint[];
	private int verticalHeight;
	private Paint paint;
	private boolean isDash;// 是否画虚线
	private PathEffect effect;// 虚线效果

	public VerticalLine(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public VerticalLine(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public VerticalLine(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context上下文
	 * @param x水平起点横坐标
	 * @param y水平起点纵坐标
	 * @param verticalHeight
	 *            竖直线高度
	 */
	public VerticalLine(Context context, int x, int y, int verticalHeight) {
		super(context);
		startPoint = new int[2];
		startPoint[0] = x + 15;
		startPoint[1] = y + 8;
		this.verticalHeight = verticalHeight;
		paint = new Paint();
		thick = 3;
	}

	/**
	 * @param context
	 * @param x
	 * @param y
	 * @param verticalHeight
	 * @param isDash
	 *            是否画虚线
	 */
	public VerticalLine(Context context, int x, int y, int verticalHeight,
			boolean isDash) {
		super(context);
		startPoint = new int[2];
		startPoint[0] = x + 15;
		startPoint[1] = y + 8;
		this.verticalHeight = verticalHeight;
		paint = new Paint();
		this.isDash = isDash;
		thick = 3;
		effect = new DashPathEffect(
				new float[] { 1, 1}, 1);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(thick);
		paint.setColor(Color.rgb(0, 0, 0));
		if (isDash) {
			Log.i("tesst", "画虚线");
			paint.setPathEffect(effect);
		}
		canvas.drawLine(startPoint[0], startPoint[1], startPoint[0],
				startPoint[1] - verticalHeight, paint);
		super.onDraw(canvas);
	}

	public int[] getEndLocation() {
		return new int[] { startPoint[0], startPoint[1] - verticalHeight };
	}

}
