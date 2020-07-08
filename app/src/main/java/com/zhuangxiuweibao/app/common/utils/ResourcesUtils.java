package com.zhuangxiuweibao.app.common.utils;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.TextView;

import com.zhuangxiuweibao.app.MyApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import static com.luck.picture.lib.tools.PictureFileUtils.getDataColumn;
import static com.luck.picture.lib.tools.PictureFileUtils.isDownloadsDocument;
import static com.luck.picture.lib.tools.PictureFileUtils.isExternalStorageDocument;
import static com.luck.picture.lib.tools.PictureFileUtils.isGooglePhotosUri;
import static com.luck.picture.lib.tools.PictureFileUtils.isMediaDocument;


/**
 * 类描述：获取资源
 * 创建人：haoruigang
 * 创建时间：2017-11-20 19:08:56
 */

public class ResourcesUtils {

    /**
     * 获取字符串
     *
     * @param id
     * @param obj
     * @return
     */
    public static String getString(@StringRes int id, Object... obj) {
        String string = MyApplication.getAppContext().getResources().getString(id);
        if (null != obj && obj.length > 0)
            return String.format(string, obj);
        return string;
    }

    public static String[] getStrings(@ArrayRes int id) {
        String[] stringArray = MyApplication.getAppContext().getResources().getStringArray(id);
        return stringArray;
    }

    /**
     * 获取颜色
     *
     * @param color
     * @return
     */
    public static int getColor(@ColorRes int color) {
        return MyApplication.getAppContext().getResources().getColor(color);
    }

    /**
     * 获取图片
     *
     * @param drawable
     * @return
     */
    public static Drawable getDrawable(@DrawableRes int drawable) {
        return MyApplication.getAppContext().getResources().getDrawable(drawable);
    }

    /**
     * 文字中添加图片
     *
     * @param textView
     * @param imgResId
     * @param index
     * @param padding
     */
    public static void setTvaddDrawable(TextView textView, @DrawableRes int imgResId, int index, int padding) {
        if (imgResId == -1) {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(index == 1 ? imgResId : 0, index == 2 ? imgResId : 0, index == 3 ? imgResId : 0, index == 4 ? imgResId : 0);
            textView.setCompoundDrawablePadding(padding);
        }
    }

    /**
     * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param context
     * @param imageUri
     */
    @TargetApi(19)
    public static String getImageAbsolutePath(Context context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    /**
     * 读取assets中的txt文件
     *
     * @return
     */
    public static String readAssetsText(Context context, String fileName) {
        StringBuffer sb = new StringBuffer("");
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            InputStreamReader inputStreamReader = null;
            try {
                inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                LogUtils.tag("readAssetsText=" + e1);
            }
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
            } catch (IOException e) {
                LogUtils.tag("readAssetsText=" + e);
            }

        } catch (IOException e1) {

        }
        return sb.toString();
    }

    /**
     * 创建文件夹
     *
     * @param fileName 文件夹名字
     * @return 文件夹路径
     */
    public static String createNewFilePath(String fileName) {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + fileName);
        return file.toString();
    }

    /**
     * @param fileName
     * @return
     */
    public static String createNewFile(String fileName) {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + fileName);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.toString();
    }

    private static final String MAIN_SOFT_FOLDER_NAME = "MAIN_SOFT0";
    private static final String CACHE_FOLDER_NAME = "CACHE0";

    public static String getImageCachePath()//给图片一个存储路径
    {
        if (!isExistSDCard()) {
            return "";
        }

        String sdRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        String result = sdRoot +
                "/" + MAIN_SOFT_FOLDER_NAME + "/" + CACHE_FOLDER_NAME;
        if (new File(result).exists() && new File(result).isDirectory()) {
            return result;
        } else {
            return sdRoot;
        }
    }

    // 判断SD卡是否存在
    public static boolean isExistSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    /**
     * 根据Uri返回文件绝对路径
     * 兼容了file:///开头的 和 content://开头的情况
     */
    public static String getRealFilePathFromUri(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    //根据_data查找
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

}
