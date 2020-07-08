package com.zhuangxiuweibao.app.common.http.callback;

/**
 * 作者： haoruigang on 2017-11-28 11:12:56.
 * 类描述：网络回调接口类
 */
public interface IHttpCallBack<T> {
    void onSuccess(T data);

    void onFail(int statusCode, String errorMsg);

    void onError(Throwable throwable);
}
