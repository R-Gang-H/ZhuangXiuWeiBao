package com.zhuangxiuweibao.app.common.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.user.Constant;
import com.zhuangxiuweibao.app.common.utils.permissions.PermissionCallBackM;
import com.zhuangxiuweibao.app.ui.activity.BaseActivity;

import java.util.Hashtable;

/**
 * 二维码
 * Created by haoruigang on 2018/4/4 12:50.
 */

public class BitmapUtils {

    private static class SingletonHolder {
        static BitmapUtils INSTANCE = new BitmapUtils();
    }

    public static BitmapUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Bitmap getCompressedBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options,
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
     * 用字符串生成二维码
     *
     * @param str
     * @return
     * @throws WriterException
     */
    public Bitmap create2DCode(String str) throws WriterException {
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 480, 480, hints);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        // 二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * bitmap  to  uri
     *
     * @param context
     * @param bitmap
     * @return
     */
    public Uri bitmap2Uri(Context context, Bitmap bitmap) {
        if (bitmap == null) return null;
        return Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null, null));
    }

    //获取CALL_PHONE权限 haoruigang on 2018-4-3 15:29:46
    public void getCallPhone(Context context, String stuPhone) {
        ((BaseActivity) context).requestPermission(
                Constant.RC_CALL_PHONE,
                new String[]{Manifest.permission.CALL_PHONE},
                context.getString(R.string.rationale_call_phone),
                new PermissionCallBackM() {

                    @Override
                    public void onPermissionGrantedM(int requestCode, String... perms) {
                        LogUtils.e(context, "TODO: CALL_PHONE Granted");
                        context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + stuPhone)));
                    }

                    @Override
                    public void onPermissionDeniedM(int requestCode, String... perms) {
                        LogUtils.e(context, "TODO: CALL_PHONE Denied");
                    }
                });
    }

}
