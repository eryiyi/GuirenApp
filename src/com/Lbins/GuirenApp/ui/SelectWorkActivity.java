package com.Lbins.GuirenApp.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.WorkAdapter;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.HangYeTypeDara;
import com.Lbins.GuirenApp.module.HangYeType;
import com.Lbins.GuirenApp.util.Constants;
import com.Lbins.GuirenApp.util.GuirenHttpUtils;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.CustomProgressDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/5/20.
 */
public class SelectWorkActivity extends BaseActivity {
    private GridView gridView;
    private WorkAdapter adapter;
    private List<HangYeType> lists = new ArrayList<HangYeType>();
    boolean isMobileNet, isWifiNet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_work_activity);

        gridView = (GridView) this.findViewById(R.id.gridView);
        adapter = new WorkAdapter(lists, SelectWorkActivity.this);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HangYeType hangYeType = lists.get(position);
                //发送广播
                Intent intent1 = new Intent(Constants.SELECT_HANGYE_TYPE_SUCCESS);
                intent1.putExtra("hangYeType", hangYeType);
                sendBroadcast(intent1);
                finish();
            }
        });
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(SelectWorkActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(SelectWorkActivity.this);
            if (!isMobileNet && !isWifiNet) {
                showMsg(SelectWorkActivity.this ,"请检查您网络链接");
            }else {
                progressDialog = new CustomProgressDialog(SelectWorkActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                getBigType();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获得类别
    private void getBigType() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_HY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code1 =  jo.getString("code");
                                if(Integer.parseInt(code1) == 200){
                                    HangYeTypeDara data = getGson().fromJson(s, HangYeTypeDara.class);
                                    lists.clear();
                                    lists.addAll(data.getData());
                                    adapter.notifyDataSetChanged();
                                }else {
                                    Toast.makeText(SelectWorkActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(SelectWorkActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SelectWorkActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
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

    public void back(View view){
        finish();
    }
}
