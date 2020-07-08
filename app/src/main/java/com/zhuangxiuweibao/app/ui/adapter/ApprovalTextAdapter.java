package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.ApprovalEntity;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ApprovalTextAdapter extends XrecyclerAdapter {

    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.other01)
    TextView other01;
    @BindView(R.id.tvWorker)
    TextView tvWorker;
    private List<ApprovalEntity> list = new ArrayList<>();

    public ApprovalTextAdapter(List datas, Context context) {
        super(datas, context);
        list.addAll(datas);
    }

    public void update(List l) {
        list.clear();
        list.addAll(l);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        String introd = list.get(position).getIntroduction();
        other01.setText(introd);
        other01.setVisibility(U.isNotEmpty(introd) ? View.VISIBLE : View.GONE);
        tvWorker.setText(U.isNotEmpty(list.get(position).getName())?list.get(position).getName().trim():"");
//        tvStatus.setText(list.get(position).getName()+"："+(list.get(position).getStatus().equals("1")?"同意":"不同意"));
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_approval_text;
    }
}
