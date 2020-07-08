package com.zhuangxiuweibao.app.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.ApprovalEntity;
import com.zhuangxiuweibao.app.bean.BaseBeanModel;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.BaseModel;
import com.zhuangxiuweibao.app.bean.DisclosureEntity;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.bean.UserEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.Constant;
import com.zhuangxiuweibao.app.common.user.ToUIEvent;
import com.zhuangxiuweibao.app.common.user.UploadModule;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.BitmapUtils;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.utils.permissions.PermissionCallBackM;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.FullyGridLayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.ItemOffsetDecoration;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.ui.adapter.ApprovalLineAdapter;
import com.zhuangxiuweibao.app.ui.adapter.ApprovalReplyAdapter;
import com.zhuangxiuweibao.app.ui.adapter.GridImageAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 审批详情-61/61a
 * 61B任务详情
 */
public class ApprovalDetailsActivity extends BaseActivity implements GridImageAdapter.OnItemClickListener {

    @BindView(R.id.tv_title)
    TextView mtvTitle;
    @BindView(R.id.tv_applicant)
    TextView mtvApplicant;
    @BindView(R.id.tv_mobile)
    TextView mtvMobile;
    @BindView(R.id.tv_applicant_time)
    TextView mtvApplicantTime;
    @BindView(R.id.ll_endTime)
    LinearLayout llEndTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.ll_execut)
    LinearLayout llExecut;
    @BindView(R.id.tv_executive)
    TextView mtvExecutive;
    @BindView(R.id.ll_copy)
    LinearLayout llCopy;
    @BindView(R.id.tv_cc)
    TextView mtvCc;
    @BindView(R.id.ll_disclosure)
    LinearLayout llDisclosure;
    @BindView(R.id.tv_business)
    TextView mtvBusiness;
    @BindView(R.id.tv_business_des)
    TextView mtvBusinessDes;
    @BindView(R.id.tv_link)
    TextView tvLinkURL;
    @BindView(R.id.view_rv)
    View viewRv;
    @BindView(R.id.xre_grid)
    RecyclerView mxreGrid;
    @BindView(R.id.lin_info)
    LinearLayout mlinInfo;
    @BindView(R.id.re_timeline)
    RecyclerView mxreTimeline;
    @BindView(R.id.xre_list)
    XRecyclerView mxreList;
    @BindView(R.id.tv_index)
    TextView mtvIndex;
    @BindView(R.id.tv_bo)
    TextView mtvBo;
    @BindView(R.id.tv_left)
    TextView mtvLeft;
    @BindView(R.id.tv_right)
    TextView mtvRight;
    @BindView(R.id.replyView)
    LinearLayout replyView;
    @BindView(R.id.rel_menu)
    RelativeLayout relMenu;
    @BindView(R.id.lin_menu)
    LinearLayout linMenu;
    @BindView(R.id.rl_dis)
    RelativeLayout rlDis;
    @BindView(R.id.tv_task_name)
    TextView tvTaskName;
    @BindView(R.id.tv_business_title)
    TextView tvBusinessTitle;
    @BindView(R.id.tv_businessdes)
    TextView tvBusinessdes;
    @BindView(R.id.tv_endtime)
    TextView tvendTime;

    public static final int RESULT_1 = 1;//转给
    public static final int RESULT_2 = 2;//追加
    public static final int RESULT_3 = 3;//回复
    public static final int RESULT_4 = 4;//同意并回复

    private List<ApprovalEntity> reps = new ArrayList<>();
    private ApprovalReplyAdapter replyAdapter;

    private List<ApprovalEntity> lines = new ArrayList<>();
    private ApprovalLineAdapter lineAdapter;

    private String content;
    private MessageEntity entity;
    private String eventId;
    private String status;
    private String pid;
    private String orderId;
    private GridImageAdapter imgAdapter;
    private MessageEntity msgEntity;
    private String type;// 1 任务列表 2 群通知列表 3 审批列表
    private String linkUrl;
    private boolean isDis;
    private boolean isType;//不为空是从我的任务进来的


    @Override
    public int getLayoutId() {
        return R.layout.activity_approval_details;
    }

    @Override
    public void initView() {
        initImgList();
        initRepList();
        initTimeLineList();
    }

    @Override
    public void initData() {
        getDataByIntent();
    }

    private void getDataByIntent() {
        type = "3";
        mtvTitle.setText("审批详情");
        boolean isForm = getIntent().getStringExtra("from") != null;//任务外链跳转过来的都有传from字段
        isType = getIntent().getStringExtra("type") != null;
        if (isForm) {
            llEndTime.setVisibility(View.VISIBLE);
            mtvTitle.setText("任务详情");
            type = "1";
        }
        if (TextUtils.isEmpty(getIntent().getStringExtra("type"))) {//外链跳转过来的
            eventId = getIntent().getStringExtra("eventId");
        } else {
            // 从我的审批列表跳转过来
            orderId = getIntent().getStringExtra("orderId");
        }
        getDetail();
    }

    //36 审批详情/群通知详情（完成）
    public void getDetail() {//type  1任务，2群通知 ，3审批
        HttpManager.getInstance().getDetail("ApprovalDetailsActivity",
                eventId, orderId, type, new HttpCallBack<BaseDataModel<MessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        entity = data.getData().get(0);
                        setData(entity);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ApprovalDetailsActivity.this);
                        } else if (statusCode == 1007) {
                            U.showToast("该交底表已完成!");
                        }
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
            case RESULT_1://转给
                if (data != null) {
                    pid = data.getStringExtra("pid");
                    shareToParter();
                }
                break;
            case RESULT_2://追加
                if (data != null) {
                    orderId = data.getStringExtra("cid");
                    addChecker();
                }
                break;
            case RESULT_3://回复
            case RESULT_4://同意并回复
                if (data != null) {
                    content = data.getStringExtra("content");
                    if (isDis) {
                        disclosureNext();
                    } else {
                        reply();
                    }
                }
                break;
        }
    }

    //37 回复（完成）
    private void reply() {
        HttpManager.getInstance().reply("ApprovalDetailsBriefActivity", eventId, status, content,
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
                            HttpManager.getInstance().dologout(ApprovalDetailsActivity.this);
                            return;
                        } else if (statusCode == 1007) {
                            //U.showToast("当前进度已经同意并回复,已经有其他上级同意过!");
                            U.showToast("当前进度已经同意");
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
        HttpManager.getInstance().shareToParter("ApprovalDetailsBriefActivity", eventId, pid,
                new HttpCallBack<BaseBeanModel>() {
                    @Override
                    public void onSuccess(BaseBeanModel data) {
                        U.showToast("完成");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录");
                            HttpManager.getInstance().dologout(ApprovalDetailsActivity.this);
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
        HttpManager.getInstance().addChecker("ApprovalDetailsBriefActivity", eventId, orderId,
                new HttpCallBack<BaseBeanModel>() {
                    @Override
                    public void onSuccess(BaseBeanModel data) {
                        U.showToast("完成");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ApprovalDetailsActivity.this);
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
        UserEntity userEntity = UserManager.getInstance().userData;

        mtvApplicant.setText(entity.getAddUserName());
        mtvMobile.setText(entity.getAddMobile());
        String ennTime = entity.getEndTime();
        mtvApplicantTime.setText(DateUtils.getDateToString(Long.valueOf(entity.getCreateAt()), "MM/dd HH:mm"));
        tvEndTime.setText(llEndTime.getVisibility() == View.VISIBLE ?
                DateUtils.getDateToString(Long.valueOf(ennTime), "MM/dd HH:mm") : "");

        tvTaskName.setText(entity.getAddUserName());
        tvBusinessTitle.setText(entity.getTitle());
        tvBusinessdes.setText(entity.getContent());

//        linMenu.setVisibility(U.isNotEmpty(msgEntity) ? View.VISIBLE : View.GONE);// 我的进来的不显示回复

        List<DisclosureEntity> disLists = entity.getDisclose();
        isDis = U.isNotEmpty(disLists);
        boolean isAllWrit = false;
        boolean isExit = false;
        mlinInfo.setVisibility(isDis ? View.GONE : View.VISIBLE);
        rlDis.setVisibility(isDis ? View.VISIBLE : View.GONE);
        if (isDis) {
            String entDate = DateUtils.getDateToString(Long.valueOf(ennTime), "yyyy/MM/dd");
            tvendTime.setText(String.format("截止时间\t\t%s\t%s\t%s", entDate, DateUtils.getWeekByDateStr(entDate),
                    DateUtils.getDateToString(Long.valueOf(ennTime), "HH:mm")));

            relMenu.setVisibility(View.VISIBLE);
            llDisclosure.removeAllViews();
            mtvBo.setText("填写交底表");
            for (int i = 0; i < disLists.size(); i++) {
                LinearLayout viewLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_dis_exetu, null);
                TextView tvItemName = viewLayout.findViewById(R.id.tv_item_name);
                TextView tvExecName = viewLayout.findViewById(R.id.tv_exec_name);
                ImageView imgItem = viewLayout.findViewById(R.id.img_item);
                tvItemName.setText(disLists.get(i).getItemName());
                tvExecName.setText(String.format("执行人：%s", disLists.get(i).getDoreName()));
                llDisclosure.addView(viewLayout);
                if (disLists.size() - 1 == i) {
                    imgItem.setVisibility(View.GONE);
                }
                if (!isExit && userEntity.getUid().equals(disLists.get(i).getDoerId())) {
                    isExit = true;// 循环筛选申请人是否同时是执行人
                }
            }
            if (entity.getAddMobile().equals(userEntity.getMobile())) {// 任务交底表发布人可查看交底表 !isExit &&
                isAllWrit = entity.getProcess().size() == disLists.size();// 是否全部执行人都填写了交底表
                if (isAllWrit || isType) {
                    mtvBo.setText("查看交底表");// 全部执行人填写交底表
                } else {// 任务交底表执行人
                    mtvBo.setVisibility(View.GONE);
                }
            }
        }
        List<MessageEntity> workers = entity.getWorkers();//执行人
        if (workers != null && workers.size() > 0) {
            if (workers.size() == 1) {
                mtvExecutive.setText(workers.get(0).getWorkName());
            } else {
                mtvExecutive.setText(String.format("%s等%s位同事", workers.get(0).getWorkName(), workers.size()));
            }
        } else {
            mtvExecutive.setText("暂无执行人");
        }
        List<MessageEntity> copyto = entity.getCopyto();//抄送人
        if (copyto != null && copyto.size() > 0) {
            if (copyto.size() == 1) {
                mtvCc.setText(copyto.get(0).getCopyName());
            } else {
                mtvCc.setText(String.format("%s等%s位同事", copyto.get(0).getCopyName(), copyto.size()));
            }
        } else {
            mtvCc.setText("暂无抄送人");
        }

        mtvBusinessDes.setText(entity.getContent());
        linkUrl = entity.getLinkUrl().trim();
        tvLinkURL.setVisibility(U.isNotEmpty(linkUrl) ? View.VISIBLE : View.GONE);
        tvLinkURL.setText(String.format("%s\t\t%s", "查看链接", linkUrl));
        if (entity.getHuifu() != null && entity.getHuifu().size() > 0) {
            mtvIndex.setText(String.format("回复\t\t%s", entity.getHuifu().size()));
            replyView.setVisibility(View.VISIBLE);
        }
        //回复列表
        if (entity.getHuifu() != null) {
            reps.clear();
            for (MessageEntity entity1 : entity.getHuifu()) {
                reps.add(ApprovalEntity.builder().img(entity1.getHuiImage()).name(entity1.getHuiName())
                        .status(entity1.getHuiConent()).time(entity1.getHuiTime()).build());
            }
            replyAdapter.update(reps);
        }
        mtvRight.setVisibility(entity.getAgreeStatus().equals("1") ? View.GONE : View.VISIBLE);// 同意状态 1 同意并回复 2未点击同意按钮
        mtvLeft.setBackgroundResource(entity.getAgreeStatus().equals("1") ? R.mipmap.ic_login_btn_bg : R.mipmap.bg_bt_left);
        mtvBo.setVisibility(isExit ? View.VISIBLE : View.GONE);
        if (entity.getType().equals("2")) {//1群通知/2审批/3任务
            mtvBusiness.setText(entity.getApproveName());
            if (entity.getAddMobile().equals(userEntity.getMobile())) {// 审批申请人可取消审批
                relMenu.setVisibility(View.VISIBLE);
                mtvLeft.setBackgroundResource(R.mipmap.ic_login_btn_bg);
                mtvRight.setVisibility(View.GONE);
                mtvBo.setVisibility(entity.getCanel().equals("2") || !isType ? View.GONE : View.VISIBLE);//发布人是否取消 1否，2是 | 订单跳转过来的
                mtvBo.setText("取消审批");
            } else if (entity.getWorkers() != null && entity.getWorkers().size() != 0) {
                for (MessageEntity entity1 : entity.getWorkers()) {
                    if (UserManager.getInstance().userData.getUid().
                            equals(entity1.getWid())) {//自己作为审批人
                        relMenu.setVisibility(View.VISIBLE);
                        //判断审批类型 审批类型 1固定流程/2自定义审批
                        if (entity.getApproveType() != null) {
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
                        break;
                    }
                }
            }
        } else if (entity.getType().equals("3")) {
            mtvBusiness.setText(entity.getTitle());
            // 没有全部填写 发任务人可取消任务 从我的任务进来
            if ((isType && !isExit) && entity.getAddMobile().equals(userEntity.getMobile())) {// !isAllWrit &&
                relMenu.setVisibility(View.VISIBLE);
                mtvLeft.setBackgroundResource(R.mipmap.ic_login_btn_bg);
                mtvRight.setVisibility(View.GONE);
                mtvBo.setVisibility(entity.getCanel().equals("2") || !isType ? View.GONE : View.VISIBLE);//发布人是否取消 1否，2是 | 订单跳转过来的
                mtvBo.setText("取消任务");
            }
        } else {
            mtvBusiness.setText(entity.getTitle());
        }
        //审批/任务 完成列表
        if (entity.getProcess() != null) {
            lines.clear();
            for (MessageEntity entity1 : entity.getProcess()) {
                //判断当前登录账户和审批人列表是否匹配，是-可以审批/转给，否-抄送人，无最底部按钮
                if (userEntity.getUid().equals(entity1.getUid())) {
                    if (!isDis) {
//                        mtvBo.setVisibility(View.VISIBLE);
                    } else {// 填写交底表 | if (!isAllWrit) 没有全部填写交底表
                        mtvBo.setText("编辑交底表");// 第一个执行人填写了技术交底表，显示编辑交底表按钮，点击可以编辑
                    }
                }
                boolean isTaskDetail = getIntent().getStringExtra("from") != null;//任务外链跳转过来的都有传from字段
                lines.add(ApprovalEntity.builder().name(isTaskDetail ? String.format("%s\t完成任务", entity1.getName()) :
                        String.format("%s\t审批", entity1.getName()))
                        .status(String.format("“%s”", isTaskDetail ? entity1.getContent() : "同意"))
                        .time(entity1.getCreateAt()).isFinish(true).build());
            }
            lineAdapter.update(lines, true);
        }
        //图片列表
        String[] images = entity.getImages().split("#");
        imgAdapter.uploadModules.clear();
        if (images.length > 0 && U.isNotEmpty(images[0])) {
            imgAdapter.pngOravis = Arrays.asList(images);
            for (String image : images) {
                imgAdapter.uploadModules.add(UploadModule.builder().picPath(image)
                        .pictureType(image.substring(image.lastIndexOf(".") + 1))
                        .build());
            }
            imgAdapter.setSelectMax(images.length);
            mxreGrid.setAdapter(imgAdapter);
            imgAdapter.setOnItemClickListener(this);
            viewRv.setVisibility(View.VISIBLE);
            mxreGrid.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 图片
     */
    private void initImgList() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3,
                GridLayoutManager.VERTICAL, false);
        mxreGrid.setLayoutManager(manager);
        imgAdapter = new GridImageAdapter(this, null);
        mxreGrid.addItemDecoration(new ItemOffsetDecoration(5, 5, 5, 15));
    }

    /**
     * 回复
     */
    private void initRepList() {
        replyAdapter = new ApprovalReplyAdapter(this);
        LayoutManager.getInstance().iniXrecyclerView(mxreList);
        mxreList.setAdapter(replyAdapter);
    }

    /**
     * 时间轴
     */
    private void initTimeLineList() {
        lineAdapter = new ApprovalLineAdapter(this);
        LayoutManager.getInstance().initRecyclerView(mxreTimeline, true);
        mxreTimeline.setAdapter(lineAdapter);
    }


    @OnClick({R.id.rl_back_button, R.id.tv_mobile, R.id.tv_link, R.id.tv_left, R.id.tv_right, R.id.tv_bo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_mobile:
                String stuPhone = mtvMobile.getText().toString();
                if (U.isNotEmpty(stuPhone)) {
                    Dialog dialog = new AlertDialog.Builder(this)
                            .setTitle("确定拨打?")
                            .setNegativeButton("确定", (dialog12, which) -> {
                                dialog12.dismiss();
                                BitmapUtils.getInstance().getCallPhone(this, stuPhone);
                            })
                            .setPositiveButton("取消", (dialog1, which) -> dialog1.dismiss())
                            .create();
                    dialog.show();
                }
                break;
            case R.id.tv_link:// 查看链接
                if (!linkUrl.contains("http")) {
                    U.showToast("该链接有误");
                    return;
                }
                startActivity(new Intent(this, AboutUsActivity.class)
                        .putExtra("type", "3")
                        .putExtra("linkUrl", linkUrl));
                break;
            case R.id.tv_left://回复
                if (mtvLeft.getText().equals("回复")) {
                    startActivityForResult(new Intent(this, ReplyActivity.class)
                            .putExtra("type", "reply"), RESULT_3);
                    status = "1";
                }
                break;
            case R.id.tv_right://同意并回复
                if (mtvRight.getText().equals("完成并回复")) {
                    startActivityForResult(new Intent(this, ReplyActivity.class)
                            .putExtra("type", "agree"), RESULT_4);
                    status = "2";
                }
                break;
            case R.id.tv_bo://追加审批人/转给同事
                String boStr = mtvBo.getText().toString();
                if (boStr.equals("转给同事")) {
                    startActivityForResult(new Intent(this, SelectPersonActivity.class)
                            .putExtra("type", "1"), RESULT_1);
                } else if (boStr.equals("追加审批人")) {//追加审批人
                    startActivityForResult(new Intent(this, SelectPersonActivity.class)
                            .putExtra("type", "2"), RESULT_2);
                } else if (boStr.equals("取消审批")) {
                    stopApproval("1");
                } else if (boStr.equals("取消任务")) {
                    stopApproval("2");
                } else if (boStr.equals("填写交底表") || boStr.equals("编辑交底表")) {//填写交底表 | 编辑交底表
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

    // 撤销审批
    private void stopApproval(String type) {
        HttpManager.getInstance().doStopApproval("ApprovalDetailsActivity", orderId, type, new HttpCallBack<BaseDataModel>() {
            @Override
            public void onSuccess(BaseDataModel data) {
                mtvBo.setVisibility(View.GONE);
                EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                U.showToast("已取消");
            }

            @Override
            public void onFail(int statusCode, String errorMsg) {
                if (statusCode == 1003) {//异地登录
                    U.showToast("该账户在异地登录!");
                    HttpManager.getInstance().dologout(ApprovalDetailsActivity.this);
                } else if (statusCode == 1005) {// 审批不存在
                    U.showToast("不存在!");
                } else if (statusCode == 10012) {// 不是发布审批者
                    U.showToast("不是发布者!");
                } else {
                    U.showToast(errorMsg);
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    private void disclosureNext() {
        HttpManager.getInstance().doDisclosureNext("ApprovalDetailsActivity", eventId, content, status,
                new HttpCallBack<BaseModel>() {
                    @Override
                    public void onSuccess(BaseModel data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ApprovalDetailsActivity.this);
                        } else if (statusCode == 10012) {
                            U.showToast("任务终止");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    @Override
    public void onItemClick(int position, View v) {
        //图片列表
        String[] images = entity.getImages().split("#");
        String image = images[position];
        if (image.substring(image.lastIndexOf(".") + 1).contains("jpg") ||
                image.substring(image.lastIndexOf(".") + 1).contains("png")) {
            startActivity(new Intent(this, ImgDetailsActivity.class)
                    .putExtra("orderData", entity)
                    .putExtra("index", position - 1));
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
                            FileDisplayActivity.actionStart(ApprovalDetailsActivity.this, image, fileName);
                        }

                        @Override
                        public void onPermissionDeniedM(int requestCode, String... perms) {
                            LogUtils.e(ApprovalDetailsActivity.this, "TODO: WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT);
                        }
                    });
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
