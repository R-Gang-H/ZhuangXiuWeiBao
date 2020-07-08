package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseBeanModel;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.BaseModel;
import com.zhuangxiuweibao.app.bean.DisclosureEntity;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.bean.UserEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.ui.activity.household.PostDetailsCommunityActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.zhuangxiuweibao.app.ui.activity.ApprovalDetailsActivity.RESULT_3;
import static com.zhuangxiuweibao.app.ui.activity.ApprovalDetailsActivity.RESULT_4;

/**
 * 任务详情-简述
 */
public class TaskDetailBriefActivity extends BaseActivity {

    @BindView(R.id.rel_menu)
    RelativeLayout relMenu;
    @BindView(R.id.lin_menu)
    LinearLayout linMenu;
    @BindView(R.id.tv_title)
    TextView mtvTitle;
    @BindView(R.id.tv_text)
    TextView mtvText;
    @BindView(R.id.tv_task_icon)
    TextView tvTaskIcon;
    @BindView(R.id.tv_time)
    TextView mtvTime;
    @BindView(R.id.tv_content)
    TextView mtvContent;
    @BindView(R.id.tv_order)
    TextView mtvOrder;
    @BindView(R.id.tv_left)
    TextView tvLeft;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_bo)
    TextView mtvBo;

    private String titleName = "";
    private String eventId;
    private String status;
    private String content;
    private MessageEntity msgEntity;
    private MessageEntity entity;
    private boolean isDis;

    @Override
    public int getLayoutId() {
        return R.layout.activity_task_detail_brief;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        getDataByIntent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_3://回复
            case RESULT_4://同意并回复
                if (data != null) {
                    content = data.getStringExtra("content");
                    if (isDis || msgEntity.getType2().equals("Rj1")) {// 技术交底任务
                        disclosureNext();
                    } else {// 普通任务
                        reply();
                    }
                }
                break;
        }
    }

    private void getDataByIntent() {
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (U.isNotEmpty(msgEntity)) {
            eventId = msgEntity.getEventId();
            UserManager.getInstance().isReads(eventId);//处理为已读消息
            int icon = 0;
            switch (msgEntity.getType()) {
                case "1":// 1.任务
                    titleName = "任务";
                    icon = R.mipmap.icon_task;
                    getDetail();
                    break;
                case "8":// 8.小喇叭审核通过的通知
                    titleName = "小喇叭";
                    icon = R.mipmap.icon_laba;
                    mtvText.setText(msgEntity.getTitle());
                    mtvTime.setText(DateUtils.getDateToString(Long.valueOf(msgEntity.getCreateAt()), "yyyy/MM/dd HH:mm"));
                    mtvContent.setText(msgEntity.getContent());
                    mtvOrder.setText(String.format("%s%s", titleName, msgEntity.getCreateAt()));
                    break;
            }
            mtvTitle.setText(String.format("%s详情", titleName));
            tvTaskIcon.setText(titleName);
            tvTaskIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, 0, 0, 0);
        }
    }

    //36 审批详情/群通知详情（完成）
    public void getDetail() {//1任务，2群通知 ，3审批
        HttpManager.getInstance().getDetail("ApprovalDetailsActivity",
                eventId, "", "1", new HttpCallBack<BaseDataModel<MessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        entity = data.getData().get(0);
                        setData(entity);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(TaskDetailBriefActivity.this);
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
                        U.showToast("回复成功");
                        getDetail();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(TaskDetailBriefActivity.this);
                            return;
                        } else if (statusCode == 1007) {
                            //U.showToast("当前进度已经同意并回复,已经有其他上级同意过!");
                            U.showToast("当前进度已经同意!");
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
        relMenu.setVisibility(View.VISIBLE);
        linMenu.setVisibility((entity.getCopyto().size() > 0 && entity.getCopyto().get(0).getCoid()
                .equals(UserManager.getInstance().userData.getUid())) ? View.GONE : View.VISIBLE);// 是抄送人，隐藏按钮
        boolean isSend = entity.getAddMobile().equals(UserManager.getInstance().userData.getMobile());// 是否是任务发布者
        tvRight.setVisibility(entity.getAgreeStatus().equals("1") || isSend ? View.GONE : View.VISIBLE);// 同意状态 1 同意并回复 2未点击同意按钮
        tvLeft.setBackgroundResource(entity.getAgreeStatus().equals("1") || isSend ? R.mipmap.ic_login_btn_bg : R.mipmap.bg_bt_left);
        mtvText.setText(entity.getTitle());
        mtvTime.setText(DateUtils.getDateToString(Long.valueOf(entity.getCreateAt()), "yyyy/MM/dd HH:mm"));
        mtvContent.setText(entity.getContent());
        mtvOrder.setText(String.format("%s%s", titleName, entity.getCreateAt()));

        List<DisclosureEntity> disLists = entity.getDisclose();
        isDis = U.isNotEmpty(disLists);// 不为空是技术交底
        boolean isAllWrit = false;
        boolean isExit = false;
        UserEntity userEntity = UserManager.getInstance().userData;
        if (isDis) {
            mtvBo.setText("填写交底表");
            for (int i = 0; i < disLists.size(); i++) {
                if (userEntity.getUid().equals(disLists.get(i).getDoerId())) {
                    isExit = true;// 循环筛选申请人是否同时是执行人
                    break;
                }
            }
            mtvBo.setVisibility(isExit ? View.VISIBLE : View.GONE);
            isAllWrit = entity.getProcess().size() == disLists.size();// 是否全部执行人都填写了交底表
            if (isAllWrit) {
                if (entity.getAddMobile().equals(userEntity.getMobile())) {// 任务交底表发布人可查看交底表 !isExit &&
                    mtvBo.setVisibility(View.VISIBLE);
                    mtvBo.setText("查看交底表");// 全部执行人填写交底表
//                } else {// 任务交底表执行人
//                    mtvBo.setVisibility(View.GONE);
                }
            }
        }
        //任务完成列表
        if (entity.getProcess() != null) {
            for (MessageEntity entity1 : entity.getProcess()) {
                //判断当前登录账户和审批人列表是否匹配，是-可以审批/转给，否-抄送人，无最底部按钮
                if (userEntity.getUid().equals(entity1.getUid())) {
                    if (!isDis) {
                        mtvBo.setVisibility(View.VISIBLE);
                    } else {// 填写交底表 | if (!isAllWrit) 没有全部填写交底表
                        mtvBo.setText("编辑交底表");// 第一个执行人填写了技术交底表，显示编辑交底表按钮，点击可以编辑
                    }
                }
            }
        }
    }


    @OnClick({R.id.rl_back_button, R.id.tv_left, R.id.tv_right, R.id.tv_order, R.id.tv_bo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_order:
                switch (msgEntity.getType()) {
                    case "1":// 1.任务
                        startActivity(new Intent(this, ApprovalDetailsActivity.class)
                                .putExtra("eventId", eventId)
                                .putExtra("from", "task"));//任务
                        break;
                    case "8":// 8.小喇叭审核通过的通知
                        startActivity(new Intent(this, PostDetailsCommunityActivity.class)
                                .putExtra("msgEntity", msgEntity));//社区交流

                        break;
                }
                break;
            case R.id.tv_left://回复
                startActivityForResult(new Intent(this, ReplyActivity.class)
                        .putExtra("type", "reply"), RESULT_3);
                status = "1";
                break;
            case R.id.tv_right://同意并回复
                startActivityForResult(new Intent(this, ReplyActivity.class)
                        .putExtra("type", "agree"), RESULT_4);
                status = "2";
                break;
            case R.id.tv_bo://追加审批人/转给同事
                String boStr = mtvBo.getText().toString();
                if (boStr.equals("填写交底表") || boStr.equals("编辑交底表")) {//填写交底表 | 编辑交底表
                    startActivity(new Intent(this, FacilityColumnActivity.class)
                            .putExtra("eventId", eventId)
                            .putExtra("entityDetail", entity));
                } else if (boStr.equals("查看交底表")) {
                    startActivity(new Intent(this, AboutUsActivity.class)
                            .putExtra("type", "3")
                            .putExtra("linkUrl", entity.getUrl()));
                }
                break;
        }
    }

    private void disclosureNext() {
        HttpManager.getInstance().doDisclosureNext("TaskDetailBriefActivity", eventId, content, status,
                new HttpCallBack<BaseBeanModel>() {
                    @Override
                    public void onSuccess(BaseBeanModel data) {
                        U.showToast("回复成功");
                        getDetail();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(TaskDetailBriefActivity.this);
                        } else if (statusCode == 10012) {
                            U.showToast("任务终止");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }
}
