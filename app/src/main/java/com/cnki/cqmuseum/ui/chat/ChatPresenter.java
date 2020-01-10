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
     * 发送问题
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

}
