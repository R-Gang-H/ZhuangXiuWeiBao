package com.zhuangxiuweibao.app.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
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
 * 校验工作人员信息
 */
public class CheckStaffInfoActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_name)
    EditText metName;
    @BindView(R.id.et_idcard)
    EditText metIdcard;

    private Dialog mDialog;
    private AlertDialog.Builder mBuilder;
    private TextView tv_go;
    private String identity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_staff_info;
    }

    @Override
    public void initView() {
        tvTitle.setText("核验工作人员信息");
    }

    @Override
    public void initData() {
        identity = getIntent().getStringExtra("identity");
    }

    /**
     * 校验工作人员信息
     */
    private void testWorker() {
        String name = metName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            U.showToast("请输入姓名");
            return;
        }
        String idcard = metIdcard.getText().toString();
        if (TextUtils.isEmpty(idcard)) {
            U.showToast("请输入身份证号");
            return;
        }
        HttpManager.getInstance().testWorker("CheckStaffInfoActivity",
                UserManager.getInstance().userData.getMobile(), name, idcard,
                new HttpCallBack<BaseDataModel<UserEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        UserEntity userEntity = data.getData().get(0);
                        UserEntity userData = UserManager.getInstance().userData;
                        userData.setName(userEntity.getName());
                        userData.setFirstIdentity(identity);// 用户身份 2 是工作人员
                        userData.setDepartment(userEntity.getDepartment());
                        dialogCheckResult();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CheckStaffInfoActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1004) {
                            U.showToast("您输入的身份证号有误");
                            return;
                        } else if (statusCode == 10010) {
                            U.showToast("身份验证失败");
                            return;
                        } else if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CheckStaffInfoActivity.this);
                            return;
                        }
                        U.showToast(errorMsg);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CheckStaffInfoActivity", throwable.getMessage());
                    }
                });
    }

    /**
     * 校验结果弹窗
     */
    private void dialogCheckResult() {
        if (mBuilder == null) mBuilder = new AlertDialog.Builder(this);
        if (mDialog == null) mDialog = mBuilder.create();
        mDialog.show();
        mDialog.setContentView(R.layout.dialog_info_check);
        //如果阴影有问题，请注释掉/打开下边这句代码
        mDialog.getWindow().setBackgroundDrawable(new BitmapDrawable());
        tv_go = mDialog.findViewById(R.id.tv_go);
        tv_go.setOnClickListener(v -> {
            //去主页
            startActivity(new Intent(CheckStaffInfoActivity.this, MainWorkActivity.class));
            finish();
        });
    }

    @OnClick({R.id.rl_back_button, R.id.tv_next1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_next1:
                testWorker();
                break;
        }
    }

}
