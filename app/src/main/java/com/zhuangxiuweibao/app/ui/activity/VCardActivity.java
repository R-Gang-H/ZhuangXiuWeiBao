package com.zhuangxiuweibao.app.ui.activity;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.BitmapUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 电子名片
 */
public class VCardActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_qrcode)
    ImageView ivQrcode;

    @Override
    public int getLayoutId() {
        return R.layout.activity_vcard;
    }

    @Override
    public void initView() {
        tvTitle.setText("电子名片");
    }

    @Override
    public void initData() {
        String QRCode = UserManager.getInstance().userData.getQrcode();
        try {
            Bitmap bitmapAct = BitmapUtils.getInstance().create2DCode(QRCode);//根据内容生成二维码
            ivQrcode.setImageBitmap(bitmapAct);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }
}
