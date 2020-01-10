package com.cnki.cqmuseum.ui.collection;


import android.animation.ObjectAnimator;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.base.BaseActivity;
import com.cnki.cqmuseum.bean.CollectionList;
import com.cnki.cqmuseum.constant.IntentActionConstant;
import com.cnki.cqmuseum.constant.UrlConstant;
import com.cnki.cqmuseum.manager.RobotManager;
import com.cnki.cqmuseum.utils.GlideUtils;
import com.cnki.cqmuseum.utils.Rotate3dAnimation;
import com.cnki.cqmuseum.view.MixtureTextView;

/**
 * 文物精品详情页面
 */
public class CollectDetailActivity extends BaseActivity<CollectionDetailPresenter> implements ICollectionDetailView {


    private CollectionDetailPresenter mPresenter;
    private MixtureTextView mMixtureTextView;
    private ImageView mImageViewBack;
    private ImageView mImageViewPic;
    private ScrollView mScrollViewContent;
    private TextView mTextViewName;
    private TextView mTextViewProduce;
    private TextView mTextViewTime;
    private TextView mTextViewInArea;
    private TextView mTextViewBody;
    private TextView mTextViewFeed;
    private TextView mTextViewMen;
    private TextView mTextViewGang;
    private TextView mTextViewKe;
    private TextView mTextViewMu;
    private TextView mTextViewShu;
    private TextView mTextViewIntroduce;
    private TextView mTextViewInAreaText;

    @Override
    public CollectionDetailPresenter initPresenter() {
        mPresenter = new CollectionDetailPresenter(this);
        return mPresenter;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_collect_detail;
    }

    @Override
    public void initView() {
        mImageViewBack = findViewById(R.id.iv_collectiondetail_back);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CollectDetailActivity.this.finish();
            }
        });
        mImageViewPic = findViewById(R.id.iv_collectiondetail_pic);
        //设置旋转动画
        mImageViewPic.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 获取布局的中心点位置，作为旋转的中心点
                float centerX = mImageViewPic.getWidth();
                float centerY = mImageViewPic.getHeight() / 2f;
                // 构建3D旋转动画对象，旋转角度为360到270度，这使得ImageView将会从可见变为不可见，并且旋转的方向是相反的
                final Rotate3dAnimation rotation = new Rotate3dAnimation(0, -5, centerX,
                        centerY, 0.0f, true);
                // 动画持续时间500毫秒
                rotation.setDuration(0);
                // 动画完成后保持完成的状态
                rotation.setFillAfter(true);
                rotation.setInterpolator(new AccelerateInterpolator());
                // 设置动画的监听器
//                rotation.setAnimationListener(new TurnToListView());
                mImageViewPic.startAnimation(rotation);

            }
        });
        mScrollViewContent = findViewById(R.id.sv_collectiondetail_content);
        mScrollViewContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 获取布局的中心点位置，作为旋转的中心点
                float centerX = 0;
                float centerY = mScrollViewContent.getHeight() / 2f;
                // 构建3D旋转动画对象，旋转角度为360到270度，这使得ImageView将会从可见变为不可见，并且旋转的方向是相反的
                final Rotate3dAnimation rotation = new Rotate3dAnimation(0, 5, centerX,
                        centerY, 0.0f, true);
                // 动画持续时间500毫秒
                rotation.setDuration(0);
                // 动画完成后保持完成的状态
                rotation.setFillAfter(true);
                rotation.setInterpolator(new AccelerateInterpolator());
                // 设置动画的监听器
//                rotation.setAnimationListener(new TurnToListView());
                mScrollViewContent.startAnimation(rotation);
            }
        });
        mTextViewName = findViewById(R.id.tv_collectiondetail_name);
        mTextViewProduce = findViewById(R.id.tv_collectiondetail_produce);
        mTextViewTime = findViewById(R.id.tv_collectiondetail_time);
        mTextViewInArea = findViewById(R.id.tv_collectiondetail_inarea);
        mTextViewBody = findViewById(R.id.tv_collectiondetail_body);
        mTextViewFeed = findViewById(R.id.tv_collectiondetail_feed);
        mTextViewMen = findViewById(R.id.tv_collectiondetail_men);
        mTextViewGang = findViewById(R.id.tv_collectiondetail_gang);
        mTextViewKe = findViewById(R.id.tv_collectiondetail_ke);
        mTextViewMu = findViewById(R.id.tv_collectiondetail_mu);
        mTextViewShu = findViewById(R.id.tv_collectiondetail_shu);
        mTextViewIntroduce = findViewById(R.id.tv_collectiondetail_introduce);
        mTextViewInAreaText = findViewById(R.id.tv_collectiondetail_inareatext);
    }

    @Override
    public void initData() {
        CollectionList.CollectionItem collectionItem = (CollectionList.CollectionItem) getIntent().getSerializableExtra(IntentActionConstant.ACTION_COLLECTION_DATA);
        //初始化数据
        if (collectionItem != null){
            mTextViewName.setText(collectionItem.name);
            GlideUtils.loadPic(mContext, UrlConstant.URL_CNKI_PIC + collectionItem.image, mImageViewPic);
            mTextViewIntroduce.setText(collectionItem.introduce);
            RobotManager.speak(collectionItem.introduce);
            if (!TextUtils.isEmpty(collectionItem.place)){
                mTextViewProduce.setText("产地：" + collectionItem.place);
            }else{
                mTextViewProduce.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(collectionItem.time)){
                mTextViewTime.setText("地质年代：" + collectionItem.time);
            }else{
                mTextViewTime.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(collectionItem.area)){
                mTextViewInArea.setText(collectionItem.area);
            }else{
                mTextViewInArea.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(collectionItem.area)){
                mTextViewInArea.setText("分布地区：" + collectionItem.area);
            }else{
                mTextViewInArea.setVisibility(View.GONE);
                mTextViewInAreaText.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(collectionItem.body)){
                mTextViewBody.setText("体长：" + collectionItem.body);
            }else{
                mTextViewBody.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(collectionItem.feed)){
                mTextViewFeed.setText("食性：" + collectionItem.feed);
            }else{
                mTextViewFeed.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(collectionItem.men)){
                mTextViewMen.setText("门：" + collectionItem.men);
            }else{
                mTextViewMen.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(collectionItem.gang)){
                mTextViewGang.setText("纲：" + collectionItem.gang);
            }else{
                mTextViewGang.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(collectionItem.ke)){
                mTextViewKe.setText("科：" + collectionItem.ke);
            }else{
                mTextViewKe.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(collectionItem.mu)){
                mTextViewMu.setText("目：" + collectionItem.mu);
            }else{
                mTextViewMu.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(collectionItem.shu)){
                mTextViewShu.setText("属：" + collectionItem.shu);
            }else{
                mTextViewShu.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        RobotManager.stopSpeak();
    }
}
