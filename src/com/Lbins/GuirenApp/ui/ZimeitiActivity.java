package com.Lbins.GuirenApp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.*;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.ItemDianpuAdapter;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.dao.DBHelper;
import com.Lbins.GuirenApp.data.DianpuData;
import com.Lbins.GuirenApp.library.PullToRefreshBase;
import com.Lbins.GuirenApp.library.PullToRefreshListView;
import com.Lbins.GuirenApp.module.EmpDianpu;
import com.Lbins.GuirenApp.util.GuirenHttpUtils;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.CustomProgressDialog;
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
 * Created by zhl on 2016/7/29.
 */
public class ZimeitiActivity extends BaseActivity implements View.OnClickListener {
    //下拉刷新
    private PullToRefreshListView lstv ;
    private ItemDianpuAdapter adapter;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private List<EmpDianpu> listgoods = new ArrayList<EmpDianpu>();
    private ImageView no_record;
    private String emp_id = "";//当前登陆者UUID

    private String gd_type_id;//分类
    private String gd_type_name;//分类名称

    boolean isMobileNet, isWifiNet;
    private TextView detail_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dianpu_fragment);
        emp_id = getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class);
        initView();
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(ZimeitiActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(ZimeitiActivity.this);
            if (!isMobileNet && !isWifiNet) {
                listgoods.addAll(DBHelper.getInstance(ZimeitiActivity.this).getDianpus());
                if(listgoods.size() > 0){
                    no_record.setVisibility(View.GONE);
                    lstv.setVisibility(View.VISIBLE);
                }else {
                    no_record.setVisibility(View.VISIBLE);
                    lstv.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }else {
                progressDialog = new CustomProgressDialog(ZimeitiActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                initData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        gd_type_id = "";
        gd_type_name = "自媒体";
        detail_title = (TextView) this.findViewById(R.id.detail_title);
        detail_title.setText(gd_type_name);
        this.findViewById(R.id.back).setOnClickListener(this);
        lstv = (PullToRefreshListView) this.findViewById(R.id.lstv);//列表
        adapter = new ItemDianpuAdapter(listgoods, ZimeitiActivity.this);
        lstv.setAdapter(adapter);
        lstv.setMode(PullToRefreshBase.Mode.BOTH);
        lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                //判断是否有网
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(ZimeitiActivity.this);
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(ZimeitiActivity.this);
                    if (!isMobileNet && !isWifiNet) {
                        lstv.onRefreshComplete();
                    }else {
                        initData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                //判断是否有网
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(ZimeitiActivity.this);
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(ZimeitiActivity.this);
                    if (!isMobileNet && !isWifiNet) {
                        lstv.onRefreshComplete();
                    }else {
                        initData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if(position > 0){
                        EmpDianpu dianpu = listgoods.get(position-1);
                        if (dianpu != null) {
                            Intent detail = new Intent(ZimeitiActivity.this, ProfileZmtActivity.class);
                            detail.putExtra("mm_emp_id", dianpu.getMm_emp_id());
                            startActivity(detail);
                        }
                    }
                } catch (Exception e) {

                }
            }
        });
        no_record = (ImageView) this.findViewById(R.id.no_record);
        this.findViewById(R.id.btn_val).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_val:
            {
                //筛选
                Intent intent = new Intent(ZimeitiActivity.this, ZimeitiTypeValActivity.class);
                startActivityForResult(intent, 1000);
            }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == 1000){
            gd_type_id = data.getExtras().getString("gd_type_id");
            gd_type_name = data.getExtras().getString("gd_type_name");
            detail_title.setText(gd_type_name);
            initData();
        }
    }


    //获得商家店铺列表
    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_DIANPU_MSG_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            DianpuData data = getGson().fromJson(s, DianpuData.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                if (IS_REFRESH) {
                                    listgoods.clear();
                                }
                                listgoods.addAll(data.getData());
                                adapter.notifyDataSetChanged();
                                lstv.onRefreshComplete();
                                //处理数据，需要的话保存到数据库
                                if (data != null && data.getData() != null) {
                                    DBHelper dbHelper = DBHelper.getInstance(ZimeitiActivity.this);
                                    for (EmpDianpu empDianpu : data.getData()) {
                                        if (dbHelper.getEmpDianpuById(empDianpu.getMm_emp_id()) == null) {
                                            DBHelper.getInstance(ZimeitiActivity.this).saveDianpu(empDianpu);
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(ZimeitiActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ZimeitiActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                        if(listgoods.size() == 0){
                            no_record.setVisibility(View.VISIBLE);
                            lstv.setVisibility(View.GONE);
                        }else {
                            no_record.setVisibility(View.GONE);
                            lstv.setVisibility(View.VISIBLE);
                        }
                        if(progressDialog != null){
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if(progressDialog != null){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(ZimeitiActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("keyWords", "");
                params.put("page", String.valueOf(pageIndex));
                params.put("gd_type_id", gd_type_id);
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
