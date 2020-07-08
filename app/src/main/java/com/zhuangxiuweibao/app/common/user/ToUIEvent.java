package com.zhuangxiuweibao.app.common.user;

import lombok.Data;

@Data
public class ToUIEvent {

    public static final int MESSAGE_EVENT = 0;        //消息刷新
    public static final int CONTACT_EVENT = 1;        //家庭成员
    public static final int USERINFO_EVENT = 2;        //个人信息
    public static final int VODIO_EVENT = 3;        //l录音
    public static final int DEL_DIS_EVENT = 4;        //删除技术交底执行人


    private int tag;
    private Object obj;

    public ToUIEvent(int tag) {
        this.tag = tag;
    }

    public ToUIEvent(int tag, Object obj) {
        this.tag = tag;
        this.obj = obj;
    }

}
