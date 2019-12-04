package com.cnki.cqmuseum.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.utils.ToastUtils;

/**
 * 导览悬浮按钮
 * Created by liweidong on 2019/10/26.
 */

public class GuideFloatButton extends FrameLayout {

    //按下时坐标
    private float downY;
    private WindowManager.LayoutParams params;
    private WindowManager mWindowManager;
    //是否点击了按钮
    private boolean isClick;
    private long startTime;
    private long endTime;
    private Context mContext;
    private float downX;
    private int screenWidth;
    private OnFloatBtnClickListener onFloatBtnClickListener;

    public GuideFloatButton(Context context) {
        this(context, null);
    }

    public GuideFloatButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        setBackgroundColor(Color.TRANSPARENT);
        //将floatbutton与layout绑定
        LayoutInflater.from(context).inflate(R.layout.layout_floatbutton, this);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isClick = false;
                startTime = System.currentTimeMillis();
                downY = event.getY();
//                downX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = event.getY();
                //判断如果移动距离大于3才改变悬浮窗位置
                if (Math.abs(moveY - downY) > 3){
                    params.y = (int) (y - downY);
                    params.x = (int) (x-downX);
                    mWindowManager.updateViewLayout(this, params);
                }
                break;
            case MotionEvent.ACTION_UP:
                endTime = System.currentTimeMillis();
                params.x = screenWidth;
                mWindowManager.updateViewLayout(this, params);
                if (endTime - startTime > 200){
                    isClick = false;
                }else{
                    isClick = true;
                }
                break;
        }
        if (isClick){
            onFloatBtnClickListener.onClick();
        }
        return true;
    }

    /**
     * 传入悬浮窗的参数，用于更新
     * @param params
     */
    public void setParams(WindowManager.LayoutParams params, int screenWidth){
        this.params = params;
        this.screenWidth = screenWidth;
    }

    /**
     * 设置悬浮窗的点击回调
     * @param onClickListener
     */
    public void setOnClickListener(OnFloatBtnClickListener onClickListener){
        this.onFloatBtnClickListener = onClickListener;
    }

    public interface OnFloatBtnClickListener{
        void onClick();
    }
}
