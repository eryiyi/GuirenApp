package com.Lbins.GuirenApp.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.CityData;
import com.Lbins.GuirenApp.data.CountrysData;
import com.Lbins.GuirenApp.data.ProvinceData;
import com.Lbins.GuirenApp.module.CityObj;
import com.Lbins.GuirenApp.module.CountryObj;
import com.Lbins.GuirenApp.module.HangYeType;
import com.Lbins.GuirenApp.module.ProvinceObj;
import com.Lbins.GuirenApp.util.Constants;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.CustomProgressDialog;
import com.Lbins.GuirenApp.widget.CustomerSpinner;
import com.Lbins.GuirenApp.widget.SexRadioGroup;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/7/10.
 */
public class EditEmpActivity extends BaseActivity implements View.OnClickListener , RadioGroup.OnCheckedChangeListener{
    private EditText nickname;
    private EditText email;
    private EditText qq;
    private EditText weixin;
    private EditText company;
    private EditText xingqu;
    private EditText age;
    private EditText jianjie;
    private EditText techang;
    private EditText mm_emp_native;
    private EditText mm_emp_motto;


    private SexRadioGroup profile_personal_sex;//性别
    private RadioButton button_one;
    private RadioButton button_two;

    private String sex_selected = "";

    private TextView select_hy;//行业
    HangYeType record1;//所选择的行业类别
    //省市县
//    private CustomerSpinner province;
//    private CustomerSpinner city;
//    private CustomerSpinner country;
//    private List<ProvinceObj> provinces = new ArrayList<ProvinceObj>();//省
//    private ArrayList<String> provinceNames = new ArrayList<String>();//省份名称
//    private List<CityObj> citys = new ArrayList<CityObj>();//市
//    private ArrayList<String> cityNames = new ArrayList<String>();//市名称
//    private List<CountryObj> countrys = new ArrayList<CountryObj>();//区
//    private ArrayList<String> countrysNames = new ArrayList<String>();//区名称
//    ArrayAdapter<String> ProvinceAdapter;
//    ArrayAdapter<String> cityAdapter;
//    ArrayAdapter<String> countryAdapter;
//    private String provinceName = "";
//    private String cityName = "";
//    private String countryName = "";
//    private String provinceCode = "";
//    private String cityCode = "";
//    private String countryCode = "";

    private Resources res;
    private String mm_hangye_id= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_emp_activity);
        res = getResources();
        registerBoradcastReceiver();
        this.findViewById(R.id.sign_in_button).setOnClickListener(this);
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.liner_one).setOnClickListener(this);
        select_hy = (TextView) this.findViewById(R.id.select_hy);

        nickname = (EditText) this.findViewById(R.id.nickname);
        email = (EditText) this.findViewById(R.id.email);
        qq = (EditText) this.findViewById(R.id.qq);
        weixin = (EditText) this.findViewById(R.id.weixin);
        company = (EditText) this.findViewById(R.id.company);
        xingqu = (EditText) this.findViewById(R.id.xingqu);
        age = (EditText) this.findViewById(R.id.age);
        jianjie = (EditText) this.findViewById(R.id.jianjie);
        techang = (EditText) this.findViewById(R.id.techang);
        mm_emp_native = (EditText) this.findViewById(R.id.mm_emp_native);
        mm_emp_motto = (EditText) this.findViewById(R.id.mm_emp_motto);
        profile_personal_sex = (SexRadioGroup) this.findViewById(R.id.segment_text);
        profile_personal_sex.setOnCheckedChangeListener(this);
        button_one = (RadioButton) this.findViewById(R.id.button_one);
        button_two = (RadioButton) this.findViewById(R.id.button_two);
        sex_selected = getGson().fromJson(getSp().getString("mm_emp_sex", ""), String.class);

        nickname.setText(getGson().fromJson(getSp().getString("mm_emp_nickname", ""), String.class));

