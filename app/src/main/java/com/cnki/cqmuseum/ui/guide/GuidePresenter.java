package com.cnki.cqmuseum.ui.guide;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.cnki.cqmuseum.base.BasePresenterImpl;
import com.cnki.cqmuseum.bean.Guide;
import com.cnki.cqmuseum.constant.NavigationStateConstant;
import com.cnki.cqmuseum.constant.RobotKeyConstant;
import com.cnki.cqmuseum.interf.OnGuideListener;
import com.cnki.cqmuseum.interf.OnSpeakCallBack;
import com.cnki.cqmuseum.manager.RobotActionUtils;
import com.cnki.cqmuseum.manager.RobotManager;
import com.cnki.cqmuseum.utils.ToastUtils;
import com.ubtechinc.cruzr.sdk.navigation.NavigationApi;
import com.ubtechinc.cruzr.sdk.ros.RosConstant;
import com.ubtechinc.cruzr.sdk.ros.RosRobotApi;
import com.ubtechinc.cruzr.sdk.speech.SpeechRobotApi;
import com.ubtechinc.cruzr.serverlibutil.interfaces.NavigationApiCallBackListener;
import com.ubtechinc.cruzr.serverlibutil.interfaces.RemoteCommonListener;

import java.util.ArrayList;

/**
 * Created by liweidong on 2019/11/8.
 */

