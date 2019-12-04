package com.cnki.cqmuseum.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cnki.cqmuseum.bean.AnswerBean;
import com.cnki.cqmuseum.constant.UrlConstant;
import com.cnki.cqmuseum.manager.RobotManager;
import com.cnki.cqmuseum.view.WebViewDialog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * html工具类
 * Created by admin on 2019/6/5.
 */

public class HtmlUtils {

    /**http://refbook.img.cnki.net
     * 处理html
     * @param answer
     * @return
     */
    private static String handleHtml(String answer){
        //去掉段落符号
        if (!TextUtils.isEmpty(answer)){
            answer = answer.replace("<<","《");
            answer = answer.replace(">>","》");
            answer = answer.replace("<p>","");
            answer = answer.replace("</p>", "<br>");
            if (answer.startsWith("\r<br>")){
                answer = answer.substring(5);
            }
            //如果答案中有图片，加入图片地址images/61_九龙湖校区厕所_一层.jpg
            answer = answer.replaceAll("src=\"", "src=\"" + UrlConstant.URL_CNKI_PIC);
            if(answer.endsWith(".jpg)") || answer.endsWith(".jpg )")) {
                answer = answer.replace(".jpg)", ")");
            }
            //modify
            // 1. 先把\r\n<br>替换为<br><br>\t
            answer = answer.replace("\r\n<br><br>", "<br><br>\t\t");
            answer = answer.replace("\r\n<br>", "<br><br>\t\t");
            //2. \r\n去掉
            if (!answer.contains("<br>")){
                //如果不包含br标签的话，则\r\n换位换行，否则换位空字符
                answer = answer.replace("。\r\n", "。<br>");
                answer = answer.replace("；\r\n", "；<br>");
                answer = answer.replace(">\r\n", "><br>");
                answer = answer.replace("：\r\n", "：<br>");
                answer = answer.replace("\r\n", "");
            }else{
                answer = answer.replace("\r\n", "");
            }
            answer = answer.replaceAll("\\n", "<br>");
            //去掉开头跟结尾的<br>
            if (answer.startsWith("<br>")){
                answer = answer.substring(4);
            }
            if (answer.endsWith("<br>")){
                answer = answer.substring(0, answer.length() - 4);
            }
            //段前加缩进,如果大于一行
            if (!answer.startsWith("\t") && answer.length() > 30){
                answer = "\t\t" + answer;
            }
            return answer;
        }else{
            return "";
        }
    }

    /**
     * 去掉html
     * @param speechStr
     * @return
     */
    public static String removeHtml(String speechStr){
        if (TextUtils.isEmpty(speechStr)){
            return "";
        }
        // 过滤html标签
        String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式
        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(speechStr);
        speechStr = m_html.replaceAll(""); //过滤html标签
        // 替换 \r\n
        speechStr = speechStr.replaceAll("\\r\\n", "");
        //去掉标红符号
        speechStr = speechStr.replaceAll("#", "");
        speechStr = speechStr.replaceAll("$", "");
        return speechStr;
    }



    /**
     * 设置html语言到textview
     * @param context
     * @param textView
     */
    public static void setHtmlToTextView(final Context context, TextView textView, String answer){
        if (TextUtils.isEmpty(answer)){
            textView.setText("");
        }else{
            //1.先格式化html
            answer = handleHtml(answer);
            LogUtils.e("HTMLUTILS",answer);
            if(answer.contains("<a href")) { //判断html中是否含有超链接，若有，则直接显示
                Spanned normalStr = convertH5TextToSpanned(textView, context, answer);
                textView.setText(normalStr);
                textView.setMovementMethod(new WebLinkMethod(context,UrlConstant.URL_CNKI_PIC));
            } else {
                //2.去掉超链接
                Spannable spanned = (Spannable) Html.fromHtml(answer);
//                Spanned normalStr = convertH5TextToSpanned(textView, context, spanned.toString());
                textView.setText(spanned.toString());
            }
        }
    }

    /**
     * 将H5字符串转换成Spanned字符串保证图片的显示。
     * @param textView
     * @param context
     * @param h5Str
     * @return
     */
    private static Spanned convertH5TextToSpanned(final TextView textView, final Context context, String h5Str) {
        return Html.fromHtml(h5Str, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String url) {
                final LevelListDrawable drawable = new LevelListDrawable();
                Glide.with(textView).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if(resource != null) {
                            BitmapDrawable bitmapDrawable = new BitmapDrawable(resource);
                            drawable.addLevel(0, 0, bitmapDrawable);
                            double originalWidth = resource.getWidth();
                            if (800 < originalWidth){
                                double ratio = ((double) resource.getHeight()) / ((double) resource.getWidth());
                                int height = (int) (800 * ratio);
                                // Log.e("图片", "width: " + width + ", height: " + height);
                                // 根据原图宽高比重新设置图片大小
                                drawable.setBounds(0, 0, 800, height);
                            }else{
                                drawable.setBounds(0, 0, resource.getWidth(), resource.getHeight());
                            }
                            drawable.setLevel(0);
                            textView.invalidate();
                            textView.setText(textView.getText());
                        }
                    }
                });
                return drawable;
            }
        }, null);
    }


}
