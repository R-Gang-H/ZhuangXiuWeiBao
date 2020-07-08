package com.zhuangxiuweibao.app.bean;

import java.util.ArrayList;

/**
 * Created by haoruigang on 2017/11/7.
 */

public class BaseDataModel<T> extends BaseBeanModel {
    public ArrayList<T> getData() {
        return data;
    }

    public void setData(ArrayList<T> data) {
        this.data = data;
    }

    private ArrayList<T> data;

}
