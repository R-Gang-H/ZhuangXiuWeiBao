package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.ApprovalEntity;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.bean.UserEntity;
import com.zhuangxiuweibao.app.common.audio.MediaManager;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.ToUIEvent;
import com.zhuangxiuweibao.app.common.user.UploadModule;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.ResourcesUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.FullyGridLayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.ItemOffsetDecoration;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.ui.adapter.ApprovalLineAdapter;
import com.zhuangxiuweibao.app.ui.adapter.GridImageAdapter;
import com.zhuangxiuweibao.app.ui.bean.ImgBean;
import com.zhuangxiuweibao.app.ui.bean.ReasonBean;
import com.zhuangxiuweibao.app.ui.widget.MyPopEvaluation;
import com.zhuangxiuweibao.app.ui.widget.mypopaftersale.MyPopAfterSale;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 维保订单详情
 */
public class OrderDetailActivity extends BaseActivity implements GridImageAdapter.OnItemClickListener {

    @BindView(R.id.tv_title)
    TextView mtvTitle;
    @BindView(R.id.tv_repair)
    TextView mtvRepair;
    @BindView(R.id.tv_mobile)
    TextView mtvMobile;
    @BindView(R.id.tv_repair_time)
    TextView mtvRepairTime;
    @BindView(R.id.tv_address)
    TextView mtvAddress;
    @BindView(R.id.tv_loc)
    TextView mtvLoc;
    @BindView(R.id.tv_des)
    TextView mtvDes;
    @BindView(R.id.fr_voice)
    FrameLayout frVoice;
    @BindView(R.id.iv_voice)
    ImageView ivVoice;
    @BindView(R.id.iv_audio)
    ImageView ivAudio;
    @BindView(R.id.xre_grid)
    RecyclerView mxreGrid;
    @BindView(R.id.tv_repair_person)
    TextView mtvRepairPerson;
    @BindView(R.id.tv_mobile1)
    TextView mtvMobile1;
    @BindView(R.id.tv_repair_time1)
    TextView mtvRepairTime1;
    @BindView(R.id.tv_repair_person1)
    TextView mtvRepairPerson1;
    @BindView(R.id.tv_mobile11)
    TextView mtvMobile11;
    @BindView(R.id.tv_repair_time11)
    TextView mtvRepairTime11;
    @BindView(R.id.tv_charge)
    TextView mtvCharge;
    @BindView(R.id.tv_pay_way)
    TextView mtvPayWay;
    @BindView(R.id.tv_pay_time)
    TextView mtvPayTime;
    @BindView(R.id.xre_timeline)
    RecyclerView mxreTimeline;
    @BindView(R.id.rel_root)
    RelativeLayout mrelRoot;
    @BindView(R.id.lin_menu)
    LinearLayout linMenu;
    @BindView(R.id.rl_btn)
    LinearLayout llBtn;
    @BindView(R.id.btnAfterSaleOrder)
    TextView btnAfterSaleOrder;
    @BindView(R.id.tv_cancel_ser)
    TextView tv_cancel_ser;
    @BindView(R.id.tv_update_time)
    TextView tv_update_time;
    @BindView(R.id.FeaturesView)
    LinearLayout FeaturesView;
    @BindView(R.id.btnLeft)
    TextView btnLeft;
    @BindView(R.id.btnRight)
    TextView btnRight;
    @BindView(R.id.ll_repair_person1)
    LinearLayout llRepairPerson1;
    @BindView(R.id.ll_mobile1)
    LinearLayout llMobile1;
    @BindView(R.id.ll_repair_time11)
    LinearLayout llRepairTime11;

    private GridImageAdapter adapter;
    private List<ApprovalEntity> lines = new ArrayList<>(), lines2 = new ArrayList<>();
    private ApprovalLineAdapter lineAdapter;

    private MyPopEvaluation mPop;
    private MyPopAfterSale mSale;

