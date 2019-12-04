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
    public void sendQuestion(final String question){
        addDisposable(CnkiApi.getInstance().getCnkiAnswer(question), new BaseCnkiObserver() {

            private volatile boolean isCnkiFinish;
            private volatile AnswerBean.AnswerItem mRobotAnswerItem;
            @Override
            public void onSuccess(ArrayList<AnswerBean> answerBeans) {
                AnswerBean.AnswerItem answerItem = mModle.getViewType(answerBeans);
                answerItem.orignalQuestion = question;
                LogUtils.e("答案类型:" + answerItem.viewType);
                mView.notifyChatData(question, answerItem);
            }

            @Override
            protected void onStart() {
                super.onStart();
                //发送问题前先停止播报
                RobotManager.stopSpeech();
                //同时请求机器人答案
                isCnkiFinish = false;
                mRobotAnswerItem = null;
                askRobotQuestion(question, new OnRobotQuestionCallBack() {
                    @Override
                    public void onUpdateUI(String question, AnswerBean.AnswerItem answerItem) {
                        mRobotAnswerItem = answerItem;
                        updateRobotAndCnki(question, isCnkiFinish, mRobotAnswerItem);
                    }
                });
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onFailed() {
                super.onFailed();
                //获取机器人api答案
                LogUtils.e("知网获取失败   获取机器人答案");
                isCnkiFinish = true;
                updateRobotAndCnki(question, isCnkiFinish, mRobotAnswerItem);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                //获取机器人api答案
                LogUtils.e("知网获取失败   获取机器人答案");
                isCnkiFinish = true;
                updateRobotAndCnki(question, isCnkiFinish, mRobotAnswerItem);
            }
        });
    }

    /**
     * 更新cnki机器人答案
     * @param question
     * @param isCnkiFinish
     * @param mRobotAnswerItem
     */
    private void updateRobotAndCnki(String question, boolean isCnkiFinish, AnswerBean.AnswerItem mRobotAnswerItem){
        if (isCnkiFinish && mRobotAnswerItem != null){
            mView.notifyChatData(question, mRobotAnswerItem);
        }
    }


    /**
     * 机器人提问
     * @param question
     */
    public void askRobotQuestion(final String question, final OnRobotQuestionCallBack callBack){
        RobotManager.askQuestionToRobot(mContext, question, new OnRobotAnswerCallBack() {
            @Override
            public void onSuccess(AnswerBean.AnswerItem successAnswer) {
                callBack.onUpdateUI(question, successAnswer);
            }

            @Override
            public void onFailed(AnswerBean.AnswerItem failAnswer) {
                callBack.onUpdateUI(question, failAnswer);
            }
        });
    }

    /**
     * 播报答案
     * @param answerItem
     */
    public void speechResult(AnswerBean.AnswerItem answerItem){
        switch (answerItem.viewType){
            case ChatViewTypeConstant.VIEWTYPE_COLLECTION:
                RobotManager.stopSpeech();
                break;
            default:
                //默认类别，去掉html节点
                if (!TextUtils.isEmpty(answerItem.Answer)){
                    String answer = answerItem.Answer;
                    answer = HtmlUtils.removeHtml(answer);
                    RobotManager.speechVoice(answer);
                }
                break;
        }
    }

    public interface OnRobotQuestionCallBack{
        void onUpdateUI(String question, AnswerBean.AnswerItem successAnswer);
    }
}
