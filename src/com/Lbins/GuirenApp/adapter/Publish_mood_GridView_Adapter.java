package com.Lbins.GuirenApp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/4
 * Time: 11:31
 * 类的功能、说明写在此处.
 */
public class Publish_mood_GridView_Adapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> dataList;
    private ViewHolder viewHolder;

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public Publish_mood_GridView_Adapter(Context c, ArrayList<String> dataList) {
        this.mContext = c;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.publish_mood_gridview_item, null);
            convertView.setTag(viewHolder);
            viewHolder.imageview = (ImageView) convertView.findViewById(R.id.row_gridview_imageview);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String path = dataList.get(position);
        if (path.contains("camera_default")) {
            viewHolder.imageview.setImageResource(R.drawable.addphoto_button_pressed);
        } else {
            imageLoader.displayImage("file://" + path, viewHolder.imageview, GuirenApplication.options);
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView imageview;
    }
}
