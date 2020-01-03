package com.cnki.cqmuseum.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import com.cnki.cqmuseum.bean.BaseEvenBusBean;
import com.cnki.cqmuseum.constant.EvenBusConstant;
import com.cnki.cqmuseum.constant.IntentActionConstant;
import com.cnki.cqmuseum.constant.RobotKeyConstant;
import com.cnki.cqmuseum.ui.guide.GuideActivity;
import com.cnki.cqmuseum.ui.navigation.NavigationActivity;
import com.cnki.cqmuseum.utils.LogUtils;
import com.cnki.cqmuseum.utils.MapUtils;
import com.cnki.cqmuseum.utils.RandomUtils;
import com.ubtechinc.cruzr.sdk.dance.DanceConnectionListener;
import com.ubtechinc.cruzr.sdk.dance.DanceConstant;
import com.ubtechinc.cruzr.sdk.dance.DanceControlApi;
import com.ubtechinc.cruzr.sdk.dance.RemoteDanceListener;
import com.ubtechinc.cruzr.sdk.face.CruzrFaceApi;
import com.ubtechinc.cruzr.sdk.face.StringUtils;
import com.ubtechinc.cruzr.sdk.navigation.NavigationApi;
import com.ubtechinc.cruzr.sdk.navigation.model.MapPointModel;
import com.ubtechinc.cruzr.sdk.ros.RosConstant;
import com.ubtechinc.cruzr.sdk.ros.RosRobotApi;
import com.ubtechinc.cruzr.serverlibutil.aidl.Position;
import com.ubtechinc.cruzr.serverlibutil.interfaces.NavigationApiCallBackListener;
import com.ubtechinc.cruzr.serverlibutil.interfaces.RemoteCommonListener;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 机器人意图工具类
 * Created by admin on 2019/6/25.
 */

public class RobotActionUtils {
    private static int mSetMapId = 0;//开始设置地图的id
    private static int mStartLocationId = 0;//开始定位的id
    private static int mStopLocationId = 0;//停止定位的id
    private static String locationName = "";//将要去的位置名称
    private static String mapName = "";//地图名称
    private static String goPointName = "";//将要去的位置点
    private static ArrayList<MapPointModel> chargings;//充电桩位置点集合
    private static int chargingIndex = 0;//充电桩位置
    private static int pointLocationId = 0;

    /**
     * 左转右转多少度
     * @param angle
     */
    public static void tureRotate(float angle){
        float rotate = angle / 58;
        moveTo(0,0, rotate);
    }

    /**
     * 左转圈
     */
    public static void tureLeftCircle(){
        moveToward(0,0, 0.8f);
    }

    /**
     * 右转圈
     */
    public static void tureRightCircle(){
        moveToward(0,0, -0.8f);
    }

    /**
     * 向前向后移动多少米
     * @param metes
     */
    public static void moveBackOrForthMete(float metes){
        moveTo(metes, 0, 0);
    }

    /**
     * 向后一直移动
     */
    public static void moveBackStill(){
        moveToward(-0.5f,0,0);
    }

    /**
     * 向前一直移动
     */
    public static void moveForthStill(){
        moveToward(0.5f,0,0);
    }

    /**
     * 初始化舞蹈服务
     * @param context
     */
    public static void initDanceServer(Context context){
        // 初始化方法
        DanceControlApi.getInstance().initialize(context, new DanceConnectionListener() {
            @Override
            public void onConnected() {
                //与舞蹈服务成功建立连接
                LogUtils.e("lwd", "与舞蹈服务成功建立连接");

            }

            @Override
            public void onDisconnected() {
                //与舞蹈服务连接断开
                LogUtils.e("lwd", "与舞蹈服务连接断开");
                stopDance();
            }

            @Override
            public void onReconnected() {
                //与舞蹈舞蹈服务重新连接
                LogUtils.e( "lwd", "与舞蹈舞蹈服务重新连接");
            }
        });
    }

