package com.Lbins.GuirenApp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.ListviewAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/4/12.
 */
public class CustomerSpinner extends Spinner implements AdapterView.OnItemClickListener {
    public static Spinner_Dialog dialog = null;
    private ArrayList<String> list;
    private static String text;

    public CustomerSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean performClick() {
        Context context = getContext();
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        final View view = inflater.inflate(R.layout.spinner_bg, null);
        final ListView listview = (ListView) view
                .findViewById(R.id.spinner_option);
        ListviewAdapter adapters = new ListviewAdapter(context, getList());
        listview.setAdapter(adapters);
        listview.setOnItemClickListener(this);
        dialog = new Spinner_Dialog(context, R.style.spinner_Dialog);//创建Dialog并设置样式主题
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        dialog.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
        dialog.show();
        dialog.addContentView(view, params);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> view, View itemView, int position,
                            long id) {
        setSelection(position);
        setText(list.get(position));
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public ArrayList<String> getList() {
        return list;
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
