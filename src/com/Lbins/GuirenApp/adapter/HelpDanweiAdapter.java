package com.Lbins.GuirenApp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.module.HelpDanwei;

import java.util.List;

/**
 */
public class HelpDanweiAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<HelpDanwei> records;
    private Context mContext;

    public HelpDanweiAdapter(List<HelpDanwei> records, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_help_type, null);
            holder.card_number = (TextView) convertView.findViewById(R.id.card_number);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final HelpDanwei cell = records.get(position);//获得元素
        if (cell != null) {
            holder.card_number.setText(cell.getHelp_danwei_name());
        }

        return convertView;
    }

    class ViewHolder {
        TextView card_number;
    }
}
