package com.cnki.cqmuseum.application;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.cnki.cqmuseum.manager.RobotManager;
import com.ubtechinc.cruzr.assistant.sdk.AssistantManager;
import com.ubtechinc.cruzr.media_sdk.manager.MediaPlayManager;
import com.ubtechinc.cruzr.sys.cruzrleisure.leisure.LeisureManager;
import com.ubtrobot.Robot;

import static java.security.AccessController.getContext;


/**
 * applicaiton
 * Created by admin on 2019/6/3.
 */

public class HomeApplication extends MultiDexApplication {

    private static HomeApplication instance;


    public static HomeApplication getInstance(){
        synchronized (HomeApplication.class){
            if (instance == null){
                instance = new HomeApplication();
            }
        }
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //方法突破65535
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化异常处理handler
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        //初始化机器人api
        Robot.initialize(this);
        //初始化多媒体服务
        MediaPlayManager.getInstance().init(getApplicationContext());
        RobotManager.initService();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
