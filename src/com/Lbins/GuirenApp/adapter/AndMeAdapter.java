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
import com.Lbins.GuirenApp.module.Relate;
import com.Lbins.GuirenApp.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/6
 * Time: 14:06
 * 类的功能、说明写在此处.
 */
public class AndMeAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<Relate> findEmps;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    public AndMeAdapter(List<Relate> findEmps, Context mContext) {
        this.findEmps = findEmps;
        this.mContext = mContext;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.andme_item_xml, null);
            holder.andme_item_cover = (ImageView) convertView.findViewById(R.id.andme_item_cover);
            holder.andme_item_nickname = (TextView) convertView.findViewById(R.id.andme_item_nickname);
            holder.andme_item_cont = (TextView) convertView.findViewById(R.id.andme_item_cont);
            holder.andme_item_dateline = (TextView) convertView.findViewById(R.id.andme_item_dateline);
            holder.is_read = (ImageView) convertView.findViewById(R.id.is_read);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Relate favour = findEmps.get(position);
        if (findEmps != null) {
            imageLoader.displayImage(favour.getEmpCover(), holder.andme_item_cover, GuirenApplication.txOptions, animateFirstListener);
            holder.andme_item_nickname.setText(StringUtil.replaceBlank(favour.getEmpName()));
            holder.andme_item_cont.setText(favour.getCont());
            holder.andme_item_dateline.setText(favour.getDateline());
            if("0".equals(favour.getIsread())){
                holder.is_read.setVisibility(View.VISIBLE);
            }
            if("1".equals(favour.getIsread())){
                holder.is_read.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    class ViewHolder {
        ImageView is_read;
        ImageView andme_item_cover;
        TextView andme_item_nickname;
        TextView andme_item_cont;
        TextView andme_item_dateline;

    }

}