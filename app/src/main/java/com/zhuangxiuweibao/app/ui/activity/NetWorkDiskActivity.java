package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.luck.picture.lib.entity.LocalMedia;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.NetWorkEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.ui.adapter.NetWorkAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class NetWorkDiskActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.xrecyclerview)
    XRecyclerView xrecyclerview;

    private Integer type;
    private NetWorkAdapter adapter;
    private List<NetWorkEntity> netWorkList = new ArrayList<>();
    private List<LocalMedia> selectList = new ArrayList<>();
    private int selCount = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_net_work_disk;
    }

    @Override
    public void initView() {
        type = getIntent().getIntExtra("type", 0);
        selectList = getIntent().getParcelableArrayListExtra("localMedia");
        if (type == 1) {
            tvTitle.setText("公用网盘");
        } else {
            tvTitle.setText("私有网盘");
        }
        LayoutManager.getInstance().init(this).iniXrecyclerView(xrecyclerview);
        adapter = new NetWorkAdapter(netWorkList, this, this);
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

    @Override
    public void initData() {
        getData(true);
    }

    private void getData(boolean b) {
        HttpManager.getInstance().doSkyDriveList("NetWorkDiskActivity", type - 1 + "", pageIndex + ""
                , new HttpCallBack<BaseDataModel<NetWorkEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<NetWorkEntity> data) {
                        if (b) {
                            netWorkList.clear();
                        }
                        netWorkList.addAll(data.getData());
                        for (NetWorkEntity work : netWorkList) {
                            for (LocalMedia select : selectList) {
                                if (work.getSrcUrl().equals(select.getPath())) {
                                    selCount += 1;// 已选中的
                                    work.setSelect(true);
                                    break;
                                }
                            }
                        }
                        adapter.update(netWorkList);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NetWorkDiskActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(NetWorkDiskActivity.this);
                            return;
                        }
                        U.showToast(errorMsg);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("NetWorkDiskActivity", throwable.getMessage());
                    }
                });
        if (b) {
            xrecyclerview.refreshComplete();
        } else {
            xrecyclerview.loadMoreComplete();
        }
    }

    @OnClick({R.id.rl_back_button, R.id.btnConfirm})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.btnConfirm:
                setResult();// 单选
                break;
        }
    }

    private void setResult() {
        List<NetWorkEntity> netWorks = new ArrayList<>();
        for (NetWorkEntity bean : netWorkList) {
            if (bean.isSelect()) {
                netWorks.add(bean);
            }
        }
        if (netWorks.size() == 0) {
            U.showToast("请选择文件");
            return;
        }
        if ((selectList.size() - selCount + netWorks.size()) > 5) {
            U.showToast("超出最大选择");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("netWorks", (Serializable) netWorks);
        this.setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        position -= 1;
        if (netWorkList.get(position).isSelect()) {//选中的为false，则置为true，否则置为false
            netWorkList.get(position).setSelect(false);
        } else {
            netWorkList.get(position).setSelect(true);
        }
        adapter.update(netWorkList);
    }
}
