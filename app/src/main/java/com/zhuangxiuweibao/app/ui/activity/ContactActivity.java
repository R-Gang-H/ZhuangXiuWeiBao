package com.zhuangxiuweibao.app.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.SosContactsEntity;
import com.zhuangxiuweibao.app.bean.UserEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.Constant;
import com.zhuangxiuweibao.app.common.user.ToUIEvent;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.ResourcesUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.utils.permissions.PermissionCallBackM;
import com.zhuangxiuweibao.app.common.view.LoadingFrameView;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.ui.adapter.ContactAdapter;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 家庭联系人
 */
public class ContactActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycler_view)
    XRecyclerView recyclerView;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;
    @BindView(R.id.li_menu)
    LinearLayout liMenu;
    @BindView(R.id.tv_add)
    TextView tvAdd;
    @BindView(R.id.tv_scan)
    TextView tvScan;

    ContactAdapter contactAdapter;
    List<SosContactsEntity> constactData = new ArrayList<>();
    private String openType;

    OptionsPickerView pvOptions;
    private List<String> list = null;

    @Override
    public int getLayoutId() {
        return R.layout.activity_contact;
    }

    @Override
    public void initView() {
        tvTitle.setText("联系人");
        openType = getIntent().getStringExtra("type");// 1 编辑个人信息 2 家庭成员
        liMenu.setVisibility(openType.equals("2") ? View.VISIBLE : View.GONE);
        LayoutManager.getInstance().init(this).iniXrecyclerView(recyclerView);
        contactAdapter = new ContactAdapter(constactData, this, this);
        recyclerView.setAdapter(contactAdapter);
    }

    @Override
    public void initData() {
        getFamily();
    }

    private void getFamily() {
        HttpManager.getInstance().doGetFamily(this.getLocalClassName(),
                new HttpCallBack<BaseDataModel<SosContactsEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<SosContactsEntity> data) {
                        constactData.clear();
                        constactData.addAll(data.getData());
                        contactAdapter.setData(constactData);
                        if (constactData.size() > 0) {
                            loadView.delayShowContainer(true);
                        } else {
                            loadView.setNoShown(true);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("ContactActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ContactActivity.this);
                            return;
                        }
                        U.showToast(errorMsg);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("ContactActivity", throwable.getMessage());
                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.tv_add, R.id.tv_scan})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_add:
                startActivity(new Intent(this, AddMemberActivity.class));
                break;
            case R.id.tv_scan:
                //点击事件
                requestPermission(
                        Constant.RC_AUDIO,
                        new String[]{Manifest.permission.CAMERA
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        getString(R.string.camera),
                        new PermissionCallBackM() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onPermissionGrantedM(int requestCode, String... perms) {
                                Intent intent = new Intent(ContactActivity.this, QRCodeActivity.class);
                                startActivityForResult(intent, QRCodeActivity.REQUEST_CODE);
                            }

                            @Override
                            public void onPermissionDeniedM(int requestCode, String... perms) {
                                LogUtils.e(ContactActivity.this, "TODO: CAMERA Denied", Toast.LENGTH_SHORT);
                            }
                        });
                break;
        }
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
                    if (house[0].replace("tag=", "").equals("house")) {
                        houseId = house[1].replace("houseId=", "");
                    } else {
                        hostId = house[1].replace("hostId=", "");
                        houseId = house[2].replace("houseId=", "");
                    }
                    AddLinju(hostId);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    U.showToast("二维码识别失败");
                }
            }
        }
    }

    //确认添加为邻居
    private void AddLinju(String hostId) {
        Dialog dialog = new AlertDialog.Builder(ContactActivity.this)
                .setTitle("是否确认添加对方为邻居")
                .setPositiveButton("确定", (dialog12, which) -> {
                    addlinju(hostId);
                    dialog12.dismiss();
                })
                .setNegativeButton("取消", (dialog1, which) -> {
                    dialog1.dismiss();
                })
                .create();
        dialog.show();
    }

    private void addlinju(String hostId) {
        HttpManager.getInstance().doAddlinju("ContactActivity", hostId
                , new HttpCallBack<BaseDataModel<UserEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        U.showToast("成功");
                        getFamily();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("ContactActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ContactActivity.this);
                            return;
                        }
                        U.showToast(errorMsg);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("ContactActivity", throwable.getMessage());
                    }
                });
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        SosContactsEntity contact = constactData.get(position - 1);
        if (openType.equals("1")) {// 编辑家庭成员
            optPicker(constactData.get(position - 1));
        } else {
            startActivity(new Intent(this, ContactInfoActivity.class)
                    .putExtra("contact", contact));
        }
    }

    /**
     * 关系选择器
     *
     * @param sosContactsEntity
     */
    private void optPicker(SosContactsEntity sosContactsEntity) {
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
                sosContactsEntity.setRelationName(relationName);
                EventBus.getDefault().post(new ToUIEvent(ToUIEvent.CONTACT_EVENT, sosContactsEntity));
                finish();
            }).build();
            pvOptions.setPicker(list);
        }
        pvOptions.show();
    }

}
