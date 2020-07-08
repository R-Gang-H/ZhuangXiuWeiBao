package com.zhuangxiuweibao.app.common.utils;

import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.zhuangxiuweibao.app.MyApplication;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.UserEntity;
import com.zhuangxiuweibao.app.common.user.ToUIEvent;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.ui.activity.MainActivity;
import com.zhuangxiuweibao.app.ui.activity.MainWorkActivity;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;

import static com.alibaba.sdk.android.ams.common.global.AmsGlobalHolder.getPackageName;

/**
 * Created by Muzik
 * 2018/12/20 10:17
 */
public class NotificationUtil {
    //判断是否需要打开设置界面
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void OpenNotificationSetting(Context context, OnNextLitener mOnNextLitener) {
        if (!isNotificationEnabled(context)) {
            showSettingDialog(context);
        } else {
            if (mOnNextLitener != null) {
                mOnNextLitener.onNext();
            }
        }
    }

    //判断该app是否打开了通知

    /**
     * 可以通过NotificationManagerCompat 中的 areNotificationsEnabled()来判断是否开启通知权限。NotificationManagerCompat 在 android.support.v4.app包中，是API 22.1.0 中加入的。而 areNotificationsEnabled()则是在 API 24.1.0之后加入的。
     * areNotificationsEnabled 只对 API 19 及以上版本有效，低于API 19 会一直返回true
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean isNotificationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            boolean areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled();
            return areNotificationsEnabled;
        }

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
        /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    //打开手机设置页面

    /**
     * 假设没有开启通知权限，点击之后就需要跳转到 APP的通知设置界面，对应的Action是：Settings.ACTION_APP_NOTIFICATION_SETTINGS, 这个Action是 API 26 后增加的
     * 如果在部分手机中无法精确的跳转到 APP对应的通知设置界面，那么我们就考虑直接跳转到 APP信息界面，对应的Action是：Settings.ACTION_APPLICATION_DETAILS_SETTINGS
     */
    private static void gotoSet(Context context) {

        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else {
            // 其他
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /*=====================添加Listener回调================================*/
    public interface OnNextLitener {
        /**
         * 不需要设置通知的下一步
         */
        void onNext();
    }

    private OnNextLitener mOnNextLitener;

    public void setOnNextLitener(OnNextLitener mOnNextLitener) {
        this.mOnNextLitener = mOnNextLitener;
    }


    private static void showSettingDialog(final Context mContext) {
        //提示弹窗
        Dialog dialog = new AlertDialog.Builder(mContext)
                .setTitle("开启推送实时接收维保信息")
                .setPositiveButton("通知我", (dialog12, which) -> {
                    gotoSet(mContext);
                    dialog12.dismiss();
                })
                .setNegativeButton("暂不需要", (dialog1, which) -> {
                    dialog1.dismiss();
                })
                .create();
        dialog.show();
    }

    static int clickNotificationCode = 888;

    /**
     * 接受到对应消息后，消息的弹出处理
     */
    public static void buildNotification(Context context, CPushMessage message) {
        NotificationManager notificationManager = MyApplication.createNotificationChannel();
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_cus_notif);
        remoteViews.setImageViewResource(R.id.m_icon, R.mipmap.ic_launcher);
        remoteViews.setTextViewText(R.id.m_title, message.getTitle());
        remoteViews.setTextViewText(R.id.m_text, message.getContent());
        remoteViews.setTextViewText(R.id.text, new SimpleDateFormat("HH:mm").format(new Date()));

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, getPackageName());
        } else {
            builder = new Notification.Builder(context);
        }

        Intent appIntent;
        // 通知栏点击打开方式
        UserEntity userData = UserManager.getInstance().userData;
        if (U.isNotEmpty(userData)) {
            EventBus.getDefault().post(new ToUIEvent(ToUIEvent.MESSAGE_EVENT, 0));
            if (userData.getFirstIdentity().equals("2")) {// 2工作人员
                appIntent = new Intent(context, MainWorkActivity.class);
            } else {
                appIntent = new Intent(context, MainActivity.class);
            }
//            appIntent.setAction(Intent.ACTION_MAIN);
//            appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//关键的一步，设置启动模式
            PendingIntent contentIntent = PendingIntent.getActivity(context, clickNotificationCode, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
        }

        builder.setContent(remoteViews)
                .setContentTitle(message.getTitle())
                .setContentText(message.getContent())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        builder.build().flags = Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(message.hashCode(), builder.build());

//        MediaPlayer player = null;
//        if (player == null) {
//            player = new MediaPlayer();
//        }
//        try {
//            AssetFileDescriptor fileDescriptor = context.getResources().getAssets().openFd("xg_ring.wav");
//            player.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getStartOffset());//fileDescriptor.getLength());
//            player.prepare();
//            player.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
