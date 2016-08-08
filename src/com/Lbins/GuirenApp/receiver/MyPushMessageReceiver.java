package com.Lbins.GuirenApp.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.SuccessData;
import com.Lbins.GuirenApp.ui.AndMeAcitvity;
import com.Lbins.GuirenApp.util.Constants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.android.pushservice.PushMessageReceiver;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by zhanghl on 2015/1/6.
 */
public class MyPushMessageReceiver extends PushMessageReceiver {

    @Override
    public void onBind(Context context, int errorCode, String appid, String userId, String channelId, String requestId) {
        updateChanelId(context, channelId, userId);//程序一运行就执行
    }

    @Override
    public void onUnbind(Context context, int i, String s) {
        Log.e("Message", s);
    }

    @Override
    public void onSetTags(Context context, int i, List<String> strings, List<String> strings2, String s) {
        Log.e("Message", s);
    }

    @Override
    public void onDelTags(Context context, int i, List<String> strings, List<String> strings2, String s) {
        Log.e("Message", s);
    }

    @Override
    public void onListTags(Context context, int i, List<String> strings, String s) {
        Log.e("Message", s);
//        Intent intent = new Intent(context, JiaohuActivity.class);
//        context.startActivity(intent);
    }

    @Override
    public void onMessage(Context context, String s, String s2) {
//        Intent intent = new Intent(context, JiaohuActivity.class);
//        context.startActivity(intent);
        Log.e("Message", s);
    }

    @Override
    public void onNotificationArrived(Context context, String title,
                                      String description, String customContentString) {

//        String notifyString = "通知到达 onNotificationArrived  title=\"" + title
//                + "\" description=\"" + description + "\" customContent="
//                + customContentString;
//        Log.d(TAG, notifyString);
//
//        // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
//        if (!TextUtils.isEmpty(customContentString)) {
//            JSONObject customJson = null;
//            try {
//                customJson = new JSONObject(customContentString);
//                String myvalue = null;
//                if (!customJson.isNull("mykey")) {
//                    myvalue = customJson.getString("mykey");
//                }
//            } catch (JSONException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        // 你可以參考 onNotificationClicked中的提示从自定义内容获取具体值
        updateContent(context);
    }

    private void updateContent(Context context) {
        //发送通知 修改主页面
        Intent intent1 = new Intent("arrived_msg_andMe");
        context.sendBroadcast(intent1);
    }

    @Override
    public void onNotificationClicked(Context context, String title, String content, String customContent) {
        Log.e("Message", title);

        try {
            Intent intent = new Intent();
            JSONObject custom = new JSONObject(customContent);
            int type = custom.getInt("type");
            switch (type) {
                case 1://公告
                    //改变底部图标
//                    Intent msg_notice = new Intent("_msg_notice");
//                    context.sendBroadcast(msg_notice);
//                    intent.setClass(context.getApplicationContext(), NoticeActivity.class);
                    break;
                case 2://与我相关
                    Intent msg_record = new Intent("_msg_record");
                    context.sendBroadcast(msg_record);
                    intent.setClass(context.getApplicationContext(), AndMeAcitvity.class);
                    break;
                case 3://关禁闭
//                    String time = custom.getString("time");
//                    intent.putExtra("time", time);
//                    intent.setClass(context.getApplicationContext(),ExplainActivity.class);
                    break;

            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public void updateChanelId(final Context context, String chanelId, final String userId) {
        final SharedPreferences sp = context.getSharedPreferences("university_manage", Context.MODE_PRIVATE);
//        String spUserId = sp.getString(Constants.PUSH_USER_ID, "");
//        if(spUserId.equals(userId)){
//            return;
//        }
        String empId = new Gson().fromJson(sp.getString("mm_emp_id", ""), String.class);
        RequestQueue queue = Volley.newRequestQueue(context);
        String uri = String.format(InternetURL.UPDATE_PUSH_ID + "?id=%s&userId=%s&type=3&channelId=%s", empId, userId,chanelId);
        StringRequest request = new StringRequest(
                Request.Method.GET,
                uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            SuccessData successDATA = new Gson().fromJson(s, SuccessData.class);
                            if (Integer.parseInt(successDATA.getCode()) == 200) {
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString(Constants.PUSH_USER_ID, userId).commit();
                            }
                        } catch (Exception e) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }
        );
        queue.add(request);
    }
}
