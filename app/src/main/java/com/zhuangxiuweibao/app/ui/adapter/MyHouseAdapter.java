package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.HouseEntity;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Date: 2019/4/24
 * Author: haoruigang
 * Description: 我的住房 适配器
 */
public class MyHouseAdapter extends XrecyclerAdapter {

    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_utility)
    TextView tvUtility;
    @BindView(R.id.tv_area)
    TextView tvArea;
    @BindView(R.id.tv_direction)
    TextView tvDirection;
    private List<HouseEntity.zoneInfo> zones = new ArrayList<>();

    public MyHouseAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
        this.zones.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        if (zones.get(position).getZoneName().contains("卫生间")) {
            ivIcon.setImageResource(R.mipmap.icon_toilet);
        } else if (zones.get(position).getZoneName().contains("厨房")) {
            ivIcon.setImageResource(R.mipmap.icon_chuf);
        } else if (zones.get(position).getZoneName().contains("主卧") ||
                zones.get(position).getZoneName().contains("卧室1")) {
            ivIcon.setImageResource(R.mipmap.icon_master);
        } else if (zones.get(position).getZoneName().contains("次卧") ||
                zones.get(position).getZoneName().contains("卧室2")) {
            ivIcon.setImageResource(R.mipmap.icon_bedroom);
        } else if (zones.get(position).getZoneName().contains("起居室")) {
            ivIcon.setImageResource(R.mipmap.icon_life);
        } else if (zones.get(position).getZoneName().contains("阳台")) {
            ivIcon.setImageResource(R.mipmap.icon_balcony);
        }
        tvUtility.setText(zones.get(position).getZoneName());
        String Area = zones.get(position).getArea();
        tvArea.setText(String.format("面积\t:\t%s", U.isEmpty(Area) ? "0" : Area));
        String Direc = zones.get(position).getDirection();
        tvDirection.setText(String.format("朝向\t:\t%s", U.isEmpty(Direc) ? "0" : Direc));
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_my_house;
    }
}
