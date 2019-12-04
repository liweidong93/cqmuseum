package com.cnki.cqmuseum.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.ui.collection.CollectionActivity;

/**
 * Created by liweidong on 2019/11/6.
 */

public class ColletionTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private String[] types;
    private OnItemClickListener onItemClickListener;

    public ColletionTypeAdapter(Context context,String[] types, OnItemClickListener onItemClickListener){
        this.mContext = context;
        this.types = types;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_collectiontype, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        ViewHolder mHolder = (ViewHolder) viewHolder;
        mHolder.mTextViewText.setText(types[i]);
        if (CollectionActivity.mSelectType.equals(types[i])){
            mHolder.mLinearLayoutRoot.setBackgroundResource(R.drawable.corner_24835b_10);
            mHolder.mTextViewText.setTextColor(mContext.getResources().getColor(R.color.white));
        }else{
            mHolder.mTextViewText.setTextColor(mContext.getResources().getColor(R.color.color_84C28C));
            mHolder.mLinearLayoutRoot.setBackgroundResource(R.drawable.corner_e7e7e7_10);
        }
        mHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CollectionActivity.mSelectType.equals(types[i])){
                    CollectionActivity.mSelectType = "";
                }else{
                    CollectionActivity.mSelectType = types[i];
                }
                notifyDataSetChanged();
                onItemClickListener.onItemClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return types.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout mLinearLayoutRoot;
        private TextView mTextViewText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLinearLayoutRoot = itemView.findViewById(R.id.ll_collectiontypeadapter_root);
            mTextViewText = itemView.findViewById(R.id.tv_collectiontypeadapter_text);
        }
    }

    public interface OnItemClickListener{
        void onItemClick();
    }
}
