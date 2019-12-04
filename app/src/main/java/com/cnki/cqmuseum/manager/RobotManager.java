package com.cnki.cqmuseum.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.cnki.cqmuseum.bean.AnswerBean;
import com.cnki.cqmuseum.bean.BaseEvenBusBean;
import com.cnki.cqmuseum.bean.RobotResultBean;
import com.cnki.cqmuseum.constant.ChatViewTypeConstant;
import com.cnki.cqmuseum.constant.EvenBusConstant;
import com.cnki.cqmuseum.constant.IntentActionConstant;
import com.cnki.cqmuseum.constant.RobotConstant;
import com.cnki.cqmuseum.constant.RobotKeyConstant;
import com.cnki.cqmuseum.interf.OnASRCallBack;
import com.cnki.cqmuseum.interf.OnRobotAnswerCallBack;
import com.cnki.cqmuseum.interf.OnSpeakCallBack;
import com.cnki.cqmuseum.ui.chat.ChatActivity;
import com.cnki.cqmuseum.ui.collection.CollectionActivity;
import com.cnki.cqmuseum.ui.guide.GuideActivity;
import com.cnki.cqmuseum.utils.LogUtils;
import com.cnki.cqmuseum.utils.TextStyleUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ubtechinc.cruzr.sdk.dance.DanceConnectionListener;
import com.ubtechinc.cruzr.sdk.dance.DanceControlApi;
import com.ubtechinc.cruzr.sdk.navigation.NavigationApi;
import com.ubtechinc.cruzr.sdk.ros.RosRobotApi;
import com.ubtechinc.cruzr.sdk.speech.ISpeechContext;
import com.ubtechinc.cruzr.sdk.speech.SpeechConstant;
import com.ubtechinc.cruzr.sdk.speech.SpeechRobotApi;
import com.ubtechinc.cruzr.serverlibutil.interfaces.InitListener;
import com.ubtechinc.cruzr.serverlibutil.interfaces.SpeechASRListener;
import com.ubtechinc.cruzr.serverlibutil.interfaces.SpeechTtsListener;
import com.ubtrobot.async.DoneCallback;
import com.ubtrobot.async.FailCallback;
import com.ubtrobot.speech.AbstractNLUParamGenerator;
import com.ubtrobot.speech.LegacyUnderstandResult;
import com.ubtrobot.speech.LegacyUnderstander;
import com.ubtrobot.speech.UnderstandConstant;
import com.ubtrobot.speech.UnderstandException;
import com.ubtrobot.speech.UnderstandOption;
import com.ubtrobot.speech.understand.CruzrLegacyUnderstanderFactory;
import com.ubtrobot.speech.understand.xml.MapManager;
import com.ubtrobot.speech.understand.xml.XmlHelper;

import org.greenrobot.eventbus.EventBus;

/**
 * 机器人api管理类
 * Created by admin on 2019/6/10.
 */

public class RobotManager {

    private static SpeechTtsListener speechTtsListener;
    private static LegacyUnderstander understander;
    private static UnderstandOption.Builder builder;
    private static Gson gson;
    public static boolean isListen = true;//机器人是否接受语音问题
    private static float mProgress;
    private static boolean isAbort = false;

    /**
     * 设置机器人
     */
    public static void setRobotSetting(final Context mContext) {
        speechTtsListener = new SpeechTtsListener();
        //进入app后再次重新设置，避免以前初始化过异常问题
        SpeechRobotApi.get().initializ(mContext, RobotConstant.APPID, new InitListener() {
            @Override
            public void onInit() {
                //初始化成功
                LogUtils.e("robot","语音初始化完成！");
                // 所有在线指令会分发给 app 9003 ,离线指令按照正常指令分发
                setOnlineSpeechMode();
                RobotManager.setEnableWake(3, true);
//                SpeechRobotApi.get().enableWakeup(SpeechConstant.WAKE_UP_TYPE_SPEECH, true);
                SpeechRobotApi.get().registerSpeech(new ISpeechContext() {
                    @Override
                    public void onStart() {
                        LogUtils.e("robot","registerSpeech  onStart");
                    }

                    @Override
                    public void onStop() {
                        LogUtils.e("robot","registerSpeech  onStop");
                    }

                    @Override
                    public void onResult(String s) {
                        LogUtils.e("robot","registerSpeech  onResult:" + s);
                        handleResult(mContext, s);
                    }

                    @Override
                    public void onPause() {
                        LogUtils.e("robot","registerSpeech  onPause");
                    }

                    @Override
                    public void onResume() {
                        LogUtils.e("robot","registerSpeech  onResume");
                    }
                });

                SpeechRobotApi.get().speechStartTTS("您好，知网小智正在为您服务", speechTtsListener);
            }
        });
    }

