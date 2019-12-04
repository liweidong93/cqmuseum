package com.cnki.cqmuseum.updateapk;

import android.os.Environment;

/**
 * 关于更细apk的常量类
 * Created by admin on 2019/7/29.
 */

public class ApkConstant {

    //下载apk路径,内网地址
//    public static final String UPDATEAPK_URL = "http://192.168.103.24:8080/robot-general/";

    //下载apk路径,公网地址
    public static final String UPDATEAPK_URL = "http://qa2.cnki.net/robot-general/";

    //项目名称
    public static final String PROJECT_NAME = "重庆自然博物馆";

    //产品类型id
    public static final String APP_ID = "9005";

    //更新apk存储路径
    public static final String APK_STORAGEPATH = Environment.getExternalStorageDirectory() + "/robot/apk/";
}
