package com.zhuangxiuweibao.app.common.http;

import android.app.Activity;
import android.text.TextUtils;

import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.Constant;
import com.zhuangxiuweibao.app.common.user.UserManager;

import java.util.HashMap;
import java.util.Map;

import static com.zhuangxiuweibao.app.common.user.Constant.ADDCHECKER;
import static com.zhuangxiuweibao.app.common.user.Constant.ADDDISCLOSURECOLUMN;
import static com.zhuangxiuweibao.app.common.user.Constant.ADDLINJU;
import static com.zhuangxiuweibao.app.common.user.Constant.APPOINTTIME;
import static com.zhuangxiuweibao.app.common.user.Constant.ASKAFTER;
import static com.zhuangxiuweibao.app.common.user.Constant.ASKREPAIR;
import static com.zhuangxiuweibao.app.common.user.Constant.ASKSOS;
import static com.zhuangxiuweibao.app.common.user.Constant.CANCELSERVICE;
import static com.zhuangxiuweibao.app.common.user.Constant.CHECKERLIST;
import static com.zhuangxiuweibao.app.common.user.Constant.CHECKRELEASE;
import static com.zhuangxiuweibao.app.common.user.Constant.CONFIRMTIME;
import static com.zhuangxiuweibao.app.common.user.Constant.DISCLOSETEMPLATE;
import static com.zhuangxiuweibao.app.common.user.Constant.DISCLOSURENEXT;
import static com.zhuangxiuweibao.app.common.user.Constant.EMERGENCYHELP;
import static com.zhuangxiuweibao.app.common.user.Constant.GETDETAIL;
import static com.zhuangxiuweibao.app.common.user.Constant.GETDISCLOSURECOLUMN;
import static com.zhuangxiuweibao.app.common.user.Constant.GETFAMILY;
import static com.zhuangxiuweibao.app.common.user.Constant.GETGROUP;
import static com.zhuangxiuweibao.app.common.user.Constant.GETITEMROW;
import static com.zhuangxiuweibao.app.common.user.Constant.GETMESSAGEINFO;
import static com.zhuangxiuweibao.app.common.user.Constant.GETRELEASE;
import static com.zhuangxiuweibao.app.common.user.Constant.GETSENDDISCLOSE;
import static com.zhuangxiuweibao.app.common.user.Constant.GETSKYDRIVELIST;
import static com.zhuangxiuweibao.app.common.user.Constant.GETUSERINFO;
import static com.zhuangxiuweibao.app.common.user.Constant.GETWEIORDER;
import static com.zhuangxiuweibao.app.common.user.Constant.HOUSEINFO;
import static com.zhuangxiuweibao.app.common.user.Constant.ISREAD;
import static com.zhuangxiuweibao.app.common.user.Constant.MYORDER;
import static com.zhuangxiuweibao.app.common.user.Constant.ORDEREVALUATE;
import static com.zhuangxiuweibao.app.common.user.Constant.PARTERSLIST;
import static com.zhuangxiuweibao.app.common.user.Constant.REGISTMEMBER;
import static com.zhuangxiuweibao.app.common.user.Constant.RELEASECHECK;
import static com.zhuangxiuweibao.app.common.user.Constant.RELEASEGROUPNOTICE;
import static com.zhuangxiuweibao.app.common.user.Constant.RELEASENEWS;
import static com.zhuangxiuweibao.app.common.user.Constant.RELEASEWORK;
import static com.zhuangxiuweibao.app.common.user.Constant.REPLY;
import static com.zhuangxiuweibao.app.common.user.Constant.REPLYDETAIL;
import static com.zhuangxiuweibao.app.common.user.Constant.REPLYTO;
import static com.zhuangxiuweibao.app.common.user.Constant.SENDDISCLOSURETASK;
import static com.zhuangxiuweibao.app.common.user.Constant.SETSOSCONTANCT;
import static com.zhuangxiuweibao.app.common.user.Constant.SHARETOPARTER;
import static com.zhuangxiuweibao.app.common.user.Constant.SHEQUINFO;
import static com.zhuangxiuweibao.app.common.user.Constant.SOSRESMSG;
import static com.zhuangxiuweibao.app.common.user.Constant.STOPAPPROVAL;
import static com.zhuangxiuweibao.app.common.user.Constant.TASKLIST;
import static com.zhuangxiuweibao.app.common.user.Constant.TESTHOLD;
import static com.zhuangxiuweibao.app.common.user.Constant.TESTWORKER;
import static com.zhuangxiuweibao.app.common.user.Constant.TESTWORKERPHONE;
import static com.zhuangxiuweibao.app.common.user.Constant.UPDATEOWNINFO;
import static com.zhuangxiuweibao.app.common.user.Constant.VILLAGESTATUS;
import static com.zhuangxiuweibao.app.common.user.Constant.WEICONFIRM;
import static com.zhuangxiuweibao.app.common.user.Constant.WEIDONE;
import static com.zhuangxiuweibao.app.common.user.Constant.WORKERLIST;
import static com.zhuangxiuweibao.app.common.user.Constant.WRITEDISCLOSURE;

