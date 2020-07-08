package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.ToUIEvent;
import com.zhuangxiuweibao.app.common.user.UploadModule;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.FullyGridLayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.ItemOffsetDecoration;
import com.zhuangxiuweibao.app.ui.activity.household.PostDetailsCommunityActivity;
import com.zhuangxiuweibao.app.ui.adapter.GridImageAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 社区内容审核
 */
public class ShequAuditDetailActivity extends BaseActivity implements GridImageAdapter.OnItemClickListener {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_no_audit_pass)
    TextView tvNoAuditPass;
    @BindView(R.id.tv_audit_pass)
    TextView tvAuditPass;
    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_worker)
    TextView tvWorker;
    @BindView(R.id.tv_mobile)
    TextView tvMobile;
    @BindView(R.id.tv_work_time)
    TextView tvWorkTtime;
    @BindView(R.id.tv_audit_tag)
    TextView tvAuditTag;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.ll_btn)
    LinearLayout llBtn;
    @BindView(R.id.tv_order)
    TextView tvOrder;

    private String type;// 2:审核通过 3:审核不通过

    private MessageEntity msgEntity, shequDetail;
    private GridImageAdapter adapter;
    private String eventId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_shequ_audit_detail;
    }

    @Override
    public void initView() {
        tvTitle.setText("消息详情");
    }

    @Override
    public void initData() {
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (U.isNotEmpty(msgEntity)) {
            eventId = msgEntity.getEventId();
            UserManager.getInstance().isReads(eventId);//处理为已读消息
            tvText.setText(String.format("%s%s发布的新内容待审核", msgEntity.getXiaoquName(), msgEntity.getAddUserName()));
            tvTime.setText(DateUtils.getDateToString(Long.valueOf(msgEntity.getCreateAt()), "yy/MM/dd HH:mm"));
            FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3,
                    GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(manager);
            adapter = new GridImageAdapter(this, null);
            recyclerView.addItemDecoration(new ItemOffsetDecoration(5, 5));
        }
        getRelease();
    }

    private void getRelease() {
        HttpManager.getInstance().doGetRelease(this.getLocalClassName(), eventId,
                new HttpCallBack<BaseDataModel<MessageEntity>>(this, true) {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        shequDetail = data.getData().get(0);
                        setData(shequDetail);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("ShequAuditDetailActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ShequAuditDetailActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("ShequAuditDetailActivity", throwable.getMessage());
                    }
                });
    }

    private void setData(MessageEntity data) {
        tvWorker.setText(data.getAddUserName());
        tvMobile.setText(data.getAddMobile());
        tvWorkTtime.setText(DateUtils.getDateToString(Long.valueOf(data.getTime()), "MM/dd HH:mm"));
        String tagName = "";
        switch (data.getTag()) {// 1寻物启事 2闲置物品 3为你服务 999不加标签
            case "1":
                tagName = "寻物启事";
                break;
            case "2":
                tagName = "闲置物品";
                break;
            case "3":
                tagName = "为你服务";
                break;
            default:
                tvAuditTag.setVisibility(View.GONE);
                break;
        }
        tvAuditTag.setText(tagName);
        tvContent.setText(U.getEmoji(data.getContent()));
        String[] images = data.getImages().split("#");
        if (images.length > 0 && U.isNotEmpty(images[0])) {
            adapter.pngOravis = Arrays.asList(images);
            for (String image : images) {
                adapter.uploadModules.add(UploadModule.builder().picPath(image).pictureType("jpg").build());
            }
            adapter.setSelectMax(images.length);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(this);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
        tvOrder.setText(String.format("社区交流%s", data.getEventId()));
        llBtn.setVisibility(data.getCheckStatus().equals("1") ? View.VISIBLE : View.GONE);// 1 待审核 2审核通过 3审核不通过
    }

    @OnClick({R.id.rl_back_button, R.id.tv_order, R.id.tv_no_audit_pass, R.id.tv_audit_pass})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_order:
                startActivity(new Intent(this, PostDetailsCommunityActivity.class)
                        .putExtra("msgEntity", msgEntity)
                        .putExtra("shequDetail", shequDetail));
                break;
            case R.id.tv_no_audit_pass:
                type = "3";
                checkRelease();
                break;
            case R.id.tv_audit_pass:
                type = "2";
                checkRelease();
                break;
        }
    }

    private void checkRelease() {
        HttpManager.getInstance().doCheckRelease(this.getLocalClassName(),
                eventId, type, new HttpCallBack<BaseDataModel<MessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("ShequAuditDetailActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ShequAuditDetailActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("ShequAuditDetailActivity", throwable.getMessage());
                    }
                });
    }

    @Override
    public void onItemClick(int position, View v) {
        startActivity(new Intent(this, ImgDetailsActivity.class)
                .putExtra("orderData", shequDetail)
                .putExtra("index", position));
    }
}
