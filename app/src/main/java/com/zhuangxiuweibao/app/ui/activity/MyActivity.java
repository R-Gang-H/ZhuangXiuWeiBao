package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.UserEntity;
import com.zhuangxiuweibao.app.common.user.ToUIEvent;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.GlideUtils;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.ui.adapter.MyAdapter;
import com.zhuangxiuweibao.app.ui.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的 住户版
 */
public class MyActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_avatar)
    RoundedImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.iv_qrcode)
    ImageView ivQrcode;
    @BindView(R.id.tv_huzhu)
    TextView tvHuzhu;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    int[] resId = {R.mipmap.icon_famil, R.mipmap.icon_my_home, R.mipmap.icon_main_order,
            R.mipmap.icon_centsoft, R.mipmap.icon_help};
    String[] resText = {"我的联系人", "我的住房", "维保订单"
            //, "在线客服", "帮助"
    };

    List<UserBean> userList = new ArrayList<>();
    MyAdapter settingAdapter;

    private UserEntity userData;

    @Override
    public int getLayoutId() {
        return R.layout.activity_my;
    }

    @Override
    public void initView() {
        tvTitle.setText("我的");
        rlNextButton.setVisibility(View.VISIBLE);
        ivTitleRight.setBackgroundResource(R.mipmap.icon_setting);
        LayoutManager.getInstance().initRecyclerView(recyclerView, true);
        settingAdapter = new MyAdapter(userList, this, this);
        recyclerView.setAdapter(settingAdapter);

        userData = UserManager.getInstance().userData;
        GlideUtils.setGlideImg(this, userData.getHeadImage(), R.mipmap.icon_avatar, ivAvatar);
        tvName.setText(userData.getName());
        ivQrcode.setVisibility(userData.getIsHuzhu().equals("1") ? View.VISIBLE : View.GONE);
        tvHuzhu.setText(userData.getIsHuzhu().equals("1") ? "户主" : "家庭成员");// 1 是户主 2不是户主
    }

    @Override
    public void initData() {
        this.userList.clear();
        for (int i = 0; i < resText.length; i++) {
            userList.add(UserBean.builder().resId(resId[i]).userText(resText[i]).build());
        }
        settingAdapter.setData(userList);
    }

    @OnClick({R.id.rl_back_button, R.id.rl_next_button, R.id.rl_title, R.id.iv_qrcode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.rl_next_button:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.rl_title:
                startActivity(new Intent(this, MyInfoActivity.class));
                break;
            case R.id.iv_qrcode:
                startActivity(new Intent(this, VCardActivity.class));
                break;
        }
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        switch (position) {
            case 0:// 家庭成员
                startActivity(new Intent(this, ContactActivity.class)
                        .putExtra("type", "2"));// 2 家庭成员
                break;
            case 1:// 我的住房
                startActivity(new Intent(this, MyHouseActivity.class));
                break;
            case 2:// 维保订单
                startActivity(new Intent(this, WeiBaoOrderActivity.class));
                break;
        }
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        switch (event.getTag()) {
            case ToUIEvent.USERINFO_EVENT:
                initData();
                break;
        }
    }

}
