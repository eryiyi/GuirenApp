package com.Lbins.GuirenApp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.ItemTvAdapter;
import com.Lbins.GuirenApp.adapter.ItemVideoTypeAdapter;
import com.Lbins.GuirenApp.adapter.OnClickContentItemListener;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.dao.DBHelper;
import com.Lbins.GuirenApp.data.RecordDATA;
import com.Lbins.GuirenApp.data.VideoTypeObjData;
import com.Lbins.GuirenApp.data.VideosData;
import com.Lbins.GuirenApp.library.PullToRefreshBase;
import com.Lbins.GuirenApp.library.PullToRefreshListView;
import com.Lbins.GuirenApp.module.VideoPlayer;
import com.Lbins.GuirenApp.module.VideoTypeObj;
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
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/15.
 * 视频
 */
public class VideosActivity extends BaseActivity implements View.OnClickListener,OnClickContentItemListener {
    private PullToRefreshListView lstv;
    private List<Videos> list = new ArrayList<Videos>();
    protected ItemTvAdapter adapter;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;

    private String emp_id = "";//当前登陆者UUID

    boolean isMobileNet, isWifiNet;

    //viewPager
    private ViewPager vPager;
    private List<View> views;
    private View view1, view2;
    private int currentSelect = 0;//当前选中的viewpage

    //----第二部分
    private GridView gridv_one;
    private ItemVideoTypeAdapter adapterType;
    private List<VideoTypeObj> listsType = new ArrayList<VideoTypeObj>();

    private TextView btn_one;
    private TextView btn_two;


