package com.zhuangxiuweibao.app.common.http;

import android.os.Build;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.Constant;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.RSAUtils;
import com.zhuangxiuweibao.app.common.utils.U;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author haoruigang
 * @Package com.haoruigang.okhttpdome
 * @project OkHttpDome
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2017/11/26 14:05
 */
public class OkHttpUtils {

    /**
     * 加密的参数时调用
     *
     * @param url
     * @param encodeMap 加密//     * @param obj       数据模型
     * @return
     */
    public static void getOkHttpJsonRequest(String tag, String url, Map<String, String> encodeMap, HttpCallBack callBack) {
//        JSONObject jsonObj = new JSONObject(encodeMap);
//        String jsonParams = jsonObj.toString();
//        LogUtils.e(tag, "请求:" + url + "    加密前的参数：" + jsonParams);

        if (encodeMap != null) {
            if (!url.contains("/startUp") && !url.contains("/code") && !url.contains("/login") && !url.contains("/autoLogin")) {
                String token = U.MD5(UserManager.getInstance().userData.getToken() + "_" + Constant.API_KEY);
                encodeMap.put("uid", UserManager.getInstance().userData.getUid());
                encodeMap.put("token", token);
            }
            encodeMap.put("deviceId", Build.SERIAL);
            encodeMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
            encodeMap.put("appKey", U.MD5(Constant.API_KEY));
            LogUtils.e(tag, "请求:" + url + "请求参数----" + encodeMap.toString());

            // RSA
            try {
                // 从字符串中得到公钥
                RSAUtils.loadPublicKey(Constant.PUBLIC_KEY);
                // 加密
                String encryptByte = RSAUtils.encryptWithRSA(U.transMap2String(encodeMap));
//                Logger.e("加密：" + encryptByte);
                HashMap<String, String> params = new HashMap<>();
                params.put("params", encryptByte);  // 加密的参数串
                com.lzy.okhttputils.OkHttpUtils.post(url)
                        .params(params)
                        .execute(callBack);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            com.lzy.okhttputils.OkHttpUtils.post(url)
                    .execute(callBack);
        }
    }

    /**
     * 不加密的参数时调用
     *
     * @param url
     * @param encodeMap
     * @param map
     * @param callBack
     */
    public static void getOkHttpJsonRequest(String tag, String url, Map<String, String> encodeMap, Map<String, String> map, HttpCallBack callBack) {

        if (map != null && encodeMap != null) {
            if (!url.contains("/startUp") && !url.contains("/code") && !url.contains("/login") && !url.contains("/autoLogin")) {
                String token = U.MD5(UserManager.getInstance().userData.getToken() + "_" + Constant.API_KEY);
                encodeMap.put("uid", UserManager.getInstance().userData.getUid());
                encodeMap.put("token", token);
            }
            encodeMap.put("deviceId", Build.SERIAL);
            encodeMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
            encodeMap.put("appKey", U.MD5(Constant.API_KEY));
            LogUtils.e(tag, "请求:" + url + ",请求参数----" + encodeMap.toString());

            // RSA
            try {
                // 从字符串中得到公钥
                RSAUtils.loadPublicKey(Constant.PUBLIC_KEY);
                // 加密
                String encryptByte = RSAUtils.encryptWithRSA(U.transMap2String(encodeMap));
                Logger.e("不加密：" + U.transMap2String(map));
                Logger.e("加密：" + encryptByte);

                HashMap<String, String> params = new HashMap<>();
                params.put("params", encryptByte);  // 加密的参数串
                params.putAll(map);   // 不加密的参数
                com.lzy.okhttputils.OkHttpUtils.post(url)
                        .params(params)
                        .execute(callBack);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            com.lzy.okhttputils.OkHttpUtils.post(url)
                    .execute(callBack);
        }
    }

    /**
     * Post String
     *
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @param body
     * @return
     * @throws Exception
     */
    public static HttpResponse doPost(String host, String path, String method,
                                      Map<String, String> headers,
                                      Map<String, String> querys,
                                      String body)
            throws Exception {
        HttpClient httpClient = wrapClient(host);

        HttpPost request = new HttpPost(buildUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        if (!TextUtils.isEmpty(body)) {
            request.setEntity(new StringEntity(body, "utf-8"));
        }

        return httpClient.execute(request);
    }

    private static String buildUrl(String host, String path, Map<String, String> querys) throws UnsupportedEncodingException {
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(host);
        if (!TextUtils.isEmpty(path)) {
            sbUrl.append(path);
        }
        if (null != querys) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, String> query : querys.entrySet()) {
                if (0 < sbQuery.length()) {
                    sbQuery.append("&");
                }
                if (TextUtils.isEmpty(query.getKey()) && !TextUtils.isEmpty(query.getValue())) {
                    sbQuery.append(query.getValue());
                }
                if (!TextUtils.isEmpty(query.getKey())) {
                    sbQuery.append(query.getKey());
                    if (!TextUtils.isEmpty(query.getValue())) {
                        sbQuery.append("=");
                        sbQuery.append(URLEncoder.encode(query.getValue(), "utf-8"));
                    }
                }
            }
            if (0 < sbQuery.length()) {
                sbUrl.append("?").append(sbQuery);
            }
        }

        return sbUrl.toString();
    }

    private static HttpClient wrapClient(String host) {
        HttpClient httpClient = new DefaultHttpClient();
        return httpClient;
    }

}
