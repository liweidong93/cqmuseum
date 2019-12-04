package com.cnki.cqmuseum.updateapk.api;

import com.cnki.cqmuseum.bean.UpdateApkBean;
import com.cnki.cqmuseum.server.RetrofitServiceManager;
import com.cnki.cqmuseum.updateapk.ApkConstant;
import com.cnki.cqmuseum.updateapk.service.ApkService;

import java.util.HashMap;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * 更新apk接口类
 * Created by admin on 2019/7/29.
 */

public class UpdateApkApi {

    private static UpdateApkApi instance;
    private static ApkService apkService;
    private static ApkService updateJsonService;

    private UpdateApkApi(){

    }

    public static UpdateApkApi getInstance(){
        if (instance == null){
            synchronized (UpdateApkApi.class){
                instance = new UpdateApkApi();
                apkService = RetrofitServiceManager.createFileService(ApkConstant.UPDATEAPK_URL, ApkService.class);
                updateJsonService = RetrofitServiceManager.createJsonService(ApkConstant.UPDATEAPK_URL, ApkService.class);
            }
        }
        return instance;
    }

    /**
     * 获取apk最新版本信息
     * @return
     */
    public Observable<UpdateApkBean> getApkLasterInfo(){
        HashMap<String, String> params = new HashMap<>();
        params.put("projectName", ApkConstant.PROJECT_NAME);
        params.put("appId", String.valueOf(ApkConstant.APP_ID));
        return updateJsonService.getApkLasterInfo(RetrofitServiceManager.makeRequstBody(params));
    }

    /**
     * 下载apk
     * @param url
     * @return
     */
    public Observable<ResponseBody> downloadApk(String url){
        return apkService.downloadApk(url);
    }
}
