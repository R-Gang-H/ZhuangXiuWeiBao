package com.zhuangxiuweibao.app.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.LubanOptions;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.SosContactsEntity;
import com.zhuangxiuweibao.app.bean.UserEntity;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.Constant;
import com.zhuangxiuweibao.app.common.user.ToUIEvent;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.GlideUtils;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.utils.alicloud.oss.AliYunOss;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.ui.adapter.EditContactAdapter;
import com.zxy.tiny.Tiny;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.zhuangxiuweibao.app.common.user.ToUIEvent.CONTACT_EVENT;

/**
 * 编辑个人信息
 */
public class EditInfoActivity extends BaseActivity implements TakePhoto.TakeResultListener, InvokeListener {

    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_title_left)
    TextView tvTitleLeft;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_avatar)
    RoundedImageView ivAvatar;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.tv_birthday)
    TextView tvBirthday;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    String mImageName = "";//user/avatar/时间戳+用户id.jpg
    AliYunOss mOssClient;

    private InvokeParam invokeParam;
    private TakePhoto takePhoto;

    public UserEntity userData;
    String sex = "1";//1 男 2 女
    String birthday = "";
    //Url
    String headImage = "";

    EditContactAdapter editAdapter;

    OptionsPickerView pvOptions;
    private List<String> list = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_info;
    }

    @Override
    public void initView() {
        ivLeft.setVisibility(View.GONE);
        tvTitleLeft.setVisibility(View.VISIBLE);
        rlNextButton.setVisibility(View.VISIBLE);
        ivTitleRight.setVisibility(View.GONE);
        tvTitle.setText("个人信息");
        tvTitleRight.setText("完成");
        mOssClient = new AliYunOss(this);

        LayoutManager.getInstance().init(this).initRecyclerView(recyclerView, true);
        editAdapter = new EditContactAdapter(new ArrayList(), this);
        recyclerView.setAdapter(editAdapter);
    }

    @Override
    public void initData() {
        userData = UserManager.getInstance().userData;
        headImage = userData.getHeadImage();

        GlideUtils.setGlideImg(this, headImage, R.mipmap.icon_avatar, ivAvatar);
        sex = userData.getSex();
        if (sex.equals("1")) {// 性别 1男 2女
            tvSex.setText("男");
        } else if (sex.equals("2")) {
            tvSex.setText("女");
        }
        birthday = userData.getBirthday();
        tvBirthday.setText(DateUtils.getDateToString(Long.parseLong(birthday), "yyyy-MM-dd"));
        editAdapter.setData(userData.getSosContacts());
    }

    @OnClick({R.id.rl_back_button, R.id.rl_avatar, R.id.tv_sex, R.id.tv_birthday, R.id.rl_add_contact, R.id.rl_next_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.rl_avatar:
                showPhotoDialog();
                break;
            case R.id.tv_sex:
                selectSex();
                break;
            case R.id.tv_birthday:
                getBirthday();
                break;
            case R.id.rl_add_contact:
                startActivity(new Intent(this, ContactActivity.class)
                        .putExtra("type", "1"));// 1 编辑个人信息
                break;
            case R.id.rl_next_button:
                if (validate()) {
                    return;
                }
                updateOwnInfo();
                break;
        }
    }

    private boolean validate() {
        if (U.isNotEmpty(mImageName)) {
            headImage = Constant.OSS_URL + mImageName;
        }
//        if (U.isEmpty(headImage)) {
//            U.showToast("请选择头像");
//            return true;
//        }
//        if (U.isEmpty(sex)) {
//            U.showToast("请选择性别");
//            return true;
//        }
        if (U.isEmpty(birthday)) {
            U.showToast("请选择生日");
            return true;
        }
        /*if (editAdapter.constactData.size() == 0) {
            U.showToast("请添加紧急联系人");
            return true;
        }*/
        return false;
    }

    private void updateOwnInfo() {//编辑个人信息
        String sosContact = new Gson().toJson(editAdapter.constactData);
        HttpManager.getInstance().doUpdateOwnInfo(this.getLocalClassName(), headImage, sex, birthday, sosContact,
                new HttpCallBack<BaseDataModel<UserEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        if (userData != null) {
                            userData.setBirthday(birthday);
                            userData.setHeadImage(headImage);
                            userData.setSex(sex);
                            userData.setSosContacts(editAdapter.constactData);
                        }
                        UserManager.getInstance().save(EditInfoActivity.this, userData);
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.USERINFO_EVENT));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("EditInfoActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(EditInfoActivity.this);
                            return;
                        }
                        U.showToast(errorMsg);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("EditInfoActivity", throwable.getMessage());
                    }
                });
    }

    /**
     * 选择孩子性别
     */
    private void selectSex() {
        String[] sexArr = {"男", "女"};
        //条件选择器
        if (pvOptions == null) {
            list.clear();
            list.addAll(Arrays.asList(sexArr));
            pvOptions = new OptionsPickerBuilder(this, (options1, option2, options3, v) -> {
                sex = options1 + 1 + "";
                tvSex.setText(list.get(options1));
            }).build();
            pvOptions.setPicker(list);
        }
        pvOptions.show();
    }

    private void getBirthday() {
        new TimePickerBuilder(this, (date, v) -> {
            birthday = String.valueOf(date.getTime() / 1000);
            tvBirthday.setText(DateUtils.getDateToString(date.getTime() / 1000, "yyyy-MM-dd"));
        })
                .setType(new boolean[]{true, true, true, false, false, false})
                .setRangDate(null, Calendar.getInstance())
                .setLabel("年", "月", "日", "",
                        "", "")
                .build().show();
    }

    private void showPhotoDialog() {
        final String items[] = {"拍照", "相册"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择头像");
        builder.setItems(items, (dialog, which) -> {
            dialog.dismiss();
            switch (which) {
                case 0:
                    if (!selfPermissionGranted(Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CAMERA}, 120);
                    } else {
                        takeOrPickPhoto(true);
                    }
                    break;
                case 1:
                    takeOrPickPhoto(false);
                    break;
                default:
                    break;
            }
        });
        builder.create().show();
    }

    private void takeOrPickPhoto(boolean isTakePhoto) {
        File file = new File(Environment.getExternalStorageDirectory() + "/head.jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        TakePhoto takePhoto = getTakePhoto();
        configCompress(takePhoto); // 压缩处理
        configTakePhotoOption(takePhoto);
        if (isTakePhoto) { // 拍照
            takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());
        } else { // 选取图片
            takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions());
        }
    }

    /**
     * 配置 裁剪选项
     *
     * @return
     */
    private CropOptions getCropOptions() {
        int height = 100;
        int width = 100;

        CropOptions.Builder builder = new CropOptions.Builder();

        //按照宽高比例裁剪
        builder.setAspectX(width).setAspectY(height);
        //是否使用Takephoto自带的裁剪工具
        builder.setWithOwnCrop(false);
        return builder.create();
    }

    /**
     * 拍照相关的配置
     *
     * @param takePhoto
     */
    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        //是否使用Takephoto自带的相册
        if (false) {
            builder.setWithOwnGallery(true);
        }
        //纠正拍照的旋转角度
        if (true) {
            builder.setCorrectImage(true);
        }
        takePhoto.setTakePhotoOptions(builder.create());
    }

    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //以下代码为处理Android6.0、7.0动态权限所需
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    @Override
    public void takeSuccess(TResult result) {
        // 拍照或者选图成功
        String mHeaderAbsolutePath = result.getImages().get(0).getOriginalPath();
        Logger.d(mHeaderAbsolutePath);

        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        Tiny.getInstance().source(mHeaderAbsolutePath).asFile().withOptions(options).compress((isSuccess, outfile, t) -> {
            //return the compressed file path

            new Handler().post(() -> Glide.with(this).load(outfile).into(ivAvatar));

            mImageName = Constant.photo + DateUtils.getCurTimeLong("yyyyMMddHHmmss") + UserManager.getInstance().userData.getUid() + ".jpg";
            //文件名
            mOssClient.upload(mImageName, outfile, null);
        });
    }

    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {

    }

    /**
     * 配置 压缩选项
     *
     * @param takePhoto
     */
    private void configCompress(TakePhoto takePhoto) {
        int maxSize = 102400;
        int width = 800;
        int height = 800;
        //是否显示 压缩进度条
        boolean showProgressBar = true;
        //压缩后是否保存原图
        boolean enableRawFile = true;
        CompressConfig config;
        if (false) {
            //使用自带的压缩工具
            config = new CompressConfig.Builder()
                    .setMaxSize(maxSize)
                    .setMaxPixel(width >= height ? width : height)
                    .enableReserveRaw(enableRawFile)
                    .create();
        } else {
            //使用开源的鲁班压缩工具
            LubanOptions option = new LubanOptions.Builder()
                    .setMaxHeight(height)
                    .setMaxWidth(width)
                    .setMaxSize(maxSize)
                    .create();
            config = CompressConfig.ofLuban(option);
            config.enableReserveRaw(enableRawFile);
        }
        takePhoto.onEnableCompress(config, showProgressBar);
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        if (event.getTag() == CONTACT_EVENT) {
            editAdapter.constactData.add((SosContactsEntity) event.getObj());
            editAdapter.notifyDataSetChanged();
        }

    }
}
