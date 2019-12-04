package com.cnki.cqmuseum.ui.guide;

import com.cnki.cqmuseum.base.BaseView;
import com.cnki.cqmuseum.bean.Guide;

/**
 * Created by liweidong on 2019/11/8.
 */

public interface IGuideView extends BaseView {
    void notifyGuideUi(String pointName);

    void reachBack(Guide guide);
}
