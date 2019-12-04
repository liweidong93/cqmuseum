package com.cnki.cqmuseum.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 包工具类
 * Created by liweidong on 2019/7/31.
 */

public class PackageUtils {

    /**
     * 检查某个app是否安装
     * @param context
     * @param pkgName
     * @return
     */
    public static boolean checkAppInstalled( Context context, String pkgName) {
        if (pkgName== null || pkgName.isEmpty()) {
            return false;
        }
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> info = packageManager.getInstalledPackages(0);
        if(info == null || info.isEmpty())
            return false;
        for ( int i = 0; i < info.size(); i++ ) {
            if(pkgName.equals(info.get(i).packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 打开新的app
     * @param context
     * @param packageName
     * @param activityName
     */
    public static  void  startNewApp(Context context, String packageName, String activityName){
        if(isAvilible(context,packageName)){
            ComponentName componentName = new ComponentName(packageName,activityName );
            Intent intent1 = new Intent();
            Bundle bundle = new Bundle();
            /*bundle.putString("user_question", "我要查书");
            intent1.putExtras(bundle);*/
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.setComponent(componentName);
            context.startActivity(intent1);
        }else{
            Toast.makeText(context, "未安装该应用", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 判断应用是否存在
     * @param context
     * @param packageName
     * @return
     */
    private static boolean isAvilible(Context context, String packageName){
        final PackageManager packageManager = context.getPackageManager();//获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);//获取所有已安装程序的包信息
        List<String> pName = new ArrayList<String>();//用于存储所有已安装程序的包名
        //从pinfo中将包名字逐一取出，压入pName list中
        if(pinfo != null){
            for(int i = 0; i < pinfo.size(); i++){
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);//判断pName中是否有目标程序的包名，有TRUE，没有FALSE
    }

    /**
     * 获取当前apk版本号
     * @param context
     * @return
     */
    public static int getApkVersionCode(Context context){
        PackageManager packageManager = context.getPackageManager();
        int version = 0;
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }


    /**
     * 安装apk
     * @param context
     * @param file
     */
    public static void installApk(final Context context, final File file){
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
//            RxPermissionUtils.checkPermissions((Activity) context, new RxPermissionUtils.OnPermissionCallback() {
//                @Override
//                public void onSuccess() {
//
//                }
//            }, Manifest.permission.REQUEST_INSTALL_PACKAGES);
            Uri apkUri = FileProvider.getUriForFile(context, "com.cnki.cqmuseum.Fileprovider", file);
            intent.setDataAndType(apkUri,"application/vnd.android.package-archive");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        try{
            context.startActivity(intent);
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }
}
