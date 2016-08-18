package com.Lbins.GuirenApp.ui;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.receiver.SMSBroadcastReceiver;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.CustomProgressDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhl on 2016/7/8.
 */
public class ForgetActivity extends BaseActivity implements View.OnClickListener{
    Resources res;
    private EditText mm_emp_mobile;
    private EditText code;
    private EditText password;
    private EditText surepass;
    private Button btn_code;
    private Button btn;

    //mob短信
    // 填写从短信SDK应用后台注册得到的APPKEY
    private static String APPKEY = InternetURL.APP_MOB_KEY;//"69d6705af33d";0d786a4efe92bfab3d5717b9bc30a10d
    // 填写从短信SDK应用后台注册得到的APPSECRET
    private static String APPSECRET = InternetURL.APP_MOB_SCRECT;
    public String phString;//手机号码

    //短信读取
    private SMSBroadcastReceiver mSMSBroadcastReceiver;
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_pwr_activity);
        res = getResources();
        //mob短信无GUI
        SMSSDK.initSDK(this, APPKEY, APPSECRET, true);
        EventHandler eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }

        };
        SMSSDK.registerEventHandler(eh);
        initView();

        //生成广播处理
        mSMSBroadcastReceiver = new SMSBroadcastReceiver();
        //实例化过滤器并设置要过滤的广播
        IntentFilter intentFilter = new IntentFilter(ACTION);
        intentFilter.setPriority(Integer.MAX_VALUE);
        //注册广播
        this.registerReceiver(mSMSBroadcastReceiver, intentFilter);
        mSMSBroadcastReceiver.setOnReceivedMessageListener(new SMSBroadcastReceiver.MessageListener() {
            @Override
            public void onReceived(String message) {
                if (!StringUtil.isNullOrEmpty(message)) {
                    String codestr = StringUtil.valuteNumber(message);
                    if (!StringUtil.isNullOrEmpty(codestr)) {
                        code.setText(codestr);
                    }
                }
            }
        });
    }

    void initView() {
        mm_emp_mobile = (EditText) this.findViewById(R.id.mm_emp_mobile);
        code = (EditText) this.findViewById(R.id.code);
        password = (EditText) this.findViewById(R.id.password);
        surepass = (EditText) this.findViewById(R.id.surepass);
        btn_code = (Button) this.findViewById(R.id.btn_code);
        btn = (Button) this.findViewById(R.id.btn);

        btn_code.setOnClickListener(this);
        btn.setOnClickListener(this);
        this.findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_code:
                //验证码
                if (!TextUtils.isEmpty(mm_emp_mobile.getText().toString()) && mm_emp_mobile.getText().toString().length() == 11) {
                    SMSSDK.getVerificationCode("86", mm_emp_mobile.getText().toString());//发送请求验证码，手机10s之内会获得短信验证码
                    phString = mm_emp_mobile.getText().toString();
                    btn_code.setClickable(false);//不可点击
                    MyTimer myTimer = new MyTimer(60000, 1000);
                    myTimer.start();
                } else {
                    showMsg(ForgetActivity.this, getResources().getString(R.string.pwr_error_seven));
                    return;
                }
                break;
            case R.id.btn:
                //确定
                if (StringUtil.isNullOrEmpty(mm_emp_mobile.getText().toString())) {
                    showMsg(ForgetActivity.this, getResources().getString(R.string.pwr_error_seven));
                    return;
                }
                if (StringUtil.isNullOrEmpty(code.getText().toString())) {
                    showMsg(ForgetActivity.this, getResources().getString(R.string.pwr_error_eight));
                    return;
                }

                if (StringUtil.isNullOrEmpty(password.getText().toString())) {
                    showMsg(ForgetActivity.this, getResources().getString(R.string.pwr_error_three));
                    return;
                }
                if (password.getText().toString().length() > 18 || password.getText().toString().length() < 6) {
                    showMsg(ForgetActivity.this, getResources().getString(R.string.pwr_error_four));
                    return;
                }
                if (StringUtil.isNullOrEmpty(surepass.getText().toString())) {
                    showMsg(ForgetActivity.this, getResources().getString(R.string.pwr_error_five));
                    return;
                }
                if (!password.getText().toString().equals(surepass.getText().toString())) {
                    showMsg(ForgetActivity.this, getResources().getString(R.string.pwr_error_six));
                    return;
                }
                progressDialog = new CustomProgressDialog(ForgetActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                SMSSDK.submitVerificationCode("86", phString, code.getText().toString());
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    class MyTimer extends CountDownTimer {

        public MyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            btn_code.setText(res.getString(R.string.daojishi_three));
            btn_code.setClickable(true);//可点击
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btn_code.setText(res.getString(R.string.daojishi_one) + millisUntilFinished / 1000 + res.getString(R.string.daojishi_two));
        }
    }

    void reg() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.UPDATE_PWR_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    showMsg(ForgetActivity.this, getResources().getString(R.string.pwr_success_one));
                                    save("password", password.getText().toString());
                                    finish();
                                } else {
                                    showMsg(ForgetActivity.this, getResources().getString(R.string.pwr_error_nine));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showMsg(ForgetActivity.this, getResources().getString(R.string.pwr_error_nine));
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
                        showMsg(ForgetActivity.this, getResources().getString(R.string.pwr_error_nine));
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_emp_mobile", mm_emp_mobile.getText().toString());
                params.put("newpass", password.getText().toString());
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


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                System.out.println("--------result" + event);
                //短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
//                    Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
                    reg();

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //已经验证
//                    Toast.makeText(UpdatePwrActivity.this, R.string.code_msg_one, Toast.LENGTH_SHORT).show();
                }

            } else {
//				((Throwable) data).printStackTrace();
                Toast.makeText(ForgetActivity.this, R.string.code_msg_two, Toast.LENGTH_SHORT).show();
//					Toast.makeText(MainActivity.this, "123", Toast.LENGTH_SHORT).show();
                int status = 0;
                try {
                    ((Throwable) data).printStackTrace();
                    Throwable throwable = (Throwable) data;

                    JSONObject object = new JSONObject(throwable.getMessage());
                    String des = object.optString("detail");
                    status = object.optInt("status");
                    if (!TextUtils.isEmpty(des)) {
                        Toast.makeText(ForgetActivity.this, des, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    SMSLog.getInstance().w(e);
                }
            }
        }
        ;
    };


    public void onDestroy() {
        super.onPause();
        SMSSDK.unregisterAllEventHandler();
        //注销短信监听广播
        this.unregisterReceiver(mSMSBroadcastReceiver);
    }

}
