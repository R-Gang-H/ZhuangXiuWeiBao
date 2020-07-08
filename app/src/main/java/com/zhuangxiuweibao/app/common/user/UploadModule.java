package com.zhuangxiuweibao.app.common.user;

import java.io.Serializable;

import lombok.Builder;

/**
 * Created by haoruigang on 2018/4/17 11:24.
 */
@Builder
public class UploadModule implements Serializable {

    private String picPath;
    private String pictureType;
    private String uploadObject;

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getPictureType() {
        return pictureType;
    }

    public void setPictureType(String pictureType) {
        this.pictureType = pictureType;
    }

    public String getUploadObject() {
        return uploadObject;
    }

    public void setUploadObject(String uploadObject) {
        this.uploadObject = uploadObject;
    }
}
