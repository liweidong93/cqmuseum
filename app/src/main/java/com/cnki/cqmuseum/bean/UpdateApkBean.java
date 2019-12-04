package com.cnki.cqmuseum.bean;

import java.io.Serializable;

/**
 * 更新apk实体{"msg":"未找到版本信息","result":false,"time":"2019-07-29 13:47:54.943","code":500}
 * {
 "msg": "操作成功",
 "result": {
 "id": 3,
 "projectName": "library",
 "appId": "seu",
 "appName": "东南大学图书馆",
 "versionCode": 12,
 "versionName": "1.2",
 "stamp": "1564217075730",
 "dateTime": "2019-07-27 16:44:35 730",
 "linkUrl": "/assets/library/seu/东南大学图书馆V2.0.apk",
 "md5": "98a257f02ed6373485431b9f03e63d63"
 },
 "time": "2019-07-29 14:35:34.859",
 "code": 200
 }
 * Created by admin on 2019/7/29.
 */

public class UpdateApkBean implements Serializable{
    public String msg;
    public String time;
    public int code;//500 为找到版本信息 200 成功
    public Result result;

    public class Result{
        public int id;
        public String projectName;
        public String appId;
        public String versionCode;
        public String versionName;
        public String stamp;
        public String dateTime;
        public String linkUrl;
        public String md5;
    }

}