    /**
     * 开始跳舞指令
     */
    public static void startDance() {
        ArrayList<String> dances = RandomUtils.getRandomArray(1, RobotKeyConstant.dances);
        if (dances != null && dances.size() != 0){
            DanceControlApi.getInstance().dance(dances.get(0), new RemoteDanceListener() {
                @Override
                public void onResult(int status) {
                    switch (status){
                        case DanceConstant.STATE_DANCE_START:
                            //舞蹈启动
                            break;
                        case DanceConstant.STATE_DANCE_COMPLETE:
                            //舞蹈正常结束
                            break;
                        case DanceConstant.STATE_DANCE_CANCEL:
                            //舞蹈被取消
                            break;
                        case DanceConstant.STATE_DANCE_FORBIDDEN:
                            //机器人手臂被锁定，禁止跳舞
                            RobotManager.speechVoice("机器人手臂被锁定，禁止跳舞");
                            break;
                        case DanceConstant.STATE_DANCE_FAIL:
                            //舞蹈失败
                            break;
                    }
                }
            });
        }
    }

    /**
     * 停止跳舞
     */
    public static void stopDance(){
        DanceControlApi.getInstance().stop();
    }

    /**
     * 是否正在跳舞
     * @return
     */
    public static boolean isDancing(){
        return DanceControlApi.getInstance().isDancing();
    }

    /**
     * 播放音乐
     * @param context
     */
//    public static void playMusic(Context context){
//        File file = new File(RobotConstant.ROBOT_MUSICPATH);
//        if (file.exists() && file.isDirectory()){
//            String[] lists = file.list();
//            if (lists != null && lists.length != 0){
//                String fileName = lists[0];
//                Intent intent = new Intent("com.ubt.cruzr.START_TASK");
//                intent.putExtra("data",new String[]{RobotConstant.ROBOT_MUSICPATH + "/" + fileName});
//                context.sendBroadcast(intent);
//                return;
//            }
//        }
//        LogUtils.e("没有音乐文件");
//        RobotManager.speechVoice("哎，我还没有音乐呢");
////        PackageUtils.startNewApp(context, "com.ubtechinc.cruzr.music","com.ubtechinc.cruzr.music.MainActivity");
//    }

    /**
     * 停止播放音乐
     * @param context
     */
    public static void stopMusicAndVideo(Context context){
        Intent intent = new Intent("com.ubt.cruzr.END_TASK");
        context.sendBroadcast(intent);
    }

    /**
     * 播放视频
     * @param context
     */
//    public static void playVideo(Context context){
////        PackageUtils.startNewApp(context, "com.ubtechinc.cruzr.video","com.ubtechinc.cruzr.video.MainActivity");
//        File file = new File(RobotConstant.ROBOT_VIDEOPATH);
//        if (file.exists() && file.isDirectory()){
//            String[] lists = file.list();
//            if (lists != null && lists.length != 0){
//                String fileName = lists[0];
//                Intent intent = new Intent("com.ubt.cruzr.START_TASK");
//                intent.putExtra("type",1);
//                intent.putExtra("data",new String[]{RobotConstant.ROBOT_VIDEOPATH + "/" + fileName});
//                context.sendBroadcast(intent);
//                return;
//            }
//        }
//        RobotManager.speechVoice("哎，我还没有视频呢");
//    }

