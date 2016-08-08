package com.Lbins.GuirenApp.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;

public class FileUtils {

    /**
     * 保存图片到指定的目录
     *
     * @param bit
     * @param fileName 文件名
     * @return
     */
    public static String saveBitToSD(Bitmap bit, String fileName) {
        if (bit == null || bit.isRecycled()) return "";

        File file = new File(Environment.getExternalStorageDirectory(), "/uniapp/photoCache");
        File dirFile = new File(file.getAbsolutePath());
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File pathFile = new File(dirFile, fileName);
        if (pathFile.exists()) {
            return pathFile.getAbsolutePath();
        } else {
            ImageUtils.Bitmap2File(bit, pathFile.getAbsolutePath());
            return pathFile.getAbsolutePath();
        }
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }


    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     *
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }
}
