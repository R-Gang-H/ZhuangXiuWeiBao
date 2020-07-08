package com.zhuangxiuweibao.app.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.zxing.WriterException;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.BitmapUtils;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.ResourcesUtils;
import com.zhuangxiuweibao.app.common.utils.U;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddMemberActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_kinship)
    TextView tvKinship;
    @BindView(R.id.iv_qrcode)
    ImageView ivQrcode;

    TimePickerView pvTime;
    OptionsPickerView pvOptions;
    private List<String> list = null;
    private String kinship;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_member;
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

    /**
     * 关系选择器
     */
    private void optPicker() {
        String str = ResourcesUtils.readAssetsText(this, "family_relationship");
        //条件选择器
        if (pvOptions == null) {
            list = new ArrayList<>();
            try {
                JSONArray ja = new JSONArray(str);
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jj = ja.getJSONObject(i);
                    list.add(jj.optString("relationName"));
                }
            } catch (JSONException e) {
                LogUtils.tag("" + e.toString());
            }
            pvOptions = new OptionsPickerBuilder(this, (options1, option2, options3, v) -> {
                kinship = list.get(options1);
                tvKinship.setText(kinship);
            }).build();
            pvOptions.setPicker(list);
        }
        pvOptions.show();
    }


    @OnClick({R.id.rl_back_button, R.id.tv_kinship})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_kinship:
                optPicker();
                break;
        }
    }
}
