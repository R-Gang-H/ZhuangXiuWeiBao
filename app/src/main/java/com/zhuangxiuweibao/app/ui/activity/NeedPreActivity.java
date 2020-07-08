package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.white.progressview.CircleProgressView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.NeedEntity;
import com.zhuangxiuweibao.app.bean.UserEntity;
import com.zhuangxiuweibao.app.common.audio.MediaManager;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.Constant;
import com.zhuangxiuweibao.app.common.user.ToUIEvent;
import com.zhuangxiuweibao.app.common.user.UploadModule;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.ThreadPoolManager;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.utils.alicloud.oss.AliYunOss;
import com.zhuangxiuweibao.app.common.utils.alicloud.oss.callback.ProgressCallback;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.FullyGridLayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.ItemOffsetDecoration;
import com.zhuangxiuweibao.app.ui.adapter.GridImageAdapter;
import com.zxy.tiny.Tiny;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.FutureTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 需求预览
 */
public class NeedPreActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_step)
    TextView tvStep;
    @BindView(R.id.tv_step_content)
    TextView tvStepContent;
    @BindView(R.id.tv_server_name)
    TextView tvServerName;
    @BindView(R.id.tv_repairs)
    TextView tvRepairs;
    @BindView(R.id.tv_phone_num)
    TextView tvPhoneNum;
    @BindView(R.id.tv_house_name)
    TextView tvHouseName;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.iv_voice)
    ImageView ivVoice;
    @BindView(R.id.iv_audio)
    ImageView ivAudio;
    @BindView(R.id.rl_attach)
    RelativeLayout rlAttach;
    @BindView(R.id.fr_voice)
    FrameLayout frVoice;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    @BindView(R.id.circle_progress_fill_in)
    CircleProgressView mCircleProgress;

    private NeedEntity needData;
    private String houseId, zoneId;

    private GridImageAdapter adapter;
    AliYunOss mOssClient;
    private UserEntity userData;
    private String yuyin = "";
    private float seconds;
    private String filePath;

    @Override
    public int getLayoutId() {
        return R.layout.activity_need_pre;
    }

    @Override
    public void initView() {
        tvTitle.setText("发需求");
        tvStep.setText("3");
        tvStepContent.setText("步,确认以下内容正确后,点击提交");

        needData = (NeedEntity) getIntent().getSerializableExtra("needData");
        userData = UserManager.getInstance().userData;

        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3,
                GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, null);
        adapter.setSelectMax(needData.getImages().size());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(5, 5));
    }

    @Override
    public void initData() {
        if (U.isNotEmpty(needData) && U.isNotEmpty(userData)) {
            houseId = needData.getHouseId();
            zoneId = needData.getZone().getId();
            adapter.pngOravis = needData.getPngOravis();
            adapter.uploadModules = needData.getImages();
            filePath = needData.getFilePath();
            seconds = needData.getSeconds();
            tvServerName.setText("维修服务");
            tvRepairs.setText(userData.getName());
            tvPhoneNum.setText(userData.getMobile());
            tvHouseName.setText(userData.getHouseName());
            tvLocation.setText(needData.getZone().getName());
            tvDesc.setText(U.getEmoji(needData.getDescribe()));
            frVoice.setVisibility(needData.isVoice() ? View.VISIBLE : View.GONE);
            rlAttach.setVisibility(adapter.pngOravis.size() > 0 || needData.isVoice() ? View.VISIBLE : View.GONE);
            if (needData.isVoice()) {// 有录音
                String curTime = DateUtils.getCurTimeLong("yyyyMMddHHmmss") + "/" + System.currentTimeMillis();
                String uploadPath = Constant.audio + curTime + ".mp4";
                yuyin = Constant.OSS_URL + uploadPath;
                adapter.uploadModules.add(UploadModule.builder().picPath(filePath)
                        .pictureType("video/mp4").uploadObject(uploadPath).build());
            }
        }
        mOssClient = AliYunOss.getInstance(this);
    }

    /**
     * 11.发布住房维保需求（完成）
     */
    private void askRepair() {
        //判断条件
        String needs = needData.getDescribe();
        if (TextUtils.isEmpty(needs)) {
            U.showToast("请输入房屋受损描述");
            return;
        }
        StringBuilder pngOravis = new StringBuilder();
        boolean isExcut = false;
        for (String url : adapter.pngOravis) {
            if (isExcut) {
                pngOravis.append("#");
            }
            pngOravis.append(url);
            isExcut = true;
        }
        HttpManager.getInstance().askRepair("NeedReleaseActivity",
                houseId, zoneId, needs, pngOravis.toString(), yuyin,
                new HttpCallBack<BaseDataModel<NeedEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<NeedEntity> data) {
                        reset();
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                        showNeedDialog();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NeedReleaseActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(NeedPreActivity.this);
                            return;
                        }
                        U.showToast(errorMsg);
                        reset();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("NeedReleaseActivity onError", throwable.getMessage());
                        reset();
                    }
                });
    }

    private void reset() {
        tvComplete.setEnabled(true);
        mCircleProgress.setVisibility(View.GONE);
        rlBackButton.setClickable(true);
        isBackPressed = true;
    }

    /**
     * 提示
     */
    private void showNeedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_need_success, null);
        TextView tv_go_home = view.findViewById(R.id.tv_go_home);
        TextView tv_relapse = view.findViewById(R.id.tv_relapse);
        tv_go_home.setOnClickListener(v -> {
            startActivity(new Intent(NeedPreActivity.this, MainActivity.class));
            dialog.dismiss();
        });
        tv_relapse.setOnClickListener(v -> {
            startActivity(new Intent(this, DemandStep1_2Activity.class));
            dialog.dismiss();
        });
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
    }

    private void upLoadVoice(String filePath, int seconds) {
        ivAudio.setVisibility(View.GONE);
        MediaManager.playSound(filePath, mp -> {
            // 录音播放完毕
            ivAudio.setVisibility(View.VISIBLE);
        });
    }

    @OnClick({R.id.rl_back_button, R.id.iv_voice, R.id.tv_complete})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.iv_voice:
                upLoadVoice(filePath, (int) seconds);
                break;
            case R.id.tv_complete:
                tvComplete.setEnabled(false);
                mCircleProgress.setVisibility(View.VISIBLE);
                rlBackButton.setClickable(false);
                isBackPressed = false;
                if (adapter.uploadModules.size() > 0) {// 有图片,视频或附件
                    Thread thread = new Thread() {
                        @Override
                        public synchronized void run() {
                            super.run();
                            for (UploadModule media : adapter.uploadModules) { // 高并发
                                LogUtils.e("CourserReleaseActivity",
                                        "----->路径:" + media.getUploadObject());
                                uploadManage(media.getPicPath(), media.getPictureType(), media.getUploadObject());
                            }
                            //子线程
                        }
                    };
                    ThreadPoolManager.getInstance().execute(new FutureTask<>(thread, null), null);
                } else {
                    askRepair();
                }
                break;
        }
    }

    int position = 0;
    float index = 0;
    boolean isProgress = true;
    boolean isBackPressed = true;// 控制返回键是否能受控制

    public ProgressCallback<PutObjectRequest, PutObjectResult> getRrogressCallback() {
        return new ProgressCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                LogUtils.e("CourserReleaseActivity", "---onProgress:---" + (int) (currentSize * 100) / totalSize);
                mCircleProgress.setProgress((int) ((currentSize * 100) / totalSize));
            }

            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                LogUtils.e("CourserReleaseActivity", "---onSuccess:---");
                position++;
                //全部上传完成后提交接口并刷新
                if (adapter.uploadModules.size() == position) {
                    LogUtils.e("CourserReleaseActivity",
                            "----->上传:" + adapter.pngOravis.toString());
                    runOnUiThread(() -> askRepair());//主线程
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException
                    clientException, ServiceException serviceException) {
                LogUtils.e("CourserReleaseActivity", "---onFailure:---");
            }
        };
    }


    private void uploadManage(String picPath, String pictureType, String uploadName) {

        float numb = (float) (Math.random() * 100);// 100以内的随机数
        int itemNum = 3;//小数点前的位数
        float totalNumb = numb * itemNum;
        index = (float) (Math.round(totalNumb * 100) / 100);//如果要求精确小数点前4位就*10000然后/10000

        if (pictureType.equalsIgnoreCase("image/png")
                || pictureType.equalsIgnoreCase("image/jpeg")) {
            isProgress = true;

            Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
            Tiny.getInstance().source(picPath).asFile().withOptions(options).compress((isSuccess, outfile, t) -> {
                //return the compressed file path
                mOssClient.upload(uploadName, outfile, getRrogressCallback());
                isProgress = false;
                mCircleProgress.setProgress(100);
            });
            while (isProgress) {// 图片进度条
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mCircleProgress.setProgress((int) ++index);
            }
        } else if (pictureType.equalsIgnoreCase("video/mp4")) {
            mOssClient.upload(uploadName, picPath, getRrogressCallback());
        } else {
            mOssClient.upload(uploadName, picPath, getRrogressCallback());
        }
    }

    @Override
    public void onBackPressed() {
        if (!isCosumenBackKey()) {
            return;
        }
        super.onBackPressed();
    }

    private boolean isCosumenBackKey() {
        // 这儿做返回键的控制，如果自己处理返回键逻辑就返回true，如果返回false,代表继续向下传递back事件，由系统去控制
        return isBackPressed;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }
}
