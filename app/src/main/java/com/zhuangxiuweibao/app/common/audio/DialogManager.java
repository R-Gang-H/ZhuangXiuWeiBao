package com.zhuangxiuweibao.app.common.audio;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.zhuangxiuweibao.app.R;

public class DialogManager {
    private AlertDialog.Builder builder;
    private ProgressBar mIcon;
    //    private ImageView mVoice;
    private TextView mLable;

    private Context context;

    private AlertDialog dialog;//用于取消AlertDialog.Builder

    /**
     * 构造方法 传入上下文
     */
    public DialogManager(Context context) {
        this.context = context;
    }

    // 显示录音的对话框
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showRecordingDialog() {
        builder = new AlertDialog.Builder(context, R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_recorder, null);

        mIcon = (ProgressBar) view.findViewById(R.id.id_recorder_dialog_icon);
        mLable = (TextView) view.findViewById(R.id.id_recorder_dialog_label);
//        GlideUtil.loadSquarePicture(R.drawable.gif_record, mIcon);
        builder.setView(view);
        builder.create();
        dialog = builder.show();
        dialog.setCanceledOnTouchOutside(false);//设置点击外部不消失
    }

    public void recording() {
        if (dialog != null && dialog.isShowing()) { //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
//            GlideUtil.loadSquarePicture(R.drawable.gif_record, mIcon);
//            mIcon.setImageResource(R.mipmap.orange_arrow_up);
            mLable.setText("手指上滑，取消发送");
        }
    }

    // 显示想取消的对话框
    public void wantToCancel() {
        if (dialog != null && dialog.isShowing()) { //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
//            mIcon.setImageResource(R.mipmap.icon_cancel);
            mLable.setText("松开手指，取消发送");
        }
    }

    public void updateTime(int time) {
        if (dialog != null && dialog.isShowing()) { //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
            mLable.setText(time + 1 + "s");
        }
    }

    // 显示时间过短的对话框
    public void tooShort() {
        if (dialog != null && dialog.isShowing()) { //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
            mLable.setText("录音时间过短");
        }
    }

    // 显示取消的对话框
    public void dimissDialog() {
        if (dialog != null && dialog.isShowing()) { //显示状态
            dialog.dismiss();
            dialog = null;
        }
    }

    // 显示更新音量级别的对话框
    public void updateVoiceLevel(int level) {
        if (dialog != null && dialog.isShowing()) { //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
//          在这里没有做操作
//可以再这里根据音量大小，设置图片，实现效果；
        }
    }
}
