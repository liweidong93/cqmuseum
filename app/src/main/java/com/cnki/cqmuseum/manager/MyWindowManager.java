package com.cnki.cqmuseum.manager;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;

import com.cnki.cqmuseum.view.GuideFloatButton;

/**
 * 悬浮窗统一管理类，与悬浮窗交互真正实现
 * Created by liweidong on 2019/10/26.
 */

public class MyWindowManager {

    private static WindowManager mWindowManager;
    private static GuideFloatButton mFloatButton;
    private static boolean mHasShown;
    private static WindowManager.LayoutParams params;

    public static void createFloatButton(Context context){
        params = new WindowManager.LayoutParams();
        mWindowManager = getWindowManager(context);
        mFloatButton = new GuideFloatButton(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //设置图片格式，效果为背景透明
        params.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为右侧中间
        params.gravity = Gravity.LEFT | Gravity.TOP;

        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        //窗口的宽度
        int screenWidth = dm.widthPixels;
        //窗口高度
        int screenHeight = dm.heightPixels;
        //以屏幕左上角为原点，设置x、y初始值，相对于gravity
        params.x = screenWidth;
        params.y = screenHeight / 2;

        //设置悬浮窗口长宽数据
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mFloatButton.setParams(params, screenWidth);
        mWindowManager.addView(mFloatButton, params);
        mHasShown = true;
    }

    /**
     * 返回当前已创建的WindowManager。
     */
    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    /**
     * 设置点击事件
     * @param onFloatBtnListener
     */
    public static void setOnFloatBtnListener(GuideFloatButton.OnFloatBtnClickListener onFloatBtnListener){
        mFloatButton.setOnClickListener(onFloatBtnListener);
    }

    /**
     * 移除悬浮窗
     */
    public static void removeFloatWindowManager() {
        //移除悬浮窗口
        boolean isAttach = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isAttach = mFloatButton.isAttachedToWindow();
        }
        if (mHasShown && isAttach && mWindowManager != null)
            mWindowManager.removeView(mFloatButton);
    }

    public static void hide() {
        if (mHasShown)
            mWindowManager.removeViewImmediate(mFloatButton);
        mHasShown = false;
    }

    public static void show() {
        if (!mHasShown)
            mWindowManager.addView(mFloatButton, params);
        mHasShown = true;
    }

    public static WindowManager.LayoutParams getFloatBtnParams(){
        return params;
    }
}
