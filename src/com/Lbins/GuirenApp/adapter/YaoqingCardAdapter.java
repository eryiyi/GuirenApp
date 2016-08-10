package com.Lbins.GuirenApp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.module.YaoqingCard;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 */
public class YaoqingCardAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<YaoqingCard> records;
    private Context mContext;
    private String mEmp_id;//当前登陆者的UUID
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public YaoqingCardAdapter(List<YaoqingCard> records, Context mContext) {
        this.records = records;
        this.mContext = mContext;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_yaoqingcard, null);
            holder.card_number = (TextView) convertView.findViewById(R.id.card_number);
            holder.card_btn = (TextView) convertView.findViewById(R.id.card_btn);
            holder.relate = (RelativeLayout) convertView.findViewById(R.id.relate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final YaoqingCard cell = records.get(position);//获得元素
        if (cell != null) {
            holder.card_number.setText(cell.getGuiren_card_num());
            if("1".equals(cell.getIs_use())){
                holder.card_btn.setText("已使用");
                holder.relate.setBackground(mContext.getResources().getDrawable(R.drawable.btn_style_gray_card));
            }else {
                holder.card_btn.setText("点击分享给好友");
                holder.relate.setBackground(mContext.getResources().getDrawable(R.drawable.btn_style_green_card));

            }
        }

        return convertView;
    }

    class ViewHolder {
        TextView card_btn;//
        TextView card_number;//
        RelativeLayout relate;//
    }
}
