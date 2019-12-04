package com.cnki.cqmuseum.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 机器人指令
 * Created by admin on 2019/6/10.
 */

public class RobotKeyConstant {
    // 停止播报的指令
    public static List<String> stopSpeakList = new ArrayList<>();
    // 做动作的指令
    public static Map<String, String> actionMap = new HashMap<>();
    public static List<String> actionList = new ArrayList<>();
    // 跳舞的指令
    public static List<String> danceList = new ArrayList<>();

    private static String[] orderName = {"xiaozhi", "xiaozi"};

    //返回上一页
    public static ArrayList<String> goLastList = new ArrayList<>();
    //返回首页
    public static ArrayList<String> goHomeList = new ArrayList<>();

    //聊天语料
    public static String [] chatLists = new String[]{"你好，欢迎你哦","hi,这位贵宾，我是知网小东，您可以直接问我问题哦","hi！你可以找我帮忙哦","hi！需要帮忙吗",
            "你好，很高兴认识你，可以来找我聊天哦", "哈喽，你好呀！", "您好，我是知网小东，有什么可以帮您的", "你好呀，有什么我可以帮忙的？"};

    static {
        //设置停止说话命令
        for (String name : orderName) {
            stopSpeakList.add(name + "tingzhi");
            stopSpeakList.add(name + "tingzi");
            stopSpeakList.add(name + "bieshuohua");
            stopSpeakList.add(name + "biesuohua");
            stopSpeakList.add(name + "bizui");
        }

        // 设置动作指令
        for (String name : orderName) {
            actionMap.put(name + "zaijian", "goodbye");
            actionMap.put(name + "baibai", "goodbye");
            actionMap.put(name + "huihuishou", "goodbye");
            actionMap.put(name + "huishou", "goodbye");
            actionMap.put(name + "zhaozhaoshou", "goodbye");
            actionMap.put(name + "zhaoshou", "goodbye");

            actionList.add(name + "zaijian");
            actionList.add(name + "baibai");
            actionList.add(name + "huihuishou");
            actionList.add(name + "huishou");
            actionList.add(name + "zhaozhaoshou");
            actionList.add(name + "zhaoshou");
        }

        for (String name : orderName){
            // 设置跳舞指令
            danceList.add(name + "tiaogewu");
            danceList.add(name + "tiaowu");
            danceList.add(name + "tiaozhiwu");
        }

        for (String name : orderName){
            //返回上一页
            goLastList.add(name + "fanhui");
            goLastList.add(name + "fanfei");
        }

        for (String name : orderName){
            //返回首页
            goHomeList.add(name + "shouye");
            goHomeList.add(name + "souye");
        }

    }

    //机器人动作指令
    public static final String ROBOTKEY_BYE = "goodbye";//再见
    public static final String ROBOTKEY_SALUTE = "pose1";//敬礼
    public static final String ROBOTKEY_PLAN = "pose2";//比划
    public static final String ROBOTKEY_NOMATTER = "pose3";//无所谓
    public static final String ROBOTKEY_HUG = "hug";//拥抱
    public static final String ROBOTKEY_SHANKHAND = "shankhand";//握手
    public static final String ROBOTKEY_APPLAUSE = "applause";//鼓掌
    public static final String ROBOTKEY_ZHUATOU = "zhuatou";//抓头
    public static final String ROBOTKEY_RESET = "reset";//复位
    //机器人表情指令
    public static final String ROBOTKEY_WRONGED = "techface_wronged";//委屈
    public static final String ROBOTKEY_LOVE = "techface_love";//爱心
    public static final String ROBOTKEY_HAPPY = "techface_happy";//开心


    //舞蹈种类
    public static String[] dances = { "TocaToca", "Crayon", "Seaweed", "Curry", "IevanPolkka", "Panama", "Faded", "Dura","Modern Dance", "Flamenco", "Arabian","Play Violin" };

}
