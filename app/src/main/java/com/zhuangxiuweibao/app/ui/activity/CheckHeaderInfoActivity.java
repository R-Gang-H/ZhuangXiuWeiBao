package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.UserEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.ResourcesUtils;
import com.zhuangxiuweibao.app.common.utils.U;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 校验户主信息/添加家庭成员信息
 */
public class CheckHeaderInfoActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rel_info)
    RelativeLayout mrelInfo;
    @BindView(R.id.img_scan)
    ImageView mimgScan;
    @BindView(R.id.lin_userinfo)
    LinearLayout mlinUserInfo;
    @BindView(R.id.tv_name)
    TextView mtvName;
    @BindView(R.id.tv_idcart)
    TextView mtvIdCard;
    @BindView(R.id.tv_bir)
    TextView mtvBir;
    @BindView(R.id.tv_address)
    TextView mtvAddress;
    @BindView(R.id.et_name)
    EditText metName;
    @BindView(R.id.tv_birthday)
    TextView mtvBirthday;
    @BindView(R.id.tv_kinship)
    TextView mtvKinship;


    TimePickerView pvTime;
    OptionsPickerView pvOptions;
    private List<String> list = null;

    //前一页传过来的想要成为的身份
    private String identity;
    //家庭成员-生日-时间戳
    private String time;
    //家庭关系
    private String kinship;
    //扫码获取
    private String houseId, hostId = "3";
    private String hourseName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_header_info;
    }

    @Override
    public void initView() {
        //前一页选择的身份
        identity = getIntent().getStringExtra("identity");
        //扫码获取
        houseId = getIntent().getStringExtra("houseId");
        hostId = getIntent().getStringExtra("hostId");
        identity();
    }

    @Override
    public void initData() {
        UserManager.getInstance().houseInfo(this, houseId, entity -> {
            UserManager.getInstance().userData.setHouseId(houseId);
            //XX小区XX楼XX单元XXX室
            hourseName = UserManager.getInstance().houseEntity.getHouseName();
            mtvAddress.setText(hourseName);
        });
    }

    /**
     * 验证户主身份信息
     */
    private void testHold() {
        String name = mtvName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            U.showToast("用户名称未识别，请重新扫描");
            return;
        }
        String idcard = mtvIdCard.getText().toString();
        if (TextUtils.isEmpty(idcard)) {
            U.showToast("身份证号未识别，请重新扫描");
            return;
        }
        String bir = mtvBir.getText().toString();
        if (TextUtils.isEmpty(bir)) {
            U.showToast("出生日期未识别，请重新扫描");
            return;
        }
        HttpManager.getInstance().testHold("CheckHeaderInfoActivity", houseId, name, idcard, bir,
                new HttpCallBack<BaseDataModel<UserEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        UserEntity userData = UserManager.getInstance().userData;
                        userData.setName(name);
                        userData.setBirthday(String.valueOf(DateUtils.getStringToDate(bir, "yyyyMMdd")));
                        userData.setHouseName(hourseName);
                        userData.setIsHuzhu("1");
                        userData.setFirstIdentity(identity);// 住户
                        userData.setSecondIdentity("1");// 户主
                        userData.setQrcode(String.format("tag=host&hostId=%s&houseId=%s", userData.getUid(), houseId));
                        //验证通过
                        dialogCheckResult();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CheckHeaderInfoActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CheckHeaderInfoActivity.this);
                        } else if (statusCode == 1009) {
                            U.showToast("身份验证失败");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CheckHeaderInfoActivity", throwable.getMessage());
                    }
                });
    }

    /**
     * 注册家庭成员信息
     */
    private void registMember() {
        String name = metName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            U.showToast("请输入姓名");
            return;
        }
        if (TextUtils.isEmpty(time)) {
            U.showToast("请选择生日");
            return;
        }
        if (TextUtils.isEmpty(kinship)) {
            U.showToast("请选择与户主关系");
            return;
        }
        HttpManager.getInstance().registMember("CheckHeaderInfoActivity",
                hostId, name, time, kinship, new HttpCallBack<BaseDataModel<UserEntity>>() {

                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        UserEntity userData = UserManager.getInstance().userData;
                        userData.setName(name);
                        userData.setBirthday(time);
                        userData.setHouseName(hourseName);
                        userData.setIsHuzhu("2");
                        userData.setFirstIdentity(identity);// 住户
                        userData.setSecondIdentity("2");// 家庭成员
                        //验证通过
                        dialogCheckResult();
                    }


                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CheckHeaderInfoActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CheckHeaderInfoActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CheckHeaderInfoActivity", throwable.getMessage());
                    }
                });
    }

    /**
     * 时间选择器
     */
    private void setTimePicker() {
        //时间选择器
        if (pvTime == null) pvTime = new TimePickerBuilder(this, (date, v) -> {
            time = date.getTime() / 1000 + "";
            mtvBirthday.setText(DateUtils.getDateToString(date.getTime(), "yyyy-MM-dd"));
        }).build();
        pvTime.show();
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
                mtvKinship.setText(kinship);
            }).build();
            pvOptions.setPicker(list);
        }
        pvOptions.show();
    }

    /**
     * 身份区分
     */
    private void identity() {
        if (identity.equals("0")) {//户主
            tvTitle.setText("检验户主信息");
            mimgScan.setVisibility(View.VISIBLE);
            mrelInfo.setVisibility(View.GONE);
            mlinUserInfo.setVisibility(View.GONE);
        } else if (identity.equals("1")) {//家庭成员
            tvTitle.setText("填写家庭成员信息");
            mimgScan.setVisibility(View.GONE);
            mrelInfo.setVisibility(View.GONE);
            mlinUserInfo.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置户主数据-head of houseHold,识别身份证后获取
     */
    private void setHOH(String[] idCards) {
        mtvName.setText(idCards[0]);
        mtvIdCard.setText(idCards[1]);
        mtvBir.setText(idCards[2]);
    }

    /**
     * 校验结果弹窗
     */
    private void dialogCheckResult() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_info_check, null);
        TextView tvGo = view.findViewById(R.id.tv_go);
        tvGo.setOnClickListener(v -> {
            //去主页
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        dialog.setView(view);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //扫描完成且识别出来
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3 && resultCode == 3) {//身份证请求结果
            mimgScan.setVisibility(View.GONE);
            mrelInfo.setVisibility(View.VISIBLE);
            //设置数据
            setHOH(data.getStringArrayExtra("idCards"));
        }
    }

    @OnClick({R.id.rl_back_button, R.id.img_scan, R.id.tv_rescan, R.id.tv_next, R.id.tv_birthday, R.id.tv_kinship, R.id.tv_next1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.img_scan:
            case R.id.tv_rescan:
                startActivityForResult(new Intent(this, CameraActivity.class), 3);
                break;
            case R.id.tv_next://户主下一步
                testHold();
                break;
            case R.id.tv_birthday://选择生日
                setTimePicker();
                U.hideKeyboard(this);
                break;
            case R.id.tv_kinship://选择关系
                optPicker();
                U.hideKeyboard(this);
                break;
            case R.id.tv_next1://家庭成员下一步
                registMember();
                break;
        }
    }

}
