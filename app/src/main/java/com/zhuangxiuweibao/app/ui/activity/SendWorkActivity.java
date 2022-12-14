package com.zhuangxiuweibao.app.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.white.progressview.CircleProgressView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.BatchEntity;
import com.zhuangxiuweibao.app.bean.DisclosureEntity;
import com.zhuangxiuweibao.app.bean.NetWorkEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.Constant;
import com.zhuangxiuweibao.app.common.user.ToUIEvent;
import com.zhuangxiuweibao.app.common.user.UploadModule;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.ResourcesUtils;
import com.zhuangxiuweibao.app.common.utils.ThreadPoolManager;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.utils.alicloud.oss.AliYunOss;
import com.zhuangxiuweibao.app.common.utils.alicloud.oss.callback.ProgressCallback;
import com.zhuangxiuweibao.app.common.utils.permissions.PermissionCallBackM;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.FullyGridLayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.ItemOffsetDecoration;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.ui.adapter.CheckerAdapter;
import com.zhuangxiuweibao.app.ui.adapter.GridImageAdapter;
import com.zxy.tiny.Tiny;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
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

/**
 * ?????????
 */
public class SendWorkActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.btnSubmit)
    TextView btnSubmit;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.etDescription)
    EditText etDescription;
    @BindView(R.id.et_link)
    EditText etLink;
    @BindView(R.id.tvEndTime)
    TextView tvEndTime;
    @BindView(R.id.imgList)
    RecyclerView imgList;
    @BindView(R.id.selectList)
    RecyclerView selectList;
    @BindView(R.id.checkList)
    RecyclerView checkList;

    @BindView(R.id.ll_work)
    LinearLayout llWork;
    //???????????????????????????Item???LinearLayout??????
    @BindView(R.id.ll_disclosure)
    LinearLayout addHotelNameView;
    @BindView(R.id.circle_progress_fill_in)
    CircleProgressView mCircleProgress;

    private String startTimes;
    private CheckerAdapter checkerAdapter, uploadPeopleAdapter;
    private ArrayList<String> copytoName = new ArrayList<>(), checkerId = new ArrayList<>(), checkerName = new ArrayList<>(), copytoId = new ArrayList<>();
    //                              ??????????????????                  ????????????Id                      ??????????????????                      ????????????ID
    private int SELECT_PEOPLE = 0, SELECT_TYPE;
    private StringBuffer checkerIds = new StringBuffer();
    private StringBuffer copytoIds = new StringBuffer();
    private String linkUrl;
    List<LocalMedia> pictureLists = new ArrayList<>(), networkPris = new ArrayList<>(), networkPubs = new ArrayList<>();
    private List<DisclosureEntity> discLists = new ArrayList<>();
    ;
    private List<BatchEntity.ItemsBean> itemsBeans;
    private int indexPos;
    private String tId;//????????????Id
    private int typeDisk = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_send_work;
    }

    @Override
    public void initView() {
        tvTitle.setText("?????????");
        indexPos = getIntent().getIntExtra("index", 0);
        BatchEntity entity = (BatchEntity) getIntent().getSerializableExtra("entity");
        if (indexPos != 1) { // ??????????????????
            discLists.clear();
            itemsBeans = entity.getItems();
            tId = entity.getTid();
            llWork.setVisibility(View.GONE);
            addHotelNameView.setVisibility(View.VISIBLE);
            if (itemsBeans.size() > 0) {
                addViewItem();
            }
        } else {// ????????????
            initList();
            initCheckList();//???????????????
            initCopyto();//???????????????
        }
    }

    //??????ViewItem
    private void addViewItem() {
        for (int i = 0; i < itemsBeans.size(); i++) {
            LinearLayout view = (LinearLayout) LayoutInflater.from(this)
                    .inflate(R.layout.item_disclosure, null);
            addHotelNameView.addView(view);
            sortHotelViewItem();
            discLists.add(new DisclosureEntity());
        }
    }

    /**
     * Item??????
     */
    private void sortHotelViewItem() {
        //??????LinearLayout???????????????view
        for (int i = 0; i < addHotelNameView.getChildCount(); i++) {
            final View childAt = addHotelNameView.getChildAt(i);
            final TextView tvName = childAt.findViewById(R.id.tvName);
            tvName.setText(itemsBeans.get(i).getItemName());
            final TextView imgName = childAt.findViewById(R.id.img_name);
            int finalI = i;
            imgName.setOnClickListener(v -> {
                SELECT_TYPE = 5;
                startActivityForResult(new Intent(SendWorkActivity.this, SelectPersonActivity.class)
                                .putExtra("type", SELECT_TYPE + "")
                                .putExtra("discList", (Serializable) discLists)
                                .putExtra("position", finalI)
                                .putExtra("isTaskNeed", true)
                        , SELECT_PEOPLE);
            });
        }
    }

    //?????????????????????????????????
    private void initCheckList() {
        LayoutManager.getInstance().init(this).initRecyclerGrid(checkList, 3);
        checkerAdapter = new CheckerAdapter(this, new CheckerAdapter.OnItemClickListener() {
            @Override
            public void onDel(int position) {
                checkerName.remove(position);
                checkerId.remove(position);
                checkerAdapter.update(checkerName, "0");
            }

            @Override
            public void onAdd() {
                SELECT_TYPE = 4;
                startActivityForResult(new Intent(SendWorkActivity.this, SelectPersonActivity.class)
                                .putExtra("type", SELECT_TYPE + "")
                                .putExtra("copytoId", copytoId)
                                .putExtra("checkerId", checkerId)
                                .putExtra("isTaskNeed", true)
                        , SELECT_PEOPLE);
            }
        });
        checkList.setAdapter(checkerAdapter);
        checkerAdapter.update(checkerName, "0");
    }

    //???????????????
    private void initCopyto() {
        LayoutManager.getInstance().init(this).initRecyclerGrid(selectList, 3);
        uploadPeopleAdapter = new CheckerAdapter(this, new CheckerAdapter.OnItemClickListener() {
            @Override
            public void onDel(int position) {
                copytoName.remove(position);
                copytoId.remove(position);
                uploadPeopleAdapter.update(copytoName, "0");
            }

            @Override
            public void onAdd() {
                SELECT_TYPE = 3;
                startActivityForResult(new Intent(SendWorkActivity.this, SelectPersonActivity.class)
                                .putExtra("type", SELECT_TYPE + "")
                                .putExtra("copytoId", copytoId)
                                .putExtra("checkerId", checkerId)
                                .putExtra("isTaskNeed", true)
                        , SELECT_PEOPLE);
            }
        });
        selectList.setAdapter(uploadPeopleAdapter);
        uploadPeopleAdapter.update(copytoName, "0");
    }

    @Override
    public void initData() {
        mOssClient = AliYunOss.getInstance(this);//??????????????????OS
    }

    private int themeId;//????????????
    private GridImageAdapter adapter;//???????????????

    private int maxSelectNum = 5;//????????????????????????

    private void initList() {
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
                                        FileDisplayActivity.actionStart(SendWorkActivity.this, media.getPath(), media.getCutPath());
                                    }

                                    @Override
                                    public void onPermissionDeniedM(int requestCode, String... perms) {
                                        LogUtils.e(SendWorkActivity.this, "TODO: WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT);
                                    }
                                });
                        break;
                }
            }
        });
    }

    OptionsPickerView pvOptions;
    private List<String> list = new ArrayList<>();
    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            U.hideKeyboard(SendWorkActivity.this);
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
        PictureSelector.create(SendWorkActivity.this)
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

    //??????????????????
    private void readyUpload(String path, String pictureType, String mImageName, int type) {
        adapter.pngOravis.add(type == 0 ? Constant.OSS_URL + mImageName : path);
        adapter.uploadModules.add(UploadModule.builder().picPath(path)
                .pictureType(pictureType).uploadObject(mImageName).build());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
        if (requestCode == SELECT_PEOPLE && resultCode == 1) {//?????????????????????
            String name = data.getStringExtra("name");
            String uid = data.getStringExtra("uid");
            if (SELECT_TYPE == 4) {//???????????????
                checkerName.add(name);
                checkerId.add(uid);
                checkerAdapter.update(checkerName, "0");
            } else if (SELECT_TYPE == 3) {//???????????????
                copytoName.add(name);
                copytoId.add(uid);
                uploadPeopleAdapter.update(copytoName, "0");
            } else if (SELECT_TYPE == 5) {
                int pos = data.getIntExtra("position", -1);
                DisclosureEntity disEntity = new DisclosureEntity();
                disEntity.setItemId(itemsBeans.get(pos).getItemId());
                disEntity.setItemName(itemsBeans.get(pos).getItemName());
                disEntity.setDoerId(uid);
                disEntity.setDoreName(name);
                //??????LinearLayout???????????????view
                for (int i = 0; i < addHotelNameView.getChildCount(); i++) {
                    if (i == pos) {
                        final View childAt = addHotelNameView.getChildAt(i);
                        final TextView imgName = childAt.findViewById(R.id.img_name);
                        final ImageView imgDel = childAt.findViewById(R.id.img_del);
                        imgName.setBackgroundColor(ResourcesUtils.getColor(R.color.color_f6));
                        imgName.setText(name);
                        imgDel.setVisibility(View.VISIBLE);
                        imgDel.setOnClickListener(v -> {
                            imgName.setBackgroundResource(R.mipmap.ic_add);
                            imgName.setText("");
                            imgDel.setVisibility(View.GONE);
                            EventBus.getDefault().post(new ToUIEvent(ToUIEvent.DEL_DIS_EVENT, pos));
                        });
                        discLists.set(i, disEntity);
                        break;
                    }
                }
            }
        }
    }

    int position = 0;
    float index = 0;
    boolean isProgress = true;
    boolean isBackPressed = true;// ?????????????????????????????????
    AliYunOss mOssClient;

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
                LogUtils.e("SendWorkActivity", "---onProgress:---" + (int) (currentSize * 100) / totalSize);
                mCircleProgress.setProgress((int) ((currentSize * 100) / totalSize));
            }

            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                LogUtils.e("SendWorkActivity", "---onSuccess:---");
                position++;
                //??????????????????????????????????????????
                if (adapter.uploadModules.size() == position) {
                    LogUtils.e("SendWorkActivity",
                            "----->??????:" + adapter.pngOravis.toString());
                    runOnUiThread(() ->
                            releaseWork());//?????????
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException
                    clientException, ServiceException serviceException) {
                LogUtils.e("SendWorkActivity", "---onFailure:---");
            }
        };
    }

    //??????????????????
    private void releaseWork() {
        StringBuilder pngOravis = new StringBuilder();
        boolean isExcut = false;
        for (String url : adapter.pngOravis) {
            if (isExcut) {
                pngOravis.append("#");
            }
            pngOravis.append(url);
            isExcut = true;
        }
//        23 ??????????????????
        HttpManager.getInstance().releaseWork("FixedApprovalActivity", checkerIds.toString(), copytoIds.toString(),
                etTitle.getText().toString(), etDescription.getText().toString(), pngOravis.toString(),
                startTimes, linkUrl, new HttpCallBack<BaseDataModel>() {
                    @Override
                    public void onSuccess(BaseDataModel data) {
                        U.showToast("????????????");
                        reset();
                        finish();
                    }


                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(SendWorkActivity.this);
                            return;
                        }
                        reset();
                    }


                    @Override
                    public void onError(Throwable throwable) {
                        reset();
                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.btnSubmit, R.id.tvEndTime})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tvEndTime:
                U.hideKeyboard(this);
                new TimePickerBuilder(this, (date, v) -> {
                    startTimes = String.valueOf(date.getTime() / 1000);
                    tvEndTime.setText(DateUtils.getDateToString(date.getTime() / 1000, "yyyy.MM.dd HH:mm"));
                    tvEndTime.setTextColor(Color.parseColor("#333333"));
                })
                        .setType(new boolean[]{true, true, true, true, true, false})
                        .setRangDate(Calendar.getInstance(), null)
                        .setLabel("???", "???", "???", "???",
                                "???", "")
                        .build().show();
                break;
            case R.id.btnSubmit:
                if (validation()) {
                    btnSubmit.setEnabled(false);
                    mCircleProgress.setVisibility(View.VISIBLE);
                    rlBackButton.setClickable(false);
                    isBackPressed = false;
                    if (indexPos != 1) { // ??????????????????
                        sendDisclosureTask();
                    } else {// ????????????
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
                                                        releaseWork());//?????????
                                            }
                                        }
                                    }//?????????
                                }
                            };
                            ThreadPoolManager.getInstance().execute(new FutureTask<>(thread, null), null);
                        } else {
                            releaseWork();
                        }
                    }
                }
                break;
        }
    }

    private void sendDisclosureTask() {
        String item = new Gson().toJson(discLists);
        HttpManager.getInstance().doSendDisclosureTask("SendWorkActivity", etTitle.getText().toString(),
                etDescription.getText().toString(), startTimes, tId, item, new HttpCallBack<BaseDataModel>() {
                    @Override
                    public void onSuccess(BaseDataModel data) {
                        reset();
                        finish();
                    }


                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(SendWorkActivity.this);
                            return;
                        }
                        reset();
                    }


                    @Override
                    public void onError(Throwable throwable) {
                        reset();
                    }
                });
    }

    private boolean validation() {
        //????????????
        if (TextUtils.isEmpty(etTitle.getText().toString())) {
            U.showToast("???????????????");
            return false;
        }
        if (TextUtils.isEmpty(etDescription.getText().toString())) {
            U.showToast("???????????????");
            return false;
        }
        if (U.isEmpty(tvEndTime.getText())) {
            U.showToast("?????????????????????");
            return false;
        }
        if (indexPos == 1) {// ????????????
//        if (adapter.selectList.size() == 0) {
//            U.showToast("???????????????");
//            return false;
//        }
            //????????????
            if (TextUtils.isEmpty(checkerIds.toString())) {
                if (checkerId.size() != 0) {
                    for (int i = 0; i < checkerId.size(); i++) {
                        checkerIds.append(checkerId.get(i) + ",");
                    }
                    checkerIds.deleteCharAt(checkerIds.length() - 1);
                }
            }
            if (checkerIds.length() == 0) {
                U.showToast("??????????????????");
                return false;
            }
            for (int i = 0; i < copytoName.size(); i++) {
                copytoIds.append(copytoId.get(i) + ",");
            }
            if (copytoIds.length() > 0) {
//            U.showToast("??????????????????");
                copytoIds.deleteCharAt(copytoIds.length() - 1).toString();
            }
            linkUrl = etLink.getText().toString();
        } else {
            for (DisclosureEntity disc : discLists) {
                if (U.isEmpty(disc.getDoerId())) {
                    U.showToast("?????????????????????");
                    return false;
                }
            }
        }
        return true;
    }

    private void reset() {
        btnSubmit.setEnabled(true);
        mCircleProgress.setVisibility(View.GONE);
        rlBackButton.setClickable(true);
        isBackPressed = true;
    }

    @Override
    public void onBackPressed() {
        if (!isCosumenBackKey()) {
            return;
        }
        super.onBackPressed();
    }

    private boolean isCosumenBackKey() {
        // ????????????????????????????????????????????????????????????????????????true???????????????false,????????????????????????back???????????????????????????
        return isBackPressed;
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        if (event.getTag() == ToUIEvent.DEL_DIS_EVENT) {
            discLists.set((int) event.getObj(), new DisclosureEntity());
        }
    }
}
