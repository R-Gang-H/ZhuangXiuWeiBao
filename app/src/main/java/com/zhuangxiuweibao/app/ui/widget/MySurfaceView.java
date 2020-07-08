package com.zhuangxiuweibao.app.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zhuangxiuweibao.app.MyApplication;
import com.zhuangxiuweibao.app.R;
import com.zhuangxiuweibao.app.common.utils.U;

import java.util.List;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.AutoFocusCallback {

    private Context mContext;
    private Camera mCamera;

    private int mScreenWidth;//屏幕宽度
    private int mScreenHeight;//屏幕高度

    private Camera.Parameters parameters = null;

    public MySurfaceView(Context context) {
        super(context);
        mContext = context;
        getScreenMetrix(context);
        initView();
        initDraw();
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        getScreenMetrix(context);
        initView();
        initDraw();
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        getScreenMetrix(context);
        initView();
        initDraw();
    }

    //Android6.0以下的摄像头权限处理：
    public boolean isCameraCanUse() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            // setParameters 是针对魅族MX5 做的。MX5 通过Camera.open() 拿到的Camera
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            canUse = false;
        }
        if (mCamera != null) {
            mCamera.release();
        }
        return canUse;
    }

    /**
     * 记录上次两指之间的距离
     */
    private double lastFingerDis;
    private double fingerDis;

    //当前缩放值
    private int currentZoom;
    //最大缩放值
    private int maxZoom;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    // 当有两个手指按在屏幕上时，计算两指之间的距离
                    lastFingerDis = distanceBetweenFingers(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 2) {
                    currentZoom = parameters.getZoom();
                    // 有两个手指按在屏幕上移动时，为缩放状态
                    centerPointBetweenFingers(event);
                    fingerDis = distanceBetweenFingers(event);
                    //状态
                    String currentStatus;
                    //状态值
                    String STATUS_ZOOM_OUT = "1";
                    String STATUS_ZOOM_IN = "2";
                    if (fingerDis > lastFingerDis) {
                        currentStatus = STATUS_ZOOM_OUT;
                    } else {
                        currentStatus = STATUS_ZOOM_IN;
                    }
                    if (parameters.isZoomSupported()) {
                        //缩放
                        if (currentStatus.equals(STATUS_ZOOM_OUT) && currentZoom < maxZoom) {
                            currentZoom++;
                            if (currentZoom < maxZoom) {
                                parameters.setZoom(currentZoom);
                                mCamera.setParameters(parameters);
                            }
                        } else if (currentStatus.equals(STATUS_ZOOM_IN) && currentZoom < maxZoom) {
                            currentZoom--;
                            if (currentZoom > 0) {
                                parameters.setZoom(currentZoom);
                                mCamera.setParameters(parameters);
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                lastFingerDis = fingerDis;
                break;
            case MotionEvent.ACTION_DOWN:
                clearCameraFocus();
                focusOnTouch(event);
                break;
        }
        return true;
    }

    /**
     * TODO-------------------------------------点击聚焦同时画出一个方块(边界未作处理)-----------------------------------------------
     */
    private Rect calculateTapArea(float x, float y, float coefficient) {
        float focusAreaSize = 200;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerX = (int) ((x / getResolution().width) * 2000 - 1000);
        int centerY = (int) ((y / getResolution().height) * 2000 - 1000);
        int left = clamp(centerX - (areaSize / 2), -1000, 1000);
        int top = clamp(centerY - (areaSize / 2), -1000, 1000);
        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    public Camera.Size getResolution() {
        Camera.Parameters params = mCamera.getParameters();
        return params.getPreviewSize();
    }

    /**
     * 清除自动对焦
     */
    private void clearCameraFocus() {
        mCamera.cancelAutoFocus();
        parameters.setFocusAreas(null);
        parameters.setMeteringAreas(null);
        try {
            mCamera.setParameters(parameters);
        } catch (Exception e) {
        }
    }

    public void focusOnTouch(MotionEvent event) {
//        Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f);
//        Rect meteringRect = calculateTapArea(event.getX(), event.getY(), 1.5f);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//        if (parameters.getMaxNumFocusAreas() > 0) {
//            List<Camera.Area> focusAreas = new ArrayList<>();
//            focusAreas.add(new Camera.Area(focusRect, 200));
//            parameters.setFocusAreas(focusAreas);
//        }
//        if (parameters.getMaxNumMeteringAreas() > 0) {
//            List<Camera.Area> meteringAreas = new ArrayList<>();
//            meteringAreas.add(new Camera.Area(meteringRect, 300));
//            parameters.setMeteringAreas(meteringAreas);
//        }
        mCamera.cancelAutoFocus();


        mCamera.setParameters(parameters);
        mCamera.autoFocus(this);
        int x = (int) event.getX();
        int y = (int) event.getY();
        mRectf = new RectF(x - 80, y - 80, x + 80, y + 80);
        invalidate();
    }

    private void initDraw() {
        mRectf = new RectF(0, 0, 0, 0);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//消除锯齿
        mPaint.setStrokeWidth(1);//设置画笔的宽度
        mPaint.setStyle(Paint.Style.STROKE);//设置绘制轮廓
        mPaint.setColor(mContext.getResources().getColor(R.color.color_f));
    }

    private RectF mRectf;
    private Paint mPaint;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(mRectf, mPaint);
    }


    /**
     * TODO -------------------------------------------缩放处理-----------------------------------------------------
     * 计算两个手指之间的距离。
     *
     * @param event
     * @return 两个手指之间的距离
     */
    private double distanceBetweenFingers(MotionEvent event) {
        float disX = Math.abs(event.getX(0) - event.getX(1));
        float disY = Math.abs(event.getY(0) - event.getY(1));
        return Math.sqrt(disX * disX + disY * disY);
    }

    /**
     * 计算两个手指之间中心点的坐标。
     *
     * @param event
     */
    private void centerPointBetweenFingers(MotionEvent event) {
        float xPoint0 = event.getX(0);
        float yPoint0 = event.getY(0);
        float xPoint1 = event.getX(1);
        float yPoint1 = event.getY(1);
        /**
         * 记录两指同时放在屏幕上时，中心点的横坐标值
         */
        float centerPointX = (xPoint0 + xPoint1) / 2;
        /**
         * 记录两指同时放在屏幕上时，中心点的纵坐标值
         */
        float centerPointY = (yPoint0 + yPoint1) / 2;
    }


    // TODO ------------------------------------初始化-----------------------------
    //获取宽高
    private void getScreenMetrix(Context context) {
        mScreenWidth = MyApplication.getScreenInfo(context).widthPixels;
        mScreenHeight = MyApplication.getScreenInfo(context).heightPixels;
    }

    private void initView() {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置类型
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setWillNotDraw(false);
        if (mCamera == null) {
            try {
                mCamera = Camera.open();//开启相机
                parameters = mCamera.getParameters();
                setCameraParams(mScreenWidth, mScreenHeight);
                mCamera.setPreviewDisplay(holder);//摄像头画面显示在Surface上
                currentZoom = parameters.getZoom();
                maxZoom = parameters.getMaxZoom();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();//停止预览
        mCamera.release();//释放相机资源
        mCamera = null;
    }


    //设置相机参数
    private void setCameraParams(int width, int height) {
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> focusModes = parameters.getSupportedFocusModes();
        // 获取摄像头支持的PictureSize列表
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        /**从列表中选取合适的分辨率*/
        Camera.Size picSize = getCloselyPreSize(true, width, height, pictureSizeList);
        if (null == picSize) {
            picSize = parameters.getPictureSize();
        }
        // 根据选出的PictureSize重新设置SurfaceView大小
        parameters.setPictureSize(picSize.width, picSize.height);
        // 获取摄像头支持的PreviewSize列表
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();

        Camera.Size preSize = getCloselyPreSize(true, width, height, previewSizeList);
        if (null != preSize) {
            parameters.setPreviewSize(preSize.width, preSize.height);
        }
//        //TODO 预览尺寸,解决预览比例失调,可以传画布的宽高,由于翻转90度,此时宽和高相互调换.
//        //TODO 文档解释:在预览前设置使用,如果预览已经开始,请停止并设置,在开启.
        parameters.setJpegQuality(100); // 设置照片质量
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
        }
        mCamera.cancelAutoFocus();//自动对焦。
        // 设置PreviewDisplay的方向，效果就是将捕获的画面旋转多少度显示
        // TODO 这里直接设置90°不严谨，具体见https://developer.android.com/reference/android/hardware/Camera.html#setPreviewDisplay%28android.view.SurfaceHolder%29
        mCamera.setDisplayOrientation(90);
        mCamera.setParameters(parameters);
    }


    /**
     * 通过对比得到与宽高比最接近的预览尺寸（如果有相同尺寸，优先选择）
     *
     * @param isPortrait    是否竖屏
     * @param surfaceWidth  需要被进行对比的原宽
     * @param surfaceHeight 需要被进行对比的原高
     * @param preSizeList   需要对比的预览尺寸列表
     * @return 得到与原宽高比例最接近的尺寸
     */
    public static Camera.Size getCloselyPreSize(boolean isPortrait, int surfaceWidth, int surfaceHeight, List<Camera.Size> preSizeList) {
        int reqTmpWidth;
        int reqTmpHeight;
        // 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
        if (isPortrait) {
            reqTmpWidth = surfaceHeight;
            reqTmpHeight = surfaceWidth;
        } else {
            reqTmpWidth = surfaceWidth;
            reqTmpHeight = surfaceHeight;
        }
        //先查找preview中是否存在与surfaceview相同宽高的尺寸
        for (Camera.Size size : preSizeList) {
            if ((size.width == reqTmpWidth) && (size.height == reqTmpHeight)) {
                return size;
            }
        }

        // 得到与传入的宽高比最接近的size
        float reqRatio = ((float) reqTmpWidth) / reqTmpHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : preSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }
        return retSize;
    }


    //拍照
    public void takePicture(OnResult listener) {
        mListener = listener;
        // 当调用camera.takePiture方法后，camera关闭了预览，这时需要调用startPreview()来重新开启预览
        if (mCamera != null) {
            restore();
            mCamera.takePicture(null, null, jpeg);
        }
    }

    //  恢复预览
    public void restore() {
        if (mCamera != null)
            mCamera.startPreview();// 开启预览
    }

    public void setAutoFocus() {
        mCamera.autoFocus(this);
    }

    /**
     * 控制闪光灯开关
     *
     * @param isEnable
     */
    public void isLightEnable(boolean isEnable) {
        if (mCamera != null) {
            parameters = mCamera.getParameters();
            if (isEnable) {
                setParameter(Camera.Parameters.FLASH_MODE_TORCH);
            } else {
                setParameter(Camera.Parameters.FLASH_MODE_OFF);
            }
        }
    }


    /**
     * 设置闪光灯
     *
     * @param parameter 闪光灯类型
     */
    public void setParameter(String parameter) {
        parameters.setFlashMode(parameter);
        mCamera.setParameters(parameters);
    }

    /**
     * 裁剪并旋转
     *
     * @param bm
     * @return
     */
    public Bitmap editBitmap(Bitmap bm, String str) {
        Matrix matrix = new Matrix();
        int width = bm.getWidth();
        int height = bm.getHeight();
        int clipHeight;
        if (TextUtils.isEmpty(str)) {
            matrix.postRotate(90);
            clipHeight = (int) (U.dip2px(getContext(), 376) * 1.0 / mScreenHeight * width);
            return Bitmap.createBitmap(bm, 0, 0, clipHeight, height, matrix, true);
        } else {
            clipHeight = (int) (U.dip2px(getContext(), 376) * 1.0 / mScreenHeight * height);
            return Bitmap.createBitmap(bm, 0, 0, width, clipHeight, matrix, true);
        }
    }

    //创建jpeg图片回调数据对象
    private Camera.PictureCallback jpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera Camera) {
            try {
                // 获得图片
                Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                if (mListener != null) mListener.result(bm);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    mCamera.stopPreview();// 关闭预览
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };

    @Override
    public void onAutoFocus(boolean success, Camera camera) {

    }

    OnResult mListener;

    public interface OnResult {
        void result(Bitmap bitmap);
    }
}
