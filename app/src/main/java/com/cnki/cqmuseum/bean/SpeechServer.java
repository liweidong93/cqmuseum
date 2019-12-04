package com.cnki.cqmuseum.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liweidong on 2019/11/7.
 */

public class SpeechServer {
    @SerializedName(value = "编号")
    public String id;
    @SerializedName(value = "图片")
    public String pic;
    @SerializedName(value = "简介")
    public String text;
    @SerializedName(value = "时间")
    public String time;
}
