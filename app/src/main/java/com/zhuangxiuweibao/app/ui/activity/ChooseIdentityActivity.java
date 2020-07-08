package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.ui.adapter.UserTypeAdapter;
import com.zhuangxiuweibao.app.ui.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 选择身份
 */
public class ChooseIdentityActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_tips)
    TextView mtvTips;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
    @BindView(R.id.xrv_type_list)
    RecyclerView mxrvTypeList;

    private UserTypeAdapter adapter;
    private List<UserBean> list = new ArrayList<>();

    private String identity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_choose_identity;
    }

    @Override
    public void initView() {
        rlNextButton.setVisibility(View.VISIBLE);
        tvTitle.setText("选择身份");
//        mtvTips.setText(Html.fromHtml(getResources().getString(R.string.applets)));
        initList();
    }

    @Override
    public void initData() {

    }

    /**
     * 下一步判定
     */
    private void next() {
        if (TextUtils.isEmpty(identity)) {
            U.showToast("请选择身份");
            return;
        }
        startActivity(new Intent(this, ToBeHeadOfHouseholdActivity.class)
                .putExtra("identity", identity));
    }

    /**
     * 初始化列表
     */
    private void initList() {
        //添加数据
        list.add(UserBean.builder().isSelect(false).resId(R.mipmap.icon_building).userText("我是住户，也是户主").build());
        list.add(UserBean.builder().isSelect(false).resId(R.mipmap.icon_family).userText("我是家庭成员").build());
        list.add(UserBean.builder().isSelect(false).resId(R.mipmap.icon_manager).userText("我是工作人员").build());
        //初始化适配器
        adapter = new UserTypeAdapter(list, this, this);
        //初始化列表
        LayoutManager.getInstance().initRecyclerView(mxrvTypeList, true);
        mxrvTypeList.setAdapter(adapter);
    }

    @OnClick({R.id.rl_back_button, R.id.tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.tv_next:
                next();
                break;
        }
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        //点击事件
        for (int i = 0; i < list.size(); i++) {
            if (i == position) {
                list.get(i).setSelect(true);
            } else {
                list.get(i).setSelect(false);
            }
        }
        switch (position) {
            case 0:
                identity = "0";
                break;
            case 1:
                identity = "1";
                break;
            case 2:
                identity = "2";
                break;
        }
        adapter.update(list);
    }
}
