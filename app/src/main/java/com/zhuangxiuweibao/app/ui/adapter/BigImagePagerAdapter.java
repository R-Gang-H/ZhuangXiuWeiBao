package com.zhuangxiuweibao.app.ui.adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.luck.picture.lib.photoview.PhotoView;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.utils.GlideUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * haorugiang on 2019-1-9 11:42:34
 */

public class BigImagePagerAdapter extends PagerAdapter {

    public static boolean CheckIsImage(String strUrl) {
        String imageReg = "(?i).+?\\.(jpg|jpeg|gif|bmp|png)";
        return strUrl.matches(imageReg);
    }

    ImageView mPlayBtn;
    private PhotoView mPhotoView;

    Activity mContext;

    private ArrayList<String> arrUrlList;

    public BigImagePagerAdapter(List datas, Activity context) {
        arrUrlList = new ArrayList<>();
        arrUrlList.addAll(datas);
        mContext = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_big_image, container, false);
        mPlayBtn = view.findViewById(R.id.iv_play_btn);
        mPhotoView = view.findViewById(R.id.img);

        final String strUrl = arrUrlList.get(position);
        if (CheckIsImage(strUrl)) {
            mPlayBtn.setVisibility(View.GONE);

            // 启用图片缩放功能
            mPhotoView.setEnabled(true);//.enable();

            mPhotoView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            mPhotoView.setOnClickListener(v -> {
                mContext.finish();
                mContext.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });
        } else {
            mPlayBtn.setVisibility(View.VISIBLE);

            // 禁用图片缩放功能
            mPhotoView.setEnabled(false);//.disenable();

            mPhotoView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            mPhotoView.setOnClickListener(v -> {
            });
        }

        GlideUtils.setGlideImg(mContext, strUrl, R.mipmap.ic_shequ_bg, mPhotoView);
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return arrUrlList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

}