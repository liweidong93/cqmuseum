package com.cnki.cqmuseum.server.api;

import com.cnki.cqmuseum.bean.BaseResult;
import com.cnki.cqmuseum.bean.CollectionList;
import com.cnki.cqmuseum.bean.SpeechServer;
import com.cnki.cqmuseum.constant.UrlConstant;
import com.cnki.cqmuseum.server.RetrofitServiceManager;
import com.cnki.cqmuseum.server.service.MuseumService;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * 重庆自然博物馆接口api
 * Created by liweidong on 2019/11/7.
 */

public class MuseumApi {

    private static MuseumApi instance;
    private static MuseumService museumService;

    private MuseumApi(){

    }

    public static MuseumApi getInstance(){
        if (instance == null){
            synchronized (MuseumApi.class){
                instance = new MuseumApi();
                museumService = RetrofitServiceManager.createJsonService(UrlConstant.URL_MUSEUMAPI, MuseumService.class);
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
}
