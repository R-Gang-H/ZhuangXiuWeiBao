package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

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
import com.zhuangxiuweibao.app.ui.adapter.MyTaskDisAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的任务技术交底
 */
public class MyTaskDisActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycler_view)
    XRecyclerView xrecyclerview;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    private List<MessageEntity> allList = new ArrayList<>();
    private MyTaskDisAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_task_dis;
    }

    @Override
    public void initView() {
        tvTitle.setText("我的技术交底");
    }

    @Override
    public void initData() {
        initList();
        getSendDisclose(true);
    }

    private void initList() {
        LayoutManager.getInstance().init(this).iniXrecyclerView(xrecyclerview);
        adapter = new MyTaskDisAdapter(allList, this, this);
        xrecyclerview.setAdapter(adapter);
        xrecyclerview.setPullRefreshEnabled(true);
        xrecyclerview.setLoadingMoreEnabled(true);
        xrecyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                getSendDisclose(true);
            }

            @Override
            public void onLoadMore() {
                pageIndex++;
                getSendDisclose(false);
            }
        });

    }

    private void getSendDisclose(boolean isClear) {
        HttpManager.getInstance().doGetSendDisclose("MyTaskDisActivity", pageIndex + ""
                , new HttpCallBack<BaseDataModel<MessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        if (isClear) {
                            allList.clear();
                        }
                        allList.addAll(data.getData());
                        adapter.update(allList);
                        if (allList.size() > 0) {
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
                        LogUtils.d("MyTaskDisActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MyTaskDisActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("MyTaskDisActivity", throwable.getMessage());
                    }
                });
    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        //任务详情
        startActivity(new Intent(this, ApprovalDetailsActivity.class)
                .putExtra("orderId", allList.get(position - 1).getTid())
                .putExtra("type", "1")
                .putExtra("from", "task"));

    }
}
