package com.cnki.cqmuseum.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.ui.chat.ChatPresenter;

/**
 * 推荐问题适配器
 * Created by liweidong on 2019/11/13.
 */

public class SuggestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private String[] mSuggestions;
    private ChatPresenter mPresenter;

    public SuggestionAdapter(Context context, String[] suggestionList, ChatPresenter presenter){
        this.mContext = context;
        this.mSuggestions = suggestionList;
        this.mPresenter = presenter;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_suggestion, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        ViewHolder mHolder = (ViewHolder) viewHolder;
        mHolder.mTextViewQuestion.setText((i+1) + "." + mSuggestions[i]);
        mHolder.mTextViewQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.sendQuestion(mSuggestions[i]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSuggestions.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView mTextViewQuestion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewQuestion = itemView.findViewById(R.id.tv_suggestionadapter_question);
        }
    }
}
