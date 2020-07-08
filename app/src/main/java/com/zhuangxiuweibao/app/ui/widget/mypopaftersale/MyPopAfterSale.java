package com.zhuangxiuweibao.app.ui.widget.mypopaftersale;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.ui.interfaces.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyPopAfterSale {

    @BindView(R.id.xre_list)
    XRecyclerView mxreList;

    private View view;
    private PopupWindow mPopupWindow;
    private View mRootView;
    private MyAfterSaleAdapter adapter;
    private OnSubmitListener mListener;

    public MyPopAfterSale(Context context, View root,OnItemClickListener listener,OnSubmitListener submitListener) {
        view = LayoutInflater.from(context).inflate(R.layout.pop_after_sale, null);
        mRootView = root;
        mListener=submitListener;
        ButterKnife.bind(this, view);
        mPopupWindow = new PopupWindow(context);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setContentView(view);
        initList(context,listener);
    }

    private void initList(Context context, OnItemClickListener listener){
        LayoutManager.getInstance().iniXrecyclerView(mxreList);
        adapter=new MyAfterSaleAdapter(context,listener);
        mxreList.setAdapter(adapter);
    }

    public void update(List list){
        adapter.update(list);
    }

    public void show() {
        if (mPopupWindow != null) {
            if (!mPopupWindow.isShowing()) {
                mPopupWindow.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
            }else {
                mPopupWindow.dismiss();
            }
        }
    }

    @OnClick({R.id.img_close, R.id.tv_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                show();
                break;
            case R.id.tv_submit:
                if (mListener!=null) mListener.submit();
                break;
        }
    }

    public interface OnSubmitListener{
        void submit();
    }
}
