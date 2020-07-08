package com.zhuangxiuweibao.app.ui.activity;

import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.BaseModel;
import com.zhuangxiuweibao.app.bean.SosContactsEntity;
import com.zhuangxiuweibao.app.bean.UserEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.GlideUtils;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.ResourcesUtils;
import com.zhuangxiuweibao.app.common.utils.U;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 联系人信息
 */
public class ContactInfoActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_avatar)
    RoundedImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.tv_birthday)
    TextView tvBirthday;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_detail_address)
    TextView tvDetailAddress;
    @BindView(R.id.tv_huzhu)
    TextView tvHuzhu;

    private SosContactsEntity contactEntity;
    private String userId;
    private UserEntity userData;

    @Override
    public int getLayoutId() {
        return R.layout.activity_contact_info;
    }

    @Override
    public void initView() {
        tvTitle.setText("联系人信息");
    }

    @Override
    public void initData() {
        contactEntity = (SosContactsEntity) getIntent().getSerializableExtra("contact");
        if (U.isNotEmpty(contactEntity)) {
            userId = contactEntity.getContactId();
            getUserInfo();
        }
    }

    private void getUserInfo() {
        HttpManager.getInstance().doGetUserInfo("ContactInfoActivity",
                userId, new HttpCallBack<BaseModel<UserEntity>>(this) {
                    @Override
                    public void onSuccess(BaseModel<UserEntity> data) {
                        userData = data.getData();
                        GlideUtils.setGlideImg(ContactInfoActivity.this, userData.getHeadImage(),
                                R.mipmap.icon_avatar, ivAvatar);
                        tvName.setText(userData.getName());
                        String sex = userData.getSex();
                        if (sex.equals("1")) {// 性别 1男 2女
                            tvSex.setText("男");
                        } else {
                            tvSex.setText("女");
                        }
                        tvBirthday.setText(DateUtils.getDateToString(Long.parseLong(userData.getBirthday()), "yyyy-MM-dd"));
                        tvPhone.setText(userData.getMobile());
                        tvDetailAddress.setText(userData.getHouseName());
                        tvHuzhu.setText(userData.getIsHuzhu().equals("1") ? "户主" : "家庭成员");// 1 是户主 2不是户主
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("ContactInfoActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ContactInfoActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("ContactInfoActivity", throwable.getMessage());
                    }
                });

    }

    @OnClick({R.id.rl_back_button, R.id.tv_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_submit:
                optPicker();
                break;
        }
    }

    OptionsPickerView pvOptions;
    private List<String> list = null;

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
                String relationName = list.get(options1);
                setSosContanct(relationName);
            }).build();
            pvOptions.setPicker(list);
        }
        pvOptions.show();
    }

    private void setSosContanct(String relationName) {
        HttpManager.getInstance().doSetSosContanct("ContactInfoActivity",
                userId, relationName, new HttpCallBack<BaseDataModel<UserEntity>>(this) {
                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        boolean isExit = false;
                        UserEntity user = UserManager.getInstance().userData;
                        for (SosContactsEntity contact : user.getSosContacts()) {
                            if (contact.getContactId().equals(userId)) {
                                isExit = true;
                                break;
                            }
                        }
                        if (!isExit) {
                            user.getSosContacts().add(SosContactsEntity.builder()
                                    .contactId(userData.getUserId()).name(userData.getName())
                                    .relationName(relationName).mobile(userData.getMobile()).build());
                            GlideUtils.setGlideImg(ContactInfoActivity.this, userData.getHeadImage(),
                                    R.mipmap.icon_avatar, ivAvatar);
                            tvName.setText(userData.getName());
                            String sex = userData.getSex();
                            if (sex.equals("1")) {// 性别 1男 2女
                                tvSex.setText("男");
                            } else {
                                tvSex.setText("女");
                            }
                            U.showToast("设置成功");
                        } else {
                            U.showToast("已是该用户紧急联系人。");
                        }
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("ContactInfoActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ContactInfoActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("ContactInfoActivity", throwable.getMessage());
                    }
                });
    }
}
