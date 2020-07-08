package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.SosContactsEntity;
import com.zhuangxiuweibao.app.bean.UserEntity;
import com.zhuangxiuweibao.app.common.user.UserManager;
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
public class EditContactAdapter extends XrecyclerAdapter {

    public List<SosContactsEntity> constactData = new ArrayList<>();

    @BindView(R.id.tv_relation_name)
    TextView tvRelationName;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_del)
    TextView tvDel;

    public EditContactAdapter(List datas, Context context) {
        super(datas, context);
        this.constactData.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        SosContactsEntity contactEntity = constactData.get(position);
        tvRelationName.setText(contactEntity.getRelationName());
        tvName.setText(contactEntity.getName());
        tvDel.setOnClickListener(v -> {
            constactData.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, constactData.size());
            UserEntity user = UserManager.getInstance().userData;
            for (int i = 0; i < user.getSosContacts().size(); i++) {
                SosContactsEntity contact = user.getSosContacts().get(i);
                if (contact.getContactId().equals(contactEntity.getContactId())) {
                    UserManager.getInstance().userData.getSosContacts().remove(i);
                    break;
                }
            }
        });
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
        return R.layout.item_edit_contact;
    }
}
