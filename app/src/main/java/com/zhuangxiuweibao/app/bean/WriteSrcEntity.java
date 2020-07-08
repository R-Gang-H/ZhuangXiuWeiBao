package com.zhuangxiuweibao.app.bean;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WriteSrcEntity implements Serializable {

    private String srcid;
    private String value;
}
