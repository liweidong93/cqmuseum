package com.cnki.cqmuseum.ui.navigation;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.base.BaseActivity;
import com.cnki.cqmuseum.constant.IntentActionConstant;
import com.cnki.cqmuseum.interf.OnSpeakCallBack;
import com.cnki.cqmuseum.manager.RobotActionUtils;
import com.cnki.cqmuseum.manager.RobotManager;
import com.cnki.cqmuseum.ui.guide.GuideActivity;
import com.ubtechinc.cruzr.sdk.navigation.NavigationApi;
import com.ubtechinc.cruzr.sdk.ros.RosConstant;
import com.ubtechinc.cruzr.serverlibutil.interfaces.NavigationApiCallBackListener;

public class NavigationActivity extends BaseActivity<NavigationPresenter> implements INavigationView {

    private NavigationPresenter mPresenter;
    private String location;
    private boolean isOriginalListen;
    private Handler mHandler = new Handler();
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
                RobotActionUtils.stopNavigation();
                RobotManager.speechVoice("取消导航成功");
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
        NavigationApi.get().setNavigationApiCallBackListener(new NavigationApiCallBackListener() {
            @Override
            public void onNavigationResult(int i, float v, float v1, float v2) {

            }

            @Override
            public void onRemoteCommonResult(String pointName, int status, String message) {
                switch (status) {
                    case RosConstant.Action.ACTION_FINISHED:
                        RobotManager.speechVoice("您好" + location + "已经到了" + "您好" + location + "已经到了" + "您好" + location + "已经到了");
                        NavigationActivity.this.finish();
                        break;
                    case RosConstant.Action.ACTION_CANCEL:
                        RobotManager.speechVoice("取消导航成功");
                        //导航取消
                        NavigationActivity.this.finish();
                        break;
                    case RosConstant.Action.ACTION_BE_IMPEDED:
                        //导航遇到障碍
                        RobotManager.speechVoice("有什么挡到我了");
                        break;
                    case RosConstant.Action.ACTION_FAILED:
                    case RosConstant.Action.ACTION_DEVICE_CONFLICT:
                    case RosConstant.Action.ACTION_EMERGENCY_STOPPED:
                    case RosConstant.Action.ACTION_ACCESS_FORBIDDEN:
                    case RosConstant.Action.ACTION_UNIMPLEMENTED:
                        //导航失败
                        RobotManager.speechVoice("导航好像出现了点问题，请帮我联系管理员");
                        NavigationActivity.this.finish();
                        break;
                    default:
                        break;
                }
            }
        });
        mTextViewTip.setText("正在前往" + location + ",请跟进我");
        RobotManager.speechVoice("我将要带您前往" + location + ",请跟紧我哦！");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                NavigationApi.get().startNavigationService(location);
            }
        },7000);
    }

    @Override
    public void finish() {
        super.finish();
        if (isOriginalListen){
            RobotManager.isListen = true;
        }
    }
}
