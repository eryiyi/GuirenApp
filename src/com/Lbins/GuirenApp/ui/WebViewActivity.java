package com.Lbins.GuirenApp.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.*;
import android.widget.ImageView;
import android.widget.TextView;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.CustomProgressDialog;

/**
 * Created by Administrator on 2015/5/14.
 */
public class WebViewActivity extends BaseActivity implements View.OnClickListener ,Runnable{
    private WebView detail_webview;
    private ImageView menu;
    private String strurl;
    private String strname;

    private TextView title;
    private static final String APP_CACAHE_DIRNAME = "/webcache";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        strurl = getIntent().getExtras().getString("strurl");
        strname = getIntent().getExtras().getString("strname");

        progressDialog = new CustomProgressDialog(WebViewActivity.this, "正在加载中",R.anim.custom_dialog_frame);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        initView();
        if(!StringUtil.isNullOrEmpty(strname)){
            title.setText(strname);
        }
//        detail_webview.setInitialScale(35);
        detail_webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        detail_webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        detail_webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //设置 缓存模式
        // 开启 DOM storage API 功能
        detail_webview.getSettings().setDomStorageEnabled(true);
        //开启 database storage API 功能
        detail_webview.getSettings().setDatabaseEnabled(true);
        String cacheDirPath = getFilesDir().getAbsolutePath()+APP_CACAHE_DIRNAME;
//      String cacheDirPath = getCacheDir()getCacheDir.getAbsolutePath()+Constant.APP_DB_DIRNAME;
        //设置数据库缓存路径
        detail_webview.getSettings().setDatabasePath(cacheDirPath);
        //设置  Application Caches 缓存目录
        detail_webview.getSettings().setAppCachePath(cacheDirPath);
        //开启 Application Caches 功能
        detail_webview.getSettings().setAppCacheEnabled(true);
        detail_webview.getSettings().setJavaScriptEnabled(true);
        detail_webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        detail_webview.getSettings().setUseWideViewPort(true);
        detail_webview.getSettings().setLoadWithOverviewMode(true);
        detail_webview.requestFocus();
        detail_webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, true);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
        });

        detail_webview.loadUrl(strurl);
        detail_webview.setWebViewClient(new HelloWebViewClient());

        // 启动一个线程
        new Thread(WebViewActivity.this).start();

    }

    private void initView() {
        menu = (ImageView) this.findViewById(R.id.menu);
        menu.setOnClickListener(this);
        this.findViewById(R.id.close).setOnClickListener(this);
        detail_webview = (WebView) this.findViewById(R.id.detail_webview);
        title = (TextView) this.findViewById(R.id.title);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.menu:
                onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN,200));
                break;
            case R.id.close:
                finish();
                break;
        }
    }


    @Override
    public void run() {
        try {
            Thread.sleep(3000);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    // 设置回退
    // 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && detail_webview.canGoBack()) {
            detail_webview.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            detail_webview.loadData("", "text/html; charset=UTF-8", null);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause ()
    {
        detail_webview.reload ();
        super.onPause ();
    }


}
