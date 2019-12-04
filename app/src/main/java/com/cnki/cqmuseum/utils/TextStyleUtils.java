package com.cnki.cqmuseum.utils;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.List;

/**
 * 文本样式工具类
 * Created by admin on 2019/6/6.
 */

public class TextStyleUtils {

    /**
     * 判断字符串是否为空
     * @param str
     * @return
     */
    public static String isStrEmpty(String str){
        if (TextUtils.isEmpty(str)){
            return "";
        }
        return str.trim();
    }

    /**
     * 关键字改变颜色
     * @param answer
     * @return
     */
    public static String addToolBookKeyWordColor(String answer){
        if (TextUtils.isEmpty(answer)){
            return "";
        }
        answer = answer.replace("###", "<font color='#FF0000'>").replace("$$$", "</font>");
        return answer;
    }
    /**
     * 获取html关键字标红文本
     * @param str
     * @return
     */
    public static Spanned getHtmlKeyWordText(String str){
        return Html.fromHtml(TextStyleUtils.addToolBookKeyWordColor(TextStyleUtils.isStrEmpty(str)));
    }



    /**
     * 汉字转为拼音
     *
     * @param chinese
     * @return
     */
    public static String toPinyin(String chinese) {
        LogUtils.e("");
        String pinyinStr = "";
        char[] newChar = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < newChar.length; i++) {
            if (newChar[i] > 128) {
                try {
                    String[] strings = PinyinHelper.toHanyuPinyinStringArray(newChar[i], defaultFormat);
                    if (strings != null && strings.length != 0){
                        pinyinStr += strings[0];
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinStr += newChar[i];
            }
        }
        return pinyinStr;
    }

    /**
     * 以key开头
     * @param pinyin
     * @param keyList
     * @return
     */
    public static boolean startWithKey(String pinyin, List<String> keyList) {
        for(String key : keyList) {
            if(pinyin.startsWith(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 去掉标红
     * @param result
     * @return
     */
    public static String replaceRedTag(String result){
        if (TextUtils.isEmpty(result)){
            return "";
        }
        result  = result.replace("###","");
        result  = result.replace("$$$","");
        return result;
    }

    /**
     * 判断字符串中中文的个数
     * @param s
     * @return
     */
    public static double getChineseSize(String s) {
        double valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        for (int i = 0; i < s.length(); i++) {
            // 获取一个字符
            String temp = s.substring(i, i + 1);
            // 判断是否为中文字符
            if (temp.matches(chinese)) {
                // 中文字符长度为1
                valueLength += 1;
            } else {
                // 其他字符长度为0.5
                valueLength += 0.5;
            }
        }
        //进位取整
        return Math.ceil(valueLength);
    }


    /**
     * 去掉字符串中的所有特殊符号、标点符号
     * @param s
     * @return
     */
    public static String removeAllChar(String s){
        if (TextUtils.isEmpty(s)){
            return "";
        }
        String str=s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
        str = str.replace("_","");
        return str;
    }

    /**
     * 字体单位px转dip
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return(int)(pxValue/scale+0.5f);
    }

    /**
     * 半角转换为全角
     *
     * @param input
     * @return
     */
    public static String toDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {// 全角空格为12288，半角空格为32
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)// 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

}
