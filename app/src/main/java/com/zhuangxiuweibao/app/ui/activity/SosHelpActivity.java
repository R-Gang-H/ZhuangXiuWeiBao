package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.ui.adapter.SosHelpAdapter;
import com.zhuangxiuweibao.app.ui.bean.SosHelpBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 紧急求助SOS
 */
public class SosHelpActivity extends BaseActivity implements ViewOnItemClick {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    String[] sosText = {"警务", "火灾", "救护车"};
    int[] sosBg = {R.mipmap.ic_police_bg, R.mipmap.ic_fire_bg, R.mipmap.ic_ambulance_bg};
    int[] sosIcon = {R.mipmap.icon_police, R.mipmap.icon_fire, R.mipmap.icon_ambulance};

    List<SosHelpBean> sosHelpBean = new ArrayList<>();
    SosHelpAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_sos_help;
    }

    @Override
    public void initView() {
        tvTitle.setText("SOS紧急联络");
        LayoutManager.getInstance().init(this).initRecyclerView(recyclerView, true);
    }

    @Override
    public void initData() {
        this.sosHelpBean.clear();
        for (int i = 0; i < sosText.length; i++) {
            this.sosHelpBean.add(SosHelpBean.builder()
                    .sosBg(sosBg[i]).sosIcon(sosIcon[i]).sosText(sosText[i])
                    .build());
        }
        adapter = new SosHelpAdapter(sosHelpBean, this, this);
        recyclerView.setAdapter(adapter);

    }

    @OnClick({R.id.rl_back_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        startActivity(new Intent(this, SosDetailActivity.class)
                .putExtra("sosText", sosText[position]));
    }
}
