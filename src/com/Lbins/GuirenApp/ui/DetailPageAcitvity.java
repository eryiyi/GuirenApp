package com.Lbins.GuirenApp.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.*;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.CommentContentDATA;
import com.Lbins.GuirenApp.data.FavoursDATA;
import com.Lbins.GuirenApp.data.RecordDATA;
import com.Lbins.GuirenApp.data.SuccessData;
import com.Lbins.GuirenApp.face.FaceConversionUtil;
import com.Lbins.GuirenApp.library.PullToRefreshBase;
import com.Lbins.GuirenApp.library.PullToRefreshListView;
import com.Lbins.GuirenApp.module.CommentContent;
import com.Lbins.GuirenApp.module.Favour;
import com.Lbins.GuirenApp.module.Record;
import com.Lbins.GuirenApp.util.Constants;
import com.Lbins.GuirenApp.util.PicUtil;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.CustomProgressDialog;
import com.Lbins.GuirenApp.widget.DeletePopWindow;
import com.Lbins.GuirenApp.widget.PictureGridview;
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
 * author: ${zhanghailong}
 * Date: 2015/1/31
 * Time: 14:36
 * 类的功能、说明写在此处.
 */
public class DetailPageAcitvity extends BaseActivity implements View.OnClickListener, OnClickContentItemListener {
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    List<CommentContent> commentContents;
    private Record record;//传参
    private PullToRefreshListView detail_lstv;
    private ImageView detail_back;//返回按钮
    private ImageView detail_share;//分享按钮
    private TextView detail_title;//标题
    private LinearLayout detail_like_liner;//赞区域
    private LinearLayout detail_comment_liner;//评论区域
    private LinearLayout detail_report_liner;//分享区域
    private LinearLayout detail_delete_liner;//删除区域

    private LinearLayout commentLayout;//头部
    private ImageView detail_photo;//头像
    private TextView detail_nickname;//昵称
    private TextView detail_time;//时间
    private TextView home_item_school;//学校
    private TextView detail_content;//详细内容
    private PictureGridview detail_grideview;//grideview
    private ImageView detail_video_pic;//视频图片
    private ImageView detail_player_icon_video;//视频播放按钮
    private ImageView picone;//单一图片的时候使用

    private ImageView detail_ad_image;//广告图片
    private TextView pl_text;//评论数量
    private PullToRefreshListView detail_comment_lstv;//评论列表
    private DetailCommentAdapter adapter;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;

    private GridView gridView;
    private List<Favour> itemList = new ArrayList<Favour>();
    private DetailFavourAdapter adaptertwo;

    private List<Favour> itemListtwo = new ArrayList<Favour>();//赞列表用

    private RelativeLayout detail_like_liner_layout;//赞区域
//    private String schoolId = "";
//    private String emp_type = "";
    private String emp_id = "";//当前登陆者UUID

//    private Ad ad;

//    UMSocialService mController;
//    String shareCont = "";//内容
//    String shareUrl = InternetURL.SHARE_RECORD;
//    String sharePic = "";//分享图片
//    String shareParams = "";
//    String appID = "wx198fc23a0fae697a";
//    private DeletePopWindow deleteWindow;

    private DeletePopWindow deleteWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.detail_page);
        registerBoradcastReceiver();
        record = (Record) getIntent().getExtras().get(Constants.INFO);//传递过来的值
        commentContents = new ArrayList<CommentContent>();
        emp_id = getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class);
        initView();
        initData();

        detail_grideview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    private void initView() {
        commentLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.detail_header, null);
        detail_back = (ImageView) this.findViewById(R.id.detail_back);
        detail_back.setOnClickListener(this);
        detail_share = (ImageView) this.findViewById(R.id.detail_share);
        detail_share.setOnClickListener(this);
        detail_title = (TextView) this.findViewById(R.id.detail_title);
        detail_lstv = (PullToRefreshListView) this.findViewById(R.id.detail_lstv);
        detail_like_liner = (LinearLayout) this.findViewById(R.id.detail_like_liner);
        detail_comment_liner = (LinearLayout) this.findViewById(R.id.detail_comment_liner);
        detail_report_liner = (LinearLayout) this.findViewById(R.id.detail_report_liner);
        detail_delete_liner = (LinearLayout) this.findViewById(R.id.detail_delete_liner);
        detail_report_liner.setOnClickListener(this);
        detail_comment_liner.setOnClickListener(this);
        detail_like_liner.setOnClickListener(this);
        detail_delete_liner.setOnClickListener(this);

