package com.cnki.cqmuseum.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cnki.cqmuseum.R;

/**
 * 导览对话框
 * 实现点对点的游览讲解服务
 * Created by liweidong on 2019/10/28.
 */

public class GuideDialog extends Dialog{

    private Context mContext;

    public GuideDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_guide, null);
        setContentView(view);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setWindowAnimations(R.style.dialogWindowAnim);
        window.setAttributes(attributes);
    }
}
