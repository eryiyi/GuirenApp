package com.Lbins.GuirenApp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.*;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.YaoqingCardAdapter;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.YaoqingCardData;
import com.Lbins.GuirenApp.library.PullToRefreshBase;
import com.Lbins.GuirenApp.library.PullToRefreshListView;
import com.Lbins.GuirenApp.module.YaoqingCard;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/5/26.
 */
public class MineYqCardActivity  extends BaseActivity implements View.OnClickListener {
    private PullToRefreshListView listView;
    private ImageView no_collections;
    private YaoqingCardAdapter adapter;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private List<YaoqingCard> recordList = new ArrayList<YaoqingCard>();
    boolean isMobileNet, isWifiNet;
    private TextView btn_goumai;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_yqcard_activity);
        initView();
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(MineYqCardActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(MineYqCardActivity.this);
            if (!isMobileNet && !isWifiNet) {
                showMsg(MineYqCardActivity.this ,"请检查您网络链接");
            }else {
                progressDialog = new CustomProgressDialog(MineYqCardActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                initData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    YaoqingCard record;
    void initView(){
        this.findViewById(R.id.menu).setOnClickListener(this);
        btn_goumai = (TextView) this.findViewById(R.id.btn_goumai);
        btn_goumai.setOnClickListener(this);
        no_collections = (ImageView) this.findViewById(R.id.no_record);
        listView = (PullToRefreshListView) this.findViewById(R.id.lstv);
        adapter = new YaoqingCardAdapter(recordList, MineYqCardActivity.this);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                listView.onRefreshComplete();
//                IS_REFRESH = true;
//                pageIndex = 1;
//                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                listView.onRefreshComplete();
//                IS_REFRESH = false;
//                pageIndex++;
//                initData();
            }
        });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                record  = recordList.get(position - 1);
                if("0".equals(record.getIs_use())){
                    share();
                }else {
                    showMsg(MineYqCardActivity.this, "邀请码已经使用！换个试试");
                }
            }
        });
    }

    void share() {
        new ShareAction(MineYqCardActivity.this).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setShareboardclickCallback(shareBoardlistener)
                .open();
    }

    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            UMImage image = new UMImage(MineYqCardActivity.this, R.drawable.ic_launcher);
            String title =  (record.getMm_emp_nickname()==null?"":record.getMm_emp_nickname()) +"邀请您加入贵人" ;
            String content = "邀请码：" + record.getGuiren_card_num();
            new ShareAction(MineYqCardActivity.this).setPlatform(share_media).setCallback(umShareListener)
                    .withText(content)
                    .withTitle(title)
                    .withTargetUrl((InternetURL.SHARE_YAOQING_CARD_URL + "?id=" + record.getGuiren_card_id()))
                    .withMedia(image)
                    .share();
        }
    };

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(MineYqCardActivity.this, platform + getResources().getString(R.string.share_success), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(MineYqCardActivity.this, platform + getResources().getString(R.string.share_error), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(MineYqCardActivity.this, platform + getResources().getString(R.string.share_cancel), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        UMShareAPI.get(MineYqCardActivity.this).onActivityResult(requestCode, resultCode, data);
    }


    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_YAOQING_CARD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            YaoqingCardData data = getGson().fromJson(s, YaoqingCardData.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
//                                if (IS_REFRESH) {
                                    recordList.clear();
//                                }
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
                            } else {
                                Toast.makeText(MineYqCardActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineYqCardActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MineYqCardActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_emp_id", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
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
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(MineYqCardActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(MineYqCardActivity.this);
            if (!isMobileNet && !isWifiNet) {
                showMsg(MineYqCardActivity.this, "请检查网络链接");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (v.getId()){
            case R.id.menu:
                finish();
                break;
            case R.id.btn_goumai:
                //购买
                Intent orderV = new Intent(MineYqCardActivity.this, OrderMakeActivity.class);
                startActivity(orderV);
                break;
        }
    }

}
