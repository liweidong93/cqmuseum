package com.cnki.cqmuseum.ui.speechserver;

import android.os.Handler;

import com.cnki.cqmuseum.base.BasePresenterImpl;
import com.cnki.cqmuseum.bean.SpeechServer;
import com.cnki.cqmuseum.server.BaseResultObserver;
import com.cnki.cqmuseum.server.api.MuseumApi;
import com.cnki.cqmuseum.utils.ToastUtils;

import java.util.ArrayList;

/**
 * Created by liweidong on 2019/11/7.
 */

public class SpeechServerPresenter extends BasePresenterImpl<ISpeechServerView> {

    private Handler mHandler = new Handler();

    public SpeechServerPresenter(ISpeechServerView view) {
        super(view);
        getSpeechServer();
    }

    /**
     * 获取讲解服务数据
     */
    public void getSpeechServer(){
        addDisposable(MuseumApi.getInstance().getSpeechServer(), new BaseResultObserver<ArrayList<SpeechServer>>(){
            @Override
            public void onSuccess(ArrayList<SpeechServer> speechServers) {
                super.onSuccess(speechServers);
                if (speechServers != null && speechServers.size() != 0){
                    mView.onSuccess(speechServers);
                }else{
                    ToastUtils.toast(mContext,"暂无数据");
                }
            }

            @Override
            public void onFailed() {
                super.onFailed();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getSpeechServer();
                    }
                },1000);
            }
        });
    }
}
