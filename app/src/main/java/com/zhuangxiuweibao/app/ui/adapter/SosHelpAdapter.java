package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;
import com.zhuangxiuweibao.app.ui.bean.SosHelpBean;

import java.util.List;

import butterknife.BindView;

/**
 * Date: 2019/5/13
 * Author: haoruigang
 * Description: com.zhuangxiuweibao.app.ui.adapter
 */
public class SosHelpAdapter extends XrecyclerAdapter {

    @BindView(R.id.rl_sos_bg)
    RelativeLayout rlSosBg;
    @BindView(R.id.iv_sos_icon)
    ImageView ivSosIcon;
    @BindView(R.id.tv_sos_text)
    TextView tvSosText;

    public SosHelpAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        SosHelpBean bean = (SosHelpBean) datas.get(position);
        rlSosBg.setBackgroundResource(bean.getSosBg());
        ivSosIcon.setImageResource(bean.getSosIcon());
        tvSosText.setText(bean.getSosText());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_sos_help;
    }
}
