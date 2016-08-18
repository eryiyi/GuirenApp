package com.Lbins.GuirenApp.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.OnClickContentItemListener;
import com.Lbins.GuirenApp.adapter.RenmaiAdapter;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.EmpsData;
import com.Lbins.GuirenApp.library.PullToRefreshBase;
import com.Lbins.GuirenApp.library.PullToRefreshListView;
import com.Lbins.GuirenApp.module.*;
import com.Lbins.GuirenApp.util.Constants;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.CustomProgressDialog;
import com.Lbins.GuirenApp.widget.MyAlertDialog;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.view.wheelcity.OnWheelChangedListener;
import com.view.wheelcity.WheelView;
import com.view.wheelcity.adapters.AbstractWheelTextAdapter;
import com.view.wheelcity.adapters.ArrayWheelAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/6/3.
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener ,OnClickContentItemListener {
    private Resources res;

    //    RECORD_URL
    private PullToRefreshListView listView;
    private ImageView no_collections;
    private RenmaiAdapter adapter;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private List<Emp> recordList = new ArrayList<Emp>();

    Emp recordtmp;//转换用

    private TextView btn_one;
    private TextView btn_two;

    String keyStr;

    private EditText input_edittext;
    private String mm_hangye_id;
    private String mm_emp_countryId;

    private String mm_hangye_name;
    private String mm_emp_countryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        res = getResources();
        registerBoradcastReceiver();
        mm_hangye_id = getIntent().getExtras().getString("mm_hangye_id");
        mm_emp_countryId = getIntent().getExtras().getString("mm_emp_countryId");
        mm_hangye_name = getIntent().getExtras().getString("mm_hangye_name");
        mm_emp_countryName = getIntent().getExtras().getString("mm_emp_countryName");
//        mm_hangye_id = getGson().fromJson(getSp().getString("mm_hangye_id", ""), String.class);
//        mm_emp_cityId = getGson().fromJson(getSp().getString("mm_emp_cityId", ""), String.class);
        initView();
        progressDialog = new CustomProgressDialog(SearchActivity.this, "正在加载中",R.anim.custom_dialog_frame);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        initData();
        this.findViewById(R.id.back).setOnClickListener(this);
        if(!StringUtil.isNullOrEmpty(mm_hangye_name)){
            btn_one.setText(mm_hangye_name);
        }
        if(!StringUtil.isNullOrEmpty(mm_emp_countryName)){
            btn_two.setText(mm_emp_countryName);
        }
        btn_one.setOnClickListener(this);
        btn_two.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_one:
            {
                View viewDialog = dialogmHy();
                final MyAlertDialog dialog1 = new MyAlertDialog(SearchActivity.this)
                        .builder()
                        .setTitle("请选择行业")
                        .setView(viewDialog)
                        .setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mm_hangye_id = "";
                                mm_hangye_name = "点击切换行业";
                                btn_one.setText(mm_hangye_name);
                                keyStr = input_edittext.getText().toString();
                                progressDialog = new CustomProgressDialog(SearchActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                                progressDialog.setCancelable(true);
                                progressDialog.setIndeterminate(true);
                                progressDialog.show();
                                initData();
                            }
                        });
                dialog1.setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_one.setText(hangYeType.getMm_hangye_name());
                        mm_hangye_id = hangYeType.getMm_hangye_id();
                        mm_hangye_name = hangYeType.getMm_hangye_name();
                        keyStr = input_edittext.getText().toString();
                        progressDialog = new CustomProgressDialog(SearchActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                        progressDialog.setCancelable(true);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                        initData();
                    }
                });
                dialog1.show();
            }
                break;
            case R.id.btn_two:
            {
                View viewDialog = dialogm();
                final MyAlertDialog dialog1 = new MyAlertDialog(SearchActivity.this)
                        .builder()
                        .setTitle("请选择地区")
                        .setView(viewDialog)
                        .setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mm_emp_countryId = "";
                                mm_emp_countryName = "点击切换地区";
                                btn_two.setText(mm_emp_countryName);
                                keyStr = input_edittext.getText().toString();
                                progressDialog = new CustomProgressDialog(SearchActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                                progressDialog.setCancelable(true);
                                progressDialog.setIndeterminate(true);
                                progressDialog.show();
                                initData();
                            }
                        });
                dialog1.setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_two.setText(countryObj.getArea());
                        mm_emp_countryId = countryObj.getAreaID();
                        mm_emp_countryName = countryObj.getArea();
                        keyStr = input_edittext.getText().toString();
                        progressDialog = new CustomProgressDialog(SearchActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                        progressDialog.setCancelable(true);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                        initData();
                    }
                });
                dialog1.show();
            }
                break;
            case R.id.no_record:
                IS_REFRESH = true;
                pageIndex = 1;
                progressDialog = new CustomProgressDialog(SearchActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                initData();
                break;
            case R.id.back:
                finish();
                break;
            case R.id.btn_search:
                keyStr = input_edittext.getText().toString();
                progressDialog = new CustomProgressDialog(SearchActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                initData();
                break;
        }
    }

    ProvinceObj provinceObj = null;
    CityObj cityObj = null;
    CountryObj countryObj = null;
    private String cityTxt = "";
    private View dialogm() {
        View contentView = LayoutInflater.from(SearchActivity.this).inflate(
                R.layout.wheelcity_cities_layout, null);
        final WheelView country = (WheelView) contentView
                .findViewById(R.id.wheelcity_country);
        country.setVisibleItems(3);
        country.setViewAdapter(new CountryAdapter(SearchActivity.this));

        final List<CityObj> cities = new ArrayList<CityObj>();
        final List<CountryObj> ccities = new ArrayList<CountryObj>();

        final WheelView city = (WheelView) contentView
                .findViewById(R.id.wheelcity_city);
        city.setVisibleItems(0);

        final WheelView ccity = (WheelView) contentView
                .findViewById(R.id.wheelcity_ccity);
        ccity.setVisibleItems(0);

        country.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                provinceObj = GuirenApplication.provinces.get(newValue);
                List<String> lists = new ArrayList<String>();
                for(CityObj cityObj : GuirenApplication.cities){
                    if(cityObj.getFather().equals(provinceObj.getProvinceID())){
                        cities.add(cityObj);
                        lists.add(cityObj.getCity());
                    }
                }
                updateCities(city, lists, newValue);
//                cityTxt = AddressData.PROVINCES[country.getCurrentItem()]
//                        + " | "
//                        + AddressData.CITIES[country.getCurrentItem()][city
//                        .getCurrentItem()]
//                        + " | "
//                        + AddressData.COUNTIES[country.getCurrentItem()][city
//                        .getCurrentItem()][ccity.getCurrentItem()];
            }
        });

        city.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                List<CityObj> cities = new ArrayList<CityObj>();
                for(CityObj cityObj : GuirenApplication.cities){
                    if(cityObj.getFather().equals(provinceObj.getProvinceID())){
                        cities.add(cityObj);
                    }
                }

                cityObj = cities.get(newValue);
                List<String> lists = new ArrayList<String>();
                for(CountryObj countryObj : GuirenApplication.areas){
                    if(countryObj.getFather().equals(cityObj.getCityID())){
                        ccities.add(countryObj);
                        lists.add(countryObj.getArea());
                    }
                }
                updatecCities(ccity, lists, country.getCurrentItem(),
                        newValue);
