package com.zhuangxiuweibao.app.ui.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.SosContactsEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.ToUIEvent;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 紧急求助详情
 */
public class SosDetailActivity extends BaseActivity {

    @BindView(R.id.tv_sos_time)
    TextView tvSosTime;
    @BindView(R.id.tv_sos_data)
    TextView tvSosData;
    @BindView(R.id.tv_sos_content)
    TextView tvSosContent;

    private Timer timer;
    //设定倒计时时长 n 单位 s
    private int time = 10;
    private String sosText;

    @Override
    public int getLayoutId() {
        return R.layout.activity_sos_detail;
    }

    @Override
    public void initView() {
        sosText = getIntent().getStringExtra("sosText");
    }

    @Override
    public void initData() {
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (time > -1) {
                    time--;
                }
                handler.sendEmptyMessage((time == -1 ? 1 : 0));
            }
        }, 1000, 1000);
    }


    @OnClick(R.id.tv_submit)
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.tv_submit:
                finish();
                break;
        }
    }

    //初始化 Handler
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    tvSosTime.setText(String.valueOf(time));
                    break;
                case 1:
                    askSOS();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void askSOS() {
        HttpManager.getInstance().doAskSOS("SosDetailActivity",
                sosText, new HttpCallBack<BaseDataModel<SosContactsEntity>>(this) {
                    @Override
                    public void onSuccess(BaseDataModel<SosContactsEntity> data) {
                        U.showToast(sosText + "求助已发送");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("SosDetailActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(SosDetailActivity.this);
                            return;
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("SosDetailActivity", throwable.getMessage());
                    }
                });
    }


    @Override
    protected void onPause() {
        timer.cancel();
        super.onPause();
    }

}
