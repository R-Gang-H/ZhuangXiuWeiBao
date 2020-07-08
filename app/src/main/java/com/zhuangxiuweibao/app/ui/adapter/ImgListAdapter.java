package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.utils.GlideUtils;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.List;

import butterknife.BindView;

public class ImgListAdapter extends XrecyclerAdapter {

    @BindView(R.id.img_mini)
    ImageView imgMini;

    public ImgListAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        GlideUtils.setGlideImg(context, datas.get(position).toString(), R.mipmap.icon_def, imgMini);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_img;
    }

}
