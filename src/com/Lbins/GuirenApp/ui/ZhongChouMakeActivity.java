package com.Lbins.GuirenApp.ui;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.OrderInfoAndSignDATA;
import com.Lbins.GuirenApp.data.SuccessData;
import com.Lbins.GuirenApp.module.Order;
import com.Lbins.GuirenApp.order.OrderInfoAndSign;
import com.Lbins.GuirenApp.order.PayResult;
import com.Lbins.GuirenApp.util.StringUtil;
import com.Lbins.GuirenApp.widget.MyTextView2;
import com.alipay.sdk.app.PayTask;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/2.
 * 生成订单
 */
public class ZhongChouMakeActivity extends BaseActivity implements View.OnClickListener {
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
                            Toast.makeText(ZhongChouMakeActivity.this, "支付结果确认中",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(ZhongChouMakeActivity.this, "支付失败",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(ZhongChouMakeActivity.this, "检查结果为：" + msg.obj,
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
    private Button order_sure;//确定按钮
    private TextView order_count;//价格合计

    MyTextView2 view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhongchou_make_activity);
        initView();
    }

    private void initView() {
        back = (ImageView) this.findViewById(R.id.back);
        order_sure = (Button) this.findViewById(R.id.order_sure);
        order_count = (TextView) this.findViewById(R.id.order_count);

        back.setOnClickListener(this);
        order_sure.setOnClickListener(this);
        view = (MyTextView2) findViewById(R.id.view);
        view.setText(getAssetsString(this, "msg"));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.back:
                finish();
                break;
            case R.id.order_sure:
                //todo
            order_sure.setClickable(false);
                //先传值给服务端
                Order order = new Order();
                order.setGoods_count("0");
                order.setPayable_amount(order_count.getText().toString());
                order.setEmp_id(getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
                if(order!=null ){
                    //传值给服务端
                    sendOrderToServer(order);
                }

                break;

        }
    }
    public String getAssetsString(Context context, String fileName) {
        StringBuffer sb = new StringBuffer();
        try {
            AssetManager am = context.getAssets();
            InputStream in = am.open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                line += ("\n");
                sb.append(line);
            }
            reader.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    //计算金额总的
    void toCalculate(){
        DecimalFormat df = new DecimalFormat("0.00");
        Double doublePrices = 0.0;
        doublePrices = doublePrices + Double.parseDouble(order_count.getText().toString());
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
                                Toast.makeText(ZhongChouMakeActivity.this, R.string.order_error_one, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(ZhongChouMakeActivity.this, R.string.order_error_one, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ZhongChouMakeActivity.this, R.string.order_error_one, Toast.LENGTH_SHORT).show();
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
                PayTask alipay = new PayTask(ZhongChouMakeActivity.this);
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
                PayTask payTask = new PayTask(ZhongChouMakeActivity.this);
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
                                    Toast.makeText(ZhongChouMakeActivity.this, R.string.order_success, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ZhongChouMakeActivity.this, R.string.order_error_one, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ZhongChouMakeActivity.this, R.string.order_error_one, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(ZhongChouMakeActivity.this, R.string.order_error_one, Toast.LENGTH_SHORT).show();
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

}
