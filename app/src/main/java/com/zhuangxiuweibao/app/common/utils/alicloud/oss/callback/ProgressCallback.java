package com.zhuangxiuweibao.app.common.utils.alicloud.oss.callback;

/**
 * Created by haoruigang on 2017/8/31.
 */

public interface ProgressCallback<T1, T2> extends Callback<T1, T2> {
    void onProgress(T1 request, long currentSize, long totalSize);
}
