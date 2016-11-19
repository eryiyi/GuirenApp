package com.hyphenate.easeui.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.Lbins.GuirenApp.GuirenApplication;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.AnimateFirstDisplayListener;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.dao.DBHelper;
import com.Lbins.GuirenApp.data.EmpsData;
import com.Lbins.GuirenApp.huanxin.mine.MyEMConversation;
import com.Lbins.GuirenApp.huanxin.ui.GroupsActivity;
import com.Lbins.GuirenApp.module.Emp;
import com.Lbins.GuirenApp.ui.AndMeAcitvity;
import com.Lbins.GuirenApp.util.GuirenHttpUtils;
import com.Lbins.GuirenApp.util.StringUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMConversationListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.widget.EaseConversationList;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.*;

/**
 * conversation list fragment
 *
 */
public class EaseConversationListFragment extends EaseBaseFragment implements OnClickListener{
    private View view;
    private Resources res;

	private final static int MSG_REFRESH = 2;
    protected EditText query;
    protected ImageButton clearSearch;
    protected boolean hidden;
    protected List<MyEMConversation> conversationList = new ArrayList<MyEMConversation>();
    protected EaseConversationList conversationListView;
    protected FrameLayout errorItemContainer;

    protected boolean isConflict;

    private LinearLayout listViewHead;//头部
    private TextView unread_andme_number;
    private InputMethodManager inputMethodManager;
    boolean isMobileNet, isWifiNet;

