package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.NetWorkEntity;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class NetWorkAdapter extends XrecyclerAdapter {

    @BindView(R.id.rou_head)
    RoundedImageView rouHead;
    @BindView(R.id.image_head)
    ImageView imageHead;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.img_select)
    ImageView imgSelect;

    private List<NetWorkEntity> allList = new ArrayList<>();


    public NetWorkAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
        this.allList.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        if (allList.get(position).isSelect()) {
            imgSelect.setImageResource(R.mipmap.icon_timeline);
            imgSelect.setVisibility(View.VISIBLE);
        } else {
            imgSelect.setImageResource(R.mipmap.icon_unselect);
        }
        tvName.setText(allList.get(position).getSrcName());
        String path = allList.get(position).getSrcUrl();
        String picType = allList.get(position).getSrcType();
        if (picType.contains("image") || picType.contains("jpg") || picType.contains("jpeg") || picType.contains("png")) {//图片
            Glide.with(context).load(path).into(imageHead);
        } else if (picType.contains("video") || picType.contains("mp4")) {//视频
            Glide.with(context).load(path).into(imageHead);
        } else {
            int icon = 0;
            if (picType.contains("pdf")) {
                icon = R.mipmap.icon_pdf;
            } else if (picType.contains("xls")) {
                icon = R.mipmap.icon_xls;
            } else if (picType.contains("doc")) {
                icon = R.mipmap.icon_word;
            } else if (picType.contains("zip")) {
                icon = R.mipmap.icon_zip;
            }
            imageHead.setImageResource(icon);
        }
        rouHead.setVisibility(View.GONE);
        imageHead.setVisibility(View.VISIBLE);
    }

    public void update(List datas) {
        allList.clear();
        allList.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_select_person;
    }

    @Override
    public int getItemCount() {
        return allList.isEmpty() ? 0 : allList.size();

    }
}
