package com.Lbins.GuirenApp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.MainActivity;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.EmpData;
import com.Lbins.GuirenApp.huanxin.DemoHelper;
import com.Lbins.GuirenApp.huanxin.db.DemoDBManager;
import com.Lbins.GuirenApp.module.Emp;
import com.Lbins.GuirenApp.util.StringUtil;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhl on 2016/5/6.
 */
public class WelcomeActivity extends BaseActivity implements Runnable  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        // 启动一个线程
        new Thread(WelcomeActivity.this).start();
    }


    @Override
    public void run() {
        try {
            // 3秒后跳转到登录界面
            Thread.sleep(1500);
            SharedPreferences.Editor editor = getSp().edit();
            boolean isFirstRun = getSp().getBoolean("isFirstRun", true);
//            if (isFirstRun) {
//                editor.putBoolean("isFirstRun", false);
//                editor.commit();
//                Intent loadIntent = new Intent(WelcomeActivity.this, AboutActivity.class);
//                startActivity(loadIntent);
//                finish();
//            } else {
                if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_mobile", ""), String.class)) && !StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("password", ""), String.class))){
                    loginData();
                }else{
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
//            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void loginData(){
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
                                    showMsg(WelcomeActivity.this, getResources().getString(R.string.login_error_one));
                                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                } else if (Integer.parseInt(code) == 2) {
                                    showMsg(WelcomeActivity.this, getResources().getString(R.string.login_error_two));
                                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                } else if (Integer.parseInt(code) == 3) {
                                    showMsg(WelcomeActivity.this, getResources().getString(R.string.login_error_three));
                                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                } else if (Integer.parseInt(code) == 4) {
                                    showMsg(WelcomeActivity.this, getResources().getString(R.string.login_error_four));
                                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }else {
                                    showMsg(WelcomeActivity.this, getResources().getString(R.string.login_error));
                                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                                    startActivity(intent);
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
                        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(WelcomeActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", getGson().fromJson(getSp().getString("mm_emp_mobile", ""), String.class));
                params.put("password", getGson().fromJson(getSp().getString("password", ""), String.class));
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


    public void saveAccount(final Emp emp) {
        //登录成功，绑定百度云推送
//        if (StringUtil.isNullOrEmpty(emp.getUserId())) {
            //进行绑定
            PushManager.startWork(getApplicationContext(),
                    PushConstants.LOGIN_TYPE_API_KEY,
                    com.Lbins.GuirenApp.baidu.Utils.getMetaValue(WelcomeActivity.this, "api_key"));
//        } else {
//            //如果已经绑定，就保存
//            save("userId", emp.getUserId());
//        }

        // 登陆成功，保存用户名密码
        save("mm_emp_id", emp.getMm_emp_id());
        save("mm_emp_mobile", emp.getMm_emp_mobile());
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

//        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
//        startActivity(intent);
//        finish();
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
//                boolean updatenick = EMClient.getInstance().updateCurrentUserNick(
//                        GuirenApplication.currentUserNick.trim());

                boolean updatenick = EMClient.getInstance().updateCurrentUserNick(emp.getMm_emp_nickname());

                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }

                // get user's info (this should be get from App's server or 3rd party service)
                DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();

                Intent intent = new Intent(WelcomeActivity.this,
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
                        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                        startActivity(intent);
//                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
//                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}
