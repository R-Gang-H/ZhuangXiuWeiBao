package com.zhuangxiuweibao.app.common.utils;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.ViewPropertyTransition;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

@SuppressLint("CheckResult")
public class GlideUtils {

    /**
     * GC
     */
    public static void gcAndFinalize() {
        Runtime runtime = Runtime.getRuntime();
        System.gc();
        runtime.runFinalization();
        System.gc();
    }

    static ViewPropertyTransition.Animator animationObject = view -> {
        view.setAlpha(0f);
        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeAnim.setDuration(233);
        fadeAnim.start();
    };

    public static RequestOptions getPhotoImageOption(Context context, int defaultImage) {
        //通过RequestOptions扩展功能
        RequestOptions options = new RequestOptions();
        options.placeholder(defaultImage)//预览图片
                .error(defaultImage);// 加载失败时显示的图片
        return options;
    }

    public static void setGlideImg(Context context, String url, int defaultImage, ImageView imageView) {
        Glide.with(context).load(url).apply(getPhotoImageOption(context, defaultImage)).into(imageView);
    }

    // 圆角
    public static void getBitmap(Context context, String url, int defaultImage, final ImageView imageView) {
        //  获取Bitmap
        Glide.with(context).asBitmap().load(url).apply(getPhotoImageOption(context, defaultImage)).into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                super.setResource(resource);
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCornerRadius(20); //设置圆角弧度
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    // 圆角
    public static void getBitmap(Context context, String url, int defaultImage, SimpleTarget<Bitmap> simpleTarget) {
        //  获取Bitmap
        Glide.with(context).asBitmap().load(url).apply(getPhotoImageOption(context, defaultImage)).into(simpleTarget);
    }

}
