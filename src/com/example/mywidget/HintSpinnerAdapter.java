package com.example.mywidget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**用于设定自定义spinner hint的字体方法与大小
 * @author steven
 *
 */
public class HintSpinnerAdapter extends ArrayAdapter<String> {
	Context context;
	List<String> items = new ArrayList<String>();

	public HintSpinnerAdapter(final Context context,
			final int textViewResourceId, List<String> objects) {
		super(context, textViewResourceId, objects);
		this.items = objects;
		this.context = context;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(
					android.R.layout.simple_spinner_item, parent, false);
		}

		return convertView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(
					android.R.layout.simple_spinner_item, parent, false);
		}

		TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
		tv.setText(items.get(position));
		tv.setTextColor(Color.parseColor("#808080"));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		return convertView;
	}
}
