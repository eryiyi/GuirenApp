package com.Lbins.GuirenApp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.Publish_mood_GridView_Adapter;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.CityData;
import com.Lbins.GuirenApp.data.CountrysData;
import com.Lbins.GuirenApp.data.ProvinceData;
import com.Lbins.GuirenApp.data.RecordSingleDATA;
import com.Lbins.GuirenApp.module.*;
import com.Lbins.GuirenApp.util.*;
import com.Lbins.GuirenApp.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/12/20.
 */
public class AddFuwuActivity extends BaseActivity implements View.OnClickListener {

    private TextView txt_help_type;
    private EditText help_title;
    private EditText help_content;
    private EditText help_money;
    
    private TextView help_danwei_id;

    private NoScrollGridView gridView;
    private Publish_mood_GridView_Adapter adapter;
    private ArrayList<String> dataList = new ArrayList<String>();
    private ArrayList<String> tDataList = new ArrayList<String>();
    private List<String> uploadPaths = new ArrayList<String>();

    private static int REQUEST_CODE = 1;

    private Uri uri;

    private SelectPhoTwoPopWindow deleteWindow;
    AsyncHttpClient client = new AsyncHttpClient();
    //省市县
    private CustomerSpinner province1;
    private CustomerSpinner city1;
    private CustomerSpinner country1;
    private List<ProvinceObj> provinces1 = new ArrayList<ProvinceObj>();//省
    private ArrayList<String> provinceNames1 = new ArrayList<String>();//省份名称
    private List<CityObj> citys1 = new ArrayList<CityObj>();//市
    private ArrayList<String> cityNames1 = new ArrayList<String>();//市名称
    private List<CountryObj> countrys1 = new ArrayList<CountryObj>();//区
    private ArrayList<String> countrysNames1 = new ArrayList<String>();//区名称
    ArrayAdapter<String> ProvinceAdapter1;
    ArrayAdapter<String> cityAdapter1;
    ArrayAdapter<String> countryAdapter1;
    private String provinceName1 = "";
    private String cityName1 = "";
    private String countryName1 = "";
    private String provinceCode1 = "";
    private String cityCode1 = "";
    private String countryCode1 = "";

