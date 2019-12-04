package com.cnki.cqmuseum.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by liweidong on 2019/11/5.
 */

public class ImageUtils {

    /**
     * 给Image添加光晕
     * @param context 上下文
     * @param imageId 图片id
     * @param shadowColorId 光晕颜色id
     * @param radius （外围光晕宽度，也可以根据图片尺寸按照比例来，根据实际需求）
     * @return 加完光晕的图片
     */
    public static Bitmap addHaloToImage(Context context, int imageId, int shadowColorId, float radius){
        BitmapDrawable mBitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(imageId);
        Bitmap mBitmap = mBitmapDrawable.getBitmap();
        int mBitmapWidth = mBitmap.getWidth();
        int mBitmapHeight = mBitmap.getHeight();
        int shadowRadius = 50;
        //创建一个比原来图片大2个radius的图片对象
        Bitmap mHaloBitmap = Bitmap.createBitmap(mBitmapWidth+shadowRadius*2, mBitmapHeight+shadowRadius*2, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(mHaloBitmap);
        //设置抗锯齿
        mCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setColor(context.getResources().getColor(shadowColorId));
        //外发光
        mPaint.setMaskFilter(new BlurMaskFilter(shadowRadius, BlurMaskFilter.Blur.OUTER));
        //从原位图中提取只包含alpha的位图
        Bitmap alphaBitmap = mBitmap.extractAlpha();
        //在画布上（mHaloBitmap）绘制alpha位图
        mCanvas.drawBitmap(alphaBitmap, shadowRadius, shadowRadius, mPaint);
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mCanvas.drawBitmap(mBitmap,null,new Rect(shadowRadius+1,shadowRadius+1,shadowRadius+mBitmapWidth-1, shadowRadius+mBitmapHeight-1),null);
        //回收
        mBitmap.recycle();
        alphaBitmap.recycle();
        return mHaloBitmap;
    }

}
