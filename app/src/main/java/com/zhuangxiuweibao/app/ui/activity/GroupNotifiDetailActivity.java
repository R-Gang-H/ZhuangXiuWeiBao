package com.zhuangxiuweibao.app.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.ApprovalEntity;
import com.zhuangxiuweibao.app.bean.BaseBeanModel;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.Constant;
import com.zhuangxiuweibao.app.common.user.UploadModule;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.utils.permissions.PermissionCallBackM;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.FullyGridLayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.ItemOffsetDecoration;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.ui.adapter.ApprovalReplyAdapter;
import com.zhuangxiuweibao.app.ui.adapter.GridImageAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

//群通知详情页
public class GroupNotifiDetailActivity extends BaseActivity implements GridImageAdapter.OnItemClickListener {


    @BindView(R.id.btnSign)
    TextView btnSign;
    @BindView(R.id.btnReply)
    TextView btnReply;
    @BindView(R.id.lin_menu)
    LinearLayout linMenu;
    @BindView(R.id.btnSearch)
    TextView btnSearch;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tvmTitle)
    TextView tvmTitle;
    @BindView(R.id.tvContent)
    TextView tvContent;
    @BindView(R.id.imgList)
    RecyclerView imgList;
    @BindView(R.id.ll_startTime)
    LinearLayout llStartTime;
    @BindView(R.id.v_start_end_line)
    View vStartEndLine;
    @BindView(R.id.ll_endTime)
    LinearLayout llEndTime;
    @BindView(R.id.tvRange)
    TextView tvRange;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tv_link)
    TextView tvLinkURL;
    @BindView(R.id.replyList)
    RecyclerView replyList;
    @BindView(R.id.tvReplyNum)
    TextView tvReplyNum;
    @BindView(R.id.tvEndTime)
    TextView tvEndTime;
    @BindView(R.id.tvStartTime)
    TextView tvStartTime;
    private MessageEntity msgEntity, messageEntity;
    private String eventId;
    private ApprovalReplyAdapter replyAdapter;
    private List<ApprovalEntity> reps = new ArrayList<>();
    private String status;
    public static final int RESULT_3 = 3;//回复
    private String content, orderId;
    private GridImageAdapter imgAdapter;
    private String linkUrl;

    @Override
    public int getLayoutId() {
        return R.layout.activity_group_notifi_detail;
    }

    @Override
    public void initView() {
        tvTitle.setText("群通知");
        initList();
        initImgList();
        getDataIntent();
    }

    /**
     * 图片
     */
    private void initImgList() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3,
                GridLayoutManager.VERTICAL, false);
        imgList.setLayoutManager(manager);
        imgAdapter = new GridImageAdapter(this, null);
        imgList.addItemDecoration(new ItemOffsetDecoration(5, 5));
        imgAdapter.setOnItemClickListener(this);

    }

    private void getDataIntent() {
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (U.isNotEmpty(msgEntity)) {
            eventId = msgEntity.getEventId();//主页跳转到外链
            UserManager.getInstance().isReads(eventId);//处理为已读消息
        } else {
            // 我的任务列表跳转过来的
            orderId = getIntent().getStringExtra("orderId");
        }
        getData();
    }

    @Override
    public void initData() {

    }

    private void initList() {
        LayoutManager.getInstance().init(this).initRecyclerView(replyList, true);
        replyAdapter = new ApprovalReplyAdapter(this);
        replyList.setAdapter(replyAdapter);
    }

    private void getData() {//1任务，2群通知 ，3审批
        HttpManager.getInstance().getDetail("GroupNotifiDetailActivity", eventId, orderId, "2",
                new HttpCallBack<BaseDataModel<MessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        if (U.isNotEmpty(data)) {
                            setData(data);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    private void setData(BaseDataModel<MessageEntity> dataModel) {
        messageEntity = dataModel.getData().get(0);
        tvmTitle.setText(messageEntity.getTitle());
        tvContent.setText(messageEntity.getContent());
        tvRange.setText(messageEntity.getBranchName());
        tvTime.setText(DateUtils.getDateToString(Long.valueOf(messageEntity.getCreateAt()), "yyyy/MM/dd HH:mm"));
        tvStartTime.setText(DateUtils.getDateToString(Long.valueOf(messageEntity.getSignBeginTime()), "yyyy/MM/dd HH:mm"));
        tvEndTime.setText(DateUtils.getDateToString(Long.valueOf(messageEntity.getSignEndTime()), "yyyy/MM/dd HH:mm"));
//       sign 	群通知类型 1带报名/默认
        if (messageEntity.getSign().equals("2")) {
            btnSearch.setText("回复");
        } else {
            linMenu.setVisibility(View.VISIBLE);
            llStartTime.setVisibility(View.VISIBLE);
            vStartEndLine.setVisibility(View.VISIBLE);
            llEndTime.setVisibility(View.VISIBLE);
            btnSign.setVisibility(messageEntity.getReceiptStatus().equals("1") ? View.GONE : View.VISIBLE);//回执状态 1 已回执 2 未回执
            btnReply.setBackgroundResource(messageEntity.getReceiptStatus().equals("1") ? R.mipmap.ic_login_btn_bg : R.mipmap.bg_bt_left);
            btnSearch.setText("查看回执情况");
        }

        linkUrl = messageEntity.getLinkUrl().trim();
        tvLinkURL.setVisibility(U.isNotEmpty(linkUrl) ? View.VISIBLE : View.GONE);
//        回复列表
        if (U.isNotEmpty(messageEntity.getHuifu()) && messageEntity.getHuifu().size() > 0) {
            //回复列表
            reps.clear();
            for (MessageEntity entity1 : messageEntity.getHuifu()) {
                reps.add(ApprovalEntity.builder().img(entity1.getHuiImage()).name(entity1.getHuiName()).status(entity1.getHuiConent()
                ).time(entity1.getHuiTime()).build());
            }
            replyAdapter.update(reps);
            tvReplyNum.setText(String.format("回复（%s）", messageEntity.getHuifu().size()));
        } else {
            tvReplyNum.setVisibility(View.GONE);
        }
        //图片列表
        String[] images = messageEntity.getImages().split("#");
        if (images.length > 0 && U.isNotEmpty(images[0])) {
            imgAdapter.pngOravis = Arrays.asList(images);
            for (String image : images) {
                imgAdapter.uploadModules.add(UploadModule.builder().picPath(image)
                        .pictureType(image.substring(image.lastIndexOf(".") + 1))
                        .build());
            }
            imgAdapter.setSelectMax(images.length);
            imgList.setAdapter(imgAdapter);
            // imgAdapter.setOnItemClickListener(this);
        } else {
            imgList.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.btnSign, R.id.btnReply, R.id.btnSearch, R.id.tv_link, R.id.rl_back_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSign:
                if (U.isNotEmpty(messageEntity.getSignBeginTime())) {
                    // 如果未到回执开始时间，点击回执按钮，弹出提示：还未到回执开始时间
                    // 3.如果超过回执结束时间，点击回执按钮，弹出提示：回执时间已过
                    if (U.timeEqual(messageEntity.getSignBeginTime()).equals("1")) {
                        U.showToast("还未到回执开始时间");
                        return;
                    }
                    if (U.timeEqual(messageEntity.getSignEndTime()).equals("-1")) {
                        U.showToast("回执时间已过");
                        return;
                    }
                    replyto();
                }
                break;
            case R.id.tv_link:// 查看链接
                startActivity(new Intent(this, AboutUsActivity.class)
                        .putExtra("type", "3")
                        .putExtra("linkUrl", linkUrl));
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
            case R.id.rl_back_button:
                finish();
                break;
        }
    }

    //42 回执（完成）
    private void replyto() {
        HttpManager.getInstance().replyto("ApprovalDetailsActivity", U.isNotEmpty(msgEntity) ? eventId : orderId,
                new HttpCallBack<BaseBeanModel>() {
                    @Override
                    public void onSuccess(BaseBeanModel data) {
                        U.showToast("成功");
                        finish();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_3://回复
                if (data != null) {
                    content = data.getStringExtra("content");
                    reply();
                }
                break;
        }
    }

    //37 回复（完成）
    private void reply() {
        HttpManager.getInstance().reply("GroupNotifiDetailActivity", U.isNotEmpty(msgEntity) ? eventId : orderId, status, content,
                new HttpCallBack<BaseBeanModel>() {
                    @Override
                    public void onSuccess(BaseBeanModel data) {
                        U.showToast("回复成功");
                        getData();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(GroupNotifiDetailActivity.this);
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
    public void onItemClick(int position, View v) {
        //图片列表
        String[] images = messageEntity.getImages().split("#");
        String image = images[position];
        if (image.substring(image.lastIndexOf(".") + 1).contains("jpg") ||
                image.substring(image.lastIndexOf(".") + 1).contains("png")) {
            startActivity(new Intent(this, ImgDetailsActivity.class)
                    .putExtra("orderData", messageEntity)
                    .putExtra("index", position));
        } else {
            //动态权限申请
            requestPermission(Constant.REQUEST_CODE_WRITE,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    getString(R.string.rationale_file),
                    new PermissionCallBackM() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onPermissionGrantedM(int requestCode, String... perms) {
                            String fileName = image.substring(image.lastIndexOf("/") + 1);
                            FileDisplayActivity.actionStart(GroupNotifiDetailActivity.this, image, fileName);
                        }

                        @Override
                        public void onPermissionDeniedM(int requestCode, String... perms) {
                            LogUtils.e(GroupNotifiDetailActivity.this, "TODO: WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT);
                        }
                    });
        }
    }
}
