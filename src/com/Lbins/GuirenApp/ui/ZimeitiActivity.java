package com.Lbins.GuirenApp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.ItemDianpuAdapter;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.DianpuData;
import com.Lbins.GuirenApp.library.PullToRefreshBase;
import com.Lbins.GuirenApp.library.PullToRefreshListView;
import com.Lbins.GuirenApp.module.EmpDianpu;
import com.Lbins.GuirenApp.util.StringUtil;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dianpu_fragment);
        emp_id = getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class);
        initView();
        initData();
    }

    private void initView() {
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
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                initData();
            }
        });
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    EmpDianpu dianpu = listgoods.get(position-1);
                    if (dianpu != null) {
                        Intent detail = new Intent(ZimeitiActivity.this, ProfileZmtActivity.class);
                        detail.putExtra("mm_emp_id", dianpu.getMm_emp_id());
                        startActivity(detail);
                    }
                } catch (Exception e) {

                }
            }
        });
        no_record = (ImageView) this.findViewById(R.id.no_record);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ZimeitiActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("keyWords", "");
                params.put("page", String.valueOf(pageIndex));
                if(!StringUtil.isNullOrEmpty(GuirenApplication.latStr)){
                    params.put("lat",GuirenApplication.latStr);
                }
                if(!StringUtil.isNullOrEmpty(GuirenApplication.lngStr)){
                    params.put("lng",GuirenApplication.lngStr);
                }
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
