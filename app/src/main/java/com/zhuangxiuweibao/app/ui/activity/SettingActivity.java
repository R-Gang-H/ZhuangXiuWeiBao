package com.zhuangxiuweibao.app.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.utils.CleanCacheUtil;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.ui.adapter.SettingAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置
 */
public class SettingActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    ArrayList<String> mArrSettingOptions;
    private SettingAdapter pAdapter;

    private int[] iconSetting = {//R.mipmap.ic_push_setting,R.mipmap.ic_priv_setting,
            R.mipmap.ic_clear_cache,
            R.mipmap.ic_user_agree, R.mipmap.ic_priv_clause,
            //R.mipmap.ic_recom,
            R.mipmap.ic_bus_coop
            //, R.mipmap.ic_about_us
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        tvTitle.setText("设置");

        mArrSettingOptions = new ArrayList<>();
//        mArrSettingOptions.add("推送设置");
//        mArrSettingOptions.add("隐私设置");
        mArrSettingOptions.add("清空缓存");
        mArrSettingOptions.add("用户协议");
        mArrSettingOptions.add("隐私条款");
//        mArrSettingOptions.add("把App推荐给朋友");
        mArrSettingOptions.add("商务合作");
//        mArrSettingOptions.add("关于我们");

        LayoutManager.getInstance().initRecyclerView(recyclerView, true);
        pAdapter = new SettingAdapter(mArrSettingOptions, iconSetting, this, this);
        recyclerView.setAdapter(pAdapter);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.rl_back_button, R.id.btn_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.btn_logout:
                showLogoutDialog();
                break;
        }
    }

    // 退出弹框
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("是否退出登录？")
                .setPositiveButton("确定", (d, which) -> {
                    HttpManager.getInstance().dologout((Activity) mContext);
                })
                .setNegativeButton("取消", (d, which) -> d.dismiss())
                .create().show();
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        switch (position) {
//            case 0://推送设置
//                break;
//            case 1://隐私设置
//                break;
            case 0://清空缓存
                SettingActivity.this.CleanCache();
                break;
            case 1://用户协议
                startActivity(new Intent(this, AboutUsActivity.class)
                        .putExtra("type", "2"));
                break;
            case 2://隐私条款
                startActivity(new Intent(this, AboutUsActivity.class)
                        .putExtra("type", "1"));
                break;
            case 3://商务合作
                Business();
                break;
        }
    }

    // 清除缓存
    private void CleanCache() {
        Dialog dialog = new AlertDialog.Builder(SettingActivity.this)
                .setTitle("提示")
                .setMessage("是否清除缓存？")
                .setPositiveButton("确定", (dialog1, which) -> {
                    CleanCacheUtil.clearAllCache(SettingActivity.this);
                    pAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("取消", (dialog12, which) -> dialog12.dismiss())
                .create();
        dialog.show();
    }

    //商务合作
    private void Business() {
        Dialog dialog = new AlertDialog.Builder(SettingActivity.this)
                .setTitle("商务合作请联系")
                .setMessage("Email: yslinde@hotmail.com")
                .setPositiveButton("确定", (dialog12, which) -> dialog12.dismiss())
                .create();
        dialog.show();
    }
}
