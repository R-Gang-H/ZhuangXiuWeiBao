package com.zhuangxiuweibao.app.ui.activity;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.BaseDataModel;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.bean.UserEntity;
import com.zhuangxiuweibao.app.common.AppManager;
import com.zhuangxiuweibao.app.common.http.HttpManager;
import com.zhuangxiuweibao.app.common.http.callback.HttpCallBack;
import com.zhuangxiuweibao.app.common.user.ToUIEvent;
import com.zhuangxiuweibao.app.common.user.UserManager;
import com.zhuangxiuweibao.app.common.utils.GlideUtils;
import com.zhuangxiuweibao.app.common.utils.LogUtils;
import com.zhuangxiuweibao.app.common.utils.NotificationUtil;
import com.zhuangxiuweibao.app.common.utils.ResourcesUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.LoadingFrameView;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.ui.adapter.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import butterknife.Setter;

/**
 * 主界面消息流（住户版）
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.iv_title_left)
    RoundedImageView ivTitleLeft;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.title_line)
    View titleLine;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.ll_btn_notify_tag)
    LinearLayout llBtnNotifyTag;
    @BindView(R.id.ll_btn_community_tag)
    LinearLayout llBtnCommunityTag;
    @BindView(R.id.xrecyclerview)
    XRecyclerView xrecyclerview;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;
    @BindView(R.id.rl_other_btn)
    RelativeLayout rlOtherBtn;

    @BindViews({R.id.ll_all_btn, R.id.ll_maintain_btn, R.id.ll_notify_btn, R.id.ll_community_btn, R.id.ll_sos_btn, R.id.ll_other_btn})
    List<TextView> radioTextView;
    private int position;

    @BindViews({R.id.btn_notice, R.id.btn_action, R.id.btn_opinion})
    List<Button> radioNoticeButtons;

    @BindViews({R.id.btn_something, R.id.btn_unused, R.id.btn_server})
    List<Button> radioCommunityButtons;

    MessageAdapter adapter;
    ArrayList<MessageEntity> msgData = new ArrayList<>();
    public static String type = "1";// 1全部 2维保 3通知 4社区交流 5通知公告 6活动报名 7意见建议 8寻物启事 9闲置物品 10为你服务
    private String xiaoqu = "1";// 1当前用户所在小区 2其他小区
    private UserEntity userData;
    private String Status;
    private String keyword;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    public void initView() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etSearch.length() > 0) {
                    // 获得焦点
                    etSearch.setTextAlignment(TextView.TEXT_ALIGNMENT_VIEW_START);
                    ivSearch.setVisibility(View.GONE);
                } else if (etSearch.length() == 0) {
                    // 失去焦点
                    etSearch.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                    ivSearch.setVisibility(View.VISIBLE);
                }
            }
        });
        etSearch.setOnEditorActionListener((v, actionId, event) -> {//搜索框内容监听
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                keyword = etSearch.getText().toString().trim();
                U.hideKeyboard(MainActivity.this);
                // 搜索
                UserMsgList(true);
                return true;
            }
            return false;
        });
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        titleLine.setVisibility(View.GONE);
        UserManager.getInstance().xgPush(this);//信鸽
        LayoutManager.getInstance().init(this).iniXrecyclerView(xrecyclerview);
        adapter = new MessageAdapter(msgData, this);
        xrecyclerview.setAdapter(adapter);
        xrecyclerview.setPullRefreshEnabled(true);
        xrecyclerview.setLoadingMoreEnabled(true);
        xrecyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                UserMsgList(true);
            }

            @Override
            public void onLoadMore() {
                pageIndex++;
                UserMsgList(false);
            }
        });
        radioTextView.get(0).setOnClickListener(this);
        radioTextView.get(1).setOnClickListener(this);
        radioTextView.get(2).setOnClickListener(this);
        radioTextView.get(3).setOnClickListener(this);
        radioTextView.get(4).setOnClickListener(this);
        radioTextView.get(5).setOnClickListener(this);
        setChioceItem(position);
        radioNoticeButtons.get(0).setOnClickListener(this);
        radioNoticeButtons.get(1).setOnClickListener(this);
        radioNoticeButtons.get(2).setOnClickListener(this);

        radioCommunityButtons.get(0).setOnClickListener(this);
        radioCommunityButtons.get(1).setOnClickListener(this);
        radioCommunityButtons.get(2).setOnClickListener(this);
    }


    @Override
    public void initData() {
        ivLeft.setVisibility(View.GONE);
        rlNextButton.setVisibility(View.VISIBLE);
        userData = UserManager.getInstance().userData;
        ivTitleRight.setImageResource(R.mipmap.icon_add_msg);
        ivTitleLeft.setCornerRadius(50);
        ivTitleLeft.setBorderColor(ResourcesUtils.getColor(R.color.col_ed));
        ivTitleLeft.setBorderWidth(R.dimen.margin_size_1);
        GlideUtils.setGlideImg(this, userData.getHeadImage(), R.mipmap.icon_default, ivTitleLeft);
//        radioTextView.get(5).setVisibility(userData.getFirstIdentity().equals("3") ? View.VISIBLE : View.GONE);// 社区大妈
        UserMsgList(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //判断是否需要开启通知栏功能
            NotificationUtil.OpenNotificationSetting(mContext, null);
        }
    }

    private void UserMsgList(boolean isClear) {
        HttpManager.getInstance().doUserMsgList(getLocalClassName(),
                type, xiaoqu, pageIndex + "", new HttpCallBack<BaseDataModel<MessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        if (isClear) {
                            msgData.clear();
                        }
                        msgData.addAll(data.getData());
                        adapter.setData(msgData, xiaoqu);
                        if (msgData.size() > 0) {
                            loadView.delayShowContainer(true);
                        } else {
                            if (isClear) {
                                loadView.setNoShown(true);
                            } else {
                                loadView.delayShowContainer(true);
                            }
                        }
                        xrecyclerview.loadMoreComplete();
                        xrecyclerview.refreshComplete();
                        VillageStatus();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("MainActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MainActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("MainActivity", throwable.getMessage());
                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.rl_next_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                startActivity(new Intent(this, MyActivity.class));
                break;
            case R.id.rl_next_button:
                ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this,
                        R.anim.slide_bottom_in, R.anim.slide_bottom_out);
                startActivity(new Intent(this, HouseGeneralServiceActivity.class)
                        .putExtra("Status", Status), options.toBundle());
                break;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        position = savedInstanceState.getInt("position");
        setChioceItem(position);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        //记录当前的position
        super.onSaveInstanceState(outState);
        outState.putInt("position", position);
    }

    public void setChioceItem(int index) {
        this.position = index;
        UserManager.apply(radioTextView, TABSPEC, radioTextView.get(index));
        llBtnNotifyTag.setVisibility(View.GONE);
        llBtnCommunityTag.setVisibility(View.GONE);
        switch (index) {
            case 0:
                //全部
                break;
            case 1:
                //维保
                break;
            case 2:
                // 通知
                llBtnNotifyTag.setVisibility(View.VISIBLE);
                break;
            case 3:
                // 社区交流
                llBtnCommunityTag.setVisibility(View.VISIBLE);
                break;
            case 4:
                // 紧急通知
                break;
            case 5:
                // 其他社区
                llBtnCommunityTag.setVisibility(View.VISIBLE);
                break;
        }
    }

    //控制normal 状态的当前View 隐藏，其它空间仍然为显示
    final Setter<TextView, TextView> TABSPEC = (view, value, index) -> {
        assert value != null;
        if (view.getId() == value.getId()) {
            view.setSelected(true);
            view.setBackground(ResourcesUtils.getDrawable(R.drawable.shape_msg_tab_bg));
        } else {
            view.setSelected(false);
            view.setBackgroundColor(0);
        }
    };

    @Override
    public void onClick(View v) {
        // 1全部 2维保 3通知 4社区交流 5通知公告 6活动报名 7意见建议 8寻物启事 9闲置物品 10为你服务 11紧急求助
        switch (v.getId()) {
            case R.id.ll_all_btn:
                type = "1";
                xiaoqu = "1";
                setChioceItem(0);
                break;
            case R.id.ll_maintain_btn:
                type = "2";
                xiaoqu = "1";
                setChioceItem(1);
                break;
            case R.id.ll_notify_btn:
                type = "3";
                xiaoqu = "1";
                setChioceItem(2);
                break;
            case R.id.ll_community_btn:
                type = "4";
                xiaoqu = "1";
                setChioceItem(3);
                break;
            case R.id.ll_sos_btn:
                type = "11";
                xiaoqu = "1";
                setChioceItem(4);
                break;
            case R.id.ll_other_btn:
                type = "1";
                xiaoqu = "2";
                setChioceItem(5);
                break;
            case R.id.btn_notice:
                type = "5";
                checkStatus(0, radioNoticeButtons);
                break;
            case R.id.btn_action:
                type = "6";
                checkStatus(1, radioNoticeButtons);
                break;
            case R.id.btn_opinion:
                type = "7";
                checkStatus(2, radioNoticeButtons);
                break;
            case R.id.btn_something:
                type = "8";
                checkStatus(0, radioCommunityButtons);
                break;
            case R.id.btn_unused:
                type = "9";
                checkStatus(1, radioCommunityButtons);
                break;
            case R.id.btn_server:
                type = "10";
                checkStatus(2, radioCommunityButtons);
                break;
        }
        pageIndex = 1;
        UserMsgList(true);
    }

    public void checkStatus(int index, List<Button> radio) {
        UserManager.apply(radio, BTNSPEC, radio.get(index));
    }

    //控制normal 状态的当前View 隐藏，其它空间仍然为显示
    final Setter<TextView, TextView> BTNSPEC = (view, value, index) -> {
        assert value != null;
        if (view.getId() == value.getId()) {
            view.setSelected(true);
        } else {
            view.setSelected(false);
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) {
        if ((paramKeyEvent.getAction() == 0)
                && (paramKeyEvent.getKeyCode() == 4)) {
            AppManager.getAppManager().finishAllActivity();
        }
        return super.dispatchKeyEvent(paramKeyEvent);
    }

    private void VillageStatus() {
        HttpManager.getInstance().doVillageStatus("MainActivity",
                userData.getXiaoquId(), new HttpCallBack<BaseDataModel<MessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        Status = data.getData().get(0).getStatus();
                        //社区小喇叭状态 0显示1不显示
                        rlOtherBtn.setVisibility(Status.equals("1") ? View.GONE : View.VISIBLE);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("MainActivity", statusCode + ":" + errorMsg);
                        if (statusCode == 1003) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MainActivity.this);
                            return;
                        }
                        U.showToast(errorMsg);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("MainActivity", throwable.getMessage());
                    }
                });
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        switch (event.getTag()) {
            case ToUIEvent.MESSAGE_EVENT:
                pageIndex = 1;
                UserMsgList(true);
                break;
            case ToUIEvent.USERINFO_EVENT:
                initData();
                break;
        }
    }

}
