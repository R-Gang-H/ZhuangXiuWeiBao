package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.HouseEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.ui.adapter.DemandAdapter;
import com.zhuangxiuweibao.app.ui.bean.DemandBean;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 发需求第一步和第二步
 */
public class DemandStep1_2Activity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_step)
    TextView tvStep;
    @BindView(R.id.tv_step_content)
    TextView tvStepContent;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_select_list)
    RecyclerView rvSelectList;

    private DemandAdapter adapter;
    private List<DemandBean> list = new ArrayList<>();

    private DemandBean zoneBean;

    @Override
    public int getLayoutId() {
        return R.layout.activity_demand_step1_2;
    }

    @Override
    public void initView() {
        tvTitle.setText("发需求");
        tvStep.setText("1");
        tvStepContent.setText("步，请选择故障设施所在的位置");
        initList();
    }

    @Override
    public void initData() {
        List<HouseEntity.zoneInfo> infoList = UserManager.getInstance().houseEntity.getZones();
        if (infoList != null) {
            for (int i = 0; i < infoList.size(); i++) {
                list.add(DemandBean.builder().isSelect(false).name(infoList.get(i).getZoneName()).id(infoList.get(i).getZoneId()).build());
            }
            adapter.update(list);
        }
    }

    /**
     * 初始化列表
     */
    private void initList() {
        LayoutManager.getInstance().init(this).initRecyclerView(rvSelectList, true);
        adapter = new DemandAdapter(list, this, this);
        rvSelectList.setAdapter(adapter);
    }

    @OnClick({R.id.rl_back_button, R.id.tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_next:
                if (U.isEmpty(zoneBean) || U.isEmpty(zoneBean.getId())) {
                    U.showToast("请选择需维修的位置");
                    return;
                }
                startActivity(new Intent(this, NeedReleaseActivity.class)
                        .putExtra("zoneBean", zoneBean)
                        .putExtra("type", "1"));// 1：发布住房维保
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
        adapter.update(list);
        zoneBean = list.get(position);
    }
}
