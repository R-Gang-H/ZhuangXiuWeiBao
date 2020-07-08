package com.zhuangxiuweibao.app.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.white.progressview.CircleProgressView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.NeedEntity;
import com.zhuangxiuweibao.app.common.audio.AudioRecorderButton;
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
import com.zhuangxiuweibao.app.common.utils.permissions.PermissionCallBackM;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.FullyGridLayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.ItemOffsetDecoration;
import com.zhuangxiuweibao.app.ui.adapter.GridImageAdapter;
import com.zhuangxiuweibao.app.ui.bean.DemandBean;
import com.zxy.tiny.Tiny;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import butterknife.Setter;

/**
 * 发需求  住户端
 */
public class NeedReleaseActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.il_step)
    LinearLayout ilStep;
    @BindView(R.id.tv_step)
    TextView tvStep;
    @BindView(R.id.tv_step_content)
    TextView tvStepContent;
    @BindView(R.id.ll_btn_tag)
    LinearLayout llBtnTag;
    @BindView(R.id.rl_voice)
    RelativeLayout rlVoice;
    @BindView(R.id.tv_upload_voice)
    TextView tvUploadVoice;
    @BindView(R.id.iv_voice)
    ImageView ivVoice;
    @BindView(R.id.iv_del)
    ImageView ivDel;
    @BindView(R.id.iv_audio)
    ImageView ivAudio;
    @BindView(R.id.iv_press_voice)
    AudioRecorderButton ivPressVoice;
    @BindView(R.id.fr_voice)
    FrameLayout frVoice;
    @BindView(R.id.tv_upload_img)
    TextView tvUploadImg;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.et_need)
    EditText mtvNeeds;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    @BindView(R.id.circle_progress_fill_in)
    CircleProgressView mCircleProgress;

    private GridImageAdapter adapter;
    private int maxSelectNum = 5;
    private int themeId;
    AliYunOss mOssClient;

    private String houseId;
    private String tag;// 1寻物启事 2闲置物品 3为你服务 999不加标签

    @BindViews({R.id.btn_server, R.id.btn_something, R.id.btn_unused, R.id.btn_no_tag})
    List<Button> radioButtons;
    private String type = "1";// 1：发布住房维保 2：发布小喇叭
    private String needContent;
    private DemandBean zoneBean;
    private float seconds;
    private String filePath;

    @Override
    public int getLayoutId() {
        return R.layout.activity_send_needs;
    }

    @Override
    public void initView() {
        tvStep.setText("2");
        tvUploadVoice.setText(Html.fromHtml(getResources().getString(R.string.upload_voice)));
        tvUploadImg.setText(Html.fromHtml(getResources().getString(R.string.upload_image)));

        getDataByIntent();

        themeId = R.style.picture_white_style;

        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3,
                GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, onAddPicClickListener);
        adapter.setSelectMax(maxSelectNum);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(5, 5));
        adapter.setOnItemClickListener((position, v) -> {
            if (adapter.selectList.size() > 0) {
                LocalMedia media = adapter.selectList.get(position);
                String pictureType = media.getPictureType();
                int mediaType = PictureMimeType.pictureToVideo(pictureType);
                switch (mediaType) {
                    case 1:
                        // 预览图片 可自定长按保存路径
                        //PictureSelector.create(MainActivity.this).themeStyle(themeId).externalPicturePreview(position, "/custom_file", selectList);
                        PictureSelector.create(this).themeStyle(themeId).openExternalPreview(position, adapter.selectList);
                        break;
                    case 2:
                        // 预览视频
                        PictureSelector.create(this).externalPictureVideo(media.getPath());
                        break;
                    case 3:
                        // 预览音频
                        PictureSelector.create(this).externalPictureAudio(media.getPath());
                        break;
                }
            }
        });
    }

    @Override
    public void initData() {
        radioButtons.get(0).setOnClickListener(this);
        radioButtons.get(1).setOnClickListener(this);
        radioButtons.get(2).setOnClickListener(this);
        radioButtons.get(3).setOnClickListener(this);
    }

    private void getDataByIntent() {
        mOssClient = AliYunOss.getInstance(this);

        houseId = UserManager.getInstance().userData.getHouseId();
        zoneBean = (DemandBean) getIntent().getSerializableExtra("zoneBean");
        type = getIntent().getStringExtra("type");
        tvTitle.setText(type.equals("1") ? "发需求" : "小喇叭");

        ilStep.setVisibility(type.equals("1") ? View.VISIBLE : View.GONE);
        rlVoice.setVisibility(type.equals("1") ? View.VISIBLE : View.GONE);
        llBtnTag.setVisibility(type.equals("2") ? View.VISIBLE : View.GONE);
        recordAudio();
    }

    private boolean validation() {
        //判断条件
        if (type.equals("2") && TextUtils.isEmpty(tag)) {
            U.showToast("请选择标签");
            return false;
        }
        needContent = U.getString(mtvNeeds.getText().toString());
        if (TextUtils.isEmpty(needContent)) {
            U.showToast("请输入描述");
            return false;
        }
        /*if (adapter.selectList.size() == 0) {
            U.showToast("请选择图片");
            return false;
        }*/
        return true;
    }

    /**
     * 12.发布小喇叭（完成）
     */
    private void releaseNews() {
        StringBuilder pngOravis = new StringBuilder();
        boolean isExcut = false;
        for (String url : adapter.pngOravis) {
            if (isExcut) {
                pngOravis.append("#");
            }
            pngOravis.append(url);
            isExcut = true;
        }
        HttpManager.getInstance().releaseNews("NeedReleaseActivity",
                tag, needContent, pngOravis.toString(),
                new HttpCallBack<BaseDataModel<NeedEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<NeedEntity> data) {
                        reset();
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
                        new AlertDialog.Builder(NeedReleaseActivity.this)
                                .setTitle("提交成功")
                                .setMessage(R.string.release_need)
                                .setCancelable(false)
                                .setPositiveButton("确定", (arg0, arg1) -> {
                                    startActivity(new Intent(NeedReleaseActivity.this, MainActivity.class));
                                }).create().show();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NeedReleaseActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(NeedReleaseActivity.this);
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

    @Override
    public void onClick(View v) {
        // 1寻物启事 2闲置物品 3为你服务 999不加标签
        switch (v.getId()) {
            case R.id.btn_server:
                checkStatus(0);
                tag = "3";
                break;
            case R.id.btn_something:
                checkStatus(1);
                tag = "1";
                break;
            case R.id.btn_unused:
                checkStatus(2);
                tag = "2";
                break;
            case R.id.btn_no_tag:
                checkStatus(3);
                tag = "999";
                break;
        }
    }

    public void checkStatus(int index) {
        UserManager.apply(radioButtons, BTNSPEC, radioButtons.get(index));
    }

    //控制normal 状态的当前View 隐藏，其它空间仍然为显示
    final Setter<TextView, TextView> BTNSPEC = (view, value, index) -> {
        assert value != null;
        if (view.getId() == value.getId()) {
            view.setSelected(true);
        } else {
            view.setSelected(false);
        }
    };

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            // 进入相册
            PictureSelector.create(NeedReleaseActivity.this)
                    .openGallery(PictureMimeType.ofAll())// 全部、图片、视频、音频
                    .theme(themeId)// 主题样式设置
                    .maxSelectNum(maxSelectNum)// 最大图片选择数量
                    .minSelectNum(1)// 最小选择数量
                    .imageSpanCount(3)// 每行显示个数
                    .selectionMode(PictureConfig.MULTIPLE)// 多选
                    .previewImage(true)// 是否可预览图片
                    .previewVideo(true)// 是否可预览视频
                    .enablePreviewAudio(true) // 是否可播放音频
                    .isCamera(true)// 是否显示拍照按钮
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    .enableCrop(false)// 是否裁剪
                    .compress(false)// 是否压缩
                    .synOrAsy(true)//同步true或异步false 压缩 默认同步
                    //.compressSavePath(getPath())//压缩图片保存地址
                    //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                    .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                    .isGif(true)// 是否显示gif图片
                    .openClickSound(true)// 是否开启点击声音
                    .selectionMedia(adapter.selectList)// 是否传入已选图片
                    .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                    .minimumCompressSize(100)// 小于100kb的图片不压缩
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    adapter.pngOravis.clear();
                    adapter.uploadModules.clear();
                    adapter.selectList = PictureSelector.obtainMultipleResult(data);
                    ExecutorService executorService = Executors.newSingleThreadExecutor();//避免文件路径重复
                    for (int i = 0; i < adapter.selectList.size(); i++) { // 高并发
                        int finalI = i;//避免文件名称覆盖
                        Runnable syncRunnable = () -> {
                            LocalMedia media = adapter.selectList.get(finalI);
                            String path = U.isEmpty(media.getCompressPath()) ? media.getPath() :
                                    media.getCompressPath();
                            String pictureType = media.getPictureType();
                            String curTime = DateUtils.getCurTimeLong("yyyyMMddHHmmss") + "/" + System.currentTimeMillis() + finalI;
                            String mImageName = "";
                            if (pictureType.equalsIgnoreCase("image/png") || pictureType.equalsIgnoreCase("image/jpeg")) {
                                mImageName = Constant.picture + curTime + ".jpg";
                            } else if (pictureType.equalsIgnoreCase("video/mp4")) {
                                long duration = media.getDuration();
                                if ((duration / 1000) > 60) {// 限制视频时长不能超过60秒
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            U.showToast("视频时长不能超过30秒");
                                        }
                                    });
                                } else {
                                    mImageName = Constant.video + curTime + ".mp4";
                                }
                            }
                            LogUtils.d("CourserReleaseActivity", path + "===" + mImageName);
                            if (U.isNotEmpty(mImageName)) {
                                //准备上传工作
                                readyUpload(path, pictureType, mImageName);
                            }
                        };
                        executorService.execute(syncRunnable);
                    }
                    break;
            }
            adapter.notifyDataSetChanged();
        }

    }

    //准备上传参数
    private void readyUpload(String path, String pictureType, String mImageName) {
        adapter.pngOravis.add(Constant.OSS_URL + mImageName);
        adapter.uploadModules.add(UploadModule.builder().picPath(path)
                .pictureType(pictureType).uploadObject(mImageName).build());
    }

    private void upLoadVoice(String filePath, int seconds) {
        ivAudio.setVisibility(View.GONE);
        MediaManager.playSound(filePath, mp -> {
            // 录音播放完毕
            ivAudio.setVisibility(View.VISIBLE);
        });
    }

    @OnClick({R.id.rl_back_button, R.id.ll_del, R.id.iv_voice, R.id.tv_complete})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.ll_del:
                frVoice.setVisibility(View.GONE);
                ivPressVoice.setVisibility(View.VISIBLE);
                filePath = "";
                seconds = 0;
                ivAudio.setVisibility(View.VISIBLE);
                MediaManager.release();
                break;
            case R.id.iv_voice:
                upLoadVoice(filePath, (int) seconds);
                break;
            case R.id.tv_complete:
                if (validation()) {
                    if (type.equals("1")) {// 1：发布住房维保
                        NeedEntity needData = new NeedEntity();
                        needData.setHouseId(houseId);
                        needData.setZone(zoneBean);
                        needData.setDescribe(needContent);
                        needData.setPngOravis(adapter.pngOravis);
                        needData.setImages(adapter.uploadModules);
                        needData.setVoice(frVoice.getVisibility() == View.VISIBLE);
                        needData.setFilePath(filePath);
                        needData.setSeconds(seconds);
                        startActivity(new Intent(NeedReleaseActivity.this, NeedPreActivity.class)
                                .putExtra("needData", needData));
                    } else if (type.equals("2")) {// 2：发布小喇叭
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
                            releaseNews();
                        }
                    }
                }
                break;
        }
    }

    public void recordAudio() {
        requestPermission(
                Constant.RC_AUDIO,
                new String[]{Manifest.permission.RECORD_AUDIO
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE},
                getString(R.string.rationale_audio),
                new PermissionCallBackM() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionGrantedM(int requestCode, String... perms) {
                        ivPressVoice.setAudioFinishRecorderListener((seconds, filePath) -> {
                            NeedReleaseActivity.this.seconds = seconds;
                            NeedReleaseActivity.this.filePath = filePath;
                            //录音完成 seconds 录音时长，filePath 录音文件的位置
                            LogUtils.d(NeedReleaseActivity.this, "Audio recorded successfully!");
                            EventBus.getDefault().post(new ToUIEvent(ToUIEvent.VODIO_EVENT));
                        });
                    }

                    @Override
                    public void onPermissionDeniedM(int requestCode, String... perms) {
                        LogUtils.e(NeedReleaseActivity.this, "TODO: CAMERA Denied", Toast.LENGTH_SHORT);
                    }
                });
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
                    runOnUiThread(() -> releaseNews());//主线程
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
        if (pictureType.equalsIgnoreCase("image/png")
                || pictureType.equalsIgnoreCase("image/jpeg")) {
            isProgress = true;

            float numb = (float) (Math.random() * 100);// 100以内的随机数
            int itemNum = 3;//小数点前的位数
            float totalNumb = numb * itemNum;
            index = (float) (Math.round(totalNumb * 100) / 100);//如果要求精确小数点前4位就*10000然后/10000

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

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        switch (event.getTag()) {
            case ToUIEvent.VODIO_EVENT:
                if (frVoice.getVisibility() == View.GONE) {
                    frVoice.setVisibility(View.VISIBLE);
                    ivPressVoice.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }
}
