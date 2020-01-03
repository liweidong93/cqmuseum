package com.cnki.cqmuseum.ui.speechserver;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.adapter.SpeechServerAdapter;
import com.cnki.cqmuseum.base.BaseActivity;
import com.cnki.cqmuseum.bean.SpeechServer;
import com.cnki.cqmuseum.interf.OnSpeakCallBack;
import com.cnki.cqmuseum.manager.FloatButtonManager;
import com.cnki.cqmuseum.manager.RobotManager;

import java.util.ArrayList;

/**
 * 讲解服务界面
 * Created by liweidong on 2019/11/7.
 */

public class SpeechServerActivity extends BaseActivity<SpeechServerPresenter> implements ISpeechServerView {

    private SpeechServerPresenter mPresenter;
    private RecyclerView mRecyclerViewData;
    private ArrayList<SpeechServer> speechServers;
    private SpeechServerAdapter mAdapter;

    @Override
    public SpeechServerPresenter initPresenter() {
        mPresenter = new SpeechServerPresenter(this);
        return mPresenter;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_speechserver;
    }

    @Override
    public void initView() {
        mRecyclerViewData = findViewById(R.id.rv_speechserver_data);
        ImageView mImageViewBack = findViewById(R.id.iv_speechserver_back);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpeechServerActivity.this.finish();
            }
        });
    }

    @Override
    public void initData() {
//        FloatButtonManager.getInstance().show();
        speechServers = new ArrayList<>();
        mAdapter = new SpeechServerAdapter(this, speechServers);
        mRecyclerViewData.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewData.setAdapter(mAdapter);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mPresenter.getSpeechServer();
    }

    @Override
    public void onSuccess(ArrayList<SpeechServer> datas) {
        speechServers.clear();
        speechServers.addAll(datas);
        mAdapter.notifyDataSetChanged();
        //开始播报
        String speechVoice = "";
        for (SpeechServer speechServer : datas){
            if (!TextUtils.isEmpty(speechServer.text)){
                speechVoice += speechServer.text;
            }
        }
        if (!TextUtils.isEmpty(speechVoice)){
            speech(speechVoice);
        }
    }

    /**
     * 播报
     * @param voice
     */
    public void speech(final String voice){
        RobotManager.speechVoice(voice, new OnSpeakCallBack() {
            @Override
            public void onSpeakEnd() {
                speech(voice);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
//        FloatButtonManager.getInstance().hide();
        if (RobotManager.isSpeaking()){
            RobotManager.stopSpeech();
        }
    }
}
