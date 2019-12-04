package com.cnki.cqmuseum.viewholder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.adapter.SuggestionAdapter;
import com.cnki.cqmuseum.bean.AnswerBean;
import com.cnki.cqmuseum.ui.chat.ChatPresenter;
import com.cnki.cqmuseum.utils.HtmlUtils;

import java.util.ArrayList;

/**
 * 默认答案的viewholder
 * Created by admin on 2019/6/5.
 */

public class PandaViewHolder extends BaseViewHolder<AnswerBean.AnswerItem>{

    private TextView mTextViewDefault;
    private LinearLayout mLinearLayoutSuggestion;
    private RecyclerView mRecyclerViewSuggestionList;

    public PandaViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    void initView(View itemView) {
        mTextViewDefault = itemView.findViewById(R.id.tv_chatpanda_answer);
        mLinearLayoutSuggestion = itemView.findViewById(R.id.ll_chatpanda_suggestion);
        mRecyclerViewSuggestionList = itemView.findViewById(R.id.rv_chatpanda_suggestionlist);
    }

    @Override
    public void initData(Context context, ChatPresenter presenter, RecyclerView.Adapter adapter, ArrayList<AnswerBean.AnswerItem> datas, int position) {
        //如果大于一行的话就padding改变
//        if (datas.get(position).Answer.length() > 30){
//            mTextViewDefault.setPadding(75,42,40,49);
//        }else{
//            mTextViewDefault.setPadding(75,16,40,15);
//        }
        HtmlUtils.setHtmlToTextView(context, mTextViewDefault, datas.get(position).Answer);
        if (datas.get(position).extra != null && !TextUtils.isEmpty(datas.get(position).extra.fromText)){
            mLinearLayoutSuggestion.setVisibility(View.VISIBLE);
            String fromText = datas.get(position).extra.fromText;
            //推荐问题
            String[] suggestions = fromText.split(";");
            SuggestionAdapter suggestionAdapter = new SuggestionAdapter(context, suggestions, presenter);
            mRecyclerViewSuggestionList.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerViewSuggestionList.setAdapter(suggestionAdapter);
        }else{
            mLinearLayoutSuggestion.setVisibility(View.GONE);
        }
    }
}

