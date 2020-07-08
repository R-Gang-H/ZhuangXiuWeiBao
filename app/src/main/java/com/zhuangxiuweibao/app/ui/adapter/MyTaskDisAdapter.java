package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;
import com.zhuangxiuweibao.app.ui.activity.AboutUsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MyTaskDisAdapter extends XrecyclerAdapter {

    @BindView(R.id.tv_loc_name)
    TextView tvLocName;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_original)
    TextView tvOriginal;
    @BindView(R.id.tv_contrast)
    TextView tvContrast;
    private List<MessageEntity> allList = new ArrayList<>();

    public MyTaskDisAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
    }


    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        MessageEntity entity = allList.get(position);
        tvLocName.setText(entity.getName());
        tvName.setText(DateUtils.getDateToString(Long.valueOf(entity.getTime()), "MM/dd HH:mm"));
        tvOriginal.setVisibility(U.isNotEmpty(entity.getUrl()) ? View.VISIBLE : View.GONE);
        tvOriginal.setOnClickListener(v -> {
            setUrl(entity.getUrl());
        });
        tvContrast.setText(U.isNotEmpty(entity.getContrastForms()) ? tvContrast.getText() : "");
        tvContrast.setVisibility(U.isNotEmpty(entity.getUrl()) ? View.VISIBLE : View.INVISIBLE);
        tvContrast.setOnClickListener(v -> {
            setUrl(entity.getContrastForms().get(0).getFormUrl());
        });
    }

    private void setUrl(String url) {
        mContext.startActivity(new Intent(mContext, AboutUsActivity.class)
                .putExtra("type", "3")
                .putExtra("linkUrl", url));
    }

    public void update(List<MessageEntity> data) {
        this.allList.clear();
        this.allList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return allList.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_task_dis;
    }
}
