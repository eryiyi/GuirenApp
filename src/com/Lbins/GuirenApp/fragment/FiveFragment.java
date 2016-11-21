package com.Lbins.GuirenApp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.AnimateFirstDisplayListener;
import com.Lbins.GuirenApp.adapter.ItemPicAdapter;
import com.Lbins.GuirenApp.base.BaseFragment;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.dao.DBHelper;
import com.Lbins.GuirenApp.data.MinePicObjData;
import com.Lbins.GuirenApp.module.MinePicObj;
import com.Lbins.GuirenApp.ui.*;
import com.Lbins.GuirenApp.util.*;
import com.Lbins.GuirenApp.widget.PictureGridview;
import com.Lbins.GuirenApp.widget.SelectPhoPopWindow;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.easemob.redpacketui.utils.RedPacketUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
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
 * Created by zhl on 2016/5/6.
 */
public class FiveFragment extends BaseFragment implements View.OnClickListener {
    private View view;
    private Resources res;

    private ImageView head;
    private TextView nickname;
    private TextView mobile;
    private TextView hangyename;
    private TextView email;
    private TextView company;
    private TextView aihao;
    private TextView address;
    private TextView qq;
    private TextView weixin;
    private TextView age;
    private TextView jianjie;
    private TextView techang;
    private TextView mm_emp_motto;
    private TextView mm_emp_native;
    private ImageView sex;


    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private String pics = "";
    private static final File PHOTO_CACHE_DIR = new File(Environment.getExternalStorageDirectory() + "/liangxun/PhotoCache");

    private SelectPhoPopWindow deleteWindow;
    AsyncHttpClient client = new AsyncHttpClient();

    private LinearLayout img_bg;//背景图

