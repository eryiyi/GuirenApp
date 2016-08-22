package com.Lbins.GuirenApp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.MoneyJiageObjData;
import com.Lbins.GuirenApp.data.OrderInfoAndSignDATA;
import com.Lbins.GuirenApp.data.SuccessData;
import com.Lbins.GuirenApp.data.WxPayObjData;
import com.Lbins.GuirenApp.module.MoneyJiageObj;
import com.Lbins.GuirenApp.module.Order;
import com.Lbins.GuirenApp.module.WxPayObj;
import com.Lbins.GuirenApp.order.OrderInfoAndSign;
import com.Lbins.GuirenApp.order.PayResult;
import com.Lbins.GuirenApp.util.MD5;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.wxpay.Util;
import com.alipay.sdk.app.PayTask;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Administrator on 2015/8/2.
 * 生成订单
 */
public class OrderMakeActivity extends BaseActivity implements View.OnClickListener,Runnable {
    public static String out_trade_no;

    //---------------------------------支付开始----------------------------------------
    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
//                        Toast.makeText(OrderMakeActivity.this, "支付成功",
//                                Toast.LENGTH_SHORT).show();
                        //更新订单状态
                        updateMineOrder();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(OrderMakeActivity.this, "支付结果确认中",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(OrderMakeActivity.this, "支付失败",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(OrderMakeActivity.this, "检查结果为：" + msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        };
    };
    //------------------------------------------------------------------------------------
    private ImageView back;
    private TextView order_count;//价格合计
    private TextView number;
    private TextView money;
    //微信支付
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_make_activity);
        initView();
        registerBoradcastReceiver();
        //微信支付
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, InternetURL.WEIXIN_APPID, false);
        //获得价格表单
        getMoney();

    }

//    pay_wx_success

    private void initView() {
        back = (ImageView) this.findViewById(R.id.back);
        order_count = (TextView) this.findViewById(R.id.order_count);
        number = (TextView) this.findViewById(R.id.number);
        money = (TextView) this.findViewById(R.id.money);

        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.back:
                finish();
                break;
        }
    }

