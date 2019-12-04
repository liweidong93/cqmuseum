package com.cnki.cqmuseum.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.cnki.cqmuseum.bean.BaseEvenBusBean;
import com.cnki.cqmuseum.constant.EvenBusConstant;
import com.cnki.cqmuseum.interf.FloatCallBack;
import com.cnki.cqmuseum.manager.FloatButtonManager;
import com.cnki.cqmuseum.manager.MyWindowManager;
import com.cnki.cqmuseum.receiver.HomeWatcherReceiver;
import com.cnki.cqmuseum.ui.chat.ChatActivity;
import com.cnki.cqmuseum.ui.guide.GuideActivity;
import com.cnki.cqmuseum.ui.updateapk.UpdateApkActivity;
import com.cnki.cqmuseum.utils.ToastUtils;
import com.cnki.cqmuseum.view.GuideFloatButton;

import org.greenrobot.eventbus.EventBus;

/**
 * 悬浮窗服务
 * Created by liweidong on 2019/10/26.
 */

public class GuideFloatButtonService extends Service implements FloatCallBack{

    private HomeWatcherReceiver homeWatcherReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        //注册监听
        FloatButtonManager.getInstance().registerCallLittleMonk(this);
        //注册广播接受者
        homeWatcherReceiver = new HomeWatcherReceiver();
        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(homeWatcherReceiver, homeFilter);
        //初始化悬浮窗
        initFloatButtonUI();
    }

    /**
     * 初始化悬浮窗
     */
    private void initFloatButtonUI() {
        MyWindowManager.createFloatButton(this);
        MyWindowManager.setOnFloatBtnListener(new GuideFloatButton.OnFloatBtnClickListener() {
            @Override
            public void onClick() {
                //跳转到导航界面
                EventBus.getDefault().post(new BaseEvenBusBean<>(EvenBusConstant.EVENBUS_STARTGUIDE));
            }
        });
        //隐藏掉悬浮窗口
        MyWindowManager.hide();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyWindowManager.removeFloatWindowManager();
        //移除广播
        if (homeWatcherReceiver != null){
            unregisterReceiver(homeWatcherReceiver);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void guideUser(int type) {

    }

    @Override
    public void show() {
        MyWindowManager.show();
    }

    @Override
    public void hide() {
        MyWindowManager.hide();
    }

}
