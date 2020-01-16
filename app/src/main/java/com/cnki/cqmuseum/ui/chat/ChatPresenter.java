package com.cnki.cqmuseum.ui.chat;

import android.os.Handler;
import android.text.TextUtils;

import com.cnki.cqmuseum.base.BasePresenterImpl;
import com.cnki.cqmuseum.bean.AnswerBean;
import com.cnki.cqmuseum.constant.ChatViewTypeConstant;
import com.cnki.cqmuseum.interf.OnRobotAnswerCallBack;
import com.cnki.cqmuseum.manager.RobotManager;
import com.cnki.cqmuseum.server.BaseCnkiObserver;
import com.cnki.cqmuseum.server.api.CnkiApi;
import com.cnki.cqmuseum.utils.HtmlUtils;
import com.cnki.cqmuseum.utils.LogUtils;
import com.cnki.cqmuseum.view.GuideDialog;
import com.ubtrobot.async.DoneCallback;
import com.ubtrobot.async.FailCallback;
import com.ubtrobot.speech.UnderstandingException;
import com.ubtrobot.speech.UnderstandingResult;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * 聊天页P层
 * 处理对话逻辑
 * Created by liweidong on 2019/10/23.
 */

public class ChatPresenter extends BasePresenterImpl<IChatView> {

    private ChatModle mModle;

    public ChatPresenter(IChatView view) {
        super(view);
        mModle = new ChatModle();
    }

    /**
     * 正常语音提问发送问题
     * @param question
     */
    public void sendQuestion(final String question, final String robotMsg){
        addDisposable(CnkiApi.getInstance().getCnkiAnswer(question), new BaseCnkiObserver() {

            @Override
            public void onSuccess(ArrayList<AnswerBean> answerBeans) {
                AnswerBean.AnswerItem answerItem = mModle.getViewType(answerBeans, false);
                answerItem.orignalQuestion = question;
                LogUtils.e("答案类型:" + answerItem.viewType);
                mView.notifyChatData(question, answerItem);
            }

            @Override
            protected void onStart() {
                super.onStart();
                //发送问题前先停止播报
                RobotManager.stopSpeak();
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onFailed() {
                super.onFailed();
                //获取机器人api答案
                LogUtils.e("知网获取失败   获取机器人答案");
                handRobotAnswer(question, robotMsg);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                //获取机器人api答案
                LogUtils.e("知网获取失败   获取机器人答案");
                handRobotAnswer(question, robotMsg);
            }
        });
    }

    /**
     * 处理机器人回答
     * @param question
     * @param robotMsg
     */
    private void handRobotAnswer(String question, String robotMsg){
        //设置类型为闲聊通用
        AnswerBean.AnswerItem answerItem = new AnswerBean.AnswerItem();
        answerItem.viewType = ChatViewTypeConstant.VIEWTYPE_ROBOT;
        answerItem.Question = question;
        answerItem.orignalQuestion = question;
        if (TextUtils.isEmpty(robotMsg)){
            answerItem.Answer = "我正在学习中，稍后博物馆老师会为您解答！";
        }else{
            answerItem.Answer = robotMsg;
        }
        mView.notifyChatData(question, answerItem);
    }

    /**
     * 播报答案
     * @param answerItem
     */
    public void speechResult(AnswerBean.AnswerItem answerItem){
        switch (answerItem.viewType){
            case ChatViewTypeConstant.VIEWTYPE_COLLECTION:
                RobotManager.stopSpeak();
                break;
            case ChatViewTypeConstant.VIEWTYPE_PANDA_KNOWLWDGE:
                if (answerItem.kNodeItems != null && answerItem.kNodeItems.size() != 0 && answerItem.kNodeItems.get(0).dataItems != null
                        && answerItem.kNodeItems.get(0).dataItems.size() != 0 && answerItem.kNodeItems.get(0).dataItems.get(0).fieldValue != null){
                    RobotManager.stopSpeak();
                    RobotManager.speak(answerItem.kNodeItems.get(0).dataItems.get(0).fieldValue.introduce);
                }
                break;
            default:
                //默认类别，去掉html节点
                if (!TextUtils.isEmpty(answerItem.Answer)){
                    String answer = answerItem.Answer;
                    answer = HtmlUtils.removeHtml(answer);
                    RobotManager.speak(answer);
                }
                break;
        }
    }

    /**
     * 手动点击发送问题
     * @param question
     */
    public void clickSendQuestion(final String question){
        addDisposable(CnkiApi.getInstance().getCnkiAnswer(question), new BaseCnkiObserver() {

            @Override
            public void onSuccess(ArrayList<AnswerBean> answerBeans) {
                AnswerBean.AnswerItem answerItem = mModle.getViewType(answerBeans, false);
                answerItem.orignalQuestion = question;
                mView.notifyChatData(question, answerItem);
            }

            @Override
            protected void onStart() {
                super.onStart();
                //发送问题前先停止播报
                RobotManager.stopSpeak();
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onFailed() {
                super.onFailed();
                //获取机器人api答案
                getClickRobotAnswer(question);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                //获取机器人api答案
                getClickRobotAnswer(question);
            }
        });
    }

    /**
     * 获取机器人答案
     * @param question
     */
    public void getClickRobotAnswer(final String question){
        RobotManager.understantSpeakByClick(question)
                .done(new DoneCallback<UnderstandingResult>() {
                    @Override
                    public void onDone(UnderstandingResult understandingResult) {
                        LogUtils.e("robot","机器人理解结果：" + understandingResult.getSpeechFulfillment().getText());
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
                        //设置类型为闲聊通用
                        AnswerBean.AnswerItem answerItem = new AnswerBean.AnswerItem();
                        answerItem.viewType = ChatViewTypeConstant.VIEWTYPE_ROBOT;
                        answerItem.Question = question;
                        answerItem.orignalQuestion = question;
                        if (TextUtils.isEmpty(robotMsg)){
                            answerItem.Answer = "我正在学习中，稍后博物馆老师会为您解答！";
                        }else{
                            answerItem.Answer = robotMsg;
                        }
                        if (mView != null){
                            mView.notifyChatData(question, answerItem);
                        }
                    }
                })
                .fail(new FailCallback<UnderstandingException>() {
                    @Override
                    public void onFail(UnderstandingException e) {
                        //设置类型为闲聊通用
                        AnswerBean.AnswerItem answerItem = new AnswerBean.AnswerItem();
                        answerItem.viewType = ChatViewTypeConstant.VIEWTYPE_ROBOT;
                        answerItem.Question = question;
                        answerItem.orignalQuestion = question;
                        answerItem.Answer = "我正在学习中，稍后博物馆老师会为您解答！";
                        if (mView != null){
                            mView.notifyChatData(question, answerItem);
                        }
                    }
                });
    }

}
