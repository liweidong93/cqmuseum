package com.cnki.cqmuseum.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 精品文物列表
 * Created by liweidong on 2019/11/7.
 */

public class CollectionList implements Serializable{
    public int totalCount;
    public ArrayList<CollectionItem> collections;

    public static class CollectionItem implements Serializable{
        @SerializedName(value = "编号")
        public String id;
        @SerializedName(value = "名称")
        public String name;
        @SerializedName(value = "类别")
        public String type;
        @SerializedName(value = "发掘地点")
        public String place;
        @SerializedName(value = "分布地区")
        public String area;
        @SerializedName(value = "体长")
        public String body;
        @SerializedName(value = "食性")
        public String feed;
        @SerializedName(value = "地质年代")
        public String time;
        @SerializedName(value = "简介")
        public String introduce;
        @SerializedName(value = "图片")
        public String image;
        @SerializedName(value = "门")
        public String men;
        @SerializedName(value = "纲")
        public String gang;
        @SerializedName(value = "目")
        public String mu;
        @SerializedName(value = "科")
        public String ke;
        @SerializedName(value = "属")
        public String shu;
    }
}
