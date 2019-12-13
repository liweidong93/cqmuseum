package com.cnki.cqmuseum.ui.collection;

import com.cnki.cqmuseum.base.BasePresenterImpl;
import com.cnki.cqmuseum.bean.AnswerBean;
import com.cnki.cqmuseum.bean.CollectionList;
import com.cnki.cqmuseum.constant.ChatViewTypeConstant;
import com.cnki.cqmuseum.server.BaseCnkiObserver;
import com.cnki.cqmuseum.server.BaseResultObserver;
import com.cnki.cqmuseum.server.api.CnkiApi;
import com.cnki.cqmuseum.server.api.MuseumApi;
import com.cnki.cqmuseum.ui.chat.ChatModle;
import com.cnki.cqmuseum.utils.ToastUtils;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by liweidong on 2019/11/6.
 */

public class CollectionPresenter extends BasePresenterImpl<ICollectionView> {

    private ChatModle chatModle;
    private String collectionIds;

    public CollectionPresenter(ICollectionView view) {
        super(view);
        chatModle = new ChatModle();
    }


    /**
     * 根据类型获取文物列表
     * @param type ""传空，获取所有列表信息
     * @param pageSize
     */
    public void getCollectionListByType(String type, final int pageSize){
        addDisposable(MuseumApi.getInstance().getCollectionListByType(type, pageSize), new BaseResultObserver<CollectionList>(){
            @Override
            public void onSuccess(CollectionList collectionList) {
                super.onSuccess(collectionList);
                if (collectionList != null && collectionList.collections != null && collectionList.collections.size() != 0){
                    mView.onSuccess(collectionList.totalCount, collectionList.collections);
                }else{
                    //无数据情况
                    if (pageSize == 0){
                        mView.noEmpty();
                    }else{
                        mView.onFinish();
                    }
                }
            }

            @Override
            public void onFailed() {
                super.onFailed();
                mView.onFailed();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.toast(mContext,"网络不给力，请稍后重试");
            }
        });
    }

    /**
     * 根据名称模糊查询文物
     * @param name
     */
    public void getCollectionListLikeName(String name){
        addDisposable(MuseumApi.getInstance().getCollectionListLikeName(name), new BaseResultObserver<ArrayList<CollectionList.CollectionItem>>(){
            @Override
            public void onSuccess(ArrayList<CollectionList.CollectionItem> collectionItems) {
                super.onSuccess(collectionItems);
                if (collectionItems != null && collectionItems.size() != 0){
                    mView.onSuccess(0, collectionItems);
                }else{
                    mView.noEmpty();
                }
            }

            @Override
            public void onFailed() {
                super.onFailed();
                mView.onFailed();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.toast(mContext,"网络不给力，请稍后重试");
            }
        });
    }

    /**
     * 提问匹配文物
     * @param question
     */
    public void askQuestion(String question){
        addDisposable(CnkiApi.getInstance().getCnkiAnswer(question), new BaseCnkiObserver() {
            @Override
            public void onSuccess(ArrayList<AnswerBean> answerBeans) {
                AnswerBean.AnswerItem answerItem = chatModle.getViewType(answerBeans);
                //判断是否是精品文物,如果为精品文物的话，通过id来进行查找精品文物列表
                if (answerItem.viewType == ChatViewTypeConstant.VIEWTYPE_COLLECTION){
                    if (answerItem != null && answerItem.kNodeItems != null && answerItem.kNodeItems.size() != 0){
                        ArrayList<AnswerBean.AnswerItem.KNodeItem> kNodeItems = answerItem.kNodeItems;
                        //将精品文物的id获取到，然后在通过id来获取 'JW00000001'+'JW00000002'
                        collectionIds = "";
                        for (AnswerBean.AnswerItem.KNodeItem kNodeItem : kNodeItems){
                            collectionIds = collectionIds + "+ '" + kNodeItem.dataItems.get(0).fieldValue.num + "'";
                        }
                        getCollectionListByIds(collectionIds.substring(1));
                    }else{
                        mView.noEmpty();
                    }
                }else{
                    mView.noEmpty();
                }
            }

            @Override
            public void onFailed() {
                super.onFailed();
                mView.noEmpty();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.toast(mContext,"网络不给力，请稍后重试");
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 根据id获取文物列表
     * @param ids
     */
    public void getCollectionListByIds(String ids){
        addDisposable(MuseumApi.getInstance().getCollectionListByIds(ids), new BaseResultObserver<ArrayList<CollectionList.CollectionItem>>(){
            @Override
            public void onSuccess(ArrayList<CollectionList.CollectionItem> collectionItems) {
                super.onSuccess(collectionItems);
                if (collectionItems != null && collectionItems.size() != 0){
                    mView.onSuccess(0, collectionItems);
                }else{
                    mView.noEmpty();
                }
            }

            @Override
            public void onFailed() {
                super.onFailed();
                mView.onFailed();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.toast(mContext,"网络不给力，请稍后重试");
            }
        });
    }

}
