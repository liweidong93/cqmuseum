package com.cnki.cqmuseum.interf;

public interface OnLocationCallBack {
    void onSuccess(String markerName, String id);

    void onFailed();
}
