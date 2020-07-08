package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.DisclosureEntity;
import com.zhuangxiuweibao.app.bean.FacilityEntity;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 设施列表适配器
 */
public class FacilityColumnAdapter extends XrecyclerAdapter {

    @BindView(R.id.rou_head)
    RoundedImageView rouHead;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.img_select)
    ImageView imgSelect;

    private List<FacilityEntity> facilityList = new ArrayList<>();

    public FacilityColumnAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        FacilityEntity entity = facilityList.get(position);
        rouHead.setVisibility(View.GONE);
        tvName.setText(String.format("%s（%s）", entity.getLocationName(), entity.getUnit()));
        if (entity.getIsDone().equals("1")) {//1:完成 2：未完成
            imgSelect.setImageResource(R.mipmap.icon_timeline);
        } else {
            imgSelect.setImageResource(R.mipmap.icon_unselect);
        }
    }

    public void update(List<FacilityEntity> data) {
        this.facilityList.clear();
        this.facilityList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return facilityList.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_select_person;
    }
}
