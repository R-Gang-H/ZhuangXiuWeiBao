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
 * ????????????-61/61a
 * 61B????????????
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

    public static final int RESULT_1 = 1;//??????
    public static final int RESULT_2 = 2;//??????
    public static final int RESULT_3 = 3;//??????
    public static final int RESULT_4 = 4;//???????????????

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
    private String type;// 1 ???????????? 2 ??????????????? 3 ????????????
    private String linkUrl;
    private boolean isDis;
    private boolean isType;//????????????????????????????????????


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
        mtvTitle.setText("????????????");
        boolean isForm = getIntent().getStringExtra("from") != null;//????????????????????????????????????from??????
        isType = getIntent().getStringExtra("type") != null;
        if (isForm) {
            llEndTime.setVisibility(View.VISIBLE);
            mtvTitle.setText("????????????");
            type = "1";
        }
        if (TextUtils.isEmpty(getIntent().getStringExtra("type"))) {//?????????????????????
            eventId = getIntent().getStringExtra("eventId");
        } else {
            // ?????????????????????????????????
            orderId = getIntent().getStringExtra("orderId");
        }
        getDetail();
    }

    //36 ????????????/???????????????????????????
    public void getDetail() {//type  1?????????2????????? ???3??????
        HttpManager.getInstance().getDetail("ApprovalDetailsActivity",
                eventId, orderId, type, new HttpCallBack<BaseDataModel<MessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        entity = data.getData().get(0);
                        setData(entity);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(ApprovalDetailsActivity.this);
                        } else if (statusCode == 1007) {
                            U.showToast("?????????????????????!");
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
            case RESULT_1://??????
                if (data != null) {
                    pid = data.getStringExtra("pid");
                    shareToParter();
                }
                break;
            case RESULT_2://??????
                if (data != null) {
                    orderId = data.getStringExtra("cid");
                    addChecker();
                }
                break;
            case RESULT_3://??????
            case RESULT_4://???????????????
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

    //37 ??????????????????
    private void reply() {
        HttpManager.getInstance().reply("ApprovalDetailsBriefActivity", eventId, status, content,
                new HttpCallBack<BaseBeanModel>() {
                    @Override
                    public void onSuccess(BaseBeanModel data) {
                        U.showToast("????????????");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(ApprovalDetailsActivity.this);
                            return;
                        } else if (statusCode == 1007) {
                            //U.showToast("?????????????????????????????????,??????????????????????????????!");
                            U.showToast("????????????????????????");
                            return;
                        } else if (statusCode == 10012) {
                            U.showToast("???????????????");
                            return;
                        }
                        U.showToast(errorMsg + "");
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    //38 ????????????
    private void shareToParter() {
        HttpManager.getInstance().shareToParter("ApprovalDetailsBriefActivity", eventId, pid,
                new HttpCallBack<BaseBeanModel>() {
                    @Override
                    public void onSuccess(BaseBeanModel data) {
                        U.showToast("??????");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//????????????
                            U.showToast("????????????????????????");
                            HttpManager.getInstance().dologout(ApprovalDetailsActivity.this);
                            return;
                        } else if (statusCode == 10012) {
                            U.showToast("???????????????");
                            return;
                        }
                        U.showToast(errorMsg + "");
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    //39 ???????????????????????????
    private void addChecker() {
        HttpManager.getInstance().addChecker("ApprovalDetailsBriefActivity", eventId, orderId,
                new HttpCallBack<BaseBeanModel>() {
                    @Override
                    public void onSuccess(BaseBeanModel data) {
                        U.showToast("??????");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//????????????
                            U.showToast("????????????????????????!");
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

//        linMenu.setVisibility(U.isNotEmpty(msgEntity) ? View.VISIBLE : View.GONE);// ??????????????????????????????

        List<DisclosureEntity> disLists = entity.getDisclose();
        isDis = U.isNotEmpty(disLists);
        boolean isAllWrit = false;
        boolean isExit = false;
        mlinInfo.setVisibility(isDis ? View.GONE : View.VISIBLE);
        rlDis.setVisibility(isDis ? View.VISIBLE : View.GONE);
        if (isDis) {
            String entDate = DateUtils.getDateToString(Long.valueOf(ennTime), "yyyy/MM/dd");
            tvendTime.setText(String.format("????????????\t\t%s\t%s\t%s", entDate, DateUtils.getWeekByDateStr(entDate),
                    DateUtils.getDateToString(Long.valueOf(ennTime), "HH:mm")));

            relMenu.setVisibility(View.VISIBLE);
            llDisclosure.removeAllViews();
            mtvBo.setText("???????????????");
            for (int i = 0; i < disLists.size(); i++) {
                LinearLayout viewLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_dis_exetu, null);
                TextView tvItemName = viewLayout.findViewById(R.id.tv_item_name);
                TextView tvExecName = viewLayout.findViewById(R.id.tv_exec_name);
                ImageView imgItem = viewLayout.findViewById(R.id.img_item);
                tvItemName.setText(disLists.get(i).getItemName());
                tvExecName.setText(String.format("????????????%s", disLists.get(i).getDoreName()));
                llDisclosure.addView(viewLayout);
                if (disLists.size() - 1 == i) {
                    imgItem.setVisibility(View.GONE);
                }
                if (!isExit && userEntity.getUid().equals(disLists.get(i).getDoerId())) {
                    isExit = true;// ?????????????????????????????????????????????
                }
            }
            if (entity.getAddMobile().equals(userEntity.getMobile())) {// ?????????????????????????????????????????? !isExit &&
                isAllWrit = entity.getProcess().size() == disLists.size();// ??????????????????????????????????????????
                if (isAllWrit || isType) {
                    mtvBo.setText("???????????????");// ??????????????????????????????
                } else {// ????????????????????????
                    mtvBo.setVisibility(View.GONE);
                }
            }
        }
        List<MessageEntity> workers = entity.getWorkers();//?????????
        if (workers != null && workers.size() > 0) {
            if (workers.size() == 1) {
                mtvExecutive.setText(workers.get(0).getWorkName());
            } else {
                mtvExecutive.setText(String.format("%s???%s?????????", workers.get(0).getWorkName(), workers.size()));
            }
        } else {
            mtvExecutive.setText("???????????????");
        }
        List<MessageEntity> copyto = entity.getCopyto();//?????????
        if (copyto != null && copyto.size() > 0) {
            if (copyto.size() == 1) {
                mtvCc.setText(copyto.get(0).getCopyName());
            } else {
                mtvCc.setText(String.format("%s???%s?????????", copyto.get(0).getCopyName(), copyto.size()));
            }
        } else {
            mtvCc.setText("???????????????");
        }

        mtvBusinessDes.setText(entity.getContent());
        linkUrl = entity.getLinkUrl().trim();
        tvLinkURL.setVisibility(U.isNotEmpty(linkUrl) ? View.VISIBLE : View.GONE);
        tvLinkURL.setText(String.format("%s\t\t%s", "????????????", linkUrl));
        if (entity.getHuifu() != null && entity.getHuifu().size() > 0) {
            mtvIndex.setText(String.format("??????\t\t%s", entity.getHuifu().size()));
            replyView.setVisibility(View.VISIBLE);
        }
        //????????????
        if (entity.getHuifu() != null) {
            reps.clear();
            for (MessageEntity entity1 : entity.getHuifu()) {
                reps.add(ApprovalEntity.builder().img(entity1.getHuiImage()).name(entity1.getHuiName())
                        .status(entity1.getHuiConent()).time(entity1.getHuiTime()).build());
            }
            replyAdapter.update(reps);
        }
        mtvRight.setVisibility(entity.getAgreeStatus().equals("1") ? View.GONE : View.VISIBLE);// ???????????? 1 ??????????????? 2?????????????????????
        mtvLeft.setBackgroundResource(entity.getAgreeStatus().equals("1") ? R.mipmap.ic_login_btn_bg : R.mipmap.bg_bt_left);
        mtvBo.setVisibility(isExit ? View.VISIBLE : View.GONE);
        if (entity.getType().equals("2")) {//1?????????/2??????/3??????
            mtvBusiness.setText(entity.getApproveName());
            if (entity.getAddMobile().equals(userEntity.getMobile())) {// ??????????????????????????????
                relMenu.setVisibility(View.VISIBLE);
                mtvLeft.setBackgroundResource(R.mipmap.ic_login_btn_bg);
                mtvRight.setVisibility(View.GONE);
                mtvBo.setVisibility(entity.getCanel().equals("2") || !isType ? View.GONE : View.VISIBLE);//????????????????????? 1??????2??? | ?????????????????????
                mtvBo.setText("????????????");
            } else if (entity.getWorkers() != null && entity.getWorkers().size() != 0) {
                for (MessageEntity entity1 : entity.getWorkers()) {
                    if (UserManager.getInstance().userData.getUid().
                            equals(entity1.getWid())) {//?????????????????????
                        relMenu.setVisibility(View.VISIBLE);
                        //?????????????????? ???????????? 1????????????/2???????????????
                        if (entity.getApproveType() != null) {
                            if (entity.getApproveType().equals("1")) {// 1????????????/2???????????????
                                //????????????  ????????????????????????
                                if (entity.getForwardStatus().equals("2")) {//?????????????????? 1?????? 2 ??????
                                    mtvBo.setVisibility(View.GONE);
                                    linMenu.setVisibility(View.VISIBLE);
                                } else {
                                    mtvBo.setVisibility(View.VISIBLE);
                                    mtvBo.setText("????????????");
                                }
                            } else {//???????????????
                                linMenu.setVisibility(View.VISIBLE);
                                mtvBo.setVisibility(View.VISIBLE);
                                mtvBo.setText("???????????????");
                            }
                        }
                        break;
                    }
                }
            }
        } else if (entity.getType().equals("3")) {
            mtvBusiness.setText(entity.getTitle());
            // ?????????????????? ??????????????????????????? ?????????????????????
            if ((isType && !isExit) && entity.getAddMobile().equals(userEntity.getMobile())) {// !isAllWrit &&
                relMenu.setVisibility(View.VISIBLE);
                mtvLeft.setBackgroundResource(R.mipmap.ic_login_btn_bg);
                mtvRight.setVisibility(View.GONE);
                mtvBo.setVisibility(entity.getCanel().equals("2") || !isType ? View.GONE : View.VISIBLE);//????????????????????? 1??????2??? | ?????????????????????
                mtvBo.setText("????????????");
            }
        } else {
            mtvBusiness.setText(entity.getTitle());
        }
        //??????/?????? ????????????
        if (entity.getProcess() != null) {
            lines.clear();
            for (MessageEntity entity1 : entity.getProcess()) {
                //????????????????????????????????????????????????????????????-????????????/????????????-??????????????????????????????
                if (userEntity.getUid().equals(entity1.getUid())) {
                    if (!isDis) {
//                        mtvBo.setVisibility(View.VISIBLE);
                    } else {// ??????????????? | if (!isAllWrit) ???????????????????????????
                        mtvBo.setText("???????????????");// ?????????????????????????????????????????????????????????????????????????????????????????????
                    }
                }
                boolean isTaskDetail = getIntent().getStringExtra("from") != null;//????????????????????????????????????from??????
                lines.add(ApprovalEntity.builder().name(isTaskDetail ? String.format("%s\t????????????", entity1.getName()) :
                        String.format("%s\t??????", entity1.getName()))
                        .status(String.format("???%s???", isTaskDetail ? entity1.getContent() : "??????"))
                        .time(entity1.getCreateAt()).isFinish(true).build());
            }
            lineAdapter.update(lines, true);
        }
        //????????????
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
     * ??????
     */
    private void initImgList() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3,
                GridLayoutManager.VERTICAL, false);
        mxreGrid.setLayoutManager(manager);
        imgAdapter = new GridImageAdapter(this, null);
        mxreGrid.addItemDecoration(new ItemOffsetDecoration(5, 5, 5, 15));
    }

    /**
     * ??????
     */
    private void initRepList() {
        replyAdapter = new ApprovalReplyAdapter(this);
        LayoutManager.getInstance().iniXrecyclerView(mxreList);
        mxreList.setAdapter(replyAdapter);
    }

    /**
     * ?????????
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
                            .setTitle("?????????????")
                            .setNegativeButton("??????", (dialog12, which) -> {
                                dialog12.dismiss();
                                BitmapUtils.getInstance().getCallPhone(this, stuPhone);
                            })
                            .setPositiveButton("??????", (dialog1, which) -> dialog1.dismiss())
                            .create();
                    dialog.show();
                }
                break;
            case R.id.tv_link:// ????????????
                if (!linkUrl.contains("http")) {
                    U.showToast("???????????????");
                    return;
                }
                startActivity(new Intent(this, AboutUsActivity.class)
                        .putExtra("type", "3")
                        .putExtra("linkUrl", linkUrl));
                break;
            case R.id.tv_left://??????
                if (mtvLeft.getText().equals("??????")) {
                    startActivityForResult(new Intent(this, ReplyActivity.class)
                            .putExtra("type", "reply"), RESULT_3);
                    status = "1";
                }
                break;
            case R.id.tv_right://???????????????
                if (mtvRight.getText().equals("???????????????")) {
                    startActivityForResult(new Intent(this, ReplyActivity.class)
                            .putExtra("type", "agree"), RESULT_4);
                    status = "2";
                }
                break;
            case R.id.tv_bo://???????????????/????????????
                String boStr = mtvBo.getText().toString();
                if (boStr.equals("????????????")) {
                    startActivityForResult(new Intent(this, SelectPersonActivity.class)
                            .putExtra("type", "1"), RESULT_1);
                } else if (boStr.equals("???????????????")) {//???????????????
                    startActivityForResult(new Intent(this, SelectPersonActivity.class)
                            .putExtra("type", "2"), RESULT_2);
                } else if (boStr.equals("????????????")) {
                    stopApproval("1");
                } else if (boStr.equals("????????????")) {
                    stopApproval("2");
                } else if (boStr.equals("???????????????") || boStr.equals("???????????????")) {//??????????????? | ???????????????
                    startActivity(new Intent(this, FacilityColumnActivity.class)
                            .putExtra("eventId", eventId)
                            .putExtra("entityDetail", entity));
                } else if (boStr.equals("???????????????")) {
                    startActivity(new Intent(this, AboutUsActivity.class)
                            .putExtra("type", "3")
                            .putExtra("linkUrl", entity.getUrl()));
                }
                break;
        }
    }

    // ????????????
    private void stopApproval(String type) {
        HttpManager.getInstance().doStopApproval("ApprovalDetailsActivity", orderId, type, new HttpCallBack<BaseDataModel>() {
            @Override
            public void onSuccess(BaseDataModel data) {
                mtvBo.setVisibility(View.GONE);
                EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                U.showToast("?????????");
            }

            @Override
            public void onFail(int statusCode, String errorMsg) {
                if (statusCode == 1003) {//????????????
                    U.showToast("????????????????????????!");
                    HttpManager.getInstance().dologout(ApprovalDetailsActivity.this);
                } else if (statusCode == 1005) {// ???????????????
                    U.showToast("?????????!");
                } else if (statusCode == 10012) {// ?????????????????????
                    U.showToast("???????????????!");
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
                        if (statusCode == 1003) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(ApprovalDetailsActivity.this);
                        } else if (statusCode == 10012) {
                            U.showToast("????????????");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    @Override
    public void onItemClick(int position, View v) {
        //????????????
        String[] images = entity.getImages().split("#");
        String image = images[position];
        if (image.substring(image.lastIndexOf(".") + 1).contains("jpg") ||
                image.substring(image.lastIndexOf(".") + 1).contains("png")) {
            startActivity(new Intent(this, ImgDetailsActivity.class)
                    .putExtra("orderData", entity)
                    .putExtra("index", position - 1));
        } else {
            //??????????????????
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
            getDetail();//????????????
        }
    }
}
