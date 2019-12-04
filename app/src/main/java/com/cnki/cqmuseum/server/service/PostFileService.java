package com.cnki.cqmuseum.server.service;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * 上传文件接口
 */
public interface PostFileService {

    @Multipart
    @POST("qa/logger/uploadRECFile")
    Call<ResponseBody> postFile(@Part MultipartBody.Part file);

}