    /**
     * 是否包含小智名字
     * @param result
     * @return
     */
    private static boolean isXZH(String result){
        return result.contains("小智") || result.contains("小志") || result.contains("小子") || result.contains("乔治");
    }

    /**
     * 对返回结果进行处理
     * @param context
     * @param json
     */
    private static void handleResult(Context context,String json){
        if (!TextUtils.isEmpty(json)){
            RobotResultBean robotResultBean = gson.fromJson(json, RobotResultBean.class);
            if (robotResultBean != null && robotResultBean.onlu != null && !TextUtils.isEmpty(robotResultBean.onlu.request)){
                //获取到机器人结果
                String result = robotResultBean.onlu.request;
                //将机器人结果转为拼音
//                String pinResult = TextStyleUtils.toPinyin(result);
                //首先判断是否正在说话，如果是说话中则判断是否说的为停止说话
                if (isSpeaking()){
                    if ((isXZH(result) && result.contains("别说")) || (isXZH(result) && result.contains("闭嘴"))
                            || isXZH(result) && (result.contains("不要") || result.contains("停止")) && result.contains("说")){
                        //停止说话
                        stopSpeech();
                        RobotManager.speechVoice("好哒");
                    }
                    return;
                }
                //只有包含机器人名字才能进入固定的语音判断
                if (isXZH(result)){
                    //再见
                    if ((result.contains("再见") || result.contains("拜拜"))){
                        RobotActionUtils.playSettingAction(RobotKeyConstant.ROBOTKEY_BYE);
                        return;
                    }
                    //拥抱
                    if (result.contains("抱")){
                        RobotActionUtils.playSettingAction(RobotKeyConstant.ROBOTKEY_HUG);
                        return;
                    }
                    //握手
                    if (result.contains("握")){
                        RobotActionUtils.playSettingAction(RobotKeyConstant.ROBOTKEY_SHANKHAND);
                        return;
                    }
                    //跳舞
                    if (result.contains("跳") && result.contains("舞")){
                        RobotActionUtils.startDance(context);
                        return;
                    }
                    //停止跳舞
                    if (result.contains("跳") && (result.contains("别") || result.contains("停止") )){
                        RobotActionUtils.stopDance();
                        return;
                    }
                }
                String topActivity = ActivityViewManager.getInstance().getTopActivity(context);
                //导航去指定地点
                if ((result.startsWith("带我去") || result.startsWith("小智带我去"))){
                    //如果关闭语音，则不进行问答
                    if (isListen){
                        //导航去某地
                        RobotActionUtils.goPointNavi(context, result);
                    }
                    return;
                }
                //开始导览
                if (result.equals("开始导航") || ( (result.contains("游览") || result.contains("参观")) && result.contains("带"))){
                    if ("com.cnki.cqmuseum.ui.guide.GuideActivity".equals(topActivity)){
                        BaseEvenBusBean<String> naviEvenBus = new BaseEvenBusBean<>(EvenBusConstant.EVENBUS_STARTNAVI);
                        EventBus.getDefault().post(naviEvenBus);
                    }else{
                        //先跳转再开始导览
                        Intent intent = new Intent(context, GuideActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(IntentActionConstant.NAVI_GUIDE,"start");
                        context.startActivity(intent);
                    }
                    return;
                }
                //导览控制器
                if ("com.cnki.cqmuseum.ui.guide.GuideActivity".equals(topActivity)){
                    if (result.equals("停止导航")){
                        BaseEvenBusBean<String> naviEvenBus = new BaseEvenBusBean<>(EvenBusConstant.EVENBUS_STOPNAVI);
                        EventBus.getDefault().post(naviEvenBus);
                    }else if (result.equals("继续导航")){
                        BaseEvenBusBean<String> naviEvenBus = new BaseEvenBusBean<>(EvenBusConstant.EVENBUS_CONTINUENAVI);
                        EventBus.getDefault().post(naviEvenBus);
                    }else if (result.equals("暂停导航")){
                        BaseEvenBusBean<String> naviEvenBus = new BaseEvenBusBean<>(EvenBusConstant.EVENBUS_PAUSENAVI);
                        EventBus.getDefault().post(naviEvenBus);
                    }else if (result.equals("需要")){
                        BaseEvenBusBean<String> naviEvenBus = new BaseEvenBusBean<>(EvenBusConstant.EVENBUS_GOHOME);
                        EventBus.getDefault().post(naviEvenBus);
                    }
                    return;
                }
                //语音问答分发
                if ("com.cnki.cqmuseum.ui.chat.ChatActivity".equals(topActivity)) {
                    //如果关闭语音，则不进行问答
                    if (!isListen){
                        return;
                    }
                    //如果当前界面是聊天页，直接发送问题
                    BaseEvenBusBean<String> evenBusBean = new BaseEvenBusBean<>(EvenBusConstant.EVENBUS_ROBOTQUESTION);
                    evenBusBean.setObject(result);
                    EventBus.getDefault().post(evenBusBean);
                } else if ("com.cnki.cqmuseum.ui.home.HomeActivity".equals(topActivity)) {
                    //如果关闭语音，则不进行问答
                    if (!isListen){
                        return;
                    }
                    // 如果当前是首页，则跳转到 ChatActivity进行回答问题
                    Intent intent = new Intent();
                    intent.putExtra(IntentActionConstant.ACTION_QUESTION, result);
                    intent.setClass(context, ChatActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else if("com.cnki.cqmuseum.ui.collection.CollectionActivity".equals(topActivity) && CollectionActivity.isPressDown){
                    BaseEvenBusBean<String> evenBusBean = new BaseEvenBusBean<>(EvenBusConstant.EVENBUS_COLLECTIONTEXT);
                    evenBusBean.setObject(result);
                    EventBus.getDefault().post(evenBusBean);
                }
            }
        }
    }

    /**
     * 销毁掉语音服务
     */
    public static void destorySpeachService(){
        //退出app之前设置为正常模式
        SpeechRobotApi.get().speechPermissionDispatch(RobotConstant.APPID, SpeechConstant.SPEECH_DISPATCH_PERMISSION_NONE);
        SpeechRobotApi.get().destory();
    }

    /**
     * 设置机器人为正常的聊天模式
     * 全部指令由机器人接管
     */
    public static void setNormalSpeechMode(){
        SpeechRobotApi.get().speechPermissionDispatch(RobotConstant.APPID, SpeechConstant.SPEECH_DISPATCH_PERMISSION_NONE);
    }

    /**
     * 设置机器人为截获语音信息，程序进行处理
     * 部分指令由机器人接管
     */
    public static void setOnlineSpeechMode(){
        SpeechRobotApi.get().speechPermissionDispatch(RobotConstant.APPID, SpeechConstant.SPEECH_DISPATCH_PERMISSION_ALL);
    }

    /**
     * 初始化机器人
     */
    public static void initRobot(Context context) {
        //初始化机器人api
        RosRobotApi.get().initializ(context, null);
        //初始化导航api
        NavigationApi.get().initializ(context);
        // 机器人动作关节初始化
        RosRobotApi.get().initializ(context, new InitListener() {
            @Override
            public void onInit() {
                //初始化成功
                System.out.println("初始化成功...");
            }
        });
        // 创建 understand
        MapManager mapManager = XmlHelper.get().getIntentMapManager();
        builder = new UnderstandOption.Builder();
        String language = "zh-CN";
        builder.setLanguage(language);
        understander = (new CruzrLegacyUnderstanderFactory(context)).createUnderstander("zhujian", language, new AbstractNLUParamGenerator() {
            @Override
            public Bundle getBundle(String s) {
                Bundle bundle = new Bundle();
                switch (s) {
                    case UnderstandConstant.SOURCE_EMOTIBOT:
                        bundle.putString(UnderstandConstant.SOURCE_EMOTIBOT_APPID,
                                //填写正确的appid
                                RobotConstant.ROBOT_APPID);
                        bundle.putString(UnderstandConstant.SOURCE_EMOTIBOT_USERID,
                                //"序列号：Cruzr.01.1234567,则填写1234567"
                                RobotConstant.ROBOT_NUM);
                        bundle.putString(UnderstandConstant.SOURCE_EMOTIBOT_CITY,
                                //对应的城市
                                RobotConstant.ROBOT_CITY);
                }
                return bundle;
            }
        }, mapManager);
        // json宽松
        //支持Map的key为复杂对象的形式
        //智能null
        gson = new GsonBuilder()
                    .setLenient()// json宽松
                    .enableComplexMapKeySerialization()//支持Map的key为复杂对象的形式
                    .serializeNulls() //智能null
                    .create();
    }

    /**
     * 停止播报
     */
    public static void stopSpeech(){
        isAbort = true;
        SpeechRobotApi.get().speechStopTTS();
    }

    /**
     * 播报
     * @param speechStr
     */
    public static void speechVoice(String speechStr){
        isAbort = false;
        SpeechRobotApi.get().speechStartTTS(speechStr, speechTtsListener);
    }

    /**
     * 播报，带播报完通知回调
     * @param str
     * @param onSpeakCallBack
     */
    public static void speechVoice(final String str, final OnSpeakCallBack onSpeakCallBack){
        mProgress = 0;
        isAbort = false;
        SpeechRobotApi.get().speechStartTTS(str, new SpeechTtsListener(){
            @Override
            public void onEnd() {
                super.onEnd();
                onSpeakCallBack.onSpeakEnd();
            }

            @Override
            public void onSpeakProgress(int progress) {
                super.onSpeakProgress(progress);
                mProgress = progress;
            }

            @Override
            public void onAbort() {
                super.onAbort();
                if (isAbort){
                    return;
                }
                //被打断的情况处理
                mProgress = mProgress / 100;
                if (0 < mProgress && mProgress < 1){
                    speechVoice(str.substring((int) (str.length() * mProgress)), onSpeakCallBack);
                }else{
                    onSpeakCallBack.onSpeakEnd();
                }
            }
        });
    }

    /**
     * 是否正在说话
     * @return
     */
    public static boolean isSpeaking(){
        return SpeechRobotApi.get().isTtsSpeaking();
    }

    /**
     * 机器人api提问
     * @param context
     * @param question
     * @param callBack
     */
    public static void askQuestionToRobot(Context context, final String question, final OnRobotAnswerCallBack callBack){
        //判断问答服务是否初始化成功
        if (understander == null){
            MapManager mapManager = XmlHelper.get().getIntentMapManager();
            builder = new UnderstandOption.Builder();
            String language = "zh-CN";
            builder.setLanguage(language);
            understander = (new CruzrLegacyUnderstanderFactory(context)).createUnderstander("zhujian", language, new AbstractNLUParamGenerator() {
                @Override
                public Bundle getBundle(String s) {
                    Bundle bundle = new Bundle();
                    switch (s) {
                        case UnderstandConstant.SOURCE_EMOTIBOT:
                            bundle.putString(UnderstandConstant.SOURCE_EMOTIBOT_APPID,
                                    //填写正确的appid
                                    RobotConstant.ROBOT_APPID);
                            bundle.putString(UnderstandConstant.SOURCE_EMOTIBOT_USERID,
                                    //"序列号：Cruzr.01.1234567,则填写1234567"
                                    RobotConstant.ROBOT_NUM);
                            bundle.putString(UnderstandConstant.SOURCE_EMOTIBOT_CITY,
                                    //对应的城市
                                    RobotConstant.ROBOT_CITY);
                    }
                    return bundle;
                }
            }, mapManager);
        }
        understander.understand(question,builder.build())
                .done(new DoneCallback<LegacyUnderstandResult>() {
                    @Override
                    public void onDone(LegacyUnderstandResult legacyUnderstandResult) {
                        // 1.主线程回调
                        String result = legacyUnderstandResult.getFulfillment().getSpeech();
                        // 替换名称为“小智”
                        if (result.contains("小影")) {
                            result = result.replace("小影", "小智");
                        }
                        LogUtils.e("机器人", result);
                        //设置类型为闲聊通用
                        AnswerBean.AnswerItem answerItem = new AnswerBean.AnswerItem();
                        answerItem.viewType = ChatViewTypeConstant.VIEWTYPE_DEFAULT;
                        answerItem.Question = question;
                        answerItem.Answer = result;
                        //回调出去
                        callBack.onSuccess(answerItem);
                    }
                }).fail(new FailCallback<UnderstandException>() {
            @Override
            public void onFail(UnderstandException e) {
                //异常
                LogUtils.e("机器人", "获取答案失败:" + e.toString());
                AnswerBean.AnswerItem answerItem = new AnswerBean.AnswerItem();
                answerItem.viewType = ChatViewTypeConstant.VIEWTYPE_DEFAULT;
                answerItem.Question = question;
                answerItem.Answer = "您好，该问题已提交问题库，博物馆老师稍后会为您解答。";
                callBack.onFailed(answerItem);
            }
        });
    }

    /**
     * 开始语音听写
     * @param onASRCallBack
     */
    public static void startSpeechASR(final OnASRCallBack onASRCallBack){
        SpeechRobotApi.get().startSpeechASR(new SpeechASRListener() {
            @Override
            public void onBegin() {
                //语音听写开始
                onASRCallBack.onStart();
            }

            @Override
            public void onEnd() {
                //语音听写结束
                onASRCallBack.onEnd();
            }

            @Override
            public void onVolumeChanged(int i) {
                //语音听写音量
                onASRCallBack.onVolumeChanged(i);
            }

            @Override
            public void onResult(String s, boolean b) {
                //语音听写返回结果
                onASRCallBack.onResult(s);
            }

            @Override
            public void onError(int i) {
                //错误回调
                onASRCallBack.onError();
            }

            @Override
            public void onIllegal() {
                //出现非法情况回调
                onASRCallBack.onError();
            }
        });
    }

    /**
     * 停止语音听写
     */
    public static void stopSpeechASR(){
        SpeechRobotApi.get().stopSpeechASR();
    }

    /**
     * 是否开启唤醒
     * @param type 语音:0 、虚拟按键:1、视觉唤醒:2 全部:3
     * @param isAllow
     */
    public static void setEnableWake(int type, boolean isAllow){
        SpeechRobotApi.get().enableWakeup(type, isAllow);
    }

}
