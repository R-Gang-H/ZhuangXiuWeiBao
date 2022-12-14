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

//?????????????????????????????????
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
    private String companyId = "";//??????Id
    private String departmentId = "";//??????Id
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
        tvTitle.setText("?????????");
        mOssClient = AliYunOss.getInstance(this);
        initImgList();
        etDescription.setHint("????????????");
        CbReceipt.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {//	?????????????????? 1?????? 2?????????
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
        themeId = R.style.picture_white_style;//????????????
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
                        // ???????????? ???????????????????????????
                        //PictureSelector.create(MainActivity.this).themeStyle(themeId).externalPicturePreview(position, "/custom_file", selectList);
                        PictureSelector.create(this).themeStyle(themeId).openExternalPreview(position, adapter.selectList);
                        break;
                    case 2:
                        // ????????????
                        PictureSelector.create(this).externalPictureVideo(media.getPath());
                        break;
                    case 3:
                        // ????????????
                        PictureSelector.create(this).externalPictureAudio(media.getPath());
                        break;
                    case Constant.TYPE_FILE:
                        // ????????????
                        //??????????????????
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
                    if (adapter.uploadModules.size() > 0) {// ?????????,???????????????
                        Thread thread = new Thread() {
                            @Override
                            public synchronized void run() {
                                super.run();
                                for (UploadModule media : adapter.uploadModules) { // ?????????
                                    LogUtils.e("SendWorkActivity",
                                            "----->??????:" + media.getUploadObject());
                                    if (media.getUploadObject().contains("user/circle/")) {
                                        uploadManage(media.getPicPath(), media.getPictureType(), media.getUploadObject());
                                    } else {
                                        position++;
                                        //??????????????????????????????????????????
                                        if (adapter.uploadModules.size() == position) {
                                            LogUtils.e("SendWorkActivity",
                                                    "----->??????:" + adapter.pngOravis.toString());
                                            runOnUiThread(() ->
                                                    releaseGroupNotice());//?????????
                                        }
                                    }
                                }//?????????
                            }
                        };
                        ThreadPoolManager.getInstance().execute(new FutureTask<>(thread, null), null);
                    } else {
                        releaseGroupNotice();
                    }
                }

                break;
            case R.id.selectRange://??????????????????
                startActivityForResult(new Intent(this, SelectSendRangeActivity.class), SELECT_RANGE);
                break;
            case R.id.startTime:
                new TimePickerBuilder(this, (date, v) -> {
                    startData = date.getTime();
                    if (startData < DateUtils.getCurTimeLong()) {
                        U.showToast("????????????????????????????????????");
                        return;
                    }
                    startTimes = String.valueOf(date.getTime() / 1000);
                    startTime.setText(DateUtils.getDateToString(date.getTime() / 1000, "yyyy.MM.dd HH:mm"));
                    startTime.setTextColor(Color.parseColor("#333333"));
                })
                        .setType(new boolean[]{true, true, true, true, true, false})
                        .setRangDate(Calendar.getInstance(), null)
                        .setLabel("???", "???", "???", "???",
                                "???", "")
                        .build().show();
                break;
            case R.id.endTime:
                new TimePickerBuilder(this, (date, v) -> {
                    endData = date.getTime();
                    if (endData < startData) {
                        U.showToast("????????????????????????????????????");
                        return;
                    }
                    endTimes = String.valueOf(date.getTime() / 1000);
                    endTime.setText(DateUtils.getDateToString(date.getTime() / 1000, "yyyy.MM.dd HH:mm"));
                    endTime.setTextColor(Color.parseColor("#333333"));

                })
                        .setType(new boolean[]{true, true, true, true, true, false})
                        .setRangDate(Calendar.getInstance(), null)
                        .setLabel("???", "???", "???", "???",
                                "???", "")
                        .build().show();
                break;
        }
    }

    private boolean validation() {
        //????????????
        if (TextUtils.isEmpty(etTitle.getText().toString()) || TextUtils.isEmpty(etDescription.getText().toString())) {
            Toast.makeText(this, "???????????????", Toast.LENGTH_SHORT).show();
            return false;
        }
        /*if (adapter.selectList.size() == 0) {
            U.showToast("???????????????");
            return false;
        }*/
        if (TextUtils.isEmpty(companyId) && TextUtils.isEmpty(departmentId)) {
            U.showToast("????????????????????????");
            return false;
        }
        if (CbReceipt.isChecked()) {
            if ((startData == 0 || endData == 0) || (U.isEmpty(startTime.getText()) || U.isEmpty(endTime.getText()))) {
                U.showToast("???????????????");
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
                        U.showToast("??????????????????");
                        reset();
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NeedReleaseActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//????????????
                            U.showToast("????????????????????????!");
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
    boolean isBackPressed = true;// ?????????????????????????????????

    private void uploadManage(String picPath, String pictureType, String uploadName) {
        if (pictureType.equalsIgnoreCase("image/png")
                || pictureType.equalsIgnoreCase("image/jpeg")) {
            isProgress = true;

            float numb = (float) (Math.random() * 100);// 100??????????????????
            int itemNum = 3;//?????????????????????
            float totalNumb = numb * itemNum;
            index = (float) (Math.round(totalNumb * 100) / 100);//??????????????????????????????4??????*10000??????/10000

            Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
            Tiny.getInstance().source(picPath).asFile().withOptions(options).compress((isSuccess, outfile, t) -> {
                //return the compressed file path
                mOssClient.upload(uploadName, outfile, getRrogressCallback());
                isProgress = false;
                mCircleProgress.setProgress(100);
            });
            while (isProgress) {// ???????????????
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
                //??????????????????????????????????????????
                if (adapter.uploadModules.size() == position) {
                    LogUtils.e("CourserReleaseActivity",
                            "----->??????:" + adapter.pngOravis.toString());
                    runOnUiThread(() ->
                            releaseGroupNotice());//?????????
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
            selectRange.setText(names.length > 0 && !TextUtils.isEmpty(names[0]) ? names[0] + "???" + names.length + "???" : names[0]);
        }
        if (resultCode == RESULT_OK) {
            // ????????????????????????
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
                        media.setMimeType(0);// 0 ??????
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
                case NETWORKDISK:// ?????????????????????
                    if (typeDisk == 1) {//??????
                        networkPubs.clear();
                    } else {// ??????
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
                        localMedia.setMimeType(1);// 1 ??????
                        localMedia.setCutPath(mImageName);
                        if (typeDisk == 1) {//??????
                            networkPubs.add(localMedia);
                        } else {// ??????
                            networkPris.add(localMedia);
                        }
                    }
                    break;
            }
            adapter.selectList.addAll(pictureLists);
            adapter.selectList.addAll(networkPubs);
            adapter.selectList.addAll(networkPris);
            ExecutorService executorService = Executors.newSingleThreadExecutor();//????????????????????????
            for (int i = 0; i < adapter.selectList.size(); i++) { // ?????????
                int finalI = i;//????????????????????????
                Runnable syncRunnable = () -> {
                    LocalMedia media = adapter.selectList.get(finalI);
                    String path = U.isEmpty(media.getCompressPath()) ? media.getPath() :
                            media.getCompressPath();
                    String pictureType = media.getPictureType();
                    String mImageName = media.getCutPath();
                    int type = media.getMimeType();
                    LogUtils.d("SendWorkActivity", path + "===" + mImageName);
                    //??????????????????
                    readyUpload(path, pictureType, mImageName, type);
                };
                executorService.execute(syncRunnable);
            }
            adapter.notifyDataSetChanged();
        }
    }

    //??????????????????
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
        final String items[] = {"??????????????????", "????????????????????????", "????????????????????????"};
        //???????????????
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


    // ??????????????????
    private void OpenPic() {
        // ????????????
        PictureSelector.create(SendInfoActivity.this)
                .openGallery(PictureMimeType.ofAll())// ?????????????????????????????????
                .theme(themeId)// ??????????????????
                .maxSelectNum(maxSelectNum)// ????????????????????????
                .minSelectNum(1)// ??????????????????
                .imageSpanCount(3)// ??????????????????
                .selectionMode(PictureConfig.MULTIPLE)// ??????
                .previewImage(true)// ?????????????????????
                .previewVideo(true)// ?????????????????????
                .enablePreviewAudio(true) // ?????????????????????
                .isCamera(true)// ????????????????????????
                .isZoomAnim(true)// ?????????????????? ???????????? ??????true
                .enableCrop(false)// ????????????
                .compress(false)// ????????????
                .synOrAsy(true)//??????true?????????false ?????? ????????????
                //.compressSavePath(getPath())//????????????????????????
                //.sizeMultiplier(0.5f)// glide ?????????????????? 0~1?????? ????????? .glideOverride()??????
                .glideOverride(160, 160)// glide ???????????????????????????????????????????????????????????????????????????????????????
                .isGif(true)// ????????????gif??????
                .openClickSound(true)// ????????????????????????
                .selectionMedia(adapter.selectList)// ????????????????????????
                .previewEggs(true)// ??????????????? ????????????????????????????????????(???????????????????????????????????????????????????)
                .minimumCompressSize(100)// ??????100kb??????????????????
                .forResult(PictureConfig.CHOOSE_REQUEST);//????????????onActivityResult code
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
