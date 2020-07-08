package com.zhuangxiuweibao.app.ui.activity;


import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.DisclosureEntity;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.ToUIEvent;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;
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
 * 消息详情_维保服务
 */
public class MaintenanceDetailActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.tv_worker)
    TextView tvWorker;
    @BindView(R.id.tv_mobile)
    TextView tvMobile;
    @BindView(R.id.tv_work_time)
    TextView tvWorkTime;
    @BindView(R.id.tv_order_id)
    TextView tvOrderId;
    @BindView(R.id.tvContent)
    TextView tvContent;
    @BindView(R.id.footView)
    LinearLayout footView;
    @BindView(R.id.rl_work_info)
    RelativeLayout rlWorkInfo;
    @BindView(R.id.contentView)
    LinearLayout contentView;
    @BindView(R.id.FeaturesView)
    LinearLayout FeaturesView;
    @BindView(R.id.btnLeft)
    TextView btnLeft;
    @BindView(R.id.btnRight)
    TextView btnRight;
    @BindView(R.id.btnAfterSaleOrder)
    TextView btnAfterSaleOrder;
    @BindView(R.id.rel_root)
    RelativeLayout mrelRoot;
    private MessageEntity orderData, msgEntity, orderStatus2;
    private List<ImgBean> star1 = new ArrayList<>();
    private List<ImgBean> star2 = new ArrayList<>();
    private List<ImgBean> star3 = new ArrayList<>();
    private String eventId, orderId, skill, service, priceEvaluate, comment, reason;

    private String addisMaketure, addisMaketure1;
    private String addisDone, addisDone1;
    private String addisPingjia, addisPingjia1;
    private MyPopAfterSale mSale;
    private List<ReasonBean> reasons = new ArrayList<>();
    private MyPopEvaluation mPop;


    @Override
    public int getLayoutId() {
        return R.layout.activity_maintenance_detail;
    }

    @Override
    public void initView() {
        tvTitle.setText("消息详情");
    }

    @Override
    public void initData() {
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        orderData = (MessageEntity) getIntent().getSerializableExtra("orderData");
        if (U.isNotEmpty(msgEntity)) {//entity不为空就证明是主页跳转过来的维保详情
            eventId = msgEntity.getEventId();
            UserManager.getInstance().isReads(eventId);//处理为已读消息
        } else if (U.isNotEmpty(orderData)) {
            orderId = orderData.getOrderId();
        }
        getWeiOrder();
    }

    //主页跳转过来的维保详情
    private void setMsgData(MessageEntity data) {
        tvText.setText(msgEntity.getTitle());//
        tvContent.setText(msgEntity.getContent());
        switch (msgEntity.getType2()) {
            // 住户↓↓↓
            case "1":// 发起需求(待审核) 这时候还没有订单详情，只能给提示
            case "2":// 审核不通过
            case "3":// 审核通过  提示发送成功
                //工作人员↓↓↓
            case "4":// 已安排服务人员   有外链，显示详情，外链按钮：转给同事  预约上门时间
            case "5":// 分配或选择转给维保人员  工作人员
            case "7":// 住户选择取消服务
            case "8":// 住户选择修改上门时间
            case "9":// 住户选择确认上门时间  工作人员
            case "11":// 住户点击问题已解决  解决之后就不用再对维保进行操作了  工作人员
                contentView.setVisibility(View.VISIBLE);
                break;
            //住户↓↓↓
            case "6"://已为您安排服务人员
                //住户是否确认了上门时间 1已经确认 2没有确认
                rlWorkInfo.setVisibility(View.VISIBLE);
                tvOrderId.setVisibility(View.VISIBLE);
                footView.setVisibility(data.getIsOrder().equals("0") ?
                        addisMaketure.equals("2") ? View.VISIBLE : View.GONE :
                        addisMaketure1.equals("2") ? View.VISIBLE : View.GONE);//取消服务，修改上门时间，确认
                break;
            case "10":// 完成服务  详细消息，外链：外链按钮问题已解决，问题未解决  住户
                statusCharge(data.getStatus());
                break;
        }

    }

    private void statusCharge(String status) {
        rlWorkInfo.setVisibility(View.VISIBLE);
        tvOrderId.setVisibility(View.VISIBLE);
        switch (status) {//llSolve 问题是否解决 llBtn修改上门时间   btnAfterSaleOrder售后
            /*
             * 维保订单当前所处的状态 1：待审核 2：审核 3：审核不通过
             * 4.维保人员还没有设置服务时间 5.已分配维保人员
             * 6：用户已经确认了服务时间 7：服务完成8：已取消9：售后10：评价*/
            case "7":
                FeaturesView.setVisibility(View.VISIBLE);
                switch (TextUtils.equals(orderStatus2.getStatus2(), "0") ? addisDone : addisDone1) {
                    //本次服务是否解决了问题 0住户还没有操作 1住户点击了【问题已解决】 2住户点击了【问题未解决】
                    case "0":
                        btnLeft.setText("问题未解决");
                        btnRight.setText("问题已解决");
                        break;
                    case "1"://问题已解决
                        // status2：0 维保订单显示申请售后，其他值是售后订单不显示审批售后
                        btnLeft.setVisibility(TextUtils.equals(orderStatus2.getStatus2(), "0") ? View.VISIBLE : View.GONE);
                        btnLeft.setText("申请售后");
                        btnRight.setText("去评价");
                        btnRight.setBackgroundResource(TextUtils.equals(orderStatus2.getStatus2(), "0")
                                ? R.mipmap.bg_bt_left : R.mipmap.ic_login_btn_bg);
                        break;
                    case "2"://问题未解决
                        FeaturesView.setVisibility(View.GONE);
                        btnAfterSaleOrder.setVisibility(View.VISIBLE);
                        btnAfterSaleOrder.setText("申请售后");
                        // status2：0 维保订单显示申请售后，其他值是售后订单不显示审批售后
                        btnAfterSaleOrder.setVisibility(TextUtils.equals(orderStatus2.getStatus2(), "0") ? View.VISIBLE : View.GONE);
                        break;
                }
                break;
            case "9"://售后
                btnAfterSaleOrder.setVisibility(View.GONE);
                btnAfterSaleOrder.setText("查看售后详情");
                statusCharge(orderStatus2.getStatus2());
                break;
            case "10"://评价
                // 住户是否评价了本次服务 1已经评价 2没有评价
                btnAfterSaleOrder.setVisibility((orderStatus2.getStatus2().equals("0") ?
                        addisPingjia : addisPingjia1).equals("2") ? View.VISIBLE : View.GONE);
                btnAfterSaleOrder.setText("去评价");
                break;
        }
    }

    private void getWeiOrder() {
        HttpManager.getInstance().doGetWeiOrder("OrderDetailActivity",
                eventId, orderId, new HttpCallBack<BaseDataModel<MessageEntity>>(this) {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        setData(data.getData().get(0));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("OrderDetailActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MaintenanceDetailActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("OrderDetailActivity", throwable.getMessage());
                    }
                });
    }

    private void setData(MessageEntity data) {
        tvTime.setText(DateUtils.getDateToString(
                Long.valueOf(data.getAddTime()), "yy/MM/dd HH:mm"));
        tvWorker.setText(data.getWorkerName());
        tvMobile.setText(data.getWorkerMobile());
        String appointTime = data.getAppointmentTime();
        tvWorkTime.setText(U.isEmpty(appointTime) || appointTime.equals("0") ? "未设置"
                : DateUtils.getDateToString(Long.valueOf(appointTime), "yy/MM/dd HH:mm:ss"));
        tvOrderId.setText(String.format("维保订单%s", data.getOrderNum()));
        addisMaketure = data.getAddisMaketure();//住户是否确认了上门时间 1已经确认 2没有确认
        addisMaketure1 = data.getAddisMaketure1();//住户是否确认了上门时间 1已经确认 2没有确认
        addisDone = data.getAddisDone();
        addisDone1 = data.getAddisDone1();
        addisPingjia = data.getAddisPingjia();
        addisPingjia1 = data.getAddisPingjia1();
        orderStatus2 = data;
        setMsgData(data);
    }

    @OnClick({R.id.rl_back_button, R.id.tv_order_id, R.id.tv_cancel, R.id.tv_change_time, R.id.tv_confirm, R.id.btnRight, R.id.btnLeft, R.id.btnAfterSaleOrder})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_order_id:
                startActivity(new Intent(this, OrderDetailActivity.class)
                        .putExtra("msgEntity", msgEntity));
                break;
            case R.id.tv_cancel:
                cancelService();
                break;
            case R.id.tv_change_time:
                getUpdateTime();
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
                        evaluation();
                        break;
                }
                break;
        }
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
        HttpManager.getInstance().doOrderEvaluate("OrderDetailActivity", eventId, orderId, skill,
                service, priceEvaluate, comment, new HttpCallBack<BaseDataModel<BaseDataModel<MessageEntity>>>() {
                    @Override
                    public void onSuccess(BaseDataModel<BaseDataModel<MessageEntity>> data) {
                        dialogResult();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("OrderDetailActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MaintenanceDetailActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("OrderDetailActivity", throwable.getMessage());
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
            finish();
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
        HttpManager.getInstance().doAskAfter("OrderDetailActivity", eventId, orderId, reason,
                new HttpCallBack<BaseDataModel<MessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("OrderDetailActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MaintenanceDetailActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("OrderDetailActivity", throwable.getMessage());
                    }
                });
    }

    private void weiConfirm(String type) {//处理问题解决或者未解决
        HttpManager.getInstance().doWeiConfirm("OrderDetailActivity",
                eventId, orderId, type, new HttpCallBack<BaseDataModel<MessageEntity>>(this) {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        U.showToast("成功");
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("OrderDetailActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MaintenanceDetailActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("OrderDetailActivity", throwable.getMessage());
                    }
                });
    }

    //取消服务
    private void cancelService() {
        HttpManager.getInstance().cancelService("MaintenanceDetailActivity", orderId, new HttpCallBack<BaseDataModel<MessageEntity>>() {
            @Override
            public void onSuccess(BaseDataModel<MessageEntity> data) {
                EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                finish();
            }

            @Override
            public void onFail(int statusCode, String errorMsg) {
                if (statusCode == 1003) {//异地登录
                    U.showToast("该账户在异地登录!");
                    HttpManager.getInstance().dologout(MaintenanceDetailActivity.this);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtils.e("MaintenanceDetailActivity", throwable.getMessage());
            }
        });
    }

    private void confirmTime() {
        HttpManager.getInstance().doConfirmTime("MaintenanceDetailActivity",
                eventId, orderId, new HttpCallBack<BaseDataModel<MessageEntity>>(this) {

                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("MaintenanceDetailActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MaintenanceDetailActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("MaintenanceDetailActivity", throwable.getMessage());
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
        HttpManager.getInstance().doAppointTime("OrderDetailActivity",
                eventId, orderId, orderTime, new HttpCallBack<BaseDataModel<MessageEntity>>(this) {

                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("OrderDetailActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MaintenanceDetailActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("OrderDetailActivity", throwable.getMessage());
                    }
                });
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        if (event.getTag() == ToUIEvent.MESSAGE_EVENT) {
            getWeiOrder();
        }
    }
}
