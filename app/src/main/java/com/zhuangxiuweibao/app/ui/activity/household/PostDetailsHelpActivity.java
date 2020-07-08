package com.zhuangxiuweibao.app.ui.activity.household;

import android.view.View;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.ApprovalEntity;
import com.zhuangxiuweibao.app.bean.BaseModel;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.GlideUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.ui.activity.BaseActivity;
import com.zhuangxiuweibao.app.ui.adapter.ApprovalReplyAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 帖子详情，紧急求助
 */
public class PostDetailsHelpActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_avatar)
    RoundedImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tvData)
    TextView tvData;
    @BindView(R.id.tvPeople)
    TextView tvPeople;
    @BindView(R.id.tv_phone_num)
    TextView tvPhoneNum;
    @BindView(R.id.tvReason)
    TextView tvReason;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.replyNum)
    TextView replyNum;
    @BindView(R.id.replyList)
    RecyclerView replyList;
    private MessageEntity msgEntity, messageEntity;
    private List<ApprovalEntity> reps = new ArrayList<>();
    private ApprovalReplyAdapter replyAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_post_details_help;
    }

    @Override
    public void initView() {
        tvTitle.setText("紧急求助");
    }

    @Override
    public void initData() {
        initList();
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (msgEntity != null) {
            //证明是消息列表跳转过来的  需要请求接口拿到回复列表
            HttpManager.getInstance().emergencyHelp("EmergencyHelpDetailsActivity",
                    msgEntity.getEventId(), new HttpCallBack<BaseModel<MessageEntity>>() {
                        @Override
                        public void onSuccess(BaseModel<MessageEntity> data) {
                            messageEntity = data.getData();
                            if (messageEntity != null) {
                                setData();
                            }
                        }

                        @Override
                        public void onFail(int statusCode, String errorMsg) {
                            if (statusCode == 1003) {//异地登录
                                U.showToast("该账户在异地登录!");
                                HttpManager.getInstance().dologout(PostDetailsHelpActivity.this);
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {

                        }
                    });
        } else {
            //从消息详情页面链接跳转
            messageEntity = (MessageEntity) getIntent().getSerializableExtra("entity");
            setData();
        }
    }

    private void setData() {
        GlideUtils.setGlideImg(this, messageEntity.getAddUserIcon(), R.mipmap.icon_default, ivAvatar);
        tvReason.setText(messageEntity.getReason());
        tvPeople.setText(messageEntity.getAddUserName());
        tvPhoneNum.setText(messageEntity.getAddMobile());
        tvName.setText(messageEntity.getAddUserName());
        tvData.setText(DateUtils.getDateToString(Long.valueOf(messageEntity.getTime()), "yyyy/MM/dd HH:mm"));
        tvAddress.setText(messageEntity.getAddress());
        //回复列表
        if (messageEntity.getReplayList() != null) {
            reps.clear();
            for (MessageEntity entity1 : messageEntity.getReplayList()) {
                String content = entity1.getReplyContent();
                if (content.contains("紧急联系人")) {
                    entity1.setReplyContent(String.format("%s：我是%s的紧急联系人，我已收到您的求助",
                            entity1.getReplyName(), messageEntity.getAddUserName()));
                }
                reps.add(ApprovalEntity.builder().img(entity1.getReplyIcon()).name(entity1.getReplyName()).status(entity1.getReplyContent()
                ).time(entity1.getReplyAt()).build());
            }
            replyAdapter.update(reps);
            replyNum.setVisibility(messageEntity.getReplayList().size() > 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void initList() {
        LayoutManager.getInstance().init(this).initRecyclerView(replyList, true);
        replyAdapter = new ApprovalReplyAdapter(this);
        replyList.setAdapter(replyAdapter);
    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }
}
