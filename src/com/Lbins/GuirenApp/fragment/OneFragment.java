package com.Lbins.GuirenApp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.ItemXixunAdapter;
import com.Lbins.GuirenApp.adapter.OnClickContentItemListener;
import com.Lbins.GuirenApp.adapter.RenmaiAdapter;
import com.Lbins.GuirenApp.base.BaseFragment;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.dao.DBHelper;
import com.Lbins.GuirenApp.data.EmpsData;
import com.Lbins.GuirenApp.data.XixunObjData;
import com.Lbins.GuirenApp.library.PullToRefreshBase;
import com.Lbins.GuirenApp.library.PullToRefreshListView;
import com.Lbins.GuirenApp.module.*;
import com.Lbins.GuirenApp.ui.ProfileActivity;
import com.Lbins.GuirenApp.ui.TuopuActivity;
import com.Lbins.GuirenApp.util.Constants;
import com.Lbins.GuirenApp.util.GuirenHttpUtils;
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
 * Created by zhl on 2016/5/6.
 */
public class OneFragment extends BaseFragment implements View.OnClickListener ,OnClickContentItemListener {
    private View view;
    private Resources res;
//    private PullToRefreshListView listView;
//    private RenmaiAdapter adapter;
//    private int pageIndex = 1;
//    private static boolean IS_REFRESH = true;
//    private List<Emp> recordList = new ArrayList<Emp>();
//    Emp recordtmp;//转换用

    private TextView btn_one;
    private TextView btn_two;
    String keyStr;

    private EditText input_edittext;
    private String mm_hangye_id;
    private String mm_emp_countryId;

    private String mm_hangye_name;
    private String mm_emp_countryName;
    boolean isMobileNet, isWifiNet;

    private LinearLayout headView;

