package com.cnki.cqmuseum.interf;

/**
 * 机器人听写回调
 * Created by liweidong on 2019/10/31.
 */

public interface OnASRCallBack {

    /**
     * 开始
     */
    void onStart();

    /**
     * 结束
     */
    void onEnd();

    /**
     * 音量变化
     * @param i
     */
    void onVolumeChanged(int i);

    /**
     * 结果回调
     * @param result
     */
    void onResult(String result);

    /**
     * 发生错误
     */
    void onError();
}
