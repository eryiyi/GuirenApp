package com.Lbins.GuirenApp.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.module.AdObj;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by Administrator on 2015/5/24.
 */
public class AdViewPagerAdapter extends PagerAdapter {
    private ViewHolder holder;
    private OnClickContentItemListener onClickContentItemListener;
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private List<AdObj> mPaths;
    private Context mContext;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public AdViewPagerAdapter(Context cx) {
        mContext = cx;
    }

    public void change(List<AdObj> paths) {
        mPaths = paths;
    }

    @Override
    public int getCount() {
        return mPaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == (View) obj;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        holder = new ViewHolder();
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_main_viewpage_xml, null);
        holder.iv = (ImageView) convertView.findViewById(R.id.item_pic);
        AdObj slidePic = mPaths.get(position);
        imageLoader.displayImage((slidePic.getMm_ad_pic() == null ? "" : slidePic.getMm_ad_pic()), holder.iv, GuirenApplication.options, animateFirstListener);
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 0, "000");
            }
        });
        ((ViewPager) container).addView(convertView, 0);
        return convertView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    class ViewHolder {
        ImageView iv;
    }

}
