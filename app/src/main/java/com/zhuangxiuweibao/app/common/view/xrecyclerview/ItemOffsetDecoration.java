package com.zhuangxiuweibao.app.common.view.xrecyclerview;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

    private int left;
    private int top;
    private int right;
    private int bottme;

    public ItemOffsetDecoration(int itemOffset) {
        this.left = itemOffset;
        this.top = itemOffset;
        this.right = itemOffset;
        this.bottme = itemOffset;
    }

    public ItemOffsetDecoration(int leftRight, int topBottme) {
        this.left = leftRight;
        this.right = leftRight;
        this.top = topBottme;
        this.bottme = topBottme;
    }

    public ItemOffsetDecoration(int left, int top, int right, int bottme) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottme = bottme;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(left, top, right, bottme);
    }

}
