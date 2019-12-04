package com.cnki.cqmuseum.ui.speechserver;

import com.cnki.cqmuseum.base.BaseView;
import com.cnki.cqmuseum.bean.SpeechServer;

import java.util.ArrayList;

/**
 * Created by liweidong on 2019/11/7.
 */

public interface ISpeechServerView extends BaseView{

    void onSuccess(ArrayList<SpeechServer> speechServers);
}
