package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.LoadingFrameView;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.ui.adapter.OrderAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的维保服务
 */
public class MyWeiBaoSerActivity extends BaseActivity implements ViewOnItemClick {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_weibao)
    TextView btnWeibao;
    @BindView(R.id.btn_after)
    TextView btnAfter;
    @BindView(R.id.recycler_view)
    XRecyclerView xrecyclerview;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    private List<MessageEntity> orderData = new ArrayList<>();
    private OrderAdapter adapter;
    private String status = "1";// 1全部 2进行中 3已完成 4已取消 5售后订单

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_wei_bao_ser;
    }

    @Override
    public void initView() {
        tvTitle.setText("我的维保服务");
        btnWeibao.setSelected(true);

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
    }

    @Override
    public void initData() {
        MyOrder(true);
    }

    private void MyOrder(boolean isClear) {
        HttpManager.getInstance().doMyOrder(getLocalClassName(),
                status, pageIndex + "",
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
                            HttpManager.getInstance().dologout(MyWeiBaoSerActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("WeiBaoOrderActivity", throwable.getMessage());
                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.btn_weibao, R.id.btn_after})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.btn_weibao:
                status = "1";// 全部
                btnWeibao.setSelected(true);
                btnAfter.setSelected(false);
                btnWeibao.setBackgroundResource(R.mipmap.ic_order_tob);
                btnAfter.setBackgroundResource(0);
                MyOrder(true);
                break;
            case R.id.btn_after:
                status = "5";//  5售后订单
                btnWeibao.setSelected(false);
                btnAfter.setSelected(true);
                btnWeibao.setBackgroundResource(0);
                btnAfter.setBackgroundResource(R.mipmap.ic_order_tob);
                MyOrder(true);
                break;
        }
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        MessageEntity orderEntity = orderData.get(position - 1);
        startActivity(new Intent(this, OrderHandlerActivity.class)
                .putExtra("orderData", orderEntity));
    }
}
