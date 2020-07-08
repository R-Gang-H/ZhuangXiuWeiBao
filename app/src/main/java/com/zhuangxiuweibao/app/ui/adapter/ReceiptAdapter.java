package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.ApprovalEntity;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ReceiptAdapter extends XrecyclerAdapter {

    @BindView(R.id.tv_user)
    TextView mtvUser;
    @BindView(R.id.tv_mobile)
    TextView mtvMobile;
    @BindView(R.id.tv_depart)
    TextView mtvDepart;
    @BindView(R.id.tv_time)
    TextView mtvTime;

    private List<ApprovalEntity> list = new ArrayList<>();

    public ReceiptAdapter(Context context) {
        super(context);
    }

    public void update(List l) {
        list.clear();
        list.addAll(l);
        notifyDataSetChanged();
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        mtvUser.setText(list.get(position).getName());
        mtvMobile.setText(list.get(position).getMobile());
        mtvDepart.setText(list.get(position).getBranchName());
        mtvTime.setText(DateUtils.getDateToString(Long.valueOf(list.get(position).getCreateAt()), "yyyy/MM/dd HH:mm"));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_receipt;
    }
}
