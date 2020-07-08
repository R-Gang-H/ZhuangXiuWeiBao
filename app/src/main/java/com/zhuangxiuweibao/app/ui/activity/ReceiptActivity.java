package com.zhuangxiuweibao.app.ui.activity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.ApprovalEntity;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.LoadingFrameView;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.ui.adapter.ReceiptAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 78a 查看回执情况
 */
public class ReceiptActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView mtvTitle;
    @BindView(R.id.xre_list)
    XRecyclerView mxreList;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;
    @BindView(R.id.tabView)
    LinearLayout tabView;
    private String eventId;
    private List<ApprovalEntity> list = new ArrayList<>();
    private ReceiptAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_receipt;
    }

    @Override
    public void initView() {
        mtvTitle.setText("回执情况");
        initList();
    }

    @Override
    public void initData() {
        getDataByIntent();
    }

    private void getDataByIntent() {
        eventId = getIntent().getStringExtra("eventId");
        replyDetail();
    }

    private void replyDetail() {
        HttpManager.getInstance().replyDetail("ReceiptActivity", eventId, new HttpCallBack<BaseDataModel<ApprovalEntity>>() {
            @Override
            public void onSuccess(BaseDataModel<ApprovalEntity> data) {
                list.clear();
                list.addAll(data.getData());
                if (list == null || list.size() == 0) {
                    loadView.setVisibility(View.VISIBLE);
                    tabView.setVisibility(View.GONE);
                    mxreList.setVisibility(View.GONE);
                    loadView.setNoShown(true);//展示暂无内容
                } else {
                    loadView.setVisibility(View.GONE);
                    loadView.delayShowContainer(true);//展示有数据
                    adapter.update(list);
                }
            }

            @Override
            public void onFail(int statusCode, String errorMsg) {
                if (statusCode == 1003) {//异地登录
                    U.showToast("该账户在异地登录!");
                    HttpManager.getInstance().dologout(ReceiptActivity.this);
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    private void initList() {
        LayoutManager.getInstance().iniXrecyclerView(mxreList);
        adapter = new ReceiptAdapter(this);
        mxreList.setAdapter(adapter);
    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked() {
        finish();
    }
}
