package com.Lbins.GuirenApp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * 类的功能、说明写在此处.
 */
public class PictureGridview extends GridView {
//    注意：构造方法要将GridView中的三种构造全部写上，否则很可能出现解析xml文件异常的错误。
    public PictureGridview(Context context) {

        super(context);

    }

    @Override

    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_MOVE) {

            return true;  //禁止GridView滑动

        }


        return super.dispatchTouchEvent(ev);

    }

    public PictureGridview(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

    }

    public PictureGridview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
