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

/**
 * author: ${zhanghailong}
 * Date: 2015/2/5
 * Time: 16:17
 * 类的功能、说明写在此处.
 */
public class ImageGridViewAdapter extends BaseAdapter {
    private String[] imageUrls;
    private Context mContext;
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    public ImageGridViewAdapter(String[] imageUrls, Context mContext) {
        this.imageUrls = imageUrls;
        this.mContext = mContext;

    }

    @Override
    public int getCount() {
        return imageUrls.length;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ImageView imageView;
        if (convertView == null) {
            imageView = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.item_grid_detail_image, parent, false);
        } else {
            imageView = (ImageView) convertView;
        }
        // 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView
        String img_url = imageUrls[position];
        if(img_url.indexOf("7xt74j.com1.z0.glb.clouddn.com") > 0){
            //图片保存到七牛上了，有缩率图
            img_url = img_url + "-yasuoone";
        }
        imageLoader.displayImage(img_url, imageView, GuirenApplication.options);
        return imageView;
    }
    

}

