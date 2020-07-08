package com.zhuangxiuweibao.app.ui.activity;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.bean.MessageEntity;
import com.zhuangxiuweibao.app.common.utils.DateUtils;
import com.zhuangxiuweibao.app.common.utils.U;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.LayoutManager;
import com.zhuangxiuweibao.app.common.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.zhuangxiuweibao.app.ui.adapter.BigImagePagerAdapter;
import com.zhuangxiuweibao.app.ui.adapter.ImgListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 42 图片详情
 */
public class ImgDetailsActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_title)
    TextView mtvTitle;
    @BindView(R.id.vp_big_image)
    ViewPager vpBigImage;
    @BindView(R.id.tv_uploader)
    TextView mtvUploader;
    @BindView(R.id.tv_time)
    TextView mtvTime;
    @BindView(R.id.xre_list)
    RecyclerView mxreList;

    private ImgListAdapter adapter;
    private List<String> list = new ArrayList<>();
    private MessageEntity orderData;

    @Override
    public int getLayoutId() {
        return R.layout.activity_img_details;
    }

    @Override
    public void initView() {
        mtvTitle.setText("图片详情");
        LayoutManager.getInstance().init(this).initRecyclerView(mxreList, false);
    }

    @Override
    public void initData() {
        orderData = (MessageEntity) getIntent().getSerializableExtra("orderData");
        if (U.isNotEmpty(orderData)) {
            initList();
            mtvUploader.setText(String.format("上传者:\t\t%s", orderData.getAddUserName()));
            String AddTime = orderData.getAddTime();
            String time = orderData.getTime();
            String createTime = U.isNotEmpty(AddTime) ? AddTime : time;
            String createAt = orderData.getCreateAt();
            mtvTime.setText(DateUtils.getDateToString(Long.valueOf(U.isNotEmpty(createTime) ?
                    createTime : createAt), "yyyy/MM/dd HH:mm"));
        }
    }

    private void initList() {
        int index = getIntent().getIntExtra("index", 0);
        list.clear();
        list.addAll(Arrays.asList(orderData.getImages().split("#")));
        adapter = new ImgListAdapter(list, this, this);
        mxreList.setAdapter(adapter);

        BigImagePagerAdapter mAdapter = new BigImagePagerAdapter(list, this);
        vpBigImage.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        vpBigImage.setAdapter(mAdapter);
        vpBigImage.setCurrentItem(index);
    }


    @OnClick(R.id.rl_back_button)
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        vpBigImage.setCurrentItem(position);
    }
}
