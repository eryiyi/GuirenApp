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
import android.widget.ImageView;
import android.widget.TextView;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.AnimateFirstDisplayListener;
import com.Lbins.GuirenApp.adapter.GridClassViewAdapter;
import com.Lbins.GuirenApp.base.BaseFragment;
import com.Lbins.GuirenApp.module.HelpType;
import com.Lbins.GuirenApp.ui.SearchMoreClassActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class ProClassFragment extends BaseFragment {

	private ArrayList<HelpType> list = new ArrayList<HelpType>();
	private GridView gridView;
	private GridClassViewAdapter adapter;
	private HelpType goodsType;
	private TextView toptype;
	private ImageView icon;


	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pro_type1, null);
		gridView = (GridView) view.findViewById(R.id.listView);
		int index = getArguments().getInt("index");
		goodsType = SearchMoreClassActivity.list.get(index);
		toptype = (TextView) view.findViewById(R.id.toptype);
		icon = (ImageView) view.findViewById(R.id.icon);
		if(goodsType != null){
			toptype.setText(goodsType.getHelp_type_name());
//			imageLoader.displayImage(goodsType.getLx_class_cover(), icon, MeirenmeibaAppApplication.txOptions, animateFirstListener);
		}
		GetTypeList();
		adapter = new GridClassViewAdapter(getActivity(), list);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				if(list !=null){
					if(list.size() > i){
						HelpType goodsType1 = list.get(i);
						if(goodsType1 != null){
//							Intent intent = new Intent(getActivity(), NearbyActivity.class);
//							intent.putExtra("lx_class_id", goodsType1.getLx_class_id());
//							startActivity(intent);
							Intent intent = new Intent();
							intent.putExtra("helpType", goodsType1);
							getActivity().setResult(getActivity().RESULT_OK, intent);
							getActivity().finish();
						}
					}
				}

			}
		});

		return view;
	}

	private void GetTypeList() {
		list.clear();
		for(HelpType helpType : SearchMoreClassActivity.listAll){
			if(helpType.getHelp_type_f_id().equals(goodsType.getHelp_type_id())){
				list.add(helpType);
			}
		}
//		adapter.notifyDataSetChanged();
//			StringRequest request = new StringRequest(
//					Request.Method.POST,
//					InternetURL.appGetLxClass,
//					new Response.Listener<String>() {
//						@Override
//						public void onResponse(String s) {
//							if (StringUtil.isJson(s)) {
//								try {
//									JSONObject jo = new JSONObject(s);
//									String code = jo.getString("code");
//									if (Integer.parseInt(code) == 200) {
//										list.clear();
//										Gson gson = getGson();
//										if(gson != null){
//											LxClassData data = gson.fromJson(s, LxClassData.class);
//											list.addAll(data.getData());
//											adapter.notifyDataSetChanged();
//										}
//									}else {
//										Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
//									}
//								} catch (JSONException e) {
//									e.printStackTrace();
//								}
//
//
//							}
//						}
//					},
//					new Response.ErrorListener() {
//						@Override
//						public void onErrorResponse(VolleyError volleyError) {
//							Toast.makeText(getActivity(), getResources().getString(R.string.get_data_error), Toast.LENGTH_SHORT).show();
//						}
//					}
//			) {
//				@Override
//				protected Map<String, String> getParams() throws AuthFailureError {
//					Map<String, String> params = new HashMap<String, String>();
//					params.put("f_lx_class_id", goodsType.getLx_class_id());
//					return params;
//				}
//
//				@Override
//				public Map<String, String> getHeaders() throws AuthFailureError {
//					Map<String, String> params = new HashMap<String, String>();
//					params.put("Content-Type", "application/x-www-form-urlencoded");
//					return params;
//				}
//			};
//			getRequestQueue().add(request);
	}
}
