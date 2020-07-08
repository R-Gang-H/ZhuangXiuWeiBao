package com.zhuangxiuweibao.app.common.utils.alicloud.oss.callback;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;

/**
 * Created by haoruigang on 2017/8/31.
 */

public interface Callback<T1, T2> {

    void onSuccess(T1 request, T2 result);

    void onFailure(T1 request, ClientException clientException, ServiceException serviceException);
}