    private PullToRefreshListView listView;
    private ImageView no_collections;
    private ItemXixunAdapter adapter;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private List<XixunObj> recordList = new ArrayList<XixunObj>();
    private String emp_id = "";//当前登陆者UUID

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.one_fragment, null);
        res = getActivity().getResources();
        registerBoradcastReceiver();
        initView();

        if(!StringUtil.isNullOrEmpty(mm_hangye_name)){
            btn_one.setText(mm_hangye_name);
        }
        if(!StringUtil.isNullOrEmpty(mm_emp_countryName)){
            btn_two.setText(mm_emp_countryName);
        }
        btn_one.setOnClickListener(this);
        btn_two.setOnClickListener(this);

        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
            if (!isMobileNet && !isWifiNet) {
                recordList.addAll(DBHelper.getInstance(getActivity()).getXixunList());
                if(recordList.size() > 0){
                    no_collections.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }else {
                    no_collections.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();

            }else {
                initData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }



    @Override
    public void onClick(View view) {
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
            if (!isMobileNet && !isWifiNet) {
                Toast.makeText(getActivity(), "请检查您网络链接", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (view.getId()){
            case R.id.btn_one:
            {
                View viewDialog = dialogmHy();
                final MyAlertDialog dialog1 = new MyAlertDialog(getActivity())
                        .builder()
                        .setTitle("请选择行业")
                        .setView(viewDialog)
                        .setNegativeButton("全部", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mm_hangye_id = "";
                                mm_hangye_name = "点击切换行业";
                                btn_one.setText(mm_hangye_name);
                                keyStr = input_edittext.getText().toString();
                                //判断是否有网
                                try {
                                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
                                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
                                    if (!isMobileNet && !isWifiNet) {
                                        Toast.makeText(getActivity(), "请检查您网络链接", Toast.LENGTH_SHORT).show();
                                    }else {
                                       //todo
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                dialog1.setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_one.setText(hangYeType.getMm_hangye_name());
                        mm_hangye_id = hangYeType.getMm_hangye_id();
                        mm_hangye_name = hangYeType.getMm_hangye_name();
                        keyStr = input_edittext.getText().toString();
                        //判断是否有网
                        try {
                            isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
                            isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
                            if (!isMobileNet && !isWifiNet) {
                                Toast.makeText(getActivity(), "请检查您网络链接", Toast.LENGTH_SHORT).show();
                            }else {
                               //todo
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog1.show();
            }
            break;
            case R.id.btn_two:
            {
                View viewDialog = dialogm();
                final MyAlertDialog dialog1 = new MyAlertDialog(getActivity())
                        .builder()
                        .setTitle("请选择地区")
                        .setView(viewDialog)
                        .setNegativeButton("全部", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mm_emp_countryId = "";
                                mm_emp_countryName = "点击切换地区";
                                btn_two.setText(mm_emp_countryName);
                                keyStr = input_edittext.getText().toString();
                                //判断是否有网
                                try {
                                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
                                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
                                    if (!isMobileNet && !isWifiNet) {
                                        Toast.makeText(getActivity(), "请检查您网络链接", Toast.LENGTH_SHORT).show();
                                    }else {
                                       //todo
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                dialog1.setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_two.setText(countryObj.getArea());
                        mm_emp_countryId = countryObj.getAreaID();
                        mm_emp_countryName = countryObj.getArea();
                        keyStr = input_edittext.getText().toString();
                        //判断是否有网
                        try {
                            isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
                            isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
                            if (!isMobileNet && !isWifiNet) {
                                Toast.makeText(getActivity(), "请检查您网络链接", Toast.LENGTH_SHORT).show();
                            }else {
                                //todo
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog1.show();
            }
            break;
//            case R.id.no_record:
//                IS_REFRESH = true;
//                pageIndex = 1;
//                //判断是否有网
//                try {
//                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
//                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
//                    if (!isMobileNet && !isWifiNet) {
//                        Toast.makeText(getActivity(), "请检查您网络链接", Toast.LENGTH_SHORT).show();
//                    }else {
//                        IS_REFRESH = true;
//                        pageIndex = 1;
//                        initData();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;

            case R.id.btn_search:
                keyStr = input_edittext.getText().toString();
                //判断是否有网
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
                    if (!isMobileNet && !isWifiNet) {
                        Toast.makeText(getActivity(), "请检查您网络链接", Toast.LENGTH_SHORT).show();
                    }else {
                        //todo
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    ProvinceObj provinceObj = null;
    CityObj cityObj = null;
    CountryObj countryObj = null;
    private String cityTxt = "";
    private View dialogm() {
        View contentView = LayoutInflater.from(getActivity()).inflate(
                R.layout.wheelcity_cities_layout, null);
        final WheelView country = (WheelView) contentView
                .findViewById(R.id.wheelcity_country);
        country.setVisibleItems(3);
        country.setViewAdapter(new CountryAdapter(getActivity()));

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
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(getActivity(),
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
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(getActivity(),
                ccities);
        adapter.setTextSize(18);
        adapter.setTextColor(res.getColor(R.color.text_color));
        city.setViewAdapter(adapter);
        city.setCurrentItem(0);
    }

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
            if (!isMobileNet && !isWifiNet) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String str = (String) object;
        if("1000".equals(str)){
            switch (flag){
                case 1:
                    if(recordList.size()>position){
                        XixunObj xixunObj = recordList.get(position);
                        if(xixunObj != null){
                            //查看我和他的人脉关系
                            Intent intent = new Intent(getActivity(), TuopuActivity.class);
                            intent.putExtra("mm_emp_id", xixunObj.getMm_emp_id());
                            startActivity(intent);
                        }
                    }
                    break;
            }
        }
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
//        listView = (PullToRefreshListView) view.findViewById(R.id.lstv);
        headView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.one_header_fragment, null);
        btn_one = (TextView) headView.findViewById(R.id.btn_one);
        btn_two = (TextView) headView.findViewById(R.id.btn_two);
        btn_one.setOnClickListener(this);
        btn_two.setOnClickListener(this);
        listView = (PullToRefreshListView) view.findViewById(R.id.lstv);
        no_collections = (ImageView) view.findViewById(R.id.no_record);

        ListView lst = listView.getRefreshableView();

        lst.addHeaderView(headView);

        adapter = new ItemXixunAdapter(recordList, getActivity(), emp_id);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                //判断是否有网
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
                    if (!isMobileNet && !isWifiNet) {
                        listView.onRefreshComplete();
                    }else {
                        initData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                //判断是否有网
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
                    if (!isMobileNet && !isWifiNet) {
                        listView.onRefreshComplete();
                    }else {
                        initData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                XixunObj xixunObj = recordList.get(position-2);
                Intent intentV = new Intent(getActivity(), ProfileActivity.class);
                intentV.putExtra("mm_emp_id", xixunObj.getMm_emp_id());
                startActivity(intentV);
            }
        });
        no_collections.setOnClickListener(this);
        adapter.setOnClickContentItemListener(this);

        input_edittext = (EditText) view.findViewById(R.id.input_edittext);
        view.findViewById(R.id.btn_search).setOnClickListener(this);
        if(!StringUtil.isNullOrEmpty(keyStr)){
            input_edittext.setText(keyStr);
        }
        input_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
                    if (!isMobileNet && !isWifiNet) {
                        Toast.makeText(getActivity(), "请检查您网络链接", Toast.LENGTH_SHORT).show();
                    }else {
                        //todo
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //获取数据
//    private void initData() {
//        StringRequest request = new StringRequest(
//                Request.Method.POST,
//                InternetURL.GET_RENMAI_URL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        if (StringUtil.isJson(s)) {
//                            EmpsData data = getGson().fromJson(s, EmpsData.class);
//                            if (Integer.parseInt(data.getCode()) == 200) {
//                                if (IS_REFRESH) {
//                                    recordList.clear();
//                                }
//                                recordList.addAll(data.getData());
//                                listView.onRefreshComplete();
//                                adapter.notifyDataSetChanged();
//                            } else {
//                                Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                        }
//                        if(progressDialog != null){
//                            progressDialog.dismiss();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        if(progressDialog != null){
//                            progressDialog.dismiss();
//                        }
//                        Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("page", String.valueOf(pageIndex));
//                if(!StringUtil.isNullOrEmpty(input_edittext.getText().toString())){
//                    params.put("keyword", input_edittext.getText().toString());
//                }
//                if(!StringUtil.isNullOrEmpty(mm_hangye_id)){
//                    params.put("mm_hangye_id", mm_hangye_id);
//                }
//                if(!StringUtil.isNullOrEmpty(mm_emp_countryId)){
//                    params.put("mm_emp_countryId", mm_emp_countryId);
//                }
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                return params;
//            }
//        };
//
//        request.setRetryPolicy(new DefaultRetryPolicy(10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        getRequestQueue().add(request);
//    }
//
//    @Override
//    public void onClickContentItem(int position, int flag, Object object) {
//        switch (flag){
//            case 1:
//            {
//                if(recordList.size()>position){
//                    Emp emp = recordList.get(position);
//                    if(emp != null){
//                        //查看我和他的人脉关系
//                        Intent intent = new Intent(getActivity(), TuopuActivity.class);
//                        intent.putExtra("mm_emp_id", emp.getMm_emp_id());
//                        startActivity(intent);
//                    }
//                }
//            }
//                break;
//        }
//    }

    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Constants.SELECT_HANGYE_TYPE_SUCCESS)){
                HangYeType hangYeType = (HangYeType) intent.getExtras().get("hangYeType");
                mm_hangye_id = hangYeType.getMm_hangye_id();
                btn_one.setText(hangYeType.getMm_hangye_name() );
               //todo
            }
            if(action.equals(Constants.SEND_SELECT_AREA_SUCCESS)){
                CountryObj hangYeType = (CountryObj) intent.getExtras().get("countryObj");
                mm_emp_countryId = hangYeType.getAreaID();
                btn_two.setText(hangYeType.getArea());
                //todo
            }

        }
    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.SELECT_HANGYE_TYPE_SUCCESS);
        myIntentFilter.addAction(Constants.SEND_SELECT_AREA_SUCCESS);
        //注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }


    public void onDestroy() {
        super.onPause();
        //注销短信监听广播
        getActivity().unregisterReceiver(mBroadcastReceiver);
    };



    HangYeType hangYeType = null;
    private View dialogmHy() {
        View contentView = LayoutInflater.from(getActivity()).inflate(
                R.layout.wheelhangye_hy_layout, null);
        final WheelView country = (WheelView) contentView
                .findViewById(R.id.wheelhy_hy);
        country.setVisibleItems(3);
        country.setViewAdapter(new HangyeAdapter(getActivity()));

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

    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_XIXUN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            XixunObjData data = getGson().fromJson(s, XixunObjData.class);
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

                                //处理数据，需要的话保存到数据库
                                if (data != null && data.getData() != null) {
                                    DBHelper dbHelper = DBHelper.getInstance(getActivity());
                                    for (XixunObj xixunObj : data.getData()) {
                                        if (dbHelper.getXixunObjById(xixunObj.getGuiren_xixun_id()) != null) {
                                            //已经存在了 不需要插入了
                                        } else {
                                            DBHelper.getInstance(getActivity()).saveXixunObj(xixunObj);
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", String.valueOf(pageIndex));
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



}
