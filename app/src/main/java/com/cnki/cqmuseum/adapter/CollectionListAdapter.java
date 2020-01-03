package com.cnki.cqmuseum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.bean.CollectionList;
import com.cnki.cqmuseum.constant.UrlConstant;
import com.cnki.cqmuseum.utils.GlideUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

/**
 * 加載更多上啦刷新適配器
 * Created by admin on 2019/6/10.
 */

public class CollectionListAdapter extends RecyclerArrayAdapter<CollectionList.CollectionItem> {

    private Context mContext;

    public CollectionListAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public int getViewType(int position) {
        return position;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_collection,parent,false));
    }

    class ViewHolder extends BaseViewHolder<CollectionList.CollectionItem>{

        private ImageView mImageViewPic;
        private TextView mTextViewName;
        private TextView mTextViewIntroduce;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageViewPic = itemView.findViewById(R.id.iv_collectionadapter_pic);
            mTextViewName = itemView.findViewById(R.id.tv_collectionadapter_name);
            mTextViewIntroduce = itemView.findViewById(R.id.tv_collectionadapter_introduce);
        }

        @Override
        public void setData(CollectionList.CollectionItem data) {
            super.setData(data);
            mTextViewName.setText(data.name);
            mTextViewIntroduce.setText(data.introduce);
            GlideUtils.loadPic(mContext, UrlConstant.URL_CNKI_PIC + data.image, mImageViewPic);
        }
    }
}
