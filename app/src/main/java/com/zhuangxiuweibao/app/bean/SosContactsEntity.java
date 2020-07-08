package com.zhuangxiuweibao.app.bean;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;


/**
 * Date: 2019/4/19
 * Author: haoruigang
 * Description: com.zhuangxiuweibao.app.bean
 */
@Builder
@Data
public class SosContactsEntity implements Serializable {

    private String contactId;
    private String name;
    private String mobile;
    private String headImage;
    private String relationName;

}
