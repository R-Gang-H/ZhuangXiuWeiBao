package com.zhuangxiuweibao.app.bean;

/**
 * Created by haoruigang on 2017/7/27.
 */
public class BaseBeanModel {
    private String errorMsg;
    private int status;

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
