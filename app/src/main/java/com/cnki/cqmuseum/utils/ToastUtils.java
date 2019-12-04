package com.cnki.cqmuseum.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by lwd on 2017/7/14.
 */

public class ToastUtils {

    private static Context context = null;
    private static Toast mToast = null;

    public ToastUtils(Context context) {
        this.context = context;

    }

    public static void ToastUtils(Context context,String str){
        Toast toast=Toast.makeText(context,str,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,10);
        toast.show();
    }

    public static void toast(Context context,String msg){
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

}
