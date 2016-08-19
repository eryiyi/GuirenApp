package com.Lbins.GuirenApp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.AnimateFirstDisplayListener;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.RelateDATA;
import com.Lbins.GuirenApp.module.Relate;
import com.Lbins.GuirenApp.util.GuirenHttpUtils;
import com.Lbins.GuirenApp.util.StringUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhl on 2016/7/14.
 */
public class DetailRelateActivity extends BaseActivity implements View.OnClickListener {
    private String emp_relate_id;
    private Relate relate;
    private TextView nickname;
    private ImageView head;
    private TextView content;
    private String state;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_relate_activity);
        emp_relate_id = getIntent().getExtras().getString("emp_relate_id");//拜见的请求ID
        relate = (Relate)getIntent().getExtras().get("relate");
        initView();
        initData();
    }

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        head = (ImageView) this.findViewById(R.id.head);
        nickname = (TextView) this.findViewById(R.id.nickname);
        content = (TextView) this.findViewById(R.id.content);
        this.findViewById(R.id.btn_agreen).setOnClickListener(this);
        this.findViewById(R.id.btn_refuse).setOnClickListener(this);
        head.setOnClickListener(this);
    }

    void initData(){
        imageLoader.displayImage(relate.getEmpCover(), head, GuirenApplication.txOptions, animateFirstListener);
        nickname.setText(relate.getEmpName());
        content.setText(relate.getCont());
    }
    boolean isMobileNet, isWifiNet;
    @Override
    public void onClick(View v) {
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(DetailRelateActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(DetailRelateActivity.this);
            if (!isMobileNet && !isWifiNet) {
                showMsg(DetailRelateActivity.this, "请检查网络链接");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.btn_agreen:
            {
                //同意
                updataData("1");
            }
                break;
            case R.id.btn_refuse:
            {
                //拒绝
                updataData("2");
            }
                break;
            case R.id.head:
            {
                Intent intent = new Intent(DetailRelateActivity.this, ProfileActivity.class);
                intent.putExtra("mm_emp_id", relate.getEmpId());
                startActivity(intent);
            }
                break;
        }
    }
    private void updataData(final String state) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.UPDATE_MINE_CONTACTS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            RelateDATA data = getGson().fromJson(s, RelateDATA.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                showMsg(DetailRelateActivity.this, "操作成功！");
                                finish();
                            } else {
                                showMsg(DetailRelateActivity.this, "操作失败");
                            }
                        } else {
                            showMsg(DetailRelateActivity.this, "操作失败");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        showMsg(DetailRelateActivity.this, "操作失败");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_relate_id", emp_relate_id);
                params.put("state", state);
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

}
