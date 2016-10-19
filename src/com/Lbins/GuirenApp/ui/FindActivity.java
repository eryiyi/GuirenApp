package com.Lbins.GuirenApp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.OnClickContentItemListener;
import com.Lbins.GuirenApp.adapter.RecordAdapter;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.dao.DBHelper;
import com.Lbins.GuirenApp.data.RecordDATA;
import com.Lbins.GuirenApp.data.SuccessData;
import com.Lbins.GuirenApp.library.PullToRefreshBase;
import com.Lbins.GuirenApp.library.PullToRefreshListView;
import com.Lbins.GuirenApp.module.Record;
import com.Lbins.GuirenApp.util.Constants;
import com.Lbins.GuirenApp.util.GuirenHttpUtils;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.CustomProgressDialog;
import com.Lbins.GuirenApp.widget.DeletePopWindow;
import com.Lbins.GuirenApp.widget.MenuPopMenu;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/6/9.
 */
public class FindActivity extends BaseActivity implements View.OnClickListener,OnClickContentItemListener ,MenuPopMenu.OnItemClickListener{
    private Resources res;
    private PullToRefreshListView listView;
    private ImageView no_collections;
    private RecordAdapter adapter;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private List<Record> recordList = new ArrayList<Record>();
    private String emp_id = "";//当前登陆者UUID
    Record recordtmp;//转换用

    private int tmpSelected;//暂时存UUID  删除用
    private DeletePopWindow deleteWindow;

    boolean isMobileNet, isWifiNet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_activity);
        registerBoradcastReceiver();
        res = getResources();
        emp_id = getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class);
        initView();

        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(FindActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(FindActivity.this);
            if (!isMobileNet && !isWifiNet) {
                recordList.addAll(DBHelper.getInstance(FindActivity.this).getRecordList());
                if(recordList.size() > 0){
                    no_collections.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }else {
                    no_collections.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }else {
                progressDialog = new CustomProgressDialog(FindActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                initData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void initView(){
        //初始化
        no_collections = (ImageView) this.findViewById(R.id.no_record);
        listView = (PullToRefreshListView) this.findViewById(R.id.lstv);
        adapter = new RecordAdapter(recordList, FindActivity.this, emp_id);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                //判断是否有网
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(FindActivity.this);
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(FindActivity.this);
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
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                //判断是否有网
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(FindActivity.this);
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(FindActivity.this);
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
        adapter.setOnClickContentItemListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Record record = recordList.get(position - 1);
                if(record != null){
//                if (!record.getMm_msg_type().equals("1")) {
                    Intent detail = new Intent(FindActivity.this, DetailPageAcitvity.class);
                    detail.putExtra(Constants.INFO, record);
                    startActivity(detail);
//                }
                }

            }
        });
        this.findViewById(R.id.back).setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
        }
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
                                    DBHelper dbHelper = DBHelper.getInstance(FindActivity.this);
                                    for (Record record1 : data.getData()) {
                                        if (dbHelper.getRecordById(record1.getMm_msg_id()) == null) {
                                            DBHelper.getInstance(FindActivity.this).saveRecord(record1);
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(FindActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(FindActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(FindActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
    Record record;

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(FindActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(FindActivity.this);
            if (!isMobileNet && !isWifiNet) {
                showMsg(FindActivity.this, "请检查网络链接");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        record = recordList.get(position);
        switch (flag) {
            case 1:
                Intent comment = new Intent(FindActivity.this, PublishCommentAcitvity.class);
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
                    Intent profile = new Intent(FindActivity.this, ProfileActivity.class);
                    profile.putExtra("mm_emp_id", record.getMm_emp_id());
                    startActivity(profile);
                } else {
                    Intent profile = new Intent(FindActivity.this, EditEmpActivity.class);
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
                    Intent webView = new Intent(FindActivity.this, WebViewActivity.class);
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
                Intent intentzmt = new Intent(FindActivity.this, ProfileZmtActivity.class);
                intentzmt.putExtra("mm_emp_id", record.getMm_emp_id());
                startActivity(intentzmt);
            }
                break;
            case 11:
            {
                //刪除動態
            }
                break;
            default:
                break;
        }
    }

    // 选择是否删除
    private void showSelectImageDialog() {
        deleteWindow = new DeletePopWindow(FindActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(FindActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            deleteWindow.dismiss();
            //判断是否有网
            try {
                isMobileNet = GuirenHttpUtils.isMobileDataEnable(FindActivity.this);
                isWifiNet = GuirenHttpUtils.isWifiDataEnable(FindActivity.this);
                if (!isMobileNet && !isWifiNet) {
                    showMsg(FindActivity.this, "请检查网络链接");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                                Toast.makeText(FindActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                                recordList.remove(tmpSelected);
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(FindActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(FindActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(FindActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(FindActivity.this, "点赞成功", Toast.LENGTH_SHORT).show();
                                recordtmp.setZanNum(String.valueOf((Integer.valueOf(recordtmp.getZanNum()) + 1)));
                                adapter.notifyDataSetChanged();
                            }
                            if (Integer.parseInt(data.getCode())  == 1) {
                                Toast.makeText(FindActivity.this, "已经赞过", Toast.LENGTH_SHORT).show();
                            }
                            if (Integer.parseInt(data.getCode())  == 2) {
                                Toast.makeText(FindActivity.this, "点赞失败，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(FindActivity.this, "点赞失败，请稍后重试", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(FindActivity.this, "点赞失败，请稍后重试", Toast.LENGTH_SHORT).show();
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
                //判断是否有网
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(FindActivity.this);
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(FindActivity.this);
                    if (!isMobileNet && !isWifiNet) {
                        listView.onRefreshComplete();
                    }else {
                        initData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    //下拉菜单
    private MenuPopMenu menu;
    List<String> arrayMenu = new ArrayList<String>();

    //弹出顶部主菜单
    public void onTopMenuPopupButtonClick(View view) {
        arrayMenu.clear();
        arrayMenu.add("文字");
//        arrayMenu.add("秒拍");
        arrayMenu.add("相机");
        //顶部右侧按钮
        menu = new MenuPopMenu(FindActivity.this, arrayMenu);
        menu.setOnItemClickListener(this);
        menu.showAsDropDown(view);
    }

    @Override
    public void onItemClick(int index) {
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(FindActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(FindActivity.this);
            if (!isMobileNet && !isWifiNet) {
                showMsg(FindActivity.this, "请检查网络链接");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (index) {
            case 0:
                Intent pic = new Intent(FindActivity.this, PublishPicActivity.class);
                pic.putExtra(Constants.SELECT_PHOTOORPIIC, "0");
                startActivity(pic);
                break;
//            case 1:
//                save(Constants.PK_ADD_VIDEO_TYPE, "0");
//                Intent intent = new Intent(MainActivity.this, MediaRecorderActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
//                break;
            case  1:
                Intent photo = new Intent(FindActivity.this, PublishPicActivity.class);
                photo.putExtra(Constants.SELECT_PHOTOORPIIC, "1");
                startActivity(photo);
                break;
        }
    }

}
