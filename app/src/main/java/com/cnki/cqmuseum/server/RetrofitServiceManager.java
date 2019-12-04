package com.cnki.cqmuseum.server;


import com.cnki.cqmuseum.server.api.BaseApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 获取接口Service管理类
 * Created by liweidong on 2019/3/24.
 */

public class RetrofitServiceManager {

    /**
     * 创建json格式的retrofit service
     * @param service
     * @param <T>
     * @return
     */
    public static <T> T createJsonService(String url, Class<T> service){
        T t = BaseApi.getJsonRetrofit(url).create(service);
        return t;
    }

    /**
     * 创建json格式的retrofit service
     * @param service
     * @param <T>
     * @return
     */
    public static <T> T createFileService(String url, Class<T> service){
        T t = BaseApi.getFileRetrofit(url).create(service);
        return t;
    }

    /**
     * 创建表单格式的retrofit service
     * @param service
     * @param <T>
     * @return
     */
    public static <T> T createFormService(String url, Class<T> service){
        T t = BaseApi.getFormRetrofit(url).create(service);
        return t;
    }

    /**
     * 生成请求体
     * @param map
     * @return
     */
    public static RequestBody makeRequstBody(Object map) {
        Gson gson = new GsonBuilder()
                .setLenient()// json宽松
                .enableComplexMapKeySerialization()//支持Map的key为复杂对象的形式
//                .serializeNulls() //智能null
                .create();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                gson.toJson(map).trim());

        return requestBody;
    }

    public static RequestBody parseRequestBody(File file) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), file);
    }

    public static String parseMapKey(String key, String fileName) {
        return key + "\"; filename=\"" + fileName;
    }

    /**
     * 多文件上传part
     * @param key
     * @param files
     * @return
     */
    public static List<MultipartBody.Part> filesToMultipartBodyParts(String key, List<File> files) {
        List<MultipartBody.Part> parts = new ArrayList<>(files.size());
        for (File file : files) {
            RequestBody requestBody = parseRequestBody(file);
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), requestBody);
            parts.add(part);
        }
        return parts;
    }

    /**
     * 创建多个文件请求体
     * @param files
     */
    public static Map<String, RequestBody> createFilesBodyList(List<File> files){
        Map<String, RequestBody> map = new HashMap<>();
        for (int i = 0; i < files.size(); i++) {
//            "images\"; filename=\"image1.png"
            RequestBody requestBody = parseRequestBody(files.get(i));
            map.put("images\"; filename=\"image" + String.valueOf(i) + ".png", requestBody);
        }
        return map;
    }

    public static RequestBody makeFormText(String map) {

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),map);

        return requestBody;
    }

}
