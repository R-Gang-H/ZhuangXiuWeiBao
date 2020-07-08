package com.zhuangxiuweibao.app.bean;

/**
 * Created by haoruigang on 2018-4-3 15:02:34.
 */

public class BaseModel<T> extends BaseBeanModel {

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private T data;

}
