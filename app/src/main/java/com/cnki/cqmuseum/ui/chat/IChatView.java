package com.cnki.cqmuseum.ui.chat;

import com.cnki.cqmuseum.base.BaseView;
import com.cnki.cqmuseum.bean.AnswerBean;

/**
 * Created by liweidong on 2019/10/23.
 */

public interface IChatView extends BaseView{

    /**
     * 刷新适配器
     * @param question
     * @param answerItem
     */
    void notifyChatData(String question, AnswerBean.AnswerItem answerItem);
}