//                cityTxt = AddressData.PROVINCES[country.getCurrentItem()]
//                        + " | "
//                        + AddressData.CITIES[country.getCurrentItem()][city
//                        .getCurrentItem()]
//                        + " | "
//                        + AddressData.COUNTIES[country.getCurrentItem()][city
//                        .getCurrentItem()][ccity.getCurrentItem()];
            }
        });

        ccity.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                List<String> lists = new ArrayList<String>();
                List<CountryObj> ccities = new ArrayList<CountryObj>();
                for(CountryObj countryObj : GuirenApplication.areas){
                    if(countryObj.getFather().equals(cityObj.getCityID())){
                        ccities.add(countryObj);
                        lists.add(countryObj.getArea());
                    }
                }
                countryObj = ccities.get(newValue);
//                cityTxt = AddressData.PROVINCES[country.getCurrentItem()]
//                        + " | "
//                        + AddressData.CITIES[country.getCurrentItem()][city
//                        .getCurrentItem()]
//                        + " | "
//                        + AddressData.COUNTIES[country.getCurrentItem()][city
//                        .getCurrentItem()][ccity.getCurrentItem()];
            }
        });

        country.setCurrentItem(4);
        city.setCurrentItem(4);
        ccity.setCurrentItem(4);
        return contentView;
    }

    /**
     * Updates the city wheel
     */
    private void updateCities(WheelView city, List<String> cities, int index) {
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(SearchActivity.this,
                cities);
        adapter.setTextSize(18);
        adapter.setTextColor(res.getColor(R.color.text_color));
        city.setViewAdapter(adapter);
        city.setCurrentItem(0);
    }

    /**
     * Updates the ccity wheel
     */
    private void updatecCities(WheelView city, List<String> ccities, int index,
                               int index2) {
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(SearchActivity.this,
                ccities);
        adapter.setTextSize(18);
        adapter.setTextColor(res.getColor(R.color.text_color));
        city.setViewAdapter(adapter);
        city.setCurrentItem(0);
    }

    /**
     * Adapter for countries
     */
    private class CountryAdapter extends AbstractWheelTextAdapter {
        // Countries names
        private List<ProvinceObj> countries = GuirenApplication.provinces;

        /**
         * Constructor
         */
        protected CountryAdapter(Context context) {
            super(context, R.layout.wheelcity_country_layout, NO_RESOURCE);
            setItemTextResource(R.id.wheelcity_country_name);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return countries.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return countries.get(index).getProvince();
        }
    }



    void initView(){
        //初始化
        btn_one = (TextView) this.findViewById(R.id.btn_one);
        btn_two = (TextView) this.findViewById(R.id.btn_two);
        btn_one.setOnClickListener(this);
        btn_two.setOnClickListener(this);
        no_collections = (ImageView) this.findViewById(R.id.no_record);
        no_collections.setOnClickListener(this);
        listView = (PullToRefreshListView) this.findViewById(R.id.lstv);
        adapter = new RenmaiAdapter(recordList, SearchActivity.this, getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(SearchActivity.this.getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(SearchActivity.this.getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                initData();
            }
        });
        listView.setAdapter(adapter);
        adapter.setOnClickContentItemListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Emp record = recordList.get(position - 1);
                Intent detail = new Intent(SearchActivity.this, ProfileActivity.class);
                detail.putExtra("mm_emp_id", record.getMm_emp_id());
                startActivity(detail);
            }
        });
        input_edittext = (EditText) findViewById(R.id.input_edittext);
        findViewById(R.id.btn_search).setOnClickListener(this);
        if(!StringUtil.isNullOrEmpty(keyStr)){
            input_edittext.setText(keyStr);
        }

    }

    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_RENMAI_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            EmpsData data = getGson().fromJson(s, EmpsData.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                if (IS_REFRESH) {
                                    recordList.clear();
                                }
                                recordList.addAll(data.getData());
                                listView.onRefreshComplete();
                                if(recordList.size() > 0){
                                    no_collections.setVisibility(View.GONE);
                                    listView.setVisibility(View.VISIBLE);
                                }else {
                                    no_collections.setVisibility(View.VISIBLE);
                                    listView.setVisibility(View.GONE);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(SearchActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SearchActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SearchActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", String.valueOf(pageIndex));
                if(!StringUtil.isNullOrEmpty(input_edittext.getText().toString())){
                    params.put("keyword", input_edittext.getText().toString());
                }
                if(!StringUtil.isNullOrEmpty(mm_hangye_id)){
                    params.put("mm_hangye_id", mm_hangye_id);
                }
               if(!StringUtil.isNullOrEmpty(mm_emp_countryId)){
                   params.put("mm_emp_countryId", mm_emp_countryId);
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

        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(request);
    }

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        switch (flag){
            case 1:
                //查看我和他的人脉关系
                Toast.makeText(SearchActivity.this,"功能尚未开发，敬请期待",Toast.LENGTH_LONG).show();
                break;
        }
    }

    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Constants.SELECT_HANGYE_TYPE_SUCCESS)){
                HangYeType hangYeType = (HangYeType) intent.getExtras().get("hangYeType");
                mm_hangye_id = hangYeType.getMm_hangye_id();
                btn_one.setText(hangYeType.getMm_hangye_name() );
                progressDialog = new CustomProgressDialog(SearchActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }
            if(action.equals(Constants.SEND_SELECT_AREA_SUCCESS)){
                CountryObj hangYeType = (CountryObj) intent.getExtras().get("countryObj");
                mm_emp_countryId = hangYeType.getAreaID();
                btn_two.setText(hangYeType.getArea());
                progressDialog = new CustomProgressDialog(SearchActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }

        }
    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.SELECT_HANGYE_TYPE_SUCCESS);
        myIntentFilter.addAction(Constants.SEND_SELECT_AREA_SUCCESS);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }


    public void onDestroy() {
        super.onPause();
        //注销短信监听广播
        unregisterReceiver(mBroadcastReceiver);
    };



    HangYeType hangYeType = null;
    private View dialogmHy() {
        View contentView = LayoutInflater.from(SearchActivity.this).inflate(
                R.layout.wheelhangye_hy_layout, null);
        final WheelView country = (WheelView) contentView
                .findViewById(R.id.wheelhy_hy);
        country.setVisibleItems(3);
        country.setViewAdapter(new HangyeAdapter(SearchActivity.this));

        country.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                hangYeType = GuirenApplication.listsTypeHy.get(newValue);
            }
        });

        country.setCurrentItem(4);
        return contentView;
    }

    /**
     * Adapter for countries
     */
    private class HangyeAdapter extends AbstractWheelTextAdapter {
        // Countries names
        private List<HangYeType> countries = GuirenApplication.listsTypeHy;

        /**
         * Constructor
         */
        protected HangyeAdapter(Context context) {
            super(context, R.layout.wheelcity_country_layout, NO_RESOURCE);
            setItemTextResource(R.id.wheelcity_country_name);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return countries.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return countries.get(index).getMm_hangye_name();
        }
    }

}
