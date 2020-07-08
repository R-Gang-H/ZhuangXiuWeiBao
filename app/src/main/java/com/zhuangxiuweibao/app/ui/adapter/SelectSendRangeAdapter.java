package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import butterknife.OnItemClick;

public class SelectSendRangeAdapter extends XrecyclerAdapter {
    @BindView(R.id.imgSelect)
    CheckBox imgSelect;
    @BindView(R.id.tv_name)
    TextView tvName;
    private List<UserBean> list = new ArrayList<>();
    private Context mContext;
    private OnItemClick onItemClick;


    public SelectSendRangeAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
        mContext = context;
        list.addAll(datas);
    }

    public interface OnItemClick {
        void enterNext(int position);
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void update(List l) {
        list.clear();
        list.addAll(l);
        notifyDataSetChanged();
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        imgSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                list.get(position).setSelect(isChecked);
            }
        });
        imgSelect.setChecked(list.get(position).isSelect());
        tvName.setText(list.get(position).getName());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_send_range;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}