    private ImageView right_image;
    private ImageView andme_icon;

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    protected EMConversationListener convListener = new EMConversationListener(){
		@Override
		public void onCoversationUpdate() {
			refresh();
		}
    };
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.ease_fragment_conversation_list, container, false);
        view = inflater.inflate(R.layout.ease_fragment_conversation_list, null);
        res = getActivity().getResources();
        registerBoradcastReceiver();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        //初始化ListView头部组件
        listViewHead = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.chatheader, null);
        unread_andme_number = (TextView) listViewHead.findViewById(R.id.unread_andme_number);
        listViewHead.findViewById(R.id.andme).setOnClickListener(this);
        andme_icon = (ImageView) listViewHead.findViewById(R.id.andme_icon);
        imageLoader.displayImage( getGson().fromJson(getSp().getString("mm_emp_cover", ""), String.class) ,andme_icon, GuirenApplication.txOptions, animateFirstListener);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void initView() {
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        conversationListView = (EaseConversationList) view.findViewById(R.id.list);
        query = (EditText) view.findViewById(R.id.query);
        // button to clear content in search bar
        clearSearch = (ImageButton) view.findViewById(R.id.search_clear);
        errorItemContainer = (FrameLayout) view.findViewById(R.id.fl_error_item);
        conversationListView.addHeaderView(listViewHead);
        right_image = (ImageView) view.findViewById(R.id.right_image);
        right_image.setImageDrawable(view.getResources().getDrawable(R.drawable.em_contact_list_togroup));
    }
    
    @Override
    protected void setUpView() {
        conversationList.addAll(loadConversationList());
        if(conversationList != null && conversationList.size()>0){
            for(MyEMConversation emConversation:conversationList){

            }
        }
        conversationListView.init(conversationList);
        
        if(listItemClickListener != null){
            conversationListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MyEMConversation conversation = conversationListView.getItem(position);
                    listItemClickListener.onListItemClicked(conversation);
                }
            });
        }
        
        EMClient.getInstance().addConnectionListener(connectionListener);
        
        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                conversationListView.filter(s);
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        clearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });
        
        conversationListView.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();
                return false;
            }
        });

        right_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GroupsActivity.class);
                startActivity(intent);
            }
        });

        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
            if (!isMobileNet && !isWifiNet) {
                Toast.makeText(getActivity(), "请检查网络链接", Toast.LENGTH_SHORT).show();
            }else {
                getNickNamesByHxUserNames(getHxUsernames(conversationList));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getHxUsernames(List<MyEMConversation> conversationList) {
        StringBuffer strUser = new StringBuffer();
        for (int i = 0; i < conversationList.size(); i++) {
            strUser.append(conversationList.get(i).getEmConversation().getUserName());
            if (i < conversationList.size() - 1) {
                strUser.append(",");
            }
        }
        return strUser.toString();
    }
    
    protected EMConnectionListener connectionListener = new EMConnectionListener() {
        
        @Override
        public void onDisconnected(int error) {
            if (error == EMError.USER_REMOVED || error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                isConflict = true;
            } else {
               handler.sendEmptyMessage(0);
            }
        }
        
        @Override
        public void onConnected() {
            handler.sendEmptyMessage(1);
        }
    };
    private EaseConversationListItemClickListener listItemClickListener;
    
    protected Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case 0:
                onConnectionDisconnected();
                break;
            case 1:
                onConnectionConnected();
                break;
            
            case MSG_REFRESH:
	            {
	            	conversationList.clear();
	                conversationList.addAll(loadConversationList());

//                    if(conversationListView != null){
//                        conversationListView.refresh();
//                    }

                    //判断是否有网
                    try {
                        isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
                        isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
                        if (!isMobileNet && !isWifiNet) {
                            Toast.makeText(getActivity(), "请检查网络链接", Toast.LENGTH_SHORT).show();
                        }else {
                            getNickNamesByHxUserNames(getHxUsernames(conversationList));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

	                break;
	            }
            default:
                break;
            }
        }
    };
    
    /**
     * connected to server
     */
    protected void onConnectionConnected(){
        errorItemContainer.setVisibility(View.GONE);
    }
    
    /**
     * disconnected with server
     */
    protected void onConnectionDisconnected(){
        errorItemContainer.setVisibility(View.VISIBLE);
    }
    

    /**
     * refresh ui
     */
    public void refresh() {
    	if(!handler.hasMessages(MSG_REFRESH)){
    		handler.sendEmptyMessage(MSG_REFRESH);
    	}

    }
    
    /**
     * load conversation list
     * 
     * @return
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        +    */
    protected List<MyEMConversation> loadConversationList(){
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, MyEMConversation>> sortList = new ArrayList<Pair<Long, MyEMConversation>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    MyEMConversation my = new MyEMConversation();
                    my.setEmConversation(conversation);
                    sortList.add(new Pair<Long, MyEMConversation>(my.getEmConversation().getLastMessage().getMsgTime(), my));
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<MyEMConversation> list = new ArrayList<MyEMConversation>();

        for (Pair<Long, MyEMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }

        return list;
    }

    /**
     * sort conversations according time stamp of last message
     * 
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<Pair<Long, MyEMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, MyEMConversation>>() {
            @Override
            public int compare(final Pair<Long, MyEMConversation> con1, final Pair<Long, MyEMConversation> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }
    
   protected void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden && !isConflict) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(isConflict){
            outState.putBoolean("isConflict", true);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.andme) {
            unread_andme_number.setText("0");
            unread_andme_number.setVisibility(View.INVISIBLE);
            Intent relateView = new Intent(getActivity(), AndMeAcitvity.class);
            startActivity(relateView);
        }
    }


    public interface EaseConversationListItemClickListener {
        /**
         * click event for conversation list
         * @param conversation -- clicked item
         */
        void onListItemClicked(MyEMConversation conversation);
    }
    
    /**
     * set conversation list item click listener
     * @param listItemClickListener
     */
    public void setConversationListItemClickListener(EaseConversationListItemClickListener listItemClickListener){
        this.listItemClickListener = listItemClickListener;
    }

    //获得好友资料
    List<Emp> emps = new ArrayList<Emp>();
    //通过贵人username获取用户昵称
    private void getNickNamesByHxUserNames(final String hxUserNames) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_INVITE_CONTACT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            EmpsData data = getGson().fromJson(s, EmpsData.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                emps = data.getData();
                                if(conversationListView != null){
                                    conversationListView.refresh();
                                }
                                if(emps != null){
                                    for(Emp emp : emps){
                                        Emp emp1 = DBHelper.getInstance(getActivity()).getEmpByEmpId(emp.getMm_emp_id());
                                        if(emp1 != null){
                                            //说明存在这个用户了
                                        }else{
                                            //不存在该用户 可以保存到数据库
                                            DBHelper.getInstance(getActivity()).saveEmp(emp);
                                        }
                                    }
                                }
                                notifyMyAdapter();
                            } else {
                                Toast.makeText(getActivity(), "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("hxUserNames", hxUserNames);
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

    private void notifyMyAdapter() {

        for (MyEMConversation my : conversationList) {
            for (Emp emp : emps) {
                if (my.getEmConversation().getUserName().equals(emp.getHxusername())) {
                    my.setEmp(emp);
                }
            }
        }
        if (conversationListView.adapter != null)
            conversationListView.adapter.notifyDataSetChanged();
    }

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("arrived_msg_andMe");//有与我相关
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }


    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("arrived_msg_andMe")){
                String strCount = unread_andme_number.getText().toString();
                if (!StringUtil.isNullOrEmpty(strCount)) {
                    //说明有值
                    unread_andme_number.setText(String.valueOf(Integer.parseInt(strCount) + 1));
                } else {
                    unread_andme_number.setText("1");
                }
                unread_andme_number.setVisibility(View.VISIBLE);
            }
        }
    }  ;

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
        EMClient.getInstance().removeConnectionListener(connectionListener);
    }
}
