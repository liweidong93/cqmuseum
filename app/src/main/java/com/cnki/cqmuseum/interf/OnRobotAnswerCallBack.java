package com.cnki.cqmuseum.interf;


import com.cnki.cqmuseum.bean.AnswerBean;

/**
 * 机器人答案回调
 * Created by admin on 2019/6/11.
 */

public interface OnRobotAnswerCallBack {

    /**
     * 成功回调
     * @param successAnswer
     */
    void onSuccess(AnswerBean.AnswerItem successAnswer);

    /**
     * 失败回调
     * @param failAnswer
     */
    void onFailed(AnswerBean.AnswerItem failAnswer);
}
