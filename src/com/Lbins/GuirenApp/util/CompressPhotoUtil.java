package com.Lbins.GuirenApp.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * author: liuzwei
 * Date: 2014/8/16
 * Time: 9:28
 * 压缩图片工具类
 */
public class CompressPhotoUtil {
    private static final int TARGET_WIDTH = 800;
    private static final int TARGET_HEIGHT = 800;

    /**
     * @param pathName 图片的路径名
     * @return 压缩好的图片路径
     */
    public static Bitmap compressBySize(String pathName) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;// 不去真的解析图片，只是获取图片的头部信息，包含宽高等；
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, opts);
        // 得到图片的宽度、高度；
        int imgWidth = opts.outWidth;
        int imgHeight = opts.outHeight;
        // 分别计算图片宽度、高度与目标宽度、高度的比例；取大于等于该比例的最小整数；
        int widthRatio = (int) Math.ceil(imgWidth / (float) TARGET_WIDTH);
        int heightRatio = (int) Math.ceil(imgHeight / (float) TARGET_HEIGHT);
        if (widthRatio > 1 || widthRatio > 1) {
            if (widthRatio > heightRatio) {
                opts.inSampleSize = widthRatio;
            } else {
                opts.inSampleSize = heightRatio;
            }
        }
        // 设置好缩放比例后，加载图片进内容；
        opts.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(pathName, opts);
        return bitmap;
    }

    /**
     * 将压缩好的图片进行保存
     *
     * @param bmp
     * @param filename
     * @return
     */
    public static String saveBitmap2file(Bitmap bmp, String filename, File savePth) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {
            if (!savePth.exists()) {
                boolean iscreat = savePth.mkdirs();// 创建照片的存储目录
                Log.e("YAO", "" + iscreat);
            }
            stream = new FileOutputStream(savePth + "/" + filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bmp.compress(format, quality, stream);
        return savePth + "/" + filename;
    }
}