//        ProvinceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, provinceNames);
//        ProvinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        province = (CustomerSpinner) findViewById(R.id.mm_emp_provinceId);
//        province.setAdapter(ProvinceAdapter);
//        province.setList(provinceNames);
//        province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                citys.clear();
//                cityNames.clear();
//                cityNames.add(getResources().getString(R.string.select_city));
//                cityAdapter.notifyDataSetChanged();
//                ProvinceObj province = null;
//                if (provinces != null && position > 0) {
//                    province = provinces.get(position - 1);
//                    provinceName = province.getProvince();
//                    provinceCode = province.getProvinceID();
//                } else if (provinces != null) {
//                    province = provinces.get(position);
//                    provinceName = province.getProvince();
//                    provinceCode = province.getProvinceID();
//                }
//                try {
//                    getCitys();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//
//        cityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cityNames);
//        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        city = (CustomerSpinner) findViewById(R.id.mm_emp_cityId);
//        city.setAdapter(cityAdapter);
//        city.setList(cityNames);
//        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position > 0) {
//                    countrys.clear();
//                    countrysNames.clear();
//                    countrysNames.add(getResources().getString(R.string.select_area));
//                    CityObj city = citys.get(position - 1);
//                    cityName = city.getCity();
//                    cityCode = city.getCityID();
//                    try {
//                        getArea();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    country.setEnabled(true);
//                    countrysNames.clear();
//                    countrysNames.add(res.getString(R.string.select_area));
//                    countrys.clear();
//                    countryAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        countryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countrysNames);
//        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        country = (CustomerSpinner) findViewById(R.id.mm_emp_countryId);
//        country.setAdapter(countryAdapter);
//        country.setList(countrysNames);
//        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position != 0) {
//                    CountryObj country = countrys.get(position - 1);
//                    countryCode = country.getAreaID();
//                    countryName = country.getArea();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_sex", ""), String.class))){
            sex_selected = getGson().fromJson(getSp().getString("mm_emp_sex", ""), String.class);
            if ("1".equals(sex_selected)) {
                button_one.setChecked(false);//男未选中
                button_two.setChecked(true);//女选中
            }
            if ("0".equals(sex_selected)) {
                button_one.setChecked(true);
                button_two.setChecked(false);
            }
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_motto", ""), String.class))){
            mm_emp_motto.setText(getGson().fromJson(getSp().getString("mm_emp_motto", ""), String.class));
        }else {
            mm_emp_motto.setText("");
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_native", ""), String.class))){
            mm_emp_native.setText(getGson().fromJson(getSp().getString("mm_emp_native", ""), String.class));
        }else {
            mm_emp_native.setText("");
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_email", ""), String.class))){
            email.setText(getGson().fromJson(getSp().getString("mm_emp_email", ""), String.class));
        }else {
            email.setText("");
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_qq", ""), String.class))){
            qq.setText(getGson().fromJson(getSp().getString("mm_emp_qq", ""), String.class));
        }else {
            qq.setText("");
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_weixin", ""), String.class))){
            weixin.setText(getGson().fromJson(getSp().getString("mm_emp_weixin", ""), String.class));
        }else {
            weixin.setText("");
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_company", ""), String.class))){
            company.setText(getGson().fromJson(getSp().getString("mm_emp_company", ""), String.class));
        }else {
            company.setText("");
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_techang", ""), String.class))){
            techang.setText(getGson().fromJson(getSp().getString("mm_emp_techang", ""), String.class));
        }else {
            techang.setText("");
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_xingqu", ""), String.class))){
            xingqu.setText(getGson().fromJson(getSp().getString("mm_emp_xingqu", ""), String.class));
        }else {
            xingqu.setText("");
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_age", ""), String.class))){
            age.setText(getGson().fromJson(getSp().getString("mm_emp_age", ""), String.class));
        }else {
            age.setText("");
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_detail", ""), String.class))){
            jianjie.setText(getGson().fromJson(getSp().getString("mm_emp_detail", ""), String.class));
        }else {
            jianjie.setText("");
        }
        mm_hangye_id = getGson().fromJson(getSp().getString("mm_hangye_id", ""), String.class);
        select_hy.setText(getGson().fromJson(getSp().getString("mm_hangye_name", ""), String.class));
//        provinceCode = getGson().fromJson(getSp().getString("mm_emp_provinceId", ""), String.class);
//        cityCode = getGson().fromJson(getSp().getString("mm_emp_cityId", ""), String.class);
//        countryCode = getGson().fromJson(getSp().getString("mm_emp_countryId", ""), String.class);
//        getProvince();
    }

    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        if (group == profile_personal_sex) {
            if (checkedId == R.id.button_one) {
                sex_selected = "0";
            } else if (checkedId == R.id.button_two) {
                sex_selected = "1";
            }
//        }
    }

    //获得省份
