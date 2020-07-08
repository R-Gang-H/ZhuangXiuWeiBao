package com.zhuangxiuweibao.app.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuangxiuweibao.app.common.user.ToUIEvent;
import com.zhuangxiuweibao.app.common.utils.permissions.BasePermissionFragment;
import com.zhuangxiuweibao.app.ui.activity.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 基类Fragment
 *
 * @author haoruigang 继承权限父类
 */
public abstract class BaseFragment extends BasePermissionFragment {

    protected BaseActivity mActivity;
    public Context mContext;
    public int pageIndex = 1;
    public String pageSize = "15";
    Handler handler = new Handler();
    HashMap<String, String> map = new HashMap<>();
    private Unbinder unbinder;

    /**
     * 获得全局的，防止使用getActivity()为空
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (BaseActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, view);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);//注册EventBus
        }
        initView(view, savedInstanceState);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /**
     * 该抽象方法就是 onCreateView中需要的layoutId
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 初始化View
     *
     * @param view
     * @param savedInstanceState
     */
    public abstract void initView(View view, Bundle savedInstanceState);

    /**
     * 初始化数据
     */
    public abstract void initData();

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);//反注册EventBus
        }
    }

    //Eventbus
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(ToUIEvent event) {

    }

}
