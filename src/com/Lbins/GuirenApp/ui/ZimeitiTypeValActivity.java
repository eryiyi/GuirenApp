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
import com.Lbins.GuirenApp.adapter.ItemVideoTypeAdapter;
import com.Lbins.GuirenApp.adapter.ItemZmtTypeAdapter;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.GdTypeObjData;
import com.Lbins.GuirenApp.module.GdTypeObj;
import com.Lbins.GuirenApp.module.VideoTypeObj;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.PictureGridview;
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
 * Created by zhl on 2016/10/1.
 */
public class ZimeitiTypeValActivity extends BaseActivity implements View.OnClickListener {
    private PictureGridview gridv_one;
    private ItemZmtTypeAdapter adapterType;
    private List<GdTypeObj> listsType = new ArrayList<GdTypeObj>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zimeit_type_val_activity);
        initView();
        getType();
    }

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        gridv_one = (PictureGridview) this.findViewById(R.id.lstv);
        adapterType = new ItemZmtTypeAdapter(listsType, ZimeitiTypeValActivity.this);
        gridv_one.setAdapter(adapterType);
        gridv_one.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(listsType.size() > position){
                    GdTypeObj videoTypeObj = listsType.get(position);
                    if(videoTypeObj != null){
                        Intent intent = new Intent();
                        intent.putExtra("gd_type_id" , videoTypeObj.getGd_type_id());
                        intent.putExtra("gd_type_name" , videoTypeObj.getGd_type_name());
                        setResult(1000 , intent);
                        finish();
                    }
                }
            }
        });
        gridv_one.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
        }
    }

    public void getType() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_GD_TYPE_LISTS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code1 = jo.getString("code");
                                if (Integer.parseInt(code1) == 200) {
                                    GdTypeObjData data = getGson().fromJson(s, GdTypeObjData.class);
                                    listsType.clear();
                                    listsType.add(new  GdTypeObj("", "全部", "0"));
                                    listsType.addAll(data.getData());
                                    adapterType.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(ZimeitiTypeValActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ZimeitiTypeValActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
}
