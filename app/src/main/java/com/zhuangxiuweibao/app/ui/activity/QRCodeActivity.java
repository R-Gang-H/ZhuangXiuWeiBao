package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.jaeger.library.StatusBarUtil;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.utils.U;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by haoruigang on 2017/10/19.
 * 定制化显示扫描界面
 */
public class QRCodeActivity extends BaseActivity {

    @BindView(R.id.cb_torch)
    CheckBox cbTorch;

    public static final int REQUEST_CODE = 666;

    @Override
    public int getLayoutId() {
        return R.layout.activity_qrcode;
    }

    @Override
    public void initView() {
        StatusBarUtil.setTranslucentForImageView(this, 0, null);
        cbTorch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            /**
             * 打开 true 关闭 false 闪光灯
             */
            CodeUtils.isLightEnable(isChecked);
            cbTorch.setText(String.format("%s手电筒", isChecked ? "关闭" : "打开"));
        });
    }

    @Override
    public void initData() {
        CaptureFragment captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();
    }


    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            if (!result.contains("tag")) {
                U.showToast("请扫描正确的二维码!");
                QRCodeActivity.this.finish();
                return;
            }
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            QRCodeActivity.this.setResult(RESULT_OK, resultIntent);
            QRCodeActivity.this.finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            QRCodeActivity.this.setResult(RESULT_OK, resultIntent);
            QRCodeActivity.this.finish();
        }
    };


    @OnClick({R.id.rl_back_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }

}
