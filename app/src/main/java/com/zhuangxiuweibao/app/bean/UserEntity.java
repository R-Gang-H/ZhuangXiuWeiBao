package com.zhuangxiuweibao.app.bean;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Created by haoruigang on 2018/3/5.
 */
@Data
public class UserEntity implements Serializable {

    private String coopen;

    private String code;

    private String uid;
    private String token;
    private String mobile;
    private String headImage;
    private String name;
    private String sex;
    private String birthday;
    private String qrcode;
    private String isHuzhu;//1 是户主 2不是户主
    private String houseName;//
    private String houseId;//
    private String xiaoquId;//
    private List<SosContactsEntity> sosContacts;
    private String isNew;//是否注册 1已注册，2未注册
    private String firstIdentity;//主用户角色 1住户 2工作人员 3”大妈”（从住户中挑选，赋予工作人员权限） 用户身份未确定此字段为空
    private String secondIdentity;//用户角色细分 1户主 2家庭成员 3普通员工 4维保人员 5社区负责人 6客服 7系统管理员 8”大妈”（从住户中挑选，赋予工作人员权限）用户身份未确定此字段为空
    private String department;

    private String userId;

}