    private UMShareListener mShareListener;
    private ShareAction mShareAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
        setContentView(R.layout.videos_activity_t);
        emp_id = getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class);
        initView();
        mShareListener = new CustomShareListener(VideosActivity.this);
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
                getTypes();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        //viewPage
        btn_one = (TextView) this.findViewById(R.id.btn_one);
        btn_two = (TextView) this.findViewById(R.id.btn_two);
        btn_one.setOnClickListener(this);
        btn_two.setOnClickListener(this);
        vPager = (ViewPager) this.findViewById(R.id.vPager);
        views = new ArrayList<View>();
        LayoutInflater inflater = getLayoutInflater();
        view1 = inflater.inflate(R.layout.video_one_fragment, null);
        view2 = inflater.inflate(R.layout.video_two_fragment, null);
        views.add(view1);
        views.add(view2);

        vPager.setAdapter(new MyViewPagerAdapter(views));
        currentSelect = 0;
        vPager.setCurrentItem(currentSelect);
        vPager.setOnPageChangeListener(new MyOnPageChangeListener());

        //第一部分
        lstv = (PullToRefreshListView) view1.findViewById(R.id.lstv);
        adapter = new ItemTvAdapter(list, VideosActivity.this);
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
                    } else {
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
                    } else {
                        initData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        lstv.setAdapter(adapter);
        adapter.setOnClickContentItemListener(this);
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Videos tmpVideos = list.get(position - 1);
                Intent detailView = new Intent(VideosActivity.this, DetailTvActivity.class);
                detailView.putExtra(Constants.INFO, tmpVideos);
                startActivity(detailView);
            }
        });
        this.findViewById(R.id.back).setOnClickListener(this);

        gridv_one = (GridView) view2.findViewById(R.id.lstv);
        adapterType = new ItemVideoTypeAdapter(listsType, VideosActivity.this);
        gridv_one.setAdapter(adapterType);
        gridv_one.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(listsType.size() > position){
                    VideoTypeObj videoTypeObj = listsType.get(position);
                    if(videoTypeObj != null){
                        Intent intent = new Intent(VideosActivity.this, VideosByTypeActivity.class);
                        intent.putExtra("video_type_id", videoTypeObj.getVideo_type_id());
                        intent.putExtra("video_type_name", videoTypeObj.getVideo_type_name());
                        startActivity(intent);
                    }
                }
            }
        });
        gridv_one.setSelector(new ColorDrawable(Color.TRANSPARENT));
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
            case R.id.back:
                finish();
                break;
            case R.id.btn_one:
            {
                btn_one.setTextColor(getResources().getColor(R.color.white));
                btn_two.setTextColor(getResources().getColor(R.color.gray_button));
                currentSelect = 0;
                vPager.setCurrentItem(currentSelect);
            }
                break;
            case R.id.btn_two:
            {
                btn_two.setTextColor(getResources().getColor(R.color.white));
                btn_one.setTextColor(getResources().getColor(R.color.gray_button));
                currentSelect = 1;
                vPager.setCurrentItem(currentSelect);
            }
                break;
        }
    }

    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
               InternetURL.GET_VIDEOS_TV_URL,
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

    private void getTypes() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_VIDEO_TYPES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            VideoTypeObjData data = getGson().fromJson(s, VideoTypeObjData.class);
                            if (Integer.parseInt(data.getCode())== 200) {
                                listsType.clear();
                                listsType.addAll(data.getData());
                                adapterType.notifyDataSetChanged();
//                                //处理数据，需要的话保存到数据库
//                                if (data != null && data.getData() != null) {
//                                    DBHelper dbHelper = DBHelper.getInstance(VideosActivity.this);
//                                    for (Videos videos : data.getData()) {
//                                        if (dbHelper.getVideosById(videos.getId()) == null) {
//                                            DBHelper.getInstance(VideosActivity.this).saveVideos(videos);
//                                        }
//                                    }
//                                }
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

    public class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;

        public MyViewPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mListViews.get(position), 0);
            return mListViews.get(position);
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
//        int one = offset * 1 + bmpW;
//        int two = one * 1;

        public void onPageScrollStateChanged(int arg0) {
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageSelected(int arg0) {
//            Animation animation = new TranslateAnimation(one*currIndex, one*arg0, 0, 0);
//            currIndex = arg0;
//            animation.setFillAfter(true);
//            animation.setDuration(300);
//            imageView.startAnimation(animation);
            if (arg0 == 0) {
                btn_one.setTextColor(getResources().getColor(R.color.white));
                btn_two.setTextColor(getResources().getColor(R.color.gray_button));
                currentSelect = 0;
            }
            if (arg0 == 1) {
                btn_one.setTextColor(getResources().getColor(R.color.gray_button));
                btn_two.setTextColor(getResources().getColor(R.color.white));
                currentSelect = 1;
            }
        }
    }
    int tmpId = 0;
    Videos tmpVideos ;
    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        tmpVideos = list.get(position);
        tmpId = position;
        switch (flag){
            case 1:
                //评论
            {
                Intent comment = new Intent(VideosActivity.this, PublishTvCommentAcitvity.class);
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
//                share();
                UMImage image = new UMImage(VideosActivity.this, tmpVideos.getPicUrl());
                String title =  getGson().fromJson(getSp().getString("mm_emp_nickname", ""), String.class)+"邀您免费看电影" ;
                String content = tmpVideos.getTitle()+tmpVideos.getContent();

                 /*无自定按钮的分享面板*/
                mShareAction = new ShareAction(VideosActivity.this).setDisplayList(
                        SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE,
                        SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
                        SHARE_MEDIA.ALIPAY,
                        SHARE_MEDIA.SMS, SHARE_MEDIA.EMAIL,
                        SHARE_MEDIA.MORE)
                        .withText(content)
                        .withTitle(title)
                        .withTargetUrl((InternetURL.SHARE_VIDEOS_TV + "?id=" + tmpVideos.getId()))
                        .withMedia(image)
                        .setCallback(mShareListener);

                ShareBoardConfig config = new ShareBoardConfig();
                config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_CENTER);
                config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR); // 圆角背景
                mShareAction.open(config);
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
                Intent intent = new Intent(VideosActivity.this, VideoPlayerActivity2.class);
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

    private static class CustomShareListener implements UMShareListener {

        private WeakReference<Context> mActivity;

        private CustomShareListener(Context context) {
            mActivity = new WeakReference(context);
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {

            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(mActivity.get(), platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                if (platform!= SHARE_MEDIA.MORE&&platform!=SHARE_MEDIA.SMS
                        &&platform!=SHARE_MEDIA.EMAIL
                        &&platform!=SHARE_MEDIA.FLICKR
                        &&platform!=SHARE_MEDIA.FOURSQUARE
                        &&platform!=SHARE_MEDIA.TUMBLR
                        &&platform!=SHARE_MEDIA.POCKET
                        &&platform!=SHARE_MEDIA.PINTEREST
                        &&platform!=SHARE_MEDIA.LINKEDIN
                        &&platform!=SHARE_MEDIA.INSTAGRAM
                        &&platform!=SHARE_MEDIA.GOOGLEPLUS
                        &&platform!=SHARE_MEDIA.YNOTE
                        &&platform!=SHARE_MEDIA.EVERNOTE){
                    Toast.makeText(mActivity.get(), platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
                }

            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (platform!= SHARE_MEDIA.MORE&&platform!=SHARE_MEDIA.SMS
                    &&platform!=SHARE_MEDIA.EMAIL
                    &&platform!=SHARE_MEDIA.FLICKR
                    &&platform!=SHARE_MEDIA.FOURSQUARE
                    &&platform!=SHARE_MEDIA.TUMBLR
                    &&platform!=SHARE_MEDIA.POCKET
                    &&platform!=SHARE_MEDIA.PINTEREST
                    &&platform!=SHARE_MEDIA.LINKEDIN
                    &&platform!=SHARE_MEDIA.INSTAGRAM
                    &&platform!=SHARE_MEDIA.GOOGLEPLUS
                    &&platform!=SHARE_MEDIA.YNOTE
                    &&platform!=SHARE_MEDIA.EVERNOTE){
                Toast.makeText(mActivity.get(), platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
                if (t != null) {
                    Log.d("throw", "throw:" + t.getMessage());
                }
            }

        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(VideosActivity.this).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 屏幕横竖屏切换时避免出现window leak的问题
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mShareAction.close();
    }

//    void share() {
//        new ShareAction(VideosActivity.this).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
//                .setShareboardclickCallback(shareBoardlistener)
//                .open();
//    }
//
//    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {
//
//        @Override
//        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
//            UMImage image = new UMImage(VideosActivity.this, tmpVideos.getPicUrl());
//            String title =  getGson().fromJson(getSp().getString("mm_emp_nickname", ""), String.class)+"邀您免费看电影" ;
//            String content = tmpVideos.getTitle()+tmpVideos.getContent();
//            new ShareAction(VideosActivity.this).setPlatform(share_media).setCallback(umShareListener)
//                    .withText(content)
//                    .withTitle(title)
//                    .withTargetUrl((InternetURL.SHARE_VIDEOS_TV + "?id=" + tmpVideos.getId()))
//                    .withMedia(image)
//                    .share();
//        }
//    };
//
//    private UMShareListener umShareListener = new UMShareListener() {
//        @Override
//        public void onResult(SHARE_MEDIA platform) {
//            Toast.makeText(VideosActivity.this, platform + getResources().getString(R.string.share_success), Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onError(SHARE_MEDIA platform, Throwable t) {
//            Toast.makeText(VideosActivity.this, platform + getResources().getString(R.string.share_error), Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onCancel(SHARE_MEDIA platform) {
//            Toast.makeText(VideosActivity.this, platform + getResources().getString(R.string.share_cancel), Toast.LENGTH_SHORT).show();
//        }
//    };
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        UMShareAPI.get(VideosActivity.this).onActivityResult(requestCode, resultCode, data);
//    }


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
                params.put("empId", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
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

}