    private Resources res;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_fuwu_activity);
        res = getResources();
        dataList.add("camera_default");
        initView();
        getProvince1();
    }

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.liner_help_danwei_id).setOnClickListener(this);
        this.findViewById(R.id.liner_help_type).setOnClickListener(this);
        txt_help_type = (TextView) this.findViewById(R.id.txt_help_type);
        help_title = (EditText) this.findViewById(R.id.help_title);
        help_danwei_id = (TextView) this.findViewById(R.id.help_danwei_id);
   
        help_content = (EditText) this.findViewById(R.id.help_content);
        help_money = (EditText) this.findViewById(R.id.help_money);
        gridView = (NoScrollGridView) this.findViewById(R.id.gridView);
        adapter = new Publish_mood_GridView_Adapter(this, dataList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new GridView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String path = dataList.get(position);
                if (path.equals("camera_default")) {
                    showSelectImageDialog();
                } else {
                    Intent intent = new Intent(AddFuwuActivity.this, ImageDelActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("path", dataList.get(position));
                    startActivityForResult(intent, CommonDefine.DELETE_IMAGE);
                }
            }
        });

        ProvinceAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, provinceNames1);
        ProvinceAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        province1 = (CustomerSpinner) findViewById(R.id.mm_emp_provinceId);
        province1.setAdapter(ProvinceAdapter1);
        province1.setList(provinceNames1);
        province1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                citys1.clear();
                cityNames1.clear();
                cityNames1.add(getResources().getString(R.string.select_city));
                cityAdapter1.notifyDataSetChanged();
                ProvinceObj province = null;
                if (provinces1 != null && position > 0) {
                    province = provinces1.get(position - 1);
                    provinceName1 = province.getProvince();
                    provinceCode1 = province.getProvinceID();
                } else if (provinces1 != null) {
                    province = provinces1.get(position);
                    provinceName1 = province.getProvince();
                    provinceCode1 = province.getProvinceID();
                }
                try {
                    getCitys1();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        cityAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cityNames1);
        cityAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city1 = (CustomerSpinner) findViewById(R.id.mm_emp_cityId);
        city1.setAdapter(cityAdapter1);
        city1.setList(cityNames1);
        city1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    countrys1.clear();
                    countrysNames1.clear();
                    countrysNames1.add(getResources().getString(R.string.select_area));
                    CityObj city = citys1.get(position - 1);
                    cityName1 = city.getCity();
                    cityCode1 = city.getCityID();
                    try {
                        getArea1();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    country1.setEnabled(true);
                    countrysNames1.clear();
                    countrysNames1.add(res.getString(R.string.select_area));
                    countrys1.clear();
                    countryAdapter1.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        countryAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countrysNames1);
        countryAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        country1 = (CustomerSpinner) findViewById(R.id.mm_emp_countryId);
        country1.setAdapter(countryAdapter1);
        country1.setList(countrysNames1);
        country1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    CountryObj country = countrys1.get(position - 1);
                    countryCode1 = country.getAreaID();
                    countryName1 = country.getArea();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //获得省份
    public void getProvince1() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_PROVINCE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code1 = jo.getString("code");
                                if (Integer.parseInt(code1) == 200) {
                                    provinceNames1.add(res.getString(R.string.select_province));
                                    ProvinceData data = getGson().fromJson(s, ProvinceData.class);
                                    provinces1 = data.getData();
                                    if (provinces1 != null) {
                                        for (int i = 0; i < provinces1.size(); i++) {
                                            provinceNames1.add(provinces1.get(i).getProvince());
                                        }
                                    }
                                    ProvinceAdapter1.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(AddFuwuActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddFuwuActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("is_use", "1");
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

    //获得城市
    public void getCitys1() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_CITY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code1 = jo.getString("code");
                                if (Integer.parseInt(code1) == 200) {
                                    CityData data = getGson().fromJson(s, CityData.class);
                                    citys1 = data.getData();
                                    if (citys1 != null) {
                                        for (int i = 0; i < citys1.size(); i++) {
                                            cityNames1.add(citys1.get(i).getCity());
                                        }
                                    }
                                    cityAdapter1.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(AddFuwuActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddFuwuActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("father", provinceCode1);
                params.put("is_use", "1");
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

    //获得地区
    public void getArea1() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_COUNTRY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code1 = jo.getString("code");
                                if (Integer.parseInt(code1) == 200) {
                                    CountrysData data = getGson().fromJson(s, CountrysData.class);
                                    countrys1 = data.getData();
                                    if (countrys1 != null) {
                                        for (int i = 0; i < countrys1.size(); i++) {
                                            countrysNames1.add(countrys1.get(i).getArea());
                                        }
                                    }
                                    countryAdapter1.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(AddFuwuActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddFuwuActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("father", cityCode1);
                params.put("is_use", "1");
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
            case R.id.liner_help_type:
            {
                Intent intent = new Intent(AddFuwuActivity.this, SearchMoreClassActivity.class);
                intent.putExtra("is_type", "0");
                startActivityForResult(intent, 1000);
            }
                break;
            case R.id.liner_help_danwei_id:
            {
                Intent intent = new Intent(AddFuwuActivity.this, SelecthelpDanweiActivity.class);
                startActivityForResult(intent, 1001);
            }
                break;
            case R.id.back:
            {
                if (!TextUtils.isEmpty(help_title.getText().toString().trim())|| dataList.size()!=0 || !TextUtils.isEmpty(help_content.getText().toString().trim())|| !TextUtils.isEmpty(help_money.getText().toString().trim())) {
                    showSelectPublishDialog();
                } else {
                    finish();
                }
            }
                break;
        }
    }

    private HelpType helpType;//选中的类别
    private HelpDanwei helpDanwei;
    private PublishPopWindow publishPopWindow;

    // 选择是否退出发布
    private void showSelectPublishDialog() {
        publishPopWindow = new PublishPopWindow(AddFuwuActivity.this, itemOnClick);
        publishPopWindow.showAtLocation(AddFuwuActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            publishPopWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_sure:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            if (!TextUtils.isEmpty(help_title.getText().toString().trim())|| dataList.size()!=0 || !TextUtils.isEmpty(help_content.getText().toString().trim())|| !TextUtils.isEmpty(help_money.getText().toString().trim())) {
                showSelectPublishDialog();
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    // 选择相册，相机
    private void showSelectImageDialog() {
        deleteWindow = new SelectPhoTwoPopWindow(AddFuwuActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(AddFuwuActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private ArrayList<String> getIntentArrayList(ArrayList<String> dataList) {

        ArrayList<String> tDataList = new ArrayList<String>();

        for (String s : dataList) {
//            if (!s.contains("camera_default")) {
            tDataList.add(s);
//            }
        }
        return tDataList;
    }

    private final static int SELECT_LOCAL_PHOTO = 110;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SELECT_LOCAL_PHOTO:
                    tDataList = data.getStringArrayListExtra("datalist");
                    if (tDataList != null) {
                        for (int i = 0; i < tDataList.size(); i++) {
                            String string = tDataList.get(i);
                            dataList.add(string);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        finish();
                    }
                    break;
                case CommonDefine.TAKE_PICTURE_FROM_CAMERA:
                    String sdStatus = Environment.getExternalStorageState();
                    if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                        return;
                    }
                    Bitmap bitmap = ImageUtils.getUriBitmap(this, uri, 400, 400);
                    String cameraImagePath = FileUtils.saveBitToSD(bitmap, System.currentTimeMillis() + ".jpg");

                    dataList.add(cameraImagePath);
                    adapter.notifyDataSetChanged();
                    break;
                case CommonDefine.TAKE_PICTURE_FROM_GALLERY:
                    tDataList = data.getStringArrayListExtra("datalist");
                    if (tDataList != null) {
                        dataList.clear();
                        for (int i = 0; i < tDataList.size(); i++) {
                            String string = tDataList.get(i);
                            dataList.add(string);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case CommonDefine.DELETE_IMAGE:
                    int position = data.getIntExtra("position", -1);
                    dataList.remove(position);
                    adapter.notifyDataSetChanged();
                    break;
                case 1000:
                {
                    helpType = (HelpType) data.getExtras().get("helpType");
                    if(helpType != null){
                        txt_help_type.setText(helpType.getHelp_type_name());
                    }
                }
                break;
                case 1001:
                {
                    helpDanwei = (HelpDanwei) data.getExtras().get("helpDanwei");
                    if(helpDanwei != null){
                        help_danwei_id.setText(helpDanwei.getHelp_danwei_name());
                    }
                }
                break;
            }
        }
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            deleteWindow.dismiss();
            switch (v.getId()) {
                case R.id.camera: {
                    Intent cameraIntent = new Intent();
                    cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    // 根据文件地址创建文件
                    File file = new File(CommonDefine.FILE_PATH);
                    if (file.exists()) {
                        file.delete();
                    }
                    uri = Uri.fromFile(file);
                    // 设置系统相机拍摄照片完成后图片文件的存放地址
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    // 开启系统拍照的Activity
                    startActivityForResult(cameraIntent, CommonDefine.TAKE_PICTURE_FROM_CAMERA);
                }
                break;
                case R.id.mapstorage: {
                    Intent intent = new Intent(AddFuwuActivity.this, AlbumActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("dataList", getIntentArrayList(dataList));
                    bundle.putString("editContent", help_title.getText().toString());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, CommonDefine.TAKE_PICTURE_FROM_GALLERY);
                }
                break;
                default:
                    break;
            }
        }

    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void saveAction(View view){
        //保存信息
        if(StringUtil.isNullOrEmpty(help_title.getText().toString())){
            showMsg(AddFuwuActivity.this, "请输入标题！");
            return;
        }
        if(StringUtil.isNullOrEmpty(help_content.getText().toString())){
            showMsg(AddFuwuActivity.this, "请输入内容！");
            return;
        }
        if(StringUtil.isNullOrEmpty(help_money.getText().toString())){
            showMsg(AddFuwuActivity.this, "请输入赏金！");
            return;
        }
        if(StringUtil.isNullOrEmpty(provinceCode1) || StringUtil.isNullOrEmpty(cityCode1) || StringUtil.isNullOrEmpty(countryCode1)){
            showMsg(AddFuwuActivity.this, "请选择地址！");
            return;
        }
        if(helpType == null){
            showMsg(AddFuwuActivity.this, "请选择求助类型！");
            return;
        }
        //检查有没有选择图片
        if (dataList.size() == 1) {
            showMsg(AddFuwuActivity.this, "请选择一张图片！");
            return;
        }
        uploadPaths.clear();
        progressDialog = new CustomProgressDialog(AddFuwuActivity.this, "正在加载中",R.anim.custom_dialog_frame);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        //检查有没有选择图片
        for (int i = 1; i < dataList.size(); i++) {
            //七牛
            Bitmap bm = FileUtils.getSmallBitmap(dataList.get(i));
            final String cameraImagePath = FileUtils.saveBitToSD(bm, System.currentTimeMillis() + ".jpg");
            Map<String,String> map = new HashMap<String, String>();
            map.put("space", InternetURL.QINIU_SPACE);
            RequestParams params = new RequestParams(map);
            client.get(InternetURL.UPLOAD_TOKEN ,params, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        String token = response.getString("data");
                        UploadManager uploadManager = new UploadManager();
                        uploadManager.put(StringUtil.getBytes(cameraImagePath), StringUtil.getUUID(), token,
                                new UpCompletionHandler() {
                                    @Override
                                    public void complete(String key, ResponseInfo info, JSONObject response) {
                                        //key
                                        uploadPaths.add(key);
                                        if (uploadPaths.size() == (dataList.size()-1)) {
                                            publishAll();
                                        }
                                    }
                                }, null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
        }
    }

    //上传完图片后开始发布
    private void publishAll() {
        final StringBuffer filePath = new StringBuffer();
        for (int i = 0; i < uploadPaths.size(); i++) {
            filePath.append(uploadPaths.get(i));
            if (i != uploadPaths.size() - 1) {
                filePath.append(",");
            }
        }
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SAVE_HELP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            RecordSingleDATA data = getGson().fromJson(s, RecordSingleDATA.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                Toast.makeText(AddFuwuActivity.this, R.string.publish_success, Toast.LENGTH_SHORT).show();
                                //调用广播，刷新主页
                                Intent intent1 = new Intent(Constants.SEND_INDEX_SUCCESS);
                                intent1.putExtra("addRecord", data.getData());
                                sendBroadcast(intent1);
                                finish();
                            }
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
                        Toast.makeText(AddFuwuActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("help_pic", String.valueOf(filePath));
                params.put("mm_emp_id", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
                params.put("help_title", help_title.getText().toString());
                params.put("help_content", help_title.getText().toString());
                params.put("help_content", help_content.getText().toString());
                if(StringUtil.isNullOrEmpty(GuirenApplication.latStr)){
                    params.put("lat",GuirenApplication.latStr );
                }
                if(StringUtil.isNullOrEmpty(GuirenApplication.lngStr)){
                    params.put("lng",GuirenApplication.lngStr );
                }

                params.put("provinceID", provinceCode1);
                params.put("cityID", cityCode1);
                params.put("areaID", countryCode1);
                
                params.put("help_money", help_money.getText().toString());
                params.put("help_danwei_id", "");//单位 服务用得到
                if(helpType != null){
                    params.put("help_type_id", helpType.getHelp_type_id());
                }
                params.put("help_type", "1");

                if(helpDanwei != null){
                    params.put("help_danwei_id", helpDanwei.getHelp_danwei_id());
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
