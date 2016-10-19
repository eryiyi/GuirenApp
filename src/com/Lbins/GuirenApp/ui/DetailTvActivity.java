package com.Lbins.GuirenApp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.AnimateFirstDisplayListener;
import com.Lbins.GuirenApp.adapter.DetailCommentAdapter;
import com.Lbins.GuirenApp.adapter.DetailFavourAdapter;
import com.Lbins.GuirenApp.adapter.OnClickContentItemListener;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.CommentContentDATA;
import com.Lbins.GuirenApp.data.FavoursDATA;
import com.Lbins.GuirenApp.data.RecordDATA;
import com.Lbins.GuirenApp.library.PullToRefreshBase;
import com.Lbins.GuirenApp.library.PullToRefreshListView;
import com.Lbins.GuirenApp.module.CommentContent;
import com.Lbins.GuirenApp.module.Favour;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/15.
 */
public class DetailTvActivity extends BaseActivity implements View.OnClickListener,OnClickContentItemListener {
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    List<CommentContent> commentContents;
    private Videos record;//传参
    private PullToRefreshListView detail_lstv;
    private ImageView detail_back;//返回按钮
    private ImageView detail_share;//分享按钮
    private LinearLayout commentLayout;//头部

    private LinearLayout detail_like_liner;//赞区域
    private LinearLayout detail_comment_liner;//评论区域

    private TextView title;
    private TextView content;
    private ImageView picone;
    private ImageView picplay;

    private PullToRefreshListView detail_comment_lstv;//评论列表
    private DetailCommentAdapter adapter;

    private GridView gridView;
    private List<Favour> itemList = new ArrayList<Favour>();
    private DetailFavourAdapter adaptertwo;

    private List<Favour> itemListtwo = new ArrayList<Favour>();//赞列表用

    private RelativeLayout detail_like_liner_layout;//赞区域
    private String emp_id = "";//当前登陆者UUID

