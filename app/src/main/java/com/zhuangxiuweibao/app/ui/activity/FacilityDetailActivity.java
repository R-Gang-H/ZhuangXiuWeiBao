package com.zhuangxiuweibao.app.ui.activity;

import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
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
import com.zhuangxiuweibao.app.ui.adapter.FacilityDetailAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设施详情
 */
public class FacilityDetailActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private FacilityEntity entity;
    private MessageEntity entityDetail;
    private String tId, itemid, eventId;

    List<FacilityEntity> disEntityList = new ArrayList<>();
    private FacilityDetailAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_facility_detail;
    }

    @Override
    public void initView() {
        eventId = getIntent().getStringExtra("eventId");
        entity = (FacilityEntity) getIntent().getSerializableExtra("disclosureEntity");
        entityDetail = (MessageEntity) getIntent().getSerializableExtra("entityDetail");
        tvTitle.setText(entity.getLocationName());
        tId = entityDetail.getTid();
        List<DisclosureEntity> disclose = entityDetail.getDisclose();
        if (U.isNotEmpty(disclose)) {
            for (DisclosureEntity dis : disclose) {
                if(dis.getDoerId().equals(UserManager.getInstance().userData.getUid())){// 是该执行人
                    itemid = dis.getItemId();
                    break;
                }
            }
            LayoutManager.getInstance().init(this).initRecyclerView(recyclerView, true);
            adapter = new FacilityDetailAdapter(this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void initData() {
        getItemRow();
    }

    private void getItemRow() {
        HttpManager.getInstance().doGetItemRow("FacilityDetailActivity", tId, itemid, eventId, entity.getLocId(),
                new HttpCallBack<BaseDataModel<FacilityEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<FacilityEntity> data) {
                        disEntityList.clear();
                        disEntityList.addAll(data.getData());
                        adapter.update(disEntityList);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("FacilityDetailActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(FacilityDetailActivity.this);
                            return;
                        }
                        U.showToast(errorMsg);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("FacilityDetailActivity", throwable.getMessage());
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
                writeDisclosure();
                break;
        }
    }

    private void writeDisclosure() {
        for (int i = 0; i < adapter.writeSrcs.size(); i++) {
            if (U.isEmpty(adapter.writeSrcs.get(i).getValue())) {
                U.showToast(disEntityList.get(i).getName() + "不能为空。");
                return;
            }
        }
        String json = new Gson().toJson(adapter.writeSrcs);
        HttpManager.getInstance().doWriteDisclosure("FacilityDetailActivity", eventId, itemid,
                entity.getLocId(), json, new HttpCallBack<BaseDataModel<FacilityEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<FacilityEntity> data) {
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("FacilityDetailActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(FacilityDetailActivity.this);
                            return;
                        } else if (statusCode == 10012) {
                            U.showToast("技术交底过期或终止!");
                            return;
                        }
                        U.showToast(errorMsg);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("FacilityDetailActivity", throwable.getMessage());
                    }
                });
    }
}