    private List<ImgBean> star1 = new ArrayList<>();
    private List<ImgBean> star2 = new ArrayList<>();
    private List<ImgBean> star3 = new ArrayList<>();
    private List<ReasonBean> reasons = new ArrayList<>();
    private MessageEntity msgEntity, orderEntity, orderData, orderStatus2;
    private String eventId, orderId, skill, service, priceEvaluate, comment, reason;
    private String addisMaketure, addisMaketure1, appointmentTime, appointmentTime1;
    private String filePath;
    private String addisDone, addisDone1;
    private String addisPingjia, addisPingjia1;

    @Override
    public int getLayoutId() {
        return R.layout.activity_order_detail;
    }

    @Override
    public void initView() {
        mtvTitle.setText("订单详情");//维保详情
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3,
                GridLayoutManager.VERTICAL, false);
        mxreGrid.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, null);
        mxreGrid.addItemDecoration(new ItemOffsetDecoration(5, 5));
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        orderEntity = (MessageEntity) getIntent().getSerializableExtra("orderData");
        if (U.isNotEmpty(msgEntity)) {
            eventId = msgEntity.getEventId();
            UserManager.getInstance().isReads(eventId);//处理为已读消息
        } else if (U.isNotEmpty(orderEntity)) {
            orderId = orderEntity.getOrderId();
        }
        String firstIdentity = (String) U.getPreferences("firstIdentity", "");
        if (firstIdentity.equals("2")) {//如果是工作人员端登录的话详情页就不显示按钮
            linMenu.setVisibility(View.GONE);
        }
        getWeiOrder();
    }

    private void getWeiOrder() {
        HttpManager.getInstance().doGetWeiOrder("OrderDetailActivity",
                eventId, orderId, new HttpCallBack<BaseDataModel<MessageEntity>>(this) {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        orderData = data.getData().get(0);
                        setData(orderData);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("OrderDetailActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(OrderDetailActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("OrderDetailActivity", throwable.getMessage());
                    }
                });
    }

    private void setData(MessageEntity data) {
        mtvRepair.setText(data.getAddUserName());
        mtvMobile.setText(data.getAddMobile());
        mtvRepairTime.setText(DateUtils.getDateToString(
                Long.valueOf(data.getAddTime()), "MM/dd HH:mm"));
        mtvAddress.setText(data.getHouseName());
        mtvLoc.setText(data.getZoneName());
        mtvDes.setText(U.getEmoji(data.getContent()));
        filePath = U.parseEmpty(data.getYuyin());
        frVoice.setVisibility(U.isNotEmpty(filePath) ? View.VISIBLE : View.GONE);

        String[] images = data.getImages().split("#");
        if (images.length > 0 && U.isNotEmpty(images[0])) {
            adapter.pngOravis = Arrays.asList(images);
            for (String image : images) {
                adapter.uploadModules.add(UploadModule.builder().picPath(image).pictureType("jpg").build());
            }
            adapter.setSelectMax(images.length);
            mxreGrid.setAdapter(adapter);
        }
        // 维保人员信息
        appointmentTime = data.getAppointmentTime();// 是否预约上门时间 0没有预约，其他预约
        mtvRepairPerson.setText(getIsEmtity(data.getWorkerName()));
        mtvMobile1.setText(getIsEmtity(data.getWorkerMobile()));
        mtvRepairTime1.setText(U.isEmpty(appointmentTime) || appointmentTime.equals("0") ? "未设置"
                : DateUtils.getDateToString(Long.parseLong(appointmentTime), "yy/MM/dd HH:mm:ss"));
        // 售后人员信息
        llRepairPerson1.setVisibility(U.isNotEmpty(data.getWorkerMobile1()) ? View.VISIBLE : View.GONE);
        llMobile1.setVisibility(U.isNotEmpty(data.getWorkerMobile1()) ? View.VISIBLE : View.GONE);
        llRepairTime11.setVisibility(U.isNotEmpty(data.getWorkerMobile1()) ? View.VISIBLE : View.GONE);
        appointmentTime1 = data.getAppointmentTime1();// 是否预约上门时间 0没有预约，其他预约
        mtvRepairPerson1.setText(getIsEmtity(data.getWorkerName1()));
        mtvMobile11.setText(getIsEmtity(data.getWorkerMobile1()));
        mtvRepairTime11.setText(U.isEmpty(appointmentTime1) || appointmentTime1.equals("0") ? "未设置"
                : DateUtils.getDateToString(Long.parseLong(appointmentTime1), "yy/MM/dd HH:mm:ss"));

        //住户是否确认了上门时间 1已经确认 2没有确认
        addisMaketure = data.getAddisMaketure();
        addisMaketure1 = data.getAddisMaketure1();
        addisDone = data.getAddisDone();
        addisDone1 = data.getAddisDone1();
        addisPingjia = data.getAddisPingjia();
        addisPingjia1 = data.getAddisPingjia1();
        orderStatus2 = data;

        /**按钮显示
         *   住户版首页进入订单  显示取消服务，修改上门时间 根据msgEntity的type值进行判断
         *   住户版维保订单进入详情页时 需要显示取消服务，修改上门时间，申请售后，去评价，查看售后详情按钮 根据接口返回的status值进行判断*/
        if (U.isNotEmpty(msgEntity)) {
            setMsgData(msgEntity, data);
        } else {//这里证明是维保订单
            statusCharge(data.getStatus());//根据status判断
        }

        initTimeLineList(data);
    }

    //主页跳转过来的维保详情
    private void setMsgData(MessageEntity msgEntity, MessageEntity status) {
        switch (msgEntity.getType2()) {
            // 住户
            case "1":// 发起需求(待审核) 这时候还没有订单详情，只能给提示
            case "2":// 审核不通过
            case "3":// 审核通过  提示发送成功
            case "4":// 已安排服务人员   有外链，显示详情，外链按钮：转给同事  预约上门时间
                //工作人员
            case "5":// 分配或选择转给维保人员  工作人员
            case "10":// 完成服务  详细消息，外链：外链按钮问题已解决，问题未解决  住户
                statusCharge(status.getStatus());
                break;
            case "6"://已为您安排服务人员
                //取消服务，修改上门时间，确认
                llBtn.setVisibility(status.getIsOrder().equals("0") ?
                        addisMaketure.equals("2") ? View.VISIBLE : View.GONE :
                        addisMaketure1.equals("2") ? View.VISIBLE : View.GONE);//住户是否确认了上门时间 1已经确认 2没有确认
                break;
            case "7":// 住户选择取消服务
                break;
            case "8":// 住户选择修改上门时间
            case "9":// 住户选择确认上门时间  工作人员
                linMenu.setVisibility(View.VISIBLE);
                btnAfterSaleOrder.setVisibility(View.VISIBLE);
                btnAfterSaleOrder.setText("服务完成");
                statusCharge(status.getStatus());
                break;
            case "11":// 住户点击问题已解决  解决之后就不用再对维保进行操作了  工作人员
                break;
        }
    }

    private void statusCharge(String status) {
        switch (status) {//llSolve 问题是否解决 llBtn修改上门时间   btnAfterSaleOrder售后
            /*
             * 维保订单当前所处的状态 1：待审核 2：审核 3：审核不通过
             * 4.维保人员还没有设置服务时间 5.已分配维保人员
             * 6：用户已经确认了服务时间 7：服务完成8：已取消9：售后10：评价*/
            case "1"://1：发起需求   无按钮
            case "2"://2：审核   无按钮
            case "3":
            case "4"://维保人员还没有设置服务时间
                linMenu.setVisibility(View.GONE);
                break;
            case "5"://已分派服务人员 addisMaketure.equals("2") ||
                //住户是否确认了上门时间 1已经确认 2没有确认 addisMaketure1.equals("2") ||
                llBtn.setVisibility(orderStatus2.getIsOrder().equals("0") ?
                        appointmentTime.equals("1") ? View.VISIBLE : View.GONE :
                        appointmentTime1.equals("1") ? View.VISIBLE : View.GONE);
                break;
            case "6":
                FeaturesView.setVisibility(View.GONE);
                linMenu.setVisibility(View.VISIBLE);
                btnAfterSaleOrder.setVisibility(View.VISIBLE);
                btnAfterSaleOrder.setText("服务完成");
                break;
            case "7":
                UserEntity userData = UserManager.getInstance().userData;
                if (userData.getIsNew().equals("2") && userData.getFirstIdentity().equals("2")) {// 2工作人员
                    btnAfterSaleOrder.setVisibility(View.GONE);
                } else {// 住户
                    FeaturesView.setVisibility(View.VISIBLE);
                    switch (orderStatus2.getStatus2().equals("0") ? addisDone : addisDone1) {//本次服务是否解决了问题 0住户还没有操作 1住户点击了【问题已解决】 2住户点击了【问题未解决】
                        case "0":
                            btnLeft.setText("问题未解决");
                            btnRight.setText("问题已解决");
                            break;
                        case "1"://问题已解决
                            // status2：0 维保订单显示申请售后，其他值是售后订单不显示审批售后
                            btnLeft.setVisibility(TextUtils.equals(orderStatus2.getStatus2(), "0") ? View.VISIBLE : View.GONE);
                            btnLeft.setText("申请售后");
                            btnRight.setText("去评价");
                            btnRight.setBackground(ResourcesUtils.getDrawable(TextUtils.equals(orderStatus2.getStatus2(), "0")
                                    ? R.mipmap.bg_bt_left : R.mipmap.ic_login_btn_bg));
                            break;
                        case "2":
                            FeaturesView.setVisibility(View.GONE);
                            btnAfterSaleOrder.setVisibility(View.VISIBLE);
                            btnAfterSaleOrder.setText("申请售后");
                            // status2：0 维保订单显示申请售后，其他值是售后订单不显示审批售后
                            btnAfterSaleOrder.setVisibility(TextUtils.equals(orderStatus2.getStatus2(), "0") ? View.VISIBLE : View.GONE);
                            break;
                    }
                }
                break;
            case "9"://售后
                llBtn.setVisibility(View.GONE);
                FeaturesView.setVisibility(View.GONE);
                btnAfterSaleOrder.setVisibility(View.GONE);
                btnAfterSaleOrder.setText("查看售后详情");
                statusCharge(orderStatus2.getStatus2());
                break;
            case "10"://评价
                switch (orderStatus2.getStatus2().equals("0") ? addisPingjia : addisPingjia1) {
                    case "1":// 住户是否评价了本次服务 1已经评价 2没有评价
                        llBtn.setVisibility(View.GONE);
                        FeaturesView.setVisibility(View.GONE);
                        btnAfterSaleOrder.setVisibility(View.GONE);
                        break;
                    case "2":
                        FeaturesView.setVisibility(View.GONE);
                        llBtn.setVisibility(View.GONE);
                        btnAfterSaleOrder.setVisibility(View.VISIBLE);
                        btnAfterSaleOrder.setText("去评价");
                        break;
                }
                break;
        }
    }

    private String getIsEmtity(String text) {
        return U.isEmpty(text) ? "未设置" : text;
    }


    /**
     * 时间轴
     *
     * @param data
     */
    private void initTimeLineList(MessageEntity data) {
        lineAdapter = new ApprovalLineAdapter(this);
        LayoutManager.getInstance().init(this).initRecyclerView(mxreTimeline, true);
        mxreTimeline.setAdapter(lineAdapter);

        boolean isWeiBao = !TextUtils.equals(orderStatus2.getStatus2(), "0");// status2：0 维保订单，其他值是售后订单

        //客服受理
        TimeLine("客服受理", "客服已受理您的维保需求，正在为您指派维保人员，请您保持手机畅通。", orderData.getSendOrderTime());
        //上门服务
        TimeLine("上门服务", "您已和维保人员确认了上门服务时间，请您耐心等待。", orderData.getAppointmentTime());
        //服务完成
        TimeLine("服务完成", "维保人员已完成服务，请您在48小时内确认服务是否完成。", orderData.getWorkerDoneTime());
        switch (addisDone) {//本次服务是否解决了问题 0住户还没有操作 1住户点击了【问题已解决】 2住户点击了【问题未解决】
            case "0":
                break;
            case "1"://问题已解决
                //住户确认服务完成/问题已解决
                TimeLine("住户确认服务完成", "问题已解决，期待再次为您服务。", orderData.getConfirmAt());
                break;
            case "2":
                //住户确认服务完成/问题未解决
                TimeLine("住户确认服务完成", "问题未解决，请申请售后。", orderData.getConfirmAt());
                break;
        }
        if (!isWeiBao) {
            //用户评价
            TimeLine("用户评价", "客服已收到您的评价，非常感谢。", orderData.getEventTime());
        }

        /*
         * 维保订单当前所处的状态 1：待审核 2：审核 3：审核不通过
         * 4.维保人员还没有设置服务时间 5.已分配维保人员
         * 6：用户已经确认了服务时间 7：服务完成8：已取消9：售后10：评价*/
        switch (orderData.getStatus()) {
            case "1":
            case "3":
            case "8":
                lines.clear();
                break;
            case "2":
            case "4":
            case "5":
                if (!isWeiBao && !addisDone.equals("0")) {
                    lines.remove(4);
                }
                lines.remove(3);
                lines.remove(2);
                lines.remove(1);
                break;
            case "6":
                if (!isWeiBao && !addisDone.equals("0")) {
                    lines.remove(4);
                }
                lines.remove(3);
                lines.remove(2);
                break;
            case "7":
                UserEntity userData = UserManager.getInstance().userData;
                if (!isWeiBao && !addisDone.equals("0")) {
                    lines.remove(4);
                } else {
                    lines.remove(3);
                }
//                if (userData.getFirstIdentity().equals("2")) {// 2工作人员
//                    lines.remove(3);
//                }
                break;
            case "10":
                break;
        }
        lineAdapter.update(lines, true);
        if (isWeiBao) {
            TimeLine2("客服受理", "客服已受理您的售后申请，正在为您指派维保人员，请您保持手机畅通。", orderData.getSendOrderTime1());
            //上门服务
            TimeLine2("上门服务", "您已和维保人员确认了售后上门服务时间，请您耐心等待。", orderData.getAppointmentTime1());
            //服务完成
            TimeLine2("服务完成", "维保人员已完成售后服务，请您在48小时内确认服务是否完成。", orderData.getWorkerDoneTime1());
            switch (addisDone1) {//本次服务是否解决了问题 0住户还没有操作 1住户点击了【问题已解决】 2住户点击了【问题未解决】
                case "0":
                    break;
                case "1"://问题已解决
                    //住户确认服务完成/问题已解决
                    TimeLine2("住户确认服务完成", "问题已解决，期待再次为您服务。", orderData.getConfirmAt1());
                    break;
                case "2":
                    //住户确认服务完成/问题未解决
                    TimeLine2("住户确认服务完成", "问题未解决，请重新发需求。", orderData.getConfirmAt1());
                    break;
            }
            //用户评价
            TimeLine2("用户评价", "客服已收到您的售后评价，非常感谢。", orderData.getEventTime());

            /*
             * 维保订单当前所处的状态 1：待审核 2：审核 3：审核不通过
             * 4.维保人员还没有设置服务时间 5.已分配维保人员
             * 6：用户已经确认了服务时间 7：服务完成8：已取消9：售后10：评价*/
            switch (orderData.getStatus2()) {
                case "1":
                case "3":
                case "8":
                    lines2.clear();
                    break;
                case "2":
                case "4":
                case "5":
                    if (isWeiBao && !addisDone1.equals("0")) {
                        lines2.remove(4);
                    }
                    lines2.remove(3);
                    lines2.remove(2);
                    lines2.remove(1);
                    break;
                case "6":
                    if (isWeiBao && !addisDone1.equals("0")) {
                        lines2.remove(4);
                    }
                    lines2.remove(3);
                    lines2.remove(2);
                    break;
                case "7":
                    UserEntity userData = UserManager.getInstance().userData;
                    if (isWeiBao && !addisDone1.equals("0")) {
                        lines2.remove(4);
                    } else {
                        lines2.remove(3);
                    }
//                    if (userData.getFirstIdentity().equals("2")) {// 2工作人员
//                        lines.remove(3);
//                    }
                    break;
                case "10":
                    break;
            }
            lines.addAll(lines2);
            lineAdapter.update(lines, true);
        }
    }

    private void TimeLine(String n, String s, String time) {
        lines.add(ApprovalEntity.builder()
                .isFinish(true).name(n)
                .status(s)
                .time(time)
                .build());
    }

    private void TimeLine2(String n, String s, String time) {
        lines2.add(ApprovalEntity.builder()
                .isFinish(true).name(n)
                .status(s)
                .time(time)
                .build());
    }

    private void upLoadVoice(String filePath, int seconds) {
        ivAudio.setVisibility(View.GONE);
        MediaManager.playSound(filePath, mp -> {
            // 录音播放完毕
            ivAudio.setVisibility(View.VISIBLE);
        });
    }

    @OnClick({R.id.rl_back_button, R.id.iv_voice, R.id.tv_cancel_ser, R.id.tv_update_time, R.id.btnAfterSaleOrder, R.id.btnLeft, R.id.btnRight, R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.iv_voice:
                upLoadVoice(filePath, 0);
                break;
            case R.id.tv_cancel_ser://取消服务
                cancelService();
                break;
            case R.id.tv_update_time:
                getUpdateTime();//修改上门时间
                break;
            case R.id.tv_confirm://确认上门时间
                confirmTime();
                break;
            case R.id.btnAfterSaleOrder://查看售后详情按钮
                switch (btnAfterSaleOrder.getText().toString()) {
                    case "申请售后":
                        afterSale();
                        break;
                    case "去评价":
                        evaluation();
                        break;
                    case "服务完成":
                        weiDone();
                        break;
                }
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
        }
    }

    // 46、维保人员点击服务完成（完成）
    private void weiDone() {
        HttpManager.getInstance().doWeiDone("OrderDetailActivity", eventId, orderId,
                new HttpCallBack<BaseDataModel<MessageEntity>>(this) {

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
                            HttpManager.getInstance().dologout(OrderDetailActivity.this);
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
        HttpManager.getInstance().cancelService("OrderDetailActivity", orderId, new HttpCallBack<BaseDataModel<MessageEntity>>() {
            @Override
            public void onSuccess(BaseDataModel<MessageEntity> data) {
                EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                finish();
            }

            @Override
            public void onFail(int statusCode, String errorMsg) {
                if (statusCode == 1003) {//异地登录
                    U.showToast("该账户在异地登录!");
                    HttpManager.getInstance().dologout(OrderDetailActivity.this);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtils.e("MaintenanceDetailActivity", throwable.getMessage());
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
                            HttpManager.getInstance().dologout(OrderDetailActivity.this);
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
                            HttpManager.getInstance().dologout(OrderDetailActivity.this);
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
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("OrderDetailActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(OrderDetailActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("OrderDetailActivity", throwable.getMessage());
                    }
                });
    }

    private void getUpdateTime() {//修改时间
        new TimePickerBuilder(this, (date, v) -> {
            AppointTime(String.valueOf(date.getTime() / 1000));
        })
                .setType(new boolean[]{true, true, true, true, true, false})
                .setRangDate(Calendar.getInstance(), null)
                .setLabel("年", "月", "日", "时",
                        "分", "")
                .build().show();
    }

    private void AppointTime(String orderTime) {//修改时间
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
                            HttpManager.getInstance().dologout(OrderDetailActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("OrderDetailActivity", throwable.getMessage());
                    }
                });
    }

    private void confirmTime() {//45、住户确认维保服务的上门时间
        HttpManager.getInstance().doConfirmTime("OrderDetailActivity",
                eventId, orderId, new HttpCallBack<BaseDataModel<MessageEntity>>(this) {

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
                            HttpManager.getInstance().dologout(OrderDetailActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("OrderDetailActivity", throwable.getMessage());
                    }
                });
    }

    @Override
    public void onItemClick(int position, View v) {//图片查看
        startActivity(new Intent(this, ImgDetailsActivity.class)
                .putExtra("orderData", orderData)
                .putExtra("index", position));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MediaManager.release();
        if (U.isNotEmpty(mPop)) {
            mPop.dismiss();
        }
    }
}
