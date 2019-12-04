package com.cnki.cqmuseum.interf;


import com.cnki.cqmuseum.view.GuideFloatButton;

/**
 * Author:xishuang
 * Date:2017.08.01
 * Des:暴露一些与悬浮窗交互的接口
 */
public interface FloatCallBack {
    void guideUser(int type);

    void show();

    void hide();
}
