package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;
import com.zhuangxiuweibao.app.ui.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MyTaskAdapter extends XrecyclerAdapter {
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvContent)
    TextView tvContent;
    @BindView(R.id.tvTime)
    TextView tvTime;
    private List<UserBean> allList = new ArrayList<>();


    public MyTaskAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
        this.allList.addAll(datas);
    }
    public void update(List datas) {
        allList.clear();
        allList.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        tvTitle.setText(allList.get(position).getTitle());
        tvContent.setText(allList.get(position).getContent());
        tvTime.setText(DateUtils.getDateToString(Long.valueOf(allList.get(position).getCreateAt()), "yyyy/MM/dd HH:mm"));

    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_my_task;
    }

    @Override
    public int getItemCount() {
        return allList.isEmpty() ? 0 : allList.size();

    }
}
