package com.Lbins.GuirenApp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.module.Notice;
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
public class NoticeAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<Notice> findEmps;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    public NoticeAdapter(List<Notice> findEmps, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.notice_item_xml, null);
            holder.notice_item_dateline = (TextView) convertView.findViewById(R.id.notice_item_dateline);
            holder.notice_item_title = (TextView) convertView.findViewById(R.id.notice_item_title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Notice favour = findEmps.get(position);
        if (findEmps != null) {
            holder.notice_item_title.setText(StringUtil.replaceBlank(favour.getMm_notice_title()));
            holder.notice_item_dateline.setText(favour.getDateline());
        }
        return convertView;
    }

    class ViewHolder {
        TextView notice_item_title;
        TextView notice_item_dateline;
    }

}