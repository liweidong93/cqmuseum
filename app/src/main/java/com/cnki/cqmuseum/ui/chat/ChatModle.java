package com.cnki.cqmuseum.ui.chat;

import com.cnki.cqmuseum.bean.AnswerBean;
import com.cnki.cqmuseum.constant.ChatViewTypeConstant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by liweidong on 2019/10/23.
 */

public class ChatModle {

    //答案优先级排序器
    private class AnswerComparatpor implements Comparator<AnswerBean> {

        @Override
        public int compare(AnswerBean lhs, AnswerBean rhs) { //lhs 后面的数据  rhs 前面的数据
            return lhs.DataType - rhs.DataType;
        }
    }

    /**
     * 获取答案分类的item,并且设置viewtype
     * @param answerBeans
     * @return
     */
    public AnswerBean.AnswerItem getViewType(ArrayList<AnswerBean> answerBeans){
        Collections.sort(answerBeans, new AnswerComparatpor());//根据优先级表进行排序
        AnswerBean.AnswerItem answerItem = answerBeans.get(0).Data;
        if (answerItem != null && answerItem.Domain.equals("熊猫")){
            if (answerItem.intent_domain.equals("精品文物")){
                answerItem.viewType = ChatViewTypeConstant.VIEWTYPE_COLLECTION;
            }else{
                answerItem.viewType = ChatViewTypeConstant.VIEWTYPE_DEFAULT;
            }
        }else if (answerItem.Domain.equals("熊猫时代业务问答")){
            answerItem.viewType = ChatViewTypeConstant.VIEWTYPE_PANDA;
        }else{
            answerItem.viewType = ChatViewTypeConstant.VIEWTYPE_DEFAULT;
        }
        return answerItem;
    }

}