    /**
     * 开始定位
     */
    public static void startLocation(){
        //初始化数据
        if (chargings != null){
            chargings.clear();
        }
        chargingIndex = 0;
        pointLocationId = 0;
        //注册监听
        RosRobotApi.get().registerCommonCallback(new RemoteCommonListener() {
            @Override
            public void onResult(int sectionId, int status, String message) {
                //开始定位
                if(pointLocationId == sectionId){
                    switch (status) {
                        case RosConstant.Action.ACTION_FINISHED://结束
                            LogUtils.e("lwd","定位成功");
                            break;
                        case RosConstant.Action.ACTION_START://正在定位
                            RobotManager.speechVoice("我正在定位，请稍等");
                            LogUtils.e("lwd","正在定位:" + chargingIndex);
                            break;
                        case RosConstant.Action.ACTION_CANCEL://取消
                        case RosConstant.Action.ACTION_BE_IMPEDED://阻碍，挡路
                        case RosConstant.Action.ACTION_FAILED://失败
                        case RosConstant.Action.ACTION_DEVICE_CONFLICT://设备冲突
                        case RosConstant.Action.ACTION_EMERGENCY_STOPPED://紧急停止
                        case RosConstant.Action.ACTION_ACCESS_FORBIDDEN://访问被禁止
                        case RosConstant.Action.ACTION_UNIMPLEMENTED://未实现
                            if (chargings != null && chargings.size() != 0 && chargingIndex < chargings.size()){
                                LogUtils.e("lwd","定位失败，开启下一次定位:" + chargingIndex);
                                //进行局部定位
                                MapPointModel mapPointModel = chargings.get(chargingIndex);
                                chargingIndex++;
                                Float mapX = Float.valueOf(mapPointModel.getMapX());
                                Float mapY = Float.valueOf(mapPointModel.getMapY());
                                Float mapTheta = Float.valueOf(mapPointModel.getTheta());
                                pointLocationId = RosRobotApi.get().navigateRelocationStartByPos(mapX,mapY,mapTheta);
                            }else{
                                RobotManager.speechVoice("地图上没有找到我的位置，请叫一下我的管理员对我定位");
                            }
                            break;
                        default:
                            break;
                    }
                    return;
                }
            }
        });
        //2.获取当前地图
        String rosMapPath = RosRobotApi.get().getCurrentMap();
        if (TextUtils.isEmpty(rosMapPath)){
            RobotManager.speechVoice("我还没有默认地图，请联系管理员进行设置");
        }else{
            mapName = MapUtils.getFileName(rosMapPath);
            if (TextUtils.isEmpty(mapName)){
                RobotManager.speechVoice("我还没有默认地图，请联系管理员进行设置");
            }else{
                mSetMapId = RosRobotApi.get().setCurrentMap(mapName);
                RobotManager.speechVoice("设置地图成功");
                LogUtils.e("lwd","设置地图成功");
                //3.查询地图位置点
                List<MapPointModel> mapPointModels = NavigationApi.get().queryAllMapPointByMapName(mapName);
                if (mapPointModels != null && mapPointModels.size() != 0){
                    //获取充电桩的位置点信息
                    chargings = new ArrayList<>();
                    for (MapPointModel mapPointModel : mapPointModels){
                        String pointType = mapPointModel.getPointType();
                        if (pointType.equals("charge_position")){
                            chargings.add(mapPointModel);
                        }
                    }
                    //判断充电桩数量不为空，则循环定位充电桩
                    if (chargings != null && chargings.size() != 0){
                        LogUtils.e("lwd","成功获取充电桩数量：" + chargings.size());
                        //进行局部定位
                        MapPointModel mapPointModel = chargings.get(chargingIndex);
                        chargingIndex++;
                        Float mapX = Float.valueOf(mapPointModel.getMapX());
                        Float mapY = Float.valueOf(mapPointModel.getMapY());
                        Float mapTheta = Float.valueOf(mapPointModel.getTheta());
                        pointLocationId = RosRobotApi.get().navigateRelocationStartByPos(mapX,mapY,mapTheta);
                    }
                }else{
                    RobotManager.speechVoice("我的地图上没有位置点，请联系管理员进行设置");
                }
            }
        }
    }


