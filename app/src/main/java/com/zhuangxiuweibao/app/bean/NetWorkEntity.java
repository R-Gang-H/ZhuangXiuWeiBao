package com.zhuangxiuweibao.app.bean;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NetWorkEntity implements Serializable {

    /**
     * srcId : 1
     * srcName : Chrysanthemum.jpg
     * srcType : jpg
     * srcUrl : http://www.123.com/Chrysanthemum.jpg
     */

    private String srcId;
    private String srcName;
    private String srcType;
    private String srcUrl;
    private boolean isSelect;//选中的身份

}
