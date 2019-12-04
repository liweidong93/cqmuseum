package com.cnki.cqmuseum.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.adapter.ChatAdapter;
import com.cnki.cqmuseum.bean.AnswerBean;
import com.cnki.cqmuseum.constant.ChatViewTypeConstant;
import com.cnki.cqmuseum.ui.chat.ChatPresenter;
import com.cnki.cqmuseum.viewholder.BaseViewHolder;
import com.cnki.cqmuseum.viewholder.CollectionViewHolder;
import com.cnki.cqmuseum.viewholder.DefaultViewHolder;
import com.cnki.cqmuseum.viewholder.PandaViewHolder;
import com.cnki.cqmuseum.viewholder.QuestionViewHolder;

import java.util.ArrayList;

/**
 * 会话适配器工具类
 * Created by admin on 2019/6/5.
 */

public class ChatAdapterUtils {

    /**
     * 给聊天适配器根据viewtype生成chat
     * @param context
     * @param parent
     * @param viewType
     * @return
     */
    public static BaseViewHolder onCreateViewHolder(Context context, ViewGroup parent, int viewType){
        //根据不同的viewtype来选用不同的item布局
        switch (viewType){
            case ChatViewTypeConstant.VIEWTYPE_QUESTION://问题的布局
                return new QuestionViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_chat_questionitem, parent, false));
            case ChatViewTypeConstant.VIEWTYPE_DEFAULT://默认答案布局
                return new DefaultViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_chat_defaultitem, parent, false));
            case ChatViewTypeConstant.VIEWTYPE_COLLECTION://精品文物
                return new CollectionViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_chat_collectionitem, parent, false));
            case ChatViewTypeConstant.VIEWTYPE_PANDA:
                return new PandaViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_chatpanda, parent, false));
            default://默认答案布局
                return new DefaultViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_chat_defaultitem, parent, false));
        }
    }

    /**
     * 根据viewtype绑定view
     * @param chatAdapter
     * @param context
     * @param answerItems
     * @param holder
     * @param position
     */
    public static void onBindViewHolder(final ChatAdapter chatAdapter, ChatPresenter mPresenter, Context context, final ArrayList<AnswerBean.AnswerItem> answerItems, BaseViewHolder holder, final int position){
        holder.initData(context, mPresenter, chatAdapter, answerItems, position);
    }
}
