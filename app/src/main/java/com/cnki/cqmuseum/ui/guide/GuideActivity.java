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
    private String goPoint = "迎宾点";
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
        RobotManager.stopSpeak();
        String guide = getIntent().getStringExtra(IntentActionConstant.NAVI_GUIDE);
        String location = getIntent().getStringExtra(IntentActionConstant.NAVI_LOCATION);
        if (TextUtils.isEmpty(location)){
            if (TextUtils.isEmpty(guide)){
                RobotManager.speak("请跟我说开始导航");
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
        Guide jiedai = new Guide("迎宾点", "http://qa2.cnki.net/YuMNHShow/admin/file/faq/大熊猫/cqzrbwg27.jpg", "这里是迎宾点", (RelativeLayout) findViewById(R.id.include_guide_point1));
        guideRoutes.add(jiedai);
        Guide guide1 = new Guide("特展厅", "https://www.cmnh.org.cn/upLoad/news/month_1609/201609222119537197.jpg", "这里是特展厅",(RelativeLayout) findViewById(R.id.include_guide_point2));
        guideRoutes.add(guide1);
        Guide guide2 = new Guide("贝林厅", "https://www.cmnh.org.cn/upLoad/news/month_1609/201609222112067287.jpg", "感谢肯尼斯·贝林先生，捐赠丰富的野生动物标本，让我们亲历一场寰球动物嘉年华。这里，各大洲的自然风光辽阔壮美，生机勃发；这里，各种动物标本千姿百态，栩栩如生；这里，生物之间的依存关系描摹生动，耐人寻味。动物和谐，环境和谐，自然和谐，大美无言。多种多样的生物是大自然赐予人类的宝贵财富。它们是人类赖以生存的资源，更是生态系统的重要成员，是一座座独特的基因库，是我们在这颗美丽星球上生存的伙伴。",(RelativeLayout) findViewById(R.id.include_guide_point3));
        guideRoutes.add(guide2);
        Guide guide3 = new Guide("重庆厅", "https://www.cmnh.org.cn/upLoad/news/month_1609/201609222114431724.jpg", "重庆，一座山水交融的城市，处于我国地理大格局南北、东西分界的交汇地带。亿万年来，强烈的造山运动所引起的海陆变迁及江河发育，造就了今日重庆奇特的山川形貌，并孕育了丰富的物产资源。重庆有着许多远古的印痕，大江大河串连起众多古遗址，三峡地区是古人类演化的重要通道。半山半水间，从远古到现代，重庆人一路艰辛，在利用自然的过程中，形成了尊崇自然、适应自然、自然优先的城市发展理念；这将引领重庆向更美好的未来迈进。",(RelativeLayout) findViewById(R.id.include_guide_point4));
        guideRoutes.add(guide3);
        Guide guide4 = new Guide("恐龙厅", "https://www.cmnh.org.cn/upLoad/news/month_1609/201609222115562723.jpg", "这是一个失落的世界，它的统治者早已绝迹，只留下深埋在岩层中的骨骼化石，讲述着一段跨越一亿六千万年的壮美故事。恐龙是中生代地球的主宰，为了生息繁衍，它们曾不断改变着自身，同时也改变着地球的生态，谱写了一段生物演化的宏大诗篇。恐龙种类繁多，形态千奇百怪；足迹曾遍布大地，在各大洲竞相发展。但就是这个盛极一时的族群却突然退出了生命的舞台，留下难解的谜题吸引人们去探寻，解答。穿越时空，这里将呈现一个不一样的世界！",(RelativeLayout) findViewById(R.id.include_guide_point5));
        guideRoutes.add(guide4);
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
                mImageViewPic.setVisibility(View.GONE);
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
                mImageViewPic.setVisibility(View.VISIBLE);
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
