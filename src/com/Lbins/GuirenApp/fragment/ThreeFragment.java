package com.Lbins.GuirenApp.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.AdViewPagerAdapter;
import com.Lbins.GuirenApp.adapter.ItemTvAdapter;
import com.Lbins.GuirenApp.adapter.ItemXixunAdapter;
import com.Lbins.GuirenApp.adapter.OnClickContentItemListener;
import com.Lbins.GuirenApp.base.BaseFragment;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.dao.DBHelper;
import com.Lbins.GuirenApp.data.AdObjData;
import com.Lbins.GuirenApp.data.RecordDATA;
import com.Lbins.GuirenApp.data.VideosData;
import com.Lbins.GuirenApp.data.XixunObjData;
import com.Lbins.GuirenApp.library.PullToRefreshBase;
import com.Lbins.GuirenApp.library.PullToRefreshListView;
import com.Lbins.GuirenApp.module.*;
import com.Lbins.GuirenApp.ui.*;
import com.Lbins.GuirenApp.util.Constants;
import com.Lbins.GuirenApp.util.GuirenHttpUtils;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.CustomProgressDialog;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/5/6.
 */
public class ThreeFragment extends BaseFragment implements View.OnClickListener ,OnClickContentItemListener{
    private View view;
    private Resources res;

    private PullToRefreshListView listView;
    private ImageView no_collections;
    private ItemTvAdapter adapter;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private List<Videos> list = new ArrayList<Videos>();
    private String emp_id = "";//当前登陆者UUID

    //导航
    private ViewPager viewpager;
    private AdViewPagerAdapter adapterAd;
    private LinearLayout viewGroup;
    private ImageView dot, dots[];
    private Runnable runnable;
    private int autoChangeTime = 5000;
    private List<AdObj> listsAd = new ArrayList<AdObj>();
    private LinearLayout headLiner;

