package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.ResourcesUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.LoadingFrameView;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.ui.adapter.OrderAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import butterknife.Setter;

/**
 * 维保订单
 */
public class WeiBaoOrderActivity extends BaseActivity implements View.OnClickListener, ViewOnItemClick {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.title_line)
    View titleLine;
    @BindView(R.id.xrecyclerview)
    XRecyclerView xrecyclerview;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    @BindViews({R.id.ll_all_btn, R.id.ll_proceed_btn, R.id.ll_complete_btn, R.id.ll_cancel_btn, R.id.ll_order_btn})
    List<TextView> radioTextView;
    private int position;

    private List<MessageEntity> orderData = new ArrayList<>();
    private OrderAdapter adapter;
    private String status = "1";// 1全部 2进行中 3已完成 4已取消 5售后订单


    @Override
    public int getLayoutId() {
        return R.layout.activity_wei_bao_order;
    }

    @Override
    public void initView() {
        tvTitle.setText("维保订单");
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        titleLine.setVisibility(View.GONE);
        LayoutManager.getInstance().init(this).iniXrecyclerView(xrecyclerview);
        adapter = new OrderAdapter(orderData, this, this);
        xrecyclerview.setAdapter(adapter);
        xrecyclerview.setPullRefreshEnabled(true);
        xrecyclerview.setLoadingMoreEnabled(true);
        xrecyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                MyOrder(true);
            }

            @Override
            public void onLoadMore() {
                pageIndex++;
                MyOrder(false);
            }
        });
        radioTextView.get(0).setOnClickListener(this);
        radioTextView.get(1).setOnClickListener(this);
        radioTextView.get(2).setOnClickListener(this);
        radioTextView.get(3).setOnClickListener(this);
        radioTextView.get(4).setOnClickListener(this);
        setChioceItem(position);
    }

    @Override
    public void initData() {
        MyOrder(true);
    }

    private void MyOrder(boolean isClear) {
        HttpManager.getInstance().doMyOrder(getLocalClassName(), status, pageIndex + "",
                new HttpCallBack<BaseDataModel<MessageEntity>>(this) {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        if (isClear) {
                            orderData.clear();
                        }
                        orderData.addAll(data.getData());
                        adapter.setData(orderData, status);
                        if (orderData.size() > 0) {
                            loadView.delayShowContainer(true);
                        } else {
                            if (isClear) {
                                loadView.setNoShown(true);
                            } else {
                                loadView.delayShowContainer(true);
                            }
                        }
                        xrecyclerview.loadMoreComplete();
                        xrecyclerview.refreshComplete();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("WeiBaoOrderActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(WeiBaoOrderActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("WeiBaoOrderActivity", throwable.getMessage());
                    }
                });
    }


    @OnClick({R.id.rl_back_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        position = savedInstanceState.getInt("position");
        setChioceItem(position);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        //记录当前的position
        super.onSaveInstanceState(outState);
        outState.putInt("position", position);
    }

    public void setChioceItem(int index) {
        // 1全部 2进行中 3已完成 4已取消 5售后订单
        this.position = index;
        UserManager.apply(radioTextView, TABSPEC, radioTextView.get(index));
    }

    //控制normal 状态的当前View 隐藏，其它空间仍然为显示
    final Setter<TextView, TextView> TABSPEC = (view, value, index) -> {
        assert value != null;
        if (view.getId() == value.getId()) {
            view.setSelected(true);
            view.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_msg_tab_bg));
        } else {
            view.setSelected(false);
            view.setBackgroundColor(0);
        }
    };

    @Override
    public void onClick(View v) {
        // 1全部 2进行中 3已完成 4已取消 5售后订单
        switch (v.getId()) {
            case R.id.ll_all_btn:
                status = "1";
                setChioceItem(0);
                break;
            case R.id.ll_proceed_btn:
                status = "2";
                setChioceItem(1);
                break;
            case R.id.ll_complete_btn:
                status = "3";
                setChioceItem(2);
                break;
            case R.id.ll_cancel_btn:
                status = "4";
                setChioceItem(3);
                break;
            case R.id.ll_order_btn:
                status = "5";
                setChioceItem(4);
                break;
        }
        pageIndex = 1;
        MyOrder(true);
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        MessageEntity orderEntity = orderData.get(position - 1);
        startActivity(new Intent(this, OrderDetailActivity.class)
                .putExtra("orderData", orderEntity));
    }

}
