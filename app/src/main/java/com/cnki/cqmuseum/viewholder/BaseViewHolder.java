package com.cnki.cqmuseum.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cnki.cqmuseum.ui.chat.ChatPresenter;
import java.util.ArrayList;

/**
 * recyclerview  viewholder基类
 * Created by admin on 2019/6/6.
 */

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
        initView(itemView);
    }

    /**
     * 初始化UI
     * @param itemView
     */
    abstract void initView(View itemView);

    /**
     * 初始化数据
     * @param context
     * @param adapter
     * @param datas
     * @param position
     */
    public abstract void initData(Context context, ChatPresenter presenter, RecyclerView.Adapter adapter, ArrayList<T> datas, int position);
}
