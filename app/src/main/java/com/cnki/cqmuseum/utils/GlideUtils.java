package com.cnki.cqmuseum.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cnki.cqmuseum.R;

/**
 * 图片加载glide工具类
 * Created by liweidong on 2019/12/16.
 */

public class GlideUtils {

    /**
     * 加载图片
     * @param context
     * @param url
     * @param mImageView
     */
    public static void loadPic(Context context, String url, ImageView mImageView){
        Glide.with(context).load(url).thumbnail(0.1f).error(R.mipmap.glide_default).into(mImageView);
    }
}
