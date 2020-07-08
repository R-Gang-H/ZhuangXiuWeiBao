package com.zhuangxiuweibao.app.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.white.progressview.CircleProgressView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.NeedEntity;
import com.zhuangxiuweibao.app.bean.NetWorkEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.Constant;
import com.zhuangxiuweibao.app.common.user.UploadModule;
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
import com.zxy.tiny.Tiny;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

import static com.zhuangxiuweibao.app.ui.activity.FixedApprovalActivity.NETWORKDISK;

//工作人员端发一个群通知
public class SendInfoActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    @BindView(R.id.btnSubmit)
    RelativeLayout btnSubmit;
    @BindView(R.id.tvMTitle)
    TextView tvMTitle;
    @BindView(R.id.selectRange)
    TextView selectRange;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.etDescription)
    EditText etDescription;
    @BindView(R.id.et_link)
    EditText etLink;
    @BindView(R.id.imgList)
    RecyclerView imgList;
    @BindView(R.id.CbReceipt)
    CheckBox CbReceipt;
    @BindView(R.id.tvText)
    TextView tvText;
    @BindView(R.id.startTime)
    TextView startTime;
    @BindView(R.id.endTime)
    TextView endTime;
    @BindView(R.id.listView)
    LinearLayout listView;
    @BindView(R.id.circle_progress_fill_in)
    CircleProgressView mCircleProgress;
    private int SELECT_RANGE = 2;
    private String companyId = "";//公司Id
    private String departmentId = "";//分部Id
    private GridImageAdapter adapter;
    private int themeId, maxSelectNum = 5;
    AliYunOss mOssClient;
    private String startTimes;
    private String endTimes;
    private String sign = "2";
    private String linkUrl;
    List<LocalMedia> pictureLists = new ArrayList<>(), networkPris = new ArrayList<>(), networkPubs = new ArrayList<>();
    private int typeDisk = 0;
    private long startData, endData;

    @Override
    public int getLayoutId() {
        return R.layout.activity_send_info;
    }

    @Override
    public void initView() {
        tvTitle.setText("发通知");
        mOssClient = AliYunOss.getInstance(this);
        initImgList();
        etDescription.setHint("通知内容");
        CbReceipt.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {//	是否需要回执 1需要 2不需要
                sign = "1";
                tvText.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
            } else {
                sign = "2";
                tvText.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
            }
        });
    }

    private void initImgList() {
        themeId = R.style.picture_white_style;//主题样式
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3,
                GridLayoutManager.VERTICAL, false);
        imgList.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, onAddPicClickListener);
        adapter.setSelectMax(maxSelectNum);
        imgList.setAdapter(adapter);
        imgList.addItemDecoration(new ItemOffsetDecoration(5, 5));
        adapter.setOnItemClickListener((position, v) -> {
            if (adapter.selectList.size() > 0) {
                LocalMedia media = adapter.selectList.get(position);
                String pictureType = media.getPictureType();
                int mediaType = U.pictureToVideo(pictureType);
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
                    case Constant.TYPE_FILE:
                        // 预览文件
                        //动态权限申请
                        requestPermission(Constant.REQUEST_CODE_WRITE,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                getString(R.string.rationale_file),
                                new PermissionCallBackM() {
                                    @SuppressLint("MissingPermission")
                                    @Override
                                    public void onPermissionGrantedM(int requestCode, String... perms) {
                                        FileDisplayActivity.actionStart(SendInfoActivity.this, media.getPath(), media.getCutPath());
                                    }

                                    @Override
                                    public void onPermissionDeniedM(int requestCode, String... perms) {
                                        LogUtils.e(SendInfoActivity.this, "TODO: WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT);
                                    }
                                });
                        break;
                }
            }
        });
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.rl_back_button, R.id.tv_complete, R.id.selectRange, R.id.startTime, R.id.endTime})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_complete:
                if (validation()) {
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
                                    LogUtils.e("SendWorkActivity",
                                            "----->路径:" + media.getUploadObject());
                                    if (media.getUploadObject().contains("user/circle/")) {
                                        uploadManage(media.getPicPath(), media.getPictureType(), media.getUploadObject());
                                    } else {
                                        position++;
                                        //全部上传完成后提交接口并刷新
                                        if (adapter.uploadModules.size() == position) {
                                            LogUtils.e("SendWorkActivity",
                                                    "----->上传:" + adapter.pngOravis.toString());
                                            runOnUiThread(() ->
                                                    releaseGroupNotice());//主线程
                                        }
                                    }
                                }//子线程
                            }
                        };
                        ThreadPoolManager.getInstance().execute(new FutureTask<>(thread, null), null);
                    } else {
                        releaseGroupNotice();
                    }
                }

                break;
            case R.id.selectRange://选择发送范围
                startActivityForResult(new Intent(this, SelectSendRangeActivity.class), SELECT_RANGE);
                break;
            case R.id.startTime:
                new TimePickerBuilder(this, (date, v) -> {
                    startData = date.getTime();
                    if (startData < DateUtils.getCurTimeLong()) {
                        U.showToast("开始时间不能小于当前时间");
                        return;
                    }
                    startTimes = String.valueOf(date.getTime() / 1000);
                    startTime.setText(DateUtils.getDateToString(date.getTime() / 1000, "yyyy.MM.dd HH:mm"));
                    startTime.setTextColor(Color.parseColor("#333333"));
                })
                        .setType(new boolean[]{true, true, true, true, true, false})
                        .setRangDate(Calendar.getInstance(), null)
                        .setLabel("年", "月", "日", "时",
                                "分", "")
                        .build().show();
                break;
            case R.id.endTime:
                new TimePickerBuilder(this, (date, v) -> {
                    endData = date.getTime();
                    if (endData < startData) {
                        U.showToast("结束时间不能小于开始时间");
                        return;
                    }
                    endTimes = String.valueOf(date.getTime() / 1000);
                    endTime.setText(DateUtils.getDateToString(date.getTime() / 1000, "yyyy.MM.dd HH:mm"));
                    endTime.setTextColor(Color.parseColor("#333333"));

                })
                        .setType(new boolean[]{true, true, true, true, true, false})
                        .setRangDate(Calendar.getInstance(), null)
                        .setLabel("年", "月", "日", "时",
                                "分", "")
                        .build().show();
                break;
        }
    }

    private boolean validation() {
        //判断条件
        if (TextUtils.isEmpty(etTitle.getText().toString()) || TextUtils.isEmpty(etDescription.getText().toString())) {
            Toast.makeText(this, "请完善信息", Toast.LENGTH_SHORT).show();
            return false;
        }
        /*if (adapter.selectList.size() == 0) {
            U.showToast("请选择图片");
            return false;
        }*/
        if (TextUtils.isEmpty(companyId) && TextUtils.isEmpty(departmentId)) {
            U.showToast("请选择公司或部门");
            return false;
        }
        if (CbReceipt.isChecked()) {
            if ((startData == 0 || endData == 0) || (U.isEmpty(startTime.getText()) || U.isEmpty(endTime.getText()))) {
                U.showToast("请选择时间");
                return false;
            }
        }
        linkUrl = etLink.getText().toString();
        return true;
    }

    private void releaseGroupNotice() {
        StringBuilder pngOravis = new StringBuilder();
        boolean isExcut = false;
        for (String url : adapter.pngOravis) {
            if (isExcut) {
                pngOravis.append("#");
            }
            pngOravis.append(url);
            isExcut = true;
        }
        HttpManager.getInstance().releaseGroupNotice("SendInfoActivity",
                companyId,
                departmentId,
                etTitle.getText().toString(),
                etDescription.getText().toString(),
                pngOravis.toString(), sign,
                startTimes, endTimes, linkUrl,
                new HttpCallBack<BaseDataModel<NeedEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<NeedEntity> data) {
                        U.showToast("发布通知成功");
                        reset();
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NeedReleaseActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(SendInfoActivity.this);
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

    int position = 0;
    float index = 0;
    boolean isProgress = true;
    boolean isBackPressed = true;// 控制返回键是否能受控制

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
                    runOnUiThread(() ->
                            releaseGroupNotice());//主线程
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException
                    clientException, ServiceException serviceException) {
                LogUtils.e("CourserReleaseActivity", "---onFailure:---");
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_RANGE && resultCode == 7) {
            String groupId = data.getStringExtra("groupId");
            companyId = data.getStringExtra("companyId");
            departmentId = data.getStringExtra("departmentId");
            Log.e("Sunny", "cimpanyid" + companyId + "======departmentId" + departmentId);
            String[] names = data.getStringExtra("name").split(",");
            selectRange.setText(names.length > 0 && !TextUtils.isEmpty(names[0]) ? names[0] + "等" + names.length + "个" : names[0]);
        }
        if (resultCode == RESULT_OK) {
            // 图片选择结果回调
            adapter.pngOravis.clear();
            adapter.uploadModules.clear();
            adapter.selectList.clear();
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    pictureLists.clear();
                    networkPubs.clear();
                    networkPris.clear();
                    pictureLists.addAll(PictureSelector.obtainMultipleResult(data));
                    for (int i = 0; i < pictureLists.size(); i++) {
                        LocalMedia media = pictureLists.get(i);
                        media.setMimeType(0);// 0 图片
                        String pictureType = media.getPictureType();
                        String curTime = DateUtils.getCurTimeLong("yyyyMMddHHmmss") + "/" + System.currentTimeMillis() + i;
                        String mImageName = "";
                        if (pictureType.equalsIgnoreCase("image/png") || pictureType.equalsIgnoreCase("image/jpeg")) {
                            mImageName = Constant.picture + curTime + ".jpg";
                        } else if (pictureType.equalsIgnoreCase("video/mp4")) {
                            mImageName = Constant.video + curTime + ".mp4";
                        }
                        media.setCutPath(mImageName);
                    }
                    break;
                case NETWORKDISK:// 网盘选择的文件
                    if (typeDisk == 1) {//公盘
                        networkPubs.clear();
                    } else {// 私盘
                        networkPris.clear();
                    }
                    List<NetWorkEntity> netWorkEntitys = (List<NetWorkEntity>) data.getSerializableExtra("netWorks");
                    for (int i = 0; i < netWorkEntitys.size(); i++) {
                        String path = netWorkEntitys.get(i).getSrcUrl();
                        String pictureType = netWorkEntitys.get(i).getSrcType();
                        String mImageName = netWorkEntitys.get(i).getSrcName();
                        LocalMedia localMedia = new LocalMedia();
                        localMedia.setPath(path);
                        localMedia.setPictureType(pictureType);
                        localMedia.setMimeType(1);// 1 网盘
                        localMedia.setCutPath(mImageName);
                        if (typeDisk == 1) {//公盘
                            networkPubs.add(localMedia);
                        } else {// 私盘
                            networkPris.add(localMedia);
                        }
                    }
                    break;
            }
            adapter.selectList.addAll(pictureLists);
            adapter.selectList.addAll(networkPubs);
            adapter.selectList.addAll(networkPris);
            ExecutorService executorService = Executors.newSingleThreadExecutor();//避免文件路径重复
            for (int i = 0; i < adapter.selectList.size(); i++) { // 高并发
                int finalI = i;//避免文件名称覆盖
                Runnable syncRunnable = () -> {
                    LocalMedia media = adapter.selectList.get(finalI);
                    String path = U.isEmpty(media.getCompressPath()) ? media.getPath() :
                            media.getCompressPath();
                    String pictureType = media.getPictureType();
                    String mImageName = media.getCutPath();
                    int type = media.getMimeType();
                    LogUtils.d("SendWorkActivity", path + "===" + mImageName);
                    //准备上传工作
                    readyUpload(path, pictureType, mImageName, type);
                };
                executorService.execute(syncRunnable);
            }
            adapter.notifyDataSetChanged();
        }
    }

    //准备上传参数
    private void readyUpload(String path, String pictureType, String mImageName, int type) {
        adapter.pngOravis.add(type == 0 ? Constant.OSS_URL + mImageName : path);
        adapter.uploadModules.add(UploadModule.builder().picPath(path)
                .pictureType(pictureType).uploadObject(mImageName).build());
    }

    OptionsPickerView pvOptions;
    private List<String> list = new ArrayList<>();
    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            U.hideKeyboard(SendInfoActivity.this);
            showDialog();
        }
    };

    private void showDialog() {
        final String items[] = {"从图库中选择", "从公用网盘中选择", "从私有网盘中选择"};
        //条件选择器
        if (pvOptions == null) {
            list.clear();
            list.addAll(Arrays.asList(items));
            pvOptions = new OptionsPickerBuilder(this, (options1, option2, options3, v) -> {
                int index = options1;
                switch (index) {
                    case 0:
                        OpenPic();
                        break;
                    case 1:
                        typeDisk = 1;
                        NetworkDisk(typeDisk);
                        break;
                    case 2:
                        typeDisk = 2;
                        NetworkDisk(typeDisk);
                        break;
                    default:
                        break;
                }
            }).build();
            pvOptions.setPicker(list);
        }
        pvOptions.show();
    }


    // 从手机中选择
    private void OpenPic() {
        // 进入相册
        PictureSelector.create(SendInfoActivity.this)
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

    private void NetworkDisk(int type) {
        startActivityForResult(new Intent(this, NetWorkDiskActivity.class)
                .putExtra("type", type)
                .putParcelableArrayListExtra("localMedia", (ArrayList<? extends Parcelable>) adapter.selectList), NETWORKDISK);
    }

    private void reset() {
        tvComplete.setEnabled(true);
        mCircleProgress.setVisibility(View.GONE);
//        rlBackButton.setClickable(true);
        isBackPressed = true;
    }
}
