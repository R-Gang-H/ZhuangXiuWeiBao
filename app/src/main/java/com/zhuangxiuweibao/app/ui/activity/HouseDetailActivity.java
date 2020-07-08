package com.zhuangxiuweibao.app.ui.activity;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.ui.adapter.HouseDetailAdapter;
import com.zhuangxiuweibao.app.ui.bean.UserBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 住房详情页
 */
public class HouseDetailActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_house_name)
    TextView tvHouseName;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    HouseDetailAdapter adapter;
    List<UserBean> listData = new ArrayList<>();

    String[] textList = {"门", "墙面1", "墙面2", "墙面3", "墙面4", "脸盆", "底盘1", "洗漱柜1",
            "洗手盆", "热水器1", "淋浴器"};

    @Override
    public int getLayoutId() {
        return R.layout.activity_house_detail;
    }

    @Override
    public void initView() {
        LayoutManager.getInstance().init(this).initRecyclerGrid(recyclerView, 3);
        adapter = new HouseDetailAdapter(Arrays.asList(textList), this, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData() {
        MessageEntity orderData = (MessageEntity) getIntent().getSerializableExtra("orderData");
        tvHouseName.setText(orderData.getZoneName());
    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        showHouseDialog();
    }

    /**
     * 住房详情页-设施信息
     */
    private void showHouseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_house_detail, null);
        TextView tv_ok = view.findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
    }

}
