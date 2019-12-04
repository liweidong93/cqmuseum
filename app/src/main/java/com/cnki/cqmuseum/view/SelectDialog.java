package com.cnki.cqmuseum.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.manager.RobotManager;

/**
 * 选择弹窗
 * Created by liweidong on 2019/11/13.
 */

public class SelectDialog extends Dialog {

    private Context mContext;
    private ImageView mImageViewClose;
    private TextView mTextViewContent;
    private TextView mTextViewCancel;
    private TextView mTextViewSure;
    private String msg;
    private OnSelectCallBack onSelectCallBack;

    public SelectDialog(@NonNull Context context, String content, OnSelectCallBack onSelectCallBack) {
        super(context);
        this.mContext = context;
        this.msg = content;
        this.onSelectCallBack = onSelectCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);//加了才有圆角
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_select, null);
        setContentView(view);
        initView(view);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.4); // 宽度设置为屏幕的0.8
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
        setCancelable(false);
    }

    private void initView(View view) {
        mImageViewClose = view.findViewById(R.id.iv_selectdialog_close);
        mTextViewContent = view.findViewById(R.id.tv_selectdialog_content);
        mTextViewCancel = view.findViewById(R.id.tv_selectdialog_cancel);
        mTextViewSure = view.findViewById(R.id.tv_selectdialog_sure);

        mImageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectDialog.this.dismiss();
            }
        });

        mTextViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectDialog.this.dismiss();
            }
        });
        mTextViewContent.setText(msg);
        mTextViewSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectDialog.this.dismiss();
                onSelectCallBack.onSuccess();
            }
        });
    }

    public interface OnSelectCallBack{
        void onSuccess();
    }

}
