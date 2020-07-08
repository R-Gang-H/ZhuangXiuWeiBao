package com.zhuangxiuweibao.app.common.view.xrecyclerview;

import android.content.Context;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.zhuangxiuweibao.app.R;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Date: 2019/4/10
 * Author: haoruigang
 * Description: com.zhuangxiuweibao.app.common.view.xrecyclerview
 */
public class LayoutManager {

    private static LayoutManager mInstance;
    private Context mContext;

    /**
     * 获取LayoutManager实例
     */
    public static synchronized LayoutManager getInstance() {
        if (null == mInstance) {
            mInstance = new LayoutManager();
        }
        return mInstance;
    }

    public LayoutManager init(Context context) {
        mContext = context;
        return this;
    }

    public LinearLayoutManager initRecyclerView(RecyclerView recyclerView, boolean isVertical) {
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(isVertical ? RecyclerView.VERTICAL : LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
        return manager;
    }

    public void initRecyclerGrid(RecyclerView recyclerView, int span) {
        GridLayoutManager manager = new GridLayoutManager(mContext, span);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
    }

    //    初始化 RecyclerView的配置
    public LinearLayoutManager iniXrecyclerView(XRecyclerView xRecyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        xRecyclerView.setLayoutManager(layoutManager);
        // xRecyclerView.setRefreshHeader(new CustomArrowHeader(this));
        xRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        xRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        xRecyclerView.setArrowImageView(R.mipmap.ic_recycler_view_arrow);
        xRecyclerView.setPullRefreshEnabled(false);
        xRecyclerView.setLoadingMoreEnabled(false);
        xRecyclerView.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
        xRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
        return layoutManager;
    }

    public void iniXrecyclerGrid(XRecyclerView xRecyclerView, int span) {
        GridLayoutManager manager = new GridLayoutManager(mContext, span);
        manager.setOrientation(RecyclerView.VERTICAL);
        xRecyclerView.setLayoutManager(manager);
        xRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        xRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        xRecyclerView.setArrowImageView(R.mipmap.ic_recycler_view_arrow);
        xRecyclerView.setPullRefreshEnabled(false);
        xRecyclerView.setLoadingMoreEnabled(false);
        xRecyclerView.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
        xRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
    }
}
