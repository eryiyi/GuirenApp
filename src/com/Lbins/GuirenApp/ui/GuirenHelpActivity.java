package com.Lbins.GuirenApp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.fragment.HelpOneFragment;
import com.Lbins.GuirenApp.fragment.HelpTwoFragment;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.PublishPopHelpWindow;

/**
 * Created by zhl on 2016/12/20.
 */
public class GuirenHelpActivity extends BaseActivity implements View.OnClickListener {
    //设置底部图标
    Resources res;

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;

    private HelpOneFragment oneFragment;
    private HelpTwoFragment twoFragment;

    private TextView foot_one;
    private TextView foot_three;


    private PublishPopHelpWindow publishPopWindow;

    public static EditText input_edittext;

    int tmpSelect = 0;

    private TextView right_btn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.guiren_help_activity);
        registerBoradcastReceiver();
        res = getResources();
        fm = getSupportFragmentManager();
        initView();
        switchFragment(R.id.foot_liner_one);
        if(!StringUtil.isNullOrEmpty(GuirenApplication.locationCityName)){
            right_btn.setText(GuirenApplication.locationCityName);
        }else {
            if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("cityName", ""), String.class))){
                right_btn.setText(getGson().fromJson(getSp().getString("cityName", ""), String.class));
            }
        }
    }

    private void initView() {
        foot_one = (TextView) this.findViewById(R.id.foot_one);
        foot_three = (TextView) this.findViewById(R.id.foot_three);
        this.findViewById(R.id.foot_liner_one).setOnClickListener(this);
        this.findViewById(R.id.foot_liner_three).setOnClickListener(this);
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.foot_add_liner).setOnClickListener(this);
        right_btn = (TextView) this.findViewById(R.id.right_btn);
        right_btn.setOnClickListener(this);
        input_edittext = (EditText) this.findViewById(R.id.input_edittext);
        input_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                switch (tmpSelect){
                    case 0:
                    {
                        //
                        if(oneFragment != null){
                            oneFragment.IS_REFRESH = true;
                            oneFragment.pageIndex = 1;
                            oneFragment.initData();
                        }
                    }
                    break;
                    case 1:
                    {
                        //
                        if(twoFragment != null){
                            twoFragment.IS_REFRESH = true;
                            twoFragment.pageIndex = 1;
                            twoFragment.initData();
                        }
                    }
                    break;
                }
            }
        });
    }




    public void switchFragment(int id) {
        fragmentTransaction = fm.beginTransaction();
        hideFragments(fragmentTransaction);
        switch (id) {
            case R.id.foot_liner_one:
                if (oneFragment == null) {
                    oneFragment = new HelpOneFragment();
                    fragmentTransaction.add(R.id.content_frame, oneFragment);
                } else {
                    fragmentTransaction.show(oneFragment);
                }
                foot_one.setTextColor(res.getColor(R.color.red));
                foot_three.setTextColor(res.getColor(R.color.textColortwo));
                tmpSelect = 0;
                break;
            case R.id.foot_liner_three:
                if (twoFragment == null) {
                    twoFragment = new HelpTwoFragment();
                    fragmentTransaction.add(R.id.content_frame, twoFragment);
                } else {
                    fragmentTransaction.show(twoFragment);
                }
                foot_one.setTextColor(res.getColor(R.color.textColortwo));
                foot_three.setTextColor(res.getColor(R.color.red));
                tmpSelect = 1;
                break;

        }
        fragmentTransaction.commit();
    }

    private void hideFragments(FragmentTransaction ft) {
        if (oneFragment != null) {
            ft.hide(oneFragment);
        }
        if (twoFragment != null) {
            ft.hide(twoFragment);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.back){
            finish();
        }else
        if(view.getId() == R.id.right_btn){
            //右边点击城市选择
            Intent intent = new Intent(GuirenHelpActivity.this, CityLocationActivity.class);
            startActivity(intent);
        }else if(view.getId() == R.id.foot_add_liner){
            //发布
            showSelectPublishDialog();
        }else{
            switchFragment(view.getId());
        }
    }

    // 选择是否退出发布
    private void showSelectPublishDialog() {
        publishPopWindow = new PublishPopHelpWindow(GuirenHelpActivity.this, itemOnClick);
        publishPopWindow.showAtLocation(GuirenHelpActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            publishPopWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_sure:
                {
                    Intent intent = new Intent(GuirenHelpActivity.this, AddHelpActivity.class);
                    startActivity(intent);
                }
                    break;
                case R.id.btn_cancle:
                {
                    Intent intent = new Intent(GuirenHelpActivity.this, AddFuwuActivity.class);
                    startActivity(intent);
                }
                    break;
                default:
                    break;
            }
        }
    };

    //定位地址
    void initLocation(){
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("location_city", ""), String.class))){
            //说明用户自己选择了城市
            right_btn.setText(getGson().fromJson(getSp().getString("location_city", ""), String.class));
        }else {
            if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("cityName", ""), String.class))){
                right_btn.setText(getGson().fromJson(getSp().getString("cityName", ""), String.class));
            }
        }
    }


    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("update_location_success")) {
                //定位地址
                initLocation();
            }
        }
    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("update_location_success");
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

}
