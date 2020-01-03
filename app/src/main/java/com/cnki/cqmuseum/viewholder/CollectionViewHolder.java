package com.cnki.cqmuseum.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.bean.AnswerBean;
import com.cnki.cqmuseum.constant.UrlConstant;
import com.cnki.cqmuseum.ui.chat.ChatPresenter;
import com.cnki.cqmuseum.utils.DisplayUtils;
import com.cnki.cqmuseum.utils.GlideUtils;
import com.cnki.cqmuseum.utils.HtmlUtils;
import com.cnki.cqmuseum.utils.TextStyleUtils;
import com.cnki.cqmuseum.view.CollectionDialog;

import java.util.ArrayList;

/**
 * 精品文物的viewholder
 * Created by admin on 2019/6/5.
 */

public class CollectionViewHolder extends BaseViewHolder<AnswerBean.AnswerItem>{

    private ImageView mImageViewPic;
    private TextView mTextViewName;
    private TextView mTextViewType;
    private TextView mTextViewTime;
    private TextView mTextViewLocation;
    private TextView mTextViewArea;
    private TextView mTextViewFeed;
    private TextView mTextViewBody;
    private TextView mTextViewIntroduce;
    private TextView mTextViewMore;

    public CollectionViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    void initView(View itemView) {
        mImageViewPic = itemView.findViewById(R.id.iv_chatcollection_pic);
        mTextViewName = itemView.findViewById(R.id.tv_chatcollection_name);
        mTextViewType = itemView.findViewById(R.id.tv_chatcollection_type);
        mTextViewTime = itemView.findViewById(R.id.tv_chatcollection_time);
        mTextViewLocation = itemView.findViewById(R.id.tv_chatcollection_location);
        mTextViewArea = itemView.findViewById(R.id.tv_chatcollection_inarea);
        mTextViewFeed = itemView.findViewById(R.id.tv_chatcollection_feed);
        mTextViewBody = itemView.findViewById(R.id.tv_chatcollection_body);
        mTextViewIntroduce = itemView.findViewById(R.id.tv_chatcollection_introduce);
        mTextViewMore = itemView.findViewById(R.id.tv_chatcollection_more);
    }

    @Override
    public void initData(final Context context, ChatPresenter presenter, RecyclerView.Adapter adapter, final ArrayList<AnswerBean.AnswerItem> datas, final int position) {
        if (datas.get(position) != null && datas.get(position).kNodeItems != null && datas.get(position).kNodeItems.size() != 0
                && datas.get(position).kNodeItems.get(0) != null && datas.get(position).kNodeItems.get(0).dataItems != null
                && datas.get(position).kNodeItems.get(0).dataItems.size() != 0 && datas.get(position).kNodeItems.get(0).dataItems.get(0).fieldValue != null){
            AnswerBean.AnswerItem.KNodeItem.DataItem.FieldValue fieldValue = datas.get(position).kNodeItems.get(0).dataItems.get(0).fieldValue;
            GlideUtils.loadPic(context, UrlConstant.URL_CNKI_PIC + fieldValue.pic, mImageViewPic);
            mTextViewName.setText(TextStyleUtils.replaceRedTag(fieldValue.name));
            if (TextUtils.isEmpty(fieldValue.type)){
                mTextViewType.setText("类别：--");
            }else{
                mTextViewType.setText("类别：" + TextStyleUtils.replaceRedTag(fieldValue.type));
            }
            if (TextUtils.isEmpty(fieldValue.time)){
                mTextViewTime.setText("地质年代：--");
            }else{
                mTextViewTime.setText("地质年代：" + fieldValue.time);
            }
            if (TextUtils.isEmpty(fieldValue.location)){
                mTextViewLocation.setText("产地：--");
            }else{
                mTextViewLocation.setText("产地：" + fieldValue.location);
            }
            if (TextUtils.isEmpty(fieldValue.inarea)){
                mTextViewArea.setText("--");
            }else{
                mTextViewArea.setText(fieldValue.inarea);
            }
            if (TextUtils.isEmpty(fieldValue.feed)){
                mTextViewFeed.setText("食性：--");
            }else{
                mTextViewFeed.setText("食性：" + fieldValue.feed);
            }
            if (TextUtils.isEmpty(fieldValue.bodylength)){
                mTextViewBody.setText("体长：--");
            }else{
                mTextViewBody.setText("体长：" + fieldValue.bodylength);
            }
            mTextViewIntroduce.setText(fieldValue.introduce == null ? "--" : fieldValue.introduce);
            //显示更多
            if (datas.get(position).kNodeItems.size() > 1){
                mTextViewMore.setVisibility(View.VISIBLE);
                mTextViewMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new CollectionDialog(context, datas.get(position)).show();
                    }
                });
                //设置名字margin
                DisplayUtils.setMargins(mTextViewName,0,0,170,0);
            }else{
                mTextViewMore.setVisibility(View.GONE);
                mTextViewMore.setOnClickListener(null);
                DisplayUtils.setMargins(mTextViewName,0,0,0,0);
            }
        }
    }
}
