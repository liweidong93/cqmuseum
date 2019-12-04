package com.cnki.cqmuseum.manager;

import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;

import com.cnki.cqmuseum.interf.FloatCallBack;
import com.cnki.cqmuseum.service.GuideFloatButtonService;
import com.cnki.cqmuseum.view.GuideFloatButton;

/**
 * 悬浮窗与activity界面交互管理类
 * Created by liweidong on 2019/10/26.
 */

public class FloatButtonManager {

    private FloatCallBack mFloatCallBack;

    private FloatButtonManager() {
    }

    public static FloatButtonManager getInstance() {
        return LittleMonkProviderHolder.sInstance;
    }

    // 静态内部类
    private static class LittleMonkProviderHolder {
        private static final FloatButtonManager sInstance = new FloatButtonManager();
    }


    /**
     * 开启服务悬浮窗
     */
    public void startFloatServer(Context context) {
        Intent intent = new Intent(context, GuideFloatButtonService.class);
        context.startService(intent);
    }

    /**
     * 关闭悬浮窗
     */
    public void stopFloatServer(Context context) {
        Intent intent = new Intent(context, GuideFloatButtonService.class);
        context.stopService(intent);
    }

    /**
     * 注册监听
     */
    public void registerCallLittleMonk(FloatCallBack callLittleMonk) {
        mFloatCallBack = callLittleMonk;
    }

    /**
     * 调用引导的方法
     */
    public void callGuide(int type) {
        if (mFloatCallBack == null) return;
        mFloatCallBack.guideUser(type);
    }

    /**
     * 悬浮窗的显示
     */
    public void show() {
        if (mFloatCallBack == null) return;
        mFloatCallBack.show();
    }

    /**
     * 悬浮窗的隐藏
     */
    public void hide() {
        if (mFloatCallBack == null) return;
        mFloatCallBack.hide();
    }

    /**
     * 获取悬浮窗的位置参数
     * @return
     */
    public WindowManager.LayoutParams getFloatBtnParams(){
       return MyWindowManager.getFloatBtnParams();
    }
}
