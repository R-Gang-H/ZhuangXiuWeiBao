package com.zhuangxiuweibao.app.ui.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 新增设施
 */
public class AddFacilityActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_unit)
    EditText etUnit;

    private String eventId;
    private String name, unit;
    ;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_facility;
    }

    @Override
    public void initView() {
        tvTitle.setText("设施");
        eventId = getIntent().getStringExtra("eventId");
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.rl_back_button, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.btn_confirm:
                if (validate()) {
                    addDisclosureColumn();
                }
                break;
        }
    }

    private boolean validate() {
        name = etName.getText().toString();
        if (U.isEmpty(name)) {
            U.showToast("请输入设施名称。");
            return false;
        }
        unit = etUnit.getText().toString();
        if (U.isEmpty(unit)) {
            U.showToast("请输入设施单位。");
            return false;
        }
        return true;
    }

    private void addDisclosureColumn() {
        HttpManager.getInstance().doAddDiscColumn("AddFacilityActivity", eventId, name, unit,
                new HttpCallBack<BaseDataModel>() {
                    @Override
                    public void onSuccess(BaseDataModel data) {
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("AddFacilityActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(AddFacilityActivity.this);
                            return;
                        } else if (statusCode == 10012) {
                            U.showToast("任务终止");
                            return;
                        }
                        U.showToast(errorMsg);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("AddFacilityActivity", throwable.getMessage());
                    }
                });
    }
}
