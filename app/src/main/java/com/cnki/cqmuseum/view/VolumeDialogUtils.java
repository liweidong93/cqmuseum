package com.cnki.cqmuseum.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.cnki.cqmuseum.R;

/**
 * 音量弹窗
 * Created by liweidong on 2019/10/31.
 */

public class VolumeDialogUtils {

    private Context mContext;
    private ImageView mImageViewVolume;
    private PopupWindow popupWindow;

    public VolumeDialogUtils(@NonNull Context context) {
        this.mContext = context;
        createDialog();
    }

    /**
     * 创建弹窗
     */
    public void createDialog(){
        popupWindow = new PopupWindow(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_volume, null);
        popupWindow.setContentView(view);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setWidth(200);
        popupWindow.setHeight(200);
        popupWindow.setTouchable(false);
        // 设置PopupWindow的背景
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mImageViewVolume = view.findViewById(R.id.iv_volumedialog_icon);
    }

    /**
     * 显示音量窗口
     */
    public void show(){
        if (popupWindow == null){
            createDialog();
        }
        if (popupWindow.isShowing()){
            popupWindow.dismiss();
        }
        popupWindow.showAtLocation(((Activity)mContext).getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    /**
     * 隐藏音量弹窗
     */
    public void dismiss(){
        if (popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }

    /**
     * 设置音量
     * @param volume
     */
    public void setVolumeIcon(int volume){
        if (volume % 2 == 0){
            mImageViewVolume.setImageResource(R.mipmap.volume_two);
        }else if (volume % 3 == 0){
            mImageViewVolume.setImageResource(R.mipmap.volume_three);
        }else if (volume % 5 == 0){
            mImageViewVolume.setImageResource(R.mipmap.volume_four);
        }else{
            mImageViewVolume.setImageResource(R.mipmap.volume_one);
        }
    }
}
