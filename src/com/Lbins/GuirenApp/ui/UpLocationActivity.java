package com.Lbins.GuirenApp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.MainActivity;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.EmpData;
import com.Lbins.GuirenApp.huanxin.DemoHelper;
import com.Lbins.GuirenApp.huanxin.db.DemoDBManager;
import com.Lbins.GuirenApp.module.Emp;
import com.Lbins.GuirenApp.util.StringUtil;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhl on 2016/5/6.
 */
public class UpLocationActivity extends BaseActivity implements AMapLocationListener ,View.OnClickListener{
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    //定位
    private AMapLocationClient mlocationClient = null;

    private TextView lat;
    private TextView location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.up_location_activity);

        lat = (TextView) this.findViewById(R.id.lat);
        location = (TextView) this.findViewById(R.id.location);
        this.findViewById(R.id.btn_sure).setOnClickListener(this);
        this.findViewById(R.id.back).setOnClickListener(this);

        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(20000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();

    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                GuirenApplication.latStr = String.valueOf(amapLocation.getLatitude());
                GuirenApplication.lngStr = String.valueOf(amapLocation.getLongitude());
                GuirenApplication.locationAddress = amapLocation.getAddress();
                lat.setText(String.valueOf(amapLocation.getLatitude()) +"," + String.valueOf(amapLocation.getLongitude()));
                location.setText(amapLocation.getAddress());
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    void sendLocation() {
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class))){
            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    InternetURL.SEND_LOCATION_BYID_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            if (StringUtil.isJson(s)) {
                                try {
                                    JSONObject jo = new JSONObject(s);
                                    String code = jo.getString("code");
                                    if (Integer.parseInt(code) == 200) {
                                        showMsg(UpLocationActivity.this, "上传成功！");
                                        finish();
                                    } else {
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
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
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lat", (GuirenApplication.latStr == null ? "" : GuirenApplication.latStr));
                    params.put("lng", (GuirenApplication.lngStr == null ? "" : GuirenApplication.lngStr));
                    if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class))){
                        params.put("mm_emp_id", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_sure:
                if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class))){
                    sendLocation();
                }else {
                    showMsg(UpLocationActivity.this, "上传失败！请稍后重试！");
                }
                break;
            case R.id.back:
                finish();
                break;
        }
    }
}
