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
import com.Lbins.GuirenApp.module.Emp;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * 动态
 */
public class RenmaiAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<Emp> records;
    private Context mContext;
    private String mEmp_id;//当前登陆者的UUID
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public RenmaiAdapter(List<Emp> records, Context mContext, String emp_id) {
        this.records = records;
        this.mContext = mContext;
        this.mEmp_id = emp_id;
    }

    @Override
    public int getCount() {
        return records.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.renmai_item, null);
            holder.home_viewed_item_cover = (ImageView) convertView.findViewById(R.id.home_viewed_item_cover);
            holder.home_viewed_item_name = (TextView) convertView.findViewById(R.id.home_viewed_item_name);
            holder.home_viewed_item_time = (TextView) convertView.findViewById(R.id.home_viewed_item_time);
            holder.home_viewed_item_cont = (TextView) convertView.findViewById(R.id.home_viewed_item_cont);
            holder.home_item_school = (TextView) convertView.findViewById(R.id.home_item_school);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Emp cell = records.get(position);//获得元素
        if (cell != null) {
            holder.home_viewed_item_name.setText(cell.getMm_emp_nickname());
            holder.home_item_school.setText(cell.getProvinceName()+cell.getCityName()+cell.getAreaName() );
            imageLoader.displayImage(cell.getMm_emp_cover(), holder.home_viewed_item_cover, GuirenApplication.txOptions, animateFirstListener);
            holder.home_viewed_item_cont.setText( "["+cell.getMm_hangye_name()+"]");
        }

        holder.home_viewed_item_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 1, null);
            }
        });

        return convertView;
    }

    class ViewHolder {
        ImageView home_viewed_item_cover;//头像
        TextView home_viewed_item_name;//昵称
        TextView home_viewed_item_time;//日期
        TextView home_viewed_item_cont;//内容
        TextView home_item_school;//所属学校
    }
}
