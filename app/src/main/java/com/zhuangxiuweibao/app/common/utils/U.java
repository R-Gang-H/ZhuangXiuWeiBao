package com.zhuangxiuweibao.app.common.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.luck.picture.lib.config.PictureConfig;
import com.zhuangxiuweibao.app.MyApplication;
import com.zhuangxiuweibao.app.common.user.Constant;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 2019/4/10
 * Author: haoruigang
 * Description: com.zhuangxiuweibao.app.common.utils
 */
public class U {

    private static Toast toast;

    /**
     * Toast
     *
     * @param msg
     */
    public static void showToast(String msg) {
        if (MyApplication.getAppContext() != null) {
            if (toast == null) {
                toast = Toast.makeText(MyApplication.getAppContext(), msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                toast.setText(msg);
            }
            toast.show();
        }
    }

    /**
     * ???????????????
     *
     * @param argEditText
     */
    public static void hideSoftKeyboard(EditText argEditText) {
        InputMethodManager imm = (InputMethodManager) MyApplication.getAppContext().getSystemService
                (Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(argEditText.getWindowToken(), 0);
    }

    /***
     * ???????????????
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null) {
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * map???????????????
     *
     * @param map
     * @return
     */
    public static String transMap2String(Map map) {
        java.util.Map.Entry entry;
        StringBuilder sb = new StringBuilder();
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
            entry = (java.util.Map.Entry) iterator.next();
            sb.append(entry.getKey().toString()).append("=").append(null == entry.getValue() ? "" :
                    entry.getValue().toString()).append(iterator.hasNext() ? "&" : "");
        }
        return sb.toString();
    }

    /**
     * ?????????????????????
     */
    public static String getVersionName() {
        try {
            PackageInfo packageInfo = MyApplication.getAppContext().getPackageManager().getPackageInfo(MyApplication.getAppContext().getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "unknown version";
        }
    }

    /**
     * ????????????????????????
     *
     * @param context
     * @return
     */
    public static boolean isNetConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                return info.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }

    /**
     * ???????????????wifi??????
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;
        return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * ????????????????????????
     */
    public static void openNetSetting(Activity activity) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }

