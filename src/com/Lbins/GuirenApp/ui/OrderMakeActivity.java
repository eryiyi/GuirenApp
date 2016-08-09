package com.Lbins.GuirenApp.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.MoneyJiageObjData;
import com.Lbins.GuirenApp.data.OrderInfoAndSignDATA;
import com.Lbins.GuirenApp.data.RecordDATA;
import com.Lbins.GuirenApp.data.SuccessData;
import com.Lbins.GuirenApp.module.MoneyJiageObj;
import com.Lbins.GuirenApp.module.Order;
import com.Lbins.GuirenApp.order.OrderInfoAndSign;
import com.Lbins.GuirenApp.order.PayResult;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.weixin.Util;
import com.alipay.sdk.app.PayTask;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/2.
 * 生成订单
 */
public class OrderMakeActivity extends BaseActivity implements View.OnClickListener {
    private String out_trade_no;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_make_activity);
        initView();

        //微信支付
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, InternetURL.WEIXIN_APPID, false);
        //获得价格表单
        getMoney();
    }

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
//    void toCalculate(){
//        DecimalFormat df = new DecimalFormat("0.00");
//        Double doublePrices = 0.0;
//        doublePrices = doublePrices + Double.parseDouble(number.getText().toString()) * Double.parseDouble(money.getText().toString());
//        order_count.setText(df.format(doublePrices).toString());
//    }

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
                    params.put("out_trade_no",  out_trade_no);
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
//            order.setPayable_amount(order_count.getText().toString());
            order.setPayable_amount("0.01");
            order.setEmp_id(getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
            if(order!=null ) {
                //传值给服务端
                sendOrderToServer(order);
            }
        }
    }

    //微信支付
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    public void goToPayWeixin(View view){
        String url = "http://wxpay.weixin.qq.com/pub_v2/app/app_pay.php?plat=android";
        Toast.makeText(OrderMakeActivity.this, "获取订单中...", Toast.LENGTH_SHORT).show();
        try{
            byte[] buf = Util.httpGet(url);
            if (buf != null && buf.length > 0) {
                String content = new String(buf);
                Log.e("get server pay params:", content);
                JSONObject json = new JSONObject(content);
                if(null != json && !json.has("retcode") ){
                    PayReq req = new PayReq();
                    //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                    req.appId			= json.getString("appid");
                    req.partnerId		= json.getString("partnerid");
                    req.prepayId		= json.getString("prepayid");
                    req.nonceStr		= json.getString("noncestr");
                    req.timeStamp		= json.getString("timestamp");
                    req.packageValue	= json.getString("package");
                    req.sign			= json.getString("sign");
                    req.extData			= "app data"; // optional
                    Toast.makeText(OrderMakeActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
                    // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                    api.sendReq(req);
                }else{
                    Log.d("PAY_GET", "返回错误"+json.getString("retmsg"));
                    Toast.makeText(OrderMakeActivity.this, "返回错误"+json.getString("retmsg"), Toast.LENGTH_SHORT).show();
                }
            }else{
                Log.d("PAY_GET", "服务器请求错误");
                Toast.makeText(OrderMakeActivity.this, "服务器请求错误", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Log.e("PAY_GET", "异常："+e.getMessage());
            Toast.makeText(OrderMakeActivity.this, "异常："+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
                                   }
                                }
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
}
