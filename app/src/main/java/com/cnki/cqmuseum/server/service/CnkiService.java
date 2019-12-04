package com.cnki.cqmuseum.server.service;


import com.cnki.cqmuseum.bean.BaseCnki;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 知网接口service
 * Created by admin on 2019/6/3.
 */
public interface CnkiService {

    //获取问题答案
    @GET("GetAnswer?appid=lib_neu&aid=ee831eebc9f01c3f18d6c2198ff879b2&type=mobile")
    Observable<BaseCnki> getAnswer(@Query("q") String question);

}
