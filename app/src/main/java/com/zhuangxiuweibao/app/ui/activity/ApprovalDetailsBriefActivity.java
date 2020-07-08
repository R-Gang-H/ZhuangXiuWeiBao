package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.ApprovalEntity;
import com.zhuangxiuweibao.app.bean.BaseBeanModel;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.ToUIEvent;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.ui.adapter.ApprovalTextAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

import static com.zhuangxiuweibao.app.ui.activity.ApprovalDetailsActivity.RESULT_1;
import static com.zhuangxiuweibao.app.ui.activity.ApprovalDetailsActivity.RESULT_2;
import static com.zhuangxiuweibao.app.ui.activity.ApprovalDetailsActivity.RESULT_3;
import static com.zhuangxiuweibao.app.ui.activity.ApprovalDetailsActivity.RESULT_4;

/**
 * 审批详情简述
 */
public class ApprovalDetailsBriefActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView mtvTitle;
    @BindView(R.id.tv_text)
    TextView mtvText;
    @BindView(R.id.tv_time)
    TextView mtvTime;
    @BindView(R.id.xre_list)
    RecyclerView mxreList;
    @BindView(R.id.tv_order)
    TextView mtvOrder;
    @BindView(R.id.tv_bo)
    TextView mtvBo;
    @BindView(R.id.tv_right)
    TextView btnRight;
    @BindView(R.id.tv_left)
    TextView btnLeft;
    @BindView(R.id.lin_menu)
    LinearLayout linMenu;
    @BindView(R.id.rel_menu)
    RelativeLayout relMenu;

    private List<ApprovalEntity> list = new ArrayList<>();
    private ApprovalTextAdapter adapter;

    private String content;
    private String eventId;
    private MessageEntity entity;
    private String status;
    private String pid;
    private String cid,orderId;
    private MessageEntity msgEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_approval_details_brief;
    }

    @Override
    public void initView() {
        mtvTitle.setText("审批详情");
        initList();
    }

    @Override
    public void initData() {
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (U.isNotEmpty(msgEntity)) {
            eventId = msgEntity.getEventId();
            UserManager.getInstance().isReads(eventId);//处理为已读消息
        } else {
            orderId = getIntent().getStringExtra("orderId");
        }
        getDetail();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_1://转给
                if (data != null) {
                    pid = data.getStringExtra("pid");
                    shareToParter();
                }
                break;
            case RESULT_2://追加
                if (data != null) {
                    cid = data.getStringExtra("cid");
                    addChecker();
                }
                break;
            case RESULT_3://回复
            case RESULT_4://同意并回复
                if (data != null) {
                    content = data.getStringExtra("content");
                    reply();
                }
                break;
        }
    }

    //36 审批详情/群通知详情（完成）
    public void getDetail() {//1任务，2群通知 ，3审批
        HttpManager.getInstance().getDetail("ApprovalDetailsActivity", eventId, orderId, "3",
                new HttpCallBack<BaseDataModel<MessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        entity = data.getData().get(0);
                        setData(entity);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ApprovalDetailsBriefActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    //37 回复（完成）
    private void reply() {
        HttpManager.getInstance().reply("ApprovalDetailsBriefActivity", eventId, status, content,
                new HttpCallBack<BaseBeanModel>() {
                    @Override
                    public void onSuccess(BaseBeanModel data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                        U.showToast("回复成功");
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ApprovalDetailsBriefActivity.this);
                            return;
                        } else if (statusCode == 1007) {
                            //U.showToast("当前进度已经同意并回复,已经有其他上级同意过!");
                            U.showToast("当前进度已经同意!");
                            return;
                        } else if (statusCode == 10012) {
                            U.showToast("审批已取消");
                            return;
                        }
                        U.showToast(errorMsg + "");
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    //38 转给同事
    private void shareToParter() {
        HttpManager.getInstance().shareToParter("ApprovalDetailsBriefActivity", eventId, pid, new HttpCallBack<BaseBeanModel>() {
            @Override
            public void onSuccess(BaseBeanModel data) {
                EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                U.showToast("完成");
            }

            @Override
            public void onFail(int statusCode, String errorMsg) {
                if (statusCode == 1003) {//异地登录
                    U.showToast("该账户在异地登录");
                    HttpManager.getInstance().dologout(ApprovalDetailsBriefActivity.this);
                    return;
                } else if (statusCode == 10012) {
                    U.showToast("审批已撤销");
                    return;
                }
                U.showToast(errorMsg + "");
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    //39 追加审批人（完成）
    private void addChecker() {
        HttpManager.getInstance().addChecker("ApprovalDetailsBriefActivity", eventId, cid, new HttpCallBack<BaseBeanModel>() {
            @Override
            public void onSuccess(BaseBeanModel data) {
                EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                U.showToast("完成");
            }

            @Override
            public void onFail(int statusCode, String errorMsg) {
                if (statusCode == 1003) {//异地登录
                    U.showToast("该账户在异地登录!");
                    HttpManager.getInstance().dologout(ApprovalDetailsBriefActivity.this);
                    return;
                }
                U.showToast(errorMsg + "");
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    private void setData(MessageEntity entity) {
        mtvText.setText(entity.getTitle());
        mtvOrder.setText(String.format("审批%s", entity.getCreateAt()));
        mtvTime.setText(DateUtils.getDateToString(Long.valueOf(entity.getCreateAt()), "yyyy/MM/dd HH:mm"));
        String myUid = UserManager.getInstance().userData.getUid();
        if (entity.getWorkers() != null && entity.getWorkers().size() != 0 && myUid != null) {
            for (int i = 0; i < entity.getWorkers().size(); i++) {
                if (myUid.equals(entity.getWorkers().get(i).getWid())) {//自己作为审批人
                    relMenu.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
        //判断按钮显示
        if (entity.getAgreeStatus().equals("1")) {//	同意状态 1 同意并回复 2未点击同意按钮
            //判断审批类型 审批类型 1固定流程/2自定义审批
            if (entity.getApproveType().equals("1")) {
                //转给同事  判断是否转给同事
                if (entity.getForwardStatus().equals("2")) {//转给同事状态 1已转 2 未转
                    mtvBo.setText("回复");
                    linMenu.setVisibility(View.GONE);
                } else {
                    linMenu.setVisibility(View.VISIBLE);
                    mtvBo.setText("转给同事");
                }
            } else {//追加审批人
                linMenu.setVisibility(View.VISIBLE);
                mtvBo.setText("追加审批人");
            }
        } else {//未点击同意按钮
            if (entity.getApproveType().equals("1")) {
                //转给同事  判断是否转给同事
                if (entity.getForwardStatus().equals("2")) {//转给同事状态 1未转 2 已转
                    mtvBo.setVisibility(View.GONE);
                    linMenu.setVisibility(View.VISIBLE);
                } else {
                    mtvBo.setText("转给同事");
                }
            } else {//追加审批人
                linMenu.setVisibility(View.VISIBLE);
                mtvBo.setText("追加审批人");
            }
        }
        list.clear();
        for (MessageEntity entity1 : entity.getProcess()) {
            //判断当前登录账户和审批人列表是否匹配，是-可以审批/转给，否-抄送人，无最底部按钮
            if (UserManager.getInstance().userData.getUid().equals(entity1.getUid())) {
                mtvBo.setVisibility(View.VISIBLE);
            }
        }
        List<String> strings = new ArrayList<>();
        List<String> name = new ArrayList<>();
        if (msgEntity.getType2().equals("S2")) {// 回复有更新
            for (MessageEntity huifu : entity.getHuifu()) {
                strings.add(huifu.getHuiName());
                name.add(huifu.getHuiConent());
            }
        } else if (msgEntity.getType2().equals("S3")) {// 审批进度有更新
            for (MessageEntity process : entity.getProcess()) {
                strings.add("");
                name.add(process.getContent());
            }
        } else {
            strings.add("申请人");
            strings.add("联系电话");
            strings.add("申请时间");
            strings.add("业务");
            strings.add("业务描述");
            name.add(entity.getAddUserName());
            name.add(entity.getAddMobile());
//                    mtvTime.setText(DateUtils.getDateToString(Long.valueOf(list.get(position).getTime()), "yyyy/MM/dd HH:mm"));
            name.add(DateUtils.getDateToString(Long.valueOf(entity.getCreateAt()), "yyyy/MM/dd HH:mm"));
            name.add(entity.getTitle());
            name.add(entity.getContent());
        }
        for (int i = 0; i < strings.size(); i++) {
            list.add(ApprovalEntity.builder().name(name.get(i)).Introduction(strings.get(i)).build());
        }
        adapter.update(list);
    }

    private void initList() {
        LayoutManager.getInstance().init(this).initRecyclerView(mxreList, true);
        adapter = new ApprovalTextAdapter(list, this);
        mxreList.setAdapter(adapter);
    }


    @OnClick({R.id.rl_back_button, R.id.tv_order, R.id.tv_left, R.id.tv_right, R.id.tv_bo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_order:
                startActivity(new Intent(this, ApprovalDetailsActivity.class)
                        .putExtra("eventId", eventId));
                break;
            case R.id.tv_left://回复
                startActivityForResult(new Intent(this, ReplyActivity.class)
                        .putExtra("type", "reply"), RESULT_3);
                status = "1";
                break;
            case R.id.tv_right://同意并回复
                if (btnRight.getText().equals("完成并回复")) {//回复
                    startActivityForResult(new Intent(this, ReplyActivity.class)
                            .putExtra("type", "agree"), RESULT_4);
                    status = "2";
                } else {//追加审批人
                    startActivityForResult(new Intent(this, SelectPersonActivity.class)
                            .putExtra("type", "2"), RESULT_2);
                }
                break;
            case R.id.tv_bo://追加审批人/转给同事
                if (mtvBo.getText().equals("转给同事")) {
                    startActivityForResult(new Intent(this, SelectPersonActivity.class)
                            .putExtra("type", "1"), RESULT_1);
                } else if (mtvBo.getText().equals("回复")) {//回复
                    startActivityForResult(new Intent(this, ReplyActivity.class)
                            .putExtra("type", "reply"), RESULT_3);
                    status = "1";
                } else {//追加审批人
                    startActivityForResult(new Intent(this, SelectPersonActivity.class)
                            .putExtra("type", "2"), RESULT_2);
                }
                break;
        }
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        if (event.getTag() == ToUIEvent.MESSAGE_EVENT) {
            getDetail();//刷新数据
        }
    }
}