//        if (emp_type.equals("1")) {
//            //是管理员
//            detail_delete_liner.setVisibility(View.VISIBLE);
//            detail_report_liner.setVisibility(View.GONE);
//        }
        detail_delete_liner.setVisibility(View.VISIBLE);
        detail_report_liner.setVisibility(View.GONE);
        detail_photo = (ImageView) commentLayout.findViewById(R.id.detail_photo);
        detail_photo.setOnClickListener(this);
        detail_nickname = (TextView) commentLayout.findViewById(R.id.detail_nickname);
        detail_nickname.setOnClickListener(this);
        detail_time = (TextView) commentLayout.findViewById(R.id.detail_time);
        home_item_school= (TextView) commentLayout.findViewById(R.id.home_item_school);
        detail_content = (TextView) commentLayout.findViewById(R.id.detail_content);
        detail_content.setVisibility(View.GONE);
        detail_grideview = (PictureGridview) commentLayout.findViewById(R.id.gridview_detail_picture);
        detail_video_pic = (ImageView) commentLayout.findViewById(R.id.detail_video_pic);
        detail_player_icon_video = (ImageView) commentLayout.findViewById(R.id.detail_player_icon_video);
        picone = (ImageView) commentLayout.findViewById(R.id.picone);
        detail_player_icon_video.setOnClickListener(this);

//        commentLayoutfoot = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.detail_foot, null);
        detail_ad_image = (ImageView) commentLayout.findViewById(R.id.detail_ad_image);
        detail_ad_image.setOnClickListener(this);

        pl_text = (TextView) commentLayout.findViewById(R.id.pl_text);
//        detail_comment_lstv = (PullToRefreshListView) commentLayout.findViewById(R.id.detail_comment_lstv);

        detail_grideview.setVisibility(View.GONE);
        detail_video_pic.setVisibility(View.GONE);
        picone.setVisibility(View.GONE);
        detail_player_icon_video.setVisibility(View.GONE);

        adapter = new DetailCommentAdapter(this, commentContents);
        adapter.setOnClickContentItemListener(this);
        ListView listView = detail_lstv.getRefreshableView();

        listView.addHeaderView(commentLayout);
        detail_lstv.setMode(PullToRefreshBase.Mode.BOTH);

        detail_lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                loadData();
            }
        });

        detail_lstv.setAdapter(adapter);

        detail_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommentContent commentContent = (CommentContent) parent.getAdapter().getItem(position);
                if (commentContent != null) {
                    Intent comment = new Intent(DetailPageAcitvity.this, PublishCommentAcitvity.class);
                    comment.putExtra(Constants.FATHER_PERSON_NAME, commentContent.getNickName());
                    comment.putExtra(Constants.FATHER_UUID, commentContent.getId());
                    comment.putExtra(Constants.RECORD_UUID, record.getMm_msg_id());
                    comment.putExtra(Constants.FATHER_PERSON_UUID, record.getMm_emp_id());
                    comment.putExtra("fplempid", commentContent.getEmpId());
                    startActivity(comment);
                }
            }
        });


        detail_like_liner_layout = (RelativeLayout) commentLayout.findViewById(R.id.detail_like_liner_layout);
        detail_like_liner_layout.setVisibility(View.GONE);

        gridView = (GridView) commentLayout.findViewById(R.id.gridView);


    }

    private void initData() {
        imageLoader.displayImage(record.getMm_emp_cover(), detail_photo, GuirenApplication.txOptions, animateFirstListener);
        detail_nickname.setText(record.getMm_emp_nickname());
        detail_time.setText(record.getDateline());
        home_item_school.setText(record.getHangye());
        String urlStr = "  >>网页链接";
        if (!StringUtil.isNullOrEmpty(record.getMm_msg_content())) {
            detail_content.setVisibility(View.VISIBLE);
            String strcont = record.getMm_msg_content();//内容
            int textsize = (int) detail_content.getTextSize();
            textsize = StringUtil.dp2px(getBaseContext(), textsize+25);
            if (strcont.contains("http")) {
                //如果包含http
                String strhttp = strcont.substring(strcont.indexOf("http"), strcont.length());
                strcont = strcont.replaceAll(strhttp, "");
                detail_content.setText(FaceConversionUtil.getInstace().getExpressionString(DetailPageAcitvity.this, strcont + urlStr,textsize));
                detail_content.setOnClickListener(this);
            }else {
                detail_content.setText(FaceConversionUtil.getInstace().getExpressionString(DetailPageAcitvity.this, strcont,textsize));
            }
//            detail_content.setText(FaceConversionUtil.getInstace().getExpressionString(DetailPageAcitvity.this, record.getRecordCont()));
        }
        pl_text.setText("评论  " + record.getPlNum());
        //说说并且图片要有
        if ( !StringUtil.isNullOrEmpty(record.getMm_msg_picurl())) {
            final String[] pics;
            if (record.getMm_msg_picurl().contains(",")) {
                //对账图片
                pics = record.getMm_msg_picurl().split(",");
                if(pics != null && pics.length >0){
                    detail_grideview.setAdapter(new ImageGridViewAdapter(pics, this));
                    detail_grideview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            startImageActivity(pics, position);
                        }
                    });
                    detail_grideview.setVisibility(View.VISIBLE);
                    picone.setVisibility(View.GONE);
                }
                else{
                    detail_grideview.setVisibility(View.GONE);
                    picone.setVisibility(View.VISIBLE);
                    imageLoader.displayImage(pics[0], picone, GuirenApplication.options, animateFirstListener);
                }

            } else {
                //单一图片
                pics = new String[]{record.getMm_msg_picurl()};
                detail_grideview.setVisibility(View.GONE);
                picone.setVisibility(View.VISIBLE);
                imageLoader.displayImage(pics[0], picone, GuirenApplication.options, animateFirstListener);
            }

