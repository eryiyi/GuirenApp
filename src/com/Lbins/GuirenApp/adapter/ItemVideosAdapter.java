package com.Lbins.GuirenApp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.module.Videos;
import com.Lbins.GuirenApp.ui.GalleryUrlActivity;
import com.Lbins.GuirenApp.util.Constants;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.PictureGridview;
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
//            holder.item_pic = (ImageView) convertView.findViewById(R.id.item_pic);
            holder.item_title = (TextView) convertView.findViewById(R.id.item_title);
//            holder.item_play = (ImageView) convertView.findViewById(R.id.item_play);
            holder.item_comment_btn = (ImageView) convertView.findViewById(R.id.item_comment_btn);
            holder.item_share_btn = (ImageView) convertView.findViewById(R.id.item_share_btn);
            holder.item_favour_btn = (ImageView) convertView.findViewById(R.id.item_favour_btn);
            holder.gridview_detail_picture = (PictureGridview) convertView.findViewById(R.id.gridview_detail_picture);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Videos favour = findEmps.get(position);
        holder.gridview_detail_picture.setSelector(new ColorDrawable(Color.TRANSPARENT));
        if (findEmps != null) {
//            imageLoader.displayImage(favour.getPicUrl(), holder.item_pic, GuirenApplication.options, animateFirstListener);
            holder.item_title.setText(favour.getTitle()==null?"":favour.getTitle());
            holder.item_title.setText(favour.getTitle());
            holder.item_dateline.setText(favour.getDateline());
            holder.item_favour.setText((favour.getZanNum()==null?"0":favour.getZanNum()) + "点赞");
            holder.item_comment.setText((favour.getPlNum()==null?"0":favour.getPlNum()) + "评论");

            if (!StringUtil.isNullOrEmpty(favour.getVideoUrl())) {
                final String[] videoUrls = favour.getVideoUrl().split(",");//视频链接切割
                final String[] picUrls = new String[videoUrls.length];//图片链接切割
                if (videoUrls.length > 0) {
                    for(int i=0;i<videoUrls.length;i++){
                        picUrls[i] = favour.getPicUrl();
                    }
                    //有多张图
                    holder.gridview_detail_picture.setAdapter(new ImageGridViewAdapter(picUrls, mContext));
                    holder.gridview_detail_picture.setClickable(true);
                    holder.gridview_detail_picture.setPressed(true);
                    holder.gridview_detail_picture.setEnabled(true);
                    holder.gridview_detail_picture.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            Intent intent = new Intent(mContext, GalleryUrlActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                            intent.putExtra(Constants.IMAGE_URLS, favour.getPicUrl());
//                            intent.putExtra(Constants.IMAGE_POSITION, position);
//                            mContext.startActivity(intent);
                            String videoUrl = videoUrls[position];
                            final Uri uri = Uri.parse(videoUrl);
                            final Intent it = new Intent(Intent.ACTION_VIEW, uri);
                            mContext.startActivity(it);
                        }
                    });
                }
            }
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
//        holder.item_play.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClickContentItemListener.onClickContentItem(position, 4, null);
//            }
//        });

        return convertView;
    }

    class ViewHolder {
        TextView item_comment;
        TextView item_dateline;
        TextView item_favour;
//        ImageView item_pic;
        TextView item_title;
//        ImageView item_play;
        ImageView item_comment_btn;
        ImageView item_share_btn;
        ImageView item_favour_btn;
        PictureGridview gridview_detail_picture;
    }

}