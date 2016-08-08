package com.Lbins.GuirenApp.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.Pro_type_adapter;
import com.Lbins.GuirenApp.base.BaseFragment;
import com.Lbins.GuirenApp.module.HangYeType;
import com.Lbins.GuirenApp.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class Fragment_pro_type extends BaseFragment {
	private GridView listView;
	private Pro_type_adapter adapter;
	private HangYeType hangYeType;
	ArrayList<HangYeType> lists = new ArrayList<HangYeType>();//这个是所有的类别
	private List<HangYeType> toolsList = new ArrayList<HangYeType>();//这个是小类别
	private ProgressBar progressBar;

	private Thread mSplashThread;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pro_type, null);
		progressBar=(ProgressBar) view.findViewById(R.id.progressBar);
		listView = (GridView) view.findViewById(R.id.listView);
		hangYeType= (HangYeType) getArguments().getSerializable("hangYeType");
		lists = (ArrayList<HangYeType>) getArguments().getSerializable("lists");
		((TextView) view.findViewById(R.id.toptype)).setText(hangYeType.getMm_hangye_name());
		toolsList.clear();
		for(HangYeType hangYeType1 : lists){
			toolsList.add(hangYeType1);
		}
		adapter=new Pro_type_adapter(getActivity(), toolsList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				//选中的那个
				HangYeType hangYeType1 = toolsList.get(position);
				//发送广播
				Intent intent1 = new Intent(Constants.SELECT_HANGYE_TYPE_SUCCESS);
				intent1.putExtra("hangYeType", hangYeType1);
				getActivity().sendBroadcast(intent1);
				getActivity().finish();
			}
		});
		listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		progressBar.setVisibility(View.GONE);
		return view;
	}

	
	/*private class LoadTask extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			String name[]=new String[]{"shopid","type"};
			String value[]=new String[]{"0","store"};
			return NetworkHandle.requestBypost("app=u_favorite&act=index",name,value);
		}
		
		@Override
		protected void onPostExecute(String result) {	
			progressBar.setVisibility(View.GONE);
			list=new ArrayList<Shop>();
			try {
				if(Constant.isDebug)System.out.println("result:"+result);
				JSONObject ob=new JSONObject(result);
				if(ob.getString("state").equals("1")){
					arrayToList(ob.getJSONArray("list"));
					adapter=new Love_shop_adapter(getActivity(), list,listView);
					listView.setAdapter(adapter);
					listView.onRefreshComplete();
					if(list.size()<20)
						listView.onPullUpRefreshFail();
					if(list.size()==0)hint_img.setVisibility(View.VISIBLE);
					else hint_img.setVisibility(View.GONE);
				}else{
					//if(tradestate.equals("0"))
						//ResultUtils.handle((Activity_order)getActivity(), ob);
				}
			} catch (Exception e) {
			//	if(tradestate.equals("0"))
					//ResultUtils.handle((Activity_order)getActivity(), "");
				e.printStackTrace();
			}	
		}
	}
	
	private void arrayToList(JSONArray array) throws JSONException{
		JSONObject ob;
		for (int i = 0; i < array.length(); i++) {
			ob=array.getJSONObject(i);
			shop=new Shop(ob.getString("shopid"),ob.getString("shopname"), ob.getString("shoplogo"), ob.getString("weixin"), ob.getString("shopurl"));
			list.add(shop);	
		   }
		}
	*/
	
	/*private class LoadTaskMore extends AsyncTask<Void, Void, String>{
		@Override
		protected String doInBackground(Void... params) {
			String name[]=new String[]{"shopid","type"};
			String value[]=new String[]{list.get(list.size()-1).getShopid(),"store"};
			return NetworkHandle.requestBypost("app=u_favorite&act=index",name,value);
		}
		@Override
		protected void onPostExecute(String result) {
			if(Constant.isDebug)System.out.println("result:"+result);
			try {
				JSONObject ob=new JSONObject(result);
				if(ob.getString("state").equals("1")){
					JSONArray array=ob.getJSONArray("list");
					arrayToList(array);
					if(array.length()>0)
						adapter.notifyDataSetChanged();
					if(array.length()<20)
						listView.onPullUpRefreshFail();
					else 
						listView.onPullUpRefreshComplete();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}*/
	
	
	
}
