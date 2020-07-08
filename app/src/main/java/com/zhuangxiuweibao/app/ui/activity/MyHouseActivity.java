package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.HouseEntity;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.ui.adapter.MyHouseAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的住房
 */
public class MyHouseActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_house_name)
    TextView tvHouseName;
    @BindView(R.id.tv_house_type)
    TextView tvHouseType;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_repairs)
    TextView tvRepairs;
    @BindView(R.id.tv_check)
    TextView tvCheck;

    MyHouseAdapter adapter;
    private List<HouseEntity.zoneInfo> zones = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_house;
    }

    @Override
    public void initView() {
        String houseId = getIntent().getStringExtra("houseId");
        LayoutManager.getInstance().init(this).initRecyclerView(recyclerView, true);
        UserManager.getInstance().houseInfo(this, houseId, entity -> {
            tvHouseName.setText(entity.getHouseName());
            tvHouseType.setText(String.format("户型:%s" +
                    "", entity.getHouseType()));
            zones = entity.getZones();
            if (U.isNotEmpty(zones)) {
                adapter = new MyHouseAdapter(zones, this, this);
                recyclerView.setAdapter(adapter);
            }
        });
        tvRepairs.setVisibility(U.isEmpty(houseId) ? View.VISIBLE : View.GONE);
        tvCheck.setText(Html.fromHtml(getResources().getString(R.string.string_check_house)));
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.rl_back_button, R.id.tv_repairs})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_repairs:
                startActivity(new Intent(this, DemandStep1_2Activity.class));
                break;
        }
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
//        startActivity(new Intent(this, HouseDetailActivity.class));
    }
}