    /**
     * 开始导航
     * @param pointName
     */
    public static void startNavigation(final String pointName){
        //初始化数据
        mSetMapId = 0;
        mStartLocationId = 0;
        mStopLocationId = 0;
        locationName = "";
        mapName = "";
        goPointName = "";
        //1.注册监听设置地图、开始定位、取消定位的事件回调
        RosRobotApi.get().registerCommonCallback(new RemoteCommonListener() {
            @Override
            public void onResult(int sectionId, int status, String message) {
                //设置地图
                if(mSetMapId == sectionId){
                    switch (status) {
                        case RosConstant.Action.ACTION_FINISHED://结束
                            break;
                        case RosConstant.Action.ACTION_CANCEL://取消
                        case RosConstant.Action.ACTION_BE_IMPEDED://阻碍，挡路
                        case RosConstant.Action.ACTION_FAILED://失败
                        case RosConstant.Action.ACTION_DEVICE_CONFLICT://设备冲突
                        case RosConstant.Action.ACTION_EMERGENCY_STOPPED://紧急停止
                        case RosConstant.Action.ACTION_ACCESS_FORBIDDEN://访问被禁止
                        case RosConstant.Action.ACTION_UNIMPLEMENTED://未实现

                            break;
                        default:
                            break;
                    }
                    return;
                }
                //开始定位
                if(mStartLocationId == sectionId){
                    switch (status) {
                        case RosConstant.Action.ACTION_FINISHED://结束
                            LogUtils.e("lwd","定位成功");
                            //5.开始导航
                            RobotManager.speechVoice("我这就带您去" + pointName);
                            NavigationApi.get().startNavigationService(goPointName);
                            break;
                        case RosConstant.Action.ACTION_START://正在定位
                            RobotManager.speechVoice("我正在定位，请稍等");
                            break;
                        case RosConstant.Action.ACTION_CANCEL://取消
                        case RosConstant.Action.ACTION_BE_IMPEDED://阻碍，挡路
                        case RosConstant.Action.ACTION_FAILED://失败
                            RobotManager.speechVoice("我好像跑错位置了，请叫我的管理员带我去定位吧！");
                            break;
                        case RosConstant.Action.ACTION_DEVICE_CONFLICT://设备冲突
                        case RosConstant.Action.ACTION_EMERGENCY_STOPPED://紧急停止
                        case RosConstant.Action.ACTION_ACCESS_FORBIDDEN://访问被禁止
                        case RosConstant.Action.ACTION_UNIMPLEMENTED://未实现
                            RobotManager.speechVoice("地图上没有找到我的位置，请叫一下我的管理员对我定位");
                            break;
                        default:
                            break;
                    }
                    return;
                }
                //取消定位
                if(mStopLocationId == sectionId){
                    switch (status) {
                        case RosConstant.Action.ACTION_FINISHED://结束
                        case RosConstant.Action.ACTION_CANCEL://取消
                        case RosConstant.Action.ACTION_BE_IMPEDED://阻碍，挡路
                        case RosConstant.Action.ACTION_FAILED://失败
                        case RosConstant.Action.ACTION_DEVICE_CONFLICT://设备冲突
                        case RosConstant.Action.ACTION_EMERGENCY_STOPPED://紧急停止
                        case RosConstant.Action.ACTION_ACCESS_FORBIDDEN://访问被禁止
                        case RosConstant.Action.ACTION_UNIMPLEMENTED://未实现

                            break;
                        default:
                            break;
                    }
                    return;
                }
            }
        });
        //注册导航功能事件回调
        NavigationApi.get().setNavigationApiCallBackListener(new NavigationApiCallBackListener() {
            @Override
            public void onNavigationResult(int i, float v, float v1, float v2) {

            }

            @Override
            public void onRemoteCommonResult(String pointName, int status, String message) {
                switch (status) {
                    case RosConstant.Action.ACTION_FINISHED://结束
                        RobotManager.speechVoice(pointName + "已经到了");
                        break;
                    case RosConstant.Action.ACTION_CANCEL://取消
                        break;
                    case RosConstant.Action.ACTION_BE_IMPEDED://阻碍，挡路
                        RobotManager.speechVoice("有什么挡到我了");
                        break;
                    case RosConstant.Action.ACTION_FAILED://失败
                    case RosConstant.Action.ACTION_DEVICE_CONFLICT://设备冲突
                    case RosConstant.Action.ACTION_EMERGENCY_STOPPED://紧急停止
                    case RosConstant.Action.ACTION_ACCESS_FORBIDDEN://访问被禁止
                    case RosConstant.Action.ACTION_UNIMPLEMENTED://未实现
                        RobotManager.speechVoice("导航好像出现了点问题，请联系管理员设置地图");
                        stopNavigation();
                        break;
                    default:
                        break;
                }
            }
        });
        //2.获取当前地图
        String rosMapPath = RosRobotApi.get().getCurrentMap();
        if (TextUtils.isEmpty(rosMapPath)){
            RobotManager.speechVoice("我还没有默认地图，请联系管理员进行设置");
        }else{
            mapName = MapUtils.getFileName(rosMapPath);
            if (TextUtils.isEmpty(mapName)){
                RobotManager.speechVoice("我还没有默认地图，请联系管理员进行设置");
            }else{
                mSetMapId = RosRobotApi.get().setCurrentMap(mapName);
                RobotManager.speechVoice("设置地图成功");
                //3.查询地图位置点
                List<MapPointModel> mapPointModels = NavigationApi.get().queryAllMapPointByMapName(mapName);
                if (mapPointModels != null && mapPointModels.size() != 0){
                    //4.跟兴趣点对比,找出将要去的点
                    goPointName = MapUtils.getInstance().comparePointNameWithCurrentMap(mapPointModels, pointName);
                    if (TextUtils.isEmpty(goPointName)){
                        RobotManager.speechVoice("我没有找到您要找的位置点，请确认是否已经在地图中设置了该点");
                    }else{
                        //获取当前位置点信息
                        Position curPosition = RosRobotApi.get().getPosition(false);
                        float mX = (float) 2.139095E9;
                        if (curPosition != null && curPosition.x != mX){
                            //5.开始导航
                            RobotManager.speechVoice("我这就带您去" + pointName);
                            NavigationApi.get().startNavigationService(goPointName);
                        }else{
//                            MapPointModel mapPointModel = mapPointModels.get(0);
//                            Float mapX = Float.valueOf(mapPointModel.getMapX());
//                            Float mapY = Float.valueOf(mapPointModel.getMapY());
//                            Float mapTheta = Float.valueOf(mapPointModel.getTheta());
//                            mStartLocationId = RosRobotApi.get().navigateRelocationStartByPos(mapX,mapY,mapTheta);
                            mStartLocationId = RosRobotApi.get().navigateRelocationCtrl(0);
                        }
                    }
                }else{
                    RobotManager.speechVoice("我的地图上没有位置点，请联系管理员进行设置");
                }
            }
        }
    }

