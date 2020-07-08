package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;
import com.zhuangxiuweibao.app.ui.bean.DemandBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class DemandAdapter extends XrecyclerAdapter {

    private Context mContext;
    private List<DemandBean> list = new ArrayList<>();

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.rel_demand)
    RelativeLayout relDemand;

    public DemandAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
        mContext = context;
        list.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        if (list.get(position).isSelect()) {
            tvName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.mipmap.icon_rect_select, 0);
        } else {
            tvName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.mipmap.icon_rect_unselect, 0);
        }
        tvName.setText(list.get(position).getName());
    }

    public void update(List l) {
        list.clear();
        list.addAll(l);
        notifyDataSetChanged();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_demand;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

}
