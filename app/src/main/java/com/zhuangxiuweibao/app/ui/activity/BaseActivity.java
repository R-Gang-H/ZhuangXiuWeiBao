package com.zhuangxiuweibao.app.ui.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.orhanobut.logger.Logger;
import com.zhuangxiuweibao.app.common.AppManager;
import com.zhuangxiuweibao.app.common.CrashHandler;
import com.zhuangxiuweibao.app.common.user.ToUIEvent;
import com.zhuangxiuweibao.app.common.utils.permissions.BasePermissionActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;
import androidx.core.content.PermissionChecker;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by haoruigang on 2018-4-3 09:51:22 继承权限父类
 */

public abstract class BaseActivity extends BasePermissionActivity {

    private Unbinder unbinder;
    public int pageIndex = 1;
    public String pageSize = "15";
    public Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mContext = this;
        unbinder = ButterKnife.bind(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);//注册EventBus
        }
        AppManager.getAppManager().addActivity(this);
        CrashHandler.getInstance().init(this);//初始化全局异常管理
        initView();
        initData();
        Logger.e(getComponentName().toString());
    }


    /********************* 子类实现 *****************************/
    // 获取布局文件
    public abstract int getLayoutId();

    // 初始化view
    public abstract void initView();

    // 数据加载
    public abstract void initData();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);//反注册EventBus
        }
        if (Util.isOnMainThread() && !this.isFinishing()) {
            Glide.with(getApplicationContext()).pauseRequests();
        }
    }

    //设置字体为默认大小，不随系统字体大小改而改变
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)//非默认值
            getResources();
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    /**
     * 权限检测
     *
     * @param permission 权限
     * @return
     */
    public boolean selfPermissionGranted(String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int targetSdkVersion = this.getApplicationInfo().targetSdkVersion;
            Log.e("lt", "targetSdkVersion=" + targetSdkVersion);
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = this.checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(this, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }
        Log.e("lt", "result" + result);
        return result;
    }

    //Eventbus
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(ToUIEvent event) {

    }

}
