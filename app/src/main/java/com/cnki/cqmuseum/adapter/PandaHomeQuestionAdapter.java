package com.cnki.cqmuseum.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.utils.TextStyleUtils;

import java.util.ArrayList;

public class PandaHomeQuestionAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<String> mDatas;
    private boolean isChange;//数据位置是否改变
    private OnQuestionItemClickListener mClickListener;


    public PandaHomeQuestionAdapter(Context context, ArrayList<String> data, OnQuestionItemClickListener listener) {
        this.mContext = context;
        this.mDatas = data;
        this.mClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_pandahome_question, viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        ViewHolder mHolder = (ViewHolder) viewHolder;
        mHolder.mTextViewItem.setText(mDatas.get(i));
        if(isChange) {
            if(i == 2) {
                mHolder.mTextViewItem.setTextSize(TextStyleUtils.px2dip(mContext, 44));
                mHolder.mTextViewItem.setTextColor(mContext.getResources().getColor(R.color.color_059B76));
            } else if(i == 0){
                mHolder.mTextViewItem.setTextColor(mContext.getResources().getColor(R.color.color_40059B76));
            }else if(i == 1){
                mHolder.mTextViewItem.setTextSize(TextStyleUtils.px2dip(mContext, 32));
                mHolder.mTextViewItem.setTextColor(mContext.getResources().getColor(R.color.color_85059B76));
            }else if(i == 3){
                mHolder.mTextViewItem.setTextColor(mContext.getResources().getColor(R.color.color_85059B76));
            }else if(i == 4){
                mHolder.mTextViewItem.setTextColor(mContext.getResources().getColor(R.color.color_65059B76));
            }
        } else {
            if (i == 1) {
                mHolder.mTextViewItem.setTextSize(TextStyleUtils.px2dip(mContext, 44));
                mHolder.mTextViewItem.setTextColor(mContext.getResources().getColor(R.color.color_059B76));
            } else if (i == 0 || i == 2) {
                mHolder.mTextViewItem.setTextColor(mContext.getResources().getColor(R.color.color_85059B76));
            } else if (i == 3) {
                mHolder.mTextViewItem.setTextColor(mContext.getResources().getColor(R.color.color_65059B76));
            } else if (i == 4) {
                mHolder.mTextViewItem.setTextColor(mContext.getResources().getColor(R.color.color_40059B76));
            }
        }
        mHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onClick(mDatas.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewItem = itemView.findViewById(R.id.tv_homequestion);
        }
    }


    public interface OnQuestionItemClickListener {
        void onClick(String question);
    }

    public void changeData() {
        if(mDatas != null&&isChange) {
            String arg0 = mDatas.get(0);
            String arg1 = mDatas.get(1);
            String arg2 = mDatas.get(2);
            String arg3 = mDatas.get(3);
            String arg4 = mDatas.get(4);
            mDatas.clear();
            mDatas.add(arg1);
            mDatas.add(arg2);
            mDatas.add(arg3);
            mDatas.add(arg4);
            mDatas.add(arg0);
        }
    }

    /**
     * 替换集合数据
     * @param datas
     */
    public void updateData(ArrayList<String> datas){
        synchronized (mDatas){
            mDatas.clear();
            mDatas.addAll(datas);
            isChange = false;
            notifyDataSetChanged();
        }
    }

    public void isChange(boolean isChange) {
        this.isChange = isChange;
    }


}
