package com.cnki.cqmuseum.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.bean.SpeechServer;
import com.cnki.cqmuseum.constant.UrlConstant;

import java.util.ArrayList;

/**
 * Created by liweidong on 2019/11/7.
 */

public class SpeechServerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<SpeechServer> speechServers;

    public SpeechServerAdapter(Context context, ArrayList<SpeechServer> speechServers){
        this.mContext = context;
        this.speechServers = speechServers;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.adapter_speechserver, viewGroup, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder mHolder = (ViewHolder) viewHolder;
        SpeechServer speechServer = speechServers.get(position);
        if (TextUtils.isEmpty(speechServer.pic)){
            mHolder.mImageViewLeft.setVisibility(View.GONE);
            mHolder.mImageViewRight.setVisibility(View.GONE);
            mHolder.mTextViewText.setVisibility(View.VISIBLE);
            mHolder.mTextViewText.setText(speechServer.text);
        }else{
            if (position % 2 == 0){
                mHolder.mImageViewLeft.setVisibility(View.VISIBLE);
                mHolder.mImageViewRight.setVisibility(View.GONE);
                Glide.with(mContext).load(UrlConstant.URL_CNKI_PIC + speechServer.pic).thumbnail(0.1f).into(mHolder.mImageViewLeft);
            }else{
                mHolder.mImageViewLeft.setVisibility(View.GONE);
                mHolder.mImageViewRight.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(UrlConstant.URL_CNKI_PIC + speechServer.pic).thumbnail(0.1f).into(mHolder.mImageViewRight);
            }
            if (TextUtils.isEmpty(speechServer.text)){
                mHolder.mTextViewText.setVisibility(View.GONE);
            }else{
                mHolder.mTextViewText.setVisibility(View.VISIBLE);
                mHolder.mTextViewText.setText(speechServer.text);
            }
        }
    }

    @Override
    public int getItemCount() {
        return speechServers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView mImageViewLeft;
        private ImageView mImageViewRight;
        private TextView mTextViewText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageViewLeft = itemView.findViewById(R.id.iv_speechserveradapter_left);
            mImageViewRight = itemView.findViewById(R.id.iv_speechserveradapter_right);
            mTextViewText = itemView.findViewById(R.id.tv_speechserveradapter_text);
        }
    }
}
