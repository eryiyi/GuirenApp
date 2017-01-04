package com.Lbins.GuirenApp.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.Lbins.GuirenApp.R;
import com.Lbins.GuirenApp.adapter.GridCityLocationViewAdapter;
import com.Lbins.GuirenApp.base.BaseFragment;
import com.Lbins.GuirenApp.base.InternetURL;
import com.Lbins.GuirenApp.data.CityData;
import com.Lbins.GuirenApp.module.CityObj;
import com.Lbins.GuirenApp.module.ProvinceObj;
import com.Lbins.GuirenApp.ui.CityLocationActivity;
import com.Lbins.GuirenApp.util.StringUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CityLocationFragment extends BaseFragment {

	private ArrayList<CityObj> list = new ArrayList<CityObj>();
	private GridView gridView;
	private GridCityLocationViewAdapter adapter;
	private ProvinceObj provinceObj;
	private TextView toptype;
	private ImageView icon;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pro_type1, null);
		gridView = (GridView) view.findViewById(R.id.listView);
		int index = getArguments().getInt("index");
		provinceObj = CityLocationActivity.list.get(index);
		toptype = (TextView) view.findViewById(R.id.toptype);
		icon = (ImageView) view.findViewById(R.id.icon);
		if(provinceObj != null){
			toptype.setText(provinceObj.getProvince());
		}
		adapter = new GridCityLocationViewAdapter(getActivity(), list);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				if(list !=null){
					if(list.size() > i){
						CityObj cityObj = list.get(i);
						if(cityObj != null){
							save("location_city", cityObj.getCity());
							save("location_city_id", cityObj.getCityID());
							Intent intent1 = new Intent("update_location_success");
							getActivity().sendBroadcast(intent1);
							getActivity().finish();
						}
					}
				}

			}
		});

		getCitys1();

		return view;
	}

	//获得城市
	public void getCitys1() {
		StringRequest request = new StringRequest(
				Request.Method.POST,
				InternetURL.GET_CITY_URL,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String s) {
						if (StringUtil.isJson(s)) {
							try {
								JSONObject jo = new JSONObject(s);
								String code1 = jo.getString("code");
								if (Integer.parseInt(code1) == 200) {
									CityData data = getGson().fromJson(s, CityData.class);
									list.clear();
									list.addAll(data.getData());
									adapter.notifyDataSetChanged();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
						}
						if (progressDialog != null) {
							progressDialog.dismiss();
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						if (progressDialog != null) {
							progressDialog.dismiss();
						}
						Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
					}
				}
		) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("father", provinceObj.getProvinceID());
				params.put("is_use", "1");
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
