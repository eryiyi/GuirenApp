package com.Lbins.GuirenApp.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.EmpRelateObjData;
import com.Lbins.GuirenApp.module.EmpRelateObj;
import com.Lbins.GuirenApp.util.GuirenHttpUtils;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.CustomProgressDialog;
import com.Lbins.GuirenApp.widget.PingYinUtil;
import com.Lbins.GuirenApp.widget.PinyinComparator;
import com.Lbins.GuirenApp.widget.SideBar;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.*;

/**
 * Created by zhl on 2016/7/14.
 */
public class TongxunluActivity extends BaseActivity implements View.OnClickListener {
    private ListView lvContact;
    private SideBar indexBar;
    private WindowManager mWindowManager;
    private TextView mDialogText;
    private ContactAdapter adapter;
    /**
     * 昵称
     */
    private static List<EmpRelateObj> nicks = new ArrayList<EmpRelateObj>();

    boolean isMobileNet, isWifiNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        setContentView(R.layout.tongxunlu_activity);
        initView();
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(TongxunluActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(TongxunluActivity.this);
            if (!isMobileNet && !isWifiNet) {
                showMsg(TongxunluActivity.this ,"请检查您网络链接");
            }else {
                progressDialog = new CustomProgressDialog(TongxunluActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void getData(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_MINE_CONTACTS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            EmpRelateObjData data = getGson().fromJson(s, EmpRelateObjData.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                nicks.clear();
                                nicks.addAll(data.getData());
                                adapter.notifyDataSetChanged();
                            }else {
                                showMsg(TongxunluActivity.this, getResources().getString(R.string.get_data_error));
                            }
                        }else {
                            showMsg(TongxunluActivity.this, getResources().getString(R.string.get_data_error));
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
                        showMsg(TongxunluActivity.this, getResources().getString(R.string.get_data_error));
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_emp_id1", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
                params.put("state", "1");
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
        }
    }
    void initView(){
        this.findViewById(R.id.back).setOnClickListener(this);
        lvContact = (ListView) this.findViewById(R.id.lvContact);
        adapter = new ContactAdapter(TongxunluActivity.this);
        lvContact.setAdapter(adapter);
        indexBar = (SideBar) this.findViewById(R.id.sideBar);
        indexBar.setListView(lvContact);
        mDialogText = (TextView) LayoutInflater.from(this).inflate(
                R.layout.list_position, null);
        mDialogText.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mWindowManager.addView(mDialogText, lp);
        indexBar.setTextView(mDialogText);
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TongxunluActivity.this, ProfileActivity.class);
                EmpRelateObj empRelateObj = nicks.get(position);
                intent.putExtra("mm_emp_id",empRelateObj.getMm_emp_id2());
                startActivity(intent);
            }
        });
    }

    static class ContactAdapter extends BaseAdapter implements SectionIndexer {
        private Context mContext;
        private List<EmpRelateObj> mNicks = new ArrayList<EmpRelateObj>();

        @SuppressWarnings("unchecked")
        public ContactAdapter(Context mContext) {
            this.mContext = mContext;
            this.mNicks = nicks;
            // 排序(实现了中英文混排)
            String[] arrays = new String[mNicks.size()];
            for(int i=0;i<mNicks.size();i++){
                arrays[i]=mNicks.get(i).getMm_emp_nickname();
            }
            Arrays.sort(arrays, new PinyinComparator());
        }

        @Override
        public int getCount() {
            return mNicks.size();
        }

        @Override
        public Object getItem(int position) {
            return mNicks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final String nickName = mNicks.get(position).getMm_emp_nickname();
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.contact_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tvCatalog = (TextView) convertView
                        .findViewById(R.id.contactitem_catalog);
                viewHolder.ivAvatar = (ImageView) convertView
                        .findViewById(R.id.contactitem_avatar_iv);
                viewHolder.tvNick = (TextView) convertView
                        .findViewById(R.id.contactitem_nick);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String catalog = PingYinUtil.converterToFirstSpell(nickName)
                    .substring(0, 1);
            if (position == 0) {
                viewHolder.tvCatalog.setVisibility(View.VISIBLE);
                viewHolder.tvCatalog.setText(catalog);
            } else {
                String lastCatalog = PingYinUtil.converterToFirstSpell(
                        mNicks.get(position - 1).getMm_emp_nickname()).substring(0, 1);
                if (catalog.equals(lastCatalog)) {
                    viewHolder.tvCatalog.setVisibility(View.GONE);
                } else {
                    viewHolder.tvCatalog.setVisibility(View.VISIBLE);
                    viewHolder.tvCatalog.setText(catalog);
                }
            }

            viewHolder.ivAvatar.setImageResource(R.drawable.ic_launcher);
            viewHolder.tvNick.setText(nickName);
            return convertView;
        }

        static class ViewHolder {
            TextView tvCatalog;// 目录
            ImageView ivAvatar;// 头像
            TextView tvNick;// 昵称
        }

        @Override
        public int getPositionForSection(int section) {
            for (int i = 0; i < mNicks.size(); i++) {
                String l = PingYinUtil.converterToFirstSpell(mNicks.get(i).getMm_emp_nickname())
                        .substring(0, 1);
                char firstChar = l.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public int getSectionForPosition(int position) {
            return 0;
        }

        @Override
        public Object[] getSections() {
            return null;
        }
    }


}
