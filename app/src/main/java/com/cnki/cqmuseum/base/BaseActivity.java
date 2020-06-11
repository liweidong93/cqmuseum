package com.cnki.cqmuseum.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.cnki.cqmuseum.bean.BaseEvenBusBean;
import com.cnki.cqmuseum.constant.RobotConstant;
import com.cnki.cqmuseum.manager.ActivityViewManager;
import com.cnki.cqmuseum.utils.StatuBarUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * activity基类
 * Created by liweidong on 2019/3/22.
 */

public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements BaseView {
    protected T mPresenter;
    public Context mContext;
    public static boolean isRemoveAway = true;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        StatuBarUtils.setFitsSystemWindows(this, true);
        StatuBarUtils.setStatusBarFullTransparent(this);
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mContext = this;
        //将activity加入栈中
        ActivityViewManager.getInstance().addActivity(this);
        onEvenBusRegister();
        mPresenter = initPresenter();//初始化presenter
        mPresenter.setContext(this);//设置环境
        initView();
        paddingStatusBar();
        initData();
        mPresenter.start();//调用persenter的开始
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初始化presenter
     * @return
     */
    public abstract T initPresenter();

    /**
     * 设置布局id
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 初始化控件
     */
    public abstract void initView();

    /**
     * 初始化数据
     */
    public abstract void initData();


    /**
     * evenbus注册
     */
    public void onEvenBusRegister(){
        EventBus.getDefault().register(this);
    }

    /**
     * evenbus注销
     */
    public void onEvenBusUnRegister(){
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(BaseEvenBusBean baseEvenBusBean) {
        onEvenBusCallBack(baseEvenBusBean);
    }

    /**
     * evenbus回调
     * @param baseEvenBusBean
     */
    public void onEvenBusCallBack(BaseEvenBusBean baseEvenBusBean){

    }

    /**
     * 显示菜单按钮
     */
    public void showRobotBtn() {
        Intent intent = new Intent();
        intent.setAction(RobotConstant.ACTION_SHOWBAR);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendBroadcast(intent);
    }

    /**
     * 隐藏菜单按钮
     */
    public void hideRobotBtn() {
        Intent intent = new Intent();
        intent.setAction(RobotConstant.ACTION_HIDEBAR);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendBroadcast(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        finish();
    }

    @Override
    public void finish() {
        super.finish();
        if (isRemoveAway){
            //将activity移除栈中
            ActivityViewManager.getInstance().removeActivity(this);
        }
        onEvenBusUnRegister();
        if (mPresenter != null){//解除绑定
            mPresenter.detach();
            mPresenter = null;
        }
    }

    public void paddingStatusBar(){
        View rootView = ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
        rootView.setPadding(0, StatuBarUtils.getStatusBarHeight(this), 0, 0);
    }
}
