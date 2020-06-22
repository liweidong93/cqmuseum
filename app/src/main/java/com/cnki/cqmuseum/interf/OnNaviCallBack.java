package com.cnki.cqmuseum.interf;

/**
 * 导航回调
 */
public interface OnNaviCallBack {
    void onSuccess();

    void onFailed();

    void setLocationName(String locationName);
}
