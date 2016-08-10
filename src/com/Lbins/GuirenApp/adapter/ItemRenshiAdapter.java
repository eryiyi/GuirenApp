package com.Lbins.GuirenApp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.module.Emp;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 */
public class ItemRenshiAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<Emp> records;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类


    public ItemRenshiAdapter(List<Emp> records, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_renmai, null);
            holder.item_head = (ImageView) convertView.findViewById(R.id.item_head);
            holder.item_line = (ImageView) convertView.findViewById(R.id.item_line);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Emp cell = records.get(position);//获得元素
        if (cell != null) {
            imageLoader.displayImage(cell.getMm_emp_cover(), holder.item_head, GuirenApplication.txOptions, animateFirstListener);
            holder.name.setText(cell.getMm_emp_nickname());
            if(position == (records.size()-1)){
                //如果是最后一个
                holder.item_line.setVisibility(View.GONE);
            }else {
                holder.item_line.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    class ViewHolder {
        ImageView item_head;
        ImageView item_line;
        TextView name;
    }
}
