package com.Lbins.GuirenApp.wxapi;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.base.ActivityTack;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.SuccessData;
import com.Lbins.GuirenApp.ui.OrderMakeActivity;
import com.Lbins.GuirenApp.util.StringUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        
    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle(R.string.app_tip);
//			builder.setMessage(getString(R.string.pay_result_callback_msg, resp.errStr +";code=" + String.valueOf(resp.errCode)));
//			builder.show();
			Log.d(TAG, "resp.errStr +\";code=\" + String.valueOf(resp.errCode)) = " + resp.errStr + ";code=" + String.valueOf(resp.errCode));
			if(resp.errCode == 0){
				//说明支付成功
				showMsg(WXPayEntryActivity.this, "支付成功");
				//调用逻辑处理
				updateMineOrder();
				ActivityTack.getInstanse().popUntilActivity(OrderMakeActivity.class);
			}else {
				//支付失败
				showMsg(WXPayEntryActivity.this, "支付失败");
				ActivityTack.getInstanse().popUntilActivity(OrderMakeActivity.class);
			}
		}

//		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle(R.string.app_tip);
//			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//			builder.show();
//		}
	}

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
								Toast.makeText(WXPayEntryActivity.this, R.string.order_success, Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(WXPayEntryActivity.this, R.string.order_error_one, Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(WXPayEntryActivity.this, R.string.order_error_one, Toast.LENGTH_SHORT).show();
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						Toast.makeText(WXPayEntryActivity.this, R.string.order_error_one, Toast.LENGTH_SHORT).show();
					}
				}
		) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("trade_no",  OrderMakeActivity.out_trade_no);
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