package com.zhuangxiuweibao.app.bean;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApprovalEntity {
    private boolean isFinish;
    private String name;
    private String status;
    private String time;
    private String img;
    private String mobile;
    private String createAt;
    private String branchName;
    private String userId;
    private String Introduction;
}
