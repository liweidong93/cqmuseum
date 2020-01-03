package com.cnki.cqmuseum.ui.guide;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.base.BaseActivity;
import com.cnki.cqmuseum.bean.BaseEvenBusBean;
import com.cnki.cqmuseum.bean.Guide;
import com.cnki.cqmuseum.constant.EvenBusConstant;
import com.cnki.cqmuseum.constant.GuideWords;
import com.cnki.cqmuseum.constant.IntentActionConstant;
import com.cnki.cqmuseum.constant.NavigationStateConstant;
import com.cnki.cqmuseum.interf.OnGuideListener;
import com.cnki.cqmuseum.manager.FloatButtonManager;
import com.cnki.cqmuseum.manager.RobotManager;
import com.cnki.cqmuseum.utils.GlideUtils;
import com.cnki.cqmuseum.view.SelectDialog;
import com.ubtechinc.cruzr.sdk.speech.SpeechRobotApi;

import java.util.ArrayList;

/**
 * 导览界面
 * Created by liweidong on 2019/11/8.
 */

public class GuideActivity extends BaseActivity<GuidePresenter> implements IGuideView, OnGuideListener{

    private GuidePresenter mPresenter;
    //游览路线
    private ArrayList<Guide> guideRoutes;
    //将要去的位置点
    private String goPoint = "接待点";
    private ImageView mImageViewGuideIcon;
    private ImageView mImageViewPic;
    private ScrollView mScrollViewIntroduce;
    private TextView mTextViewName;
    private TextView mTextViewIntroduce;
    public static int naviState;
    private TextView mTextViewSwitch;
    //是否需要返回接待点
    public static boolean isNeedGoHome = false;

    @Override
    public GuidePresenter initPresenter() {
        mPresenter = new GuidePresenter(this, GuideActivity.this);
        return mPresenter;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_guide;
    }

