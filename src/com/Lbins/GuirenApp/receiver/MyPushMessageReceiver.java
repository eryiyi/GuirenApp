package com.Lbins.GuirenApp.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.SuccessData;
import com.Lbins.GuirenApp.ui.AndMeAcitvity;
import com.Lbins.GuirenApp.ui.NoticeActivity;
import com.Lbins.GuirenApp.util.Constants;
import com.Lbins.GuirenApp.util.StringUtil;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.android.pushservice.PushMessageReceiver;
import java.util.List;

/**
 * Created by zhanghl on 2015/1/6.
 */
public class MyPushMessageReceiver extends PushMessageReceiver {

    @Override
    public void onBind(Context context, int errorCode, String appid,
                       String userId, String channelId, String requestId) {
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

        if (errorCode == 0) {
            // 绑定成功
            // 绑定自己的账号和手机号
            updateChanelId(context, channelId, userId);
        }
    }

    public void onMessage(Context context, String message,
                          String customContentString) {
        String messageString = "透传消息 message=\"" + message
                + "\" customContentString=" + customContentString;
        Log.d(TAG, messageString);

        // 自定义内容获取方式，mykey和myvalue对应透传消息推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mm_notice_id")) {
                    myvalue = customJson.getString("mm_notice_id");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    /**
     * 接收通知到达的函数。
     *
     * @param context             上下文
     * @param title               推送的通知的标题
     * @param description         推送的通知的描述
     * @param customContentString 自定义内容，为空或者json字符串
     */

    @Override
    public void onNotificationArrived(Context context, String title,
                                      String description, String customContentString) {

        String notifyString = "onNotificationArrived  title=\"" + title
                + "\" description=\"" + description + "\" customContent="
                + customContentString;
        Log.d(TAG, notifyString);

        // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mm_notice_id")) {
                    myvalue = customJson.getString("mm_notice_id");
                    getNotice(context, myvalue);
                }else {
                    //发送通知 修改主页面
                    Intent intent1 = new Intent("arrived_msg_andMe");
                    context.sendBroadcast(intent1);
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void getNotice(Context context, String notice_id) {
//        Intent detailNotice = new Intent();
//        detailNotice.setClass(context.getApplicationContext(), NoticeDetailActivity.class);
//        detailNotice.putExtra("id", notice_id);
//        detailNotice.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.getApplicationContext().startActivity(detailNotice);
    }


    /**
     * setTags() 的回调函数。
     *
     * @param context   上下文
     * @param errorCode 错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
     *                  设置成功的tag
     * @param failTags  设置失败的tag
     * @param requestId 分配给对云推送的请求的id
     */
    @Override
    public void onSetTags(Context context, int errorCode,
                          List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onSetTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

    }

    /**
     * delTags() 的回调函数。
     *
     * @param context   上下文
     * @param errorCode 错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
     *                  成功删除的tag
     * @param failTags  删除失败的tag
     * @param requestId 分配给对云推送的请求的id
     */
    @Override
    public void onDelTags(Context context, int errorCode,
                          List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onDelTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

    }

    /**
     * listTags() 的回调函数。
     *
     * @param context   上下文
     * @param errorCode 错误码。0表示列举tag成功；非0表示失败。
     * @param tags      当前应用设置的所有tag。
     * @param requestId 分配给对云推送的请求的id
     */
    @Override
    public void onListTags(Context context, int errorCode, List<String> tags,
                           String requestId) {
        String responseString = "onListTags errorCode=" + errorCode + " tags="
                + tags;
        Log.d(TAG, responseString);

    }

    /**
     * PushManager.stopWork() 的回调函数。
     *
     * @param context   上下文
     * @param errorCode 错误码。0表示从云推送解绑定成功；非0表示失败。
     * @param requestId 分配给对云推送的请求的id
     */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
        Log.d(TAG, responseString);

        if (errorCode == 0) {
            // 解绑定成功
        }
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
                    Intent msg_notice = new Intent("_msg_notice");
                    context.sendBroadcast(msg_notice);
                    intent.setClass(context.getApplicationContext(), NoticeActivity.class);
                    break;
                case 2://与我相关
                    Intent msg_record = new Intent("_msg_record");
                    context.sendBroadcast(msg_record);
                    intent.setClass(context.getApplicationContext(), AndMeAcitvity.class);
                    break;
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


//    public void updateChanelId(final Context context, String chanelId, final String userId) {
//        final SharedPreferences sp = context.getSharedPreferences("university_manage", Context.MODE_PRIVATE);
////        String spUserId = sp.getString(Constants.PUSH_USER_ID, "");
////        if(spUserId.equals(userId)){
////            return;
////        }
//        String empId = new Gson().fromJson(sp.getString("mm_emp_id", ""), String.class);
//        RequestQueue queue = Volley.newRequestQueue(context);
//        String uri = String.format(InternetURL.UPDATE_PUSH_ID + "?id=%s&userId=%s&type=3&channelId=%s", empId, userId,chanelId);
//        StringRequest request = new StringRequest(
//                Request.Method.GET,
//                uri,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        try {
//                            SuccessData successDATA = new Gson().fromJson(s, SuccessData.class);
//                            if (Integer.parseInt(successDATA.getCode()) == 200) {
//                                SharedPreferences.Editor editor = sp.edit();
//                                editor.putString(Constants.PUSH_USER_ID, userId).commit();
//                            }
//                        } catch (Exception e) {
//
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//
//                    }
//                }
//        );
//        queue.add(request);
//    }

    private void updateChanelId(final Context context, String channelId, final String userId) {
        final SharedPreferences sp = context.getSharedPreferences("university_manage", Context.MODE_PRIVATE);
        String empId = new Gson().fromJson(sp.getString("mm_emp_id", ""), String.class);
        RequestQueue queue = Volley.newRequestQueue(context);
        String uri = String.format(InternetURL.UPDATE_PUSH_ID + "?id=%s&userId=%s&channelId=%s&type=3", empId, userId, channelId);
        StringRequest request = new StringRequest(
                Request.Method.GET,
                uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject jo = new JSONObject(s);
                            String code = jo.getString("code");
                            if (Integer.parseInt(code) == 200) {
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
