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
import com.Lbins.GuirenApp.face.FaceConversionUtil;
import com.Lbins.GuirenApp.module.CommentContent;
import com.Lbins.GuirenApp.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * author: zhanghl
 * Date: 2014/8/9
 * Time: 11:07
 * 类的功能、说明写在此处.
 */
public class DetailCommentAdapter extends BaseAdapter {
    private ViewHolder holder;
    private Context context;
    private List<CommentContent> list;
    private LayoutInflater inflater;
    private OnClickContentItemListener onClickContentItemListener;
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public DetailCommentAdapter(Context context, List<CommentContent> list) {
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.detail_comment, null);
            holder.comment_photo = (ImageView) convertView.findViewById(R.id.comment_photo);
            holder.comment_nickname = (TextView) convertView.findViewById(R.id.comment_nickname);
            holder.comment_f_nickname = (TextView) convertView.findViewById(R.id.comment_f_nickname);
            holder.comment_time = (TextView) convertView.findViewById(R.id.comment_time);
            holder.comment_content = (TextView) convertView.findViewById(R.id.comment_content);
            holder.floor_comment = (TextView) convertView.findViewById(R.id.floor_comment);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CommentContent commentContent = list.get(position);
        if (commentContent != null) {
            imageLoader.displayImage(commentContent.getCover(), holder.comment_photo, GuirenApplication.options);
            holder.comment_nickname.setText(commentContent.getNickName());
            holder.comment_time.setText(commentContent.getDateline());
            if (StringUtil.isNullOrEmpty(commentContent.getfNickName())) {//没有父评论
                holder.comment_f_nickname.setVisibility(View.GONE);
            } else {
                holder.comment_f_nickname.setVisibility(View.VISIBLE);
                holder.comment_f_nickname.setText("回复<" + commentContent.getfNickName() + ">: ");
            }
            if (!StringUtil.isNullOrEmpty(commentContent.getContent())) {
                holder.comment_content.setVisibility(View.VISIBLE);
                int textsize = (int) holder.comment_content.getTextSize();
                textsize = StringUtil.dp2px(context, textsize+25);
                holder.comment_content.setText(FaceConversionUtil.getInstace().getExpressionString(context, commentContent.getContent(),textsize));
            }
        }
        //头像点击事件
        holder.comment_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 1, null);
            }
        });
        //昵称点击事件
        holder.comment_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 1, null);
            }
        });
        holder.floor_comment.setText(String.valueOf(position + 1) + "#");
        return convertView;
    }

    class ViewHolder {
        ImageView comment_photo;//头像
        TextView comment_nickname;//用户昵称
        TextView comment_f_nickname;//父评论昵称
        TextView comment_time;//评论时间
        TextView comment_content;//评论内容
        TextView floor_comment;//评论楼层
    }
}
