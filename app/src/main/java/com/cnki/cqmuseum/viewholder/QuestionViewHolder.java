package com.cnki.cqmuseum.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.bean.AnswerBean;
import com.cnki.cqmuseum.ui.chat.ChatPresenter;

import java.util.ArrayList;

/**
 * 问题的viewholder
 * Created by admin on 2019/6/5.
 */

public class QuestionViewHolder extends BaseViewHolder<AnswerBean.AnswerItem>{

    private TextView mTextViewQuestion;

    public QuestionViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    void initView(View itemView) {
        mTextViewQuestion = itemView.findViewById(R.id.tv_chatquestion_question);
    }

    @Override
    public void initData(Context context, ChatPresenter presenter, RecyclerView.Adapter adapter, ArrayList<AnswerBean.AnswerItem> datas, int position) {
        mTextViewQuestion.setText(datas.get(position).Question);
    }

}
