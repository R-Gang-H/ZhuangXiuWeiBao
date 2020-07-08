package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.SosContactsEntity;
import com.zhuangxiuweibao.app.common.utils.GlideUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Date: 2019/4/19
 * Author: haoruigang
 * Description: com.zhuangxiuweibao.app.ui.adapter
 */
public class ContactAdapter extends XrecyclerAdapter {

    List<SosContactsEntity> constactData = new ArrayList<>();

    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_relation)
    TextView tvRelation;

    public ContactAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
        this.constactData.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        SosContactsEntity contactEntity = constactData.get(position);
        GlideUtils.setGlideImg(mContext, contactEntity.getHeadImage(), R.mipmap.icon_avatar, ivAvatar);
        tvName.setText(contactEntity.getName());
        String relation = contactEntity.getRelationName();
        tvRelation.setText(U.isNotEmpty(relation) ? relation : "暂不提供身份");
    }

    public void setData(List<SosContactsEntity> constactData) {
        this.constactData.clear();
        this.constactData.addAll(constactData);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return constactData.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_contact;
    }
}
