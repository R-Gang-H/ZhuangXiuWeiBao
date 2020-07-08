package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.List;

import butterknife.BindView;

/**
 * Date: 2019/5/16
 * Author: haoruigang
 * Description: com.zhuangxiuweibao.app.ui.adapter
 */
public class HouseDetailAdapter extends XrecyclerAdapter {

    @BindView(R.id.tv_facility)
    TextView tvFacility;

    public HouseDetailAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        String text = datas.get(position).toString();
        int res = 0;
        if (text.contains("门")) {
            res = R.mipmap.ic_room;
        } else if (text.contains("墙面")) {
            res = R.mipmap.ic_wall;
        } else if (text.contains("脸盆")) {
            res = R.mipmap.ic_basin;
        } else if (text.contains("底盘")) {
            res = R.mipmap.ic_floor;
        } else if (text.contains("洗漱柜")) {
            res = R.mipmap.ic_wash;
        } else if (text.contains("洗手盆")) {
            res = R.mipmap.ic_lavabo;
        } else if (text.contains("热水器")) {
            res = R.mipmap.ic_lavabo;
        } else if (text.contains("淋浴器")) {
            res = R.mipmap.ic_shower;
        }
        tvFacility.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                context.getResources().getDrawable(res), null, null);
        tvFacility.setText(text);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_house_detail;
    }
}