//            picone.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    startImageActivity(pics, 0);
//                }
//            });

        }
        //视频
//        if (record.getRecordType().equals("2")) {
//            detail_video_pic.setVisibility(View.VISIBLE);
//            detail_player_icon_video.setVisibility(View.VISIBLE);
//            imageLoader.displayImage(record.getRecordPicUrl(), detail_video_pic, GuirenApplication.videofailed, animateFirstListener);
//        }
        //广告
//        getSmallAd();
        loadData();
        getFavour();

    }

    private void startImageActivity(String[] urls, int position) {
        if (!PicUtil.hasSDCard()) {
            Toast.makeText(this, R.string.sd_card_is_in, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(DetailPageAcitvity.this, GalleryUrlActivity.class);
        intent.putExtra(Constants.IMAGE_URLS, urls);
        intent.putExtra(Constants.IMAGE_POSITION, position);
        DetailPageAcitvity.this.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_back://返回按钮
                finish();
                break;
            case R.id.detail_share://分享按钮
            {
                share();
            }
                break;
            case R.id.detail_report_liner://举报
                showJubao();
                break;
            case R.id.detail_comment_liner://回复评论
                Intent comment = new Intent(DetailPageAcitvity.this, PublishCommentAcitvity.class);
                comment.putExtra(Constants.FATHER_PERSON_NAME, "");
                comment.putExtra(Constants.FATHER_UUID, "0");
                comment.putExtra(Constants.RECORD_UUID, record.getMm_msg_id());
                comment.putExtra(Constants.FATHER_PERSON_UUID, record.getMm_emp_id());
                comment.putExtra("fplempid", "");
                startActivity(comment);
                break;
            case R.id.detail_like_liner://点赞
                progressDialog = new CustomProgressDialog(DetailPageAcitvity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                detail_like_liner.setClickable(false);
                zan_click(record);
                break;
            case R.id.detail_photo://头像
                if (!emp_id.equals(record.getMm_emp_id())) {
                    Intent profile = new Intent(DetailPageAcitvity.this, ProfileActivity.class);
                    profile.putExtra("mm_emp_id", record.getMm_emp_id());
                    startActivity(profile);
                } else {
                    Intent profile = new Intent(DetailPageAcitvity.this, EditEmpActivity.class);
                    startActivity(profile);
                }
                break;
            case R.id.detail_nickname://昵称
                if (!emp_id.equals(record.getMm_emp_id())) {
                    Intent profile = new Intent(DetailPageAcitvity.this, ProfileActivity.class);
                    profile.putExtra("mm_emp_id", record.getMm_emp_id());
                    startActivity(profile);
                } else {
                    Intent profile = new Intent(DetailPageAcitvity.this, EditEmpActivity.class);
                    startActivity(profile);
                }
                break;
            case R.id.detail_player_icon_video://视频播放按钮
//                String videoUrl = record.getRecordVideo();
//                Intent intent = new Intent(DetailPageAcitvity.this, VideoPlayerActivity2.class);
//                VideoPlayer video = new VideoPlayer(videoUrl);
//                intent.putExtra(Constants.EXTRA_LAYOUT, "0");
//                intent.putExtra(VideoPlayer.class.getName(), video);
//                startActivity(intent);

                break;
            case R.id.detail_ad_image://广告图片
//                if (ad != null) {
//                    Intent webView = new Intent(DetailPageAcitvity.this, WebViewActivity.class);
//                    webView.putExtra("strurl", ad.getAdUrl());
//                    startActivity(webView);
//                }
                break;
            case R.id.detail_delete_liner:
                //删除
                showSelectImageDialog();
                break;
            case R.id.detail_content:
            {
                String strcont = record.getMm_msg_content();//内容
                if (strcont.contains("http")){
                    //如果包含http
                    String strhttp = strcont.substring(strcont.indexOf("http"), strcont.length());
                    Intent webView = new Intent(DetailPageAcitvity.this, WebViewActivity.class);
                    webView.putExtra("strurl", strhttp);
                    webView.putExtra("strname", "信息详情");
                    startActivity(webView);
                }
            }
                break;

        }
    }


    void share() {
        new ShareAction(DetailPageAcitvity.this).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setShareboardclickCallback(shareBoardlistener)
                .open();
    }

    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            UMImage image = new UMImage(DetailPageAcitvity.this, record.getMm_emp_cover());
            String title =  record.getMm_emp_nickname()+"邀请您加入贵人" ;
            String content = record.getMm_msg_content()==null?"贵人，无处不在":record.getMm_msg_content();
            new ShareAction(DetailPageAcitvity.this).setPlatform(share_media).setCallback(umShareListener)
                    .withText(content)
                    .withTitle(title)
                    .withTargetUrl((InternetURL.SHARE_RECORD_URL + "?id=" + record.getMm_msg_id()))
                    .withMedia(image)
                    .share();
        }
    };

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(DetailPageAcitvity.this, platform + getResources().getString(R.string.share_success), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(DetailPageAcitvity.this, platform + getResources().getString(R.string.share_error), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(DetailPageAcitvity.this, platform + getResources().getString(R.string.share_cancel), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        UMShareAPI.get(DetailPageAcitvity.this).onActivityResult(requestCode, resultCode, data);
    }


    private void loadData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_DETAIL_PL_URL,
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
                                 detail_lstv.onRefreshComplete();
                                 detail_lstv.setVisibility(View.VISIBLE);
                                 adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(DetailPageAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailPageAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailPageAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("recordId", record.getMm_msg_id());
                params.put("page", String.valueOf(pageIndex));
                params.put("pageSize", "5");
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
                InternetURL.GET_FAVOUR_URL,
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

                                adaptertwo = new DetailFavourAdapter(itemList, DetailPageAcitvity.this , itemListtwo.size());
                                gridView.setAdapter(adaptertwo);
                                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                            Intent favour = new Intent(DetailPageAcitvity.this, DetailFavourActivity.class);
                                            favour.putExtra(Constants.RECORD_UUID, record.getMm_msg_id());
                                            startActivity(favour);

                                    }
                                });
                                if (itemList.size() > 0) {//当存在赞数据的时候
                                    detail_like_liner_layout.setVisibility(View.VISIBLE);
                                }
                                adaptertwo.notifyDataSetChanged();
                            } else {
                                Toast.makeText(DetailPageAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailPageAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailPageAcitvity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("recordId", record.getMm_msg_id());
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
        comt = commentContents.get(position);
        switch (flag) {
            case 1:
                if (!emp_id.equals(comt.getEmpId())) {
                    Intent mine = new Intent(DetailPageAcitvity.this, ProfileActivity.class);
                    mine.putExtra("mm_emp_id", comt.getEmpId());
                    startActivity(mine);
                } else {
                    Intent mine = new Intent(DetailPageAcitvity.this, EditEmpActivity.class);
                    startActivity(mine);
                }
                break;
        }
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
                                Toast.makeText(DetailPageAcitvity.this, R.string.zan_success, Toast.LENGTH_SHORT).show();
                                //调用广播，刷新详细页评论
                                Intent intent1 = new Intent(Constants.SEND_FAVOUR_SUCCESS);
                                sendBroadcast(intent1);
                            }
                            if (Integer.parseInt(data.getCode()) == 1) {
                                Toast.makeText(DetailPageAcitvity.this, R.string.zan_error_one, Toast.LENGTH_SHORT).show();
                                detail_like_liner.setClickable(true);
                            }
                            if (Integer.parseInt(data.getCode()) == 2) {
                                Toast.makeText(DetailPageAcitvity.this, R.string.zan_error_two, Toast.LENGTH_SHORT).show();
                                detail_like_liner.setClickable(true);
                            }
                        } else {
                            Toast.makeText(DetailPageAcitvity.this, R.string.zan_error_two, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(DetailPageAcitvity.this, R.string.zan_error_two, Toast.LENGTH_SHORT).show();
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

    // 举报
    private void showJubao() {
        final Dialog picAddDialog = new Dialog(DetailPageAcitvity.this, R.style.dialog);
        View picAddInflate = View.inflate(this, R.layout.jubao_dialog, null);
        TextView jubao_sure = (TextView) picAddInflate.findViewById(R.id.jubao_sure);
        final EditText jubao_cont = (EditText) picAddInflate.findViewById(R.id.jubao_cont);
        //举报提交
        jubao_sure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String contreport = jubao_cont.getText().toString();
                if (StringUtil.isNullOrEmpty(contreport)) {
                    Toast.makeText(DetailPageAcitvity.this, R.string.report_answer, Toast.LENGTH_LONG).show();
                    return;
                }
                report(contreport);
                picAddDialog.dismiss();
            }
        });

        //举报取消
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

    public void report(final String contReport) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.ADD_REPORT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            FavoursDATA data = getGson().fromJson(s, FavoursDATA.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                Toast.makeText(DetailPageAcitvity.this, R.string.report_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DetailPageAcitvity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailPageAcitvity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailPageAcitvity.this, R.string.report_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("empOne", emp_id);
                params.put("empTwo", record.getMm_emp_id());
                params.put("typeId", Constants.REPORT_TYPE_ZERRO);
                params.put("cont", contReport);
                params.put("xxid", record.getMm_msg_id());
//                params.put("schoolId", schoolId);
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
            if (action.equals(Constants.SEND_COMMENT_SUCCESS)) {
                //刷新内容
                pageIndex = 1;
                loadData();
            }
            if (action.equals(Constants.SEND_FAVOUR_SUCCESS)) {
                getFavour();
            }
        }

    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.SEND_COMMENT_SUCCESS);//评论成功，刷新评论列表
        myIntentFilter.addAction(Constants.SEND_FAVOUR_SUCCESS);//点赞成功，刷新赞列表
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    // 选择是否删除
    private void showSelectImageDialog() {
        deleteWindow = new DeletePopWindow(DetailPageAcitvity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(DetailPageAcitvity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
                                Toast.makeText(DetailPageAcitvity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                                //调用广播，刷新主页
                                Intent intent1 = new Intent(Constants.SEND_DELETE_RECORD_SUCCESS);
                                intent1.putExtra("recordId",record.getMm_msg_id());
                                sendBroadcast(intent1);
                                finish();
                            } else {
                                Toast.makeText(DetailPageAcitvity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailPageAcitvity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailPageAcitvity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("recordId", record.getMm_msg_id());
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
