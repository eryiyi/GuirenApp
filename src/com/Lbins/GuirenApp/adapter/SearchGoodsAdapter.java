package com.Lbins.GuirenApp.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.module.PaopaoGoods;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2014/8/6
 * Time: 8:47
 * 类的功能、说明写在此处.
 */
public class SearchGoodsAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<PaopaoGoods> list;
    private Context context;
    Resources res;

    public SearchGoodsAdapter(List<PaopaoGoods> list, Context context) {
        res = context.getResources();
        this.list = list;
        this.context = context;
    }

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount()
    {
        // TODO Auto-generated method stub
        return 2;
    }

    @Override
    public int getItemViewType(int position)
    {
        // TODO Auto-generated method stub
        if (position == 0)
        {
            return 0;
        }
        return 1;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_goods, null);
            holder.item_cover = (ImageView) convertView.findViewById(R.id.item_cover);
            holder.item_title = (TextView) convertView.findViewById(R.id.item_title);
            holder.item_sellPrice = (TextView) convertView.findViewById(R.id.item_sellPrice);
            holder.item_marketPrice = (TextView) convertView.findViewById(R.id.item_marketPrice);
            holder.item_nickname = (TextView) convertView.findViewById(R.id.item_nickname);
            holder.item_zhiying = (TextView) convertView.findViewById(R.id.item_zhiying);
            holder.item_marketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PaopaoGoods cell = list.get(position);
        holder.item_sellPrice.setText(String.format(res.getString(R.string.goods_prices), cell.getSellPrice()));
        holder.item_marketPrice.setText(String.format(res.getString(R.string.goods_prices), cell.getMarketPrice()));
        String titlte = cell.getName();
        if(titlte!= null && titlte.length()>20){
            holder.item_title.setText(titlte.substring(0,19));
        }else {
            holder.item_title.setText(titlte);
        }
        holder.item_nickname.setText(cell.getNickName());
        if("1".endsWith(cell.getIs_zhiying())){
            holder.item_zhiying.setVisibility(View.VISIBLE);
        }
        //加载图片
        imageLoader.displayImage(cell.getCover(), holder.item_cover, GuirenApplication.options, animateFirstListener);
        return convertView;
    }

    class ViewHolder {
        ImageView item_cover;
        TextView item_title;
        TextView item_sellPrice;
        TextView item_nickname;
        TextView item_marketPrice;
        TextView item_zhiying;
    }
}
