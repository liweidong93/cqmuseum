package com.cnki.cqmuseum.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.manager.RobotManager;
import com.cnki.cqmuseum.ui.chat.ChatActivity;

/**
 * 视频漫画窗口
 * Created by admin on 2019/7/17.
 */

public class PhotoViewDialog extends Dialog {

    private String url;
    private Context mContext;
    private boolean orignalListen;
    private PhotoView mPhotoView;

    public PhotoViewDialog(@NonNull Context context, String url) {
        super(context);
        this.url = url;
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);//加了才有圆角
        super.onCreate(savedInstanceState);
        orignalListen = RobotManager.isListen;
        if (orignalListen){
            RobotManager.isListen = false;
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_photoview, null);
        setContentView(view);
        mPhotoView = view.findViewById(R.id.pv_photoviewdialog_pic);
        // 使用原始图片大小
        Glide.with(getContext())
                .load(url)
                .override(1920,1080)
                .into(mPhotoView);
        mPhotoView.enable();

        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoViewDialog.this.dismiss();
            }
        });
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.width = (int) (d.widthPixels * 0.77); // 宽度设置为屏幕的0.8
        lp.width = 1300; // 宽度设置为屏幕的0.8
//        lp.height = (int) (d.heightPixels * 0.25); // 高度设置为屏幕的0.4
        lp.height = 700;
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

    @Override
    public void dismiss() {
        super.dismiss();
        if (orignalListen){
            RobotManager.isListen = true;
        }
    }
}
