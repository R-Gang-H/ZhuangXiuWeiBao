package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseBeanModel;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.BaseModel;
import com.zhuangxiuweibao.app.bean.DisclosureEntity;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.bean.UserEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.ToUIEvent;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.LoadingFrameView;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.ui.activity.household.PostDetailsCommunityActivity;
import com.zhuangxiuweibao.app.ui.activity.household.PostDetailsHelpActivity;
import com.zhuangxiuweibao.app.ui.adapter.MessageInfoAdapter;
import com.zhuangxiuweibao.app.ui.bean.ImgBean;
import com.zhuangxiuweibao.app.ui.bean.ReasonBean;
import com.zhuangxiuweibao.app.ui.widget.MyPopEvaluation;
import com.zhuangxiuweibao.app.ui.widget.mypopaftersale.MyPopAfterSale;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 获取消息详情
 */
public class MessageInfoActivity extends BaseActivity {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.xrecyclerview)
    XRecyclerView xrecyclerview;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;
    @BindView(R.id.tv_order_id)
    TextView tvOrderId;

    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_change_time)
    TextView tvChangeTime;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.footView)
    LinearLayout footView;
    @BindView(R.id.ll_foot)
    LinearLayout llFoot;
    @BindView(R.id.btnLeft)
    TextView btnLeft;
    @BindView(R.id.btnRight)
    TextView btnRight;
    @BindView(R.id.FeaturesView)
    LinearLayout FeaturesView;
    @BindView(R.id.btnAfterSaleOrder)
    TextView btnAfterSaleOrder;
    @BindView(R.id.rel_root)
    RelativeLayout mrelRoot;

    @BindView(R.id.rel_menu)
    RelativeLayout relMenu;
    @BindView(R.id.lin_menu)
    LinearLayout linMenu;
    @BindView(R.id.tv_left)
    TextView tvLeft;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_bo)
    TextView mtvBo;

    @BindView(R.id.btnSign)
    TextView btnSign;
    @BindView(R.id.btnReply)
    TextView btnReply;
    @BindView(R.id.ll_menu)
    LinearLayout llMenu;
    @BindView(R.id.btnSearch)
    TextView btnSearch;

    @BindView(R.id.tv_submit)
    TextView tvSubmit;

    MessageInfoAdapter adapter;
    ArrayList<MessageEntity> msgData = new ArrayList<>();
    private MessageEntity msgEntity;
    private String eventId, addUserName;
    private MessageEntity weiOrderEntity, mTaskDetail, mNotifiDetail, mApprovalDetail, mHelpiDetail;
    private String addisMaketure, addisMaketure1, addisDone, addisDone1, addisPingjia, addisPingjia1;
    private String addisForward, addisForward1, appointmentTime, appointmentTime1;
    private String status, content;

    private List<ImgBean> star1 = new ArrayList<>();
    private List<ImgBean> star2 = new ArrayList<>();
    private List<ImgBean> star3 = new ArrayList<>();
    private String skill, service, priceEvaluate, comment, reason;

    private MyPopAfterSale mSale;
    private List<ReasonBean> reasons = new ArrayList<>();
    private MyPopEvaluation mPop;
    public static final int RESULT_1 = 1;//转给
    public static final int RESULT_2 = 2;//追加
    public static final int RESULT_3 = 3;//回复
    public static final int RESULT_4 = 4;//同意并回复
    private boolean isDis;
    private int evluation;

    @Override
    public int getLayoutId() {
        return R.layout.activity_message_info;
    }

    @Override
    public void initView() {
        tvTitle.setText("消息详情");
        LayoutManager.getInstance().init(this).iniXrecyclerView(xrecyclerview);
        adapter = new MessageInfoAdapter(msgData, this);
        xrecyclerview.setAdapter(adapter);
        xrecyclerview.setPullRefreshEnabled(true);
        xrecyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                getMessageInfo(true);
            }

            @Override
            public void onLoadMore() {
            }
        });
    }

    @Override
    public void initData() {
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (U.isNotEmpty(msgEntity)) {
            eventId = msgEntity.getEventId();//主页跳转到外链
            addUserName = msgEntity.getAddUserName();//回复人姓名
            UserManager.getInstance().isReads(eventId);//处理为已读消息
            getMessageInfo(true);
        }
    }

    private void getMessageInfo(boolean isClear) {
        HttpManager.getInstance().getMessageInfo("MessageInfoActivity", eventId,
                new HttpCallBack<BaseDataModel<MessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        if (isClear) {
                            msgData.clear();
                        }
                        msgData.addAll(data.getData());
                        adapter.setData(msgData, addUserName);
                        if (msgData.size() > 0) {
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

                        // 替换为最新一条信息
                        msgEntity = msgData.get(msgData.size() - 1);
                        if (U.isNotEmpty(msgEntity)) {
                            eventId = msgEntity.getEventId();//主页跳转到外链
                        }
                        xrecyclerview.scrollToPosition(xrecyclerview.getAdapter().getItemCount());
                        // 替换为最新一条信息

                        // 1：任务 2：通知3：审批 4：紧急求助 5：维保 6：社区交流 7.需要大妈审核的社区交流帖子
                        // 8.小喇叭审核通过的通知 9.群通知
                        String typeMsg = "";
                        switch (msgEntity.getType()) {
                            case "1":// 1.任务
                                getDetail(msgEntity, "1");//1任务，2群通知 ，3审批
                                typeMsg = "任务";
                                break;
                            case "2":
                                typeMsg = "通知";
                                break;
                            case "6":
                                typeMsg = "社区交流";
                                break;
                            case "8":// 8.小喇叭审核通过的通知
                                typeMsg = "小喇叭";
//                                tvOrderId.setVisibility(View.VISIBLE);
//                                tvOrderId.setText(String.format("小喇叭%s", msgEntity.getCreateAt()));
                                break;
                            case "3":// 3.审批
                                getDetail(msgEntity, "3");//1任务，2群通知 ，3审批
                                typeMsg = "审批";
                                break;
                            case "4"://紧急求助
//                                tvTitle.setText(R.string.postHelp);
                                emergencyHelp(msgEntity);
                                typeMsg = "紧急求助";
                                break;
                            case "5":// 5.维保
                                getWeiOrder(msgEntity);
                                typeMsg = "维保";
                                break;
                            case "9":// 9.群通知
                                typeMsg = "群通知";
                                getDetail(msgEntity, "2");//1任务，2群通知 ，3审批
                                break;
                        }
                        tvOrderId.setVisibility(View.VISIBLE);
                        tvOrderId.setText(String.format(typeMsg + "%s", msgEntity.getCreateAt()));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("MessageInfoActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MessageInfoActivity.this);
                            return;
                        }
//                        U.showToast(errorMsg);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("MessageInfoActivity", throwable.getMessage());
                    }
                });
    }

    private void getWeiOrder(MessageEntity messageEntity) {
        HttpManager.getInstance().doGetWeiOrder("MessageInfoActivity", messageEntity.getEventId(),
                messageEntity.getWeiOrderId(), new HttpCallBack<BaseDataModel<MessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        setWeiOrderData(data.getData().get(0));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("MessageInfoActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MessageInfoActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("MessageInfoActivity", throwable.getMessage());
                    }
                });
    }

    private void setWeiOrderData(MessageEntity data) {
        tvOrderId.setText(String.format("维保订单%s", data.getOrderNum()));
        addisMaketure = data.getAddisMaketure();//住户是否确认了上门时间 1已经确认 2没有确认
        addisMaketure1 = data.getAddisMaketure1();//住户是否确认了上门时间 1已经确认 2没有确认
        addisDone = data.getAddisDone();
        addisDone1 = data.getAddisDone1();
        addisPingjia = data.getAddisPingjia();
        addisPingjia1 = data.getAddisPingjia1();
        addisForward = data.getAddisForward();//判断是否转给同事  1已操作  2未操作
        addisForward1 = data.getAddisForward1();//判断是否转给同事  1已操作  2未操作
        appointmentTime = data.getAppointmentTime();// 是否预约上门时间 0没有预约，其他预约
        appointmentTime1 = data.getAppointmentTime1();// 是否预约上门时间 0没有预约，其他预约
        weiOrderEntity = data;
        setMsgData(data);
    }

    //主页跳转过来的维保详情
    private void setMsgData(MessageEntity data) {
        switch (msgEntity.getType2()) {
            //住户↓↓↓
            case "10":// 完成服务  详细消息，外链：外链按钮问题已解决，问题未解决  住户
                // 工作人员↓↓↓
            case "4":// 已安排服务人员   有外链，显示详情，外链按钮：转给同事  预约上门时间
            case "5":// 分配或选择转给维保人员  工作人员
            case "8":// 住户选择修改上门时间
            case "9":// 住户选择确认上门时间  工作人员
//                btnAfterSaleOrder.setVisibility(View.VISIBLE);
//                btnAfterSaleOrder.setText("服务完成");
                statusCharge(data.getStatus());
                break;
            case "11":// 住户点击问题已解决
                tvOrderId.setVisibility(View.VISIBLE);
                break;
            //住户↓↓↓
            case "6"://已为您安排服务人员
                //住户是否确认了上门时间 1已经确认 2没有确认
                tvOrderId.setVisibility(View.VISIBLE);
                footView.setVisibility(data.getIsOrder().equals("0") ?
                        addisMaketure.equals("2") ? View.VISIBLE : View.GONE :
                        addisMaketure1.equals("2") ? View.VISIBLE : View.GONE);//取消服务，修改上门时间，确认
                break;
        }
    }

    private void statusCharge(String status) {
        tvOrderId.setVisibility(View.VISIBLE);
        switch (status) {//llSolve 问题是否解决 llBtn修改上门时间   btnAfterSaleOrder售后
            /*
             * 维保订单当前所处的状态 1：待审核 2：审核 3：审核不通过
             * 4.维保人员还没有设置服务时间 5.已分配维保人员
             * 6：用户已经确认了服务时间 7：服务完成8：已取消9：售后10：评价*/
            case "4"://4：设置服务时间 转给同事，预约上门时间
            case "5"://5：已分配维保人员 addisMaketure.equals("2") ||
                //addisMaketure1.equals("2") ||
                llFoot.setVisibility((weiOrderEntity.getIsOrder().equals("0") ?
                        addisForward.equals("1") || !appointmentTime.equals("0") ? View.GONE : View.VISIBLE :
                        addisForward1.equals("1") || !appointmentTime1.equals("0") ? View.GONE : View.VISIBLE));
                btnAfterSaleOrder.setVisibility(View.GONE);
                break;
            case "6"://6：用户已经确认了服务时间
                btnAfterSaleOrder.setVisibility(View.VISIBLE);
                btnAfterSaleOrder.setText("服务完成");
                break;
            case "7":
                UserEntity userData = UserManager.getInstance().userData;
                if (userData.getFirstIdentity().equals("1") || userData.getFirstIdentity().equals("3")) {// 1住户 2工作人员 3”大妈”
                    FeaturesView.setVisibility(View.VISIBLE);
                    switch (TextUtils.equals(weiOrderEntity.getStatus2(), "0") ? addisDone : addisDone1) {
                        //本次服务是否解决了问题 0住户还没有操作 1住户点击了【问题已解决】 2住户点击了【问题未解决】
                        case "0":
                            btnLeft.setText("问题未解决");
                            btnRight.setText("问题已解决");
                            break;
                        case "1"://问题已解决
                            // status2：0 维保订单显示申请售后，其他值是售后订单不显示审批售后
                            btnLeft.setVisibility(TextUtils.equals(weiOrderEntity.getStatus2(), "0") ? View.VISIBLE : View.GONE);
                            btnLeft.setText("申请售后");
                            btnRight.setText("去评价");
                            btnRight.setBackgroundResource(TextUtils.equals(weiOrderEntity.getStatus2(), "0")
                                    ? R.mipmap.bg_bt_left : R.mipmap.ic_login_btn_bg);
                            break;
                        case "2"://问题未解决
                            FeaturesView.setVisibility(View.GONE);
                            btnAfterSaleOrder.setVisibility(View.VISIBLE);
                            btnAfterSaleOrder.setText("申请售后");
                            // status2：0 维保订单显示申请售后，其他值是售后订单不显示审批售后
                            btnAfterSaleOrder.setVisibility(TextUtils.equals(weiOrderEntity.getStatus2(), "0") ? View.VISIBLE : View.GONE);
                            break;
                    }
                }
                break;
            case "9"://售后
                btnAfterSaleOrder.setVisibility(View.GONE);
                btnAfterSaleOrder.setText("查看售后详情");
                statusCharge(weiOrderEntity.getStatus2());
                break;
            case "10"://评价
                // 住户是否评价了本次服务 1已经评价 2没有评价
                btnAfterSaleOrder.setVisibility((weiOrderEntity.getStatus2().equals("0") ?
                        addisPingjia : addisPingjia1).equals("2") ? View.VISIBLE : View.GONE);
                btnAfterSaleOrder.setText("去评价");
                break;
        }
    }

    @OnClick({R.id.rl_back_button, R.id.tv_order_id, R.id.tv_turn, R.id.tv_make, R.id.tv_cancel,
            R.id.tv_change_time, R.id.tv_confirm, R.id.btnLeft, R.id.btnRight, R.id.btnAfterSaleOrder,
            R.id.tv_left, R.id.tv_right, R.id.tv_bo, R.id.btnSign, R.id.btnReply, R.id.btnSearch,
            R.id.tv_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_order_id:
                // 1：任务 2：通知3：审批 4：紧急求助 5：维保 6：社区交流 7.需要大妈审核的社区交流帖子
                // 8.小喇叭审核通过的通知 9.群通知
                switch (msgEntity.getType()) {
                    case "1":// 1.任务
                        startActivity(new Intent(this, ApprovalDetailsActivity.class)
                                .putExtra("eventId", eventId)
                                .putExtra("from", "task"));//任务
                        break;
                    case "2":
                    case "6":
                    case "8":// 8.小喇叭审核通过的通知
                        startActivity(new Intent(this, PostDetailsCommunityActivity.class)
                                .putExtra("msgEntity", msgEntity));//社区交流
                        break;
                    case "3":// 3.审批
                        startActivity(new Intent(this, ApprovalDetailsActivity.class)
                                .putExtra("eventId", eventId));
                        break;
                    case "4":// 4：紧急求助
                        startActivity(new Intent(this, PostDetailsHelpActivity.class)
                                .putExtra("msgEntity", msgEntity));
                        break;
                    case "5":// 5.维保
                        startActivity(new Intent(mContext, OrderDetailActivity.class)
                                .putExtra("msgEntity", msgEntity));
                        break;
                    case "9":// 9.群通知
                        startActivity(new Intent(mContext, GroupNotifiDetailActivity.class)
                                .putExtra("msgEntity", msgEntity));
                        break;
                }
                break;
            case R.id.tv_turn:
                // 38 转给同事（完成）
                startActivityForResult(new Intent(this, SelectPersonActivity.class)
                        .putExtra("type", "1"), RESULT_1);
                break;
            case R.id.tv_make:
            case R.id.tv_change_time:
                getUpdateTime();
                break;
            case R.id.tv_cancel:
                cancelService();
                break;
            case R.id.tv_confirm:
                confirmTime();
                break;
            case R.id.btnLeft:
                switch (btnLeft.getText().toString()) {
                    case "问题未解决":
                        weiConfirm("2");
                        break;
                    case "申请售后":
                        afterSale();
                        break;
                }
                break;
            case R.id.btnRight:
                switch (btnRight.getText().toString()) {
                    case "问题已解决":
                        weiConfirm("1");// 1 问题已解决
                        break;
                    case "去评价":
                        evluation = 1;
                        evaluation();
                        break;
                }
                break;
            case R.id.btnAfterSaleOrder:
                switch (btnAfterSaleOrder.getText().toString()) {
                    case "申请售后":
                        afterSale();
                        break;
                    case "去评价":
                        evluation = 2;
                        evaluation();
                        break;
                    case "服务完成":
                        weiDone();
                        break;
                }
                break;
            case R.id.tv_left://回复
                startActivityForResult(new Intent(this, ReplyActivity.class)
                        .putExtra("type", "reply"), RESULT_3);
                status = "1";
                break;
            case R.id.tv_right://同意并回复
                if (tvRight.getText().equals("完成并回复")) {//回复
                    startActivityForResult(new Intent(this, ReplyActivity.class)
                            .putExtra("type", "agree"), RESULT_4);
                    status = "2";
                } else {//追加审批人
                    startActivityForResult(new Intent(this, SelectPersonActivity.class)
                            .putExtra("type", "2"), RESULT_2);
                }
                break;
            case R.id.tv_bo://追加审批人/转给同事
                String boStr = mtvBo.getText().toString();
                if (boStr.equals("填写交底表") || boStr.equals("编辑交底表")) {//填写交底表 | 编辑交底表
                    startActivity(new Intent(this, FacilityColumnActivity.class)
                            .putExtra("eventId", eventId)
                            .putExtra("entityDetail", mTaskDetail));
                } else if (boStr.equals("查看交底表")) {
                    startActivity(new Intent(this, AboutUsActivity.class)
                            .putExtra("type", "3")
                            .putExtra("linkUrl", mTaskDetail.getUrl()));
                } else if (mtvBo.getText().equals("转给同事")) {
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
            case R.id.btnSign:
                if (U.isNotEmpty(mNotifiDetail.getSignBeginTime())) {
                    // 如果未到回执开始时间，点击回执按钮，弹出提示：还未到回执开始时间
                    // 3.如果超过回执结束时间，点击回执按钮，弹出提示：回执时间已过
                    if (U.timeEqual(mNotifiDetail.getSignBeginTime()).equals("1")) {
                        U.showToast("还未到回执开始时间");
                        return;
                    }
                    if (U.timeEqual(mNotifiDetail.getSignEndTime()).equals("-1")) {
                        U.showToast("回执时间已过");
                        return;
                    }
                    replyto(eventId);
                }
                break;
            case R.id.btnReply:
                status = "1";
                startActivityForResult(new Intent(this, ReplyActivity.class)
                        .putExtra("type", "reply"), RESULT_3);
                break;
            case R.id.btnSearch:
                if (btnSearch.getText().toString().equals("回复")) {
                    status = "1";
                    startActivityForResult(new Intent(this, ReplyActivity.class)
                            .putExtra("type", "reply"), RESULT_3);
                } else {//查看报名情况
                    startActivity(new Intent(this, ReceiptActivity.class)
                            .putExtra("eventId", eventId));
                }
                break;
            case R.id.tv_submit:
                if (tvSubmit.getText().equals("回执"))
                    replyto(eventId);
                else {
//                    回复
                    getReply();
                }
                break;
        }
    }

    //42 回执（完成）
    private void replyto(String eventId) {
        HttpManager.getInstance().replyto("ApprovalDetailsActivity", eventId,
                new HttpCallBack<BaseBeanModel>() {
                    @Override
                    public void onSuccess(BaseBeanModel data) {
                        U.showToast("成功");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        U.showToast(errorMsg + "");
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    //回执成功之后进行回复
    private void getReply() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.pop_edittext, null);
        TextView tv_ok = view.findViewById(R.id.btnSubmit);
        ImageView ivClose = view.findViewById(R.id.iv_close);
        EditText etContent = view.findViewById(R.id.etContent);
        tv_ok.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etContent.getText().toString())) {
                Toast.makeText(this, "请输入回复内容", Toast.LENGTH_SHORT).show();
            } else {
                sosResmsg(etContent.getText().toString());
                dialog.dismiss();
            }
        });
        ivClose.setOnClickListener(v -> dialog.dismiss());
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
    }

    private void sosResmsg(String string) {
        HttpManager.getInstance().sosResmsg("MessageInfoActivity", eventId, string,
                new HttpCallBack<BaseBeanModel>() {
                    @Override
                    public void onSuccess(BaseBeanModel data) {
                        U.showToast("成功");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MessageInfoActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    //取消服务
    private void cancelService() {
        HttpManager.getInstance().cancelService("MessageInfoActivity",
                msgEntity.getWeiOrderId(), new HttpCallBack<BaseDataModel<MessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MessageInfoActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("MessageInfoActivity", throwable.getMessage());
                    }
                });
    }

    private void confirmTime() {
        HttpManager.getInstance().doConfirmTime("MessageInfoActivity",
                msgEntity.getEventId(), msgEntity.getWeiOrderId(),
                new HttpCallBack<BaseDataModel<MessageEntity>>(this) {

                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("MessageInfoActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MessageInfoActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("MessageInfoActivity", throwable.getMessage());
                    }
                });
    }

    private void getUpdateTime() {
        new TimePickerBuilder(this, (date, v) -> {
            AppointTime(String.valueOf(date.getTime() / 1000));
        })
                .setType(new boolean[]{true, true, true, true, true, false})
                .setRangDate(Calendar.getInstance(), null)
                .setLabel("年", "月", "日", "时",
                        "分", "")
                .build().show();
    }

    private void AppointTime(String orderTime) {
        HttpManager.getInstance().doAppointTime("MessageInfoActivity",
                msgEntity.getEventId(), msgEntity.getWeiOrderId(), orderTime,
                new HttpCallBack<BaseDataModel<MessageEntity>>(this) {

                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("MessageInfoActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MessageInfoActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("MessageInfoActivity", throwable.getMessage());
                    }
                });
    }

    // 46、维保人员点击服务完成（完成）
    private void weiDone() {
        HttpManager.getInstance().doWeiDone("MessageInfoActivity", msgEntity.getEventId(),
                msgEntity.getWeiOrderId(), new HttpCallBack<BaseDataModel<MessageEntity>>(this) {

                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        U.showToast("成功");
                        btnAfterSaleOrder.setVisibility(View.GONE);
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("MessageInfoActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MessageInfoActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("MessageInfoActivity", throwable.getMessage());
                    }
                });
    }

    /**
     * 发表评论
     */
    private void evaluation() {
        if (mPop == null) {
            mPop = new MyPopEvaluation(this, mrelRoot, str -> {
                //提交回调 str[0] 施工技术 str[1] 服务态度 str[2] 价格 str[3] 评价内容
                skill = str[0];
                service = str[1];
                priceEvaluate = str[2];
                comment = str[3];
                orderEvaluate();
            });
            for (int i = 0; i < 5; i++) {
                star1.add(ImgBean.builder().res(R.mipmap.icon_star2).build());
                star2.add(ImgBean.builder().res(R.mipmap.icon_star2).build());
                star3.add(ImgBean.builder().res(R.mipmap.icon_star2).build());
            }
            mPop.setData(star1, 1);
            mPop.setData(star2, 2);
            mPop.setData(star3, 3);
        }
        mPop.show();
    }

    private void orderEvaluate() {//评价
        if (U.isEmpty(skill) || U.isEmpty(service) || U.isEmpty(priceEvaluate)) {
            U.showToast("请进行评价");
            return;
        }
        HttpManager.getInstance().doOrderEvaluate("MessageInfoActivity", msgEntity.getEventId(), msgEntity.getWeiOrderId()
                , skill, service, priceEvaluate, comment, new HttpCallBack<BaseDataModel<BaseDataModel<MessageEntity>>>() {
                    @Override
                    public void onSuccess(BaseDataModel<BaseDataModel<MessageEntity>> data) {
                        dialogResult();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("MessageInfoActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MessageInfoActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("MessageInfoActivity", throwable.getMessage());
                    }
                });
    }

    /**
     * 评价成功弹框
     */
    private void dialogResult() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_info_check, null);
        TextView tvGo = view.findViewById(R.id.tv_go);
        TextView tvText1 = view.findViewById(R.id.tv_text1);
        TextView tvText = view.findViewById(R.id.tv_text);
        tvGo.setVisibility(View.GONE);
        tvText1.setVisibility(View.GONE);
        tvText.setText("感谢评价");
        new Handler().postDelayed(() -> {
            dialog.dismiss();
            EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
            if (evluation == 2) {
                btnAfterSaleOrder.setVisibility(View.GONE);
            } else if (evluation == 1) {
                FeaturesView.setVisibility(View.GONE);
            }
            mPop.dismiss();
        }, 2000);
        dialog.setView(view);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 申请售后
     */
    private void afterSale() {
        if (mSale == null) {
            reasons.add(ReasonBean.builder().isSelect(false).reason("质量问题需要返修").build());
            reasons.add(ReasonBean.builder().isSelect(false).reason("收费问题需要退款").build());
            reasons.add(ReasonBean.builder().isSelect(false).reason("其他").build());
            //提交
            mSale = new MyPopAfterSale(this, mrelRoot, position -> {
                //item点击
                for (int i = 0; i < reasons.size(); i++) {
                    if (i == position) {
                        reasons.get(i).setSelect(true);
                        reason = reasons.get(i).getReason();
                    } else {
                        reasons.get(i).setSelect(false);
                    }
                }
                mSale.update(reasons);
            }, this::askAfter);
        }
        mSale.update(reasons);
        mSale.show();
    }

    private void askAfter() {//申请售后
        if (U.isEmpty(reason)) {
            U.showToast("申请原因");
            return;
        }
        HttpManager.getInstance().doAskAfter("MessageInfoActivity", msgEntity.getEventId(), msgEntity.getWeiOrderId()
                , reason, new HttpCallBack<BaseDataModel<MessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        mSale.show();
                        U.showToast("申请成功");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("MessageInfoActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MessageInfoActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("MessageInfoActivity", throwable.getMessage());
                    }
                });
    }

    private void weiConfirm(String type) {//处理问题解决或者未解决
        HttpManager.getInstance().doWeiConfirm("MessageInfoActivity", msgEntity.getEventId(),
                msgEntity.getWeiOrderId(), type, new HttpCallBack<BaseDataModel<MessageEntity>>(this) {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        U.showToast("成功");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("MessageInfoActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MessageInfoActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("MessageInfoActivity", throwable.getMessage());
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_1://转给
                if (data != null) {
                    String pid = data.getStringExtra("pid");
                    shareToParter(pid);
                }
                break;
            case RESULT_2://追加
                if (data != null) {
                    String cid = data.getStringExtra("cid");
                    addChecker(cid);
                }
                break;
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

    //38 转给同事
    private void shareToParter(String pid) {
        HttpManager.getInstance().shareToParter("MessageInfoActivity", msgEntity.getEventId(),
                pid, new HttpCallBack<BaseBeanModel>() {
                    @Override
                    public void onSuccess(BaseBeanModel data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("MessageInfoActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MessageInfoActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("MessageInfoActivity", throwable.getMessage());
                    }
                });
    }

    //39 追加审批人（完成）
    private void addChecker(String cid) {
        HttpManager.getInstance().addChecker("ApprovalDetailsBriefActivity",
                msgEntity.getEventId(), cid, new HttpCallBack<BaseBeanModel>() {
                    @Override
                    public void onSuccess(BaseBeanModel data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                        U.showToast("完成");
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MessageInfoActivity.this);
                            return;
                        }
                        U.showToast(errorMsg + "");
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    //36 审批详情/群通知详情（完成）
    public void getDetail(MessageEntity messageEntity, String type) {//1任务，2群通知 ，3审批
        HttpManager.getInstance().getDetail("MessageInfoActivity",
                messageEntity.getEventId(), "",
                type, new HttpCallBack<BaseDataModel<MessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        switch (type) {
                            case "1":
                                setTaskDetail(data.getData().get(0));
                                break;
                            case "2":
                                setNotifiDetail(data.getData().get(0));
                                break;
                            case "3":
                                setApprowalData(data.getData().get(0));
                                break;
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MessageInfoActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("MessageInfoActivity", throwable.getMessage());
                    }
                });
    }

    private void setTaskDetail(MessageEntity entity) {
        mTaskDetail = entity;
        relMenu.setVisibility(View.VISIBLE);
        linMenu.setVisibility((entity.getCopyto().size() > 0 && entity.getCopyto().get(0).getCoid()
                .equals(UserManager.getInstance().userData.getUid())) ? View.GONE : View.VISIBLE);// 是抄送人，隐藏按钮
        boolean isSend = entity.getAddMobile().equals(UserManager.getInstance().userData.getMobile());// 是否是任务发布者
        tvRight.setVisibility(entity.getAgreeStatus().equals("1") || isSend ? View.GONE : View.VISIBLE);// 同意状态 1 同意并回复 2未点击同意按钮
//        tvLeft.setVisibility(entity.getAgreeStatus().equals("1") || isSend ? View.GONE : View.VISIBLE);// 同意状态 1 同意并回复 2未点击同意按钮
        tvLeft.setBackgroundResource(entity.getAgreeStatus().equals("1") || isSend ? R.mipmap.ic_login_btn_bg : R.mipmap.bg_bt_left);
        tvOrderId.setVisibility(View.VISIBLE);
        tvOrderId.setText(String.format("任务%s", entity.getCreateAt()));

        List<DisclosureEntity> disLists = entity.getDisclose();
        isDis = U.isNotEmpty(disLists);// 不为空是技术交底
        boolean isAllWrit;
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
                    if (isDis) {// 填写交底表 | if (!isAllWrit) 没有全部填写交底表
                        mtvBo.setText("编辑交底表");// 第一个执行人填写了技术交底表，显示编辑交底表按钮，点击可以编辑
                    }
                }
            }
        }
    }

    private void setNotifiDetail(MessageEntity dataModel) {
        mNotifiDetail = dataModel;
        btnSearch.setVisibility(View.VISIBLE);
        tvOrderId.setVisibility(View.VISIBLE);
        tvOrderId.setText(String.format("群通知%s", msgEntity.getCreateAt()));
        // sign 群通知类型 1带报名/默认
        if (dataModel.getSign().equals("2")) {
            btnSearch.setText("回复");
        } else {
            llMenu.setVisibility(View.VISIBLE);
            btnSign.setVisibility(dataModel.getReceiptStatus().equals("1") ? View.GONE : View.VISIBLE);//回执状态 1 已回执 2 未回执
            btnReply.setBackgroundResource(dataModel.getReceiptStatus().equals("1") ? R.mipmap.ic_login_btn_bg : R.mipmap.bg_bt_left);
            btnSearch.setText("查看回执情况");
        }
    }

    private void setApprowalData(MessageEntity entity) {
        mApprovalDetail = entity;
        tvOrderId.setVisibility(View.VISIBLE);
        tvOrderId.setText(String.format("审批%s", entity.getCreateAt()));
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
                    mtvBo.setVisibility(View.VISIBLE);
                    linMenu.setVisibility(View.GONE);
                } else {
                    linMenu.setVisibility(View.VISIBLE);
                    mtvBo.setVisibility(View.VISIBLE);
                    mtvBo.setText("转给同事");
                }
            } else {//追加审批人
                linMenu.setVisibility(View.VISIBLE);
                mtvBo.setVisibility(View.VISIBLE);
                mtvBo.setText("追加审批人");
            }
        } else {//未点击同意按钮
            if (entity.getApproveType().equals("1")) {// 1固定流程/2自定义审批
                //转给同事  判断是否转给同事
                if (entity.getForwardStatus().equals("2")) {//转给同事状态 1未转 2 已转
                    mtvBo.setVisibility(View.GONE);
                    linMenu.setVisibility(View.VISIBLE);
                } else {
                    mtvBo.setVisibility(View.VISIBLE);
                    mtvBo.setText("转给同事");
                }
            } else {//追加审批人
                linMenu.setVisibility(View.VISIBLE);
                mtvBo.setVisibility(View.VISIBLE);
                mtvBo.setText("追加审批人");
            }
        }
    }

    private void emergencyHelp(MessageEntity messageEntity) {
        HttpManager.getInstance().emergencyHelp("MessageInfoActivity",
                messageEntity.getEventId(), new HttpCallBack<BaseModel<MessageEntity>>() {
                    @Override
                    public void onSuccess(BaseModel<MessageEntity> data) {
                        mHelpiDetail = data.getData();
                        if (mHelpiDetail != null) {
                            setHelpData();
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MessageInfoActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    private void setHelpData() {
        tvSubmit.setVisibility(mHelpiDetail.getAddUserName()
                .equals(UserManager.getInstance().userData.getName())
                ? View.GONE : View.VISIBLE);// 当前用户不显示
        tvOrderId.setVisibility(View.VISIBLE);
        tvOrderId.setText(String.format("紧急求助%s", mHelpiDetail.getTime()));
        if (mHelpiDetail.getStatus().equals("0")) {
            tvSubmit.setText("回执");
        } else {
            tvSubmit.setText("回复");
        }
    }

    private void disclosureNext() {
        HttpManager.getInstance().doDisclosureNext("MessageInfoActivity", eventId, content, status,
                new HttpCallBack<BaseBeanModel>() {
                    @Override
                    public void onSuccess(BaseBeanModel data) {
                        U.showToast("回复成功");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MessageInfoActivity.this);
                        } else if (statusCode == 10012) {
                            U.showToast("任务终止");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    //37 回复（完成）
    private void reply() {
        HttpManager.getInstance().reply("MessageInfoActivity", eventId, status, content,
                new HttpCallBack<BaseBeanModel>() {
                    @Override
                    public void onSuccess(BaseBeanModel data) {
                        U.showToast("回复成功");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MessageInfoActivity.this);
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

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        if (event.getTag() == ToUIEvent.MESSAGE_EVENT) {
            getMessageInfo(true);
        }
    }

}
