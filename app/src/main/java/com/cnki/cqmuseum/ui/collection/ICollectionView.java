package com.cnki.cqmuseum.ui.collection;

import com.cnki.cqmuseum.base.BaseView;
import com.cnki.cqmuseum.bean.CollectionList;

import java.util.ArrayList;

/**
 * Created by liweidong on 2019/10/31.
 */

public interface ICollectionView extends BaseView {

    void onSuccess(int total, ArrayList<CollectionList.CollectionItem> collectionItems);

    void onFailed();

    void noEmpty();

    void onFinish();
}
