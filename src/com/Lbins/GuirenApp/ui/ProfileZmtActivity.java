package com.Lbins.GuirenApp.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.AnimateFirstDisplayListener;
import com.Lbins.GuirenApp.adapter.OnClickContentItemListener;
import com.Lbins.GuirenApp.adapter.RecordAdapter;
import com.Lbins.GuirenApp.adapter.SearchGoodsAdapter;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.*;
import com.Lbins.GuirenApp.huanxin.ui.ChatActivity;
import com.Lbins.GuirenApp.library.PullToRefreshBase;
import com.Lbins.GuirenApp.library.PullToRefreshListView;
import com.Lbins.GuirenApp.module.*;
import com.Lbins.GuirenApp.util.Constants;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.DeletePopWindow;
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
public class ProfileZmtActivity extends BaseActivity implements View.OnClickListener ,OnClickContentItemListener {
    private String mm_emp_id;
    private Resources res;
    private PullToRefreshListView lstv;
    private RecordAdapter adapter;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private List<Record> recordList = new ArrayList<Record>();

    private String emp_id = "";//当前登陆者UUID
    Record recordtmp;//转换用

    private int tmpSelected;//暂时存UUID  删除用
    private DeletePopWindow deleteWindow;

    private ImageView pic_bg;
    private ImageView head;
    private TextView nickname;
    private RelativeLayout liner_header;

    private TextView company_person;
    private TextView company_tel;
    private TextView company_address;
    private TextView company_detail;
    private TextView title;
    private TextView add;

    private LinearLayout headLiner;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    Emp emp;
    ManagerInfo managerInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_zmt_activity);
        registerBoradcastReceiver();
        mm_emp_id = getIntent().getExtras().getString("mm_emp_id");
        res = this.getResources();
        this.findViewById(R.id.back).setOnClickListener(this);
        initView();
        if(!mm_emp_id.equals(getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class))){
            add.setVisibility(View.GONE);
        }else {
            add.setVisibility(View.VISIBLE);
        }
        progressDialog = new ProgressDialog(ProfileZmtActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        initData();
        getData();
        getDataM();
    }

    void initView(){
        headLiner = (LinearLayout) LayoutInflater.from(ProfileZmtActivity.this).inflate(R.layout.profile_zmt_header, null);
        head = (ImageView) headLiner.findViewById(R.id.head);
        add = (TextView) this.findViewById(R.id.add);
        add.setOnClickListener(this);
        nickname = (TextView) headLiner.findViewById(R.id.nickname);
        title = (TextView) this.findViewById(R.id.title);
        liner_header = (RelativeLayout) headLiner.findViewById(R.id.liner_header);
        pic_bg = (ImageView) headLiner.findViewById(R.id.pic_bg);
        company_person = (TextView) headLiner.findViewById(R.id.company_person);
        company_tel = (TextView) headLiner.findViewById(R.id.company_tel);
        company_address = (TextView) headLiner.findViewById(R.id.company_address);
        company_detail = (TextView) headLiner.findViewById(R.id.company_detail);

        head.setOnClickListener(this);
        nickname.setOnClickListener(this);

        lstv = (PullToRefreshListView) this.findViewById(R.id.lstv);
        ListView listView = lstv.getRefreshableView();

        listView.addHeaderView(headLiner);

        adapter = new RecordAdapter(recordList, ProfileZmtActivity.this,emp_id);
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
                Record record = recordList.get(position - 2);
                if(record != null){
//                if (!record.getMm_msg_type().equals("1")) {
                    Intent detail = new Intent(ProfileZmtActivity.this, DetailPageAcitvity.class);
                    detail.putExtra(Constants.INFO, record);
                    startActivity(detail);
//                }
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
                                Toast.makeText(ProfileZmtActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProfileZmtActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ProfileZmtActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", String.valueOf(pageIndex));
                params.put("mm_msg_type", Constants.ZMT_TYPE);
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

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        record = recordList.get(position);
        switch (flag) {
            case 1:
                Intent comment = new Intent(ProfileZmtActivity.this, PublishCommentAcitvity.class);
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
                if (!emp_id.equals(record.getMm_emp_id())) {
                    Intent profile = new Intent(ProfileZmtActivity.this, ProfileActivity.class);
                    profile.putExtra("mm_emp_id", record.getMm_emp_id());
                    startActivity(profile);
                } else {
                    Intent profile = new Intent(ProfileZmtActivity.this, EditEmpActivity.class);
                    startActivity(profile);
                }
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
                recordtmp = record;//放到中间存储
                tmpSelected = position;
                showSelectImageDialog();
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
                    Intent webView = new Intent(ProfileZmtActivity.this, WebViewActivity.class);
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
            case 10:
            {
                //自媒体
//                Intent intentzmt = new Intent(ProfileZmtActivity.this, ProfileZmtActivity.class);
//                intentzmt.putExtra("mm_emp_id", record.getMm_emp_id());
//                startActivity(intentzmt);
            }
            break;
            default:
                break;
        }
    }

    // 选择是否删除
    private void showSelectImageDialog() {
        deleteWindow = new DeletePopWindow(ProfileZmtActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(ProfileZmtActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            deleteWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_sure:
                    delete();
                    break;
                default:
                    break;
            }
        }
    };

    //删除方法
    private void delete() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.DELETE_RECORDS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                Toast.makeText(ProfileZmtActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                                recordList.remove(tmpSelected);
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(ProfileZmtActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProfileZmtActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ProfileZmtActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("recordId", recordtmp.getMm_msg_id());
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
                                Toast.makeText(ProfileZmtActivity.this, "点赞成功", Toast.LENGTH_SHORT).show();
                                recordtmp.setZanNum(String.valueOf((Integer.valueOf(recordtmp.getZanNum()) + 1)));
                                adapter.notifyDataSetChanged();
                            }
                            if (Integer.parseInt(data.getCode())  == 1) {
                                Toast.makeText(ProfileZmtActivity.this, "已经赞过", Toast.LENGTH_SHORT).show();
                            }
                            if (Integer.parseInt(data.getCode())  == 2) {
                                Toast.makeText(ProfileZmtActivity.this, "点赞失败，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProfileZmtActivity.this, "点赞失败，请稍后重试", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ProfileZmtActivity.this, "点赞失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("recordId", record.getMm_msg_id());
                params.put("empId", emp_id);
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
                                imageLoader.displayImage(emp.getMm_emp_cover(), head, GuirenApplication.txOptions, animateFirstListener);
                                nickname.setText(emp.getMm_emp_nickname());
                            } else {
                                Toast.makeText(ProfileZmtActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProfileZmtActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ProfileZmtActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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

    private void getDataM() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_DIANPU_MSG_DETAIL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            ManagerInfoData data = getGson().fromJson(s, ManagerInfoData.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                managerInfo = data.getData();
                                company_person.setText(managerInfo.getCompany_person());
                                company_tel.setText(managerInfo.getCompany_tel());
                                company_address.setText(managerInfo.getCompany_address());
                                company_detail.setText(managerInfo.getCompany_detail());
                                imageLoader.displayImage(managerInfo.getCompany_pic(), pic_bg, GuirenApplication.options, animateFirstListener);
                                title.setText(managerInfo.getCompany_name());
                            } else {
                                Toast.makeText(ProfileZmtActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProfileZmtActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ProfileZmtActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", mm_emp_id);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.add:
            {
                //添加
                Intent intentAdd = new Intent(ProfileZmtActivity.this, PublishZmtActivity.class);
                startActivity(intentAdd);
            }
                break;
        }
    }

    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
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


}
