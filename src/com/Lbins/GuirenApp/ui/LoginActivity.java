package com.Lbins.GuirenApp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.MainActivity;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.AnimateFirstDisplayListener;
import com.Lbins.GuirenApp.baidu.Utils;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.EmpData;
import com.Lbins.GuirenApp.huanxin.DemoHelper;
import com.Lbins.GuirenApp.huanxin.db.DemoDBManager;
import com.Lbins.GuirenApp.module.Emp;
import com.Lbins.GuirenApp.util.GuirenHttpUtils;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.CustomProgressDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhl on 2016/7/8.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private ImageView head;
    private EditText mobile;
    private EditText pwr;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        //先退出环信
        DemoHelper.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
            }
            @Override
            public void onError(int i, String s) {
            }
            @Override
            public void onProgress(int i, String s) {
            }
        });
        registerBoradcastReceiver();
        mobile = (EditText) this.findViewById(R.id.mobile);
        pwr = (EditText) this.findViewById(R.id.pwr);
        head = (ImageView) this.findViewById(R.id.head);

        this.findViewById(R.id.btn_reg).setOnClickListener(this);
        this.findViewById(R.id.btn_find).setOnClickListener(this);
        this.findViewById(R.id.btn_login).setOnClickListener(this);

        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_mobile", ""), String.class)) ){
            mobile.setText(getGson().fromJson(getSp().getString("mm_emp_mobile", ""), String.class));
        }if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("password", ""), String.class))){
            pwr.setText(getGson().fromJson(getSp().getString("password", ""), String.class));
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_cover", ""), String.class))){
            imageLoader.displayImage(getGson().fromJson(getSp().getString("mm_emp_cover", ""), String.class), head, GuirenApplication.txOptions, animateFirstListener);
        }

    }

    boolean isMobileNet, isWifiNet;

    @Override
    public void onClick(View v) {
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(getApplicationContext());
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(getApplicationContext());
            if (!isMobileNet && !isWifiNet) {
                Toast.makeText(this, R.string.net_work_error, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (v.getId()){
            case R.id.btn_reg:
                //注册
            {
                Intent intent = new Intent(LoginActivity.this, RegActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.btn_find:
                //找回密码
            {
                Intent intent = new Intent(LoginActivity.this, ForgetActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.btn_login:
                //登录

                if (StringUtil.isNullOrEmpty(mobile.getText().toString())) {
                    showMsg(LoginActivity.this, getResources().getString(R.string.error_login_one));
                    return;
                }
                if (StringUtil.isNullOrEmpty(pwr.getText().toString())) {
                    showMsg(LoginActivity.this, getResources().getString(R.string.error_login_two));
                    return;
                }
                progressDialog = new CustomProgressDialog(LoginActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                loginData();
                break;
        }
    }


    private void loginData(){
        //
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.LOGIN__URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    EmpData data = getGson().fromJson(s, EmpData.class);
                                    saveAccount(data.getData());
                                } else if (Integer.parseInt(code) == 1) {
                                    showMsg(LoginActivity.this, getResources().getString(R.string.login_error_one));
                                } else if (Integer.parseInt(code) == 2) {
                                    showMsg(LoginActivity.this, getResources().getString(R.string.login_error_two));
                                } else if (Integer.parseInt(code) == 3) {
                                    showMsg(LoginActivity.this, getResources().getString(R.string.login_error_three));
                                } else if (Integer.parseInt(code) == 4) {
                                    showMsg(LoginActivity.this, getResources().getString(R.string.login_error_four));
                                }  else {
                                    showMsg(LoginActivity.this, getResources().getString(R.string.login_error));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(LoginActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", mobile.getText().toString());
                params.put("password", pwr.getText().toString());
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

    public void saveAccount(Emp emp) {
        //登录成功，绑定百度云推送
//        if (StringUtil.isNullOrEmpty(emp.getUserId())) {
            //进行绑定
            PushManager.startWork(getApplicationContext(),
                    PushConstants.LOGIN_TYPE_API_KEY,
                    Utils.getMetaValue(LoginActivity.this, "api_key"));
//        } else {
////            如果已经绑定，就保存
//            save("userId", emp.getUserId());
//        }

        // 登陆成功，保存用户名密码
        save("mm_emp_id", emp.getMm_emp_id());
        save("mm_emp_mobile", emp.getMm_emp_mobile());
        save("password", pwr.getText().toString());
        save("mm_emp_card", emp.getGuiren_card_num());
        save("mm_emp_nickname", emp.getMm_emp_nickname());
        save("mm_emp_password", emp.getMm_emp_password());
        save("mm_emp_cover", emp.getMm_emp_cover());
        save("mm_emp_company", emp.getMm_emp_company());
        save("mm_emp_provinceId", emp.getMm_emp_provinceId());
        save("mm_emp_cityId", emp.getMm_emp_cityId());
        save("mm_emp_countryId", emp.getMm_emp_countryId());
        save("mm_emp_regtime", emp.getMm_emp_regtime());
        save("is_login", emp.getIs_login());
        save("is_use", emp.getIs_use());
        save("lat", emp.getLat());
        save("lng", emp.getLng());
        save("ischeck", emp.getIscheck());
        save("is_upate_profile", emp.getIs_upate_profile());
        save("userId", emp.getUserId());
        save("channelId", emp.getChannelId());
        save("provinceName", emp.getProvinceName());
        save("cityName", emp.getCityName());
        save("areaName", emp.getAreaName());
        save("mm_emp_email", emp.getMm_emp_email());
        save("mm_emp_sex", emp.getMm_emp_sex());
        save("mm_emp_birthday", emp.getMm_emp_birthday());
        save("mm_emp_techang", emp.getMm_emp_techang());
        save("mm_emp_xingqu", emp.getMm_emp_xingqu());
        save("mm_emp_detail", emp.getMm_emp_detail());
        save("mm_emp_up_emp", emp.getMm_emp_up_emp());
        save("guiren_card_num", emp.getGuiren_card_num());
        save("mm_hangye_id", emp.getMm_hangye_id());
        save("mm_hangye_name", emp.getMm_hangye_name());
        save("top_number", emp.getTop_number());
        save("mm_emp_qq", emp.getMm_emp_qq());
        save("mm_emp_weixin", emp.getMm_emp_weixin());
        save("mm_emp_age", emp.getMm_emp_age());
        save("mm_emp_native", emp.getMm_emp_native());
        save("mm_emp_motto", emp.getMm_emp_motto());
        save("mm_emp_type", emp.getMm_emp_type());


        // close it before login to make sure DemoDB not overlap
        DemoDBManager.getInstance().closeDB();

        // reset current user name before login
        DemoHelper.getInstance().setCurrentUserName(emp.getHxusername());
        final long start = System.currentTimeMillis();
        // call login method
        EMClient.getInstance().login(emp.getHxusername(), "111111", new EMCallBack() {

            @Override
            public void onSuccess() {

                // ** manually load all local groups and conversation
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();

                // update current user's display name for APNs
                boolean updatenick = EMClient.getInstance().updateCurrentUserNick(
                        GuirenApplication.currentUserNick.trim());
                if (!updatenick) {
//                    Log.e("LoginActivity", "update current user nick fail");
                }

                // get user's info (this should be get from App's server or 3rd party service)
                DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();

                Intent intent = new Intent(LoginActivity.this,
                        MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                runOnUiThread(new Runnable() {
                    public void run() {
//                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
//                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("reg_success_guiren")){
                if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_mobile", ""), String.class)) ){
                    mobile.setText(getGson().fromJson(getSp().getString("mm_emp_mobile", ""), String.class));
                }if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("password", ""), String.class))){
                    pwr.setText(getGson().fromJson(getSp().getString("password", ""), String.class));
                }
                if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_cover", ""), String.class))){
                    imageLoader.displayImage(getGson().fromJson(getSp().getString("mm_emp_cover", ""), String.class), head, GuirenApplication.txOptions, animateFirstListener);
                }
            }
        }

    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("reg_success_guiren");
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

}
