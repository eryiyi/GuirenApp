package com.Lbins.GuirenApp.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.Lbins.GuirenApp.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;


public abstract class AbsActivity extends FragmentActivity {
    protected AbsActivity mActThis = null;
    protected ImageLoader loader;
    protected DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActThis = this;

        loader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();
    }

}
