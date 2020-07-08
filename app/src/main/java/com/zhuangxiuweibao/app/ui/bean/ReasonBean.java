package com.zhuangxiuweibao.app.ui.bean;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReasonBean {
    private String reason;
    private boolean isSelect;
}
