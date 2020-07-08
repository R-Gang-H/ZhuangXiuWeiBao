package com.zhuangxiuweibao.app.common.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.zhuangxiuweibao.app.MyApplication;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.HouseEntity;
import com.zhuangxiuweibao.app.bean.UserEntity;
import com.zhuangxiuweibao.app.common.AppManager;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.http.progress.MyProgressDialog;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.ui.activity.ChooseIdentityActivity;
import com.zhuangxiuweibao.app.ui.activity.LoginActivity;
import com.zhuangxiuweibao.app.ui.activity.MainActivity;
import com.zhuangxiuweibao.app.ui.activity.MainWorkActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Setter;

public class UserManager {

    public UserEntity userData = new UserEntity();
    public HouseEntity houseEntity = new HouseEntity();

    private static UserManager mUserManager;

    public static UserManager getInstance() {
        if (mUserManager == null) {
            mUserManager = new UserManager();
        }
        return mUserManager;
    }

    public void save(Context context, UserEntity user) {
        userData = user;
        LogUtils.e(context.getClass().getName(), userData.toString());
        U.savePreferences("uid", userData.getUid());
        U.savePreferences("name", userData.getName());
        U.savePreferences("firstIdentity", userData.getFirstIdentity());//主用户角色 1住户 2工作人员 3”大妈”（从住户中挑选，赋予工作人员权限） 用户身份未确定此字段为空
        U.savePreferences("token", U.MD5(UserManager.getInstance().userData.getToken() + "_" + Constant.API_KEY));
    }

    public static <T extends View, V> void apply(List<T> list,
                                                 Setter<? super T, V> setter, V value) {
        for (int i = 0, count = list.size(); i < count; i++) {
            setter.set(list.get(i), value, i);
        }
    }

