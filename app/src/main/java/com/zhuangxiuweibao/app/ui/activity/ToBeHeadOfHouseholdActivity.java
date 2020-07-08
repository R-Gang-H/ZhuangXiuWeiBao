package com.zhuangxiuweibao.app.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.user.Constant;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.utils.permissions.PermissionCallBackM;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.ui.adapter.ToBeHeadOfHouseholdStepAdapter;
import com.zhuangxiuweibao.app.ui.bean.StepBean;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 成为户主/家庭成员/工作人员
 */
public class ToBeHeadOfHouseholdActivity extends BaseActivity {

    @BindView(R.id.tv_text)
    TextView mtvText;
    @BindView(R.id.rv_step_list)
    RecyclerView mxrvStepList;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private List<StepBean> list = new ArrayList<>();
    private ToBeHeadOfHouseholdStepAdapter adapter;

    private String identity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_to_be_head_of_household;
    }

    @Override
    public void initView() {
        getDataByIntent();
    }

    @Override
    public void initData() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QRCodeActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    //房子二维码： tag=house&houseId=1
                    //户主二维码： tag=host&hostId=1&houseId=1
                    if (TextUtils.isEmpty(result)) {
                        return;
                    }
                    String house[] = result.split("&");
                    String houseId = "";
                    String hostId = "";
                    if (house[0].replace("tag=", "").trim().equals("house")) {
                        houseId = house[1].replace("houseId=", "");
                    } else {
                        hostId = house[1].replace("hostId=", "");
                        houseId = house[2].replace("houseId=", "");
                    }
                    startActivity(new Intent(ToBeHeadOfHouseholdActivity.this, CheckHeaderInfoActivity.class)
                            .putExtra("identity", identity)
                            .putExtra("houseId", houseId)
                            .putExtra("hostId", hostId));
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    U.showToast("二维码识别失败");
                }
            }
        }
    }

    private void getDataByIntent() {
        identity = getIntent().getStringExtra("identity");
        String text = "";
        String item[] = new String[2];
        switch (identity) {
            case "0":
                tvTitle.setText("成为户主");
                text = "户主注册，请完成以下流程";
                item[0] = "扫描保障房维保专用二维码";
                item[1] = "核实户主身份信息，姓名，身份证号，出生日期";
                break;
            case "1":
                tvTitle.setText("成为家庭成员");
                text = "家庭成员注册,请完成以下流程";
                item[0] = "扫描户主的邀请二维码";
                item[1] = "上传您的身份信息，姓名，身份证号，出生日期";
                break;
            case "2":
                tvTitle.setText("成为工作人员");
                text = "工作人员注册,请完成以下流程";
                item[0] = "输入真实姓名和身份证号";
                item[1] = "核验手机号与身份是否匹配";
                break;
        }
        initList(text, item);
    }

    private void initList(String text, String item[]) {
        mtvText.setText(text);
        //准备数据
        for (String str : item)
            list.add(StepBean.builder().text(str).build());
        //初始化列表
        LayoutManager.getInstance().init(this).initRecyclerView(mxrvStepList, true);
        //获取适配器,添加数据
        adapter = new ToBeHeadOfHouseholdStepAdapter(list, this);
        mxrvStepList.setAdapter(adapter);
    }

    @OnClick({R.id.rl_back_button, R.id.tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_next:
                if (!identity.equals("2")) {
                    requestPermission(
                            Constant.RC_AUDIO,
                            new String[]{Manifest.permission.CAMERA
                                    , Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            getString(R.string.camera),
                            new PermissionCallBackM() {
                                @SuppressLint("MissingPermission")
                                @Override
                                public void onPermissionGrantedM(int requestCode, String... perms) {
                                    Intent intent = new Intent(ToBeHeadOfHouseholdActivity.this, QRCodeActivity.class);
                                    startActivityForResult(intent, QRCodeActivity.REQUEST_CODE);
                                }

                                @Override
                                public void onPermissionDeniedM(int requestCode, String... perms) {
                                    LogUtils.e(ToBeHeadOfHouseholdActivity.this, "TODO: CAMERA Denied", Toast.LENGTH_SHORT);
                                }
                            });
                } else {
                    startActivity(new Intent(this, CheckStaffInfoActivity.class)
                            .putExtra("identity", identity));
                }
                break;
        }
    }
}
