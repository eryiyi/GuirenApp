package com.Lbins.GuirenApp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.module.GdTypeObj;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 */
public class ItemZmtTypeAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<GdTypeObj> records;
    private Context mContext;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类


    public ItemZmtTypeAdapter(List<GdTypeObj> records, Context mContext) {
        this.records = records;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_zmt_type, null);
            holder.item_title = (TextView) convertView.findViewById(R.id.item_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final GdTypeObj cell = records.get(position);//获得元素
        if (cell != null) {
            holder.item_title.setText(cell.getGd_type_name());
        }
        return convertView;
    }

    class ViewHolder {

        TextView item_title;
    }
}
