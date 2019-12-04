package com.cnki.cqmuseum.server.api;


import com.cnki.cqmuseum.bean.BaseCnki;
import com.cnki.cqmuseum.constant.UrlConstant;
import com.cnki.cqmuseum.server.RetrofitServiceManager;
import com.cnki.cqmuseum.server.service.CnkiService;

import io.reactivex.Observable;

/**
 * 知网api
 * Created by admin on 2019/6/3.
 */

public class CnkiApi {

    private static CnkiApi instance;
    private static CnkiService cnkiJsonService;

    private CnkiApi(){

    }

    public static CnkiApi getInstance(){
        if (instance == null){
            synchronized (CnkiApi.class){
                instance = new CnkiApi();
                cnkiJsonService = RetrofitServiceManager.createJsonService(UrlConstant.URL_CNKI, CnkiService.class);
            }
        }
        return instance;
    }

    /**
     * 获取知网答案
     * @param question
     * @return
     */
    public Observable<BaseCnki> getCnkiAnswer(String question){
        return cnkiJsonService.getAnswer(question);
    }

}
