package com.Lbins.GuirenApp.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.MainPopMenuAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class MenuPopMenu implements AdapterView.OnItemClickListener {
    private static MenuPopMenu mainMenu;

    public static MenuPopMenu getInstance(Context context, List<String> arras) {
        if (mainMenu == null) {
            mainMenu = new MenuPopMenu(context, arras);
        }
        return mainMenu;
    }

    public interface OnItemClickListener {
        public void onItemClick(int index);
    }

    private ArrayList<String> itemList;
    private Context context;
    private PopupWindow popupWindow;
    private ListView listView;
    private OnItemClickListener listener;
    private LayoutInflater inflater;

    public MenuPopMenu(Context context, List<String> arras) {
        this.context = context;
        List<String> menus = arras;
        itemList = new ArrayList<String>(menus.size());
        addItems(menus);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.main_popmenu, null);

        listView = (ListView) view.findViewById(R.id.listView1);
        listView.setAdapter(new MainPopMenuAdapter(itemList, context));
        listView.setOnItemClickListener(this);

        popupWindow = new PopupWindow(view,
                context.getResources().getDimensionPixelSize(R.dimen.popmenu_width), //这里宽度需要自己指定，使用 WRAP_CONTENT 会很大
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listener != null) {
            listener.onItemClick(position);
        }
        dismiss();
    }

    // 批量添加菜单项
    public void addItems( List<String> items) {
        for (String s : items)
            itemList.add(s);
    }

    // 单个添加菜单项
    public void addItem(String item) {
        itemList.add(item);
    }

    // 下拉式 弹出 pop菜单 parent 右下角
    public void showAsDropDown(View parent) {
        popupWindow.showAsDropDown(parent, 10,
                // 保证尺寸是根据屏幕像素密度来的
                context.getResources().getDimensionPixelSize(R.dimen.popmenu_yoff));

        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 刷新状态
        popupWindow.update();
    }

    // 隐藏菜单
    public void dismiss() {
        popupWindow.dismiss();
    }
}