public class GuidePresenter extends BasePresenterImpl<IGuideView> {
    //当前位置点的index
    private int curPos = 0;
    private ArrayList<Guide> guides = new ArrayList<>();
    private static final int MSG_GUIDEPOINT = 1;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_GUIDEPOINT:
                    NavigationApi.get().startNavigationService((String) msg.obj);
                    break;
            }
        }
    };
    private Guide pointGuide;
    private OnGuideListener onGuideListener;

    public GuidePresenter(IGuideView view, OnGuideListener onGuideListener) {
        super(view);
        this.onGuideListener = onGuideListener;
    }

    /**
     * 设置所有导览位置点
     * @param guideRoutes
     */
    public void setGuides(ArrayList<Guide> guideRoutes){
        this.guides = guideRoutes;
    }

    /**
     * 开始导览
     */
    public void startNaviRoute(){
        //如果是暂停导览，则返回
        if (GuideActivity.naviState == NavigationStateConstant.STATE_PAUSE){
            return;
        }
        curPos++;
        if (curPos < guides.size() - 1){
            final Guide guide = guides.get(curPos);
            if (RobotActionUtils.isCanNavi(guide.getName())){
                RosRobotApi.get().registerCommonCallback(new RemoteCommonListener() {
                    @Override
                    public void onResult(int i, int i1, String s) {

                    }
                });
                NavigationApi.get().setNavigationApiCallBackListener(new NavigationApiCallBackListener() {
                    @Override
                    public void onNavigationResult(int i, float v, float v1, float v2) {

                    }

                    @Override
                    public void onRemoteCommonResult(String pointName, int status, String message) {
                        switch (status) {
                            case RosConstant.Action.ACTION_FINISHED:
                                //导航成功
                                mView.reachBack(guides.get(curPos));
                                RobotManager.speechVoice("您好" + pointName + "已经到了", new OnSpeakCallBack() {
                                    @Override
                                    public void onSpeakEnd() {
                                        RobotManager.speechVoice(guides.get(curPos).getIntroduce(), new OnSpeakCallBack() {
                                            @Override
                                            public void onSpeakEnd() {
                                                //延迟5秒继续导览
                                                mHandler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        startNaviRoute();
                                                    }
                                                },5000);
                                            }
                                        });
                                    }
                                });
                                break;
                            case RosConstant.Action.ACTION_CANCEL:
                                //导航取消
                                RobotManager.speechVoice("导航取消");
                                break;
                            case RosConstant.Action.ACTION_BE_IMPEDED:
                                //导航遇到障碍
                                RobotManager.speechVoice("有什么挡到我了");
                                break;
                            case RosConstant.Action.ACTION_FAILED:
                            case RosConstant.Action.ACTION_DEVICE_CONFLICT:
                            case RosConstant.Action.ACTION_EMERGENCY_STOPPED:
                            case RosConstant.Action.ACTION_ACCESS_FORBIDDEN:
                            case RosConstant.Action.ACTION_UNIMPLEMENTED:
                                //导航失败
                                RobotManager.speechVoice("导航好像出现了点问题，请联系管理员设置地图", new OnSpeakCallBack() {
                                    @Override
                                    public void onSpeakEnd() {
                                        finishActivityAndAskGoWelcome();
                                    }
                                });
                                break;
                            default:
                                break;
                        }
                    }
                });
                //通知UI改变
                mView.notifyGuideUi(guide.getName());
                RobotManager.speechVoice("我将要带您前往" + guide.getName() + "，请跟紧我哦");
                Message msg = Message.obtain();
                msg.what = MSG_GUIDEPOINT;
                msg.obj = guide.getName();
                mHandler.sendMessageDelayed(msg,7000);
            }else{
                RobotManager.speechVoice("我的定位好像出问题了，请联系管理员", new OnSpeakCallBack() {
                    @Override
                    public void onSpeakEnd() {
                        finishActivity();
                    }
                });
            }
        }else{
            //导览完成，退出界面
            RobotActionUtils.playSettingAction(RobotKeyConstant.ROBOTKEY_BYE);
            onGuideListener.finishGuide();
            RobotManager.speechVoice("我带您的游览已经结束了，下次再见！", new OnSpeakCallBack() {
                @Override
                public void onSpeakEnd() {
                    finishActivityAndAskGoWelcome();
                }
            });
        }
    }

    /**
     * 停止导航,退出界面
     */
    public void stopNaviRoute(){
        GuideActivity.naviState = NavigationStateConstant.STATE_PAUSE;
        mHandler.removeMessages(MSG_GUIDEPOINT);
        NavigationApi.get().stopNavigationService();
        RobotManager.speechVoice("停止导航成功");
        finishActivityAndAskGoWelcome();
    }

    /**
     * 暂停导览
     */
    public void pauseNaviRoute(){
        GuideActivity.naviState = NavigationStateConstant.STATE_PAUSE;
        mHandler.removeMessages(MSG_GUIDEPOINT);
        RobotActionUtils.stopNavigation();
        onGuideListener.pauseGuide();
    }

    /**
     * 继续导览
     */
    public void continueNaviRoute(){
        GuideActivity.naviState = NavigationStateConstant.STATE_NAVING;
        startNaviRoute();
        onGuideListener.continueGuide();
    }

    /**
     * 导航到某地
     * @param point
     */
    public void goPoint(final String point){
        pointGuide = null;
        for (Guide guide : guides){
            if (guide.getName().equals(point)){
                pointGuide = guide;
                break;
            }
        }
        RosRobotApi.get().registerCommonCallback(new RemoteCommonListener() {
            @Override
            public void onResult(int i, int i1, String s) {

            }
        });
        NavigationApi.get().setNavigationApiCallBackListener(new NavigationApiCallBackListener() {
            @Override
            public void onNavigationResult(int i, float v, float v1, float v2) {

            }

            @Override
            public void onRemoteCommonResult(String pointName, int status, String message) {
                switch (status) {
                    case RosConstant.Action.ACTION_FINISHED:
                        //导航成功
                        if (pointGuide != null){
                            mView.reachBack(pointGuide);
                        }
                        RobotManager.speechVoice("您好" + pointName + "已经到了", new OnSpeakCallBack() {
                            @Override
                            public void onSpeakEnd() {
                                RobotManager.speechVoice(pointGuide.getIntroduce(), new OnSpeakCallBack() {
                                    @Override
                                    public void onSpeakEnd() {
                                        //关闭界面
                                        finishActivityAndAskGoWelcome();
                                    }
                                });
                            }
                        });
                        break;
                    case RosConstant.Action.ACTION_CANCEL:
                        //导航取消
                        RobotManager.speechVoice("导航取消");
                        finishActivity();
                        break;
                    case RosConstant.Action.ACTION_BE_IMPEDED:
                        //导航遇到障碍
                        RobotManager.speechVoice("有什么挡到我了");
                        break;
                    case RosConstant.Action.ACTION_FAILED:
                    case RosConstant.Action.ACTION_DEVICE_CONFLICT:
                    case RosConstant.Action.ACTION_EMERGENCY_STOPPED:
                    case RosConstant.Action.ACTION_ACCESS_FORBIDDEN:
                    case RosConstant.Action.ACTION_UNIMPLEMENTED:
                        //导航失败
                        RobotManager.speechVoice("导航好像出现了点问题，请联系管理员设置地图", new OnSpeakCallBack() {
                            @Override
                            public void onSpeakEnd() {
                                finishActivityAndAskGoWelcome();
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        });
        //通知UI改变
        mView.notifyGuideUi(point);
        RobotManager.speechVoice("我将要带您前往" + point + "，请跟紧我哦");
        Message msg = Message.obtain();
        msg.what = MSG_GUIDEPOINT;
        msg.obj = point;
        mHandler.sendMessageDelayed(msg,7000);
    }

    /**
     * 关闭界面
     */
    private void finishActivity(){
        //关闭界面
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((GuideActivity)mContext).finish();
            }
        },2000);
    }

    /**
     * 结束界面，并且询问是否返回接待点
     */
    private void finishActivityAndAskGoWelcome(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RobotManager.speechVoice("需要我返回接待点吗？", new OnSpeakCallBack() {
                    @Override
                    public void onSpeakEnd() {
                        //延迟3s等待用户是否需要返回接待点
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //判断是否需要返回接待点
                                if (GuideActivity.isNeedGoHome){
                                    RobotActionUtils.goPointNavi(mContext, "接待点");
                                }
                                ((GuideActivity)mContext).finish();
                            }
                        }, 3000);
                    }
                });
            }
        },2000);
    }

    /**
     * 返回接待点
     * @param msg
     */
    public void goWelcomePoint(String msg){
        //判断如果正在讲话，则停止讲话
        if (RobotManager.isSpeaking()){
            RobotManager.stopSpeech();
        }
        //设置为返航中
        GuideActivity.naviState = NavigationStateConstant.STATE_RETURN;
        final Guide welcomePoint = guides.get(0);
        NavigationApi.get().setNavigationApiCallBackListener(new NavigationApiCallBackListener() {
            @Override
            public void onNavigationResult(int i, float v, float v1, float v2) {

            }

            @Override
            public void onRemoteCommonResult(String pointName, int status, String message) {
                switch (status) {
                    case RosConstant.Action.ACTION_FINISHED:
                        RobotManager.speechVoice("返回接待点成功");
                        ((GuideActivity)mContext).finish();
                        break;
                    case RosConstant.Action.ACTION_CANCEL:
                        RobotManager.speechVoice("取消返回接待点");
                        //导航取消
                        ((GuideActivity)mContext).finish();
                        break;
                    case RosConstant.Action.ACTION_BE_IMPEDED:
                        //导航遇到障碍
                        RobotManager.speechVoice("有什么挡到我了");
                        break;
                    case RosConstant.Action.ACTION_UNIMPLEMENTED:
                        //导航失败
                        RobotManager.speechVoice("导航好像出现了点问题，请联系管理员设置地图", new OnSpeakCallBack() {
                            @Override
                            public void onSpeakEnd() {
                                NavigationApi.get().stopNavigationService();
                                ((GuideActivity)mContext).finish();
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        });
        RobotManager.speechVoice(msg, new OnSpeakCallBack() {
            @Override
            public void onSpeakEnd() {
                RobotActionUtils.playSettingAction(RobotKeyConstant.ROBOTKEY_BYE);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //回接待点
                        if (RobotActionUtils.isCanNavi(welcomePoint.getName())){
                            //通知UI改变
                            mView.notifyGuideUi(welcomePoint.getName());
                            NavigationApi.get().startNavigationService(welcomePoint.getName());
                        }else{
                            ToastUtils.toast(mContext,"我的定位好像出问题了，请联系管理员");
                            finishActivity();
                        }
                    }
                },15000);
            }
        });
    }
}
