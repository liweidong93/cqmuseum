package com.cnki.cqmuseum.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.bean.AnswerBean;
import com.cnki.cqmuseum.ui.chat.ChatPresenter;
import com.cnki.cqmuseum.utils.HtmlUtils;
import com.cnki.cqmuseum.utils.TextStyleUtils;

import java.util.ArrayList;

/**
 * 默认答案的viewholder
 * Created by admin on 2019/6/5.
 */

public class DefaultViewHolder extends BaseViewHolder<AnswerBean.AnswerItem>{

    private TextView mTextViewDefault;

    public DefaultViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    void initView(View itemView) {
        mTextViewDefault = itemView.findViewById(R.id.tv_chatdefault_answer);
    }

    @Override
    public void initData(Context context, ChatPresenter presenter, RecyclerView.Adapter adapter, ArrayList<AnswerBean.AnswerItem> datas, int position) {
        //如果大于一行的话就padding改变
        if (!TextUtils.isEmpty(datas.get(position).Answer)){
            if (datas.get(position).Answer.length() > 30){
                mTextViewDefault.setPadding(75,42,40,49);
            }else{
                mTextViewDefault.setPadding(75,16,40,15);
            }
            HtmlUtils.setHtmlToTextView(context, mTextViewDefault, datas.get(position).Answer);
        }else{
            mTextViewDefault.setText("这个真是难到我了，我正在学习中...");
        }
    }
}
