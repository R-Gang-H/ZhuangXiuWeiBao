package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.GlideUtils;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Date: 2019/5/13
 * Author: haoruigang
 * Description: com.zhuangxiuweibao.app.ui.adapter
 */
public class OrderAdapter extends XrecyclerAdapter {

    @BindView(R.id.rl_all)
    RelativeLayout rlAll;
    @BindView(R.id.iv_avatar)
    RoundedImageView ivAvatar;
    @BindView(R.id.tv_order_title)
    TextView tvOrderTitle;
    @BindView(R.id.tv_order_name)
    TextView tvOrderName;
    @BindView(R.id.tv_order_time)
    TextView tvOrderTime;

    @BindView(R.id.rl_after)
    RelativeLayout rlAfter;
    @BindView(R.id.tv_reason)
    TextView tvReason;
    @BindView(R.id.tv_r_order_name)
    TextView tvROrderName;
    @BindView(R.id.iv_r_avatar)
    RoundedImageView ivRAvatar;
    @BindView(R.id.tv_r_order_title)
    TextView tvROrderTitle;
    @BindView(R.id.tv_r_order_time)
    TextView tvROrderTime;

    private List<MessageEntity> orderData = new ArrayList<>();
    private String status;

    public OrderAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
        this.orderData.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        MessageEntity orderEntity = orderData.get(position);
        rlAfter.setVisibility(status.equals("5") ? View.VISIBLE : View.GONE);// 5售后订单
        rlAll.setVisibility(status.equals("5") ? View.GONE : View.VISIBLE);// 非售后订单

        String createAt = DateUtils.getDateToString(
                Long.parseLong(orderEntity.getCreateAt()), "yyyy-MM-dd HH:mm");
        String headImage = orderEntity.getHeadImage();
        String zoneName = orderEntity.getZoneName();
        if (status.equals("5")) {// 5售后订单
            tvReason.setText(orderEntity.getReason());
            tvROrderName.setText(createAt);
            GlideUtils.setGlideImg(context, headImage, R.mipmap.icon_avatar, ivRAvatar);
            tvROrderTitle.setText(zoneName);
            tvROrderTime.setText(createAt);
        } else {//  非售后订单
            GlideUtils.setGlideImg(context, headImage, R.mipmap.icon_avatar, ivAvatar);
            tvOrderTitle.setText(String.format("%s\t\t报修", zoneName));
            tvOrderName.setText(orderEntity.getName());
            tvOrderTime.setText(createAt);
        }
    }

    public void setData(List<MessageEntity> orderData, String status) {
        this.orderData.clear();
        this.orderData.addAll(orderData);
        this.status = status;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return orderData.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_weibao_order;
    }
}
