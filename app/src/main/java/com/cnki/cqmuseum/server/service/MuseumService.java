package com.cnki.cqmuseum.server.service;

import com.cnki.cqmuseum.bean.BaseResult;
import com.cnki.cqmuseum.bean.CollectionList;
import com.cnki.cqmuseum.bean.SpeechServer;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 重庆自然博物馆api
 * Created by liweidong on 2019/11/7.
 */

public interface MuseumService {

    /**
     * 根据类别获取文物列表,分页接口
     * @param type
     * @param pageSize
     * @param pageCount
     * @return
     */
    @GET("getCollectionListByType")
    Observable<BaseResult<CollectionList>> getCollectionListByType(@Query("type")String type, @Query("pageSize")int pageSize, @Query("pageCount") int pageCount);

    /**
     *  根据名称模糊查询文物
     * @param name
     * @return
     */
    @GET("getCollectionListLikeName")
    Observable<BaseResult<ArrayList<CollectionList.CollectionItem>>> getCollectionListLikeName(@Query("name")String name);

    /**
     * 获取讲解服务数据
     * @return
     */
    @GET("getSpeechServer")
    Observable<BaseResult<ArrayList<SpeechServer>>> getSpeechServer();

    /**
     * 根据id获取文物列表
     * @return
     */
    @GET("getCollectionListByIds")
    Observable<BaseResult<ArrayList<CollectionList.CollectionItem>>> getCollectionListByIds(@Query("ids") String ids);
}
