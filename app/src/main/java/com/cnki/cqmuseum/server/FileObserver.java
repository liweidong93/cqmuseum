package com.cnki.cqmuseum.server;


import com.cnki.cqmuseum.utils.SdCardUtils;

import java.io.File;

import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;

/**
 * 文件下载接口回调
 * Created by admin on 2019/7/29.
 */

public abstract class FileObserver extends DisposableObserver<ResponseBody> {
    private String path;

    public FileObserver(String path) {
        this.path = path;
    }

    @Override
    protected void onStart() {
    }

    @Override
    public void onComplete() {
    }



    @Override
    public void onNext(final ResponseBody o) {
        File file = SdCardUtils.saveFile(path, o);
        if (file != null && file.exists()) {
            onSuccess(file);
        } else {
            onErrorMsg("file is null or file not exists");
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        onErrorMsg(e.toString());
    }


    public abstract void onSuccess(File file);

    public abstract void onErrorMsg(String msg);
}

