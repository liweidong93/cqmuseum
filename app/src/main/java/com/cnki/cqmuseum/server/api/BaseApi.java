package com.cnki.cqmuseum.server.api;

import com.cnki.cqmuseum.server.interceptor.MyLoggingInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求基类
 * kfgj gson  uaa 表单
 * Created by liweidong on 2019/3/22.
 */

public class BaseApi {

    //连接超时时间
    private static final long CONNECT_TIMEOUT = 3000;
    private static Retrofit mJsonRetrofit;//json格式的retrofit
    private static Retrofit mFormRetrofit;//表单格式的retrofit


    /**
     * 获取json格式数据的retrofit对象
     * @return
     */
    public static Retrofit getJsonRetrofit(String url){
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)//添加连接超时时间
                .retryOnConnectionFailure(true)
                .readTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(new MyLoggingInterceptor())//添加日志拦截器
                /*.addNetworkInterceptor(new Interceptor() {//添加桥接拦截器
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
//                                .addHeader(HttpHeadConstant.HEADER_CONTENTTYPE, HttpHeadConstant.CONTENTTYPE_JSON)
                                .addHeader("Connection", "close")
                                .build();
                        return chain.proceed(request);
                    }
                })*/;
        OkHttpClient okHttpClient = builder.build();

        mJsonRetrofit = new Retrofit.Builder()
                .baseUrl(url)//设置baseurl
                .addConverterFactory(GsonConverterFactory.create())//添加gson转换器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//添加rxjava2适配器
                .client(okHttpClient)//绑定okhttpclient
                .build();

        return mJsonRetrofit;
    }

    /**
     * 获取json格式数据的retrofit对象
     * @return
     */
    public static Retrofit getFileRetrofit(String url){
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .readTimeout(30, TimeUnit.MINUTES)
                .writeTimeout(30,TimeUnit.MINUTES);
        OkHttpClient okHttpClient = builder.build();

        mJsonRetrofit = new Retrofit.Builder()
                .baseUrl(url)//设置baseurl
                .addConverterFactory(GsonConverterFactory.create())//添加gson转换器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//添加rxjava2适配器
                .client(okHttpClient)//绑定okhttpclient
                .build();

        return mJsonRetrofit;
    }

    /**
     * 获取表单格式retrofit对象
     * @return
     */
    public static Retrofit getFormRetrofit(String url){
        /*OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new MyLoggingInterceptor())//添加日志拦截器
                .connectTimeout(CONNECT_TIMEOUT,TimeUnit.MILLISECONDS)//设置连接时间
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        //设置表单格式请求头
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader(HttpHeadConstant.HEADER_CONTENTTYPE, HttpHeadConstant.CONTENTTYPE_FORM)
                                .build();
                        return chain.proceed(request);
                    }
                });
        OkHttpClient okHttpClient = builder.build();
        mFormRetrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();*/
        return mFormRetrofit;
    }



}
