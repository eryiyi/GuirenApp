package com.Lbins.GuirenApp.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.ContactAdapter;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.EmpRelateObjData;
import com.Lbins.GuirenApp.module.EmpRelateObj;
import com.Lbins.GuirenApp.util.GuirenHttpUtils;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.CustomProgressDialog;
import com.Lbins.GuirenApp.widget.SideBar;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/7/14.
 */
public class TongxunluActivity extends BaseActivity implements View.OnClickListener {
    private ListView lvContact;
    private SideBar indexBar;
    private WindowManager mWindowManager;
    private TextView mDialogText;
    private ContactAdapter adapter;

    /**
     * 昵称
     */
    private static List<EmpRelateObj> nicks = new ArrayList<EmpRelateObj>();

    boolean isMobileNet, isWifiNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        setContentView(R.layout.tongxunlu_activity);
        initView();
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(TongxunluActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(TongxunluActivity.this);
            if (!isMobileNet && !isWifiNet) {
                showMsg(TongxunluActivity.this ,"请检查您网络链接");
            }else {
                progressDialog = new CustomProgressDialog(TongxunluActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void getData(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_MINE_CONTACTS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            EmpRelateObjData data = getGson().fromJson(s, EmpRelateObjData.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                nicks.clear();
                                nicks.addAll(data.getData());
                                adapter.notifyDataSetChanged();
                            }else {
                                showMsg(TongxunluActivity.this, getResources().getString(R.string.get_data_error));
                            }
                        }else {
                            showMsg(TongxunluActivity.this, getResources().getString(R.string.get_data_error));
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
                        showMsg(TongxunluActivity.this, getResources().getString(R.string.get_data_error));
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_emp_id1", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
                params.put("state", "1");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
        }
    }
    void initView(){
        this.findViewById(R.id.back).setOnClickListener(this);
        lvContact = (ListView) this.findViewById(R.id.lvContact);
        adapter = new ContactAdapter(TongxunluActivity.this, nicks);
        lvContact.setAdapter(adapter);
        indexBar = (SideBar) this.findViewById(R.id.sideBar);
        indexBar.setListView(lvContact);
        mDialogText = (TextView) LayoutInflater.from(this).inflate(
                R.layout.list_position, null);
        mDialogText.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mWindowManager.addView(mDialogText, lp);
        indexBar.setTextView(mDialogText);
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TongxunluActivity.this, ProfileActivity.class);
                EmpRelateObj empRelateObj = nicks.get(position);
                intent.putExtra("mm_emp_id",empRelateObj.getMm_emp_id2());
                startActivity(intent);
            }
        });
    }

}
