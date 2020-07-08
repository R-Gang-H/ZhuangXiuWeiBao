package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.DisclosureEntity;
import com.zhuangxiuweibao.app.bean.FacilityEntity;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.ui.adapter.FacilityColumnAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设施列表
 */
public class FacilityColumnActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_add_facility)
    Button btnAddFacility;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    private int REQUEST_NUM = 149;
    private String eventId;
    private FacilityColumnAdapter adapter;
    private List<FacilityEntity> facilityList = new ArrayList<>();
    private MessageEntity entityDetail;

    @Override
    public int getLayoutId() {
        return R.layout.activity_facility_column;
    }

    @Override
    public void initView() {
        LayoutManager.getInstance().init(this).initRecyclerView(recyclerView, true);
        adapter = new FacilityColumnAdapter(facilityList, this, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData() {
        eventId = getIntent().getStringExtra("eventId");
        entityDetail = (MessageEntity) getIntent().getSerializableExtra("entityDetail");
        // 循环筛选申请人是否同时是执行人
        List<DisclosureEntity> disclose = entityDetail.getDisclose();
        if (U.isNotEmpty(disclose)) {
            for (DisclosureEntity dis : disclose) {
                if(dis.getDoerId().equals(UserManager.getInstance().userData.getUid())){// 是该执行人
                    tvTitle.setText(String.format("填写%s", dis.getItemName()));
                    break;
                }
            }
            // 第一执行人显示新增设施按钮
            btnAddFacility.setVisibility(disclose.get(0).getDoerId().equals(UserManager.getInstance().userData.getUid()) ? View.VISIBLE : View.GONE);
        }
        getDisclosureColumn();
    }

    private void getDisclosureColumn() {
        HttpManager.getInstance().doDisclosureColumn("FacilityColumnActivity", eventId,
                new HttpCallBack<BaseDataModel<FacilityEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<FacilityEntity> data) {
                        facilityList.clear();
                        facilityList.addAll(data.getData());
                        adapter.update(facilityList);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("FacilityColumnActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(FacilityColumnActivity.this);
                            return;
                        }
                        U.showToast(errorMsg);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("FacilityColumnActivity", throwable.getMessage());
                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.btn_add_facility})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.btn_add_facility:
                startActivityForResult(new Intent(this, AddFacilityActivity.class)
                        .putExtra("eventId", eventId), REQUEST_NUM);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_NUM) {
                getDisclosureColumn();
            }
        }
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        FacilityEntity entity = facilityList.get(position);
//        if (entity.getIsDone().equals("2")) {//1:完成 2：未完成
//            entity.setIsDone("1");
//        }
//        adapter.update(facilityList);
        startActivityForResult(new Intent(this, FacilityDetailActivity.class)
                        .putExtra("eventId", eventId)
                        .putExtra("disclosureEntity", entity)
                        .putExtra("entityDetail", entityDetail)
                , REQUEST_NUM);
    }
}
