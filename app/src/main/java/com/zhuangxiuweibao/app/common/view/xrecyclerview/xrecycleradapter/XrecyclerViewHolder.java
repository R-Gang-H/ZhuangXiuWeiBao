package com.zhuangxiuweibao.app.common.view.xrecyclerview.xrecycleradapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemLongClick;


/**
 * Created by 1bu2bu-4 on 2016/9/1.
 */
public class XrecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
//    public static TextView mTextView;

    View view;
    ViewOnItemClick onItemClick;
    ViewOnItemLongClick longClick;

    public XrecyclerViewHolder(View view, ViewOnItemClick onItemClick, ViewOnItemLongClick longClick) {
        super(view);
        this.view = view;
        this.onItemClick = onItemClick;
        this.longClick = longClick;
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (onItemClick != null) {
            onItemClick.setOnItemClickListener(v, getPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (longClick != null) {
            longClick.setOnItemLongClickListener(v, getPosition());
        }
        return true;
    }

    public View getView(int id) {
        return view.findViewById(id);
    }

    public View getView() {
        return view;
    }
}
