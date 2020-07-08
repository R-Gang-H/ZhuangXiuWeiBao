package com.zhuangxiuweibao.app.ui.widget;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BatchEntity;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * 我的页面 item封装
 */
public class MyItemLayout extends XrecyclerAdapter {

    @BindView(R.id.tv_title)
    TextView menu;
    @BindView(R.id.rel_menu)
    RelativeLayout menuLayout;

    private Context mContext;
    private List<BatchEntity> batchList = new ArrayList<>();

    public MyItemLayout(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
        this.batchList.clear();
        this.batchList.addAll(datas);
    }


    private void setTextData(int leftImgId, String name) {
        setTextData(leftImgId, name, R.mipmap.arrow_right_gray);
    }

    private void setTextData(int leftImgId, String menuName, int rightImgId) {
        if (menu != null) {
            menu.setCompoundDrawablesWithIntrinsicBounds(leftImgId, 0, rightImgId, 0);
            menu.setText(menuName);
        }
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        BatchEntity batchEntity = batchList.get(position);
        setTextData(0, String.format("%s\t\t\t%s", position + 1, batchEntity.getTitle()));
    }

    public void update(List<BatchEntity> batchList) {
        this.batchList.clear();
        this.batchList.addAll(batchList);
        notifyDataSetChanged();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_me;
    }

}
