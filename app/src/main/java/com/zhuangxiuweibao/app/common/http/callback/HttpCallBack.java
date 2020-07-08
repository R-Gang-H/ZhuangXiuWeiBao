package com.zhuangxiuweibao.app.common.http.callback;

import android.app.Activity;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.lzy.okhttputils.callback.AbsCallback;
import com.orhanobut.logger.Logger;
import com.zhuangxiuweibao.app.common.http.progress.MyProgressDialog;
import com.zhuangxiuweibao.app.common.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者： haoruigang on 2017-11-28 11:11:51
 * 类描述：网络回调类
 */
public abstract class HttpCallBack<T> extends AbsCallback implements IHttpCallBack<T> {
    private MyProgressDialog dialog;

    public HttpCallBack() {
    }

    // 是否可取消请求，默认可取消  haoruigang  2017-11-28 11:12:09
    public HttpCallBack(Activity activity, boolean isDismiss) {
        if (isDismiss)
            dialog = new MyProgressDialog(activity, true);
        else
            dialog = new MyProgressDialog(activity);
        dialog.show();
    }

    public HttpCallBack(Activity activity) {
        dialog = new MyProgressDialog(activity);
        dialog.show();
    }

    private void dismiss() {
        if (null != dialog && dialog.isShowing())
            dialog.dismiss();
    }

    private Type getTType(Class<?> clazz) {
        Type mySuperClassType = clazz.getGenericSuperclass();
        Type[] types = ((ParameterizedType) mySuperClassType).getActualTypeArguments();
        if (types != null && types.length > 0) {
            return types[0];
        }
        return null;
    }

    //----------引入之前的代码-------------

    private int statusCode;
    private String data;
    private String errorMsg;

    @Override
    public Object parseNetworkResponse(okhttp3.Response response) throws Exception {
        return response.body().string();
    }

    //  成功回调
    @Override
    public void onSuccess(Object o, Call call, okhttp3.Response response) {

//      String data = response.toString();
//      LogUtils.e("lt---", "response:" + data);
        Logger.e("接口返回数据 " + o.toString());
//      Logger.json(o.toString());

        try {
            JSONObject jsonObject = new JSONObject(o.toString());
            statusCode = jsonObject.getInt("status");
            data = jsonObject.getString("data");
            errorMsg = jsonObject.getString("errorMsg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            dismiss();

            Type gsonType = getTType(this.getClass());
            if (statusCode == 0 && !TextUtils.isEmpty(o.toString())) {
                if ("class java.lang.String".equals(gsonType.toString())) {
                    onSuccess((T) o.toString());
                } else {
                    T o1 = new Gson().fromJson(o.toString(), gsonType);
                    onSuccess(o1);
                }
            } else {
                onFail(statusCode, errorMsg);
            }
        } catch (Exception e) {
            LogUtils.e("Exception", e.getMessage());
            onError(e);
        }
    }


    // 失败后的回调
    @Override
    public void onError(Call call, Response response, Exception e) {
        LogUtils.e("lt---", "error response" + response);
        super.onError(call, response, e);
        onError(e);
    }

}
