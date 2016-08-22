package com.Lbins.GuirenApp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.ItemVideosAdapter;
import com.Lbins.GuirenApp.adapter.OnClickContentItemListener;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.dao.DBHelper;
import com.Lbins.GuirenApp.data.RecordDATA;
import com.Lbins.GuirenApp.data.VideosData;
import com.Lbins.GuirenApp.library.PullToRefreshBase;
import com.Lbins.GuirenApp.library.PullToRefreshListView;
import com.Lbins.GuirenApp.module.Record;
import com.Lbins.GuirenApp.module.VideoPlayer;
import com.Lbins.GuirenApp.module.Videos;
import com.Lbins.GuirenApp.util.Constants;
import com.Lbins.GuirenApp.util.GuirenHttpUtils;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.CustomProgressDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/15.
 */
public class VideosActivity extends BaseActivity implements View.OnClickListener,OnClickContentItemListener {

    private PullToRefreshListView lstv;
    private List<Videos> list = new ArrayList<Videos>();
    private ItemVideosAdapter adapter;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;

    private String emp_id = "";//当前登陆者UUID
    private int tmpId;


//    String shareUrl = InternetURL.SHARE_VIDEOS;

    private String time_is = "1";
    private String favour_is = "0";

    boolean isMobileNet, isWifiNet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
        setContentView(R.layout.videos_activity);
        emp_id = getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class);
        initView();

        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(VideosActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(VideosActivity.this);
            if (!isMobileNet && !isWifiNet) {
                list.addAll(DBHelper.getInstance(VideosActivity.this).getVideos());
                adapter.notifyDataSetChanged();
            }else {
                progressDialog =  new CustomProgressDialog(VideosActivity.this, "正在加载中", R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                initData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void initView() {
        lstv = (PullToRefreshListView) this.findViewById(R.id.lstv);
        adapter = new ItemVideosAdapter(list, VideosActivity.this);
        lstv.setMode(PullToRefreshBase.Mode.BOTH);
        lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(VideosActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                //判断是否有网
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(VideosActivity.this);
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(VideosActivity.this);
                    if (!isMobileNet && !isWifiNet) {
                        lstv.onRefreshComplete();
                    }else {
                        initData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(VideosActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                //判断是否有网
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(VideosActivity.this);
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(VideosActivity.this);
                    if (!isMobileNet && !isWifiNet) {
                        lstv.onRefreshComplete();
                    }else {
                        initData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        lstv.setAdapter(adapter);
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Videos tmpVideos = list.get(position - 1);
                Intent detailView = new Intent(VideosActivity.this, DetailVideosActivity.class);
                detailView.putExtra(Constants.INFO, tmpVideos);
                startActivity(detailView);
            }
        });
        adapter.setOnClickContentItemListener(this);
        this.findViewById(R.id.liner_one).setOnClickListener(this);
        this.findViewById(R.id.liner_two).setOnClickListener(this);
        this.findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(VideosActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(VideosActivity.this);
            if (!isMobileNet && !isWifiNet) {
                showMsg(VideosActivity.this, "请检查网络链接");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (view.getId()){
            case R.id.liner_one:
                progressDialog = new CustomProgressDialog(VideosActivity.this, "正在加载中",R.anim.custom_dialog_frame);

                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                IS_REFRESH = true;
                pageIndex = 1;
                time_is = "1";
                favour_is = "0";
                initData();
                break;
            case R.id.liner_two:
                progressDialog = new CustomProgressDialog(VideosActivity.this, "正在加载中",R.anim.custom_dialog_frame);

                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                IS_REFRESH = true;
                pageIndex = 1;
                favour_is = "1";
                time_is = "0";
                initData();
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    Videos tmpVideos;
    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(VideosActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(VideosActivity.this);
            if (!isMobileNet && !isWifiNet) {
                showMsg(VideosActivity.this, "请检查网络链接");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tmpVideos = list.get(position);
        tmpId = position;
        switch (flag){
            case 1:
                //评论
            {
                Intent comment = new Intent(VideosActivity.this, PublishVideoCommentAcitvity.class);
                comment.putExtra(Constants.FATHER_PERSON_NAME, "");
                comment.putExtra(Constants.FATHER_UUID, "0");
                comment.putExtra(Constants.RECORD_UUID, tmpVideos.getId());
                comment.putExtra("fplempid", "");
                startActivity(comment);
            }
                break;
            case 2:
                //分享
            {
                share();
            }
                break;
            case 3:
                //赞
            {
                progressDialog =  new CustomProgressDialog(VideosActivity.this, "正在加载中",R.anim.custom_dialog_frame);

                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                zan_click(tmpVideos);
            }
                break;
            case 4:
                //播放
                String videoUrl = tmpVideos.getVideoUrl();
//                Intent intent = new Intent(VideosActivity.this, VideoPlayerActivity2.class);
//                VideoPlayer video = new VideoPlayer(videoUrl);
//                intent.putExtra(Constants.EXTRA_LAYOUT, "0");
//                intent.putExtra(VideoPlayer.class.getName(), video);
//                startActivity(intent);
                final Uri uri = Uri.parse(videoUrl);
                final Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
                break;
        }
    }

    void share() {
        new ShareAction(VideosActivity.this).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setShareboardclickCallback(shareBoardlistener)
                .open();
    }

    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            UMImage image = new UMImage(VideosActivity.this, tmpVideos.getPicUrl());
            String title =  getGson().fromJson(getSp().getString("mm_emp_nickname", ""), String.class)+"邀您免费看电影" ;
            String content = tmpVideos.getTitle()+tmpVideos.getContent();
            new ShareAction(VideosActivity.this).setPlatform(share_media).setCallback(umShareListener)
                    .withText(content)
                    .withTitle(title)
                    .withTargetUrl((InternetURL.SHARE_VIDEOS + "?id=" + tmpVideos.getId()))
                    .withMedia(image)
                    .share();
        }
    };

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(VideosActivity.this, platform + getResources().getString(R.string.share_success), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(VideosActivity.this, platform + getResources().getString(R.string.share_error), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(VideosActivity.this, platform + getResources().getString(R.string.share_cancel), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(VideosActivity.this).onActivityResult(requestCode, resultCode, data);
    }


    //赞
    private void zan_click(final Videos record) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
               InternetURL.PUBLISH_VIDEO_FAVOUR_RECORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            RecordDATA data = getGson().fromJson(s, RecordDATA.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                //赞+1
                                Toast.makeText(VideosActivity.this, R.string.zan_success, Toast.LENGTH_SHORT).show();
                                list.get(tmpId).setZanNum(String.valueOf(Integer.parseInt((list.get(tmpId).getZanNum() == null ? "0" : list.get(tmpId).getZanNum())) + 1));
                                adapter.notifyDataSetChanged();
                            }
                            if (Integer.parseInt(data.getCode()) == 1) {
                                Toast.makeText(VideosActivity.this, R.string.zan_error_one, Toast.LENGTH_SHORT).show();

                            }
                            if (Integer.parseInt(data.getCode()) == 2) {
                                Toast.makeText(VideosActivity.this, R.string.zan_error_two, Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Toast.makeText(VideosActivity.this, R.string.zan_error_two, Toast.LENGTH_SHORT).show();

                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(VideosActivity.this, R.string.zan_error_two, Toast.LENGTH_SHORT).show();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("recordId", record.getId());
                params.put("empId", emp_id);
                params.put("sendEmpId", "");
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


    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
               InternetURL.GET_VIDEOS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            VideosData data = getGson().fromJson(s, VideosData.class);
                            if (Integer.parseInt(data.getCode())== 200) {
                                if (IS_REFRESH) {
                                    list.clear();
                                }
                                list.addAll(data.getData());
                                lstv.onRefreshComplete();
                                adapter.notifyDataSetChanged();

                                //处理数据，需要的话保存到数据库
                                if (data != null && data.getData() != null) {
                                    DBHelper dbHelper = DBHelper.getInstance(VideosActivity.this);
                                    for (Videos videos : data.getData()) {
                                        if (dbHelper.getVideosById(videos.getId()) == null) {
                                            DBHelper.getInstance(VideosActivity.this).saveVideos(videos);
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(VideosActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(VideosActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(VideosActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", String.valueOf(pageIndex));
                params.put("time_is", time_is);
                params.put("favour_is", favour_is);
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
            if (action.equals(Constants.SEND_COMMENT_SUCCESS_VIDEO)) {
                //刷新内容
                list.get(tmpId).setPlNum(String.valueOf((Integer.parseInt(list.get(tmpId).getPlNum() == null ? "0" : list.get(tmpId).getPlNum()) + 1)));//评论加1
                adapter.notifyDataSetChanged();
            }
        }
    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.SEND_COMMENT_SUCCESS_VIDEO);//评论成功，刷新评论列表
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }



}
