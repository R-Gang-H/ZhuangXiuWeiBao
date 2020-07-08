package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.utils.U;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReplyActivity extends BaseActivity {

    @BindView(R.id.tv_title_left)
    TextView mtvTitleLeft;
    @BindView(R.id.tv_title_right)
    TextView mtvTitleRight;
    @BindView(R.id.tv_title)
    TextView mtvTitle;
    @BindView(R.id.et_content)
    EditText metContent;
    @BindView(R.id.iv_left)
    ImageView mIvLeft;
    @BindView(R.id.rl_next_button)
    RelativeLayout mrlNextButton;

    @Override
    public int getLayoutId() {
        return R.layout.activity_reply;
    }

    @Override
    public void initView() {
        String type = getIntent().getStringExtra("type");
        mtvTitleLeft.setVisibility(View.VISIBLE);
        mtvTitleLeft.setText("取消");
        mrlNextButton.setVisibility(View.VISIBLE);
        mtvTitleRight.setVisibility(View.VISIBLE);
        mtvTitleRight.setText("发送");
        mIvLeft.setVisibility(View.GONE);
        String title = "";
        if (type.equals("reply")) {
            title = "回复";
        } else {
            title = "同意并回复";
            metContent.setText("同意");
        }
        mtvTitle.setText(title);
    }

    @Override
    public void initData() {

    }

    private void setResult() {
        String content = metContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            U.showToast("请输入内容");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("content", content);
        this.setResult(1, intent);
        finish();
    }


    @OnClick({R.id.tv_title_left, R.id.tv_title_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_title_left:
                finish();
                break;
            case R.id.tv_title_right:
                setResult();
                break;
        }
    }
}
