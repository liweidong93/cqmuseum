package com.cnki.cqmuseum.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.bean.PandaBean;
import com.cnki.cqmuseum.ui.chat.ChatPresenter;

import java.util.ArrayList;

/**
 * Created by liweidong on 2019/8/22.
 */

public class PandaQuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<PandaBean> pandaBeans;
    private Context mContext;
    private ChatPresenter mPresenter;

    public PandaQuestionAdapter(Context context, ArrayList<PandaBean> pandaBeans, ChatPresenter mPresenter){
        this.mContext = context;
        this.pandaBeans = pandaBeans;
        this.mPresenter = mPresenter;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_pandaone, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        final ViewHolder mHolder = (ViewHolder) viewHolder;
        final PandaBean pandaBean = pandaBeans.get(i);
        ArrayList<PandaBean.SecondItem> secondItems = pandaBean.secondItems;
        if (secondItems != null && secondItems.size() != 0){
            if (pandaBean.isOpen){
                mHolder.mImageViewState.setImageResource(R.mipmap.white_open);
                mHolder.mRecyclerViewData.setVisibility(View.VISIBLE);
            }else{
                mHolder.mImageViewState.setImageResource(R.mipmap.white_close);
                mHolder.mRecyclerViewData.setVisibility(View.GONE);
            }
        }else{
            if (pandaBean.isOpen){
                mHolder.mImageViewState.setImageResource(R.mipmap.white_open);
            }else{
                mHolder.mImageViewState.setImageResource(R.mipmap.white_close);
            }
            mHolder.mRecyclerViewData.setVisibility(View.GONE);
        }
        mHolder.mRelativeLayoutName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pandaBean.isOpen){
                    pandaBean.isOpen = false;
                }else{
                    pandaBean.isOpen = true;
                }
                notifyItemChanged(i);
            }
        });
        mHolder.mTextViewName.setText(pandaBean.oneName);
        PandaQuestionTwoAdapter mTwoAdapter = new PandaQuestionTwoAdapter(mContext, pandaBeans.get(i).secondItems, mPresenter);
        mHolder.mRecyclerViewData.setLayoutManager(new LinearLayoutManager(mContext));
        mHolder.mRecyclerViewData.setAdapter(mTwoAdapter);
    }

    @Override
    public int getItemCount() {
        return pandaBeans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout mRelativeLayoutName;
        private TextView mTextViewName;
        private RecyclerView mRecyclerViewData;
        private ImageView mImageViewState;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mRelativeLayoutName = itemView.findViewById(R.id.rl_pandaadapterone_name);
            mTextViewName = itemView.findViewById(R.id.tv_pandaadapterone_onename);
            mRecyclerViewData = itemView.findViewById(R.id.rv_pandaadapterone_twodata);
            mImageViewState = itemView.findViewById(R.id.iv_pandaadapterone_state);
        }
    }
}
