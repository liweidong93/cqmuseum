package com.cnki.cqmuseum.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * 问题回答实体
 * Created by admin on 2019/6/4.
 */

public class AnswerBean {

    /**
     * DataType为API返回的类型标识，优先级数字越小优先级越高
     * DataType为0，优先级最高，答案内容为纯文本展示。  domain 图书馆_业务数据:CNKI助手:闲聊_个性:闲聊_通用
     * DataType为1，优先级比FAQ低一级，答案内容为纯文本展示。此类问题，是管理员在后台回复的用户问题。（一般是FAQ中的业务问题没有覆盖到，需要管理员进行回复的问题。）
     * DataType为3，API返回的结果，需要不同应用端进行定制化显示。 domain 机器人:天气:工具书:图书馆_图书:文献:图书馆_动态
     */
    public int DataType;//数据类型 0：1:3  业务:用户问题：图书文献
    public String ID;
    public AnswerItem Data;


    public static class AnswerItem{
        public String Answer;//答案  (图书馆_业务数据)
        @SerializedName(value = "Domain", alternate = "domain")
        public String Domain;//答案分类
        public String Question;//问题
        public String orignalQuestion;//原问题
        @SerializedName(value =  "Extra")
        public Extra extra;
        public int viewType;//选用的布局类型
        public boolean isOpen;//textview是否张开
        public String Title;
        public String intent_domain;
        public String intent_focus;//机器人指令
        public String intent_id;
        @SerializedName(value = "KNode")
        public ArrayList<KNodeItem> kNodeItems;
        @SerializedName(value = "Page")
        public Page  page;

        public static class Page{
            public int PageCount;
            public int PageNum;
            public int Total;
        }

        public static class KNodeItem{
            @SerializedName(value = "DATA")
            public ArrayList<DataItem> dataItems;

            public static class DataItem{
                @SerializedName(value = "FieldValue")
                public FieldValue fieldValue;

                public static class FieldValue{
                    @SerializedName(value = "发掘地点")
                    public String location;
                    @SerializedName(value = "名称")
                    public String name;
                    @SerializedName(value = "图片")
                    public String pic;
                    @SerializedName(value = "简介")
                    public String introduce;
                    @SerializedName(value = "类别")
                    public String type;
                    @SerializedName(value = "体长")
                    public String bodylength;
                    @SerializedName(value = "地质年代")
                    public String time;
                    @SerializedName(value = "食性")
                    public String feed;
                    @SerializedName(value = "分布地区")
                    public String inarea;
                    @SerializedName(value = "编号")
                    public String num;

                    //熊猫知识库
                    @SerializedName(value = "伪拇指")
                    public String figures;
                    @SerializedName(value = "化石")
                    public String fossil;
                    @SerializedName(value = "咀嚼肌")
                    public String muscle;
                    @SerializedName(value = "地址")
                    public String address;
                    @SerializedName(value = "头骨")
                    public String skull;
//                    @SerializedName(value = "始发期")
//                    public String periodTime;
                    @SerializedName(value = "属性")
                    public String attributes;
                    @SerializedName("年代")
                    public String age;
//                    @SerializedName(value = "成长期")
//                    public String growTime;
//                    @SerializedName(value = "来源")
//                    public String fromBy;
                    @SerializedName(value = "消化系统")
                    public String digestSystem;
                    @SerializedName(value = "种类")
                    public String kind;
//                    @SerializedName(value = "衰败期")
//                    public String decline;
                    @SerializedName(value = "身体")
                    public String body;
//                    @SerializedName(value = "鼎盛期")
//                    public String heyday;
                }
            }
        }

        public static class Extra{
            @SerializedName(value =  "答案")
            public String extraAnswer;
            @SerializedName(value =  "问题")
            public String extraQuestion;
            @SerializedName(value = "来源")
            public String fromText;
        }

    }
}
