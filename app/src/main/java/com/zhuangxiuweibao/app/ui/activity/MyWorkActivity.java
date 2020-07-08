package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.UserEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
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
 * 我的 工作人员版
 */
public class MyWorkActivity extends BaseActivity implements ViewOnItemClick {

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
    @BindView(R.id.tv_huzhu)
    TextView tvHuzhu;
    @BindView(R.id.iv_arrow_right)
    ImageView ivArrowRight;
    @BindView(R.id.iv_qrcode)
    ImageView ivQrcode;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    int[] resId = {R.mipmap.icon_my_task, R.mipmap.icon_my_batch, R.mipmap.icon_notice_w, R.mipmap.icon_my_disc,
            R.mipmap.icon_main_order, R.mipmap.icon_my_help
    };
    String[] resText = {"我的任务", "我的审批", "我的群通知", "我的技术交底"//, "我的维保服务", "帮助"
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
        ivArrowRight.setVisibility(View.GONE);
        ivQrcode.setVisibility(View.GONE);
        LayoutManager.getInstance().initRecyclerView(recyclerView, true);
        settingAdapter = new MyAdapter(userList, this, this);
        recyclerView.setAdapter(settingAdapter);

        userData = UserManager.getInstance().userData;
        GlideUtils.setGlideImg(this, userData.getHeadImage(), R.mipmap.icon_avatar, ivAvatar);
        tvName.setText(userData.getName());
        tvHuzhu.setText(userData.getDepartment());
    }

    @Override
    public void initData() {
        for (int i = 0; i < resText.length; i++) {
            userList.add(UserBean.builder().resId(resId[i]).userText(resText[i]).build());
        }
        settingAdapter.setData(userList);
    }

    @OnClick({R.id.rl_back_button, R.id.rl_next_button, R.id.rl_title})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.rl_next_button:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.rl_title:
                break;
        }
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        // 1 任务列表 2 群通知列表 3 审批列表
        switch (position) {
            case 0://我的任务
                startActivity(new Intent(this, MyTaskActivity.class)
                        .putExtra("type", "1")
                        .putExtra("name", resText[position]));
                break;
            case 1://我的审批
                startActivity(new Intent(this, MyTaskActivity.class)
                        .putExtra("type", "3")
                        .putExtra("name", resText[position]));
                break;
            case 2://群通知
                startActivity(new Intent(this, MyTaskActivity.class)
                        .putExtra("type", "2")
                        .putExtra("name", resText[position]));
                break;
            case 3:// 我的技术交底
                startActivity(new Intent(this, MyTaskDisActivity.class));
                break;
//            case 3:// 我的维保服务
//                startActivity(new Intent(this, MyWeiBaoSerActivity.class));
//                break;
        }
    }

}
