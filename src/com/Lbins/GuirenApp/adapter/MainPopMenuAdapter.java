package com.Lbins.GuirenApp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.Lbins.GuirenApp.R;

import java.util.ArrayList;

/**
 * author: liuzwei
 * Date: 2014/8/7
 * Time: 10:31
 * 商品详情页右上角  弹出功能菜单选择器
 */
public class MainPopMenuAdapter extends BaseAdapter {
    private ArrayList<String> itemList;
    private LayoutInflater inflater;
    private Context mContext;

    public MainPopMenuAdapter(ArrayList<String> itemList, Context mContext) {
        this.itemList = itemList;
        this.mContext = mContext;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.main_popmenu_item, null);
            holder.groupItem = (TextView) convertView.findViewById(R.id.main_popmenu_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.groupItem.setText(itemList.get(position));
//        ImageView icon = (ImageView) convertView.findViewById(R.id.popmenu_item_img);//设置每个条目的图标
//        if (0 == position) {
//            icon.setBackgroundResource(R.drawable.woyaocansai);
//        } else if (1 == position) {
//            icon.setBackgroundResource(R.drawable.cansaiguize);
//        } else if (2 == position) {
//            icon.setBackgroundResource(R.drawable.wangqihuigu);
//        } else {
//            icon.setBackgroundResource(R.drawable.tuichupk);
//        }
        return convertView;
    }

    class ViewHolder {
        TextView groupItem;
    }
}
