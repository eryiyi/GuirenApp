package com.Lbins.GuirenApp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.module.HangYeType;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;


public class Pro_type_adapter extends BaseAdapter {
	ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	// 定义Context
		private LayoutInflater mInflater;
	    private List<HangYeType> list;
	    private Context context;
	    private HangYeType type;
		public Pro_type_adapter(Context context, List<HangYeType> list){
			mInflater= LayoutInflater.from(context);
			this.list=list;
			this.context=context;
		}
		
		@Override
		public int getCount() {
			if(list!=null&&list.size()>0)
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
			final MyView view;
			if(convertView==null){
				view=new MyView();
				convertView=mInflater.inflate(R.layout.list_pro_type_item, null);
				view.name=(TextView)convertView.findViewById(R.id.typename);
				convertView.setTag(view);
			}else{
				view=(MyView) convertView.getTag();
			}
			if(list!=null&&list.size()>0)
			{
				type=list.get(position);
				if(type != null){
					view.name.setText(type.getMm_hangye_name());
				}
			}
	        return convertView;
		}

		private class MyView{
			private TextView name;
		}
		
}
