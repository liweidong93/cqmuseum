package com.cnki.cqmuseum.ui.navigation;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.base.BaseActivity;
import com.cnki.cqmuseum.constant.IntentActionConstant;
import com.cnki.cqmuseum.interf.OnMarkerCallBack;
import com.cnki.cqmuseum.interf.OnNaviCallBack;
import com.cnki.cqmuseum.manager.RobotManager;
import com.ubtrobot.async.DoneCallback;
import com.ubtrobot.async.FailCallback;
import com.ubtrobot.navigation.Marker;
import com.ubtrobot.speech.SynthesisException;

public class NavigationActivity extends BaseActivity<NavigationPresenter> implements INavigationView {

    private NavigationPresenter mPresenter;
    private String location;
    private boolean isOriginalListen;
    private TextView mTextViewTip;

    @Override
    public NavigationPresenter initPresenter() {
        mPresenter = new NavigationPresenter(this);
        return mPresenter;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_navigation;
    }

    @Override
    public void initView() {
        //取消导航
        findViewById(R.id.rl_navigation_rootview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RobotManager.stopNavigation();
                RobotManager.speak("取消导航成功");
                //导航取消
                NavigationActivity.this.finish();
            }
        });
        mTextViewTip = findViewById(R.id.tv_navigation_tip);
    }

    @Override
    public void initData() {
        isOriginalListen = RobotManager.isListen;
        if (isOriginalListen){
            RobotManager.isListen = false;
        }
        location = getIntent().getStringExtra(IntentActionConstant.NAVI_LOCATION);
        //根据位置点名称获取marker
        RobotManager.getMarkerByName(location, new OnMarkerCallBack() {
            @Override
            public void onSucess(Marker marker) {
                //开始导航
                RobotManager.startNavigation(marker, new OnNaviCallBack() {
                    @Override
                    public void onSuccess() {
                        mTextViewTip.setText("您好，“" + location + "”已经到了");
                        RobotManager.speak("您好" + location + "已经到了" + "您好" + location + "已经到了" + "您好" + location + "已经到了")
                                .done(new DoneCallback<Void>() {
                                    @Override
                                    public void onDone(Void aVoid) {
                                        NavigationActivity.this.finish();
                                    }
                                })
                                .fail(new FailCallback<SynthesisException>() {
                                    @Override
                                    public void onFail(SynthesisException e) {
                                        NavigationActivity.this.finish();
                                    }
                                });
                    }

                    @Override
                    public void onFailed() {
                        //导航失败，退出界面
                        RobotManager.speak("导航异常，请您重新尝试").done(new DoneCallback<Void>() {
                            @Override
                            public void onDone(Void aVoid) {
                                NavigationActivity.this.finish();
                            }
                        }).fail(new FailCallback<SynthesisException>() {
                            @Override
                            public void onFail(SynthesisException e) {
                                NavigationActivity.this.finish();
                            }
                        });
                    }
                });
            }

            @Override
            public void failed() {
                //获取位置点失败，退出界面
                NavigationActivity.this.finish();
            }

            @Override
            public void setMsg(String msg) {
                mTextViewTip.setText(msg);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        if (isOriginalListen){
            RobotManager.isListen = true;
        }
    }
}
