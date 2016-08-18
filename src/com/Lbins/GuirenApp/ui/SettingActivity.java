package com.Lbins.GuirenApp.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.base.ActivityTack;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.VersionUpdateObjData;
import com.Lbins.GuirenApp.huanxin.DemoHelper;
import com.Lbins.GuirenApp.huanxin.ui.BlacklistActivity;
import com.Lbins.GuirenApp.module.VersionUpdateObj;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.CustomProgressDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hyphenate.EMCallBack;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhl on 2016/6/13.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private TextView check_version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        initView();
    }

    private void initView() {
        this.findViewById(R.id.btn_quit).setOnClickListener(this);
        this.findViewById(R.id.liner_two).setOnClickListener(this);
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.liner_mobile).setOnClickListener(this);
        this.findViewById(R.id.liner_updatepwr).setOnClickListener(this);
        this.findViewById(R.id.liner_black).setOnClickListener(this);
        this.findViewById(R.id.liner_chat).setOnClickListener(this);
        this.findViewById(R.id.liner_help).setOnClickListener(this);
        this.findViewById(R.id.liner_version).setOnClickListener(this);

        check_version = (TextView) this.findViewById(R.id.check_version);
        check_version.setText(getVersion());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_quit:
                AlertDialog dialog = new AlertDialog.Builder(SettingActivity.this)
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle(getResources().getString(R.string.sure_quite))
                        .setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create();
                dialog.show();
                break;
            case R.id.liner_two:
                //=关于我们
            {
                Intent about = new Intent(SettingActivity.this, AboutUsActivity.class);
                startActivity(about);
            }
                break;
            case R.id.back:
                finish();
                break;
            case R.id.liner_updatepwr:
            {
                //修改密码
                Intent intent = new Intent(SettingActivity.this, UpdatePwrActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.liner_mobile:
            {
                //修改手机
                Intent mobiel =  new Intent(SettingActivity.this, UpdateMobileActivity.class);
                startActivity(mobiel);
            }
                break;
            case R.id.liner_black:
            {
                //我的黑名单
                Intent intent = new Intent(SettingActivity.this, BlacklistActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.liner_chat:
            {
                //聊天设置
                Intent intent = new Intent(SettingActivity.this, SetChatActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.liner_help:
            {
                //帮助文档
            }
                break;
            case R.id.liner_version:
            {
                //版本更新
                Resources res = getBaseContext().getResources();
                String message = res.getString(R.string.check_new_version).toString();
                progressDialog = new CustomProgressDialog(SettingActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                initData();
            }
                break;
        }
    }

    public void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.CHECK_VERSION_CODE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code1 = jo.getString("code");
                                if (Integer.parseInt(code1) == 200) {
                                    VersionUpdateObjData data = getGson().fromJson(s, VersionUpdateObjData.class);
                                    VersionUpdateObj versionUpdateObj = data.getData();
                                    if("true".equals(versionUpdateObj.getFlag())){
                                        //更新
                                        final Uri uri = Uri.parse(versionUpdateObj.getDurl());
                                        final Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(it);
                                    }else{
                                        showMsg(SettingActivity.this, "已是最新版本");
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(SettingActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SettingActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_version_code", getV());
                params.put("mm_version_package", "com.Lbins.Mlt");
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

    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return this.getString(R.string.version_name) + version;
        } catch (Exception e) {
            e.printStackTrace();
            return this.getString(R.string.can_not_find_version_name);
        }
    }

    String getV(){
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    void logout() {
        save("password", "");
        save("isLogin", "0");
        //调用广播，刷新主页
        DemoHelper.getInstance().logout(false,new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        finish();
//                        startActivity(new Intent(SettingActivity.this, com.Lbins.GuirenApp.ui.LoginActivity.class));
                        ActivityTack.getInstanse().exit(SettingActivity.this);
                    }
                });
            }
            @Override
            public void onProgress(int progress, String status) {
            }
            @Override
            public void onError(int code, String message) {
            }
        });
    }

}
