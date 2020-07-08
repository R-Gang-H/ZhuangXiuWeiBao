package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.BatchEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.ui.adapter.MyAdapter;
import com.zhuangxiuweibao.app.ui.bean.UserBean;
import com.zhuangxiuweibao.app.ui.widget.MyItemLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 房屋综合服务（工作人员版）
 */
public class HouseGeneralWorkActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    int[] resId = {R.mipmap.icon_batch_w, R.mipmap.icon_task_w, R.mipmap.icon_notice_w};
    String[] resText = {"审批", "任务", "通知"};

    List<UserBean> userList = new ArrayList<>();
    MyAdapter settingAdapter;

    List<BatchEntity> batchList = new ArrayList<>();
    List<BatchEntity> taskList = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_house_work;
    }

    @Override
    public void initView() {
        tvTitle.setText("合家到");
        LayoutManager.getInstance().init(this).initRecyclerView(recyclerView, true);
        settingAdapter = new MyAdapter(userList, this, this);
        recyclerView.setAdapter(settingAdapter);
    }

    @Override
    public void initData() {
        checkList();
        for (int i = 0; i < resText.length; i++) {
            userList.add(UserBean.builder().resId(resId[i]).userText(resText[i]).build());
        }
        settingAdapter.setData(userList);
    }

    private void discloseTemplate() {
        //61、获取技术交底模板
        HttpManager.getInstance().doDiscloseTemplate("HouseGeneralWorkActivity"
                , new HttpCallBack<BaseDataModel<BatchEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<BatchEntity> data) {
                        setModule(data.getData(), 1);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("HouseGeneralWorkActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(HouseGeneralWorkActivity.this);
                            return;
                        }
                        U.showToast(errorMsg);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("HouseGeneralWorkActivity", throwable.getMessage());
                    }
                });
    }

    private void checkList() {
        // 获取审批模板
        HttpManager.getInstance().doCheckList(this.getLocalClassName(),
                new HttpCallBack<BaseDataModel<BatchEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<BatchEntity> data) {
                        setModule(data.getData(), 0);
                        discloseTemplate();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("HouseGeneralWorkActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(HouseGeneralWorkActivity.this);
                            return;
                        }
                        U.showToast(errorMsg);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("HouseGeneralWorkActivity", throwable.getMessage());
                    }
                });
    }

    private void setModule(ArrayList<BatchEntity> dataData, int type) {// 0 审批模板 1技术交底模板
        if (type == 0) {
            batchList.clear();
            for (BatchEntity entity : dataData) {
//                if (entity.getBid().equals("0")) {
                    batchList.add(entity);
//                }
            }
            batchList.add(BatchEntity.builder().title("其他审批").modifyCopy("0").modifyExplain("0").build());//其他审批需要独自添加
        } else {
            taskList.clear();
            taskList.add(BatchEntity.builder().title("普通任务").build());//其他审批需要独自添加
            taskList.addAll(dataData);
        }
    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }

    private boolean isOpen1 = false, isOpen2 = false;

    @Override
    public void setOnItemClickListener(View view, int position) {
        final RecyclerView linRoot = view.findViewById(R.id.lin_root);
        final ImageView ivArrow = view.findViewById(R.id.iv_arrow_right);
        LayoutManager.getInstance().init(this).initRecyclerView(linRoot, true);

        switch (position) {
            case 0:
                isOpen1 = !isOpen1;
                linRoot.setVisibility(isOpen1 ? View.VISIBLE : View.GONE);
                ivArrow.setImageResource(isOpen1 ?
                        R.mipmap.arrow_bottom_gray : R.mipmap.arrow_right_gray);
                if (isOpen1) {
                    addView(linRoot, batchList, FixedApprovalActivity.class);
                } else {
                    addView(linRoot, new ArrayList<>(), FixedApprovalActivity.class);
                }
                recyclerView.scrollToPosition(position);
                break;
            case 1:
                isOpen2 = !isOpen2;
                linRoot.setVisibility(isOpen2 ? View.VISIBLE : View.GONE);
                ivArrow.setImageResource(isOpen2 ?
                        R.mipmap.arrow_bottom_gray : R.mipmap.arrow_right_gray);
                if (isOpen2) {
                    addView(linRoot, taskList, SendWorkActivity.class);
                } else {
                    addView(linRoot, new ArrayList<>(), SendWorkActivity.class);
                }
                recyclerView.scrollToPosition(position);
                break;
            case 2:
                startActivity(new Intent(this, SendInfoActivity.class));
                break;
            default:
                linRoot.setVisibility(View.GONE);
                break;
        }
    }

    private void addView(RecyclerView mlinRoot, List<BatchEntity> taskList, Class z) {
        //住房维保需求
        MyItemLayout itemLayout = new MyItemLayout(taskList, this, (view1, position1) -> {
            //点击事件  工作人员端
            startActivity(new Intent(this, z)
                    .putExtra("index", position1 + 1)
                    .putExtra("entity", taskList.get(position1)));
        });
        mlinRoot.setAdapter(itemLayout);
    }

}
