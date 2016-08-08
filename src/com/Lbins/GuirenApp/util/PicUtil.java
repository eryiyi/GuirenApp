package com.Lbins.GuirenApp.util;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * author: liuzwei
 * Date: 2014/8/20
 * Time: 14:31
 * 类的功能、说明写在此处.
 */
public class PicUtil implements Runnable {
    private String url;
    private static int MB = 1024 * 1024;

    public PicUtil(String url) {
        this.url = url;
    }

    public static void downloadImage(String imageUrl) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d("TAG", "monted sdcard");
        } else {
            Log.d("TAG", "has no sdcard");
        }
        HttpURLConnection con = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        File imageFile = null;
        try {
            URL url = new URL(imageUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5 * 1000);
            con.setReadTimeout(15 * 1000);
            con.setDoInput(true);
            con.setDoOutput(true);
            bis = new BufferedInputStream(con.getInputStream());
            imageFile = new File(getImagePath(imageUrl));
            fos = new FileOutputStream(imageFile);
            bos = new BufferedOutputStream(fos);
            byte[] b = new byte[1024];
            int length;
            while ((length = bis.read(b)) != -1) {
                bos.write(b, 0, length);
                bos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (con != null) {
                    con.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (imageFile != null) {
        }
    }

    /**
     * 获取图片的本地存储路径。
     *
     * @param imageUrl 图片的URL地址。
     * @return 图片的本地存储路径。
     */
    public static String getImagePath(String imageUrl) {
        int lastSlashIndex = imageUrl.lastIndexOf("/");
        String imageName = imageUrl.substring(lastSlashIndex + 1);
        String imageDir = Environment.getExternalStorageDirectory().getPath()
                + "/university/pics";
        File file = new File(imageDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        String imagePath = imageDir + "/" + imageName;
        return imagePath;
    }

    @Override
    public void run() {
        String filePath = getImagePath(url);
        File file = new File(filePath);
        if (file.exists()) {
            return;
        }
        downloadImage(url);
    }

    /**
     * 判断手机是否有SD卡。
     *
     * @return 有SD卡返回true，没有返回false。
     */
    public static boolean hasSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }


    /**
     * 计算sdcard上的剩余空间 * @return
     */
    public static int freeSpaceOnSd() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
                .getPath());
        double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat
                .getBlockSize()) / MB;

        return (int) sdFreeMB;
    }
}
