package com.cnki.cqmuseum.base;

import android.content.Context;

import com.cnki.cqmuseum.utils.LogUtils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liweidong on 2019/3/22.
 */

public abstract class BasePresenterImpl<V extends BaseView> implements BasePresenter {

    public V mView;
    private CompositeDisposable mCompositeDiapoable;
    public Context mContext;

    public BasePresenterImpl(V view){
        this.mView = view;
    }

    @Override
    public void setContext(Context context) {
        this.mContext = context;
    }

    @Override
    public void start() {

    }

    @Override
    public void detach() {
        this.mView = null;
        unDisposable();
    }

    /**
     * 添加请求
     * @param observable
     * @param disposableObserver
     * @param <T>
     */
    @Override
    public <T> void addDisposable(Observable<T> observable, DisposableObserver<T> disposableObserver) {
        //如果解绑的话，需要重新实例，否则无效
        if (mCompositeDiapoable == null || mCompositeDiapoable.isDisposed()){
            mCompositeDiapoable = new CompositeDisposable();
        }
        if (observable != null){
            mCompositeDiapoable.add(observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(disposableObserver));
        }else{
            LogUtils.e("提交实体为空！");
        }
    }

    /**
     * 在界面退出时需要解绑观察者统一进行解绑，防止Rx造成内存泄漏
     */
    @Override
    public void unDisposable() {
        if (mCompositeDiapoable != null){
            mCompositeDiapoable.dispose();
        }
    }
}
