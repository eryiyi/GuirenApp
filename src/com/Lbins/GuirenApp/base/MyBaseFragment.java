package com.Lbins.GuirenApp.base;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import com.Lbins.GuirenApp.GuirenApplication;
import com.android.volley.RequestQueue;
import com.google.gson.Gson;

/**
 * Created by liuzwei on 2015/1/17.
 */
public class MyBaseFragment extends Fragment {
    public ProgressDialog progressDialog;

    /**
     * 获取当前Application
     *
     * @return
     */
    public GuirenApplication getMyApp() {
        return (GuirenApplication) getActivity().getApplication();
    }

    //存储sharepreference
    public void save(String key, Object value) {
        getMyApp().getSp().edit()
                .putString(key, getMyApp().getGson().toJson(value))
                .commit();
    }

    /**
     * 获取Volley请求队列
     *
     * @return
     */
    public RequestQueue getRequestQueue() {
        return getMyApp().getRequestQueue();
    }

    /**
     * 获取Gson
     *
     * @return
     */
    public Gson getGson() {
        return getMyApp().getGson();
    }

    /**
     * 获取SharedPreferences
     *
     * @return
     */
    public SharedPreferences getSp() {
        return getMyApp().getSp();
    }


}
