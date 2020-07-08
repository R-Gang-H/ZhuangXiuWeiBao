package com.zhuangxiuweibao.app.ui.adapter.household;

import android.content.Context;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.ApprovalEntity;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

//帖子详情紧急求助适配器
public class PostHelpAdapter extends XrecyclerAdapter {

    @BindView(R.id.iv_avatar)
    RoundedImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_data)
    TextView tvData;
    @BindView(R.id.ivContent)
    TextView ivContent;
    private List<ApprovalEntity> list = new ArrayList<>();

    public PostHelpAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
    }

    public void update(List l) {
        list.clear();
        list.addAll(l);
        notifyDataSetChanged();
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {

    }

    @Override
    public int getItemCount() {
        return list.isEmpty() ? 0 : list.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_post_help;
    }
}
