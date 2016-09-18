package com.Lbins.GuirenApp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.NewsFragmentPagerAdapter;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.VideoTypeObjData;
import com.Lbins.GuirenApp.fragment.VideosFragment;
import com.Lbins.GuirenApp.module.VideoTypeObj;
import com.Lbins.GuirenApp.util.BaseTools;
import com.Lbins.GuirenApp.util.GuirenHttpUtils;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.ColumnHorizontalScrollView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/15.
 */
public class VideosSearchActivity extends BaseActivity implements View.OnClickListener {
    boolean isMobileNet, isWifiNet;

    private List<VideoTypeObj> listDianying = new ArrayList<VideoTypeObj>();
    private ColumnHorizontalScrollView mColumnHorizontalScrollView;
    LinearLayout mRadioGroup_content;
    LinearLayout ll_more_columns;
    RelativeLayout rl_column;

    private ViewPager mViewPager;
    private ImageView button_more_columns;
    private int columnSelectIndex = 0;
    public ImageView shade_left;
    public ImageView shade_right;
    private int mScreenWidth = 0;
    private int mItemWidth = 0;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videos_search_activity);
        mScreenWidth = BaseTools.getWindowsWidth(this);
        mItemWidth = mScreenWidth / 7;
        initView();
        initColumnData();
    }

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        mColumnHorizontalScrollView = (ColumnHorizontalScrollView) findViewById(R.id.mColumnHorizontalScrollView);
        mRadioGroup_content = (LinearLayout) findViewById(R.id.mRadioGroup_content);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
    }

    @Override
    public void onClick(View view) {
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(VideosSearchActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(VideosSearchActivity.this);
            if (!isMobileNet && !isWifiNet) {
                showMsg(VideosSearchActivity.this, "请检查网络链接");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (view.getId()){
//            case R.id.liner_one:
//                progressDialog = new CustomProgressDialog(VideosSearchActivity.this, "正在加载中",R.anim.custom_dialog_frame);
//
//                progressDialog.setCancelable(true);
//                progressDialog.setIndeterminate(true);
//                progressDialog.show();
//                IS_REFRESH = true;
//                pageIndex = 1;
//                time_is = "1";
//                favour_is = "0";
//                initData();
//                break;
//            case R.id.liner_two:
//                progressDialog = new CustomProgressDialog(VideosSearchActivity.this, "正在加载中",R.anim.custom_dialog_frame);
//
//                progressDialog.setCancelable(true);
//                progressDialog.setIndeterminate(true);
//                progressDialog.show();
//                IS_REFRESH = true;
//                pageIndex = 1;
//                favour_is = "1";
//                time_is = "0";
//                initData();
//                break;
            case R.id.back:
                finish();
                break;
        }
    }

    //获得电影类别
    public void setData(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_DIANYING_TYPES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            VideoTypeObjData data = getGson().fromJson(s, VideoTypeObjData.class);
                            if (Integer.parseInt(data.getCode())== 200) {
                                listDianying.clear();
                                listDianying.addAll(data.getData());
                                initTabColumn();
                                initFragment();
                            } else {
                                Toast.makeText(VideosSearchActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(VideosSearchActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(VideosSearchActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", "1");
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



    private void initColumnData() {
        //获得新闻类别
        setData();
    }

    private void initTabColumn() {
        mRadioGroup_content.removeAllViews();
        int count = listDianying.size();
        mColumnHorizontalScrollView.setParam(this, mScreenWidth, mRadioGroup_content, shade_left, shade_right, ll_more_columns, rl_column);
        for (int i = 0; i < count; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 20;
            params.rightMargin = 5;
            TextView columnTextView = new TextView(this);
            columnTextView.setTextAppearance(this, R.style.top_category_scroll_view_item_text);
            columnTextView.setBackgroundResource(R.drawable.radio_buttong_bg);
            columnTextView.setGravity(Gravity.CENTER);
            columnTextView.setPadding(5, 5, 5, 5);
            columnTextView.setId(i);
            columnTextView.setText(listDianying.get(i).getVideo_type_name());
            columnTextView.setTextColor(getResources().getColorStateList(R.color.top_category_scroll_text_color_day));
            if (columnSelectIndex == i) {
                columnTextView.setSelected(true);
            }
            columnTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
                        View localView = mRadioGroup_content.getChildAt(i);
                        if (localView != v)
                            localView.setSelected(false);
                        else {
                            localView.setSelected(true);
                            mViewPager.setCurrentItem(i);
                        }
                    }
//                    Toast.makeText(getApplicationContext(), newsClassify.get(v.getId()).getName(), Toast.LENGTH_SHORT).show();
                }
            });
            mRadioGroup_content.addView(columnTextView, i, params);
        }
    }

    private void selectTab(int tab_postion) {
        columnSelectIndex = tab_postion;
        for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
            View checkView = mRadioGroup_content.getChildAt(tab_postion);
            int k = checkView.getMeasuredWidth();
            int l = checkView.getLeft();
            int i2 = l + k / 2 - mScreenWidth / 2;
            mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
        }
        for (int j = 0; j < mRadioGroup_content.getChildCount(); j++) {
            View checkView = mRadioGroup_content.getChildAt(j);
            boolean ischeck;
            if (j == tab_postion) {
                ischeck = true;
            } else {
                ischeck = false;
            }
            checkView.setSelected(ischeck);
        }
    }


    private void initFragment() {
        int count = listDianying.size();
        for (int i = 0; i < count; i++) {
            Bundle data = new Bundle();
            data.putString("id", listDianying.get(i).getVideo_type_id());
            data.putString("name", listDianying.get(i).getVideo_type_name());
            VideosFragment newfragment = new VideosFragment();
            newfragment.setArguments(data);
            fragments.add(newfragment);
        }
        NewsFragmentPagerAdapter mAdapetr = new NewsFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setAdapter(mAdapetr);
        mViewPager.setOnPageChangeListener(pageListener);
    }

    /**
     */
    public ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            mViewPager.setCurrentItem(position);
            selectTab(position);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }



}
