package com.cnki.cqmuseum.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.bean.AnswerBean;
import com.cnki.cqmuseum.constant.UrlConstant;
import com.cnki.cqmuseum.manager.RobotManager;
import com.cnki.cqmuseum.utils.GlideUtils;
import com.cnki.cqmuseum.utils.TextStyleUtils;
import com.cnki.cqmuseum.view.CollectionDialog;

import java.util.ArrayList;

/**
 * 文物列表viewpager适配器
 * Created by liweidong on 2019/11/21.
 */

public class CollectionVPAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<AnswerBean.AnswerItem.KNodeItem> mKNodeItems;
    private ImageView mImageViewPic;
    private TextView mTextViewName;
    private TextView mTextViewType;
    private TextView mTextViewTime;
    private TextView mTextViewLocation;
    private TextView mTextViewArea;
    private TextView mTextViewFeed;
    private TextView mTextViewBody;
    private TextView mTextViewIntroduce;

    public CollectionVPAdapter(Context context,ArrayList<AnswerBean.AnswerItem.KNodeItem> kNodeItems){
        this.mContext = context;
        this.mKNodeItems = kNodeItems;
    }

    @Override
    public int getCount() {
        return mKNodeItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.adapter_vpcollection, null);
        initView(inflate);
        initData(position);
        container.addView(inflate);
        return inflate;
    }

    /**
     * 初始化view
     * @param itemView
     */
    private void initView(View itemView){
        mImageViewPic = itemView.findViewById(R.id.iv_chatcollection_pic);
        mTextViewName = itemView.findViewById(R.id.tv_chatcollection_name);
        mTextViewType = itemView.findViewById(R.id.tv_chatcollection_type);
        mTextViewTime = itemView.findViewById(R.id.tv_chatcollection_time);
        mTextViewLocation = itemView.findViewById(R.id.tv_chatcollection_location);
        mTextViewArea = itemView.findViewById(R.id.tv_chatcollection_inarea);
        mTextViewFeed = itemView.findViewById(R.id.tv_chatcollection_feed);
        mTextViewBody = itemView.findViewById(R.id.tv_chatcollection_body);
        mTextViewIntroduce = itemView.findViewById(R.id.tv_chatcollection_introduce);
    }

    /**
     * 初始化数据
     * @param position
     */
    private void initData(int position){
        AnswerBean.AnswerItem.KNodeItem.DataItem.FieldValue fieldValue = mKNodeItems.get(position).dataItems.get(0).fieldValue;
        GlideUtils.loadPic(mContext, UrlConstant.URL_CNKI_PIC + fieldValue.pic, mImageViewPic);
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
    }
}
