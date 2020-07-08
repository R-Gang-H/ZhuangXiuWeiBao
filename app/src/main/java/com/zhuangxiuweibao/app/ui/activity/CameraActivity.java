package com.zhuangxiuweibao.app.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jaeger.library.StatusBarUtil;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.http.OkHttpUtils;
import com.zhuangxiuweibao.app.common.user.Constant;
import com.zhuangxiuweibao.app.common.utils.BitmapUtils;
import com.zhuangxiuweibao.app.common.utils.ResourcesUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.utils.permissions.PermissionCallBackM;
import com.zhuangxiuweibao.app.ui.widget.MySurfaceView;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static org.apache.commons.codec.binary.Base64.encodeBase64;

public class CameraActivity extends BaseActivity {

    @BindView(R.id.sur_idcard)
    MySurfaceView msurIdcard;
    @BindView(R.id.cb_torch)
    CheckBox cbTorch;
    @BindView(R.id.img_take)
    ImageView imgTake;

    private String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public int getLayoutId() {
        return R.layout.activity_camera;
    }

    @Override
    public void initView() {
        StatusBarUtil.setTranslucentForImageView(this, 0, null);
        requestPermission(Constant.RC_AUDIO, PERMISSIONS, getString(R.string.camera),
                new PermissionCallBackM() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionGrantedM(int requestCode, String... perms) {
                        if (!msurIdcard.isCameraCanUse()) {
                            U.showToast("相机权限被拒绝,请前往设置页面进行设置");
                            finish();
                        }
                    }

                    @Override
                    public void onPermissionDeniedM(int requestCode, String... perms) {
                        U.showToast("请求权限被拒绝");
                        finish();
                    }
                });
    }

    @Override
    public void initData() {
        cbTorch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            /*
             * 打开 true 关闭 false 闪光灯
             */
            msurIdcard.isLightEnable(isChecked);
            cbTorch.setText(String.format("%s手电筒", isChecked ? "关闭" : "打开"));
        });
    }

    /*
     * 获取参数的json对象
     */
    private JSONObject getParam(int type, String dataValue) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("dataType", type);
            obj.put("dataValue", dataValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 请求身份证识别结果
     */
    private void request(String paths) {

        String host = "http://dm-51.data.aliyun.com";
        String path = "/rest/160601/ocr/ocr_idcard.json";
        String appcode = "13c5e1af01034b8aa0eb1a5dc4c11d03";
        String imgFile = paths;

        //请根据线上文档修改configure字段
        JSONObject configObj = new JSONObject();
        configObj.put("side", "face");
        String config_str = configObj.toString();
        //            configObj.put("min_size", 5);
        //            String config_str = "";

        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);

        Map<String, String> querys = new HashMap<String, String>();

        // 对图像进行base64编码
        String imgBase64 = "";
        try {
            File file = new File(imgFile);
            byte[] content = new byte[(int) file.length()];
            FileInputStream finputstream = new FileInputStream(file);
            finputstream.read(content);
            finputstream.close();
            imgBase64 = new String(encodeBase64(content));
        } catch (IOException e) {
            e.printStackTrace();
            imgTake.setEnabled(true);
            return;
        }
        // 拼装请求body的json字符串
        JSONObject requestObj = new JSONObject();
        try {
            requestObj.put("image", imgBase64);
            if (config_str.length() > 0) {
                requestObj.put("configure", config_str);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            imgTake.setEnabled(true);
            return;
        }
        String bodys = requestObj.toString();
        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = OkHttpUtils.doPost(host, path, method, headers, querys, bodys);
            int stat = response.getStatusLine().getStatusCode();
            if (stat != 200) {
                System.out.println("Http code: " + stat);
                System.out.println("http header error msg: " + response.getFirstHeader("X-Ca-Error-Message"));
                System.out.println("Http body error msg:" + EntityUtils.toString(response.getEntity()));
                runOnUiThread(() -> {
                    U.showToast("未识别，请将身份证放入扫描框内");
                    msurIdcard.restore();
                });
                imgTake.setEnabled(true);
                return;
            }

            String res = EntityUtils.toString(response.getEntity());
            JSONObject res_obj = JSON.parseObject(res);
            System.out.println(res_obj.toJSONString());
            runOnUiThread(() -> {
                try {
                    org.json.JSONObject jo = new org.json.JSONObject(res_obj.toString());
                    String[] str = {jo.optString("name"), jo.optString("num"), jo.optString("birth")};
                    Intent intent = new Intent();
                    intent.putExtra("idCards", str);
                    this.setResult(3, intent);
                    finish();
                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                    imgTake.setEnabled(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            imgTake.setEnabled(true);
        }
    }


    @OnClick({R.id.img_return, R.id.img_take})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                break;
            case R.id.img_take:
                imgTake.setEnabled(false);
                msurIdcard.takePicture(bitmap -> {
                    new Thread(() -> {
                        request(ResourcesUtils.getImageAbsolutePath(this,
                                BitmapUtils.getInstance().bitmap2Uri(this, bitmap)));
                    }).start();
                });
                break;
        }
    }
}
