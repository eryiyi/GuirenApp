package com.Lbins.GuirenApp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.AnimateFirstDisplayListener;
import com.Lbins.GuirenApp.adapter.HelpViewPagerAdapter;
import com.Lbins.GuirenApp.adapter.OnClickContentItemListener;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.huanxin.ui.ChatActivity;
import com.Lbins.GuirenApp.module.HelpObj;
import com.Lbins.GuirenApp.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhl on 2016/12/26.
 */
public class DetailHelpActivity extends BaseActivity implements View.OnClickListener,OnClickContentItemListener {
    private TextView back;
    private View view;
    private Resources res;

    //导航
    private ViewPager viewpager;
    private HelpViewPagerAdapter adapterAd;
    private LinearLayout viewGroup;
    private ImageView dot, dots[];
    private Runnable runnable;
    private int autoChangeTime = 5000;
    private List<String> listsAd = new ArrayList<String>();

    boolean isMobileNet, isWifiNet;
    private String emp_id = "";//当前登陆者UUID

    private ImageView cover;
    private TextView name;
    private TextView typeName;
    private TextView money;
    private TextView title;
    private TextView content;

    private HelpObj helpObj;
    private TextView btn_baijian;
    private TextView btn_chat;

    private LinearLayout liner_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_help_activity);
        res = this.getResources();
        emp_id = getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class);
        helpObj = (HelpObj) getIntent().getExtras().get("helpObj");
        if(helpObj != null){
            if(!StringUtil.isNullOrEmpty(helpObj.getHelp_pic())){
                String[]  arras= helpObj.getHelp_pic().split(",");
                if(arras != null){
                    for(String str:arras){
                        listsAd.add(str);
                    }
                }
            }
        }
        initView();

        initViewPager();

        initData();
    }
    private void initView() {
        back = (TextView) this.findViewById(R.id.back);
        back.setOnClickListener(this);
        cover = (ImageView) this.findViewById(R.id.cover);
        name = (TextView) this.findViewById(R.id.name);
        typeName = (TextView) this.findViewById(R.id.typeName);
        money = (TextView) this.findViewById(R.id.money);
        title = (TextView) this.findViewById(R.id.title);
        content = (TextView) this.findViewById(R.id.content);

        btn_chat = (TextView) this.findViewById(R.id.btn_chat);
        btn_baijian = (TextView) this.findViewById(R.id.btn_baijian);
        btn_baijian.setOnClickListener(this);
        btn_chat.setOnClickListener(this);

        cover.setOnClickListener(this);
        name.setOnClickListener(this);
        liner_bottom = (LinearLayout) this.findViewById(R.id.liner_bottom);

    }

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    void initData(){
        imageLoader.displayImage(helpObj.getMm_emp_cover(), cover, GuirenApplication.txOptions, animateFirstListener);
        name.setText(helpObj.getMm_emp_nickname());
        typeName.setText(helpObj.getHelp_type_name());
        money.setText("￥"+helpObj.getHelp_money());
        title.setText(helpObj.getHelp_title());
        content.setText(helpObj.getHelp_content());
        if(emp_id.equals(helpObj.getMm_emp_id())){
            liner_bottom.setVisibility(View.GONE);
        }else {
            liner_bottom.setVisibility(View.VISIBLE);
        }

    }

    private void initViewPager() {
        adapterAd = new HelpViewPagerAdapter(this);
        adapterAd.change(listsAd);
        adapterAd.setOnClickContentItemListener(this);
        viewpager = (ViewPager) this.findViewById(R.id.viewpager);
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
        viewGroup = (LinearLayout) this.findViewById(R.id.viewGroup);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                20, 20);
        layoutParams.setMargins(4, 3, 4, 3);

        dots = new ImageView[adapterAd.getCount()];
        for (int i = 0; i < adapterAd.getCount(); i++) {
            dot = new ImageView(this);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
            {
                finish();
            }
                break;
            case R.id.btn_chat:
            {
                //私聊
                Intent chatV = new Intent(DetailHelpActivity.this, ChatActivity.class);
                chatV.putExtra("userId", helpObj.getHxusername());
                chatV.putExtra("userName", helpObj.getMm_emp_nickname());
                startActivity(chatV);
            }
            break;
            case R.id.guirenlu:
            {
                //贵人录
                Intent intent = new Intent(DetailHelpActivity.this, TongxunluActivityT.class);
                intent.putExtra("mm_emp_id", helpObj.getMm_emp_id());
                startActivity(intent);
            }
            break;
            case R.id.cover:
            case R.id.name:
            case R.id.btn_baijian:
            {
                Intent intent = new Intent(DetailHelpActivity.this, ProfileActivity.class);
                intent.putExtra("mm_emp_id", helpObj.getMm_emp_id());
                startActivity(intent);
            }
            break;
        }
    }

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        //判断是否有网
//        try {
//            isMobileNet = GuirenHttpUtils.isMobileDataEnable(this);
//            isWifiNet = GuirenHttpUtils.isWifiDataEnable(this);
//            if (!isMobileNet && !isWifiNet) {
//                return;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String str = (String) object;
//        if ("000".equals(str)) {
//            switch (flag) {
//                case 0:
//                    String adObj = listsAd.get(position);
//                    if(!StringUtil.isNullOrEmpty(adObj)){
//                        final Uri uri = Uri.parse(adObj);
//                        final Intent it = new Intent(Intent.ACTION_VIEW, uri);
//                        startActivity(it);
//                    }
//                    break;
//            }
//        }
    }
}
