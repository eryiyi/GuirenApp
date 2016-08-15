package com.Lbins.GuirenApp.ui;

import android.app.Dialog;
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
import android.widget.*;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.AnimateFirstDisplayListener;
import com.Lbins.GuirenApp.adapter.OnClickContentItemListener;
import com.Lbins.GuirenApp.adapter.RecordAdapter;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.EmpData;
import com.Lbins.GuirenApp.data.EmpRelateObjData;
import com.Lbins.GuirenApp.data.RecordDATA;
import com.Lbins.GuirenApp.data.SuccessData;
import com.Lbins.GuirenApp.huanxin.ui.ChatActivity;
import com.Lbins.GuirenApp.library.PullToRefreshBase;
import com.Lbins.GuirenApp.library.PullToRefreshListView;
import com.Lbins.GuirenApp.module.Emp;
import com.Lbins.GuirenApp.module.EmpRelateObj;
import com.Lbins.GuirenApp.module.Record;
import com.Lbins.GuirenApp.util.Constants;
import com.Lbins.GuirenApp.util.StringUtil;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/5/28.
 */
public class ProfileActivity extends BaseActivity implements View.OnClickListener ,OnClickContentItemListener {
    private String mm_emp_id;
    private Resources res;
    //    RECORD_URL
    private PullToRefreshListView lstv;
    private RecordAdapter adapter;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private List<Record> recordList = new ArrayList<Record>();
    Record recordtmp;//转换用

    private ImageView head;
    private ImageView sex;
    private TextView nickname;
    private TextView hangyename;

    private TextView email;
    private TextView qq;
    private TextView weixin;
    private TextView age;
    private TextView company;
    private TextView techang;
    private TextView xingqu;
    private TextView address;
    private TextView jianjie;
    private TextView mm_emp_motto;
    private TextView mm_emp_native;


    private TextView btn_baijian;
    private TextView btn_chat;
    private LinearLayout headLiner;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private LinearLayout liner_bottom;
    private TextView guirenwang;