    boolean isMobileNet, isWifiNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_video_page);
        registerBoradcastReceiver();
        record = (Videos) getIntent().getExtras().get(Constants.INFO);//传递过来的值
        commentContents = new ArrayList<CommentContent>();
        emp_id = getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class);
        initView();
        initData();

        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(DetailTvActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(DetailTvActivity.this);
            if (!isMobileNet && !isWifiNet) {
            }else {
                progressDialog =  new CustomProgressDialog(DetailTvActivity.this, "正在加载中", R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                loadData();
                getFavour();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initView() {
        commentLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.detail_video_header, null);
        detail_back = (ImageView) this.findViewById(R.id.detail_back);
        detail_back.setOnClickListener(this);
        detail_share = (ImageView) this.findViewById(R.id.detail_share);
        detail_share.setOnClickListener(this);

        detail_lstv = (PullToRefreshListView) this.findViewById(R.id.lstv);
        detail_lstv.setMode(PullToRefreshBase.Mode.BOTH);
        detail_lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(DetailTvActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                //判断是否有网
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(DetailTvActivity.this);
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(DetailTvActivity.this);
                    if (!isMobileNet && !isWifiNet) {
                        detail_lstv.onRefreshComplete();
                    }else {
                        loadData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(DetailTvActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                //判断是否有网
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(DetailTvActivity.this);
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(DetailTvActivity.this);
                    if (!isMobileNet && !isWifiNet) {
                        detail_lstv.onRefreshComplete();
                    }else {
                        loadData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        detail_like_liner = (LinearLayout) this.findViewById(R.id.detail_like_liner);
        detail_comment_liner = (LinearLayout) this.findViewById(R.id.detail_comment_liner);
        detail_comment_liner.setOnClickListener(this);
        detail_like_liner.setOnClickListener(this);

        title = (TextView) commentLayout.findViewById(R.id.title);
        content = (TextView) commentLayout.findViewById(R.id.content);
        picone = (ImageView) commentLayout.findViewById(R.id.picone);
        picplay = (ImageView) commentLayout.findViewById(R.id.picplay);
        picplay.setOnClickListener(this);
        adapter = new DetailCommentAdapter(this, commentContents);
        adapter.setOnClickContentItemListener(this);
        ListView lstv = detail_lstv.getRefreshableView();
        lstv.addHeaderView(commentLayout);//添加头部
        detail_lstv.setAdapter(adapter);

        detail_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommentContent commentContent = (CommentContent) parent.getAdapter().getItem(position);
                if (commentContent != null) {
                    Intent comment = new Intent(DetailTvActivity.this, PublishVideoCommentAcitvity.class);
                    comment.putExtra(Constants.FATHER_PERSON_NAME, commentContent.getNickName());
                    comment.putExtra(Constants.FATHER_UUID, commentContent.getId());
                    comment.putExtra(Constants.RECORD_UUID, record.getId());
                    comment.putExtra(Constants.FATHER_PERSON_UUID, "");
                    comment.putExtra("fplempid", commentContent.getEmpId());
                    startActivity(comment);
                }
            }
        });

        detail_like_liner_layout = (RelativeLayout) commentLayout.findViewById(R.id.detail_like_liner_layout);
        detail_like_liner_layout.setVisibility(View.GONE);

        gridView = (GridView) commentLayout.findViewById(R.id.gridView);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    private void initData() {
        //视频
        imageLoader.displayImage(record.getPicUrl(), picone, GuirenApplication.options, animateFirstListener);
        title.setText(record.getTitle() == null ? "" : record.getTitle());
        content.setText(record.getContent()==null?"":record.getContent());
    }

    @Override
    public void onClick(View v) {
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(DetailTvActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(DetailTvActivity.this);
            if (!isMobileNet && !isWifiNet) {
                showMsg(DetailTvActivity.this, "请检查网络链接");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (v.getId()) {
            case R.id.detail_back://返回按钮
                finish();
                break;
            case R.id.detail_share://分享按钮
            {
                share();
            }
                break;
            case R.id.detail_comment_liner://评论
                Intent comment = new Intent(DetailTvActivity.this, PublishTvCommentAcitvity.class);
                comment.putExtra(Constants.FATHER_PERSON_NAME, "");
                comment.putExtra(Constants.FATHER_UUID, "0");
                comment.putExtra(Constants.RECORD_UUID, record.getId());
                comment.putExtra("fplempid", "");
                startActivity(comment);
                break;
            case R.id.detail_like_liner://点赞
                progressDialog = new CustomProgressDialog(DetailTvActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                detail_like_liner.setClickable(false);
                zan_click(record);
                break;

            case R.id.picplay://视频播放按钮
                String videoUrl = record.getVideoUrl();
                Intent intent = new Intent(DetailTvActivity.this, VideoPlayerActivity2.class);
                VideoPlayer video = new VideoPlayer(videoUrl);
                intent.putExtra(Constants.EXTRA_LAYOUT, "0");
                intent.putExtra(VideoPlayer.class.getName(), video);
                startActivity(intent);
//                final Uri uri = Uri.parse(videoUrl);
//                final Intent it = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(it);
                break;

        }
    }
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;

    private void loadData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_VIDEOS_PL_URL_TV,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            CommentContentDATA data = getGson().fromJson(s, CommentContentDATA.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                if (IS_REFRESH) {
                                    commentContents.clear();
                                }
                                commentContents.addAll(data.getData());
                                adapter.notifyDataSetChanged();

                            } else {
                                Toast.makeText(DetailTvActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailTvActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(DetailTvActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("recordId", record.getId());
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
        getRequestQueue().add(request);
    }


    private void getFavour() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_VIDEOS_FAVOUR_URL_TV,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            FavoursDATA data = getGson().fromJson(s, FavoursDATA.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                itemListtwo.clear();
                                itemListtwo = data.getData();
                                itemList.clear();
                                if (itemListtwo.size() > 5) {
                                    for (int i = 0; i < 6; i++) {
                                        itemList.add(itemListtwo.get(i));
                                    }
                                } else {
                                    itemList.addAll(itemListtwo);
                                }
                                if (itemList.size() > 0) {//当存在赞数据的时候
                                    detail_like_liner_layout.setVisibility(View.VISIBLE);
                                }

                                adaptertwo = new DetailFavourAdapter(itemList, DetailTvActivity.this , itemListtwo.size());
                                gridView.setAdapter(adaptertwo);
                                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        if (position == (itemList.size() - 1)) {
//                                            Intent favour = new Intent(DetailVideosActivity.this, DetailFavourActivity.class);
//                                            favour.putExtra(Constants.RECORD_UUID, record.getRecordId());
//                                            startActivity(favour);
                                        }
                                    }
                                });
                                if (itemList.size() > 0) {//当存在赞数据的时候
                                    detail_like_liner_layout.setVisibility(View.VISIBLE);
                                }
                                adaptertwo.notifyDataSetChanged();
                            } else {
                                Toast.makeText(DetailTvActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailTvActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailTvActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", record.getId());
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



    CommentContent comt;
    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(DetailTvActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(DetailTvActivity.this);
            if (!isMobileNet && !isWifiNet) {
                showMsg(DetailTvActivity.this, "请检查网络链接");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        comt = commentContents.get(position);
        switch (flag) {
            case 1:
                if (!emp_id.equals(comt.getEmpId())) {
                    Intent mine = new Intent(DetailTvActivity.this, ProfileActivity.class);
                    mine.putExtra("mm_emp_id", comt.getEmpId());
                    startActivity(mine);
                } else {
                    Intent mine = new Intent(DetailTvActivity.this, EditEmpActivity.class);
                    startActivity(mine);
                }
                break;
        }
    }

    //赞
    private void zan_click(final Videos record) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.PUBLISH_VIDEO_FAVOUR_RECORD_TV,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            RecordDATA data = getGson().fromJson(s, RecordDATA.class);
                            if (Integer.parseInt(data.getCode() )== 200) {
                                //赞+1
                                Toast.makeText(DetailTvActivity.this, R.string.zan_success, Toast.LENGTH_SHORT).show();
                                //调用广播，刷新详细页评论
                                Intent intent1 = new Intent(Constants.SEND_FAVOUR_SUCCESS_VIDEO);
                                sendBroadcast(intent1);
                                detail_like_liner.setClickable(true);
                            }
                            if (Integer.parseInt(data.getCode() )== 1) {
                                Toast.makeText(DetailTvActivity.this, R.string.zan_error_one, Toast.LENGTH_SHORT).show();
                                detail_like_liner.setClickable(true);
                            }
                            if (Integer.parseInt(data.getCode()) == 2) {
                                Toast.makeText(DetailTvActivity.this, R.string.zan_error_two, Toast.LENGTH_SHORT).show();
                                detail_like_liner.setClickable(true);
                            }
                        } else {
                            Toast.makeText(DetailTvActivity.this, R.string.zan_error_two, Toast.LENGTH_SHORT).show();
                            detail_like_liner.setClickable(true);
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailTvActivity.this, R.string.zan_error_two, Toast.LENGTH_SHORT).show();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        detail_like_liner.setClickable(true);
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


    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.SEND_COMMENT_SUCCESS_VIDEO)) {
                //刷新内容
                IS_REFRESH = true;
                pageIndex = 1;
                //判断是否有网
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(DetailTvActivity.this);
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(DetailTvActivity.this);
                    if (!isMobileNet && !isWifiNet) {
                    }else {
                        loadData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (action.equals(Constants.SEND_FAVOUR_SUCCESS_VIDEO)) {
                //判断是否有网
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(DetailTvActivity.this);
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(DetailTvActivity.this);
                    if (!isMobileNet && !isWifiNet) {
                    }else {
                        getFavour();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.SEND_COMMENT_SUCCESS_VIDEO);//评论成功，刷新评论列表
        myIntentFilter.addAction(Constants.SEND_FAVOUR_SUCCESS_VIDEO);//点赞成功，刷新赞列表
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    void share() {
        new ShareAction(DetailTvActivity.this).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setShareboardclickCallback(shareBoardlistener)
                .open();
    }

    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            UMImage image = new UMImage(DetailTvActivity.this, record.getPicUrl());
            String title =  getGson().fromJson(getSp().getString("mm_emp_nickname", ""), String.class)+"邀您免费看电影" ;
            String content = record.getTitle()+record.getContent();
            new ShareAction(DetailTvActivity.this).setPlatform(share_media).setCallback(umShareListener)
                    .withText(content)
                    .withTitle(title)
                    .withTargetUrl((InternetURL.SHARE_VIDEOS_TV + "?id=" + record.getId()))
                    .withMedia(image)
                    .share();
        }
    };

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(DetailTvActivity.this, platform + getResources().getString(R.string.share_success), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(DetailTvActivity.this, platform + getResources().getString(R.string.share_error), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(DetailTvActivity.this, platform + getResources().getString(R.string.share_cancel), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(DetailTvActivity.this).onActivityResult(requestCode, resultCode, data);
    }
}
