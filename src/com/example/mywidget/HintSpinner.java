package com.example.mywidget;

import java.util.ArrayList;
import java.util.List;

import com.example.lyfaultdiagnosissystem.R;

import android.R.anim;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class HintSpinner extends Spinner {
	private boolean isMoved = false;
	private Point touchedPoint = new Point();
	private OnClickListener dateLoadClickListener;
	private List<String> hints;

	public HintSpinner(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public HintSpinner(Context context, int mode) {
		super(context, mode);
	}

	// 重写了三个带attrs参数的方法，实现了从xml设置hint的自定义spinner
	public HintSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		setHintSpinner(context, attrs);
	}

	public HintSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setHintSpinner(context, attrs);
	}

	public HintSpinner(Context context, AttributeSet attrs, int defStyle,
			int mode) {
		super(context, attrs, defStyle, mode);
		setHintSpinner(context, attrs);
	}

	// 该spinner初始化时相关新属性的设置
	private void setHintSpinner(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.HintSpinner);
		String hint = ta.getString(R.styleable.HintSpinner_hints);
		hints = new ArrayList<>();
		hints.add(hint);
		HintSpinnerAdapter hintSpinnerAdapter=new HintSpinnerAdapter(context, android.R.layout.simple_spinner_item, hints);
		this.setAdapter(hintSpinnerAdapter);
		ta.recycle();
	}

	// 将用户需要的点击事件传入,若要实现hint的效果，需要在listener中进行adapter的设定
	public void setDateLoadClickListener(OnClickListener dateLoadClickListener) {
		this.dateLoadClickListener = dateLoadClickListener;
	}

	// 重写spinner的点击事件，在点击事件发生的时候再将用户需要的list项载入，从而实现hint的效果
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getRawX();
		int y = (int) event.getRawY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchedPoint.x = x;
			touchedPoint.y = y;
			break;
		case MotionEvent.ACTION_MOVE:
			isMoved = true;
			break;
		case MotionEvent.ACTION_UP:
			if (isMoved) {
				// 从上向下滑动
				if (y - touchedPoint.y > 20) {
				}
				// 从下向上滑动
				else if (touchedPoint.y - y > 20) {
				}
				// 滑动幅度小时，当作点击事件
				else {
					onClick();
				}
				isMoved = false;
			} else {
				onClick();
			}
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	private void onClick() {
		if (dateLoadClickListener != null && isEnabled()) {
			dateLoadClickListener.onClick(this);
			performClick();
		}
	}

}
