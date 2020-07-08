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
 * haoruigang on 2018-7-30 12:36:17
 * 新建通知卡片
 */
public class ServerNeedAdapter extends XrecyclerAdapter {

    ArrayList<UserBean> notifyCards = new ArrayList<>();
    @BindView(R.id.img_notify)
    ImageView imgNotify;
    @BindView(R.id.tv_card_name)
    TextView tvCardName;

    public ServerNeedAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
        this.notifyCards.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        imgNotify.setImageResource(notifyCards.get(position).getResId());
        tvCardName.setText(notifyCards.get(position).getUserText());
    }

    @Override
    public int getItemCount() {
        return notifyCards.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_new_need;
    }
}
