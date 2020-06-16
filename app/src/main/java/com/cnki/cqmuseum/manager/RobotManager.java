package com.cnki.cqmuseum.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.cnki.cqmuseum.bean.BaseEvenBusBean;
import com.cnki.cqmuseum.constant.EvenBusConstant;
import com.cnki.cqmuseum.constant.IntentActionConstant;
import com.cnki.cqmuseum.constant.RobotConstant;
import com.cnki.cqmuseum.constant.RobotDomainConstant;
import com.cnki.cqmuseum.interf.OnLocationCallBack;
import com.cnki.cqmuseum.interf.OnMarkerCallBack;
import com.cnki.cqmuseum.interf.OnNaviCallBack;
import com.cnki.cqmuseum.ui.chat.ChatActivity;
import com.cnki.cqmuseum.ui.collection.CollectionActivity;
import com.cnki.cqmuseum.ui.guide.GuideActivity;
import com.cnki.cqmuseum.ui.navigation.NavigationActivity;
import com.cnki.cqmuseum.utils.LogUtils;
import com.cnki.cqmuseum.utils.TextStyleUtils;
import com.ubtrobot.Robot;
import com.ubtrobot.async.CancelledCallback;
import com.ubtrobot.async.DoneCallback;
import com.ubtrobot.async.FailCallback;
import com.ubtrobot.async.ProgressCallback;
import com.ubtrobot.async.ProgressivePromise;
import com.ubtrobot.async.Promise;
import com.ubtrobot.emotion.EmotionException;
import com.ubtrobot.emotion.EmotionManager;
import com.ubtrobot.emotion.EmotionUris;
import com.ubtrobot.emotion.ExpressingProgress;
import com.ubtrobot.motion.ActionUris;
import com.ubtrobot.motion.MotionManager;
import com.ubtrobot.motion.PerformingException;
import com.ubtrobot.motion.PerformingProgress;
import com.ubtrobot.navigation.Location;
import com.ubtrobot.navigation.Marker;
import com.ubtrobot.navigation.NavMap;
import com.ubtrobot.navigation.NavMapException;
import com.ubtrobot.navigation.NavigationException;
import com.ubtrobot.navigation.NavigationManager;
import com.ubtrobot.navigation.NavigationProgress;
import com.ubtrobot.orchestration.OrchestrationManager;
import com.ubtrobot.orchestration.OrchestrationUris;
import com.ubtrobot.orchestration.PlayException;
import com.ubtrobot.orchestration.PlayProgress;;
import com.ubtrobot.speech.RecognitionException;
import com.ubtrobot.speech.RecognitionOption;
import com.ubtrobot.speech.RecognitionProgress;
import com.ubtrobot.speech.RecognitionResult;
import com.ubtrobot.speech.SpeechManager;
import com.ubtrobot.speech.SynthesisException;
import com.ubtrobot.speech.SynthesisProgress;
import com.ubtrobot.speech.UnderstandingException;
import com.ubtrobot.speech.UnderstandingResult;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.io.File;
import java.util.List;
import java.util.Random;

/**
 * 机器人api管理类
 * Created by admin on 2019/6/10.
 */

public class RobotManager {

    private static SpeechManager speechManager;
    private static ProgressivePromise<RecognitionResult, RecognitionException, RecognitionProgress> recognize;
    private static OrchestrationManager danceManager;
    //舞蹈数组
    private static Uri[] danceUris = new Uri[]{
            OrchestrationUris.NAXI,OrchestrationUris.BBOOM_BBOOM,OrchestrationUris.FLAMENCO,OrchestrationUris.MODERN,OrchestrationUris.TOCA_TOCA,OrchestrationUris.CRAYON,
            OrchestrationUris.SEAWEED,OrchestrationUris.CURRY,OrchestrationUris.IEVAN_POLKKA,OrchestrationUris.PANAMA,OrchestrationUris.FADED,OrchestrationUris.DURA
    };
    private static MotionManager motionManager;
    private static ProgressivePromise<Void, PerformingException, PerformingProgress> actionPromise;
    private static EmotionManager emotionManager;
    private static String topActivity = "";
    public static boolean isListen = true;//机器人是否接受语音问题
    private static ProgressivePromise<Void, SynthesisException, SynthesisProgress> speakPromise;
    private static NavigationManagerCompat navigationManager;
    private static ProgressivePromise<Void, NavigationException, NavigationProgress> navigatePromise;

