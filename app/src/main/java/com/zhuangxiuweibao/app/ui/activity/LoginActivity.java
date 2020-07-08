package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.UserEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.tv_pact)
    TextView mtvPact;
    @BindView(R.id.et_mobile)
    EditText etMobile;
    @BindView(R.id.tv_code)
    TextView tvCode;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.tv_login)
    TextView tvLogin;

    private CountDownTimer countDownTimer;
    private String phoneNum, code;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        mtvPact.setText(Html.fromHtml(getResources().getString(R.string.pact)));
        U.setSpannable(mtvPact, 10, 16, widget -> {// 《用户协议》
            startActivity(new Intent(this, AboutUsActivity.class)
                    .putExtra("type", "2"));
        });
        U.setSpannable(mtvPact, 17, mtvPact.length(), widget -> {// 《隐私协议》
            startActivity(new Intent(this, AboutUsActivity.class)
                    .putExtra("type", "1"));
        });
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.tv_code, R.id.tv_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_code:
                getRandomCode();
                break;
            case R.id.tv_login:
                login();
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void getRandomCode() {
        phoneNum = etMobile.getText().toString();
        if (phoneNum.length() != 11) {
            U.showToast("请输入正确的手机号码");
            return;
        }
        etCode.setFocusable(true);
        etCode.setFocusableInTouchMode(true);
        etCode.requestFocus();
        HttpManager.getInstance().doRandomCode(this.getLocalClassName(), phoneNum,
                new HttpCallBack<BaseDataModel<UserEntity>>(this, true) {
                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> date) {
                        countDown();
//                        String code = date.getData().get(0).getCode();
//                        etCode.setText(code);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("LoginActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(LoginActivity.this);
                            return;
                        }
                        U.showToast(errorMsg);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("LoginActivity", throwable.getMessage());
                    }
                });
    }

    // 倒计时
    private void countDown() {
        phoneNum = etMobile.getText().toString();
        if (phoneNum.length() != 11) {
            U.showToast("请输入正确的手机号码");
            return;
        }
        tvCode.setClickable(false);
        if (countDownTimer == null)
            countDownTimer = new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long l) {
                    tvCode.setText(String.format("%sS", l / 1000));
                }

                @Override
                public void onFinish() {
                    tvCode.setClickable(true);
                    tvCode.setText("获取验证码");
                    tvCode.setOnClickListener(view -> getRandomCode());
                }
            };
        countDownTimer.start();
    }

    /**
     * haoruigang 2018-3-30 10:20:01 登录接口
     */
    private void login() {
        phoneNum = etMobile.getText().toString();
        if (U.isEmpty(phoneNum)) {
            U.showToast("账号不能为空");
            return;
        }
        code = etCode.getText().toString();
        if (U.isEmpty(code)) {
            U.showToast("验证码不能为空");
            return;
        }
        UserManager.getInstance().login(this, phoneNum, code);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
