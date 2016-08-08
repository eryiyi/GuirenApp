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
import com.Lbins.GuirenApp.util.StringUtil;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.Log;
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

    private TextView btn_goumai;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_yqcard_activity);
        initView();
        initData();
    }

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
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                initData();
            }
        });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                YaoqingCard record = recordList.get(position - 1);
                if("0".equals(record.getIs_use())){
                    //否
//                    holder.card_btn.setText("已使用");
                }else {
//                    holder.card_btn.setText("点击分享给好友");
                    share(record);
                }
            }
        });
    }

//    private String guiren_card_id;
//    private String guiren_card_num;
//    private String mm_emp_id;
//    private String is_use;
//    private String y_mm_emp_id;
//    private String mm_emp_nickname;
//    private String mm_emp_mobile;
//    private String mm_emp_nickname_y;
//    private String mm_emp_mobile_y;

    YaoqingCard recordVOT = null;
    void share(YaoqingCard recordVO) {
        recordVOT = recordVO;
        new ShareAction(MineYqCardActivity.this).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setShareboardclickCallback(shareBoardlistener)
                .open();
    }

    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            UMImage image = new UMImage(MineYqCardActivity.this, R.drawable.ic_launcher);
            String title = recordVOT.getMm_emp_nickname_y() +"邀请您加入贵人，邀请码：";
            String content = (recordVOT.getGuiren_card_num()==null?"":"暂无");
            new ShareAction(MineYqCardActivity.this).setPlatform(share_media).setCallback(umShareListener)
                    .withText(content)
                    .withTitle(title)
                    .withTargetUrl((InternetURL.SHARE_YAOQING_CARD_URL + "?id=" + recordVOT.getGuiren_card_id()))
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
