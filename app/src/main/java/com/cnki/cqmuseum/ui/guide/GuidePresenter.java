package com.cnki.cqmuseum.ui.guide;

import android.os.Handler;
import android.os.Message;

import com.cnki.cqmuseum.base.BasePresenterImpl;
import com.cnki.cqmuseum.bean.Guide;
import com.cnki.cqmuseum.constant.NavigationStateConstant;
import com.cnki.cqmuseum.constant.RobotKeyConstant;
import com.cnki.cqmuseum.interf.OnGuideListener;
import com.cnki.cqmuseum.interf.OnMarkerCallBack;
import com.cnki.cqmuseum.interf.OnNaviCallBack;
import com.cnki.cqmuseum.interf.OnSpeakCallBack;
import com.cnki.cqmuseum.manager.RobotManager;
import com.cnki.cqmuseum.utils.ToastUtils;
import com.ubtrobot.async.DoneCallback;
import com.ubtrobot.async.FailCallback;
import com.ubtrobot.emotion.EmotionUris;
import com.ubtrobot.motion.ActionUris;
import com.ubtrobot.navigation.Marker;
import com.ubtrobot.speech.SynthesisException;

import java.util.ArrayList;

/**
 * Created by liweidong on 2019/11/8.
 */

public class GuidePresenter extends BasePresenterImpl<IGuideView> {
    //当前位置点的index
    private int curPos = 0;
    private ArrayList<Guide> guides = new ArrayList<>();
    private Guide pointGuide;
    private OnGuideListener onGuideListener;
    private static final int MSG_GUIDEPOINT = 1;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_GUIDEPOINT:
                    startNaviRoute();
                    break;
            }
        }
    };

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
            RobotManager.getMarkerByName(guide.getName(), new OnMarkerCallBack() {
                @Override
                public void onSucess(final Marker marker) {
                    //通知UI改变
                    mView.notifyGuideUi(guide.getName());
                    RobotManager.speak("我将要带您前往" + guide.getName() + "，请跟紧我哦")
                        .done(new DoneCallback<Void>() {
                            @Override
                            public void onDone(Void aVoid) {
                                navigation(marker);
                            }
                        })
                        .fail(new FailCallback<SynthesisException>() {
                            @Override
                            public void onFail(SynthesisException e) {
                                navigation(marker);
                            }
                        });
                }

                @Override
                public void failed() {
                    finishActivity();
                }

                @Override
                public void setMsg(String msg) {

                }
            });
        }else{
            //导览完成，退出界面
            //播报文字
            RobotManager.speak("我带您的游览已经结束了，下次再见！").done(new DoneCallback<Void>() {
                @Override
                public void onDone(Void aVoid) {
                    //说完话之后隐藏表情
                    RobotManager.dismissExpressEmotion();
                    finishActivityAndAskGoWelcome();
                }
            });
            //握手
            RobotManager.performAction(ActionUris.GOODBYE);
            //显示表情
            RobotManager.expressEmotion(EmotionUris.SMILE);
            onGuideListener.finishGuide();
        }
    }

    /**
     * 导航到某地
     * @param marker
     */
    private void navigation(final Marker marker){
        RobotManager.startNavigation(marker, new OnNaviCallBack() {
            @Override
            public void onSuccess() {
                //导航成功
                mView.reachBack(guides.get(curPos));
                RobotManager.speak("您好" + marker.getTitle() + "已经到了")
                        .done(new DoneCallback<Void>() {
                            @Override
                            public void onDone(Void aVoid) {
                                RobotManager.speak(guides.get(curPos).getIntroduce())
                                        .done(new DoneCallback<Void>() {
                                            @Override
                                            public void onDone(Void aVoid) {
                                                //延迟5秒继续导览
                                               mHandler.sendEmptyMessageDelayed(MSG_GUIDEPOINT, 5000);
                                            }
                                        })
                                        .fail(new FailCallback<SynthesisException>() {
                                            @Override
                                            public void onFail(SynthesisException e) {
                                                //延迟5秒继续导览
                                                mHandler.sendEmptyMessageDelayed(MSG_GUIDEPOINT, 5000);
                                            }
                                        });
                                }
                            })
                        .fail(new FailCallback<SynthesisException>() {
                            @Override
                            public void onFail(SynthesisException e) {
                                RobotManager.speak(guides.get(curPos).getIntroduce())
                                        .done(new DoneCallback<Void>() {
                                            @Override
                                            public void onDone(Void aVoid) {
                                                //延迟5秒继续导览
                                                mHandler.sendEmptyMessageDelayed(MSG_GUIDEPOINT, 5000);
                                            }
                                        })
                                        .fail(new FailCallback<SynthesisException>() {
                                            @Override
                                            public void onFail(SynthesisException e) {
                                                //延迟5秒继续导览
                                                mHandler.sendEmptyMessageDelayed(MSG_GUIDEPOINT, 5000);
                                            }
                                        });
                            }
                        });
            }

            @Override
            public void onFailed() {
                //导航失败
                RobotManager.speak("导航异常，请先对我进行定位")
                    .done(new DoneCallback<Void>() {
                        @Override
                        public void onDone(Void aVoid) {
                            finishActivityAndAskGoWelcome();
                        }
                    })
                    .fail(new FailCallback<SynthesisException>() {
                        @Override
                        public void onFail(SynthesisException e) {
                            finishActivityAndAskGoWelcome();
                        }
                    });
            }
        });
    }

    /**
     * 停止导航,退出界面
     */
    public void stopNaviRoute(){
        GuideActivity.naviState = NavigationStateConstant.STATE_PAUSE;
        mHandler.removeMessages(MSG_GUIDEPOINT);
        RobotManager.stopNavigation();
        RobotManager.speak("停止导航成功");
        finishActivityAndAskGoWelcome();
    }

    /**
     * 暂停导览
     */
    public void pauseNaviRoute(){
        GuideActivity.naviState = NavigationStateConstant.STATE_PAUSE;
        mHandler.removeMessages(MSG_GUIDEPOINT);
        RobotManager.stopNavigation();
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
        //通知UI改变
        mView.notifyGuideUi(point);
        RobotManager.speak("我将要带您前往" + point + "，请跟紧我哦")
            .done(new DoneCallback<Void>() {
                @Override
                public void onDone(Void aVoid) {
                    RobotManager.getMarkerByName(point, new OnMarkerCallBack() {
                        @Override
                        public void onSucess(Marker marker) {
                            RobotManager.startNavigation(marker, new OnNaviCallBack() {
                                @Override
                                public void onSuccess() {
                                    //导航成功
                                    if (pointGuide != null){
                                        mView.reachBack(pointGuide);
                                    }
                                    RobotManager.speak("您好" + point + "已经到了")
                                        .done(new DoneCallback<Void>() {
                                            @Override
                                            public void onDone(Void aVoid) {
                                                RobotManager.speak(pointGuide.getIntroduce())
                                                        .done(new DoneCallback<Void>() {
                                                            @Override
                                                            public void onDone(Void aVoid) {
                                                                //关闭界面
                                                                finishActivityAndAskGoWelcome();
                                                            }
                                                        });
                                            }
                                        });
                                }

                                @Override
                                public void onFailed() {
                                    RobotManager.speak("导航异常，请先对我进行定位")
                                            .done(new DoneCallback<Void>() {
                                                @Override
                                                public void onDone(Void aVoid) {
                                                    finishActivityAndAskGoWelcome();
                                                }
                                            });
                                }
                            });
                        }

                        @Override
                        public void failed() {
                            finishActivity();
                        }

                        @Override
                        public void setMsg(String msg) {

                        }
                    });
                }
            })
            .fail(new FailCallback<SynthesisException>() {
                @Override
                public void onFail(SynthesisException e) {

                }
            });
    }

    /**
     * 关闭界面
     */
    private void finishActivity(){
        //关闭界面
        ((GuideActivity)mContext).finish();
    }

    /**
     * 结束界面，并且询问是否返回接待点
     */
    private void finishActivityAndAskGoWelcome(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RobotManager.speak("需要我返回接待点吗？")
                    .done(new DoneCallback<Void>() {
                        @Override
                        public void onDone(Void aVoid) {
                            //延迟3s等待用户是否需要返回接待点
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //判断是否需要返回接待点
                                    if (GuideActivity.isNeedGoHome){
                                        RobotManager.goPoint("接待点");
                                    }
                                    ((GuideActivity)mContext).finish();
                                }
                            }, 3000);
                        }
                    });
            }
        },2000);
    }
}
