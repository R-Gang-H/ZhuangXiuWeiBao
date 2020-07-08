package com.zhuangxiuweibao.app.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseBeanModel;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.ToUIEvent;
import com.zhuangxiuweibao.app.common.user.UploadModule;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.BitmapUtils;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.FullyGridLayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.ItemOffsetDecoration;
import com.zhuangxiuweibao.app.ui.adapter.GridImageAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.Calendar;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 维保派单处理   工作人员维保订单详情
 */
public class OrderHandlerActivity extends BaseActivity implements GridImageAdapter.OnItemClickListener {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_repairs_name)
    TextView tvRepairsName;
    @BindView(R.id.tv_mobile)
    TextView tvMobile;
    @BindView(R.id.tv_work_time)
    TextView tvWorkTime;
    @BindView(R.id.tv_house_loc)
    TextView tvHouseLoc;
    @BindView(R.id.tv_service_loc)
    TextView tvServiceLoc;
    @BindView(R.id.tv_desc_content)
    TextView tvDescContent;
    @BindView(R.id.tv_order)
    TextView tvOrder;
    @BindView(R.id.ll_foot)
    LinearLayout footView;
    @BindView(R.id.tv_ser_complete)
    TextView tvSerComplete;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static final int RESULT_1 = 1;//转给

    private GridImageAdapter adapter;
    private MessageEntity msgEntity, orderEntity, orderData, orderStatus2;
    private String eventId, orderId;
    //    private String addisMaketure, addisMaketure1,
    private String addisForward, addisForward1, appointmentTime, appointmentTime1;

    @Override
    public int getLayoutId() {
        return R.layout.activity_order_handler;
    }

    @Override
    public void initView() {
        tvTitle.setText("消息详情");
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
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3,
                GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, null);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(5, 5));
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
                            HttpManager.getInstance().dologout(OrderHandlerActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("OrderDetailActivity", throwable.getMessage());
                    }
                });
    }

    private void setData(MessageEntity data) {
        tvText.setText(String.format("维保派单:\t\t%s-%s"
                , data.getXiaoquName(), data.getWorkerName()));
        String eventTime = data.getEventTime();
        tvTime.setText(DateUtils.getDateToString(
                Long.valueOf(U.isNotEmpty(eventTime) ? eventTime
                        : data.getAppointmentTime()), "yy/MM/dd HH:mm"));
        tvRepairsName.setText(data.getAddUserName());
        tvMobile.setText(data.getAddMobile());
        tvWorkTime.setText(DateUtils.getDateToString(
                Long.valueOf(data.getAddTime()), "MM/dd HH:mm"));
        tvHouseLoc.setText(data.getHouseName());
        tvServiceLoc.setText(data.getZoneName());
        tvDescContent.setText(U.getEmoji(data.getContent()));
        tvOrder.setText(String.format("维保订单%s", data.getOrderNum()));
        String[] images = data.getImages().split("#");
        if (images.length > 0 && U.isNotEmpty(images[0])) {
            adapter.pngOravis = Arrays.asList(images);
            adapter.uploadModules.clear();
            for (String image : images) {
                adapter.uploadModules.add(UploadModule.builder().picPath(image).pictureType("jpg").build());
            }
            adapter.setSelectMax(images.length);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(this);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
//        addisMaketure = data.getAddisMaketure();//住户是否确认了上门时间 1已经确认 2没有确认
//        addisMaketure1 = data.getAddisMaketure1();//住户是否确认了上门时间 1已经确认 2没有确认
        addisForward = data.getAddisForward();//判断是否转给同事  1已操作  2未操作
        addisForward1 = data.getAddisForward1();//判断是否转给同事  1已操作  2未操作
        appointmentTime = data.getAppointmentTime();// 是否预约上门时间 0没有预约，其他预约
        appointmentTime1 = data.getAppointmentTime1();// 是否预约上门时间 0没有预约，其他预约
        orderStatus2 = data;
        setMsgData(data);
    }

    //主页跳转过来的维保详情
    private void setMsgData(MessageEntity data) {
        switch (msgEntity.getType2()) {
            // 住户↓↓↓
            case "1":// 发起需求(待审核) 这时候还没有订单详情，只能给提示
            case "2":// 审核不通过
            case "3":// 审核通过  提示发送成功
            case "6":// 已为您安排服务人员
                break;
            case "10":// 完成服务  详细消息，外链：外链按钮问题已解决，问题未解决  住户
                // 工作人员↓↓↓
            case "4":// 已安排服务人员   有外链，显示详情，外链按钮：转给同事  预约上门时间
            case "5":// 分配或选择转给维保人员  工作人员
                statusCharge(data.getStatus());
                break;
            case "7":// 住户选择取消服务
            case "11":// 住户点击问题已解决  解决之|后就不用再对维保进行操作了  工作人员
                break;
            case "8":// 住户选择修改上门时间
            case "9":// 住户选择确认上门时间  工作人员
                tvSerComplete.setVisibility(View.VISIBLE);
                statusCharge(data.getStatus());
                break;
        }
    }

    private void statusCharge(String status) {
        switch (status) {
            /*
             * 维保订单当前所处的状态 1：待审核 2：审核 3：审核不通过
             * 4.维保人员还没有设置服务时间 5.已分配维保人员
             * 6：用户已经确认了服务时间 7：服务完成8：已取消9：售后10：评价*/
            case "4"://4：设置服务时间 转给同事，预约上门时间
            case "5"://5：已分配维保人员 addisMaketure.equals("2") ||
                //addisMaketure1.equals("2") ||
                footView.setVisibility((orderStatus2.getIsOrder().equals("0") ?
                        addisForward.equals("1") || !appointmentTime.equals("0") ? View.GONE : View.VISIBLE :
                        addisForward1.equals("1") || !appointmentTime1.equals("0") ? View.GONE : View.VISIBLE));
                tvSerComplete.setVisibility(View.GONE);
                break;
            case "6"://6：用户已经确认了服务时间
                tvSerComplete.setVisibility(View.VISIBLE);
                break;
            case "7":// 7：服务完成
            case "10"://评价
                footView.setVisibility(View.GONE);
                tvSerComplete.setVisibility(View.GONE);
                break;
            case "9"://售后
                statusCharge(orderStatus2.getStatus2());
                break;
        }
    }

    @OnClick({R.id.rl_back_button, R.id.tv_order, R.id.tv_turn, R.id.tv_make, R.id.tv_ser_complete, R.id.tv_mobile, R.id.tv_house_loc, R.id.tv_service_loc})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_order:
                startActivity(new Intent(this, OrderDetailActivity.class)
                        .putExtra("msgEntity", msgEntity));
                break;
            case R.id.tv_turn:
                // 38 转给同事（完成）
                startActivityForResult(new Intent(this, SelectPersonActivity.class)
                        .putExtra("type", "1"), RESULT_1);
                break;
            case R.id.tv_make:
                getUpdateTime();
                break;
            case R.id.tv_ser_complete:
                weiDone();
                break;
            case R.id.tv_mobile://弹出窗框：拨打 取消
                String stuPhone = orderData.getAddMobile();
                if (U.isNotEmpty(stuPhone)) {
                    Dialog dialog = new AlertDialog.Builder(OrderHandlerActivity.this)
                            .setTitle("确定拨打?")
                            .setNegativeButton("确定", (dialog12, which) -> {
                                dialog12.dismiss();
                                BitmapUtils.getInstance().getCallPhone(OrderHandlerActivity.this, stuPhone);
                            })
                            .setPositiveButton("取消", (dialog1, which) -> dialog1.dismiss())
                            .create();
                    dialog.show();
                }
                break;
            case R.id.tv_house_loc://住户地址：跳转住户房间详情页
                startActivity(new Intent(this, MyHouseActivity.class)
                        .putExtra("houseId", orderData.getHouseId()));
                break;
            case R.id.tv_service_loc://维修地址：跳转保修房间设施
                startActivity(new Intent(this, HouseDetailActivity.class)
                        .putExtra("orderData", orderData));
                break;
        }
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
        }
    }

    //38 转给同事
    private void shareToParter(String pid) {
        HttpManager.getInstance().shareToParter("OrderHandlerActivity", eventId, pid, new HttpCallBack<BaseBeanModel>() {
            @Override
            public void onSuccess(BaseBeanModel data) {
                EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                finish();
            }

            @Override
            public void onFail(int statusCode, String errorMsg) {
                LogUtils.d("OrderHandlerActivity", statusCode + ":" + errorMsg);
                if (statusCode == 1003) {//异地登录
                    U.showToast("该账户在异地登录!");
                    HttpManager.getInstance().dologout(OrderHandlerActivity.this);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtils.e("OrderHandlerActivity", throwable.getMessage());
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

    // 46、维保人员点击服务完成（完成）
    private void weiDone() {
        HttpManager.getInstance().doWeiDone("OrderHandlerActivity", eventId, orderId,
                new HttpCallBack<BaseDataModel<MessageEntity>>(this) {

                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("OrderHandlerActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(OrderHandlerActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("OrderHandlerActivity", throwable.getMessage());
                    }
                });
    }

    private void AppointTime(String orderTime) {
        // 44、维保服务预约上门时间（完成）
        HttpManager.getInstance().doAppointTime("OrderHandlerActivity",
                eventId, orderId, orderTime, new HttpCallBack<BaseDataModel<MessageEntity>>(this) {

                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("OrderHandlerActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(OrderHandlerActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("OrderHandlerActivity", throwable.getMessage());
                    }
                });
    }

    @Override
    public void onItemClick(int position, View v) {
        startActivity(new Intent(this, ImgDetailsActivity.class)
                .putExtra("orderData", orderData)
                .putExtra("index", position));
    }


    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        if (event.getTag() == ToUIEvent.MESSAGE_EVENT) {
            getWeiOrder();
        }
    }
}
