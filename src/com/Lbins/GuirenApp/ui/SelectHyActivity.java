package com.Lbins.GuirenApp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.HangYeTypeDara;
import com.Lbins.GuirenApp.fragment.Fragment_pro_type;
import com.Lbins.GuirenApp.module.HangYeType;
import com.Lbins.GuirenApp.util.GuirenHttpUtils;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.CustomProgressDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/5/18.
 */
public class SelectHyActivity extends BaseActivity implements View.OnClickListener {
    private final static int SCANNIN_GREQUEST_CODE = 1;

    private ArrayList<HangYeType> lists = new ArrayList<HangYeType>();//存放二级分类
    private List<HangYeType> toolsList = new ArrayList<HangYeType>();//存放顶级分类
    private List<HangYeType> listAll = new ArrayList<HangYeType>();//存放所有的分类
    private TextView toolsTextViews[];
    private View views[];
    private LayoutInflater inflaters;
    private ScrollView scrollView;
    private int scrllViewWidth = 0, scrollViewMiddle = 0;
    private ViewPager shop_pager;
    private int currentItem=0;
    private ShopAdapter shopAdapter;


    private boolean progressShow;

    boolean isMobileNet, isWifiNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_hangye_activity);
        scrollView=(ScrollView) this.findViewById(R.id.tools_scrlllview);
        shopAdapter=new ShopAdapter(getSupportFragmentManager());
        inflaters=LayoutInflater.from(this);
        initView();
        //获得大类
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(SelectHyActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(SelectHyActivity.this);
            if (!isMobileNet && !isWifiNet) {
                showMsg(SelectHyActivity.this ,"请检查您网络链接");
            }else {
                progressDialog = new CustomProgressDialog(SelectHyActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                getBigType();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void back(View view){finish();}

    private void initView() {
    }

    public void onClick(View view) {
    }

    /**
     * 动态生成显示items中的textview
     */
    private void showToolsView() {
        LinearLayout toolsLayout=(LinearLayout) this.findViewById(R.id.tools);
        toolsTextViews=new TextView[toolsList.size()];
        views=new View[toolsList.size()];

        for (int i = 0; i < toolsList.size(); i++) {
            View view=inflaters.inflate(R.layout.item_b_top_nav_layout, null);
            view.setId(i);
            view.setOnClickListener(toolsItemListener);
            TextView textView=(TextView) view.findViewById(R.id.text);
            textView.setText(toolsList.get(i).getMm_hangye_name());
            toolsLayout.addView(view);
            toolsTextViews[i]=textView;
            views[i]=view;
        }
        changeTextColor(0);

        initPager();
    }

    private View.OnClickListener toolsItemListener =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shop_pager.setCurrentItem(v.getId());
        }
    };



    /**
     * initPager<br/>
     * 初始化ViewPager控件相关内容
     */
    private void initPager() {
        shop_pager=(ViewPager) this.findViewById(R.id.goods_pager);
        shop_pager.setAdapter(shopAdapter);
        shop_pager.setOnPageChangeListener(onPageChangeListener);

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * OnPageChangeListener<br/>
     * 监听ViewPager选项卡变化事的事件
     */

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            if(shop_pager.getCurrentItem()!=arg0)shop_pager.setCurrentItem(arg0);
            if(currentItem!=arg0){
                changeTextColor(arg0);
                changeTextLocation(arg0);
            }
            currentItem=arg0;
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    /**
     * ViewPager 加载选项卡
     * @author Administrator
     *
     */
    private class ShopAdapter extends FragmentPagerAdapter {
        public ShopAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int arg0) {
            Fragment fragment =new Fragment_pro_type();
            Bundle bundle=new Bundle();
            HangYeType hangYeType=toolsList.get(arg0);//当前选中的顶级分类
            lists.clear();
            for(HangYeType hangYeType1 : listAll){
                if(hangYeType1.getMm_hangye_fid().equals(hangYeType.getMm_hangye_id())){
                    //符合条件的二级分类
                    lists.add(hangYeType1);
                }
            }
            bundle.putSerializable("lists", lists);
            bundle.putSerializable("hangYeType", hangYeType);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return toolsList.size();
        }
    }

    /**
     * 改变textView的颜色
     * @param id
     */
    private void changeTextColor(int id) {
        for (int i = 0; i < toolsTextViews.length; i++) {
            if(i!=id){
                toolsTextViews[i].setBackgroundResource(android.R.color.transparent);
                toolsTextViews[i].setTextColor(0xff000000);
            }
        }
        toolsTextViews[id].setBackgroundResource(android.R.color.white);
        toolsTextViews[id].setTextColor(0xffff5d5e);
    }


    /**
     * 改变栏目位置
     *
     * @param clickPosition
     */
    private void changeTextLocation(int clickPosition) {
        int x = (views[clickPosition].getTop() - getScrollViewMiddle() + (getViewheight(views[clickPosition]) / 2));
        scrollView.smoothScrollTo(0, x);
    }

    /**
     * 返回scrollview的中间位置
     *
     * @return
     */
    private int getScrollViewMiddle() {
        if (scrollViewMiddle == 0)
            scrollViewMiddle = getScrollViewheight() / 2;
        return scrollViewMiddle;
    }

    /**
     * 返回ScrollView的宽度
     *
     * @return
     */
    private int getScrollViewheight() {
        if (scrllViewWidth == 0)
            scrllViewWidth = scrollView.getBottom() - scrollView.getTop();
        return scrllViewWidth;
    }

    /**
     * 返回view的宽度
     *
     * @param view
     * @return
     */
    private int getViewheight(View view) {
        return view.getBottom() - view.getTop();
    }

    //获得类别
    private void getBigType() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_HY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code1 =  jo.getString("code");
                                if(Integer.parseInt(code1) == 200){
                                    HangYeTypeDara data = getGson().fromJson(s, HangYeTypeDara.class);
                                    listAll.clear();
                                    listAll.addAll(data.getData());
                                    toolsList.clear();
                                    if(listAll != null){
                                        for(HangYeType hangYeType:listAll){
                                            if(StringUtil.isNullOrEmpty(hangYeType.getMm_hangye_fid())){
                                                //如果没有fid 说明是顶级分类
                                                toolsList.add(hangYeType);
                                            }
                                        }
                                    }
                                    showToolsView();
                                }else {
                                Toast.makeText(SelectHyActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(SelectHyActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(SelectHyActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    //显示扫描到的内容
//                    mTextView.setText(bundle.getString("result"));
                    String result = bundle.getString("result");

                    //显示
                    // mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
                }
                break;
        }
    }
}
