package com.cnki.cqmuseum.base;

import android.content.Context;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

/**
 * P层初始化
 * Created by liweidong on 2019/3/22.
 */

public interface BasePresenter {

    /**
     * 初始化
     */
    void start();

    /**
     * Activity关闭把对象置为空
     */
    void detach();

    /**
     * 将网络请求的每一个disposable添加进入CompositeDisposable，在退出时一并注销
     * @param observable
     * @param disposableObserver
     * @param <T>
     */
    <T> void addDisposable(Observable<T> observable, DisposableObserver<T> disposableObserver);

    /**
     * 注销所有请求
     */
    void unDisposable();

    /**
     * 设置上下文环境
     * @param context
     */
    void setContext(Context context);

}
