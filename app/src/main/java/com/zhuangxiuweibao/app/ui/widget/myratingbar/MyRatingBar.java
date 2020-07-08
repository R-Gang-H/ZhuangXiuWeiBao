package com.zhuangxiuweibao.app.ui.widget.myratingbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MyRatingBar extends RelativeLayout {

    RecyclerView mxreList;

    private MyRatingAdapter adapter;

    private Context mContext;

    public MyRatingBar(Context context,AttributeSet attrs) {
        super(context, attrs);
        View view=inflate(context, R.layout.my_rating, this);
        mxreList=view.findViewById(R.id.xre_list);
        mContext = context;
        init();
    }

    private void init() {
        LayoutManager.getInstance().initRecyclerView(mxreList,false);
        adapter=new MyRatingAdapter(mContext);
        mxreList.setAdapter(adapter);
    }

    /**
     * 更新星星
     * @param list
     */
    public void update(List list, MyRatingAdapter.OnStarChangeListener listener){
        adapter.update(list);
        adapter.setListener(listener);
    }

    /**
     * 更新星星
     * @param list
     */
    public void update(List list){
        adapter.update(list);
    }
}
