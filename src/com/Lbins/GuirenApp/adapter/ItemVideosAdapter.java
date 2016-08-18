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
import com.Lbins.GuirenApp.module.Videos;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/6
 * Time: 14:06
 */
public class ItemVideosAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<Videos> findEmps;
    private Context mContext;

    Resources res;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public ItemVideosAdapter(List<Videos> findEmps, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_videos, null);
            holder.item_comment = (TextView) convertView.findViewById(R.id.item_comment);
            holder.item_favour = (TextView) convertView.findViewById(R.id.item_favour);
            holder.item_dateline = (TextView) convertView.findViewById(R.id.item_dateline);
            holder.item_title = (TextView) convertView.findViewById(R.id.item_title);
            holder.item_pic = (ImageView) convertView.findViewById(R.id.item_pic);
            holder.item_play = (ImageView) convertView.findViewById(R.id.item_play);
            holder.item_comment_btn = (ImageView) convertView.findViewById(R.id.item_comment_btn);
            holder.item_share_btn = (ImageView) convertView.findViewById(R.id.item_share_btn);
            holder.item_favour_btn = (ImageView) convertView.findViewById(R.id.item_favour_btn);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Videos favour = findEmps.get(position);
        if (findEmps != null) {
            imageLoader.displayImage(favour.getPicUrl(), holder.item_pic, GuirenApplication.options, animateFirstListener);
            holder.item_title.setText(favour.getTitle());
            holder.item_dateline.setText(favour.getDateline());
            holder.item_favour.setText((favour.getZanNum()==null?"0":favour.getZanNum()) + "点赞");
            holder.item_comment.setText((favour.getPlNum()==null?"0":favour.getPlNum()) + "评论");
        }
        holder.item_comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 1, null);
            }
        });
        holder.item_share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 2, null);
            }
        });
        holder.item_favour_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 3, null);
            }
        });
        holder.item_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 4, null);
            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView item_comment;
        TextView item_dateline;
        TextView item_favour;
        TextView item_title;
        ImageView item_pic;
        ImageView item_play;
        ImageView item_comment_btn;
        ImageView item_share_btn;
        ImageView item_favour_btn;
    }

}