    /**
     * 初始化一些服务
     */
    public static void initService(){
        //语音服务
        speechManager = Robot.globalContext()
                .getSystemService(SpeechManager.SERVICE);
        //编排服务
        danceManager = Robot.globalContext()
                .getSystemService(OrchestrationManager.SERVICE);
        //运动服务
        motionManager = Robot.globalContext().
                getSystemService(MotionManager.SERVICE);
        //情绪服务
        emotionManager = Robot.globalContext()
                .getSystemService(EmotionManager.SERVICE);
        //导航服务
        navigationManager = new NavigationManagerCompat(Robot.globalContext());
    }

    /**
     * 语音播报
     * @param voice
     * @return
     */
    public static  ProgressivePromise<Void, SynthesisException, SynthesisProgress> speak(String voice){
        speakPromise = speechManager.synthesize(voice);
        return speakPromise;
    }

    /**
     * 停止说话
     */
    public static void stopSpeak(){
        if (speakPromise != null){
            speakPromise.cancel();
        }
    }

    /**
     * 是否正在播报
     * @return
     */
    public static boolean isSpeaking(){
        return speechManager.isSynthesizing();
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
     * 语音持续识别接口
     * @param context
     */
    public static void recognize(final Context context){
        if (recognize != null){
            recognize.cancel();
        }
        recognize = speechManager.recognize(new RecognitionOption.Builder(RecognitionOption.MODE_CONTINUOUS).build());
        recognize.fail(new FailCallback<RecognitionException>() {
            @Override
            public void onFail(RecognitionException e) {
                e.printStackTrace();
                recognize(context);
            }
        }).progress(new ProgressCallback<RecognitionProgress>() {
            @Override
            public void onProgress(RecognitionProgress recognitionProgress) {
                //将语音识别结果进行处理
                if (!TextUtils.isEmpty(recognitionProgress.getTextResult())){
                    LogUtils.e("robot","机器人识别结果:" + recognitionProgress.getTextResult());
                    topActivity = ActivityViewManager.getInstance().getTopActivity(context);
                    if (isSpeaking()){
                        if ((isXZH(recognitionProgress.getTextResult()) && recognitionProgress.getTextResult().contains("别说")) || (isXZH(recognitionProgress.getTextResult()) && recognitionProgress.getTextResult().contains("闭嘴"))
                                || isXZH(recognitionProgress.getTextResult()) && (recognitionProgress.getTextResult().contains("不要") || recognitionProgress.getTextResult().contains("停止")) && recognitionProgress.getTextResult().contains("说")){
                            //停止说话
                            stopSpeak();
                            speak("好的，我保持安静");
                        }
                        return;
                    }
                    //开始导览
                    if (recognitionProgress.getTextResult().equals("开始导航") || ( (recognitionProgress.getTextResult().contains("游览") || recognitionProgress.getTextResult().contains("参观")) && recognitionProgress.getTextResult().contains("带"))){
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
                        if (recognitionProgress.getTextResult().equals("停止导航")){
                            BaseEvenBusBean<String> naviEvenBus = new BaseEvenBusBean<>(EvenBusConstant.EVENBUS_STOPNAVI);
                            EventBus.getDefault().post(naviEvenBus);
                        }else if (recognitionProgress.getTextResult().equals("继续导航")){
                            BaseEvenBusBean<String> naviEvenBus = new BaseEvenBusBean<>(EvenBusConstant.EVENBUS_CONTINUENAVI);
                            EventBus.getDefault().post(naviEvenBus);
                        }else if (recognitionProgress.getTextResult().equals("暂停导航")){
                            BaseEvenBusBean<String> naviEvenBus = new BaseEvenBusBean<>(EvenBusConstant.EVENBUS_PAUSENAVI);
                            EventBus.getDefault().post(naviEvenBus);
                        }else if (recognitionProgress.getTextResult().equals("需要")){
                            BaseEvenBusBean<String> naviEvenBus = new BaseEvenBusBean<>(EvenBusConstant.EVENBUS_GOHOME);
                            EventBus.getDefault().post(naviEvenBus);
                        }
                        return;
                    }else if("com.cnki.cqmuseum.ui.collection.CollectionActivity".equals(topActivity) && CollectionActivity.isPressDown){
                        //精品文物长按搜索文物
                        BaseEvenBusBean<String> evenBusBean = new BaseEvenBusBean<>(EvenBusConstant.EVENBUS_COLLECTIONTEXT);
                        evenBusBean.setObject(recognitionProgress.getTextResult());
                        EventBus.getDefault().post(evenBusBean);
                        return;
                    }else if (recognitionProgress.getTextResult().startsWith("带我去") || recognitionProgress.getTextResult().startsWith("小智带我去")
                            || recognitionProgress.getTextResult().startsWith("小志带我去") || recognitionProgress.getTextResult().startsWith("乔治带我去")
                            || recognitionProgress.getTextResult().startsWith("小子带我去")){
                        if (isListen){
                            getMarkerName(recognitionProgress.getTextResult(), new OnLocationCallBack() {
                                @Override
                                public void onSuccess(String markerName, String id) {
                                    //跳转到导航页面
                                    Intent intent = new Intent(context, NavigationActivity.class);
                                    intent.putExtra(IntentActionConstant.NAVI_LOCATION, markerName);
                                    context.startActivity(intent);
                                }

                                @Override
                                public void onFailed() {

                                }
                            });
                        }
                        return;
                    }
                    understantSpeak(context, recognitionProgress.getTextResult());
                }
            }
        });
    }

    /**
     * 语义理解接口
     * @param context
     * @param inputText
     */
    private static void understantSpeak(final Context context, final String inputText){
        final Promise<UnderstandingResult, UnderstandingException> understand = speechManager.understand((inputText.startsWith("今天天气") || inputText.startsWith("明天天气"))? RobotConstant.ROBOT_CITY + inputText : inputText);
        understand.done(new DoneCallback<UnderstandingResult>() {
            @Override
            public void onDone(final UnderstandingResult understandingResult) {
                LogUtils.e("robot","机器人理解结果为：" + understandingResult.getSpeechFulfillment().getText());
                switch (understandingResult.getIntent().getAction()){
                    case RobotDomainConstant.SAY_HELLO://问好
                        //播报文字
                        speak(understandingResult.getSpeechFulfillment().getText()).done(new DoneCallback<Void>() {
                            @Override
                            public void onDone(Void aVoid) {
                                //说完话之后隐藏表情
                                dismissExpressEmotion();
                            }
                        });
                        //握手
                        performAction(ActionUris.HANDSHAKE);
                        //显示表情
                        expressEmotion(EmotionUris.HAPPY);
                        return;
                    case RobotDomainConstant.DANCE://跳舞
                        speak("我要开始跳舞啦，注意不要靠我太近").done(new DoneCallback<Void>() {
                            @Override
                            public void onDone(Void aVoid) {
                                //随机跳一个舞蹈
                                dance();
                            }
                        });
                        return;
                    case RobotDomainConstant.SING://唱歌
                        playMusic(context);
                        return;
                    case RobotDomainConstant.HUG://拥抱
                        //播报并拥抱
                        speak("来抱抱啦");
                        //拥抱
                        performAction(ActionUris.HUG).done(new DoneCallback<Void>() {
                            @Override
                            public void onDone(Void aVoid) {
                                //说完话之后隐藏表情
                                dismissExpressEmotion();
                            }
                        }).fail(new FailCallback<PerformingException>() {
                            @Override
                            public void onFail(PerformingException e) {
                                //说完话之后隐藏表情
                                dismissExpressEmotion();
                            }
                        });
                        //显示表情
                        expressEmotion(EmotionUris.SHY);
                        return;
                    case RobotDomainConstant.SHAKE_HANDS://握手
                        //播报并握手
                        speak("你好，很高兴认识你").done(new DoneCallback<Void>() {
                            @Override
                            public void onDone(Void aVoid) {
                                //说完话之后隐藏表情
                                dismissExpressEmotion();
                            }
                        });
                        //握手
                        performAction(ActionUris.HANDSHAKE);
                        //显示表情
                        expressEmotion(EmotionUris.LOVE);
                        return;
                }
                String robotMsg = "";
                //判断机器人答案是否为空
                if (TextUtils.isEmpty(understandingResult.getSpeechFulfillment().getText())){
                    try {
                        if (understandingResult.getFulfillmentList() != null && understandingResult.getFulfillmentList().getJsonArray() != null &&
                            understandingResult.getFulfillmentList().getJsonArray().getJSONObject(0) != null){
                            robotMsg = understandingResult.getFulfillmentList().getJsonArray().getJSONObject(0).getString("reply");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    //否则
                    robotMsg = understandingResult.getSpeechFulfillment().getText();
                }
                //如果没有匹配到以上意图，则进行分发
                if ("com.cnki.cqmuseum.ui.chat.ChatActivity".equals(topActivity)) {
                    //如果关闭语音，则不进行问答
                    if (!isListen){
                        return;
                    }
                    //如果当前界面是聊天页，直接发送问题
                    BaseEvenBusBean<String> evenBusBean = new BaseEvenBusBean<>(EvenBusConstant.EVENBUS_ROBOTQUESTION);
                    evenBusBean.setObject(inputText);
                    evenBusBean.setRobotMsg(robotMsg);
                    EventBus.getDefault().post(evenBusBean);
                } else if ("com.cnki.cqmuseum.ui.home.HomeActivity".equals(topActivity)) {
                    //如果关闭语音，则不进行问答
                    if (!isListen){
                        return;
                    }
                    // 如果当前是首页，则跳转到 ChatActivity进行回答问题
                    Intent intent = new Intent();
                    intent.putExtra(IntentActionConstant.ACTION_QUESTION, inputText);
                    intent.putExtra(IntentActionConstant.ROBOT_MSG, robotMsg);
                    intent.setClass(context, ChatActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });
    }

    /**
     * 语义理解接口，通过示例问题点击
     * @param inputText
     * @return
     */
    public static Promise<UnderstandingResult, UnderstandingException> understantSpeakByClick(String inputText){
        final Promise<UnderstandingResult, UnderstandingException> understand = speechManager.understand((inputText.startsWith("今天天气") || inputText.startsWith("明天天气"))? RobotConstant.ROBOT_CITY + inputText : inputText);
        return understand;
    }

    /**
     * 跳舞
     */
    public static ProgressivePromise<Void, PlayException, PlayProgress> dance(){
        Random random = new Random();
        int i = random.nextInt(danceUris.length - 1);
        return danceManager.play(danceUris[i]);
    }

    /**
     * 做动作
     * @param actionUri
     */
    public static ProgressivePromise<Void, PerformingException, PerformingProgress> performAction(Uri actionUri){
        if (actionPromise != null){
            actionPromise.cancel();
        }
        return actionPromise = motionManager.performAction(actionUri);
    }

    /**
     * 显示表情
     * @param emotionUri
     */
    public static ProgressivePromise<Void, EmotionException, ExpressingProgress> expressEmotion(Uri emotionUri){
        return emotionManager.express(emotionUri);
    }

    /**
     * 隐藏表情
     * @return
     */
    public static Promise<Void, EmotionException> dismissExpressEmotion(){
        return emotionManager.dismiss();
    }

    /**
     * 播放音乐
     * @param context
     */
    public static void playMusic(final Context context){
        File file = new File("/sdcard/Music");
        if (file.exists() && file.isDirectory()){
            final String[] lists = file.list();
            if (lists != null && lists.length != 0){
                speak("我要开始唱歌啦，您要好好欣赏哦").done(new DoneCallback<Void>() {
                    @Override
                    public void onDone(Void aVoid) {
                        String fileName = lists[0];
                        Intent intent = new Intent("com.ubt.cruzr.START_TASK");
                        intent.putExtra("data",new String[]{"/sdcard/Music/" + fileName});
                        context.sendBroadcast(intent);
                    }
                });
            }
        }else{
            speak("哎，我还没有音乐呢");
        }
    }

    /**
     * 根据名称获取标记点
     * @param location
     */
    public static void getMarkerByName(final String location, final OnMarkerCallBack markerCallBack){
        navigationManager.getCurrentNavMap().done(new DoneCallback<NavMap>() {

            @Override
            public void onDone(final NavMap navMap) {
                getMarkerName(location, new OnLocationCallBack() {
                    @Override
                    public void onSuccess(String markerName, String id) {
                        //                Marker decMarker = navigationManager.getMarkerByName(navMap, location);
                        Marker decMarker = navMap.getMarker(id);
                        if (decMarker != null){
                            markerCallBack.setMsg("正在前往“" + location + "”,请跟紧我哦");
                            markerCallBack.onSucess(decMarker);
                        }else{
                            markerCallBack.setMsg("抱歉，我在地图上没有找到" + location + "这个位置点");
                            speak("抱歉，我在地图上没有找到" + location + "这个位置点").done(new DoneCallback<Void>() {
                                @Override
                                public void onDone(Void aVoid) {
                                    markerCallBack.failed();
                                }
                            }).fail(new FailCallback<SynthesisException>() {
                                @Override
                                public void onFail(SynthesisException e) {
                                    markerCallBack.failed();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailed() {

                    }
                });
            }
        }).fail(new FailCallback<NavMapException>() {
            @Override
            public void onFail(NavMapException e) {
                e.printStackTrace();
                markerCallBack.setMsg("我还没有设置地图，请先给我设置地图");
                speak("我还没有设置地图，请先给我设置地图").done(new DoneCallback<Void>() {
                    @Override
                    public void onDone(Void aVoid) {
                        markerCallBack.failed();
                    }
                }).fail(new FailCallback<SynthesisException>() {
                    @Override
                    public void onFail(SynthesisException e) {
                        markerCallBack.failed();
                    }
                });
            }
        });
    }

    /**
     * 通过一句话获取marker点名称
     * @param pointName
     * @param callBack
     */
    public static void getMarkerName(final String pointName, final OnLocationCallBack callBack) {
        navigationManager.getCurrentNavMap().done(new DoneCallback<NavMap>() {
            @Override
            public void onDone(NavMap navMap) {
                List<Marker> markerList = navMap.getMarkerList();
                for (int i = 0; i < markerList.size(); i++) {
                    // if (pointName.contains(allMapPointModelByMapName.get(i).getPointName())) { // 原来的方法，用文字来判断
                    if (TextStyleUtils.toPinyin(pointName).contains(TextStyleUtils.toPinyin(markerList.get(i).getTitle()))) {
                        callBack.onSuccess(markerList.get(i).getTitle(), markerList.get(i).getId());
                        return;
                    }
                }
                speak("抱歉，我在地图上没有找到您要去的位置点");
                callBack.onFailed();
            }
        }).fail(new FailCallback<NavMapException>() {
            @Override
            public void onFail(NavMapException e) {
                speak("您还没有对我设置地图，请先对我设置地图");
                callBack.onFailed();
            }
        });
    }

    /**
     * 开始导航
     * @param decMarker
     */
    public static void startNavigation(Marker decMarker, final OnNaviCallBack onNaviCallBack){
        if (navigatePromise != null){
            navigatePromise.cancel();
        }
        Location location = new Location.Builder(decMarker.getPosition())
                .setRotation(decMarker.getRotation())
                .setZ(decMarker.getZ())
                .build();
        //导航到某地
        navigatePromise = navigationManager.navigate(location).done(new DoneCallback<Void>() {
            @Override
            public void onDone(Void aVoid) {
                navigatePromise = null;
                //导航成功
                onNaviCallBack.onSuccess();
            }
        }).fail(new FailCallback<NavigationException>() {
            @Override
            public void onFail(NavigationException e) {
                navigatePromise = null;
                //导航失败
                onNaviCallBack.onFailed();
            }
        });
    }

    /**
     * 导航到某地
     * @param location
     */
    public static void goPoint(String location){
        getMarkerByName(location,new OnMarkerCallBack() {
            @Override
            public void onSucess(Marker marker) {
                startNavigation(marker, new OnNaviCallBack() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailed() {

                    }
                });
            }

            @Override
            public void failed() {

            }

            @Override
            public void setMsg(String msg) {

            }
        });
    }

    /**
     * 取消导航
     */
    public static void stopNavigation(){
        if (navigatePromise != null){
            navigatePromise.cancel();
        }
    }

}
