package com.cnki.cqmuseum.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.bean.PandaBean;
import com.cnki.cqmuseum.manager.RobotManager;
import com.cnki.cqmuseum.ui.chat.ChatPresenter;

import java.util.ArrayList;

/**
 * Created by liweidong on 2019/8/22.
 */

public class PandaQuestionTwoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<PandaBean.SecondItem> secondItems;
    private Context mContext;
    private ChatPresenter mPresenter;

    public PandaQuestionTwoAdapter(Context context, ArrayList<PandaBean.SecondItem> secondItems, ChatPresenter presenter){
        this.mContext = context;
        this.secondItems = secondItems;
        this.mPresenter = presenter;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_pandatwo, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder mHolder = (ViewHolder) viewHolder;
        final PandaBean.SecondItem secondItem = secondItems.get(i);
        ArrayList<String> threeNames = secondItem.threeNames;
        if (threeNames != null && threeNames.size() != 0){
            if (secondItem.isOpen){
                mHolder.mImageViewTwoState.setImageResource(R.mipmap.blue_open);
                mHolder.mRecyclerViewTwoData.setVisibility(View.VISIBLE);
            }else{
                mHolder.mImageViewTwoState.setImageResource(R.mipmap.blue_close);
                mHolder.mRecyclerViewTwoData.setVisibility(View.GONE);
            }
        }else{
            if (secondItem.isOpen){
                mHolder.mImageViewTwoState.setImageResource(R.mipmap.blue_open);
            }else{
                mHolder.mImageViewTwoState.setImageResource(R.mipmap.blue_close);
            }
            mHolder.mRecyclerViewTwoData.setVisibility(View.GONE);
        }
        mHolder.mLinearLayoutName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (secondItem.isOpen){
                    secondItem.isOpen = false;
                }else{
                    secondItem.isOpen = true;
                }
                notifyDataSetChanged();
                if (secondItem.threeNames != null && secondItem.threeNames.size() != 0){

                }else{
                    //直接问答
                    int pointIndex = secondItem.secondName.indexOf(".");
                    if (pointIndex != -1){
                        RobotManager.understantSpeak(mContext,secondItem.secondName.substring(pointIndex + 1));
                    }
                }

            }
        });
        mHolder.mTextViewTwoName.setText(secondItem.secondName);
        mHolder.mRecyclerViewTwoData.setLayoutManager(new LinearLayoutManager(mContext));
        PandaQuestionThreeAdapter mAdapter = new PandaQuestionThreeAdapter(mContext, secondItem.threeNames, mPresenter);
        mHolder.mRecyclerViewTwoData.setAdapter(mAdapter);
    }

    @Override
    public int getItemCount() {
        return secondItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private RecyclerView mRecyclerViewTwoData;
        private ImageView mImageViewTwoState;
        private LinearLayout mLinearLayoutName;
        private TextView mTextViewTwoName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mRecyclerViewTwoData = itemView.findViewById(R.id.rv_pandaadaptertwo_threedata);
            mImageViewTwoState = itemView.findViewById(R.id.iv_pandaadaptertwo_state);
            mTextViewTwoName = itemView.findViewById(R.id.tv_pandaadaptertwo_name);
            mLinearLayoutName = itemView.findViewById(R.id.ll_pandaadaptertwo_name);
        }

    }
}
