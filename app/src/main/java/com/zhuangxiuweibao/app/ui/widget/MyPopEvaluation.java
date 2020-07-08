package com.zhuangxiuweibao.app.ui.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.ui.bean.ImgBean;
import com.zhuangxiuweibao.app.ui.widget.myratingbar.MyRatingAdapter;
import com.zhuangxiuweibao.app.ui.widget.myratingbar.MyRatingBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyPopEvaluation {

    @BindView(R.id.rat_1)
    MyRatingBar mrat1;
    @BindView(R.id.tv_score)
    TextView mtvScore;
    @BindView(R.id.rat_2)
    MyRatingBar mrat2;
    @BindView(R.id.tv_score2)
    TextView mtvScore2;
    @BindView(R.id.rat_3)
    MyRatingBar mrat3;
    @BindView(R.id.tv_score3)
    TextView mtvScore3;
    @BindView(R.id.et_content)
    EditText metContent;

    private View view;
    private PopupWindow mPopupWindow;
    private View mRootView;
    private OnSubmitListener mListener;
    private static final String[] score = {"非常不满意", "不满意", "一般", "满意", "非常满意"};
    private String[] str = new String[4];

    public MyPopEvaluation(Context context, View root, OnSubmitListener listener) {
        view = LayoutInflater.from(context).inflate(R.layout.pop_evaluation, null);
        mRootView = root;
        mListener = listener;
        ButterKnife.bind(this, view);
        mPopupWindow = new PopupWindow(context);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setContentView(view);
    }

    public void setData(List list, int which, MyRatingAdapter.OnStarChangeListener listener) {
        switch (which) {
            case 1:
                mrat1.update(list, listener);
                break;
            case 2:
                mrat2.update(list, listener);
                break;
            case 3:
                mrat3.update(list, listener);
                break;
        }
    }

    public void setData(List<ImgBean> list, int which) {
        switch (which) {
            case 1:
                mrat1.update(list, position -> {
                    for (int i = 0; i < 5; i++) {
                        if (i <= position) {
                            list.get(i).setRes(R.mipmap.icon_star);
                        } else {
                            list.get(i).setRes(R.mipmap.icon_star2);
                        }
                    }
                    mtvScore.setText(score[position]);
                    str[0] = String.valueOf(position + 1);
                    mrat1.update(list);
                });
                break;
            case 2:
                mrat2.update(list, position -> {
                    for (int i = 0; i < 5; i++) {
                        if (i <= position) {
                            list.get(i).setRes(R.mipmap.icon_star);
                        } else {
                            list.get(i).setRes(R.mipmap.icon_star2);
                        }
                    }
                    mtvScore2.setText(score[position]);
                    str[1] = String.valueOf(position + 1);
                    mrat2.update(list);
                });
                break;
            case 3:
                mrat3.update(list, position -> {
                    for (int i = 0; i < 5; i++) {
                        if (i <= position) {
                            list.get(i).setRes(R.mipmap.icon_star);
                        } else {
                            list.get(i).setRes(R.mipmap.icon_star2);
                        }
                    }
                    mtvScore3.setText(score[position]);
                    str[2] = String.valueOf(position + 1);
                    mrat3.update(list);
                });
                break;
        }
    }

    public void show() {
        if (mPopupWindow != null) {
            if (!mPopupWindow.isShowing()) {
                mPopupWindow.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
            } else {
                mPopupWindow.dismiss();
            }
        }
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    @OnClick({R.id.img_close, R.id.tv_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                show();
                break;
            case R.id.tv_submit:
                str[3] = metContent.getText().toString();
                if (mListener != null) mListener.submit(str);
                break;
        }
    }

    public interface OnSubmitListener {
        void submit(String str[]);
    }
}
