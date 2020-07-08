package com.zhuangxiuweibao.app.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.bean.UserEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.Constant;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.utils.permissions.PermissionCallBackM;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.GridRecyclerView;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.ItemOffsetDecoration;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.ui.adapter.ServerNeedAdapter;
import com.zhuangxiuweibao.app.ui.bean.UserBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 房屋综合服务（住户版）
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class HouseGeneralServiceActivity extends BaseActivity implements ViewOnItemClick {

    ArrayList<UserBean> serverNeed = new ArrayList<>();

    private int[] serverImage = new int[]{R.mipmap.icon_service_y, R.mipmap.icon_horn, //R.mipmap.icon_door_card,
            R.mipmap.icon_sos_help, R.mipmap.icon_scan, R.mipmap.icon_noti};
    private String[] serverName = new String[]{"住房维保需求", "小喇叭", //"我的门禁卡",
            "紧急求助SOS", "扫一扫", "社区通知"};

    @BindView(R.id.recycler_view)
    GridRecyclerView recyclerView;
    @BindView(R.id.ib_colse)
    ImageButton ibColse;

    private UserEntity userData;

    @Override
    public int getLayoutId() {
        return R.layout.activity_house_general_service;
    }

    @Override
    public void initView() {
        userData = UserManager.getInstance().userData;
        VillageStatus(getIntent().getStringExtra("Status"));
    }

    private void VillageStatus(String status) {
        if (U.isNotEmpty(status)) {
            //社区小喇叭状态 0显示1不显示
            serverNeed.clear();
            for (int i = 0; i < (userData.getFirstIdentity().equals("3")
                    ? serverImage.length : serverImage.length - 1); i++) {// 大妈权限
                if (serverName[i].equals("小喇叭") && status.equals("1")) {
                    //社区小喇叭状态 0显示1不显示
                    continue;
                }
                serverNeed.add(UserBean.builder().resId(serverImage[i]).userText(serverName[i]).build());
            }
            setupRecyclerView();
        }
    }

    private void setupRecyclerView() {
        final Context context = recyclerView.getContext();
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        final int spacing = getResources().getDimensionPixelOffset(R.dimen.margin_size_10);
        recyclerView.setAdapter(new ServerNeedAdapter(serverNeed, this, this));
        recyclerView.addItemDecoration(new ItemOffsetDecoration(spacing));
        runLayoutAnimation(recyclerView, R.anim.grid_layout_animation_from_bottom, false);
    }

    private void runLayoutAnimation(final RecyclerView recyclerView, final int ResourceId, boolean isFinish) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, ResourceId);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
        if (isFinish && controller.isDone()) {//动画完成
            new Handler().postDelayed(() -> {
                finish();
                overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
            }, 200);
        }
    }


    @Override
    public void initData() {
    }

    @Override
    public void setOnItemClickListener(View view, int postion) {
        //"住房维保需求", "小喇叭", "我的门禁卡", "紧急求助SOS", "扫一扫", "社区通知"
        switch (serverNeed.get(postion).getUserText()) {
            case "住房维保需求":// 住房维保需求
                startActivity(new Intent(this, DemandStep1_2Activity.class));
                break;
            case "小喇叭":// 小喇叭
                if (serverNeed.get(postion).getUserText().equals("小喇叭")) {
                    startActivity(new Intent(this, NeedReleaseActivity.class)
                            .putExtra("type", "2"));// 2：发布小喇叭
                } else {
                    // 我的门禁卡
                    // 紧急求助SOS
                    startActivity(new Intent(this, SosHelpActivity.class));
                }
                break;
            case "我的门禁卡":// 我的门禁卡
                break;
            case "紧急求助SOS":// 紧急求助SOS
                startActivity(new Intent(this, SosHelpActivity.class));
                break;
            case "扫一扫":// 扫一扫
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
                                Intent intent = new Intent(HouseGeneralServiceActivity.this, QRCodeActivity.class);
                                startActivityForResult(intent, QRCodeActivity.REQUEST_CODE);
                            }

                            @Override
                            public void onPermissionDeniedM(int requestCode, String... perms) {
                                LogUtils.e(HouseGeneralServiceActivity.this, "TODO: CAMERA Denied", Toast.LENGTH_SHORT);
                            }
                        });
                break;
            case "社区通知":// 社区通知
                if (userData.getFirstIdentity().equals("3")) {// 大妈权限
                    startActivity(new Intent(this, ShequNotifiyActivity.class));
                }
                break;
        }
    }

    @OnClick(R.id.ib_colse)
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.ib_colse:
                if (recyclerView.getAdapter() != null) {
                    runLayoutAnimation(recyclerView, R.anim.grid_layout_animation_from_top, true);
                } else {
                    finish();
                    overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
                }
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
        Dialog dialog = new AlertDialog.Builder(HouseGeneralServiceActivity.this)
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
        HttpManager.getInstance().doAddlinju("HouseGeneralServiceActivity", hostId
                , new HttpCallBack<BaseDataModel<UserEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        U.showToast("成功");
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("HouseGeneralServiceActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(HouseGeneralServiceActivity.this);
                            return;
                        }
                        U.showToast(errorMsg);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("HouseGeneralServiceActivity", throwable.getMessage());
                    }
                });
    }

}