    @Override
    public void initView() {
        ImageView mImageViewBack = findViewById(R.id.iv_guide_back);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (naviState == NavigationStateConstant.STATE_STANDBY){
                    GuideActivity.this.finish();
                }else{
                    new SelectDialog(GuideActivity.this, "正在执行导航任务，是否确认退出导航？",
                            new SelectDialog.OnSelectCallBack() {
                                @Override
                                public void onSuccess() {
                                    mPresenter.stopNaviRoute();
                                }
                            }).show();
                }
            }
        });
        mImageViewGuideIcon = findViewById(R.id.iv_guide_guideicon);
        mImageViewPic = findViewById(R.id.iv_guide_pic);
        mScrollViewIntroduce = findViewById(R.id.sv_guide_introduce);
        mTextViewName = findViewById(R.id.tv_guide_name);
        mTextViewIntroduce = findViewById(R.id.tv_guide_introduce);
        mTextViewSwitch = findViewById(R.id.tv_guide_switch);
        mTextViewSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String switchText = mTextViewSwitch.getText().toString();
                if (switchText.equals("开始导航") && naviState == NavigationStateConstant.STATE_STANDBY){
                    //开始导览
                    startGuide();
                }else if (switchText.equals("暂停导航") && naviState == NavigationStateConstant.STATE_NAVING){
                    //暂停导览
                    mPresenter.pauseNaviRoute();
                }else if (switchText.equals("继续导航") && naviState == NavigationStateConstant.STATE_PAUSE){
                    //继续导览
                    mPresenter.continueNaviRoute();
                }
            }
        });
    }

    @Override
    public void initData() {
        isNeedGoHome = false;
        //准备数据
        createGuideRoute();
        mPresenter.setGuides(guideRoutes);
        setPoints();
        //隐藏弹窗
        FloatButtonManager.getInstance().hide();
        //初始化导航状态为待机
        naviState = NavigationStateConstant.STATE_STANDBY;
        //停止播报
        RobotManager.stopSpeech();
        String guide = getIntent().getStringExtra(IntentActionConstant.NAVI_GUIDE);
        String location = getIntent().getStringExtra(IntentActionConstant.NAVI_LOCATION);
        if (TextUtils.isEmpty(location)){
            if (TextUtils.isEmpty(guide)){
                RobotManager.speechVoice("请跟我说开始导航");
            }else{
                startGuide();
            }
        }else{
            goPoint(location);
        }
    }

    /**
     * 带我去某个具体的地点
     * @param location
     */
    private void goPoint(String location){
        mTextViewSwitch.setVisibility(View.GONE);
        //正常导航逻辑
        naviState = NavigationStateConstant.STATE_NAVING;
        mPresenter.goPoint(location);
    }

    /**
     * 开始导览
     */
    private void startGuide(){
        if (naviState == NavigationStateConstant.STATE_STANDBY){
            mTextViewSwitch.setText("暂停导航");
            naviState = NavigationStateConstant.STATE_NAVING;
            mPresenter.startNaviRoute();
        }
    }

    /**
     * 设置点
     */
    private void setPoints(){
        for (Guide guide : guideRoutes){
            RelativeLayout relativeLayout = guide.getRelativeLayout();
            ImageView mImageViewRobot = relativeLayout.findViewById(R.id.iv_guide_robot);
            ImageView mImageViewIcon = relativeLayout.findViewById(R.id.iv_guide_point);
            TextView mTextViewName = relativeLayout.findViewById(R.id.tv_guide_name);
            if (goPoint.equals(guide.getName())){
                mImageViewRobot.setVisibility(View.VISIBLE);
                mImageViewIcon.setBackgroundResource(R.drawable.stroken_2_red_solidwhite);
            }else{
                mImageViewRobot.setVisibility(View.INVISIBLE);
                mImageViewIcon.setBackgroundResource(R.drawable.stroken_2_red);
            }
            mTextViewName.setText(guide.getName());
        }
    }

    /**
     * 每个点的点击事件
     * @param view
     */
    public void onClick(View view){
        if (naviState != NavigationStateConstant.STATE_STANDBY){
            return;
        }
        int index = 0;
        switch (view.getId()){
            case R.id.include_guide_point1:
                index = 0;
                break;
            case R.id.include_guide_point2:
                index = 1;
                break;
            case R.id.include_guide_point3:
                index = 2;
                break;
            case R.id.include_guide_point4:
                index = 3;
                break;
            case R.id.include_guide_point5:
                index = 4;
                break;
            case R.id.include_guide_point6:
                index = 5;
                break;
            case R.id.include_guide_point7:
                index = 6;
                break;
            case R.id.include_guide_point8:
                index = 7;
                break;
            case R.id.include_guide_point9:
                index = 8;
                break;
        }
        goPoint = guideRoutes.get(index).getName();
        goPoint(goPoint);
    }

    /**
     * 创建游览路线
     * @return
     */
    private void createGuideRoute(){
        guideRoutes = new ArrayList<>();
        Guide jiedai = new Guide("接待点", "http://qa2.cnki.net/YuMNHShow/admin/file/faq/大熊猫/cqzrbwg27.jpg", "这里是接待点", (RelativeLayout) findViewById(R.id.include_guide_point1));
        guideRoutes.add(jiedai);
        Guide guide1 = new Guide("恐龙馆", "http://qa2.cnki.net/YuMNHShow/admin/file/faq/大熊猫/cqzrbwg27.jpg", GuideWords.DINOSAUR,(RelativeLayout) findViewById(R.id.include_guide_point2));
        guideRoutes.add(guide1);
        Guide guide2 = new Guide("岩石馆", "http://qa2.cnki.net/YuMNHShow/admin/file/faq/大熊猫/cqzrbwg27.jpg", GuideWords.ROCK,(RelativeLayout) findViewById(R.id.include_guide_point3));
        guideRoutes.add(guide2);
        Guide guide3 = new Guide("化石馆", "http://qa2.cnki.net/YuMNHShow/admin/file/faq/大熊猫/cqzrbwg27.jpg", GuideWords.FOSSIC,(RelativeLayout) findViewById(R.id.include_guide_point4));
        guideRoutes.add(guide3);
        Guide guide4 = new Guide("文化馆", "http://qa2.cnki.net/YuMNHShow/admin/file/faq/大熊猫/cqzrbwg27.jpg", "这里是文化馆",(RelativeLayout) findViewById(R.id.include_guide_point5));
        guideRoutes.add(guide4);
        Guide guide5 = new Guide("美术馆", "http://qa2.cnki.net/YuMNHShow/admin/file/faq/大熊猫/cqzrbwg27.jpg", "这里是美术馆",(RelativeLayout) findViewById(R.id.include_guide_point6));
        guideRoutes.add(guide5);
        Guide guide6 = new Guide("动物馆", "http://qa2.cnki.net/YuMNHShow/admin/file/faq/大熊猫/cqzrbwg27.jpg", "这里是动物馆",(RelativeLayout) findViewById(R.id.include_guide_point7));
        guideRoutes.add(guide6);
        Guide guide7 = new Guide("植物馆", "http://qa2.cnki.net/YuMNHShow/admin/file/faq/大熊猫/cqzrbwg27.jpg", "这里是植物馆",(RelativeLayout) findViewById(R.id.include_guide_point8));
        guideRoutes.add(guide7);
        Guide guide8 = new Guide("水生动物馆", "http://qa2.cnki.net/YuMNHShow/admin/file/faq/大熊猫/cqzrbwg27.jpg", "这里是水生动物馆",(RelativeLayout) findViewById(R.id.include_guide_point9));
        guideRoutes.add(guide8);
    }

    @Override
    public void onEvenBusCallBack(BaseEvenBusBean baseEvenBusBean) {
        super.onEvenBusCallBack(baseEvenBusBean);
        switch (baseEvenBusBean.getTag()){
            case EvenBusConstant.EVENBUS_STARTNAVI://开始导航
                //开始导览
                startGuide();
                break;
            case EvenBusConstant.EVENBUS_STOPNAVI://停止导航
                mPresenter.stopNaviRoute();
                break;
            case EvenBusConstant.EVENBUS_GOPOINT://带我去某地
                String location = (String) baseEvenBusBean.getObject();
                goPoint(location);
                break;
            case EvenBusConstant.EVENBUS_CONTINUENAVI://继续导航
                mPresenter.continueNaviRoute();
                break;
            case EvenBusConstant.EVENBUS_PAUSENAVI://暂停导航
                mPresenter.pauseNaviRoute();
                break;
            case EvenBusConstant.EVENBUS_GOHOME://返回接待点
                isNeedGoHome = true;
                break;
        }
    }

    @Override
    public void notifyGuideUi(final String pointName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mScrollViewIntroduce.setVisibility(View.GONE);
                mImageViewGuideIcon.setVisibility(View.VISIBLE);
                mImageViewGuideIcon.setImageResource(R.mipmap.icon_guide_goicon);
                //通知UI改变
                goPoint = pointName;
                setPoints();
            }
        });
    }

    @Override
    public void reachBack(final Guide guide) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //到达后通知，改变成图文形式
                mImageViewGuideIcon.setVisibility(View.GONE);
                mScrollViewIntroduce.setVisibility(View.VISIBLE);
                GlideUtils.loadPic(GuideActivity.this, guide.getPic(), mImageViewPic);
                mTextViewName.setText(guide.getName());
                mTextViewIntroduce.setText(guide.getIntroduce());
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        if (!isNeedGoHome){
            FloatButtonManager.getInstance().show();
        }else{
            isNeedGoHome = false;
        }
    }

    //=============导览监听============
    @Override
    public void pauseGuide() {
        mTextViewSwitch.setText("继续导航");
    }

    @Override
    public void continueGuide() {
        mTextViewSwitch.setText("暂停导航");
    }

    @Override
    public void finishGuide() {

    }
}
