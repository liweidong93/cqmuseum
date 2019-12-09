package com.cnki.cqmuseum.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.bean.AnswerBean;
import com.cnki.cqmuseum.constant.UrlConstant;
import com.cnki.cqmuseum.ui.chat.ChatPresenter;
import com.cnki.cqmuseum.utils.DisplayUtils;
import com.cnki.cqmuseum.utils.TextStyleUtils;
import com.cnki.cqmuseum.view.CollectionDialog;

import java.util.ArrayList;

/**
 * 熊猫知识库viewholder
 * Created by liweidong on 2019/12/9.
 */

public class PandaKnowledgeViewHolder extends BaseViewHolder<AnswerBean.AnswerItem>{

    private ImageView mImageViewPic;
    private TextView mTextViewName;
    private TextView mTextViewAttributes;
    private TextView mTextViewTime;
    private TextView mTextViewAddress;
    private TextView mTextViewBody;
    private TextView mTextViewFigure;
    private TextView mTextViewFossil;
    private TextView mTextViewSkull;
    private TextView mTextViewDigest;
    private TextView mTextViewIntroduce;

    public PandaKnowledgeViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    void initView(View itemView) {
        mImageViewPic = itemView.findViewById(R.id.iv_pandaknowledge_pic);
        mTextViewName = itemView.findViewById(R.id.tv_pandaknowledge_name);
        mTextViewAttributes = itemView.findViewById(R.id.tv_pandaknowledge_attributes);
        mTextViewTime = itemView.findViewById(R.id.tv_pandaknowledge_time);
        mTextViewAddress = itemView.findViewById(R.id.tv_pandaknowledge_address);
        mTextViewBody = itemView.findViewById(R.id.tv_pandaknowledge_bodytext);
        mTextViewFigure = itemView.findViewById(R.id.tv_pandaknowledge_figuretext);
        mTextViewFossil = itemView.findViewById(R.id.tv_pandaknowledge_fossiltext);
        mTextViewSkull = itemView.findViewById(R.id.tv_pandaknowledge_skulltext);
        mTextViewDigest = itemView.findViewById(R.id.tv_pandaknowledge_digestsystemtext);
        mTextViewIntroduce = itemView.findViewById(R.id.tv_pandaknowledge_introducetext);
    }

    @Override
    public void initData(final Context context, ChatPresenter presenter, RecyclerView.Adapter adapter, final ArrayList<AnswerBean.AnswerItem> datas, final int position) {
        if (datas.get(position) != null && datas.get(position).kNodeItems != null && datas.get(position).kNodeItems.size() != 0 && datas.get(position).kNodeItems.get(0).dataItems != null
                && datas.get(position).kNodeItems.get(0).dataItems.size() != 0 && datas.get(position).kNodeItems.get(0).dataItems.get(0).fieldValue != null){
            AnswerBean.AnswerItem.KNodeItem.DataItem.FieldValue fieldValue = datas.get(position).kNodeItems.get(0).dataItems.get(0).fieldValue;
            Glide.with(context).load(fieldValue.pic).thumbnail(0.1f).into(mImageViewPic);
            mTextViewName.setText(TextStyleUtils.replaceRedTag(fieldValue.kind));
            mTextViewAttributes.setText("属性：" + TextStyleUtils.isStrEmpty(fieldValue.attributes));
            mTextViewTime.setText("年代：" + TextStyleUtils.isStrEmpty(fieldValue.age));
            mTextViewAddress.setText("地址：" + TextStyleUtils.isStrEmpty(fieldValue.address));
            mTextViewBody.setText(TextStyleUtils.isStrEmpty(fieldValue.body));
            mTextViewFigure.setText(TextStyleUtils.isStrEmpty(fieldValue.figures));
            mTextViewFossil.setText(TextStyleUtils.isStrEmpty(fieldValue.fossil));
            mTextViewSkull.setText(TextStyleUtils.isStrEmpty(fieldValue.skull));
            mTextViewDigest.setText(TextStyleUtils.isStrEmpty(fieldValue.digestSystem));
            mTextViewIntroduce.setText(TextStyleUtils.isStrEmpty(fieldValue.introduce));
        }
    }
}
