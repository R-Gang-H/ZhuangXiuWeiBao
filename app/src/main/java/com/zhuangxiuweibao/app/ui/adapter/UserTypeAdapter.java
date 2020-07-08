package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;
import com.zhuangxiuweibao.app.ui.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class UserTypeAdapter extends XrecyclerAdapter {

    @BindView(R.id.img_icon)
    ImageView imgIcon;
    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.img_select)
    ImageView imgSelect;
    @BindView(R.id.iv_bg)
    ImageView ivBg;

    private List<UserBean> list = new ArrayList<>();

    public UserTypeAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
        this.list.clear();
        this.list.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        imgIcon.setImageResource(list.get(position).getResId());
        tvText.setText(list.get(position).getUserText());
        if (list.get(position).isSelect()) {
            imgSelect.setVisibility(View.VISIBLE);
            ivBg.setVisibility(View.VISIBLE);
        } else {
            imgSelect.setVisibility(View.GONE);
            ivBg.setVisibility(View.GONE);
        }
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_user_type;
    }

    public void update(List l) {
        this.list.clear();
        this.list.addAll(l);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

}
