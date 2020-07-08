package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;
import com.zhuangxiuweibao.app.ui.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Date: 2019/4/23
 * Author: haoruigang
 * Description: 我的 适配器
 */
public class MyAdapter extends XrecyclerAdapter {

    List<UserBean> userList = new ArrayList<>();

    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    public MyAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
        this.userList.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        ivIcon.setBackgroundResource(userList.get(position).getResId());
        tvTitle.setText(userList.get(position).getUserText());
    }

    public void setData(List<UserBean> userList) {
        this.userList.clear();
        this.userList.addAll(userList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_setting;
    }

}
