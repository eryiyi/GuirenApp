/**
 *
 */
package com.Lbins.GuirenApp.adapter;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import com.Lbins.GuirenApp.ui.AboutActivity;
import com.Lbins.GuirenApp.ui.LoginActivity;
import com.Lbins.GuirenApp.widget.ViewPageItemView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 程序启动时加载图片
 */
public class AboutViewPageAdapter extends PagerAdapter {

    private AboutActivity context;

    private JSONArray jsonArray;

    private Map<Integer, ViewPageItemView> hashMap;

    public AboutViewPageAdapter(AboutActivity context, JSONArray jsonArray) {
        this.context = context;
        this.jsonArray = jsonArray;
        this.hashMap = new HashMap<Integer, ViewPageItemView>();
    }

    // 返回资源数目
    @Override
    public int getCount() {
        return jsonArray.length();
    }

    // 初始化要显示的资源
    @Override
    public Object instantiateItem(View container, final int position) {
        ViewPageItemView itemView;
        if (hashMap.containsKey(position)) {
            itemView = hashMap.get(position);
            itemView.reload();
        } else {
            itemView = new ViewPageItemView(context);
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(position);
                itemView.setData(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            hashMap.put(position, itemView);
            ((ViewPager) container).addView(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = null;
                    switch (position) {
                        case 2:
                            Intent main = new Intent(context, LoginActivity.class);
                            context.startActivity(main);
                            break;
                    }
                }
            });
        }

        return itemView;
    }

    // 进行资源回收
    @Override
    public void destroyItem(View container, int position, Object object) {
        ViewPageItemView itemView = (ViewPageItemView) object;
        itemView.recycle();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void restoreState(Parcelable parcelable, ClassLoader classLoader) {
        ;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}
