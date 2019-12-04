package com.cnki.cqmuseum.utils;

import android.app.Activity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liweidong on 2019/8/31.
 */

public class RxPermissionUtils {

    /**
     * 检查权限
     * @param activity
     * @param callback
     * @param permissions
     */
    public static void checkPermissions(final Activity activity, final OnPermissionCallback callback, String... permissions){
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(permissions)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean){
                            //同意权限
                            callback.onSuccess();
                        }else{
                            ToastUtils.toast(activity, "请开启权限！");
                        }
                    }
                });

    }

    public interface OnPermissionCallback{
        void onSuccess();
    }

}
