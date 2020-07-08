package com.zhuangxiuweibao.app.ui.bean;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserBean {
    
    private int resId;//身份图标id
    private String userText;//身份名称
    private boolean isSelect;//选中的身份
    private boolean isCheck;//设置不能点击
    private String pid;//同事id
    private String name;
    private String mobile;
    private String cid;//审批人id
    private String uid;//联系人id
    private String headImage;//联系人id
    private String isHaveChildGroup;//是否拥有子组织 1有 2没有
    private String groupId;//架构Id
    private String isCompany;//1表示公司 2表示部门
    private String content;//
    private String createAt;//
    private String title;//

}