    /**
     * 停止导航
     */
    public static void stopNavigation(){
        NavigationApi.get().stopNavigationService();
    }


    /**
     * 预设动作播放
     * @param key
     */
    public static void playSettingAction(String key){
        RosRobotApi.get().run(key);
    }

    /**
     * 设置表情
     * @param key
     */
    public static void playSettingFace(String key){
        CruzrFaceApi.setCruzrFace(null,key,true,true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSettingFace();
            }
        },3000);
    }

    /**
     * 停止表情
     */
    public static void stopSettingFace(){
        CruzrFaceApi.setCruzrFace(null,"clean",true,true);

    }

    /**
     * 从当前位置移动到x，y坐标
     * @param x  单位米可以为负数，左右
     * @param y  单位米可以为负数，前后
     * @param theta 左右转，-2到2之间
    //     * @param maxSpeed 左右最大速度-2 到 2之间
     */
    public static void moveTo(float x, float y, float theta){
        RosRobotApi.get().moveTo(x, y, theta, 0.5f, new RemoteCommonListener() {
            @Override
            public void onResult(int i, final int status, final String message) {
                //status   0 -- start； 1 -- onGoing； 2 -- finished； 3 -- cancel； 4 -- impeded； 5 -- failed 6 -- device conflict 7 -- emergency stopped 8 -- abnormal suspend 9 -- access forbidden 10
                LogUtils.e("lwd","status:" + status + ",message:" + message);
                if (status == RosConstant.Action.ACTION_BE_IMPEDED){
                    RobotManager.speechVoice("有什么挡到我了");
                }
            }
        });
    }

    /**
     * 移动，碰到障碍物才停止
     * @param x 前后移动
     * @param y 左右移动
     * @param rotate 旋转角度
     */
    public static void moveToward(float x, float y, float rotate){
        RosRobotApi.get().moveToward(x, y, rotate, new RemoteCommonListener() {
            @Override
            public void onResult(int i, int status, String message) {
                LogUtils.e("lwd","status:" + status + ",message:" + message);
                if (status == RosConstant.Action.ACTION_BE_IMPEDED){
                    RobotManager.speechVoice("有什么挡到我了");
                }
            }
        });
    }


    /**
     * 停止移动
     */
    public static void stopMove(){
        RosRobotApi.get().stopMove();
    }

    /**
     * 是否移动
     * @return
     */
    public static boolean isMoving(){
        return RosRobotApi.get().isMoveActive();
    }

    /**
     * 调用机器人导航动作
     * @param context
     * @param requestString
     * @param isStartNew 是否跳转新界面
     */
    public static void startNavigation(Context context,String requestString, boolean isStartNew) {
        //获取当前地图
        String rosMapPath = RosRobotApi.get().getCurrentMap();
        if (TextUtils.isEmpty(rosMapPath)){
            RobotManager.speechVoice("我还没有默认地图，请联系管理员进行设置");
        }else{
            mapName = MapUtils.getFileName(rosMapPath);
            if (TextUtils.isEmpty(mapName)){
                RobotManager.speechVoice("我还没有默认地图，请联系管理员进行设置");
            }else{
                //查询地图位置点
                List<MapPointModel> mapPointModels = NavigationApi.get().queryAllMapPointByMapName(mapName);
                if (mapPointModels != null && mapPointModels.size() != 0){
                    //4.跟兴趣点对比,找出将要去的点
                    goPointName = MapUtils.getInstance().comparePointNameWithCurrentMap(mapPointModels, requestString);
                    if (TextUtils.isEmpty(goPointName)){
                        RobotManager.speechVoice("我在地图上没有找到" + goPointName + "这个位置，请确认是否设置了该点");
                    }else{
                        //获取当前位置点信息
                        Position curPosition = RosRobotApi.get().getPosition(false);
                        float mX = (float) 2.139095E9;
                        if (curPosition != null && curPosition.x != mX){
                            //地图中有该位置点 开始导航
                            if (isStartNew){
                                Intent intent = new Intent(context, GuideActivity.class);
                                intent.putExtra(IntentActionConstant.NAVI_LOCATION, goPointName);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }else{
                                BaseEvenBusBean bean = new BaseEvenBusBean(EvenBusConstant.EVENBUS_GOPOINT);
                                bean.setObject(goPointName);
                                EventBus.getDefault().post(bean);
                            }
                        }else{
                            RobotManager.speechVoice("定位失败，请重新对我进行定位");
                        }
                    }
                }else{
                    RobotManager.speechVoice("我的地图上没有位置点，请联系管理员进行设置");
                }
            }
        }
    }

    /**
     * 是否能够进行导航
     * @param pointName
     * @return
     */
    public static boolean isCanNavi(String pointName) {
        //获取当前地图
        String rosMapPath = RosRobotApi.get().getCurrentMap();
        if (TextUtils.isEmpty(rosMapPath)){
            RobotManager.speechVoice("我还没有默认地图，请联系管理员进行设置");
            return false;
        }else{
            mapName = MapUtils.getFileName(rosMapPath);
            if (TextUtils.isEmpty(mapName)){
                RobotManager.speechVoice("我还没有默认地图，请联系管理员进行设置");
                return false;
            }else{
                //查询地图位置点
                List<MapPointModel> mapPointModels = NavigationApi.get().queryAllMapPointByMapName(mapName);
                if (mapPointModels != null && mapPointModels.size() != 0){
                    //4.跟兴趣点对比,找出将要去的点
                    boolean isContainsPoint = MapUtils.getInstance().isContainsPoint(mapPointModels, pointName);
                    if (!isContainsPoint){
                        RobotManager.speechVoice("我在地图上没有找到" + goPointName + "这个位置，请确认是否设置了该点");
                        return false;
                    }else{
                        //获取当前位置点信息
                        Position curPosition = RosRobotApi.get().getPosition(false);
                        float mX = (float) 2.139095E9;
                        if (curPosition != null && curPosition.x != mX){
                            return true;
                        }else{
                            RobotManager.speechVoice("我在地图上没有找到" + goPointName + "这个位置，请确认是否设置了该点");
                            return false;
                        }
                    }
                }else{
                    RobotManager.speechVoice("我的地图上没有位置点，请联系管理员进行设置");
                    return false;
                }
            }
        }
    }

    /**
     * 导航去某地
     * @param context
     * @param result
     */
    public static void goPointNavi(Context context,String result){
        String rosMapPath = RosRobotApi.get().getCurrentMap();
        if (TextUtils.isEmpty(rosMapPath)){
            RobotManager.speechVoice("我还没有默认地图，请联系管理员进行设置");
        }else{
            mapName = MapUtils.getFileName(rosMapPath);
            if (TextUtils.isEmpty(mapName)){
                RobotManager.speechVoice("我还没有默认地图，请联系管理员进行设置");
            }else{
                //查询地图位置点
                List<MapPointModel> mapPointModels = NavigationApi.get().queryAllMapPointByMapName(mapName);
                if (mapPointModels != null && mapPointModels.size() != 0){
                    //4.跟兴趣点对比,找出将要去的点
                    String location = MapUtils.getInstance().comparePointNameWithCurrentMap(mapPointModels, result);
                    if (TextUtils.isEmpty(location)){
                        RobotManager.speechVoice("我在地图上没有找到" + location + "这个位置，请确认是否设置了该点");
                    }else{
                        //获取当前位置点信息
                        Position curPosition = RosRobotApi.get().getPosition(false);
                        float mX = (float) 2.139095E9;
                        if (curPosition != null && curPosition.x != mX){
                            Intent intent = new Intent(context, NavigationActivity.class);
                            intent.putExtra(IntentActionConstant.NAVI_LOCATION, location);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }else{
                            RobotManager.speechVoice("您还没有对我进行定位，请先对我进行定位");
                        }
                    }
                }else{
                    RobotManager.speechVoice("我的地图上没有设置任何导航点，请联系管理员给我设置");
                }
            }
        }
    }

}