    boolean isMobileNet, isWifiNet;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
        emp_id = getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.three_fragment, null);
        res = getActivity().getResources();
        emp_id = getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class);
        initView();
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
            if (!isMobileNet && !isWifiNet) {
                list.addAll(DBHelper.getInstance(getActivity()).getVideos());
                if(list.size() > 0){
                    no_collections.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }else {
                    no_collections.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
                List<AdObj> adObjs = DBHelper.getInstance(getActivity()).getAdObjs();
                if(adObjs != null){
                    listsAd.addAll(adObjs);
                    initViewPager();
                }
            }else {
                initData();
                getAd();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }




    void initView(){
        //初始化
        headLiner = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.ad_header, null);
        no_collections = (ImageView) view.findViewById(R.id.no_record);
        listView = (PullToRefreshListView) view.findViewById(R.id.lstv);

        ListView lst = listView.getRefreshableView();

        lst.addHeaderView(headLiner);

        adapter = new ItemTvAdapter(list, getActivity());
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                //判断是否有网
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
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
                String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                //判断是否有网
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(list.size()>(position - 1)){
                    Videos tmpVideos = list.get(position - 1);
                    Intent detailView = new Intent(getActivity(), DetailTvActivity.class);
                    detailView.putExtra(Constants.INFO, tmpVideos);
                    startActivity(detailView);
                }

            }
        });
        no_collections.setOnClickListener(this);
        adapter.setOnClickContentItemListener(this);
    }
    @Override
    public void onClick(View view) {
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
            if (!isMobileNet && !isWifiNet) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       switch (view.getId()){
           case R.id.no_record:
               IS_REFRESH = true;
               pageIndex = 1;
               initData();
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
                                listView.onRefreshComplete();
                                adapter.notifyDataSetChanged();

                                //处理数据，需要的话保存到数据库
                                if (data != null && data.getData() != null) {
                                    DBHelper dbHelper = DBHelper.getInstance(getActivity());
                                    for (Videos videos : data.getData()) {
                                        if (dbHelper.getVideosById(videos.getId()) == null) {
                                            DBHelper.getInstance(getActivity()).saveVideos(videos);
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    private void getAd() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_AD_LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    AdObjData data = getGson().fromJson(s, AdObjData.class);
                                    listsAd.clear();
                                    if (data != null && data.getData().size() > 0) {
                                        listsAd.addAll(data.getData());
                                    }

                                    if(data != null && data.getData() != null){
                                        for(AdObj adObj:data.getData()){
                                            if(DBHelper.getInstance(getActivity()).getAdObjById(adObj.getMm_ad_id()) == null){
                                                DBHelper.getInstance(getActivity()).saveAdObj(adObj);
                                            }
                                        }
                                    }

                                    initViewPager();
                                } else {
                                    Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_ad_type", "0");
                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_provinceId", ""), String.class))) {
                    params.put("mm_emp_provinceId", getGson().fromJson(getSp().getString("mm_emp_provinceId", ""), String.class));
                }
                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_cityId", ""), String.class))) {
                    params.put("mm_emp_cityId", getGson().fromJson(getSp().getString("mm_emp_cityId", ""), String.class));
                }
                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_countryId", ""), String.class))) {
                    params.put("mm_emp_countryId", getGson().fromJson(getSp().getString("mm_emp_countryId", ""), String.class));
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
    private void initViewPager() {
        adapterAd = new AdViewPagerAdapter(getActivity());
        adapterAd.change(listsAd);
        adapterAd.setOnClickContentItemListener(this);
        viewpager = (ViewPager) headLiner.findViewById(R.id.viewpager);
        viewpager.setAdapter(adapterAd);
        viewpager.setOnPageChangeListener(myOnPageChangeListener);
        initDot();
        runnable = new Runnable() {
            @Override
            public void run() {
                int next = viewpager.getCurrentItem() + 1;
                if (next >= adapterAd.getCount()) {
                    next = 0;
                }
                viewHandler.sendEmptyMessage(next);
            }
        };
        viewHandler.postDelayed(runnable, autoChangeTime);
    }


    // 初始化dot视图
    private void initDot() {
        viewGroup = (LinearLayout) headLiner.findViewById(R.id.viewGroup);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                20, 20);
        layoutParams.setMargins(4, 3, 4, 3);

        dots = new ImageView[adapterAd.getCount()];
        for (int i = 0; i < adapterAd.getCount(); i++) {
            dot = new ImageView(getActivity());
            dot.setLayoutParams(layoutParams);
            dots[i] = dot;
            dots[i].setTag(i);
            dots[i].setOnClickListener(onClick);

            if (i == 0) {
                dots[i].setBackgroundResource(R.drawable.dotc);
            } else {
                dots[i].setBackgroundResource(R.drawable.dotn);
            }

            viewGroup.addView(dots[i]);
        }
    }

    ViewPager.OnPageChangeListener myOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            setCurDot(arg0);
            viewHandler.removeCallbacks(runnable);
            viewHandler.postDelayed(runnable, autoChangeTime);
        }

    };
    // 实现dot点击响应功能,通过点击事件更换页面
    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            setCurView(position);
        }

    };

    /**
     * 设置当前的引导页
     */
    private void setCurView(int position) {
        if (position < 0 || position > adapterAd.getCount()) {
            return;
        }
        viewpager.setCurrentItem(position);
//        if (!StringUtil.isNullOrEmpty(lists.get(position).getNewsTitle())){
//            titleSlide = lists.get(position).getNewsTitle();
//            if(titleSlide.length() > 13){
//                titleSlide = titleSlide.substring(0,12);
//                article_title.setText(titleSlide);//当前新闻标题显示
//            }else{
//                article_title.setText(titleSlide);//当前新闻标题显示
//            }
//        }

    }

    /**
     * 选中当前引导小点
     */
    private void setCurDot(int position) {
        for (int i = 0; i < dots.length; i++) {
            if (position == i) {
                dots[i].setBackgroundResource(R.drawable.dotc);
            } else {
                dots[i].setBackgroundResource(R.drawable.dotn);
            }
        }
    }

    /**
     * 每隔固定时间切换广告栏图片
     */
    @SuppressLint("HandlerLeak")
    private final Handler viewHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setCurView(msg.what);
        }

    };

    int tmpId = 0;
    Videos tmpVideos ;

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
            if (!isMobileNet && !isWifiNet) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String str = (String) object;
        if ("000".equals(str)) {
            switch (flag) {
                case 0:
                    AdObj adObj = listsAd.get(position);
                    if(!StringUtil.isNullOrEmpty(adObj.getMm_ad_url())){
//                        Intent webV = new Intent(getActivity(), WebViewActivity.class);
//                        webV.putExtra("strurl", adObj.getMm_ad_url());
//                        webV.putExtra("strname", "贵人");
//                        startActivity(webV);

                        final Uri uri = Uri.parse(adObj.getMm_ad_url());
                        final Intent it = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(it);
                    }
                    break;
            }
        }else {
            tmpVideos = list.get(position);
            tmpId = position;
            switch (flag){
                case 1:
                    //评论
                {
                    Intent comment = new Intent(getActivity(), PublishTvCommentAcitvity.class);
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
                    progressDialog =  new CustomProgressDialog(getActivity(), "正在加载中",R.anim.custom_dialog_frame);
                    progressDialog.setCancelable(true);
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();
                    zan_click(tmpVideos);
                }
                break;
                case 4:
                    //播放
                    String videoUrl = tmpVideos.getVideoUrl();
                    Intent intent = new Intent(getActivity(), VideoPlayerActivity2.class);
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

    }

    void share() {
        new ShareAction(getActivity()).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setShareboardclickCallback(shareBoardlistener)
                .open();
    }

    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            UMImage image = new UMImage(getActivity(), tmpVideos.getPicUrl());
            String title =  getGson().fromJson(getSp().getString("mm_emp_nickname", ""), String.class)+"邀您免费看电影" ;
            String content = tmpVideos.getTitle()+tmpVideos.getContent();
            new ShareAction(getActivity()).setPlatform(share_media).setCallback(umShareListener)
                    .withText(content)
                    .withTitle(title)
                    .withTargetUrl((InternetURL.SHARE_VIDEOS_TV + "?id=" + tmpVideos.getId()))
                    .withMedia(image)
                    .share();
        }
    };

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(getActivity(), platform + getResources().getString(R.string.share_success), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(getActivity(), platform + getResources().getString(R.string.share_error), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(getActivity(), platform + getResources().getString(R.string.share_cancel), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(getActivity()).onActivityResult(requestCode, resultCode, data);
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
                            if (Integer.parseInt(data.getCode()) == 200) {
                                //赞+1
                                Toast.makeText(getActivity(), R.string.zan_success, Toast.LENGTH_SHORT).show();
                                list.get(tmpId).setZanNum(String.valueOf(Integer.parseInt((list.get(tmpId).getZanNum() == null ? "0" : list.get(tmpId).getZanNum())) + 1));
                                adapter.notifyDataSetChanged();
                            }
                            if (Integer.parseInt(data.getCode()) == 1) {
                                Toast.makeText(getActivity(), R.string.zan_error_one, Toast.LENGTH_SHORT).show();

                            }
                            if (Integer.parseInt(data.getCode()) == 2) {
                                Toast.makeText(getActivity(), R.string.zan_error_two, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.zan_error_two, Toast.LENGTH_SHORT).show();

                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), R.string.zan_error_two, Toast.LENGTH_SHORT).show();
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
