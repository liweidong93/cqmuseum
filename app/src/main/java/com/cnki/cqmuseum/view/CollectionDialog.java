package com.cnki.cqmuseum.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.adapter.CollectionVPAdapter;
import com.cnki.cqmuseum.bean.AnswerBean;
import com.cnki.cqmuseum.constant.UrlConstant;
import com.cnki.cqmuseum.manager.RobotManager;
import com.cnki.cqmuseum.ui.chat.ChatActivity;
import com.cnki.cqmuseum.utils.LogUtils;
import com.cnki.cqmuseum.utils.TextStyleUtils;

/**
 * 精品文物的dialog
 * Created by admin on 2019/6/10.
 */

public class CollectionDialog extends Dialog {

    private Context mContext;
    private boolean originalListen;
    private AnswerBean.AnswerItem answerItem;
    private ImageView mImageViewClose;
    private ViewPager mViewPager;

    public CollectionDialog(@NonNull Context context, AnswerBean.AnswerItem answerItem) {
        super(context);
        this.mContext = context;
        this.answerItem = answerItem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);//加了才有圆角
        super.onCreate(savedInstanceState);
        originalListen = RobotManager.isListen;
        if (originalListen){
            RobotManager.isListen = false;
        }
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.dialog_collection, null);
        setContentView(inflate);
        init(inflate);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = 1200; // 宽度设置为屏幕的0.8
        lp.height = 800; // 高度设置为屏幕的0.4
        dialogWindow.setWindowAnimations(R.style.dialogWindowAnim);
        dialogWindow.setAttributes(lp);
        setCancelable(true);
    }


    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_UP:
                ((ChatActivity)mContext).startTimer();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 初始化
     * @param view
     */
    private void init(View view){
        mViewPager = view.findViewById(R.id.vp_collectiondialog_view);
        mImageViewClose = view.findViewById(R.id.iv_collectiondialog_close);
        final ImageView mImageViewLeft = view.findViewById(R.id.iv_collectiondialog_left);
        AnimationDrawable leftAnimDrawable = (AnimationDrawable) mImageViewLeft.getBackground();
        leftAnimDrawable.start();
        final ImageView mImageViewRight = view.findViewById(R.id.iv_collectiondialog_right);
        AnimationDrawable rightAnimDrawable = (AnimationDrawable) mImageViewRight.getBackground();
        rightAnimDrawable.start();
        mImageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CollectionDialog.this.dismiss();
            }
        });
        if (answerItem.kNodeItems.size() == 1){
            mImageViewLeft.setVisibility(View.GONE);
            mImageViewRight.setVisibility(View.GONE);
        }else{
            mImageViewLeft.setVisibility(View.GONE);
        }
        CollectionVPAdapter mAdapter = new CollectionVPAdapter(mContext, answerItem.kNodeItems);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                LogUtils.e("onPageSelected:" + i);
                RobotManager.stopSpeech();
                if (!TextUtils.isEmpty(answerItem.kNodeItems.get(i).dataItems.get(0).fieldValue.introduce)){
                    RobotManager.speechVoice(answerItem.kNodeItems.get(i).dataItems.get(0).fieldValue.introduce);
                }
                if (i == 0){
                    mImageViewLeft.setVisibility(View.GONE);
                    mImageViewRight.setVisibility(View.VISIBLE);
                }else if (i == answerItem.kNodeItems.size() -1){
                    mImageViewLeft.setVisibility(View.VISIBLE);
                    mImageViewRight.setVisibility(View.GONE);
                }else{
                    mImageViewLeft.setVisibility(View.VISIBLE);
                    mImageViewRight.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        if (!TextUtils.isEmpty(answerItem.kNodeItems.get(0).dataItems.get(0).fieldValue.introduce)){
            RobotManager.speechVoice(answerItem.kNodeItems.get(0).dataItems.get(0).fieldValue.introduce);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        RobotManager.stopSpeech();
        if (originalListen){
            RobotManager.isListen = true;
        }
    }

}