//    //计算金额总的
    void toCalculate(){
        DecimalFormat df = new DecimalFormat("0.00");
        Double doublePrices = 0.0;
        doublePrices = doublePrices + Double.parseDouble(number.getText().toString()) * Double.parseDouble(money.getText().toString());
        order_count.setText(df.format(doublePrices).toString());
    }

    //传order给服务器
    private void sendOrderToServer(final Order order) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SEND_ORDER_TOSERVER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            OrderInfoAndSignDATA data = getGson().fromJson(s, OrderInfoAndSignDATA.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                //已经生成订单，等待支付，下面去支付
                                out_trade_no= data.getData().getOut_trade_no();
                                pay(data.getData());//调用支付接口
                            } else {
                                Toast.makeText(OrderMakeActivity.this, R.string.order_error_one, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(OrderMakeActivity.this, R.string.order_error_one, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(OrderMakeActivity.this, R.string.order_error_one, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", order.getEmp_id());
                params.put("payable_amount", order.getPayable_amount());
                params.put("goods_count", order.getGoods_count());
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

    //---------------------------------------------------------支付宝------------------------------------------

    /**
     * call alipay sdk pay. 调用SDK支付
     *
     */
    public void pay(OrderInfoAndSign orderInfoAndSign) {

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfoAndSign.getOrderInfo() + "&sign=\"" + orderInfoAndSign.getSign() + "\"&"
                + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(OrderMakeActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * check whether the device has authentication alipay account.
     * 查询终端设备是否存在支付宝认证账户
     *
     */
    public void check(View v) {
        Runnable checkRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(OrderMakeActivity.this);
                // 调用查询接口，获取查询结果
                boolean isExist = payTask.checkAccountIfExist();

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };

        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();

    }

    /**
     * get the sdk version. 获取SDK版本号
     *
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
    }

    /**
     * get the sign type we use. 获取签名方式
     *
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }


    //更新订单状态
    void updateMineOrder(){
            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    InternetURL.UPDATE_ORDER_TOSERVER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            if (StringUtil.isJson(s)) {
                                SuccessData data = getGson().fromJson(s, SuccessData.class);
                                if (Integer.parseInt(data.getCode()) == 200) {
                                    Toast.makeText(OrderMakeActivity.this, R.string.order_success, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(OrderMakeActivity.this, R.string.order_error_one, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(OrderMakeActivity.this, R.string.order_error_one, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(OrderMakeActivity.this, R.string.order_error_one, Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("trade_no",  out_trade_no);
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


    public void goToPayAliy(View view) {
        //
        if (StringUtil.isNullOrEmpty(order_count.getText().toString())) {
            showMsg(OrderMakeActivity.this, getResources().getString(R.string.please_select));
        } else {
            //先传值给服务端
            Order order = new Order();
            order.setGoods_count(number.getText().toString());
            order.setPayable_amount(order_count.getText().toString());
            order.setTrade_type("0");//trade_type  0支付宝 1微信支付
            order.setEmp_id(getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
            if(order!=null ) {
                //传值给服务端
                sendOrderToServer(order);
            }
        }
    }


    String xmlStr = "";
    WxPayObj wxPayObj;
    public void goToPayWeixin(View view){
        // 将该app注册到微信
        api.registerApp(InternetURL.WEIXIN_APPID);
        if (StringUtil.isNullOrEmpty(order_count.getText().toString()) || order_count == null) {
            showMsg(OrderMakeActivity.this, getResources().getString(R.string.please_select));
        } else {
            //先传值给服务端
            final Order order = new Order();
            order.setGoods_count(number.getText().toString());
            order.setPayable_amount(order_count.getText().toString());
            order.setTrade_type("1");//trade_type  0支付宝 1微信支付
            order.setEmp_id(getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
            if(order!=null ){
                //传值给服务端
                StringRequest request = new StringRequest(
                        Request.Method.POST,
                        InternetURL.SEND_ORDER_TOSERVER_WX,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                if (StringUtil.isJson(s)) {
                                    WxPayObjData data = getGson().fromJson(s, WxPayObjData.class);
                                    if (Integer.parseInt(data.getCode()) == 200) {
                                        //我们服务端已经生成订单，微信支付统一下单
                                        wxPayObj = data.getData();
                                        if(wxPayObj != null){
                                            xmlStr =wxPayObj.getXmlStr();
                                            out_trade_no = wxPayObj.getOut_trade_no();
                                        }
                                        // 启动一个线程
                                        new Thread(OrderMakeActivity.this).start();
                                    } else {
                                        Toast.makeText(OrderMakeActivity.this, R.string.order_error_one, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(OrderMakeActivity.this, R.string.order_error_one, Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Toast.makeText(OrderMakeActivity.this, R.string.order_error_one, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("emp_id", order.getEmp_id());
                        params.put("payable_amount", order.getPayable_amount());
                        params.put("goods_count", order.getGoods_count());
                        params.put("trade_type", order.getTrade_type());
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
    }
    public Map<String,String> decodeXml(String content) {
        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName=parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        if("xml".equals(nodeName)==false){
                            xml.put(nodeName,parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            Log.e("orion",e.toString());
        }
        return null;
    }

    @Override
    public void run() {
        String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
        byte[] buf = Util.httpPost(url, xmlStr);
        String content = new String(buf);
        Map<String,String> xmlMap=decodeXml(content);
        PayReq req = new PayReq();

        req.appId			= xmlMap.get("appid");
        req.partnerId		=  xmlMap.get("mch_id");
        req.prepayId		= xmlMap.get("prepay_id");
        req.nonceStr		= xmlMap.get("nonce_str");
        req.packageValue			= " Sign=WXPay";
        req.timeStamp = String.valueOf(genTimeStamp());

        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

        req.sign = genAppSign(signParams).toUpperCase();

        api.sendReq(req);
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }
    StringBuffer sb=new StringBuffer();;

    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(InternetURL.WX_API_KEY);

        this.sb.append("sign str\n"+sb.toString()+"\n\n");
        String appSign = MD5.getMessageDigest(sb.toString().getBytes());
        return appSign;
    }


    private List<MoneyJiageObj> list = new ArrayList<MoneyJiageObj>();
    private void getMoney() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_MOENY_JIAGE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            MoneyJiageObjData data = getGson().fromJson(s, MoneyJiageObjData.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                list.clear();
                                list.addAll(data.getData());
                                for(MoneyJiageObj moneyJiageObj : list){
                                   if("0".equals(moneyJiageObj.getIstype())){
                                       money.setText(moneyJiageObj.getMoney_jiage());
                                       break;
                                   }
                                }
                                toCalculate();
                            } else {
                                Toast.makeText(OrderMakeActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(OrderMakeActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(OrderMakeActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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

        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(request);
    }

    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("pay_wx_success")){
                //
                updateMineOrder();
            }
        }

    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("pay_wx_success");
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
