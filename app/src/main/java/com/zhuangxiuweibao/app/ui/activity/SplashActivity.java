package com.zhuangxiuweibao.app.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseModel;
import com.zhuangxiuweibao.app.bean.UserEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.GlideUtils;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Update by haoruigang on 2018-4-20 14:31:20
 */

public class SplashActivity extends BaseActivity {

    @BindView(R.id.iv_splash)
    ImageView ivSplash;

    private Timer timer;
    //设定倒计时时长 n 单位 s
    private int time = 1;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initView() {
        GlideUtils.setGlideImg(SplashActivity.this, "", R.mipmap.ic_splash, ivSplash);
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (time > 0) {
                    time--;
                }
                handler.sendEmptyMessage((time == 0 ? 1 : 0));
            }
        }, 1000, 1000);
    }

    @Override
    public void initData() {
//        CoOpen();
    }

    /**
     * 开屏页
     */
    private void CoOpen() {
        HttpManager.getInstance().doCoOpenCode(this.getLocalClassName(), new HttpCallBack<BaseModel<UserEntity>>() {
            @Override
            public void onSuccess(BaseModel<UserEntity> data) {
                GlideUtils.setGlideImg(SplashActivity.this, data.getData().getCoopen(), R.mipmap.ic_splash, ivSplash);
            }

            @Override
            public void onFail(int statusCode, String errorMsg) {
                LogUtils.d("doCoOpenCode", statusCode + ":" + errorMsg);
                if (statusCode == 1003) {//异地登录
                    U.showToast("该账户在异地登录!");
                    HttpManager.getInstance().dologout(SplashActivity.this);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtils.d("doCoOpenCode", throwable.getMessage());
            }
        });
    }

    // 跳转主页
    private void go2MainActivity() {
        String uid = (String) U.getPreferences("uid", "");
        String token = (String) U.getPreferences("token", "");
        if (!U.isEmpty(uid) && !U.isEmpty(token)) {
            LogUtils.e("----------", "autologin called");
            UserManager.getInstance().autoLogin(SplashActivity.this, uid, token);
        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }


    //初始化 Handler
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    go2MainActivity();
            }
            super.handleMessage(msg);
        }
    };

    @OnClick(R.id.btn_go)
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_go:
                handler.sendEmptyMessage(1);
                break;
        }
    }

    @Override
    protected void onPause() {
        if (timer != null) {
            timer.cancel();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }
}
