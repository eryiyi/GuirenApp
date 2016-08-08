/**
 *
 */
package com.Lbins.GuirenApp.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.Lbins.GuirenApp.R;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewPageItemView extends FrameLayout {

    /**
     * 图片的ImageView
     */
    private ImageView imageView;
    /**
     * 图片的名称
     */
    private TextView tvTitle;

    /**
     * 图片的Bitmap
     */
    private Bitmap bitmap;

    /**
     * 要显示图片的JsonObject
     */
    private JSONObject jsonObject;

    public ViewPageItemView(Context context) {
        super(context);

        initView(context);
    }

    public ViewPageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    public ViewPageItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initView(context);
    }

    // 初始控件
    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.viewpage_itemview, null);

        imageView = (ImageView) view.findViewById(R.id.imageView);
//        tvTitle = (TextView) view.findViewById(R.id.tvTitle);

        addView(view);
    }

    // 填充数据
    public void setData(JSONObject jsonObject) {
        this.jsonObject = jsonObject;

        try {
            int resourceId = jsonObject.getInt("resourceId");
            String title = jsonObject.optString("title");

            imageView.setImageResource(resourceId);
//            tvTitle.setText(title);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // 资源回收
    public void recycle() {
        imageView.setImageBitmap(null);

        if (null == bitmap || bitmap.isRecycled()) {
            return;
        }

        bitmap.recycle();
        bitmap = null;
    }

    // 重新加载资源
    public void reload() {
        try {
            int resourceId = jsonObject.getInt("resourceId");
            imageView.setImageResource(resourceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
