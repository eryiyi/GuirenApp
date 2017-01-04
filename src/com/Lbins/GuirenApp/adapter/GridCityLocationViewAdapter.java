package com.Lbins.GuirenApp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.module.CityObj;

import java.util.ArrayList;

public class GridCityLocationViewAdapter extends BaseAdapter {

	private ArrayList<CityObj> list;
	private CityObj type;
	private Context context;
	Holder view;

	public GridCityLocationViewAdapter(Context context, ArrayList<CityObj> list) {
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		if (list != null && list.size() > 0)
			return list.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_grid_view_text, null);
			view = new Holder(convertView);
			convertView.setTag(view);
		} else {
			view = (Holder) convertView.getTag();
		}
		if (list != null && list.size() > 0) {
			type = list.get(position);
			view.name.setText(type.getCity());
		}

		return convertView;
	}

	private class Holder {
		private TextView name;

		public Holder(View view) {
			name = (TextView) view.findViewById(R.id.typename);
		}
	}

}
