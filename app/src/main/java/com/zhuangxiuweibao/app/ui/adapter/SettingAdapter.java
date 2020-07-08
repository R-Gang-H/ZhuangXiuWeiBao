package com.zhuangxiuweibao.app.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.utils.CleanCacheUtil;
import com.zhuangxiuweibao.app.common.view.DrawableCenterTextView;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.List;

import butterknife.BindView;

/**
 * 设置 适配器
 */
public class SettingAdapter extends XrecyclerAdapter {

    @BindView(R.id.lv_seperate_line)
    RelativeLayout mSeperateLine;
    @BindView(R.id.tv_setting)
    DrawableCenterTextView mSettingBtn;
    @BindView(R.id.tv_setting_arrow)
    ImageView mArrowView;
    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.view_line)
    View viewLine;

    private int[] iconSetting;

    public SettingAdapter(List datas, int[] iconSetting, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
        this.iconSetting = iconSetting;
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        if (position == 3 || position == 5) {
            mSeperateLine.setVisibility(View.VISIBLE);
        } else {
            mSeperateLine.setVisibility(View.GONE);
        }
        if (position == 2 || position == 4 || position == 7) {
            viewLine.setVisibility(View.GONE);
        } else {
            viewLine.setVisibility(View.VISIBLE);
        }
        if (position == 0) {
            try {
                tvText.setText(CleanCacheUtil.getTotalCacheSize(context));
            } catch (Exception ignored) {
            }
        } else {
            tvText.setText("");
        }
        mSettingBtn.setVisibility(View.VISIBLE);
        mArrowView.setVisibility(View.VISIBLE);
        mSettingBtn.setText(datas.get(position).toString());//设置文字

        Drawable drawable = context.getResources().getDrawable(iconSetting[position]);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        mSettingBtn.setCompoundDrawables(drawable, null, null, null);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_setting_cell;
    }
}
