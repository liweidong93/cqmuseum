package com.cnki.cqmuseum.server.api;

import android.content.Context;
import android.text.TextUtils;

import com.cnki.cqmuseum.base.BasePresenterImpl;
import com.cnki.cqmuseum.bean.BaseResult;
import com.cnki.cqmuseum.bean.CollectionList;
import com.cnki.cqmuseum.bean.SpeechServer;
import com.cnki.cqmuseum.constant.UrlConstant;
import com.cnki.cqmuseum.server.RetrofitServiceManager;
import com.cnki.cqmuseum.server.service.MuseumService;
import com.cnki.cqmuseum.ui.home.HomeActivity;
import com.cnki.cqmuseum.utils.NetUtils;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

/**
 * 重庆自然博物馆接口api
 * Created by liweidong on 2019/11/7.
 */

public class MuseumApi {

    private static MuseumApi instance;
    private static MuseumService museumService;
    private static MuseumService submitService;

    private MuseumApi(){

    }

    public static MuseumApi getInstance(){
        if (instance == null){
            synchronized (MuseumApi.class){
                instance = new MuseumApi();
                museumService = RetrofitServiceManager.createJsonService(UrlConstant.URL_MUSEUMAPI, MuseumService.class);
                submitService = RetrofitServiceManager.createJsonService(UrlConstant.URL_SUBMIT, MuseumService.class);
            }
        }
        return instance;
    }

    /**
     * 根据类型获取文物列表，分页接口
     * @param type 传空返回所有数据
     * @param pageSize 从0开始计算
     * @return
     */
    public Observable<BaseResult<CollectionList>> getCollectionListByType(String type, int pageSize){
        return museumService.getCollectionListByType(type, pageSize, 20);
    }

    /**
     * 根据名称模糊查询文物
     * @param name
     * @return
     */
    public Observable<BaseResult<ArrayList<CollectionList.CollectionItem>>> getCollectionListLikeName(String name){
        return museumService.getCollectionListLikeName(name);
    }

    /**
     * 获取讲解服务数据
     * @return
     */
    public Observable<BaseResult<ArrayList<SpeechServer>>> getSpeechServer(){
        return museumService.getSpeechServer();
    }

    /**
     * 根据id获取文物列表
     * @param ids
     * @return
     */
    public Observable<BaseResult<ArrayList<CollectionList.CollectionItem>>> getCollectionListByIds(String ids){
        return museumService.getCollectionListByIds(ids);
    }

    /**
     * 提交有无问题接口
     * @param presenter
     * @param context
     * @param question
     * @param answerStataus
     */
    public void submitQa(BasePresenterImpl presenter,Context context, String question, String answerStataus){
        String ip = HomeActivity.localhostIp;
        if (TextUtils.isEmpty(ip)){
            HomeActivity.localhostIp = NetUtils.getIPAddress(context);
        }
        HashMap<String, String> params = new HashMap();
        params.put("question", question);
        params.put("ip", ip);
        params.put("clientType","mobile");
        params.put("answerStatus", answerStataus);
        params.put("user_id","0");
        presenter.addDisposable(submitService.submitQa(RetrofitServiceManager.makeRequstBody(params)), new DisposableObserver() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    /**
     * 收集问题接口
     * @param presenter
     * @param context
     * @param question
     */
    public void submit(BasePresenterImpl presenter, Context context,String question){
        String ip = HomeActivity.localhostIp;
        if (TextUtils.isEmpty(ip)){
            HomeActivity.localhostIp = NetUtils.getIPAddress(context);
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("question", question);
        params.put("type","search");
        params.put("ClientType", "robot");
        params.put("ip", HomeActivity.localhostIp);
        presenter.addDisposable(submitService.submit(RetrofitServiceManager.makeRequstBody(params)), new DisposableObserver() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
