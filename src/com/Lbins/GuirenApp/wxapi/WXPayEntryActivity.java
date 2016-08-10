package com.Lbins.GuirenApp.wxapi;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.base.ActivityTack;
import com.Lbins.GuirenApp.base.BaseActivity;
import com.Lbins.GuirenApp.ui.OrderMakeActivity;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

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
				Intent intent1 = new Intent("pay_wx_success");
				sendBroadcast(intent1);
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

}