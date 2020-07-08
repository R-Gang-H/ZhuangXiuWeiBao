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
 * 发任务
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
    //装在所有动态添加的Item的LinearLayout容器
    @BindView(R.id.ll_disclosure)
    LinearLayout addHotelNameView;
    @BindView(R.id.circle_progress_fill_in)
    CircleProgressView mCircleProgress;

    private String startTimes;
    private CheckerAdapter checkerAdapter, uploadPeopleAdapter;
    private ArrayList<String> copytoName = new ArrayList<>(), checkerId = new ArrayList<>(), checkerName = new ArrayList<>(), copytoId = new ArrayList<>();
    //                              存抄送人名字                  存抄送人Id                      存审批人名字                      存审批人ID
    private int SELECT_PEOPLE = 0, SELECT_TYPE;
    private StringBuffer checkerIds = new StringBuffer();
    private StringBuffer copytoIds = new StringBuffer();
    private String linkUrl;
    List<LocalMedia> pictureLists = new ArrayList<>(), networkPris = new ArrayList<>(), networkPubs = new ArrayList<>();
    private List<DisclosureEntity> discLists = new ArrayList<>();
    ;
    private List<BatchEntity.ItemsBean> itemsBeans;
    private int indexPos;
    private String tId;//技术交底Id
    private int typeDisk = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_send_work;
    }

    @Override
    public void initView() {
        tvTitle.setText("发任务");
        indexPos = getIntent().getIntExtra("index", 0);
        BatchEntity entity = (BatchEntity) getIntent().getSerializableExtra("entity");
        if (indexPos != 1) { // 技术交底任务
            discLists.clear();
            itemsBeans = entity.getItems();
            tId = entity.getTid();
            llWork.setVisibility(View.GONE);
            addHotelNameView.setVisibility(View.VISIBLE);
            if (itemsBeans.size() > 0) {
                addViewItem();
            }
        } else {// 普通任务
            initList();
            initCheckList();//选择执行人
            initCopyto();//选择抄送人
        }
    }

    //添加ViewItem
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
     * Item排序
     */
    private void sortHotelViewItem() {
        //获取LinearLayout里面所有的view
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

    //其他审批需要选择审批人
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

    //选择抄送人
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
        mOssClient = AliYunOss.getInstance(this);//初始化阿里云OS
    }

    private int themeId;//主题样式
    private GridImageAdapter adapter;//图片适配器

    private int maxSelectNum = 5;//最大选择图片张数

    private void initList() {
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
        PictureSelector.create(SendWorkActivity.this)
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

    //准备上传参数
    private void readyUpload(String path, String pictureType, String mImageName, int type) {
        adapter.pngOravis.add(type == 0 ? Constant.OSS_URL + mImageName : path);
        adapter.uploadModules.add(UploadModule.builder().picPath(path)
                .pictureType(pictureType).uploadObject(mImageName).build());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
        if (requestCode == SELECT_PEOPLE && resultCode == 1) {//选择完抄送人，
            String name = data.getStringExtra("name");
            String uid = data.getStringExtra("uid");
            if (SELECT_TYPE == 4) {//选择审批人
                checkerName.add(name);
                checkerId.add(uid);
                checkerAdapter.update(checkerName, "0");
            } else if (SELECT_TYPE == 3) {//选择抄送人
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
                //获取LinearLayout里面所有的view
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
    boolean isBackPressed = true;// 控制返回键是否能受控制
    AliYunOss mOssClient;

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
                LogUtils.e("SendWorkActivity", "---onProgress:---" + (int) (currentSize * 100) / totalSize);
                mCircleProgress.setProgress((int) ((currentSize * 100) / totalSize));
            }

            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                LogUtils.e("SendWorkActivity", "---onSuccess:---");
                position++;
                //全部上传完成后提交接口并刷新
                if (adapter.uploadModules.size() == position) {
                    LogUtils.e("SendWorkActivity",
                            "----->上传:" + adapter.pngOravis.toString());
                    runOnUiThread(() ->
                            releaseWork());//主线程
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException
                    clientException, ServiceException serviceException) {
                LogUtils.e("SendWorkActivity", "---onFailure:---");
            }
        };
    }

    //发布一个审批
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
//        23 发布一个任务
        HttpManager.getInstance().releaseWork("FixedApprovalActivity", checkerIds.toString(), copytoIds.toString(),
                etTitle.getText().toString(), etDescription.getText().toString(), pngOravis.toString(),
                startTimes, linkUrl, new HttpCallBack<BaseDataModel>() {
                    @Override
                    public void onSuccess(BaseDataModel data) {
                        U.showToast("发布成功");
                        reset();
                        finish();
                    }


                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
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
                        .setLabel("年", "月", "日", "时",
                                "分", "")
                        .build().show();
                break;
            case R.id.btnSubmit:
                if (validation()) {
                    btnSubmit.setEnabled(false);
                    mCircleProgress.setVisibility(View.VISIBLE);
                    rlBackButton.setClickable(false);
                    isBackPressed = false;
                    if (indexPos != 1) { // 技术交底任务
                        sendDisclosureTask();
                    } else {// 普通任务
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
                                                        releaseWork());//主线程
                                            }
                                        }
                                    }//子线程
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
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
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
        //判断条件
        if (TextUtils.isEmpty(etTitle.getText().toString())) {
            U.showToast("请输入标题");
            return false;
        }
        if (TextUtils.isEmpty(etDescription.getText().toString())) {
            U.showToast("请输入描述");
            return false;
        }
        if (U.isEmpty(tvEndTime.getText())) {
            U.showToast("请选择截止日期");
            return false;
        }
        if (indexPos == 1) {// 普通任务
//        if (adapter.selectList.size() == 0) {
//            U.showToast("请选择图片");
//            return false;
//        }
            //判断条件
            if (TextUtils.isEmpty(checkerIds.toString())) {
                if (checkerId.size() != 0) {
                    for (int i = 0; i < checkerId.size(); i++) {
                        checkerIds.append(checkerId.get(i) + ",");
                    }
                    checkerIds.deleteCharAt(checkerIds.length() - 1);
                }
            }
            if (checkerIds.length() == 0) {
                U.showToast("请选择执行人");
                return false;
            }
            for (int i = 0; i < copytoName.size(); i++) {
                copytoIds.append(copytoId.get(i) + ",");
            }
            if (copytoIds.length() > 0) {
//            U.showToast("请选择抄送人");
                copytoIds.deleteCharAt(copytoIds.length() - 1).toString();
            }
            linkUrl = etLink.getText().toString();
        } else {
            for (DisclosureEntity disc : discLists) {
                if (U.isEmpty(disc.getDoerId())) {
                    U.showToast("有执行人未选择");
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
        // 这儿做返回键的控制，如果自己处理返回键逻辑就返回true，如果返回false,代表继续向下传递back事件，由系统去控制
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