    /**
     * SharePreference????????????
     *
     * @param key
     * @param value
     */
    public static void savePreferences(String key, Object value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        }
        editor.apply();
    }

    /**
     * ??????key????????????????????????????????????SharePreference????????????
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object getPreferences(String key, Object defaultObject) {
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        if ("String".equals(type)) {
            return sp.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    /**
     * ???????????????????????????????????????????????????????????????
     *
     * @param str
     * @return
     */
    public static String stringFilter(String str) {
        str = str.replaceAll("???", "[").replaceAll("???", "]")
                .replaceAll("???", "!").replaceAll("???", ":");// ??????????????????
        String regEx = "[??????]"; // ?????????????????????
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * ??????????????????md5??????
     *
     * @param argString ??????
     * @return d5??????????????????
     */
    public static String MD5(String argString) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] btInput = argString.getBytes();
            // ??????MD5??????????????? MessageDigest ??????
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // ?????????????????????????????????
            mdInst.update(btInput);
            // ????????????
            byte[] md = mdInst.digest();
            // ????????????????????????????????????????????????
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String MD5_16(String argString) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] btInput = argString.getBytes();
            // ??????MD5??????????????? MessageDigest ??????
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // ?????????????????????????????????
            mdInst.update(btInput);
            // ????????????
            byte[] md = mdInst.digest();
            // ????????????????????????????????????????????????
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }

            String strResult = new String(str);
            return strResult.substring(8, 24);
            //return str.toString();
        } catch (Exception e) {
            Log.i("----", e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isPhoneNum(String phoneNumber) {
        return !TextUtils.isEmpty(phoneNumber) && phoneNumber.matches("0?(13|14|15|16|17|18|19)[0-9]{9}");
    }

    /**
     * ????????????????????????????????????
     *
     * @param email
     * @return
     */
    public static boolean isEMail(String email) {
        return !TextUtils.isEmpty(email) && email.matches("^[a-zA-Z0-9_-_.]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    }

    /**
     * ????????????null???????????????.
     *
     * @param str ??????????????????
     * @return ????????????String??????
     */
    public static String parseEmpty(String str) {
        if (str == null || "null".equals(str.trim())) {
            str = "";
        }
        return str.trim();
    }

    /**
     * ???????????????????????????????????????null?????????.
     *
     * @param str ??????????????????
     * @return true or false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * ????????????????????????????????????.
     *
     * @param str ??????????????????
     * @return ???????????????????????????:??????true?????????false
     */
    public static Boolean isNumberLetter(String str) {
        Boolean isNoLetter = false;
        String expr = "^[A-Za-z0-9]+$";
        if (str.matches(expr)) {
            isNoLetter = true;
        }
        return isNoLetter;
    }

    /**
     * ???????????????????????????.
     *
     * @param str ??????????????????
     * @return ??????????????????:??????true?????????false
     */
    public static Boolean isNumber(String str) {
        Boolean isNumber = false;
        String expr = "^[0-9]+$";
        if (str.matches(expr)) {
            isNumber = true;
        }
        return isNumber;
    }

    /**
     * ????????????????????????.
     *
     * @param str ??????????????????
     * @return ???????????????:??????true?????????false
     */
    public static Boolean isChinese(String str) {
        Boolean isChinese = true;
        String chinese = "[\u0391-\uFFE5]";
        if (!isEmpty(str)) {
            //?????????????????????????????????????????????????????????????????????????????????2????????????1
            for (int i = 0; i < str.length(); i++) {
                //??????????????????
                String temp = str.substring(i, i + 1);
                //???????????????????????????
                if (temp.matches(chinese)) {
                } else {
                    isChinese = false;
                }
            }
        }
        return isChinese;
    }

    /**
     * ???????????????????????????.
     *
     * @param str ??????????????????
     * @return ??????????????????:??????true?????????false
     */
    public static Boolean isContainChinese(String str) {
        Boolean isChinese = false;
        String chinese = "[\u0391-\uFFE5]";
        if (!isEmpty(str)) {
            //?????????????????????????????????????????????????????????????????????????????????2????????????1
            for (int i = 0; i < str.length(); i++) {
                //??????????????????
                String temp = str.substring(i, i + 1);
                //???????????????????????????
                if (temp.matches(chinese)) {
                    isChinese = true;
                } else {

                }
            }
        }
        return isChinese;
    }

    /**
     * ????????????????????????
     *
     * @param obj ??????
     * @return {@code true}: ??????<br>{@code false}: ?????????
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String && obj.toString().length() == 0) {
            return true;
        }
        if (obj instanceof Editable && obj.toString().length() == 0) {
            return true;
        }
        if (obj instanceof CharSequence && obj.toString().length() == 0) {
            return true;
        }
        if (obj.getClass().isArray() && Array.getLength(obj) == 0) {
            return true;
        }
        if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
            return true;
        }
        if (obj instanceof Map && ((Map) obj).isEmpty()) {
            return true;
        }
        if (obj instanceof SparseArray && ((SparseArray) obj).size() == 0) {
            return true;
        }
        if (obj instanceof SparseBooleanArray && ((SparseBooleanArray) obj).size() == 0) {
            return true;
        }
        if (obj instanceof SparseIntArray && ((SparseIntArray) obj).size() == 0) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (obj instanceof SparseLongArray && ((SparseLongArray) obj).size() == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * ????????????????????????
     *
     * @param obj ??????
     * @return {@code true}: ??????<br>{@code false}: ???
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * ????????????
     *
     * @param time ??????
     * @return {@code true}: ??????<br>{@code false}: ???
     */
    public static String timeEqual(String time) {
        String type = "0";//0 ????????????  1?????????????????????????????????  -1?????????????????????????????????
        long curTime = System.currentTimeMillis() / 1000;

        if (curTime < Long.valueOf(time)) {//??????
            return "1";
        } else if (curTime > Long.valueOf(time)) {
            return "-1";
        } else {
            return "0";
        }
    }

    /**
     * ??????????????????
     *
     * @param s
     * @return
     */
    public static String getReplaceTrim(String s) {
        return s.trim().replace(" ", "");
    }

    //???dp?????????px
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //???px?????????dp
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * ???????????????????????????16??????
     *
     * @param str
     * @return
     */
    public static byte[] SendS(String str) {
        byte[] ok = new byte[0];
        try {
            ok = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ok;
    }

    /**
     * 16?????????????????????????????????
     *
     * @param bytes
     * @return
     */
    public static String getString(byte[] bytes) {
        return new String(bytes, Charset.forName("UTF-8"));
    }


    /**
     * ?????????????????????????????????
     *
     * @param s
     * @return
     */
    public static String getString(String s) {
        int length = s.length();
        String context = "";
        //???????????????????????????????????????????????????????????????
        for (int i = 0; i < length; i++) {
            char codePoint = s.charAt(i);
            //?????????????????????emoji???????????????
            if (isEmojiCharacter(codePoint)) {
                //?????????????????????????????????
                String emoji = "{" + Integer.toHexString(codePoint) + "}";
                context = context + emoji;
                continue;
            }
            context = context + codePoint;
        }
        return context;
    }

    /**
     * ??????????????????
     *
     * @param codePoint
     * @return ??????????????? ??????false,?????? ?????????true
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }


    /**
     * ??????????????????????????????
     *
     * @param str
     * @return
     */
    public static String getEmoji(String str) {
        String string = str;
        String rep = "\\{(.*?)\\}";
        Pattern p = Pattern.compile(rep);
        Matcher m = p.matcher(string);
        while (m.find()) {
            String s1 = m.group().toString();
            String s2 = s1.substring(1, s1.length() - 1);
            String s3;
            try {
                s3 = String.valueOf((char) Integer.parseInt(s2, 16));
                string = string.replace(s1, s3);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return string;
    }

    /**
     * TextView???????????????????????????
     *
     * @param textView
     * @param start
     * @param end
     */
    public static void setSpannable(TextView textView, int start, int end, ClickableSpans click) {
        SpannableStringBuilder spanUser = new SpannableStringBuilder(textView.getText());
        spanUser.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                click.Clickable(widget);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                //???????????????
                ds.setUnderlineText(false);
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spanUser);
    }

    public interface ClickableSpans {
        void Clickable(@NonNull View widget);
    }

    public static int pictureToVideo(String pictureType) {
        if (!TextUtils.isEmpty(pictureType)) {
            if (pictureType.startsWith("video")) {
                return PictureConfig.TYPE_VIDEO;
            } else if (pictureType.startsWith("audio")) {
                return PictureConfig.TYPE_AUDIO;
            } else if (pictureType.contains("pdf") ||
                    pictureType.contains("xls") ||
                    pictureType.contains("doc") ||
                    pictureType.contains("zip")) {
                return Constant.TYPE_FILE;
            }
        }
        return PictureConfig.TYPE_IMAGE;
    }
}
