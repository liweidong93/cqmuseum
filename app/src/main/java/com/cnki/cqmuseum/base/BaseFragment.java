package com.cnki.cqmuseum.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * fragment基类
 * Created by liweidong on 2019/3/26.
 */

public abstract class BaseFragment<T extends BasePresenterImpl> extends Fragment implements BaseView{

    private T mPresenter;
    protected boolean isInit = false;
    protected boolean isLoad = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        mPresenter = initPresenter();
        mPresenter.setContext(this.getActivity());
        initView(view);
        mPresenter.start();
        return view;
    }

    protected abstract int getLayoutId();

    protected abstract T initPresenter();

    protected abstract void initView(View view);


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isInit = true;
        isCanLoadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInit = false;
        isLoad = false;
    }

    /**
     * 是否可以加载数据
     * 可以加载数据的条件：
     * 1.视图已经初始化
     * 2.视图对用户可见
     */
    private void isCanLoadData() {
        if (!isInit) {
            return;
        }

        if (getUserVisibleHint()) {
            lazyLoad();
            isLoad = true;
        } else {
            if (isLoad) {
                stopLoad();
            }
        }
    }

    /**
     * 懒加载
     */
    public void lazyLoad(){

    }

    /**
     * 停止加载
     */
    public void stopLoad(){

    }
}
