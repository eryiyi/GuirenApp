package com.Lbins.GuirenApp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.*;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.FavourAdapter;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.FavoursDATA;
import com.Lbins.GuirenApp.library.PullToRefreshBase;
import com.Lbins.GuirenApp.library.PullToRefreshListView;
import com.Lbins.GuirenApp.module.Favour;
import com.Lbins.GuirenApp.util.Constants;
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
 * author: ${zhanghailong}
 * Date: 2015/2/6
 * Time: 13:50
 * 类的功能、说明写在此处.
 */
public class DetailFavourActivity extends BaseActivity implements View.OnClickListener {
    private PullToRefreshListView detail_favour_lstv;//列表
    private ImageView detail_favour_back;//返回
    private TextView detail_favour_title;//标题
    private FavourAdapter adapter;
    List<Favour> favours;
    private static boolean IS_REFRESH = true;
    private String recordId = "";
    private String emp_id = "";//当前登陆者UUID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_favour_xml);
        emp_id = getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class);
        recordId = getIntent().getExtras().getString(Constants.RECORD_UUID);
        initView();
        getFavour();
    }

    private void initView() {
        favours = new ArrayList<Favour>();
        detail_favour_lstv = (PullToRefreshListView) this.findViewById(R.id.detail_favour_lstv);
        detail_favour_back = (ImageView) this.findViewById(R.id.detail_favour_back);
        detail_favour_back.setOnClickListener(this);
        detail_favour_title = (TextView) this.findViewById(R.id.detail_favour_title);
        adapter = new FavourAdapter(favours, this);
//        adapter.setOnClickContentItemListener(this);
        detail_favour_lstv.setAdapter(adapter);
        detail_favour_lstv.setMode(PullToRefreshBase.Mode.BOTH);
        detail_favour_lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(DetailFavourActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                getFavour();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(DetailFavourActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                getFavour();
            }
        });
        detail_favour_lstv.setAdapter(adapter);
        detail_favour_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Favour favour = favours.get(i-1);
                if(favour != null){
                    if (!emp_id.equals(favour.getEmpId())) {
                        Intent profile = new Intent(DetailFavourActivity.this, ProfileActivity.class);
                        profile.putExtra("mm_emp_id", favour.getEmpId());
                        startActivity(profile);
                    } else {
                        Intent profile = new Intent(DetailFavourActivity.this, EditEmpActivity.class);
                        startActivity(profile);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_favour_back:
                finish();
                break;
        }
    }

    private void getFavour() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_FAVOUR_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            FavoursDATA data = getGson().fromJson(s, FavoursDATA.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                favours.clear();
                                detail_favour_lstv.onRefreshComplete();
                                favours.addAll(data.getData());
                                detail_favour_title.setText(favours.size() + "人觉得它很赞");
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(DetailFavourActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailFavourActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailFavourActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("recordId", recordId);
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

    Favour favour;

//    @Override
//    public void onClickContentItem(int position, int flag, Object object) {
//        switch (flag) {
//            case 1:
//                favour = favours.get(position);
//                if (!emp_id.equals(favour.getEmpId())) {
//                    Intent profile = new Intent(DetailFavourActivity.this, ProfilePersonalActivity.class);
//                    profile.putExtra(Constants.EMPID, favour.getEmpId());
//                    startActivity(profile);
//                } else {
//                    Intent profile = new Intent(DetailFavourActivity.this, UpdateProfilePersonalActivity.class);
//                    startActivity(profile);
//                }
//                break;
//        }
//    }

}
