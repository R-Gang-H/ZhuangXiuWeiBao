package com.zhuangxiuweibao.app.bean;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Date: 2019/4/11
 * Author: haoruigang
 * Description: com.zhuangxiuweibao.app.bean
 */
@Data
public class MessageEntity implements Serializable {

    private String eventId;
    private String type;
    private String type2;
    private String createAt;
    private String endTime;
    private String title;
    private String icon;
    private String content;
    private String yuyin;
    private String xiaoquId;
    private String xiaolabaId;
    private String xiaoquName;
    private String branchName;
    private String signBeginTime;
    private String signEndTime;

    private String tag;
    private String tagId;
    private String time;
    private String images;
    private String addUserName;
    private String addUserIcon;
    private String addHeadImage;
    private String addUserId;
    private String addMobile;
    private String addUserSex;
    private String checkStatus;
    private String status, status2;//审批状态
    private String isOrder;// 0维保订单，1售后订单
    private String uid;//审批人id
    private List<MessageEntity> process;
    private String name;
    private String approveType;
    private List<MessageEntity> workers;
    private List<MessageEntity> huifu;
    private List<MessageEntity> copyto;
    private String huiName;
    private String huiImage;
    private String huiConent;
    private String huiTime;
    private String sendOrderTime, sendOrderTime1;
    private String addisMaketure, addisMaketure1;
    private String addisPingjia, addisPingjia1;
    private String addisForward, addisForward1;
    private String addisDone, addisDone1;
    private List<MessageEntity> checker;
    private List<MessageEntity> replayList;

    private String sign;
    private String wid;
    private String workName;
    private String replyEndTime;
    private String reason;
    private String address;
    private String copyName;
    private String coid;
    private String isRead, messageCount;


    private String orderId;
    private String headImage;
    private String zoneId;
    private String zoneName;
    private String eventTime;
    private String confirmAt, confirmAt1;
    private String historyStatus;
    private String workerName, workerName1;
    private String workerMobile, workerMobile1;
    private String workerDoneTime, workerDoneTime1;
    private String appointmentTime, appointmentTime1;
    private String houseId;
    private String houseName;
    private String orderNum;
    private String addTime;

    private String orderNumber;

    private String replyName;
    private String replyContent;
    private String replyIcon;
    private String replyAt;
    private String agreeStatus;
    private String forwardStatus;
    private String receiptStatus;
    private String addStatus;
    private String seeMine;

    private String approveName;
    private String linkUrl;
    private List<DisclosureEntity> disclose;
    private String tid;
    private String url;
    private String canel;
    private String schedule;

    private String weiOrderId;

    private List<ContrastForm> contrastForms;

    @Data
    public class ContrastForm implements Serializable {

        private String formUrl, formName;

    }

}