//    public void getProvince() {
//        StringRequest request = new StringRequest(
//                Request.Method.POST,
//                InternetURL.GET_PROVINCE_URL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        if (StringUtil.isJson(s)) {
//                            try {
//                                JSONObject jo = new JSONObject(s);
//                                String code1 = jo.getString("code");
//                                if (Integer.parseInt(code1) == 200) {
//                                    provinceNames.add(res.getString(R.string.select_province));
//                                    ProvinceData data = getGson().fromJson(s, ProvinceData.class);
//                                    provinces = data.getData();
//                                    if (provinces != null) {
//                                        for (int i = 0; i < provinces.size(); i++) {
//                                            provinceNames.add(provinces.get(i).getProvince());
//                                        }
//                                    }
//                                    ProvinceAdapter.notifyDataSetChanged();
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            Toast.makeText(EditEmpActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                        }
//                        if (progressDialog != null) {
//                            progressDialog.dismiss();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        if (progressDialog != null) {
//                            progressDialog.dismiss();
//                        }
//                        Toast.makeText(EditEmpActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("is_use", "1");
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
//        getRequestQueue().add(request);
//    }
//
//    //获得城市
//    public void getCitys() {
//        StringRequest request = new StringRequest(
//                Request.Method.POST,
//                InternetURL.GET_CITY_URL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        if (StringUtil.isJson(s)) {
//                            try {
//                                JSONObject jo = new JSONObject(s);
//                                String code1 = jo.getString("code");
//                                if (Integer.parseInt(code1) == 200) {
//                                    CityData data = getGson().fromJson(s, CityData.class);
//                                    citys = data.getData();
//                                    if (citys != null) {
//                                        for (int i = 0; i < citys.size(); i++) {
//                                            cityNames.add(citys.get(i).getCity());
//                                        }
//                                    }
//                                    cityAdapter.notifyDataSetChanged();
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            Toast.makeText(EditEmpActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                        }
//                        if (progressDialog != null) {
//                            progressDialog.dismiss();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        if (progressDialog != null) {
//                            progressDialog.dismiss();
//                        }
//                        Toast.makeText(EditEmpActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
////                params.put("access_token", getGson().fromJson(getSp().getString("access_token", ""), String.class));
//                params.put("father", provinceCode);
//                params.put("is_use", "1");
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
//        getRequestQueue().add(request);
//    }
//
//    //获得地区
//    public void getArea() {
//        StringRequest request = new StringRequest(
//                Request.Method.POST,
//                InternetURL.GET_COUNTRY_URL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        if (StringUtil.isJson(s)) {
//                            try {
//                                JSONObject jo = new JSONObject(s);
//                                String code1 = jo.getString("code");
//                                if (Integer.parseInt(code1) == 200) {
//                                    CountrysData data = getGson().fromJson(s, CountrysData.class);
//                                    countrys = data.getData();
//                                    if (countrys != null) {
//                                        for (int i = 0; i < countrys.size(); i++) {
//                                            countrysNames.add(countrys.get(i).getArea());
//                                        }
//                                    }
//                                    countryAdapter.notifyDataSetChanged();
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            Toast.makeText(EditEmpActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                        }
//                        if (progressDialog != null) {
//                            progressDialog.dismiss();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        if (progressDialog != null) {
//                            progressDialog.dismiss();
//                        }
//                        Toast.makeText(EditEmpActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("father", cityCode);
//                params.put("is_use", "1");
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
//        getRequestQueue().add(request);
//    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.sign_in_button:
                if(StringUtil.isNullOrEmpty(nickname.getText().toString().trim())){
                    showMsg(EditEmpActivity.this, "请输入姓名");
                    return;
                }
                if(StringUtil.isNullOrEmpty(email.getText().toString().trim())){
                    showMsg(EditEmpActivity.this, "请输入邮箱");
                    return;
                }
                if(StringUtil.isNullOrEmpty(company.getText().toString().trim())){
                    showMsg(EditEmpActivity.this, "请输入公司");
                    return;
                }
                progressDialog = new CustomProgressDialog(EditEmpActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                edit();
                break;
            case R.id.liner_one:
                //点击选择职业
                Intent selectHyV = new Intent(EditEmpActivity.this, SelectWorkActivity.class);
                startActivity(selectHyV);
                break;
        }
    }


    void edit(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.EDIT_EMP_BY_ID ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code =  jo.getString("code");
                                if(Integer.parseInt(code) == 200) {
                                    showMsg(EditEmpActivity.this, "修改信息成功");
                                    save("mm_emp_nickname", nickname.getText().toString());
                                    save("mm_emp_sex", sex_selected);
                                    save("mm_emp_email", email.getText().toString());
                                    save("mm_emp_qq", qq.getText().toString());
                                    save("mm_emp_weixin", weixin.getText().toString());
                                    save("mm_emp_company", company.getText().toString());
                                    save("mm_emp_techang", techang.getText().toString());
                                    save("mm_emp_xingqu" ,xingqu.getText().toString());
                                    save("mm_emp_detail" ,jianjie.getText().toString());
                                    save("mm_emp_age" ,age.getText().toString());
                                    save("mm_emp_motto" ,mm_emp_motto.getText().toString());
                                    save("mm_emp_native" ,mm_emp_native.getText().toString());
                                    save("mm_hangye_id" , mm_hangye_id);
                                    save("mm_hangye_name" , select_hy.getText().toString());

//                                    save("mm_emp_provinceId" , provinceCode);
//                                    save("mm_emp_cityId" , cityCode);
//                                    save("mm_emp_countryId" , countryCode);
//                                    save("provinceName", provinceName);
//                                    save("cityName", cityName);
//                                    save("areaName", countryName);

                                    //通知性别更换
                                    Intent intent1 = new Intent("update_profile_success");
                                    sendBroadcast(intent1);
                                    finish();
                                }
                                else {
                                    Toast.makeText(EditEmpActivity.this, R.string.reg_msg_three, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(EditEmpActivity.this, R.string.reg_msg_three, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(EditEmpActivity.this, R.string.reg_msg_three, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_emp_id" , getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
                params.put("mm_emp_nickname" , nickname.getText().toString());
                if(!StringUtil.isNullOrEmpty(sex_selected)){
                    params.put("mm_emp_sex" , sex_selected);
                }else {
                    params.put("mm_emp_sex" , "0");
                }

                if(!StringUtil.isNullOrEmpty(email.getText().toString())){
                    params.put("mm_emp_email" , email.getText().toString());
                }
                if(!StringUtil.isNullOrEmpty(qq.getText().toString())){
                    params.put("mm_emp_qq" , qq.getText().toString());
                }
                if(!StringUtil.isNullOrEmpty(weixin.getText().toString())){
                    params.put("mm_emp_weixin" , weixin.getText().toString());
                }
                if(!StringUtil.isNullOrEmpty(company.getText().toString())){
                    params.put("mm_emp_company" , company.getText().toString());
                }
                if(!StringUtil.isNullOrEmpty(techang.getText().toString())){
                    params.put("mm_emp_techang" , techang.getText().toString());
                }
                if(!StringUtil.isNullOrEmpty(xingqu.getText().toString())){
                    params.put("mm_emp_xingqu" , xingqu.getText().toString());
                }
                if(!StringUtil.isNullOrEmpty(jianjie.getText().toString())){
                    params.put("mm_emp_detail" , jianjie.getText().toString());
                } if(!StringUtil.isNullOrEmpty(age.getText().toString())){
                    params.put("mm_emp_age" , age.getText().toString());
                }
                if(!StringUtil.isNullOrEmpty(mm_emp_motto.getText().toString())){
                    params.put("mm_emp_motto" , mm_emp_motto.getText().toString());
                }
                if(!StringUtil.isNullOrEmpty(mm_emp_native.getText().toString())){
                    params.put("mm_emp_native" , mm_emp_native.getText().toString());
                }

                params.put("mm_hangye_id" , mm_hangye_id);

                params.put("mm_emp_provinceId", getGson().fromJson(getSp().getString("mm_emp_provinceId", ""), String.class));
                params.put("mm_emp_cityId", getGson().fromJson(getSp().getString("mm_emp_cityId", ""), String.class));
                params.put("mm_emp_countryId", getGson().fromJson(getSp().getString("mm_emp_countryId", ""), String.class));

//        provinceCode = getGson().fromJson(getSp().getString("mm_emp_provinceId", ""), String.class);
//        cityCode = getGson().fromJson(getSp().getString("mm_emp_cityId", ""), String.class);
//        countryCode = getGson().fromJson(getSp().getString("mm_emp_countryId", ""), String.class);

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

    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Constants.SELECT_HANGYE_TYPE_SUCCESS)){
                record1 = (HangYeType) intent.getExtras().get("hangYeType");
                select_hy.setText(record1.getMm_hangye_name());
                mm_hangye_id = record1.getMm_hangye_id();
            }
        }
    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.SELECT_HANGYE_TYPE_SUCCESS);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    public void onDestroy() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    };

}
