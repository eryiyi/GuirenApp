package com.Lbins.GuirenApp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.amap.api.maps.model.LatLng;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 1.二进制转换为十六进制
 * 2.E-mail 检测
 * 3.URL检测
 * 4.text是否包含空字符串
 * 5.添加对象数组
 *
 * @author dds
 */
public class StringUtil {
    //判断是否为JSOn格式
    public static boolean isJson(String json) {
        if (StringUtil.isNullOrEmpty(json)) {
            return false;
        }
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonParseException e) {
            return false;
        }
    }


    /**
     *
     * 方法描述：取得当前日期的上月或下月日期 ,amount=-1为上月日期，amount=1为下月日期；创建人：jya
     * @return
     * @throws Exception
     */
    public static String getFrontBackStrDate(String strDate, String format, int amount) throws Exception {
        if (null == strDate) {
            return null;
        }
        try {

            DateFormat fmt = new SimpleDateFormat(format);
            Calendar c = Calendar.getInstance();
            c.setTime(fmt.parse(strDate));
            c.add(Calendar.MONTH, amount);
            return fmt.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 添加对象数组
     *
     * @param spliter 间隔符
     * @param arr     数组
     * @return
     */
    public static String join(String spliter, Object[] arr) {
        if (arr == null || arr.length == 0) {
            return "";
        }
        if (spliter == null) {
            spliter = "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i == arr.length - 1) {
                break;
            }
            if (arr[i] == null) {
                continue;
            }
            builder.append(arr[i].toString());
            builder.append(spliter);
        }
        return builder.toString();
    }

    /**
     * java去除字符串中的空格、回车、换行符、制表符
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 生成32位编码
     * @return string
     */
    public static String getUUID(){
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        return uuid;
    }


    public static byte[] getBytes(String filePath){
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    //通过传入图片url获取位图方法
    public static Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }


    public static  String valuteNumber(String str){
        Pattern p = Pattern.compile("[0-9\\.]+");
        Matcher m = p.matcher(str);
        String string  = "";
        while(m.find()){
            string += m.group();
        }
        return string;
    }

    /**
     * 计算两点之间距离
     *
     * @param start
     * @param end
     * @return 米
     */
    public static String getDistance(LatLng start, LatLng end) {
        double lat1 = (Math.PI / 180) * start.latitude;
        double lat2 = (Math.PI / 180) * end.latitude;

        double lon1 = (Math.PI / 180) * start.longitude;
        double lon2 = (Math.PI / 180) * end.longitude;

//      double Lat1r = (Math.PI/180)*(gp1.getLatitudeE6()/1E6);
//      double Lat2r = (Math.PI/180)*(gp2.getLatitudeE6()/1E6);
//      double Lon1r = (Math.PI/180)*(gp1.getLongitudeE6()/1E6);
//      double Lon2r = (Math.PI/180)*(gp2.getLongitudeE6()/1E6);

        //地球半径
        double R = 6371;

        //两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(d);
    }

    public static boolean betweenTwoNumber(int start,int end){
        SimpleDateFormat formatter   =   new   SimpleDateFormat   ("HH:mm");
        Date curDate   =   new Date(System.currentTimeMillis());//获取当前时间
        String   str   =   formatter.format(curDate);
        if(!StringUtil.isNullOrEmpty(str.replace(":",""))){
            int strInt = Integer.parseInt(str.replace(":",""));
            if(strInt>start && strInt<end){
                return true;
            }else {
                return false;
            }
        }
        return false;
    }

}
