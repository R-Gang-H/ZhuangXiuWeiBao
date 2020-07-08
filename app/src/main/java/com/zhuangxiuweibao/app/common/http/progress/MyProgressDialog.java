package com.zhuangxiuweibao.app.common.http.progress;

import android.app.Activity;

import com.xiasuhuei321.loadingdialog.view.LoadingDialog;


/**
 * 作者： haoruigang on 2017-11-28 11:15:18
 * 类描述：全局dialog
 */
public class MyProgressDialog extends LoadingDialog {

    private Activity activity;
    private boolean isDismiss = false;
    public boolean isShow;

    /**
     * 不可取消(true不可，false可取消)
     *
     * @param activity
     * @param isDismiss
     */
    public MyProgressDialog(Activity activity, boolean isDismiss) {
        super(activity);
        this.isDismiss = isDismiss;
        this.activity = activity;
        init();
    }

    /**
     * 默认可取消
     *
     * @param activity
     */
    public MyProgressDialog(Activity activity) {
        super(activity);
        this.activity = activity;
        init();
    }

    private void init() {
        setLoadingText("加载中")
                .setSuccessText("加载成功")//显示加载成功时的文字
                .setInterceptBack(isDismiss)
                .show();

    }

    public void show() {
        if (this.activity != null && !this.activity.isFinishing()) {
            super.show();
            isShow = true;
        }
    }

    public boolean isShowing() {
        return isShow;
    }

    public void dismiss() {
        if (this.activity != null && !this.activity.isFinishing()) {
            super.close();
            isShow = false;
        }
    }

}