/**
 * ????????? haoruigang on 2017-11-28 11:14:10.
 * ???????????????????????????(????????????????????????)
 */
public class HttpManager {

    private static class SingletonHolder {
        static HttpManager INSTANCE = new HttpManager();
    }

    public static HttpManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 1.?????????
     *
     * @param tag
     * @param callBack
     */
    public void doCoOpenCode(String tag, HttpCallBack callBack) {
        final HashMap<String, String> map = new HashMap<>();
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.STARTUP, map, callBack);
    }

    /**
     * haoruigang on 2018-3-30 10:30:01
     * 2.??????:???????????????
     *
     * @param tag
     * @param phoneNum
     * @param callBack
     */
    public void doRandomCode(String tag, String phoneNum, HttpCallBack callBack) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("mobile", phoneNum);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.CODE, map, callBack);
    }

    /**
     * ????????? haoruigang on 2017/11/28 11:16
     * ??????: 3.??????
     *
     * @param tag
     * @param phone
     * @param code
     * @param callBack
     */
    public void doLoginRequest(String tag, String phone, String code, HttpCallBack callBack) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("mobile", phone);
        map.put("code", code);
        map.put("deviceType", "2");// 1:iOS 2:Android
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.LOGIN, map, callBack);
    }

    /**
     * ?????????haoruigang on 2018-4-2 11:28:00
     * 4.????????????
     *
     * @param tag
     * @param uid
     * @param token
     * @param callBack
     */
    public void doAutoLogin(String tag, String uid, String token, HttpCallBack callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", uid);
        map.put("token", token);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.AUTO_LOGIN, map, callBack);
    }

    /**
     * 5.??????????????????????????????
     *
     * @param tag
     * @param hostId
     * @param name
     * @param birthday
     * @param relationName
     * @param callBack
     */
    public void registMember(String tag, String hostId, String name, String birthday, String relationName, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("hostId", hostId);
        map.put("name", name);
        map.put("birthday", birthday);
        map.put("relationName", relationName);
        OkHttpUtils.getOkHttpJsonRequest(tag, REGISTMEMBER, map, callBack);
    }

    /**
     * 6.??????????????????????????????
     *
     * @param tag
     * @param houseId
     * @param name
     * @param idNumber
     * @param callBack
     */
    public void testHold(String tag, String houseId, String name, String idNumber, String birthday, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("houseId", houseId);
        map.put("name", name);
        map.put("idNumber", idNumber);
        map.put("birthday", birthday);
        OkHttpUtils.getOkHttpJsonRequest(tag, TESTHOLD, map, callBack);
    }

    /**
     * 7.????????????????????????????????????
     *
     * @param tag
     * @param mobile
     * @param name
     * @param idNumber
     * @param callBack
     */
    public void testWorker(String tag, String mobile, String name, String idNumber, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("name", name);
        map.put("idNumber", idNumber);
        OkHttpUtils.getOkHttpJsonRequest(tag, TESTWORKER, map, callBack);
    }

    /**
     * ?????????haoruigang on 2018-4-2 11:28:00
     * 8.??????????????????
     *
     * @param tag
     * @param type
     * @param xiaoqu
     * @param xiaoqu
     * @param page
     * @param callBack
     */
    public void doUserMsgList(String tag, String type, String xiaoqu, String page, HttpCallBack callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", type);
        map.put("xiaoqu", xiaoqu);
        map.put("page", page);
        map.put("pageSize", "20");
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.USERMSGLIST, map, callBack);
    }

    /**
     * 9.??????????????????????????????
     *
     * @param tag
     * @param shequId
     * @param callBack
     */
    public void shequInfo(String tag, String shequId, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("shequId", shequId);
        OkHttpUtils.getOkHttpJsonRequest(tag, SHEQUINFO, map, callBack);
    }

    /**
     * 10.??????????????????????????????
     *
     * @param tag
     * @param houseId
     * @param callBack
     */
    public void houseInfo(String tag, String houseId, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("houseId", houseId);
        OkHttpUtils.getOkHttpJsonRequest(tag, HOUSEINFO, map, callBack);
    }

    /**
     * 11.????????????????????????????????????
     *
     * @param tag
     * @param houseId
     * @param zoneId
     * @param describe
     * @param images
     * @param yuyin
     * @param callBack
     */
    public void askRepair(String tag, String houseId, String zoneId, String describe, String images,
                          String yuyin, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("houseId", houseId);
        map.put("zoneId", zoneId);
        map.put("describe", describe);
        map.put("images", images);
        map.put("yuyin", yuyin);
        OkHttpUtils.getOkHttpJsonRequest(tag, ASKREPAIR, new HashMap<>(), map, callBack);
    }

    /**
     * 12.???????????????????????????
     *
     * @param tag1
     * @param tag
     * @param content
     * @param images
     * @param callBack
     */
    public void releaseNews(String tag1, String tag, String content, String images, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("tag", tag);
        map.put("content", content);
        map.put("images", images);
        OkHttpUtils.getOkHttpJsonRequest(tag1, RELEASENEWS, new HashMap<>(), map, callBack);
    }

    /**
     * 14.???????????????????????????????????????
     *
     * @param tag
     * @param mobile
     * @param callBack
     */
    public void testWorkerPhone(String tag, String mobile, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        OkHttpUtils.getOkHttpJsonRequest(tag, TESTWORKERPHONE, map, callBack);
    }

    /**
     * 15.??????????????????????????????
     *
     * @param tag
     * @param status
     * @param page
     * @param callBack
     */
    public void doMyOrder(String tag, String status, String page, HttpCallBack callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("status", status);
        map.put("page", page);
        map.put("pageSize", "20");
        OkHttpUtils.getOkHttpJsonRequest(tag, MYORDER, map, callBack);
    }

    /**
     * 17.????????????????????????
     *
     * @param tag
     * @param orderId
     * @param callBack
     */
    public void doOrderEvaluate(String tag, String eventId, String orderId, String skill, String service,
                                String priceEvaluate, String comment, HttpCallBack callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("orderId", orderId);
        map.put("skill", skill);
        map.put("service", service);
        map.put("priceEvaluate", priceEvaluate);
        map.put("comment", comment);
        OkHttpUtils.getOkHttpJsonRequest(tag, ORDEREVALUATE, map, callBack);
    }

    /**
     * 18.????????????????????????
     *
     * @param tag
     * @param eventId
     * @param orderId
     * @param reason
     * @param callBack
     */
    public void doAskAfter(String tag, String eventId, String orderId, String reason, HttpCallBack callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("orderId", orderId);
        map.put("reason", reason);
        OkHttpUtils.getOkHttpJsonRequest(tag, ASKAFTER, map, callBack);
    }

    /**
     * 20.?????????????????????????????????????????????????????????
     *
     * @param tag
     * @param type
     * @param page
     * @param callBack
     */
    public void doWorkMsgList(String tag, String type, String keyword, String page, HttpCallBack callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", type);
        map.put("keyword", keyword);
        map.put("page", page);
        map.put("pageSize", "20");
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.WORKMSG, map, callBack);
    }

    /**
     * 22.??????????????????????????????
     *
     * @param tag
     * @param callBack
     */
    public void doCheckList(String tag, HttpCallBack callBack) {
        HashMap<String, String> map = new HashMap<>();
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.CHECKLIST, map, callBack);
    }

    /**
     * 28.????????????????????????????????????
     *
     * @param tag
     * @param xiaoquId
     * @param title
     * @param content
     * @param tagId    1.???????????? 2.???????????? 3.???????????? 4.????????????
     * @param callBack
     */
    public void doReleaseCommuNotice(String tag, String xiaoquId, String title, String content,
                                     String tagId, String linkUrl, HttpCallBack callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("xiaoquId", xiaoquId);
        map.put("title", title);
        map.put("content", content);
        map.put("tag", tagId);
        map.put("linkUrl", linkUrl);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.RELEASECOMMUNOTICE, map, callBack);
    }

    // ??????
    public void dologout(Activity activity) {
        UserManager.getInstance().xgUnPush(activity);//???????????????
    }

    /**
     * haoruigang on 2018-4-2 17:33:59
     * 29 ????????????
     *
     * @param tag
     * @param uid
     * @param token
     * @param callBack
     */
    public void doLogout(String tag, String uid, String token, HttpCallBack callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", uid);
        map.put("token", token);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.LOGOUT, map, callBack);
    }

    /**
     * 30?????????????????????????????????
     *
     * @param tag
     * @param headImage
     * @param sex
     * @param birthday
     * @param sosContacts
     * @param callBack
     */
    public void doUpdateOwnInfo(String tag, String headImage, String sex, String birthday, String sosContacts, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("headImage", headImage);
        map.put("sex", sex);
        map.put("birthday", birthday);
        map.put("sosContacts", sosContacts);
        OkHttpUtils.getOkHttpJsonRequest(tag, UPDATEOWNINFO, new HashMap<>(), map, callBack);
    }


    /**
     * 31?????????????????????????????????
     *
     * @param tag
     * @param callBack
     */
    public void doGetFamily(String tag, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        OkHttpUtils.getOkHttpJsonRequest(tag, GETFAMILY, map, callBack);
    }

    /**
     * 32?????????????????????????????????????????????????????????
     *
     * @param tag
     * @param eventId
     * @param type
     * @param callBack
     */
    public void doCheckRelease(String tag, String eventId, String type, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("type", type);
        OkHttpUtils.getOkHttpJsonRequest(tag, CHECKRELEASE, map, callBack);
    }

    /**
     * 34???????????????????????????????????????????????????
     *
     * @param tag
     * @param eventId
     * @param callBack
     */
    public void doGetRelease(String tag, String eventId, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        OkHttpUtils.getOkHttpJsonRequest(tag, GETRELEASE, map, callBack);
    }

    /**
     * 35??????????????????????????????????????????
     *
     * @param tag
     * @param eventId
     * @param orderId
     * @param callBack
     */
    public void doGetWeiOrder(String tag, String eventId, String orderId, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("orderId", orderId);
        OkHttpUtils.getOkHttpJsonRequest(tag, GETWEIORDER, map, callBack);
    }

    /**
     * 36 ????????????/???????????????????????????
     *
     * @param tag
     * @param eventId
     * @param callBack
     */
    public void getDetail(String tag, String eventId, String cid, String type, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        if (TextUtils.isEmpty(cid)) {
            map.put("eventId", eventId);
        } else {
            map.put("cid", cid);
        }
        map.put("type", type);//	1?????????2????????? ???3??????
        OkHttpUtils.getOkHttpJsonRequest(tag, GETDETAIL, map, callBack);
    }

    /**
     * 37 ??????????????????
     *
     * @param tag
     * @param eventId
     * @param status
     * @param content
     * @param callBack
     */
    public void reply(String tag, String eventId, String status, String content, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("status", status);
        map.put("content", content);
        OkHttpUtils.getOkHttpJsonRequest(tag, REPLY, map, callBack);
    }

    /**
     * 38 ????????????????????????
     *
     * @param tag
     * @param eventId
     * @param pid
     * @param callBack
     */
    public void shareToParter(String tag, String eventId, String pid, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("pid", pid);
        OkHttpUtils.getOkHttpJsonRequest(tag, SHARETOPARTER, map, callBack);
    }

    /**
     * 39 ???????????????????????????
     *
     * @param tag
     * @param eventId
     * @param cid
     * @param callBack
     */
    public void addChecker(String tag, String eventId, String cid, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("cid", cid);
        OkHttpUtils.getOkHttpJsonRequest(tag, ADDCHECKER, map, callBack);
    }

    /**
     * 40 ????????????????????????
     *
     * @param tag
     * @param callBack
     */
    public void partersList(String tag, String keyword, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("keyword", keyword);
        OkHttpUtils.getOkHttpJsonRequest(tag, PARTERSLIST, map, callBack);
    }

    /**
     * 41 ???????????????????????????
     *
     * @param tag
     * @param callBack
     */
    public void checkerList(String tag, String keyword, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("keyword", keyword);
        OkHttpUtils.getOkHttpJsonRequest(tag, CHECKERLIST, map, callBack);
    }

    /**
     * 42 ??????????????????
     *
     * @param tag
     * @param eventId
     * @param callBack
     */
    public void replyto(String tag, String eventId, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        OkHttpUtils.getOkHttpJsonRequest(tag, REPLYTO, map, callBack);
    }

    /**
     * 43 ??????????????????????????????
     *
     * @param tag
     * @param eventId
     * @param callBack
     */
    public void replyDetail(String tag, String eventId, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        OkHttpUtils.getOkHttpJsonRequest(tag, REPLYDETAIL, map, callBack);
    }

    /**
     * 21 ????????????????????????
     *
     * @param tag
     * @param callBack
     */
    public void workerList(String tag, String keyword, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("keyword", keyword);
        OkHttpUtils.getOkHttpJsonRequest(tag, WORKERLIST, map, callBack);
    }

    /**
     * 23.??????????????????
     *
     * @param tag
     * @param checkerIds
     * @param copierIds
     * @param content
     * @param title
     * @param type
     * @param images
     * @param modifyExplain
     * @param modifyCopy
     * @param linkUrl
     * @param callBack
     */
    public void releaseCheck(String tag, String checkerIds, String copierIds, String content,
                             String title, String type, String images, String modifyExplain,
                             String modifyCopy, String linkUrl, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("checkerIds", checkerIds);
        map.put("copierIds", copierIds);
        map.put("content", content);
        map.put("title", title);
        map.put("type", type);
        map.put("images", images);
        map.put("modifyExplain", modifyExplain);
        map.put("modifyCopy", modifyCopy);
        map.put("linkUrl", linkUrl);
        OkHttpUtils.getOkHttpJsonRequest(tag, RELEASECHECK, new HashMap<>(), map, callBack);
    }

    /**
     * 24.??????????????????
     *
     * @param tag
     * @param workerIds
     * @param copierIds
     * @param title
     * @param content
     * @param images
     * @param endTime
     * @param callBack
     */
    public void releaseWork(String tag, String workerIds, String copierIds, String title,
                            String content, String images, String endTime, String linkUrl, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("workerIds", workerIds);
        map.put("copierIds", copierIds);
        map.put("title", title);
        map.put("content", content);
        map.put("images", images);
        map.put("endTime", endTime);
        map.put("linkUrl", linkUrl);
        OkHttpUtils.getOkHttpJsonRequest(tag, RELEASEWORK, new HashMap<>(), map, callBack);
    }

    /**
     * 44?????????????????????????????????????????????
     *
     * @param tag
     * @param eventId
     * @param orderId
     * @param orderTime
     * @param callBack
     */
    public void doAppointTime(String tag, String eventId, String orderId, String orderTime, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("orderId", orderId);
        map.put("orderTime", orderTime);
        String userType = "1";
        if (UserManager.getInstance().userData.getFirstIdentity().equals("2")) {// 1?????? 2???????????? 3????????????
            userType = "2";
        }
        map.put("userType", userType);// 1????????????2???????????????
        OkHttpUtils.getOkHttpJsonRequest(tag, APPOINTTIME, map, callBack);
    }

    /**
     * 45??????????????????????????????????????????????????????
     *
     * @param tag
     * @param eventId
     * @param orderId
     * @param callBack
     */
    public void doConfirmTime(String tag, String eventId, String orderId, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("orderId", orderId);
        OkHttpUtils.getOkHttpJsonRequest(tag, CONFIRMTIME, map, callBack);
    }

    /**
     * 46?????????????????????????????????????????????
     *
     * @param tag
     * @param eventId
     * @param orderId
     * @param callBack
     */
    public void doWeiDone(String tag, String eventId, String orderId, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("orderId", orderId);
        OkHttpUtils.getOkHttpJsonRequest(tag, WEIDONE, map, callBack);
    }

    /**
     * 47?????????????????????????????????
     *
     * @param tag
     * @param reason
     * @param callBack
     */
    public void doAskSOS(String tag, String reason, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("reason", reason);
        OkHttpUtils.getOkHttpJsonRequest(tag, ASKSOS, map, callBack);
    }

    /**
     * 48????????????????????????????????????
     *
     * @param tag
     * @param userId
     * @param callBack
     */
    public void doGetUserInfo(String tag, String userId, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        OkHttpUtils.getOkHttpJsonRequest(tag, GETUSERINFO, map, callBack);
    }

    /**
     * 49???????????????????????????????????????????????????????????????
     *
     * @param tag
     * @param eventId
     * @param orderId
     * @param type
     * @param callBack
     */
    public void doWeiConfirm(String tag, String eventId, String orderId, String type, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("orderId", orderId);
        map.put("type", type);
        OkHttpUtils.getOkHttpJsonRequest(tag, WEICONFIRM, map, callBack);
    }

    /**
     * 50????????????????????????????????????
     *
     * @param tag
     * @param eventId
     * @param callBack
     */
    public void emergencyHelp(String tag, String eventId, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        OkHttpUtils.getOkHttpJsonRequest(tag, EMERGENCYHELP, map, callBack);
    }

    /**
     * 52????????????????????????????????????
     *
     * @param tag
     * @param userId
     * @param callBack
     */
    public void doSetSosContanct(String tag, String userId, String relationName, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        map.put("relationName", relationName);
        OkHttpUtils.getOkHttpJsonRequest(tag, SETSOSCONTANCT, map, callBack);
    }

    /**
     * 25.??????????????????
     *
     * @param tag
     * @param
     * @param callBack
     */
    public void getGroup(String tag, String groupId, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("groupId", groupId);
        OkHttpUtils.getOkHttpJsonRequest(tag, GETGROUP, map, callBack);
    }

    /**
     * 26.?????????????????????
     *
     * @param tag
     * @param
     * @param callBack
     */
    public void releaseGroupNotice(String tag, String companyIds, String departmentIds, String title,
                                   String content, String images, String sign, String signBeginTime,
                                   String signEndTime, String linkUrl, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        if (TextUtils.isEmpty(departmentIds)) {
            map.put("companyIds", companyIds);
        } else {
            map.put("departmentIds", departmentIds);
        }
        map.put("title", title);
        map.put("content", content);
        map.put("images", images);
        map.put("sign", sign);//?????????????????? 1?????? 2?????????
        if (sign.equals("1")) {
            map.put("signBeginTime", signBeginTime);
            map.put("signEndTime", signEndTime);
        }
        map.put("linkUrl", linkUrl);
        OkHttpUtils.getOkHttpJsonRequest(tag, RELEASEGROUPNOTICE, new HashMap<>(), map, callBack);
    }

    /**
     * 53???????????????/??????/????????? ??????????????????
     *
     * @param tag
     * @param type
     * @param page
     */
    public void taskList(String tag, String type, String page, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("page", page);
        map.put("type", type);
        map.put("pageSize", "20");
        OkHttpUtils.getOkHttpJsonRequest(tag, TASKLIST, map, callBack);
    }

    /**
     * 67????????????????????????????????????????????????
     *
     * @param tag
     * @param page
     * @param callBack
     */
    public void doGetSendDisclose(String tag, String page, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("page", page);
        map.put("pageSize", "20");
        OkHttpUtils.getOkHttpJsonRequest(tag, GETSENDDISCLOSE, map, callBack);
    }

    /**
     * 54??? ?????? ????????????
     *
     * @param tag
     * @param
     * @param
     */
    public void sosResmsg(String tag, String eventId, String content, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("content", content);
        OkHttpUtils.getOkHttpJsonRequest(tag, SOSRESMSG, map, callBack);
    }

    /**
     * 55???????????????????????????
     *
     * @param tag
     * @param orderId
     * @param callBack
     */
    public void cancelService(String tag, String orderId, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("orderId", orderId);
        OkHttpUtils.getOkHttpJsonRequest(tag, CANCELSERVICE, map, callBack);
    }

    /**
     * 56???????????????????????????
     *
     * @param tag
     * @param userId
     * @param callBack
     */
    public void doAddlinju(String tag, String userId, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        OkHttpUtils.getOkHttpJsonRequest(tag, ADDLINJU, map, callBack);
    }

    /**
     * 57.????????????????????????
     *
     * @param isRead
     */
    public void isReads(String isRead, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", isRead);
        OkHttpUtils.getOkHttpJsonRequest("isRead", ISREAD, map, callBack);
    }

    /**
     * 58.?????????????????????
     *
     * @param tag
     * @param xiaoquId
     * @param callBack
     */
    public void doVillageStatus(String tag, String xiaoquId, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("xiaoquId", xiaoquId);
        OkHttpUtils.getOkHttpJsonRequest(tag, VILLAGESTATUS, map, callBack);
    }

    /**
     * 60.????????????????????????
     *
     * @param tag
     * @param type
     * @param page
     * @param callBack
     */
    public void doSkyDriveList(String tag, String type, String page, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("type", type);
        map.put("page", page);
        map.put("pageSize", "20");
        OkHttpUtils.getOkHttpJsonRequest(tag, GETSKYDRIVELIST, map, callBack);
    }

    /**
     * 61???????????????????????????
     *
     * @param tag
     * @param callBack
     */
    public void doDiscloseTemplate(String tag, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        OkHttpUtils.getOkHttpJsonRequest(tag, DISCLOSETEMPLATE, map, callBack);
    }

    /**
     * 62??????????????????????????????
     *
     * @param tag
     * @param title
     * @param content
     * @param endTime
     * @param tid
     * @param item
     * @param callBack
     */
    public void doSendDisclosureTask(String tag, String title, String content, String endTime,
                                     String tid, String item, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("content", content);
        map.put("endTime", endTime);
        map.put("tid", tid);
        map.put("item", item);
        OkHttpUtils.getOkHttpJsonRequest(tag, SENDDISCLOSURETASK, new HashMap<>(), map, callBack);
    }

    /**
     * 65?????????????????????
     *
     * @param tag
     * @param eventId
     * @param callBack
     */
    public void doDisclosureColumn(String tag, String eventId, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        OkHttpUtils.getOkHttpJsonRequest(tag, GETDISCLOSURECOLUMN, map, callBack);
    }

    /**
     * 63???????????????
     *
     * @param tag
     * @param eventId
     * @param locationName
     * @param unit
     * @param callBack
     */
    public void doAddDiscColumn(String tag, String eventId, String locationName, String unit, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("locationName", locationName);
        map.put("unit", unit);
        OkHttpUtils.getOkHttpJsonRequest(tag, ADDDISCLOSURECOLUMN, map, callBack);
    }

    /**
     * 64???????????????????????????????????????????????????
     *
     * @param tag
     * @param tid
     * @param itemid
     * @param eventId
     * @param locId
     * @param callBack
     */
    public void doGetItemRow(String tag, String tid, String itemid, String eventId, String locId, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("tid", tid);
        map.put("itemid", itemid);
        map.put("eventId", eventId);
        map.put("locId", locId);
        OkHttpUtils.getOkHttpJsonRequest(tag, GETITEMROW, map, callBack);
    }

    /**
     * 66???????????????????????????
     *
     * @param tag
     * @param eventId
     * @param itemid
     * @param locId
     * @param json
     * @param callBack
     */
    public void doWriteDisclosure(String tag, String eventId, String itemid, String locId, String json, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("itemid", itemid);
        map.put("locId", locId);
        map.put("json", json);
        OkHttpUtils.getOkHttpJsonRequest(tag, WRITEDISCLOSURE, new HashMap<>(), map, callBack);
    }

    /**
     * 68???????????????????????????
     *
     * @param tag
     * @param eventId
     * @param content
     * @param status
     * @param callBack
     */
    public void doDisclosureNext(String tag, String eventId, String content, String status, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("content", content);
        map.put("status", status);
        OkHttpUtils.getOkHttpJsonRequest(tag, DISCLOSURENEXT, map, callBack);
    }

    /**
     * 69???????????????
     *
     * @param tag
     * @param cid
     * @param type     1 cid?????????????????????2cid??????????????????
     * @param callBack
     */
    public void doStopApproval(String tag, String cid, String type, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("cid", cid);
        map.put("type", type);
        OkHttpUtils.getOkHttpJsonRequest(tag, STOPAPPROVAL, map, callBack);
    }

    /**
     * 70?????????????????????
     *
     * @param tag
     * @param eventId
     * @param callBack
     */
    public void getMessageInfo(String tag, String eventId, HttpCallBack callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        OkHttpUtils.getOkHttpJsonRequest(tag, GETMESSAGEINFO, map, callBack);
    }

}


