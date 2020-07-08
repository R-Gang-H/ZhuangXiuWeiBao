package com.zhuangxiuweibao.app.common.user;

public class Constant {

    // 是否测试服
    public static boolean isTest = true, isShowLog = false;
    //  http://api.zxwb.1bu2bu.com/index.php/（测试环境）
    //  http://zxwb.1bu2bu.cn/api/public/index.php/（开发环境）
    //  http://api.hejiadao.com/index.php/（生产环境）
    private static String BASE_API = isTest ? "http://api.zxwb.1bu2bu.com/index.php/" : "http://api.hejiadao.com/index.php/";
    // API_KEY
    public static String API_KEY = "Ij638hd*(#Jfy72f";
    // RSA 加密公钥
    public static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw4wPLOnPFTcA87drH3H+\n" +
            "   6mvmAE9ZfbVEf+R2p2l5IkppcB9ag4NygmUzqCThLbZRJHgWFd2HJfXXvS9NGV1t\n" +
            "   wb3Yhuu7oUtKhusxmuQXGXOqrhkcm1cPoBQNiqqWUCEGtXaAL9Bdu/5ydPZglBBc\n" +
            "   XnNQHGtegWKxxwNhSowimckG8r7Up1J+FEXH4+odGRhBqQSfXa/pGwKk+DdlI92x\n" +
            "   mWMbgSlSYO0CWHpQiUWuPxe6OtmOjbH1gdZGyAtRECFRWXlpu5/j+4WFQoeYe+k0\n" +
            "   NyFveBsoxgx0qAmgqpLp4OZnEitC1ynTZC/iB1YThbTpIpURION1Mo3KHa2+9GeY\n" +
            "   8QIDAQAB";

    private static final String ABOUT_URL = "http://admin.zxwb.1bu2bu.com/index.php?s=/Admin/Public/";
    // 隐私说明地址
    public static final String PRIVACY = ABOUT_URL + "Privacy.html";
    // 用户协议地址
    public static final String AGREEMENT = ABOUT_URL + "Agreement.html";

    // 阿里云
    public static final String accessKeyId = isTest ? "rlMcJzZ6U2wgBrZU" : "LTAI4FfLeHkQ4xrqridqXLzx";
    public static final String accessKeySecret = isTest ? "2uhcBZntvWrFbVhiUEvpARd9PmjKgJ" : "QAGQwVMu0n5G7SdEF7miyY77uY5pxm";
    public static final String ossBucket = isTest ? "zhuangxiuweibao" : "zxwb";
    public static final String ENDPOINT = "http://oss-cn-beijing.aliyuncs.com/";//oss-cn-beijing.aliyuncs.com
    public static final String OSS_URL = "https://" + ossBucket + ".oss-cn-beijing.aliyuncs.com/";

    public static final String photo = "user/avatar/";
    public static final String picture = "user/circle/picture/";
    public static final String video = "user/circle/video/";
    public static final String audio = "user/circle/audio/";

    public static final int RC_AUDIO = 100;
    public static final int REQUEST_CODE_WRITE = 102;

