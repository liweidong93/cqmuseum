package com.cnki.cqmuseum.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.cnki.cqmuseum.bean.AnswerBean;
import com.cnki.cqmuseum.ui.chat.ChatPresenter;
import com.cnki.cqmuseum.utils.ChatAdapterUtils;
import com.cnki.cqmuseum.viewholder.BaseViewHolder;

import java.util.ArrayList;

/**
 * 会话适配器
 * Created by admin on 2019/6/5.
 */

public class ChatAdapter extends RecyclerView.Adapter<BaseViewHolder>{

    private Context mContext;
    private ArrayList<AnswerBean.AnswerItem> mAnswerItems;
    private ChatPresenter mPresenter;

    public ChatAdapter(Context context, ChatPresenter presenter, ArrayList<AnswerBean.AnswerItem> answerItems){
        this.mContext = context;
        this.mAnswerItems = answerItems;
        this.mPresenter = presenter;
    }

    @Override
    public int getItemViewType(int position) {
        return mAnswerItems.get(position).viewType;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ChatAdapterUtils.onCreateViewHolder(mContext, parent, viewType);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        ChatAdapterUtils.onBindViewHolder(this, mPresenter, mContext,mAnswerItems, holder, position);
    }

    @Override
    public int getItemCount() {
        return mAnswerItems.size();
    }
}
