package com.Lbins.GuirenApp.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.module.HelpObj;
import com.Lbins.GuirenApp.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/6
 * Time: 14:06
 */
public class ItemHelpOneAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<HelpObj> findEmps;
    private Context mContext;

    Resources res;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public ItemHelpOneAdapter(List<HelpObj> findEmps, Context mContext) {
        this.findEmps = findEmps;
        this.mContext = mContext;
        res = mContext.getResources();
    }

    @Override
    public int getCount() {
        return findEmps.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_help_one, null);
            holder.cover = (ImageView) convertView.findViewById(R.id.cover);
            holder.pic = (ImageView) convertView.findViewById(R.id.pic);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.distance = (TextView) convertView.findViewById(R.id.distance);
            holder.item_title = (TextView) convertView.findViewById(R.id.item_title);
            holder.item_content = (TextView) convertView.findViewById(R.id.item_content);
            holder.item_danwei = (TextView) convertView.findViewById(R.id.item_danwei);
            holder.address = (TextView) convertView.findViewById(R.id.address);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final HelpObj favour = findEmps.get(position);
        if (findEmps != null) {
            imageLoader.displayImage(favour.getMm_emp_cover(), holder.cover, GuirenApplication.txOptions, animateFirstListener);
            if(!StringUtil.isNullOrEmpty(favour.getHelp_pic())){
                String[] arras = favour.getHelp_pic().split(",");
                imageLoader.displayImage(arras[0], holder.pic, GuirenApplication.options, animateFirstListener);
            }
            holder.name.setText(favour.getMm_emp_nickname());
            holder.distance.setText(favour.getHelp_type_name()==null?"":favour.getHelp_type_name());
            holder.item_title.setText(favour.getHelp_title()==null?"":favour.getHelp_title());
            holder.item_content.setText(favour.getHelp_content()==null?"":favour.getHelp_content());
            holder.address.setText((favour.getProvince()==null?"":favour.getProvince())+(favour.getCity()==null?"":favour.getCity())+(favour.getArea()==null?"":favour.getArea()));
            holder.item_danwei.setText(((favour.getHelp_money()==null?"":favour.getHelp_money())+"元")+(favour.getHelp_danwei_name()==null?"":("/"+favour.getHelp_danwei_name())));
        }

        return convertView;
    }

    class ViewHolder {
        TextView name;
        TextView distance;
        TextView item_title;
        TextView item_content;
        TextView item_danwei;
        TextView address;
        ImageView pic;
        ImageView cover;
    }

}