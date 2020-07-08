package com.zhuangxiuweibao.app.ui.bean;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DemandBean implements Serializable {
    private String name;
    private String id;
    private String mobile;
    private boolean isSelect;
}
