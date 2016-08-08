package com.Lbins.GuirenApp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * author: liuzwei
 * Date: 2014/7/29
 * Time: 17:51
 * 网络工具类
 */
public class HttpUtils {
    private static String LOG_TAG = "NetWorkHelper";
    // 创建HttpClient对象
    public static HttpClient httpClient = new DefaultHttpClient();
    public static Uri uri = Uri.parse("content://telephony/carriers");

    /**
     * 判断MOBILE网络是否可用
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static boolean isMobileDataEnable(Context context) throws Exception {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isMobileDataEnable = false;

        isMobileDataEnable = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();

        return isMobileDataEnable;
    }


    /**
     * 判断wifi 是否可用
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static boolean isWifiDataEnable(Context context) throws Exception {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWifiDataEnable = false;
        isWifiDataEnable = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        return isWifiDataEnable;
    }


}
