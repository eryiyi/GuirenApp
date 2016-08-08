package com.Lbins.GuirenApp.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.huanxin.DemoHelper;
import com.Lbins.GuirenApp.huanxin.ui.BlacklistActivity;
import com.hyphenate.EMCallBack;

/**
 * Created by zhl on 2016/6/13.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
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
                        startActivity(new Intent(SettingActivity.this, com.Lbins.GuirenApp.ui.LoginActivity.class));
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
