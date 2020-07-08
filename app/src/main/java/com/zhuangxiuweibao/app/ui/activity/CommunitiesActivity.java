package com.zhuangxiuweibao.app.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.CommunityEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 关于社区
 */
public class CommunitiesActivity extends BaseActivity {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.img_abo)
    ImageView imgAbo;
    @BindView(R.id.tv_abo)
    TextView tvAbo;
    @BindView(R.id.tv_call)
    TextView tvCall;
    @BindView(R.id.tv_door)
    TextView tvDoor;

    @Override
    public int getLayoutId() {
        return R.layout.activity_communities;
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
        getDataByIntent();
    }

    private void getDataByIntent() {
        String shequId = getIntent().getStringExtra("shequId");
        shequInfo(shequId);
    }

    /**
     * 9.获取社区信息（完成）
     */
    private void shequInfo(String shequId) {
        HttpManager.getInstance().shequInfo("CommunitiesActivity", shequId,
                new HttpCallBack<BaseDataModel<CommunityEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CommunityEntity> data) {
                        setData(data.getData().get(0));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CommunitiesActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CommunitiesActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CommunitiesActivity", throwable.getMessage());
                    }
                });
    }

    /**
     * 设置值班员信息
     */
    private void setData(CommunityEntity entity) {
    }


    @OnClick({R.id.rl_back_button, R.id.tv_call, R.id.tv_door})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_call:
                break;
            case R.id.tv_door:
                break;
        }
    }
}