    Emp emp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        registerBoradcastReceiver();
        mm_emp_id = getIntent().getExtras().getString("mm_emp_id");
        res = this.getResources();
        this.findViewById(R.id.back).setOnClickListener(this);
        initView();
        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        initData();
        getData();

    }

    void initView(){
        headLiner = (LinearLayout) LayoutInflater.from(ProfileActivity.this).inflate(R.layout.profile_header, null);
        head = (ImageView) headLiner.findViewById(R.id.head);
        sex = (ImageView) headLiner.findViewById(R.id.sex);
        nickname = (TextView) headLiner.findViewById(R.id.nickname);
        guirenwang = (TextView) headLiner.findViewById(R.id.guirenwang);
        mm_emp_native = (TextView) headLiner.findViewById(R.id.mm_emp_native);
        mm_emp_motto = (TextView) headLiner.findViewById(R.id.mm_emp_motto);
        hangyename = (TextView) headLiner.findViewById(R.id.hangyename);
        email = (TextView) headLiner.findViewById(R.id.email);
        qq = (TextView) headLiner.findViewById(R.id.qq);
        weixin = (TextView) headLiner.findViewById(R.id.weixin);
        age = (TextView) headLiner.findViewById(R.id.age);
        address = (TextView) headLiner.findViewById(R.id.address);
        company = (TextView) headLiner.findViewById(R.id.company);
        techang = (TextView) headLiner.findViewById(R.id.techang);
        xingqu = (TextView) headLiner.findViewById(R.id.aihao);
        jianjie = (TextView) headLiner.findViewById(R.id.jianjie);
        headLiner.findViewById(R.id.guirenwang).setOnClickListener(this);
        headLiner.findViewById(R.id.guirenlu).setOnClickListener(this);
        liner_bottom = (LinearLayout) this.findViewById(R.id.liner_bottom);
        btn_baijian = (TextView) this.findViewById(R.id.btn_baijian);
        btn_chat = (TextView) this.findViewById(R.id.btn_chat);
        btn_baijian.setOnClickListener(this);
        btn_chat.setOnClickListener(this);

        head.setOnClickListener(this);

        lstv = (PullToRefreshListView) this.findViewById(R.id.lstv);
        ListView listView = lstv.getRefreshableView();

        listView.addHeaderView(headLiner);

        adapter = new RecordAdapter(recordList, ProfileActivity.this, getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
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
        lstv.setAdapter(adapter);
        adapter.setOnClickContentItemListener(this);
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position >1){
                    Record record = recordList.get(position - 2);
                    if(record != null){
                        //          if (!record.getMm_msg_type().equals("1")) {
                        Intent detail = new Intent(ProfileActivity.this, DetailPageAcitvity.class);
                        detail.putExtra(Constants.INFO, record);
                        startActivity(detail);
//                    }
                    }
                }

            }
        });
    }

    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.RECORD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            RecordDATA data = getGson().fromJson(s, RecordDATA.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                if (IS_REFRESH) {
                                    recordList.clear();
                                }
                                recordList.addAll(data.getData());
                                lstv.onRefreshComplete();
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(ProfileActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ProfileActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", String.valueOf(pageIndex));
                params.put("mm_emp_id", mm_emp_id);
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
    Record record;



    private void getData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_MEMBER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            EmpData data = getGson().fromJson(s, EmpData.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                emp = data.getData();
                                //查询两者之间的关系
                                getRelate();
                                imageLoader.displayImage(emp.getMm_emp_cover(), head, GuirenApplication.txOptions, animateFirstListener);
                                nickname.setText(emp.getMm_emp_nickname());
                                hangyename.setText(emp.getMm_hangye_name());
                                if(!StringUtil.isNullOrEmpty(emp.getMm_emp_email())){
                                    email.setText(emp.getMm_emp_email());
                                }else {
                                    email.setText("暂未填写");
                                }
                                if(!StringUtil.isNullOrEmpty(emp.getMm_emp_qq())){
                                    qq.setText(emp.getMm_emp_qq());
                                }else {
                                    qq.setText("暂未填写");
                                }
                                if(!StringUtil.isNullOrEmpty(emp.getMm_emp_weixin())){
                                    weixin.setText(emp.getMm_emp_weixin());
                                }else {
                                    weixin.setText("暂未填写");
                                }
                                if(!StringUtil.isNullOrEmpty(emp.getMm_emp_age())){
                                    age.setText(emp.getMm_emp_age());
                                }else {
                                    age.setText("暂未填写");
                                }
                                if(!StringUtil.isNullOrEmpty(emp.getProvinceName()) || !StringUtil.isNullOrEmpty(emp.getCityName()) ||!StringUtil.isNullOrEmpty(emp.getAreaName()) ){
                                    address.setText(emp.getProvinceName() +  emp.getCityName() + emp.getAreaName());
                                }else {
                                    address.setText("暂未填写");
                                }
                                if(!StringUtil.isNullOrEmpty(emp.getMm_emp_company())){
                                    company.setText(emp.getMm_emp_company());
                                }else{
                                    company.setText("暂未填写");
                                }

                                if(!StringUtil.isNullOrEmpty(emp.getMm_emp_techang())){
                                    techang.setText(emp.getMm_emp_techang());
                                }else{
                                    techang.setText("暂未填写");
                                }
                                if(!StringUtil.isNullOrEmpty(emp.getMm_emp_xingqu())){
                                    xingqu.setText(emp.getMm_emp_xingqu());
                                }else{
                                    xingqu.setText("暂未填写");
                                }
                                if(!StringUtil.isNullOrEmpty(emp.getMm_emp_detail())){
                                    jianjie.setText(emp.getMm_emp_detail());
                                }else{
                                    jianjie.setText("暂未填写");
                                }

                                if(!StringUtil.isNullOrEmpty(emp.getMm_emp_native())){
                                    mm_emp_native.setText(emp.getMm_emp_native());
                                }else{
                                    mm_emp_native.setText("暂未填写");
                                }

                                if(!StringUtil.isNullOrEmpty(emp.getMm_emp_motto())){
                                    mm_emp_motto.setText("签名："+emp.getMm_emp_motto());
                                }else{
                                    mm_emp_motto.setText("签名：暂未填写");
                                }

                                if(emp.getMm_emp_id().equals(getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class))){
                                    liner_bottom.setVisibility(View.GONE);
                                    guirenwang.setVisibility(View.GONE);
                                }else {
                                    liner_bottom.setVisibility(View.VISIBLE);
                                    guirenwang.setVisibility(View.VISIBLE);
                                }
                                if("1".equals(emp.getMm_emp_sex())){
                                    sex.setImageDrawable(getResources().getDrawable(R.drawable.icon_sex_female));
                                }else {
                                    sex.setImageDrawable(getResources().getDrawable(R.drawable.icon_sex_male));
                                }

                            } else {
                                Toast.makeText(ProfileActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ProfileActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", mm_emp_id);
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
        record = recordList.get(position);
        switch (flag) {
            case 1:
                Intent comment = new Intent(ProfileActivity.this, PublishCommentAcitvity.class);
                comment.putExtra(Constants.FATHER_PERSON_NAME, "");
                comment.putExtra(Constants.FATHER_UUID, "0");
                comment.putExtra(Constants.RECORD_UUID, record.getMm_msg_id());
                comment.putExtra(Constants.FATHER_PERSON_UUID, record.getMm_emp_id());
                comment.putExtra("fplempid", "");
                startActivity(comment);
                break;
            case 2:
                recordtmp = record;//放到中间存储
                zan_click(record);
                break;
            case 3:
                break;
            case 4:
//                if (!emp_id.equals(record.getMm_emp_id())) {
//                    Intent profile = new Intent(getActivity(), ProfilePersonalActivity.class);
//                    profile.putExtra(Constants.EMPID, record.getRecordEmpId());
//                    startActivity(profile);
//                } else {
//                    Intent profile = new Intent(getActivity(), UpdateProfilePersonalActivity.class);
//                    startActivity(profile);
//                }
                break;
            case 5:
//                String videoUrl = record.getRecordVideo();
//                Intent intent = new Intent(getActivity(), VideoPlayerActivity2.class);
//                VideoPlayer video = new VideoPlayer(videoUrl);
//                intent.putExtra(Constants.EXTRA_LAYOUT, "0");
//                intent.putExtra(VideoPlayer.class.getName(), video);
//                startActivity(intent);
                break;
            case 6:
                //删除该动态
//                recordtmp = record;//放到中间存储
//                tmpSelected = position;
//                showSelectImageDialog();
                break;
            case 7:
                if (record.getMm_msg_type().equals("1"))
                {
//                    //是推广
//                    Intent webView = new Intent(getActivity(), WebViewActivity.class);
//                    webView.putExtra("strurl", record.get());
//                    startActivity(webView);

                }
                break;
            case 8:
                //网址链接
            {
                String strcont = record.getMm_msg_content();//内容
                if (strcont.contains("http")){
                    //如果包含http
                    String strhttp = strcont.substring(strcont.indexOf("http"), strcont.length());
                    Intent webView = new Intent(ProfileActivity.this, WebViewActivity.class);
                    webView.putExtra("strurl", strhttp);
                    webView.putExtra("strname", "贵人");
                    startActivity(webView);
                }
            }
            break;
            case 9:
                //点击学校
//                schoolId = record.getRecordSchoolId();
//                initData();
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.btn_baijian:
//                Intent tpv=  new Intent(ProfileActivity.this, TuopuActivity.class);
//                tpv.putExtra("mm_emp_id", mm_emp_id);
//                startActivity(tpv);

                showRelate();
                break;
            case R.id.head:
                //头像
            {
                //
                final String[] picUrls = {emp.getMm_emp_cover()};
                Intent intent = new Intent(this, GalleryUrlActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.putExtra(Constants.IMAGE_URLS, picUrls);
                intent.putExtra(Constants.IMAGE_POSITION, 0);
                startActivity(intent);
            }
                break;
            case R.id.guirenwang:
                //贵人网
            {
                Intent intent = new Intent(ProfileActivity.this, TuopuActivity.class);
                intent.putExtra("mm_emp_id", emp.getMm_emp_id());
                startActivity(intent);
            }
                break;
            case R.id.btn_chat:
            {
                //私聊
                Intent chatV = new Intent(ProfileActivity.this, ChatActivity.class);
                chatV.putExtra("userId", emp.getHxusername());
                chatV.putExtra("userName", emp.getMm_emp_nickname());
                startActivity(chatV);
            }
                break;
            case R.id.guirenlu:
            {
                //贵人录
                Intent intent = new Intent(ProfileActivity.this, TongxunluActivityT.class);
                intent.putExtra("mm_emp_id", mm_emp_id);
                startActivity(intent);
            }
                break;
        }
    }

    void showRelate(){
        final Dialog picAddDialog = new Dialog(ProfileActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(this, R.layout.msg_dialog, null);
        TextView jubao_sure = (TextView) picAddInflate.findViewById(R.id.jubao_sure);
        final TextView jubao_cont = (TextView) picAddInflate.findViewById(R.id.jubao_cont);
        jubao_cont.setText("确定拜访该用户吗");
        jubao_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(ProfileActivity.this );
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                saveRelate();
                picAddDialog.dismiss();
            }
        });
        TextView jubao_cancle = (TextView) picAddInflate.findViewById(R.id.jubao_cancle);
        jubao_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picAddDialog.dismiss();
            }
        });
        picAddDialog.setContentView(picAddInflate);
        picAddDialog.show();
    }

    //赞
    private void zan_click(final Record record) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.CLICK_LIKE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            RecordDATA data = getGson().fromJson(s, RecordDATA.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                //赞+1
                                Toast.makeText(ProfileActivity.this, "点赞成功", Toast.LENGTH_SHORT).show();
                                recordtmp.setZanNum(String.valueOf((Integer.valueOf(recordtmp.getZanNum()) + 1)));
                                adapter.notifyDataSetChanged();
                            }
                            if (Integer.parseInt(data.getCode())  == 1) {
                                Toast.makeText(ProfileActivity.this, "已经赞过", Toast.LENGTH_SHORT).show();
                            }
                            if (Integer.parseInt(data.getCode())  == 2) {
                                Toast.makeText(ProfileActivity.this, "点赞失败，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "点赞失败，请稍后重试", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ProfileActivity.this, "点赞失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("recordId", record.getMm_msg_id());
                params.put("empId", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
                params.put("sendEmpId", record.getMm_emp_id());
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
            if (action.equals(Constants.SEND_SUCCESS)) {
                String str = intent.getExtras().getString(Constants.SEND_VALUE_ONE);
//                if ("0".equals(str)) {
//                    schoolId = "";
//                    maintitle.setText("所有大学");
//                }
//                if ("1".equals(str)) {
//                    schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
//                    maintitle.setText("我的大学");
//                }
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }
            if (action.equals(Constants.SEND_PIC_TX_SUCCESS)) {
                //更改头像的广播事件
//                imageLoader.displayImage(getGson().fromJson(getSp().getString(Constants.EMPCOVER, ""), String.class), main_cover, UniversityApplication.txOptions, animateFirstListener);
            }
            if (action.equals(Constants.SEND_COMMENT_RECORD_SUCCESS)) {
                //刷新内容,评论+1
                String recordId =  intent.getExtras().getString("recordId");
                for(Record record:recordList){
                    if(record.getMm_msg_id().equals(recordId)){
                        record.setPlNum(String.valueOf(Integer.parseInt(record.getPlNum())+1));
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
            if(action.equals(Constants.SEND_DELETE_RECORD_SUCCESS)){
                String recordId =  intent.getExtras().getString("recordId");
                for(Record record:recordList){
                    if(record.getMm_msg_id().equals(recordId)){
                        recordList.remove(record);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
            if(action.equals(Constants.SEND_INDEX_SUCCESS)){
                Record record1 = (Record) intent.getExtras().get("addRecord");
                recordList.add(0, record1);
                adapter.notifyDataSetChanged();
            }
        }

    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.SEND_SUCCESS);//设置下拉按钮的广播事件
        myIntentFilter.addAction(Constants.SEND_PIC_TX_SUCCESS);//设置头像的广播事件
        myIntentFilter.addAction(Constants.SEND_COMMENT_RECORD_SUCCESS);//动态评论添加  更新评论数量
        myIntentFilter.addAction(Constants.SEND_DELETE_RECORD_SUCCESS);//动态详情页删除动态，更新首页
        myIntentFilter.addAction(Constants.SEND_INDEX_SUCCESS);//添加说说和添加视频成功，刷新首页
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    void getRelate(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_MINE_CONTACTS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            EmpRelateObjData data = getGson().fromJson(s, EmpRelateObjData.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                if(data !=null){
                                    List<EmpRelateObj> empRelateObjs = data.getData();
                                    if(empRelateObjs != null && empRelateObjs.size()>0){
                                        EmpRelateObj empRelateObj = empRelateObjs.get(0);
                                        if(empRelateObj != null){
                                            switch (Integer.parseInt(empRelateObj.getState())){
                                                case 0:
                                                    //已经拜见了
                                                    btn_baijian.setText("已拜见");
                                                    btn_baijian.setClickable(false);
                                                    break;
                                                case 1:
                                                    //已经结交了
                                                    btn_baijian.setText("已结交");
                                                    btn_baijian.setClickable(false);
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_emp_id1", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
                params.put("mm_emp_id2", emp.getMm_emp_id());
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

    void saveRelate(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SAVE_MINE_CONTACTS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                               //拜见成功
                                showMsg(ProfileActivity.this, "拜见成功");
                                btn_baijian.setText("已拜见");
                                btn_baijian.setClickable(false);
                            }else if(Integer.parseInt(data.getCode()) == 2){
                                //拜见成功
                                btn_baijian.setText("已拜见");
                                btn_baijian.setClickable(false);
                            }else {
                                showMsg(ProfileActivity.this, "拜见失败,请稍后重试！");
                            }
                        } else {
                            showMsg(ProfileActivity.this, "拜见失败,请稍后重试！");
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
                        showMsg(ProfileActivity.this, "拜见失败,请稍后重试！");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_emp_id1", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
                params.put("mm_emp_id2", emp.getMm_emp_id());
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
