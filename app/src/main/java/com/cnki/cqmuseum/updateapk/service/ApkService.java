package com.cnki.cqmuseum.updateapk.service;


import com.cnki.cqmuseum.bean.UpdateApkBean;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 更新apk接口提供类
 * Created by admin on 2019/7/15.
 */

public interface ApkService {

    /**
     * 获取apk最新版本信息
     * @param params
     * @return
     */
    @POST("qa/version/getLatest")
    Observable<UpdateApkBean> getApkLasterInfo(@Body RequestBody params);

    /**
     * 下载apk
     * @param url
     * @return
     */
    @Streaming
    @GET
    Observable<ResponseBody> downloadApk(@Url String url);
}