    /**
     * 登录
     *
     * @param activity
     * @param phoneNum
     * @param code
     */
    public void login(Activity activity, String phoneNum, String code) {
        HttpManager.getInstance().doLoginRequest(activity.getLocalClassName(), phoneNum, code,
                new HttpCallBack<BaseDataModel<UserEntity>>(activity, true) {

                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        loginSave(activity, data);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.e("Login onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1004) {
                            U.showToast("验证码错误");
                        } else if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(activity);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.e("Login onError", throwable.getMessage());
                    }
                });
    }

    /**
     * 自动登录
     * haoruigang on 2018-4-2 17:30:17
     *
     * @param context
     * @param uid
     * @param token
     */
    public void autoLogin(final Activity context, String uid, String token) {
        if (uid != null && token != null) {
            HttpManager.getInstance().doAutoLogin(context.getLocalClassName(),
                    uid, token, new HttpCallBack<BaseDataModel<UserEntity>>() {
                        @Override
                        public void onSuccess(BaseDataModel<UserEntity> data) {
                            loginSave(context, data);
                        }

                        @Override
                        public void onFail(int statusCode, String errorMsg) {
                            if (statusCode == 1003) {//异地登录
                                U.showToast("该账户在异地登录!");
                                HttpManager.getInstance().dologout(context);
                                return;
                            }
                            LogUtils.d("autoLogin", statusCode + ":" + errorMsg);
                            U.showToast("登录信息失效，请重新登录");
                            U.savePreferences("uid", "");
                            U.savePreferences("token", "");
                            context.startActivity(new Intent(context, LoginActivity.class));
                            context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            LogUtils.d("autoLogin", throwable.getMessage());
                        }
                    });
        }
    }

    private void loginSave(Activity activity, BaseDataModel<UserEntity> data) {
        UserEntity userData = data.getData().get(0);

        save(activity, userData);

        if (userData.getIsNew().equals("1")) {// 1新用户(未注册) 2老用户
            activity.startActivity(new Intent(activity, ChooseIdentityActivity.class));
        } else if (userData.getIsNew().equals("2") && TextUtils.isEmpty(userData.getFirstIdentity())) {// 1新用户 2老用户(已注册) 未确定身份
            activity.startActivity(new Intent(activity, ChooseIdentityActivity.class));
        } else if (userData.getIsNew().equals("2") && userData.getFirstIdentity().equals("2")) {// 2老用户(已注册) 2工作人员
            activity.startActivity(new Intent(activity, MainWorkActivity.class));
        } else {
            houseInfo(activity, "", null);
            activity.startActivity(new Intent(activity, MainActivity.class));
        }
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        activity.finish();
    }

    /**
     * 登出
     *
     * @param context
     */
    public void logout(final Activity context, final MyProgressDialog dialog) {

        String uid = userData.getUid();
        String token = userData.getToken();

        if (uid != null && token != null) {
            AppManager.getAppManager().finishAllActivity();// finish 所有Activity
            HttpManager.getInstance().doLogout(context.getClass().getName(),
                    uid, token, new HttpCallBack<BaseDataModel<UserEntity>>(context, true) {
                        @Override
                        public void onSuccess(BaseDataModel<UserEntity> data) {
                            dialog.dismiss();
                            U.showToast("退出登录成功");
                            clearInfo(context);
                        }

                        @Override
                        public void onFail(int statusCode, String errorMsg) {
                            dialog.dismiss();
                            LogUtils.d("Logout", statusCode + ":" + errorMsg);
                            clearInfo(context);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            LogUtils.d("Logout", throwable.getMessage());
                            clearInfo(context);
                        }
                    });
        } else {
            clearInfo(context);
        }
    }

    private void clearInfo(Activity context) {
        userData = null;
        U.savePreferences("uid", "");
        U.savePreferences("token", "");
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getAppContext().startActivity(intent);
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private CloudPushService mPushService = PushServiceFactory.getCloudPushService();

    public void xgPush(Activity activity) {
        mPushService.bindAccount(U.MD5(UserManager.getInstance().userData.getUid()), new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                LogUtils.d("TPush1", s + "推送注册成功，设备token为：" + U.MD5(UserManager.getInstance().userData.getUid()));
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                LogUtils.d("TPush1", "推送注册失败，错误码：" + errorMsg + ",错误信息：" + errorMsg);
            }
        });
    }

    public void xgUnPush(Activity activity) {
//        // 1.获取设备Token
        String account = U.MD5(UserManager.getInstance().userData.getUid());
        final MyProgressDialog dialog = new MyProgressDialog(activity, true);
        dialog.show();

        mPushService.unbindAccount(new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                UserManager.getInstance().logout(activity, dialog);
                LogUtils.d("TPush2", "推送注销成功" + s);
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                LogUtils.d("TPush2", "推送注销失败" + " errCode = " + errorCode + " , msg = " + errorMsg);
            }
        });
    }

    /**
     * 10.获取房屋信息（完成）
     */
    public void houseInfo(Activity activity, String houseId, HouseInfoListnenar listnenar) {
        if (U.isEmpty(houseId)) {
            houseId = userData.getHouseId();
        }
        HttpManager.getInstance().houseInfo(activity.getLocalClassName(), houseId,
                new HttpCallBack<BaseDataModel<HouseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<HouseEntity> data) {
                        houseEntity = data.getData().get(0);
                        if (listnenar != null) {
                            listnenar.Success(houseEntity);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("houseInfo", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(activity);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("houseInfo", throwable.getMessage());
                    }
                });
    }

    /**
     * 处理消息已读状态
     *
     * @param isRead
     */
    public void isReads(String isRead) {
        HttpManager.getInstance().isReads(isRead, new HttpCallBack<BaseDataModel>() {
            @Override
            public void onSuccess(BaseDataModel data) {
                EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT));
            }

            @Override
            public void onFail(int statusCode, String errorMsg) {
                LogUtils.d("ContactActivity", statusCode + ":" + errorMsg);
                if (statusCode == 1003) {//异地登录
                    U.showToast("该账户在异地登录!");
                    HttpManager.getInstance().dologout((Activity) MyApplication.getAppContext());
                    return;
                }
                U.showToast(errorMsg);
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtils.d("ContactActivity", throwable.getMessage());
            }
        });
    }

    public interface HouseInfoListnenar {
        void Success(HouseEntity entity);
    }

}
