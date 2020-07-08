package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.ToUIEvent;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import butterknife.Setter;

/**
 * 社区通知
 */
public class ShequNotifiyActivity extends BaseActivity implements View.OnClickListener {

    @BindViews({R.id.btn_server, R.id.btn_something, R.id.btn_unused, R.id.btn_no_tag})
    List<Button> radioButtons;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.et_link)
    EditText etLink;
    @BindView(R.id.tv_complete)
    TextView tvComplete;

    private String tag = "999";// 4.通知公告 5.活动报名 6.意见建议 999.不加标签
    private String xiaoquId;
    private String title, content, linkUrl;

    @Override
    public int getLayoutId() {
        return R.layout.activity_shequ_notifiy;
    }

    @Override
    public void initView() {
        tvTitle.setText("社区通知");
        rlBackButton.setVisibility(View.GONE);
        rlNextButton.setVisibility(View.VISIBLE);
        ivTitleRight.setImageResource(R.mipmap.icon_cancel);
        checkStatus(3);
        xiaoquId = UserManager.getInstance().userData.getXiaoquId();
    }

    @Override
    public void initData() {
        radioButtons.get(0).setOnClickListener(this);
        radioButtons.get(1).setOnClickListener(this);
        radioButtons.get(2).setOnClickListener(this);
        radioButtons.get(3).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //  4.通知公告 5.活动报名 6.意见建议 999.不加标签
        switch (v.getId()) {
            case R.id.btn_server:
                checkStatus(0);
                tag = "4";
                break;
            case R.id.btn_something:
                checkStatus(1);
                tag = "5";
                break;
            case R.id.btn_unused:
                checkStatus(2);
                tag = "6";
                break;
            case R.id.btn_no_tag:
                checkStatus(3);
                tag = "999";
                break;
        }
    }

    public void checkStatus(int index) {
        UserManager.apply(radioButtons, BTNSPEC, radioButtons.get(index));
    }

    //控制normal 状态的当前View 隐藏，其它空间仍然为显示
    final Setter<TextView, TextView> BTNSPEC = (view, value, index) -> {
        assert value != null;
        if (view.getId() == value.getId()) {
            view.setSelected(true);
        } else {
            view.setSelected(false);
        }
    };

    private void doReleaseCommuNotice() {
        HttpManager.getInstance().doReleaseCommuNotice("ShequNotifiyActivity",
                xiaoquId, title, content, tag, linkUrl, new HttpCallBack<BaseDataModel>() {
                    @Override
                    public void onSuccess(BaseDataModel data) {
                        U.showToast("发布社区通知成功!");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                        startActivity(new Intent(ShequNotifiyActivity.this, MainActivity.class));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("ShequNotifiyActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ShequNotifiyActivity.this);
                            return;
                        }
                        U.showToast(errorMsg);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("ShequNotifiyActivity", throwable.getMessage());
                    }
                });
    }

    @OnClick({R.id.rl_next_button, R.id.tv_complete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_next_button:
                finish();
                break;
            case R.id.tv_complete:
                if (validate()) {
                    return;
                }
                doReleaseCommuNotice();
                break;
        }
    }

    private boolean validate() {
        title = etTitle.getText().toString();
        if (U.isEmpty(title)) {
            U.showToast("请输入标题");
            return true;
        }
        content = etContent.getText().toString();
        if (U.isEmpty(content)) {
            U.showToast("请输入内容");
            return true;
        }
        linkUrl = etLink.getText().toString();
        return false;
    }
}
