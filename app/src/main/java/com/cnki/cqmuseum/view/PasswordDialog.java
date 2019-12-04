package com.cnki.cqmuseum.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.manager.RobotManager;
import com.cnki.cqmuseum.utils.ToastUtils;

import org.w3c.dom.Text;

/**
 * 退出界面密码弹窗
 * Created by liweidong on 2019/11/15.
 */

public class PasswordDialog extends Dialog {

    private Context mContext;
    private boolean isOriginalListener;
    private EditText mEditTextPwd;
    private TextView mTextViewSure;
    private TextView mTextViewCancle;
    private ImageView mImageViewClose;
    private OnPasswordCallBack mCallBack;

    public PasswordDialog(@NonNull Context context, OnPasswordCallBack callBack) {
        super(context);
        this.mContext = context;
        this.mCallBack = callBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);//加了才有圆角
        super.onCreate(savedInstanceState);
        isOriginalListener = RobotManager.isListen;
        if (RobotManager.isListen){
            RobotManager.isListen = false;
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_password, null);
        setContentView(view);
        mEditTextPwd = view.findViewById(R.id.et_passworddialog_input);
        mTextViewSure = view.findViewById(R.id.tv_passworddialog_sure);
        mTextViewCancle = view.findViewById(R.id.tv_passworddialog_cancel);
        mImageViewClose = view.findViewById(R.id.iv_passworddialog_close);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.4); // 宽度设置为屏幕的0.8
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
        setCancelable(true);
        mTextViewCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PasswordDialog.this.dismiss();
            }
        });
        mImageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PasswordDialog.this.dismiss();
            }
        });
        mTextViewSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pwd = mEditTextPwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)){
                    ToastUtils.toast(mContext,"密码不能输入为空");
                    return;
                }
                if (pwd.equals("667788")){
                    PasswordDialog.this.dismiss();
                    mCallBack.exit();
                }else{
                    ToastUtils.toast(mContext,"密码输入错误，请重新输入");
                }
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (isOriginalListener){
            RobotManager.isListen = true;
        }
    }

    public interface OnPasswordCallBack{
        void exit();
    }
}
