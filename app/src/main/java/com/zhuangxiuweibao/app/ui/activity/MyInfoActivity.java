package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.SosContactsEntity;
import com.zhuangxiuweibao.app.bean.UserEntity;
import com.zhuangxiuweibao.app.common.user.ToUIEvent;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.GlideUtils;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.ui.adapter.SosContactAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 个人信息
 */
public class MyInfoActivity extends BaseActivity {

    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
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
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public UserEntity userData;
    private List<SosContactsEntity> sosDataList = new ArrayList<>();
    private SosContactAdapter sosAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_info;
    }

    @Override
    public void initView() {
        rlNextButton.setVisibility(View.VISIBLE);
        ivTitleRight.setVisibility(View.GONE);
        tvTitle.setText("个人信息");
        tvTitleRight.setText("编辑");
        LayoutManager.getInstance().initRecyclerView(recyclerView, true);
        sosAdapter = new SosContactAdapter(sosDataList, this);
        recyclerView.setAdapter(sosAdapter);
    }

    @Override
    public void initData() {
        userData = UserManager.getInstance().userData;
        GlideUtils.setGlideImg(this, userData.getHeadImage(), R.mipmap.icon_avatar, ivAvatar);
        tvName.setText(userData.getName());
        String sex = userData.getSex();
        if (sex.equals("1")) {// 性别 1男 2女
            tvSex.setText("男");
        } else if (sex.equals("2")) {
            tvSex.setText("女");
        }
        tvBirthday.setText(DateUtils.getDateToString(Long.parseLong(userData.getBirthday()), "yyyy-MM-dd"));
        tvPhone.setText(userData.getMobile());
        tvDetailAddress.setText(userData.getHouseName());
        tvHuzhu.setText(userData.getIsHuzhu().equals("1") ? "户主" : "家庭成员");// 1 是户主 2不是户主
        sosAdapter.setData(userData.getSosContacts());
    }

    @OnClick({R.id.rl_back_button, R.id.rl_next_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.rl_next_button:
                startActivity(new Intent(this, EditInfoActivity.class));
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