    // 1.开屏页
    public static final String STARTUP = BASE_API + "startUp";
    // 2.获取验证码
    public static final String CODE = BASE_API + "code";
    // 3.登录
    public static final String LOGIN = BASE_API + "login";
    // 4.自动登录
    public static final String AUTO_LOGIN = BASE_API + "autoLogin";
    // 5.家庭成员注册（完成）
    public static final String REGISTMEMBER = BASE_API + "registMember";
    // 6.核验户主身份（完成）
    public static final String TESTHOLD = BASE_API + "testHold";
    // 7.核验工作人员信息（完成）
    public static final String TESTWORKER = BASE_API + "testWorker";
    // 8.主界面消息流（住户版）（完成）
    public static final String USERMSGLIST = BASE_API + "userMsgList";
    // 9.获取社区信息（完成）
    public static final String SHEQUINFO = BASE_API + "shequInfo";
    // 10.获取房屋信息（完成）
    public static final String HOUSEINFO = BASE_API + "houseInfo";
    // 11.发布住房维保需求（完成）
    public static final String ASKREPAIR = BASE_API + "askRepair";
    // 12.发布小喇叭（完成）
    public static final String RELEASENEWS = BASE_API + "releaseNews";
    // 13.我的门禁 优先级低 先跳过
    // 14.核验工作人员手机号（废弃）
    public static final String TESTWORKERPHONE = BASE_API + "testWorkerPhone";
    // 15.我的维保订单（完成）
    public static final String MYORDER = BASE_API + "myOrder";
    // 16.订单详情（完成）(用35接口)
    public static final String ORDERINFO = BASE_API + "orderInfo";
    // 17.发表评价（完成）
    public static final String ORDEREVALUATE = BASE_API + "orderEvaluate";
    // 18.申请售后（完成）
    public static final String ASKAFTER = BASE_API + "askAfter";
    // 19.获取售后订单（完成）(与15接口重)
    // 20.获取主界面信息流（工作人员版）（废弃）"workMsgList";
    public static final String WORKMSGLIST = BASE_API + "workMsgList";
    // 21.获取工作人员列表
    public static final String WORKERLIST = BASE_API + "workerList";
    // 22.获取审批模板（完成）
    public static final String CHECKLIST = BASE_API + "checkList";
    // 23.发布一个审批
    public static final String RELEASECHECK = BASE_API + "releaseCheck";
    // 24.发布一个任务
    public static final String RELEASEWORK = BASE_API + "releaseWork";
    // 25.获取组织架构
    public static final String GETGROUP = BASE_API + "getGroup";
    // 26.发布一个群通知
    // 27
    public static final String RELEASEGROUPNOTICE = BASE_API + "releaseGroupNotice";
    // 28.发布一个社区通知（完成）
    public static final String RELEASECOMMUNOTICE = BASE_API + "releaseCommuNotice";
    // 29.退出登录（完成）
    public static String LOGOUT = BASE_API + "logout";
    // 30、编辑个人信息（完成）
    public static final String UPDATEOWNINFO = BASE_API + "updateOwnInfo";
    // 31、获取家庭成员（完成）
    public static final String GETFAMILY = BASE_API + "getFamily";
    // 32、审核住户发布的社区交流类帖子（完成）
    public static final String CHECKRELEASE = BASE_API + "checkRelease";
    // 33、主页面消息流（工作人员端）（完成）
    public static final String WORKMSG = BASE_API + "workMsg";
    // 34、获取社区交流类帖子的详情（完成）
    public static final String GETRELEASE = BASE_API + "getRelease";
    // 35、获取维保需求详情 （完成）
    public static final String GETWEIORDER = BASE_API + "getWeiOrder";
    // 36 审批详情/群通知详情（完成）
    public static final String GETDETAIL = BASE_API + "getDetail";
    // 37 回复（完成）
    public static final String REPLY = BASE_API + "reply";
    // 38 转给同事（完成）
    public static final String SHARETOPARTER = BASE_API + "shareToParter";
    // 39 追加审批人（完成）
    public static final String ADDCHECKER = BASE_API + "addChecker";
    // 40 同事列表（完成）
    public static final String PARTERSLIST = BASE_API + "partersList";
    // 41 审批人列表（完成）
    public static final String CHECKERLIST = BASE_API + "checkerList";
    // 42 回执（完成）
    public static final String REPLYTO = BASE_API + "signto";
    // 43 查看回执情况（完成）
    public static final String REPLYDETAIL = BASE_API + "signDetail";
    // 44、维保服务预约上门时间（完成）
    public static final String APPOINTTIME = BASE_API + "appointTime";
    // 45、住户确认维保服务的上门时间（完成）
    public static final String CONFIRMTIME = BASE_API + "confirmTime";
    // 46、维保人员点击服务完成（完成）
    public static final String WEIDONE = BASE_API + "weiDone";
    // 47、发布紧急求助（完成）
    public static final String ASKSOS = BASE_API + "askSOS";
    // 48、获取联系人信息（完成）
    public static final String GETUSERINFO = BASE_API + "getUserInfo";
    // 49、住户点击问题已解决或者问题未解决（完成）
    public static final String WEICONFIRM = BASE_API + "weiConfirm";
    // 50，获取紧急求助类帖子详情
    public static final String EMERGENCYHELP = BASE_API + "sosInfo";
    // 52、设为紧急联系人（完成）
    public static final String SETSOSCONTANCT = BASE_API + "setSosContanct";
    // 53，我的任务列表
    public static final String TASKLIST = BASE_API + "taskList";
    // 54、 回复 紧急求助
    public static final String SOSRESMSG = BASE_API + "sosResmsg";
    // 55，维保订单取消服务
    public static final String CANCELSERVICE = BASE_API + "cancelService";
    // 56、添加邻居
    public static final String ADDLINJU = BASE_API + "addlinju";
    // 57.处理消息已读状态
    public static final String ISREAD = BASE_API + "isRead";
    // 58.小区小喇叭状态
    public static final String VILLAGESTATUS = BASE_API + "VillageStatus";
    // 60.获取网盘资源列表
    public static final String GETSKYDRIVELIST = BASE_API + "getSkyDriveList";
    // 61、获取技术交底模板
    public static final String DISCLOSETEMPLATE = BASE_API + "discloseTemplate";
    // 62、发布技术交底类任务
    public static final String SENDDISCLOSURETASK = BASE_API + "sendDisclosureTask";
    // 63、新增设施
    public static final String ADDDISCLOSURECOLUMN = BASE_API + "addDisclosureColumn";
    // 64、获取技术交底模板子项目的填写字段
    public static final String GETITEMROW = BASE_API + "getItemRow";
    // 65、获取设施列表
    public static final String GETDISCLOSURECOLUMN = BASE_API + "getDisclosureColumn";
    // 66、填写技术交底表格
    public static final String WRITEDISCLOSURE = BASE_API + "writeDisclosure";
    // 67、获取我发布的技术交底类任务列表
    public static final String GETSENDDISCLOSE = BASE_API + "getSendDisclose";
    // 68、技术交底任务完成
    public static final String DISCLOSURENEXT = BASE_API + "disclosureNext";
    // 69、撤销审批
    public static final String STOPAPPROVAL = BASE_API + "stopApproval";
    // 70、获取消息详情
    public static final String GETMESSAGEINFO = BASE_API + "getMessageInfo";


    //CALL_PHONE权限
    public static final int RC_CALL_PHONE = 100;

    public final static int TYPE_FILE = 4;
}
