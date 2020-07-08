package com.zhuangxiuweibao.app.ui.activity.household;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseBeanModel;
import com.zhuangxiuweibao.app.bean.BaseModel;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.BitmapUtils;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.ui.activity.BaseActivity;

import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.OnClick;

//紧急求助 消息详情
public class EmergencyHelpDetailsActivity extends BaseActivity {


    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;
    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvPersion)
    TextView tvPersion;
    @BindView(R.id.tv_phone_num)
    TextView tvPhoneNum;
    @BindView(R.id.tvData)
    TextView tvData;
    @BindView(R.id.tvReason)
    TextView tvReason;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tvOrderId)
    TextView tvOrderId;
    private String type, keyword = "";
    private MessageEntity msgEntity, shequDetail;
    private String eventId;
    private MessageEntity messageEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_emergency_help_details;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (U.isNotEmpty(msgEntity)) {
            eventId = msgEntity.getEventId();
            UserManager.getInstance().isReads(eventId);//处理为已读消息
            tvSubmit.setVisibility(msgEntity.getAddUserName()
                    .equals(UserManager.getInstance().userData.getName())
                    ? View.GONE : View.VISIBLE);// 当前用户不显示
            switch (msgEntity.getType()) {
                case "2":// 社区通知

                    break;
                case "6":// 社区交流
                case "7":// 大妈审核社区交流

                    break;
                case "4"://紧急求助
                    tvTitle.setText(R.string.postHelp);
                    HttpManager.getInstance().emergencyHelp("EmergencyHelpDetailsActivity",
                            eventId, new HttpCallBack<BaseModel<MessageEntity>>() {
                                @Override
                                public void onSuccess(BaseModel<MessageEntity> data) {
                                    messageEntity = data.getData();
                                    if (messageEntity != null) {
                                        setData(messageEntity);
                                    }
                                }

                                @Override
                                public void onFail(int statusCode, String errorMsg) {
                                    if (statusCode == 1003) {//异地登录
                                        U.showToast("该账户在异地登录!");
                                        HttpManager.getInstance().dologout(EmergencyHelpDetailsActivity.this);
                                    }
                                }

                                @Override
                                public void onError(Throwable throwable) {

                                }
                            });
                    break;
            }
        }
    }

    private void setData(MessageEntity messageEntity) {
        String name = (String) U.getPreferences("name", "");
        if (name.equals(messageEntity.getAddUserName())) {//证明是自己发布的
            tvSubmit.setVisibility(View.GONE);
        }

        tvText.setText(msgEntity.getTitle());
        tvName.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.icon_sos, 0, 0, 0);
        tvName.setText("紧急求助SOS");
        String time = DateUtils.getDateToString(
                Long.parseLong(messageEntity.getTime()), "yyyy/MM/dd HH:mm");
        tvData.setText(time);
        tvTime.setText(time);
        tvPersion.setText(messageEntity.getAddUserName());
        tvPhoneNum.setText(messageEntity.getAddMobile());
        tvReason.setText(messageEntity.getReason());
        tvAddress.setText(messageEntity.getAddress());
        tvOrderId.setText(String.format("紧急求助%s", messageEntity.getTime()));
        if (messageEntity.getStatus().equals("0")) {
            tvSubmit.setText("回执");
            type = "0";
        } else {
            type = "1";
            tvSubmit.setText("回复");
        }
    }

    @OnClick({R.id.rl_back_button, R.id.tv_submit, R.id.tvOrderId, R.id.tv_phone_num})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_submit:
                if (tvSubmit.getText().equals("回执"))
                    replyto();
                else {
//                    回复
                    getReply();
                }
                break;
            case R.id.tvOrderId:
                startActivity(new Intent(this, PostDetailsHelpActivity.class)
                        .putExtra("entity", messageEntity));
                break;
            case R.id.tv_phone_num:
                String stuPhone = messageEntity.getAddMobile();
                if (U.isNotEmpty(stuPhone)) {
                    Dialog dialog = new AlertDialog.Builder(EmergencyHelpDetailsActivity.this)
                            .setTitle("确定拨打?")
                            .setNegativeButton("确定", (dialog12, which) -> {
                                dialog12.dismiss();
                                BitmapUtils.getInstance().getCallPhone(EmergencyHelpDetailsActivity.this, stuPhone);
                            })
                            .setPositiveButton("取消", (dialog1, which) -> dialog1.dismiss())
                            .create();
                    dialog.show();
                }
                break;
        }
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
        HttpManager.getInstance().sosResmsg("ApprovalDetailsActivity", eventId, string, new HttpCallBack<BaseBeanModel>() {
            @Override
            public void onSuccess(BaseBeanModel data) {
                U.showToast("成功");
                finish();
            }

            @Override
            public void onFail(int statusCode, String errorMsg) {
                if (statusCode == 1003) {//异地登录
                    U.showToast("该账户在异地登录!");
                    HttpManager.getInstance().dologout(EmergencyHelpDetailsActivity.this);
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    //42 回执（完成）
    private void replyto() {
        HttpManager.getInstance().replyto("ApprovalDetailsActivity", eventId, new HttpCallBack<BaseBeanModel>() {
            @Override
            public void onSuccess(BaseBeanModel data) {
                U.showToast("成功");
                finish();
            }

            @Override
            public void onFail(int statusCode, String errorMsg) {
                if (statusCode == 1003) {//异地登录
                    U.showToast("该账户在异地登录!");
                    HttpManager.getInstance().dologout(EmergencyHelpDetailsActivity.this);
                    return;
                }
                U.showToast(errorMsg + "");
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

}
