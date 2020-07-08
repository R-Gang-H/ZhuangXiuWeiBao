package com.zhuangxiuweibao.app.common.utils.alicloud.oss;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.zhuangxiuweibao.app.MyApplication;
import com.zhuangxiuweibao.app.common.user.Constant;
import com.zhuangxiuweibao.app.common.utils.alicloud.oss.callback.ProgressCallback;

import java.io.File;

/**
 * Created by 1bu2bu-4 on 2016/9/2.
 */
public class AliYunOss {
    public OSS oss;

    private static volatile AliYunOss instance = null;

    public static AliYunOss getInstance(Context context) {
        synchronized (AliYunOss.class) {
            if (instance == null) {
                instance = new AliYunOss(context);
            }
        }
        return instance;
    }


    public AliYunOss(Context context) {
        String endpoint = Constant.ENDPOINT;
        // 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考后面的`访问控制`章节
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(Constant.accessKeyId, Constant.accessKeySecret);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
//        OSSLog.enableLog();
        oss = new OSSClient(MyApplication.getAppContext(), endpoint, credentialProvider, conf);
    }

    // Upload from local files. Use asynchronous API asyncPutObjectFromLocalFile
    public void upload(String fileName, String path, final ProgressCallback<PutObjectRequest, PutObjectResult> progressCallback) {
        // Creates the upload request
        PutObjectRequest put = new PutObjectRequest(Constant.ossBucket, fileName, path);
        put.setCRC64(OSSRequest.CRC64Config.YES);

        // Sets the progress callback and upload file asynchronously
        put.setProgressCallback((request, currentSize, totalSize) -> {
            if (progressCallback != null) {
                progressCallback.onProgress(request, currentSize, totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                progressCallback.onSuccess(request, result);
                Log.d("PutObject", "UploadSuccess");

                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
                // 只有设置了servercallback，这个值才有数据
                String serverCallbackReturnJson = result.getServerCallbackReturnBody();
                Log.d("servercallback", serverCallbackReturnJson);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                progressCallback.onFailure(request, clientExcepion, serviceException);
                // request exception
                if (clientExcepion != null) {
                    // client side exception,  such as network exception
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // service side exception
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        task.waitUntilFinished();
    }


    public void download(String fileName, final ProgressCallback<GetObjectRequest, GetObjectResult> progressCallback) {
        GetObjectRequest get = new GetObjectRequest(Constant.ossBucket, fileName);
        get.setCRC64(OSSRequest.CRC64Config.YES);

        //设置下载进度回调
        get.setProgressListener((request, currentSize, totalSize) -> {
            if (progressCallback != null) {
                progressCallback.onProgress(request, currentSize, totalSize);
            }
        });
        OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                progressCallback.onSuccess(request, result);
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                progressCallback.onFailure(request, clientExcepion, serviceException);
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        task.waitUntilFinished();
    }

    //判断文件是否存在
    public boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}

