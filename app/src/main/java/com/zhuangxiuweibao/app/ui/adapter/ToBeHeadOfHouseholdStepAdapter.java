package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;
import com.zhuangxiuweibao.app.ui.bean.StepBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ToBeHeadOfHouseholdStepAdapter extends XrecyclerAdapter {

    @BindView(R.id.tv_index)
    TextView tvIndex;
    @BindView(R.id.tv_text)
    TextView tvText;

    private Context mContext;
    private List<StepBean> list = new ArrayList<>();

    public ToBeHeadOfHouseholdStepAdapter(List datas, Context context) {
        super(datas, context);
        mContext = context;
        list.clear();
        list.addAll(datas);
    }

    public void update(List l) {
        list.clear();
        list.addAll(l);
        notifyDataSetChanged();
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        tvText.setText(list.get(position).getText());
        tvIndex.setText(String.format("%s", position + 1 + ""));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_step;
    }

}
