package com.cnki.cqmuseum.server;

import com.cnki.cqmuseum.bean.AnswerBean;
import com.cnki.cqmuseum.bean.BaseCnki;
import com.cnki.cqmuseum.utils.LogUtils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.net.ssl.SSLHandshakeException;

import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;

/**
 * 请求接口回调
 * Created by liweidong on 2019/3/24.
 */

public abstract class BaseCnkiObserver extends DisposableObserver {



    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     */
    @Override
    public void onError(Throwable e) {
        String msg = "";//错误信息
        try {
            if (e instanceof SocketTimeoutException || e instanceof ConnectException) {//请求超时,网络连接超时
                msg = "网络异常，请检查网络！";
            } else if (e instanceof SSLHandshakeException) {//安全证书异常
                msg = "安全证书异常！";
            } else if (e instanceof HttpException) {//请求的地址不存在
                int code = ((HttpException) e).code();
                if (code == 404) {
                    msg = "404，请求的地址不存在！";
                } else if (code == 500){
                    msg = "服务器内部错误！";
                }else {
                    msg = code + "，请求异常！";
                }
            }else if (e instanceof UnknownHostException) {//域名解析失败
                msg = "域名解析错误！";
            } else {
                msg = "未知异常！";
                if (e != null){
                    e.printStackTrace();
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            msg = "解析异常！";
        } finally {
            LogUtils.e("http请求 onError:" + msg);
            onComplete();
        }
    }

    @Override
    public void onNext(Object o) {
        try {
//            Gson gson = new GsonBuilder()
//                    .setLenient()// json宽松
//                    .enableComplexMapKeySerialization()//支持Map的key为复杂对象的形式
////                    .serializeNulls() //智能null
//                    .create();
            BaseCnki baseCnki = (BaseCnki) o;
            if (baseCnki.result && baseCnki.MetaList != null && baseCnki.MetaList.size() != 0){
                onSuccess(baseCnki.MetaList);
            }else{
                onFailed();
            }
        } catch (Exception e) {
            onError(new Exception("服务器内部错误"));
            e.printStackTrace();
        }
    }


    /**
     * 成功回调
     * @param answerBeans
     */
    public abstract void  onSuccess(ArrayList<AnswerBean> answerBeans);

    /**
     * 成功回调
     */
    public void  onFailed(){

    };

}
