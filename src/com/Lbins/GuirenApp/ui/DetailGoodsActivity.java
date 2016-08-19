package com.Lbins.GuirenApp.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.SuccessData;
import com.Lbins.GuirenApp.module.PaopaoGoods;
import com.Lbins.GuirenApp.util.Constants;
import com.Lbins.GuirenApp.util.GuirenHttpUtils;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.DeletePopWindow;
import com.Lbins.GuirenApp.widget.GoodsTelPopWindow;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/7
 * Time: 9:06
 * 类的功能、说明写在此处.
 */
public class DetailGoodsActivity extends BaseActivity implements
        View.OnClickListener  {
    private Resources res;
    private ImageView detail_goods_back;//返回
//    private Button foot_cart;//加入购物车
//    private Button foot_order;//立即购买
//    private TextView foot_goods;//购物车
//    private TextView button_favour;//收藏按钮

    private PaopaoGoods goods;

    private String schoolId = "";
    private String emp_id = "";//当前登陆者UUID
    private String emp_type = "";//当前登陆者类别
//
//    UMSocialService mController;
//    String shareCont = "";//内容
//    String shareUrl = InternetURL.SHARE_GOODS;
//    String shareParams = "";
//    String appID = Constants.social_wx_key;
//    String sharePic = "";
//    Resources res;
//    Drawable img_favour;
    private GoodsTelPopWindow goodsTelPopWindow;
    private DeletePopWindow deleteWindow;

    //下拉菜单
//    private GoodsPopMenu menu;
//    List<String> arrayMenu = new ArrayList<>();

    private WebView webview;

//    String topPosition;
//    private ImageView top;
//    PullToRefreshListView lstv;
//    private GoodsCommentAdapter adapter;
//    private List<GoodsComment> comments;
//    private int pageIndex = 1;
//    private static boolean IS_REFRESH = true;
//    private TextView select_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res=getResources();
        setContentView(R.layout.detail_goods_xml);
        goods = (PaopaoGoods) getIntent().getExtras().get(Constants.GOODS);
        initView();
    }


    //Web视图
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private void initView() {
        webview = (WebView) this.findViewById(R.id.webview);
        //设置WebView属性，能够执行Javascript脚本
        webview.getSettings().setJavaScriptEnabled(true);
        //加载需要显示的网页
        webview.loadUrl(InternetURL.DETAIL_GOODS_URL + "?id=" + goods.getId());
        //设置Web视图
        webview.setWebViewClient(new HelloWebViewClient());
        webview.addJavascriptInterface(new Contact(), "contact");
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String openUrl) {
                if ("protocol://lianxi".equals(openUrl)) { //伪代码。判断是否是需要过滤的url,是的话，就返回不处理
                    return true;
                }

                return super.shouldOverrideUrlLoading(view, openUrl);
            }
        });

        detail_goods_back = (ImageView) this.findViewById(R.id.detail_goods_back);
        detail_goods_back.setOnClickListener(this);

    }


    class Contact {
        // 添加一个对象, 让JS可以访问该对象的方法, 该对象中可以调用JS中的方法
        public void showDialog(){
            goodsTelPopWindow = new GoodsTelPopWindow(DetailGoodsActivity.this, itemsOnClick);
            //显示窗口
            goodsTelPopWindow.showAtLocation(DetailGoodsActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_goods_back://返回
                finish();
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // 选择是否删除
    private void showSelectImageDialog() {
        deleteWindow = new DeletePopWindow(DetailGoodsActivity.this, itemsOnClickOne);
        //显示窗口
        deleteWindow.showAtLocation(DetailGoodsActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    boolean isMobileNet, isWifiNet;
    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClickOne = new View.OnClickListener() {

        public void onClick(View v) {
            deleteWindow.dismiss();
            //判断是否有网
            try {
                isMobileNet = GuirenHttpUtils.isMobileDataEnable(DetailGoodsActivity.this);
                isWifiNet = GuirenHttpUtils.isWifiDataEnable(DetailGoodsActivity.this);
                if (!isMobileNet && !isWifiNet) {
                    showMsg(DetailGoodsActivity.this, "请检查网络链接");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            switch (v.getId()) {
                case R.id.btn_sure:
                    delete();
                    break;
                default:
                    break;
            }
        }
    };

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            goodsTelPopWindow.dismiss();
            switch (v.getId()) {
                case R.id.camera: {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + goods.getTel()));
                    DetailGoodsActivity.this.startActivity(intent);
                }
                break;
                case R.id.mapstorage: {
                    if (!emp_id.equals(goods.getEmpId())) {
                        Intent profile = new Intent(DetailGoodsActivity.this, ProfileActivity.class);
                        profile.putExtra("mm_emp_id", goods.getEmpId());
                        startActivity(profile);
                    } else {
                        Intent profile = new Intent(DetailGoodsActivity.this, EditEmpActivity.class);
                        startActivity(profile);
                    }
                }
                break;
                case R.id.qq:
                    String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + goods.getQq();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    break;
                default:
                    break;
            }
        }
    };


    //删除商品方法
    private void delete() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.DELETE_GOODS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                Toast.makeText(DetailGoodsActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                                //删除之后  发广播
                                Intent intent1 = new Intent(Constants.SEND_GOOD_SUCCESS);
                                sendBroadcast(intent1);
                                finish();
                            } else {
                                Toast.makeText(DetailGoodsActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailGoodsActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailGoodsActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", goods.getId());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        getRequestQueue().add(request);
    }
//    showSelectImageDialog();




//    //收藏
//    private void favour() {
//        StringRequest request = new StringRequest(
//                Request.Method.POST,
//                InternetURL.SAVE_FAVOUR,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        if (StringUtil.isJson(s)) {
//                            SuccessData data = getGson().fromJson(s, SuccessData.class);
//                            if (data.getCode() == 200) {
//                                button_favour.setCompoundDrawables(null, img_favour, null, null);
//                                Toast.makeText(DetailGoodsActivity.this, R.string.goods_favour_success, Toast.LENGTH_SHORT).show();
//                            }else if(data.getCode() == 2){
//                                button_favour.setCompoundDrawables(null, img_favour, null, null);
//                                Toast.makeText(DetailGoodsActivity.this, R.string.goods_favour_error_one, Toast.LENGTH_SHORT).show();
//                            }else {
//                                Toast.makeText(DetailGoodsActivity.this, R.string.goods_favour_error_two, Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(DetailGoodsActivity.this, R.string.goods_favour_error_two, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        Toast.makeText(DetailGoodsActivity.this, R.string.goods_favour_error_two, Toast.LENGTH_SHORT).show();
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("goods_id", goods.getId());
//                params.put("emp_id_favour", emp_id);
//                params.put("emp_id_goods", goods.getEmpId());
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                return params;
//            }
//        };
//        getRequestQueue().add(request);
//    }
}
