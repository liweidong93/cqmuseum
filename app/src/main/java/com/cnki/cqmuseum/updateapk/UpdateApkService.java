package com.cnki.cqmuseum.updateapk;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.cnki.cqmuseum.bean.BaseEvenBusBean;
import com.cnki.cqmuseum.bean.UpdateApkBean;
import com.cnki.cqmuseum.constant.EvenBusConstant;
import com.cnki.cqmuseum.server.FileObserver;
import com.cnki.cqmuseum.updateapk.api.UpdateApkApi;
import com.cnki.cqmuseum.utils.LogUtils;
import com.cnki.cqmuseum.utils.Md5Utils;
import com.cnki.cqmuseum.utils.PackageUtils;
import com.cnki.cqmuseum.utils.SdCardUtils;
import com.cnki.cqmuseum.utils.TextStyleUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * 更新apk service
 */
public class UpdateApkService extends Service {
    public UpdateApkService() {
    }

    private Handler mHandler = new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        //首先检查是否有新版本
        UpdateApkApi.getInstance().getApkLasterInfo().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<UpdateApkBean>() {
                    @Override
                    public void onNext(UpdateApkBean updateApkBean) {
                        if (updateApkBean != null){
                            if (updateApkBean.code == 200){
                                LogUtils.e("最新版本获取成功");
                                if (updateApkBean.result != null){
                                    //首先判断当前版本跟最新版本信息对比
                                    int apkVersionCode = PackageUtils.getApkVersionCode(UpdateApkService.this);
                                    LogUtils.e("当前apk版本号为：" + apkVersionCode);
                                    if (!TextUtils.isEmpty(TextStyleUtils.isStrEmpty(updateApkBean.result.versionCode))){
                                        int versionCode = -1;
                                        try{
                                            versionCode = Integer.valueOf(updateApkBean.result.versionCode);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        if (versionCode != -1){
                                            if (apkVersionCode < versionCode){
                                                //拼接规则，拼接成apk的名字
                                                String apkName = TextStyleUtils.isStrEmpty(updateApkBean.result.projectName) + TextStyleUtils.isStrEmpty(updateApkBean.result.appId)
                                                        + TextStyleUtils.isStrEmpty(updateApkBean.result.versionName) + ".apk";
                                                //判断存不存在apk存储路径，如果不存则创建目录，存在的话则看看有没有此文件，如果有比对md5值
                                                SdCardUtils.createFile(ApkConstant.APK_STORAGEPATH);
                                                File apkFile = new File(ApkConstant.APK_STORAGEPATH + apkName);
                                                //判断apk是否存在
                                                if (apkFile.exists()){
                                                    //apk存在则判断md5值是否相同
                                                    String fileMD5 = Md5Utils.getFileMD5(apkFile);
                                                    if (fileMD5.equals(TextStyleUtils.isStrEmpty(updateApkBean.result.md5))){
                                                        //md5相同则开始安装apk
                                                        BaseEvenBusBean<UpdateApkBean> baseEvenBusBean = new BaseEvenBusBean<>(EvenBusConstant.EVENBUS_UPDATEAPK);
                                                        baseEvenBusBean.setObject(updateApkBean);
                                                        EventBus.getDefault().post(baseEvenBusBean);
                                                    }else{
                                                        //md5不同则删除掉文件，开始下载
                                                        LogUtils.e("apk存在但不完整，删除重下");
                                                        apkFile.delete();
                                                        downloadApk(updateApkBean, apkName);
                                                    }
                                                }else {
                                                    LogUtils.e("apk不存在开始下载");
                                                    //apk不存在则开始下载
                                                    downloadApk(updateApkBean, apkName);
                                                }
                                            }else{
                                                LogUtils.e("已经是最新版本，无需更新，版本号为：" + versionCode);
                                            }
                                        }else{
                                            LogUtils.e("获取最新版本信息异常，版本号转换int异常");
                                        }
                                    }else{
                                        LogUtils.e("获取最新版本信息异常，版本号为空");
                                    }
                                }
                            }else{
                                LogUtils.e("没有找到最新版本信息");
                            }
                        }else{
                            LogUtils.e("获取的apk版本实体为空");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        LogUtils.e("获取更新apk信息失败");
                        //获取失败后10s后再重启服务，因为机器人生命周期问题
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onStartCommand(intent,flags,startId);
                            }
                        },10000);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 下载apk
     * @param updateApkBean
     */
    public void downloadApk(final UpdateApkBean updateApkBean, final String apkName){
        if (!TextUtils.isEmpty(updateApkBean.result.linkUrl)){
            UpdateApkApi.getInstance().downloadApk(ApkConstant.UPDATEAPK_URL + updateApkBean.result.linkUrl)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribeWith(new FileObserver(ApkConstant.APK_STORAGEPATH + apkName) {
                        @Override
                        public void onSuccess(File file) {
                            BaseEvenBusBean<UpdateApkBean> baseEvenBusBean = new BaseEvenBusBean<>(EvenBusConstant.EVENBUS_UPDATEAPK);
                            baseEvenBusBean.setObject(updateApkBean);
                            EventBus.getDefault().post(baseEvenBusBean);
                        }

                        @Override
                        public void onErrorMsg(String msg) {
                            LogUtils.e("下载apk失败");
                            //下载失败后重新下载
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    downloadApk(updateApkBean,apkName);
                                }
                            },10000);
                        }
                    });
        }else{
            LogUtils.e("更新apk下载链接为空");
        }
    }

}
