package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.LoadingFrameView;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.ui.adapter.MyTaskAdapter;
import com.zhuangxiuweibao.app.ui.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MyTaskActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.aboveView)
    RelativeLayout aboveView;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.cview)
    CardView cview;
    @BindView(R.id.recycler_view)
    XRecyclerView xrecyclerview;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;
    private MyTaskAdapter adapter;
    private List<UserBean> allList = new ArrayList<>();
    private String type;


    @Override
    public int getLayoutId() {
        return R.layout.activity_my_wei_bao_ser;
    }

    @Override
    public void initView() {
        // 1 任务列表 2 群通知列表 3 审批列表
        type = getIntent().getStringExtra("type");
        tvTitle.setText(getIntent().getStringExtra("name"));
        aboveView.setBackgroundColor(Color.parseColor("#fff4f4f4"));
        cview.setVisibility(View.GONE);
        initList();
    }

    private void initList() {
        LayoutManager.getInstance().init(this).iniXrecyclerView(xrecyclerview);
        adapter = new MyTaskAdapter(allList, this, this);
        xrecyclerview.setAdapter(adapter);
        xrecyclerview.setPullRefreshEnabled(true);
        xrecyclerview.setLoadingMoreEnabled(true);
        xrecyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                getData(true);
            }

            @Override
            public void onLoadMore() {
                pageIndex++;
                getData(false);
            }
        });

    }

    private void getData(boolean isClear) {
        HttpManager.getInstance().taskList("MyTaskActivity", type, pageIndex + "",
                new HttpCallBack<BaseDataModel<UserBean>>() {
                    @Override
                    public void onSuccess(BaseDataModel<UserBean> data) {
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
                        LogUtils.d("MyTaskActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MyTaskActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("MyTaskActivity", throwable.getMessage());
                    }
                });
    }

    @Override
    public void initData() {
        getData(true);
    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        if (type.equals("3")) {//我的审批
            startActivity(new Intent(this, ApprovalDetailsActivity.class)
                    .putExtra("orderId", allList.get(position - 1).getCid())
                    .putExtra("type", type));
        }
        if (type.equals("2")) {//群通知
            startActivity(new Intent(this, GroupNotifiDetailActivity.class)
                    .putExtra("orderId", allList.get(position - 1).getCid())
                    .putExtra("type", type));
        }
        if (type.equals("1")) {//任务详情
            startActivity(new Intent(this, ApprovalDetailsActivity.class)
                    .putExtra("orderId", allList.get(position - 1).getCid())
                    .putExtra("type", type)
                    .putExtra("from", "task"));
        }
    }
}
