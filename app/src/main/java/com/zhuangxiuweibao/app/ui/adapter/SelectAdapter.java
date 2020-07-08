package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.utils.GlideUtils;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;
import com.zhuangxiuweibao.app.ui.bean.UserBean;
import com.zhuangxiuweibao.app.ui.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SelectAdapter extends XrecyclerAdapter {

    @BindView(R.id.rou_head)
    RoundedImageView rouHead;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.img_select)
    ImageView imgSelect;
    @BindView(R.id.lin_person)
    LinearLayout linPerson;

    private List<UserBean> list = new ArrayList<>();

    private SelectType mType;

    public SelectAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
    }

    public void update(List l, SelectType type) {
        list.clear();
        list.addAll(l);
        mType = type;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return list == null || list.size() == 0 ? 0 : list.size();
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        if (mType.equals(SelectType.RADIO)) {//单选
            imgSelect.setVisibility(View.INVISIBLE);
        } else {//多选
            imgSelect.setVisibility(View.VISIBLE);
        }
        if (list.get(position).isSelect()) {
            imgSelect.setImageResource(R.mipmap.icon_timeline);
            imgSelect.setVisibility(View.VISIBLE);
        } else {
            imgSelect.setImageResource(R.mipmap.icon_unselect);
        }
        tvName.setText(list.get(position).getName());
        GlideUtils.setGlideImg(mContext, list.get(position).getHeadImage(), R.mipmap.icon_default, rouHead);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_select_person;
    }

    public enum SelectType {
        RADIO, MORE
    }
}