    private PictureGridview gridView;
    private List<MinePicObj> picLists = new ArrayList<MinePicObj>();
    private ItemPicAdapter adapterGrid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.five_fragment, null);
        res = getActivity().getResources();
        view.findViewById(R.id.edit).setOnClickListener(this);
        head = (ImageView) view.findViewById(R.id.head);
        img_bg = (LinearLayout) view.findViewById(R.id.img_bg);
        nickname = (TextView) view.findViewById(R.id.nickname);
        mm_emp_motto = (TextView) view.findViewById(R.id.mm_emp_motto);
        mm_emp_native = (TextView) view.findViewById(R.id.mm_emp_native);
        mobile = (TextView) view.findViewById(R.id.mobile);
        hangyename = (TextView) view.findViewById(R.id.hangyename);
        email = (TextView) view.findViewById(R.id.email);
        company = (TextView) view.findViewById(R.id.company);
        aihao = (TextView) view.findViewById(R.id.aihao);
        address = (TextView) view.findViewById(R.id.address);
        qq = (TextView) view.findViewById(R.id.qq);
        weixin = (TextView) view.findViewById(R.id.weixin);
        age = (TextView) view.findViewById(R.id.age);
        jianjie = (TextView) view.findViewById(R.id.jianjie);
        techang = (TextView) view.findViewById(R.id.techang);
        sex = (ImageView) view.findViewById(R.id.sex);
        view.findViewById(R.id.liner_one).setOnClickListener(this);
        view.findViewById(R.id.liner_two).setOnClickListener(this);
        view.findViewById(R.id.liner_three).setOnClickListener(this);
        view.findViewById(R.id.liner_four).setOnClickListener(this);
        view.findViewById(R.id.liner_five).setOnClickListener(this);
        view.findViewById(R.id.liner_six).setOnClickListener(this);
        view.findViewById(R.id.liner_money).setOnClickListener(this);

        head.setOnClickListener(this);
        nickname.setOnClickListener(this);

        gridView = (PictureGridview) view.findViewById(R.id.gridview);
        adapterGrid = new ItemPicAdapter(picLists, getActivity());
        gridView.setAdapter(adapterGrid);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //判断是否有网
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
                    if (!isMobileNet && !isWifiNet) {
                        Toast.makeText(getActivity(), "请检查网络链接", Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent = new Intent(getActivity(), MinePhotoActivity.class);
                        intent.putExtra("mm_emp_id", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        initDataMine();
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
            if (!isMobileNet && !isWifiNet) {
                if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class))){
                    List<MinePicObj> minePicObjs = DBHelper.getInstance(getActivity()).getPicsListByEmpId(getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
                    if(minePicObjs != null){
                        picLists.clear();
                        picLists.addAll(minePicObjs);
                        adapterGrid.notifyDataSetChanged();
                    }
                }
            }else {
                getPics();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    void initDataMine(){
        //赋值
        imageLoader.displayImage(getGson().fromJson(getSp().getString("mm_emp_cover", ""), String.class), head, GuirenApplication.txOptions, animateFirstListener);
        nickname.setText(getGson().fromJson(getSp().getString("mm_emp_nickname", ""), String.class));
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_hangye_name", ""), String.class))){
            hangyename.setText(getGson().fromJson(getSp().getString("mm_hangye_name", ""), String.class));
        }else {
            hangyename.setText("暂未填写");
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_email", ""), String.class))){
            email.setText(getGson().fromJson(getSp().getString("mm_emp_email", ""), String.class));
        }else {
            email.setText("暂未填写");
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_company", ""), String.class))){
            company.setText(getGson().fromJson(getSp().getString("mm_emp_company", ""), String.class));
        }else {
            company.setText("暂未填写");
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_xingqu", ""), String.class))){
            aihao.setText(getGson().fromJson(getSp().getString("mm_emp_xingqu", ""), String.class));
        }else {
            aihao.setText("暂未填写");
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("provinceName", ""), String.class)) || !StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("cityName", ""), String.class)) || !StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("areaName", ""), String.class))){
            address.setText(getGson().fromJson(getSp().getString("provinceName", ""), String.class)+getGson().fromJson(getSp().getString("cityName", ""), String.class)+getGson().fromJson(getSp().getString("areaName", ""), String.class));
        }else {
            address.setText("暂未填写");
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_qq", ""), String.class))){
            qq.setText(getGson().fromJson(getSp().getString("mm_emp_qq", ""), String.class));
        }else {
            qq.setText("暂未填写");
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_weixin", ""), String.class))){
            weixin.setText(getGson().fromJson(getSp().getString("mm_emp_weixin", ""), String.class));
        }else {
            weixin.setText("暂未填写");
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_age", ""), String.class))){
            age.setText(getGson().fromJson(getSp().getString("mm_emp_age", ""), String.class));
        }else {
            age.setText("暂未填写");
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_detail", ""), String.class))){
            jianjie.setText(getGson().fromJson(getSp().getString("mm_emp_detail", ""), String.class));
        }else {
            jianjie.setText("暂未填写");
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_techang", ""), String.class))){
            techang.setText(getGson().fromJson(getSp().getString("mm_emp_techang", ""), String.class));
        }else {
            techang.setText("暂未填写");
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_sex", ""), String.class))){
            if("1".equals(getGson().fromJson(getSp().getString("mm_emp_sex", ""), String.class))){
                sex.setImageDrawable(getResources().getDrawable(R.drawable.icon_sex_female));
            }else {
                sex.setImageDrawable(getResources().getDrawable(R.drawable.icon_sex_male));
            }
        }

        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_motto", ""), String.class))){
            mm_emp_motto.setText("签名："+getGson().fromJson(getSp().getString("mm_emp_motto", ""), String.class));
        }else {
            mm_emp_motto.setText("暂无签名信息");
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_native", ""), String.class))){
            mm_emp_native.setText(getGson().fromJson(getSp().getString("mm_emp_native", ""), String.class));
        }else {
            mm_emp_native.setText("暂未填写");
        }

        mobile.setText(getGson().fromJson(getSp().getString("mm_emp_mobile", ""), String.class));


        final ImageView imageView = new ImageView(getActivity());
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_bg", ""), String.class))){
            imageLoader.displayImage(getGson().fromJson(getSp().getString("mm_emp_bg", ""), String.class), imageView, GuirenApplication.options, new AnimateFirstDisplayListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    if(imageView != null && imageView.getDrawable() != null && img_bg != null){
                        img_bg.setBackground(imageView.getDrawable());
                    }
                }
            });
        }else {
            img_bg.setBackground(res.getDrawable(R.drawable.bg_one));
        }
    }

    boolean isMobileNet, isWifiNet;
    private String typeCover;
    @Override
    public void onClick(View view) {
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
            if (!isMobileNet && !isWifiNet) {
                Toast.makeText(getActivity(), "请检查网络链接", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       switch (view.getId()){
           case R.id.edit:
           {
//               Intent intent = new Intent(getActivity(), EditEmpActivity.class);
//               startActivity(intent);
               RedPacketUtil.startChangeActivity(getActivity());
           }
               break;
           case R.id.liner_one:
               //相册
           {
               Intent intent = new Intent(getActivity(), MinePhotoActivity.class);
               intent.putExtra("mm_emp_id", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
               startActivity(intent);
           }
               break;
           case R.id.liner_two:
               //主页背景
           {
               typeCover = "0";
               Intent mapstorage = new Intent(Intent.ACTION_PICK, null);
               mapstorage.setDataAndType(
                       MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                       "image/*");
               startActivityForResult(mapstorage, 4);
           }
               break;
           case R.id.liner_three:
               //众筹
               Intent zhongchouV = new Intent(getActivity(), ZhongChouMakeActivity.class);
               startActivity(zhongchouV);
               break;
           case R.id.liner_four:
               //邀请码
               Intent yaoqingCardV = new Intent(getActivity(), MineYqCardActivity.class);
               startActivity(yaoqingCardV);
               break;
           case R.id.liner_five:
           {
               //我的自媒体
               if("0".equals(getGson().fromJson(getSp().getString("mm_emp_type", ""), String.class))){
                   //没有open
                   Intent intent = new Intent(getActivity(),OpenZimeitiActivity.class);
                   startActivity(intent);
               }else{
                   //查看自己的自媒体
                   Intent intentzmt = new Intent(getActivity(), ProfileZmtActivity.class);
                   intentzmt.putExtra("mm_emp_id", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
                   startActivity(intentzmt);
               }
           }
               break;
           case R.id.liner_six:
               //设置
           {
               Intent setV = new Intent(getActivity(), SettingActivity.class);
               startActivity(setV);
           }
               break;
           case R.id.head:
               //头像
               typeCover ="1";
               ShowPickDialog();
               break;
           case R.id.nickname:
               break;
           case R.id.liner_money:
           {
               //我的零钱
               RedPacketUtil.startChangeActivity(getActivity());
           }
               break;
       }
    }

    // 选择相册，相机
    private void ShowPickDialog() {
        deleteWindow = new SelectPhoPopWindow(getActivity(), itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(getActivity().findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            deleteWindow.dismiss();
            switch (v.getId()) {
                case R.id.camera: {
                    Intent camera = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    //下面这句指定调用相机拍照后的照片存储的路径
                    camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                            .fromFile(new File(Environment
                                    .getExternalStorageDirectory(),
                                    "ppCover.jpg")));
                    startActivityForResult(camera, 2);
                }
                break;
                case R.id.mapstorage: {
                    Intent mapstorage = new Intent(Intent.ACTION_PICK, null);
                    mapstorage.setDataAndType(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "image/*");
                    startActivityForResult(mapstorage, 1);
                }
                break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // 如果是直接从相册获取
            case 1:
                if (data != null) {
                    startPhotoZoom(data.getData());
                }
                break;
            // 如果是调用相机拍照时
            case 2:
                File temp = new File(Environment.getExternalStorageDirectory()
                        + "/ppCover.jpg");
                startPhotoZoom(Uri.fromFile(temp));
                break;
            // 取得裁剪后的图片
            case 3:
                if (data != null) {
                    setPicToView(data);
                }
                break;
            case 4:
            {
                //背景图更换
                if (data != null) {
                    Uri uri = data.getData();
                    startPhotoZoomBg(uri);
                }
            }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoomBg(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 2);
        intent.putExtra("aspectY", 3);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            if (photo != null) {
                pics = CompressPhotoUtil.saveBitmap2file(photo, System.currentTimeMillis() + ".jpg", PHOTO_CACHE_DIR);
                if("1".equals(typeCover)){
                    head.setImageBitmap(photo);
                    //上传图片到七牛
                    uploadCover();
                }
                if("0".equals(typeCover)){
                    img_bg.setBackground(drawable);
                    //上传图片到七牛
                    uploadCover();
                }
            }
        }
    }

    //上传到七牛云存贮
    void uploadCover(){
        Map<String,String> map = new HashMap<String,String>();
        map.put("space", InternetURL.QINIU_SPACE);
        RequestParams params = new RequestParams(map);
        client.get(InternetURL.UPLOAD_TOKEN, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    String token = response.getString("data");
                    UploadManager uploadManager = new UploadManager();
                    uploadManager.put(StringUtil.getBytes(pics), StringUtil.getUUID(), token,
                            new UpCompletionHandler() {
                                @Override
                                public void complete(String key, ResponseInfo info, JSONObject response) {
                                    publishAll(key);
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
    //更新数据库用户头像

    //更新
    private void publishAll(final String uploadpic) {
        String uri = "";
        if("0".equals(typeCover)){
            uri = InternetURL.UPDATE_BG_URL;
        }
        if("1".equals(typeCover)){
            uri = InternetURL.UPDATE_COVER;
        }
        StringRequest request = new StringRequest(
                Request.Method.POST,
                uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                            JSONObject jo = new JSONObject(s);
                            String code = jo.getString("code");
                            if(Integer.parseInt(code) == 200) {
                                if("0".equals(typeCover)){
                                    save("mm_emp_bg", InternetURL.QINIU_URL + uploadpic);
                                }
                                if("1".equals(typeCover)){
                                    save("mm_emp_cover", InternetURL.QINIU_URL + uploadpic);
                                }

                            }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_emp_id", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
                if("0".equals(typeCover)){
                    params.put("mm_emp_bg", uploadpic);
                }
                if("1".equals(typeCover)){
                    params.put("mm_emp_cover", uploadpic);
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



    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("update_profile_success")){
                initDataMine();
            }
        }

    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("update_profile_success");
        //注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       getActivity(). unregisterReceiver(mBroadcastReceiver);
    }

    //获得会员相册图片
    private void getPics() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.RECORD_PICS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            MinePicObjData data = getGson().fromJson(s, MinePicObjData.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                picLists.clear();
                                picLists.addAll(data.getData());
                                adapterGrid.notifyDataSetChanged();
                                //处理数据，需要的话保存到数据库
                                if (data != null && data.getData() != null) {
                                    DBHelper dbHelper = DBHelper.getInstance(getActivity());
                                    for (MinePicObj minePicObj : data.getData()) {
                                        if (dbHelper.getMinePicObjById(minePicObj.getPicStr()) != null) {
                                            //已经存在了 不需要插入了
                                        } else {
                                            DBHelper.getInstance(getActivity()).saveMinePicObj(minePicObj);
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
                params.put("page", "1");
                params.put("mm_emp_id", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
                params.put("mm_msg_type",  Constants.RECORD_TYPE);
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
