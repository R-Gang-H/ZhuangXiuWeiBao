package com.zhuangxiuweibao.app.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class DisclosureEntity implements Serializable {

    private String itemId;
    private String itemName;
    private String doerId;
    private String doreName;

}
