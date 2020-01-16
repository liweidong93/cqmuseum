package com.cnki.cqmuseum.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.manager.RobotManager;
import com.cnki.cqmuseum.ui.chat.ChatPresenter;

import java.util.ArrayList;

/**
 * Created by liweidong on 2019/8/22.
 */

public class PandaQuestionThreeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<String> threeNames;
    private ChatPresenter mPresenter;

    public PandaQuestionThreeAdapter(Context context, ArrayList<String> threeNames, ChatPresenter presenter){
        this.mContext = context;
        this.threeNames = threeNames;
        this.mPresenter = presenter;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_pandathree, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        ViewHolder mHolder = (ViewHolder) viewHolder;
        mHolder.mTextViewName.setText(threeNames.get(i));
        mHolder.mTextViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.clickSendQuestion(threeNames.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return threeNames.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView mTextViewName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewName = itemView.findViewById(R.id.tv_pandaadapterthree_name);
        }
    }
}
