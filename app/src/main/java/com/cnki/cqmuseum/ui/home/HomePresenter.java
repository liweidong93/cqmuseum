package com.cnki.cqmuseum.ui.home;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.cnki.cqmuseum.base.BasePresenterImpl;
import com.cnki.cqmuseum.manager.RobotManager;
import com.cnki.cqmuseum.utils.RxPermissionUtils;

/**
 * 首页P层
 * Created by liweidong on 2019/10/23.
 */

public class HomePresenter extends BasePresenterImpl<IHomeView>{

    //定义动画对象
    private ObjectAnimator rotation;

    public HomePresenter(IHomeView view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
        //打开机器人识别通道
        RobotManager.speak("您好，很高兴为您服务！");
        RobotManager.recognize(mContext);
        //检查读写权限，6.0以上系统使用
        checkPermission();
    }

    /**
     * 检查读写权限
     */
    private void checkPermission() {
        RxPermissionUtils.checkPermissions((Activity) mContext, new RxPermissionUtils.OnPermissionCallback() {
            @Override
            public void onSuccess() {

            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /**
     * 设置语音讲解服务动画
     * @param mImageViewSpeakServer
     */
    public void setSpeakServerAnim(final ImageView mImageViewSpeakServer){
        mImageViewSpeakServer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            boolean isRun = true;
            @Override
            public void onGlobalLayout() {
                if (isRun){
                    isRun = false;
                    int rotationX = mImageViewSpeakServer.getMeasuredWidth() / 2;
                    mImageViewSpeakServer.setPivotX(rotationX);
                    mImageViewSpeakServer.setPivotY(0);
                    rotation = ObjectAnimator.ofFloat(mImageViewSpeakServer,"rotation",10f,-10f);
                    rotation.setRepeatCount(-1);
                    rotation.setInterpolator(new LinearInterpolator());
                    rotation.setRepeatMode(ValueAnimator.REVERSE);
                    rotation.setDuration(3000);
                    rotation.start();
                }
            }
        });
    }

    /**
     * 取消掉动画
     */
    public void cancelAnimator(){
        if (rotation != null && rotation.isRunning()){
            rotation.cancel();
        }
    }
}
