package com.zhuangxiuweibao.app.ui.activity;

import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.user.Constant;
import com.zhuangxiuweibao.app.common.utils.U;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by haoruigang on 2019-3-8 18:19:23.
 * 隐私说明/用户协议
 * type: 1.隐私说明 2. 用户协议
 */

public class AboutUsActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.wb_xy)
    WebView wb_xy;

    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }


    @Override
    public void initView() {
        String type = getIntent().getStringExtra("type");
        if (type.equals("1")) {// 隐私说明
            tvTitle.setText("隐私条款");
            setUrl(Constant.PRIVACY);
        } else if (type.equals("2")) {// 用户协议
            tvTitle.setText("用户协议");
            setUrl(Constant.AGREEMENT);
        } else if (type.equals("3")) {// 查看链接
            String linkUrl = getIntent().getStringExtra("linkUrl");
            tvTitle.setText("查看链接");
            if (U.isNotEmpty(linkUrl)) {
                setUrl(linkUrl);
            }
        }
    }

    private void setUrl(String url) {
        //声明WebSettings子类
        WebSettings webSettings = wb_xy.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        //步骤3. 复写shouldOverrideUrlLoading()方法，使得打开网页时不调用系统浏览器， 而是在本WebView中显示
        wb_xy.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        wb_xy.loadUrl(url);
    }

    @Override
    public void initData() {

    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }

}
