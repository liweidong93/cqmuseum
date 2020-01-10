package com.cnki.cqmuseum.interf;

import com.ubtrobot.navigation.Marker;

/**
 * 导航点回调
 */
public interface OnMarkerCallBack {

    void onSucess(Marker marker);

    void failed();

    void setMsg(String msg);

}
