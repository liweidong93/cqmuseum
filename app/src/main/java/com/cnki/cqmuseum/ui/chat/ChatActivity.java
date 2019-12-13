package com.cnki.cqmuseum.ui.chat;

import android.os.CountDownTimer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.adapter.ChatAdapter;
import com.cnki.cqmuseum.adapter.PandaQuestionAdapter;
import com.cnki.cqmuseum.base.BaseActivity;
import com.cnki.cqmuseum.bean.AnswerBean;
import com.cnki.cqmuseum.bean.BaseEvenBusBean;
import com.cnki.cqmuseum.bean.PandaBean;
import com.cnki.cqmuseum.constant.ChatViewTypeConstant;
import com.cnki.cqmuseum.constant.EvenBusConstant;
import com.cnki.cqmuseum.constant.IntentActionConstant;
import com.cnki.cqmuseum.constant.QuestionListConstant;
import com.cnki.cqmuseum.constant.RobotKeyConstant;
import com.cnki.cqmuseum.manager.FloatButtonManager;
import com.cnki.cqmuseum.manager.RobotManager;
import com.cnki.cqmuseum.server.api.MuseumApi;
import com.cnki.cqmuseum.utils.KeyboardUtils;
import com.cnki.cqmuseum.utils.RandomUtils;
import com.cnki.cqmuseum.view.CollectionDialog;

import java.util.ArrayList;

/**
 * 二级聊天界面
 * Created by liweidong on 2019/10/23.
 */

public class ChatActivity extends BaseActivity<ChatPresenter> implements IChatView{

    private ChatPresenter mPresenter;
    //问答聊天列表
    private RecyclerView mRecyclerViewChat;
    //用于输入问题问答的输入框
    private EditText mEditTextInput;
    //提交问题按钮
    private TextView mTextViewSubmit;
    //返回键按钮
    private ImageView mImageViewBack;
    //清空聊天按钮
    private ImageView mImageViewClear;
    //控制语音接收开关按钮
    private ImageView mImageViewSwitch;
    //右侧示例问题
    private TextView mTextViewTitle1;
    private TextView mTextViewTitle2;
    private TextView mTextViewTitle3;
    private TextView mTextViewTitle4;
    private TextView mTextViewTitle5;
    private TextView mTextViewTitle6;
    private TextView mTextViewTitle7;
    //聊天数据源
    public ArrayList<AnswerBean.AnswerItem> mAnswerItems;
    //聊天列表适配器
    private ChatAdapter mChatAdapter;
    private LinearLayoutManager mChatLayoutManager;
    //计时器,用于计时，如果超过三分钟对界面不进行任何操作的话，会自动返回首页
    private CountDownTimer mTimer;
    private static final long COUNTDOWN_TIME = 3*1000*60;//倒计时时间
    private LinearSmoothScroller linearSmoothScroller;
    //熊猫专题右侧目录结构数据源
    private ArrayList<PandaBean> pandaBeans;
    //右侧显示示例问题
    private RelativeLayout mRelativeLayoutNormal;
    //右侧显示熊猫专题
    private RelativeLayout mRelativeLayoutPanda;
    //熊猫专题列表
    private RecyclerView mRecyclerViewData;

    @Override
    public ChatPresenter initPresenter() {
        mPresenter = new ChatPresenter(this);
        return mPresenter;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    public void initView() {
        //隐藏菜单按钮
        hideRobotBtn();
        //初始化控件
        mRecyclerViewChat = (RecyclerView) findViewById(R.id.rv_chat_chatview);
        mEditTextInput = (EditText) findViewById(R.id.input_et);
        mTextViewSubmit = (TextView) findViewById(R.id.commit_btn);
        mImageViewBack = (ImageView) findViewById(R.id.iv_chartheader_back);
        mImageViewClear = (ImageView) findViewById(R.id.iv_chartheader_clear);
        mImageViewSwitch = (ImageView) findViewById(R.id.iv_chartheader_switch);
        mTextViewTitle1 = (TextView) findViewById(R.id.tv_main_title1);
        mTextViewTitle2 = (TextView) findViewById(R.id.tv_main_title2);
        mTextViewTitle3 = (TextView) findViewById(R.id.tv_main_title3);
        mTextViewTitle4 = (TextView) findViewById(R.id.tv_main_title4);
        mTextViewTitle5 = (TextView) findViewById(R.id.tv_main_title5);
        mTextViewTitle6 = (TextView) findViewById(R.id.tv_main_title6);
        mTextViewTitle7 = (TextView) findViewById(R.id.tv_main_title7);
        mTextViewTitle1.setSelected(true);
        mTextViewTitle2.setSelected(true);
        mTextViewTitle3.setSelected(true);
        mTextViewTitle4.setSelected(true);
        mTextViewTitle5.setSelected(true);
        mTextViewTitle6.setSelected(true);
        mTextViewTitle7.setSelected(true);
        mRelativeLayoutNormal = findViewById(R.id.rl_pandachat_normal);
        mRelativeLayoutPanda = findViewById(R.id.rl_pandachat_panda);
        mRecyclerViewData = findViewById(R.id.rv_pandachat_data);
        //提问按钮点击事件
        mTextViewSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = mEditTextInput.getText().toString().trim();
                if (TextUtils.isEmpty(question)) {
                    Toast.makeText(ChatActivity.this, "请输入问题", Toast.LENGTH_LONG).show();
                    return;
                }
                mPresenter.sendQuestion(question);
                KeyboardUtils.hideKeyboard(mEditTextInput);
                mEditTextInput.setText("");
            }
        });

        // 返回按钮
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //停止语音播报
                RobotManager.stopSpeech();
                ChatActivity.this.finish();
            }
        });

        //清空聊天点击事件
        mImageViewClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //停止语音播报
                RobotManager.stopSpeech();
                mAnswerItems.clear();
                addDefaultWelcome();
                mChatAdapter.notifyDataSetChanged();
            }
        });

        //是否停止机器人识别语音
        mImageViewSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //停止识别语音点击处理
                if (RobotManager.isListen) {
                    RobotManager.isListen = false;
                    mImageViewSwitch.setImageResource(R.mipmap.switchicon_close);
                } else {
                    RobotManager.isListen = true;
                    mImageViewSwitch.setImageResource(R.mipmap.switchicon_open);
                }
            }
        });
        //recyclerview滑动
        linearSmoothScroller = new LinearSmoothScroller(this) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return 10f / displayMetrics.densityDpi;
            }
        };
    }

    @Override
    public void initData() {
        //显示悬浮窗口
        FloatButtonManager.getInstance().show();
        RobotManager.isListen = true;
        //给右侧提示问题设置问题
        showQuestionType();
        //设置会话适配器
        mAnswerItems = new ArrayList<>();
        addDefaultWelcome();
        mChatLayoutManager = new LinearLayoutManager(this){
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                //避免同时操作数据源崩溃问题
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        };
        mRecyclerViewChat.setItemAnimator(null);
        mRecyclerViewChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(this,mPresenter,mAnswerItems);
        mRecyclerViewChat.setAdapter(mChatAdapter);
        //初始化倒计时
        mTimer = new CountDownTimer(COUNTDOWN_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//                LogUtils.e("倒计时时间:" + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                //倒计时结束，退出当前界面
                RobotManager.stopSpeech();
                ChatActivity.this.finish();
            }
        };
        mTimer.start();
        //获取是否是语音问答跳转过来的
        String question = getIntent().getStringExtra(IntentActionConstant.ACTION_QUESTION);
        if (!TextUtils.isEmpty(question)){
            mPresenter.sendQuestion(question);
        }
    }


    /**
     * 根据不同的问题类型选择显示问题的结果
     */
    private void showQuestionType() {
        String questionType = getIntent().getStringExtra(IntentActionConstant.ACTIONTYPE_PANDAFROMTYPE);
        if (TextUtils.isEmpty(questionType)){
            //如果为空，则不处理，老逻辑 20个问题中选7个
            setQuestion(QuestionListConstant.pandaQuestions);
        }else{
            //否则，新逻辑
            switch (questionType){
                case IntentActionConstant.PANDATYPE_EXHIBITION://馆藏精品
                    setQuestion(QuestionListConstant.pandaExhibitions);
                    break;
                case IntentActionConstant.PANDATYPE_GUIDE://导览指南
                    setQuestion(QuestionListConstant.pandaGuides);
                    break;
                case IntentActionConstant.PANDATYPE_PANDATIME://熊猫时代
                    mRelativeLayoutNormal.setVisibility(View.GONE);
                    mRelativeLayoutPanda.setVisibility(View.VISIBLE);
                    //初始化熊猫书籍实体
                    pandaBeans = new ArrayList<>();
                    /*PandaBean pandaBean = new PandaBean();
                    pandaBean.oneName = "第一单元 熊猫大事件";
                    ArrayList<PandaBean.SecondItem> secondItems = new ArrayList<>();
                    PandaBean.SecondItem secondItem = new PandaBean.SecondItem();
                    secondItem.secondName = "1.现生熊猫大事纪";
                    PandaBean.SecondItem secondItem1 = new PandaBean.SecondItem();
                    secondItem1.secondName = "2.古熊猫大发现";
                    ArrayList<String> threeNames = new ArrayList<>();
                    threeNames.add("大熊猫—剑齿象动物群");
                    threeNames.add("化石珍品室");
                    secondItem1.threeNames = threeNames;
                    secondItems.add(secondItem);
                    secondItems.add(secondItem1);
                    pandaBean.secondItems = secondItems;
                    pandaBeans.add(pandaBean);*/

                    PandaBean pandaBean2 = new PandaBean();
                    pandaBean2.oneName = "第一单元 揭秘大熊猫";
                    pandaBean2.isOpen = true;
                    ArrayList<PandaBean.SecondItem> secondItems2 = new ArrayList<>();
                    PandaBean.SecondItem secondItem21 = new PandaBean.SecondItem();
                    secondItem21.secondName = "1.谁与大熊猫关系更近";
                    ArrayList<String> threeNames211 = new ArrayList<>();
                    threeNames211.add("大熊猫与小熊猫的差别明显吗");
                    threeNames211.add("哪类物种与大熊猫最相似");
                    threeNames211.add("大熊猫的头骨特征");
                    threeNames211.add("大熊猫的肌肉系统有什么特点");
                    threeNames211.add("大熊猫主要吃什么");
                    secondItem21.threeNames = threeNames211;
                    PandaBean.SecondItem secondItem22 = new PandaBean.SecondItem();
                    secondItem22.secondName = "2.大熊猫外形有何差异";
                    ArrayList<String> threeNames212 = new ArrayList<>();
                    threeNames212.add("大熊猫的外形都一样吗");
                    threeNames212.add("秦岭大熊猫的体态特征与其他大熊猫一样吗");
                    secondItem22.threeNames = threeNames212;
                    PandaBean.SecondItem secondItem23 = new PandaBean.SecondItem();
                    secondItem23.secondName = "3.骨骼比较说明了什么";
                    ArrayList<String> threeNames213 = new ArrayList<>();
                    threeNames213.add("大熊猫的头骨特征");
                    threeNames213.add("大熊猫裂齿的作用");
                    threeNames213.add("食肉动物的头骨特征");
                    secondItem23.threeNames = threeNames213;
                    PandaBean.SecondItem secondItem24 = new PandaBean.SecondItem();
                    secondItem24.secondName = "4.大熊猫肌肉系统有何特点";
                    ArrayList<String> threeNames214 = new ArrayList<>();
                    threeNames214.add("大熊猫的肌肉系统有什么特点");
                    threeNames214.add("大熊猫会爬树吗");
                    secondItem24.threeNames = threeNames214;
                    PandaBean.SecondItem secondItem25 = new PandaBean.SecondItem();
                    secondItem25.secondName = "5.食竹的适应特征有哪些";
                    ArrayList<String> threeNames215 = new ArrayList<>();
                    threeNames215.add("大熊猫主要吃什么");
                    threeNames215.add("大熊猫的头骨");
                    threeNames215.add("大熊猫的咀嚼肌有什么特点");
                    threeNames215.add("大熊猫消化系统的特征");
                    threeNames215.add("大熊猫的消化能力如何");
                    threeNames215.add("大熊猫的伪拇指是什么");
                    threeNames215.add("只有大熊猫有伪拇指吗");
                    threeNames215.add("大熊猫每天能吃多少竹子");
                    threeNames215.add("大熊猫为什么能够吃坚硬的竹子");
                    threeNames215.add("大熊猫的伪拇指有什么作用");
                    secondItem25.threeNames = threeNames215;
                    PandaBean.SecondItem secondItem26 = new PandaBean.SecondItem();
                    secondItem26.secondName = "6.生长发育中有哪些变化";
                    ArrayList<String> threeNames216 = new ArrayList<>();
                    threeNames216.add("大熊猫生长发育的各阶段");
                    threeNames216.add("介绍一下大熊猫的幼仔期");
                    threeNames216.add("大熊猫的最佳受孕时间");
                    threeNames216.add("新生大熊猫的饮食");
                    threeNames216.add("为什么新生大熊猫要吃母亲便便");
                    threeNames216.add("新生大熊猫有什么特征");
                    threeNames216.add("幼崽大熊猫有什么特征");
                    threeNames216.add("幼崽大熊猫多久可以独立");
                    threeNames216.add("大熊猫的成熟期");
                    threeNames216.add("大熊猫的寿命有多长");
                    threeNames216.add("为什么大熊猫的幼崽体型较为弱小");
                    threeNames216.add("大熊猫一次产仔的数量");
                    threeNames216.add("野生大熊猫的栖息地");
                    secondItem26.threeNames = threeNames216;
                    PandaBean.SecondItem secondItem27 = new PandaBean.SecondItem();
                    secondItem27.secondName = "7.古代人见过大熊猫吗";
                    ArrayList<String> threeNames217 = new ArrayList<>();
                    threeNames217.add("古代人见过大熊猫吗");
                    threeNames217.add("古代关于熊猫的三种流行说法");
                    threeNames217.add("关于后貔貅说");
                    threeNames217.add("关于大熊猫的驺虞说");
                    threeNames217.add("关于大熊猫的貘说");
                    threeNames217.add("关于大熊猫的貔貅说");
                    secondItem27.threeNames = threeNames217;
                    secondItems2.add(secondItem21);
                    secondItems2.add(secondItem22);
                    secondItems2.add(secondItem23);
                    secondItems2.add(secondItem24);
                    secondItems2.add(secondItem25);
                    secondItems2.add(secondItem26);
                    secondItems2.add(secondItem27);
                    pandaBean2.secondItems = secondItems2;
                    pandaBeans.add(pandaBean2);

                    PandaBean pandaBean3 = new PandaBean();
                    pandaBean3.oneName = "第二单元 大熊猫演化";
                    ArrayList<PandaBean.SecondItem> secondItems3 = new ArrayList<>();
                    PandaBean.SecondItem secondItem31 = new PandaBean.SecondItem();
                    secondItem31.secondName = "1.大熊猫家谱";
                    ArrayList<String> threeNames311 = new ArrayList<>();
                    threeNames311.add("介绍一下大熊猫的家谱");
                    threeNames311.add("介绍一下大熊猫的祖先");
                    secondItem31.threeNames = threeNames311;
                    PandaBean.SecondItem secondItem32 = new PandaBean.SecondItem();
                    secondItem32.secondName = "2.大熊猫的兴衰";
                    ArrayList<String> threeNames32 = new ArrayList<>();
                    threeNames32.add("大熊猫的始发期");
                    threeNames32.add("介绍一下禄丰始熊猫");
                    threeNames32.add("关于禄丰古猿的说法");
                    threeNames32.add("克氏熊猫的发现");
                    threeNames32.add("大熊猫生存环境的变迁");
                    threeNames32.add("小种大熊猫的由来及特征");
                    threeNames32.add("巫山人化石的发现");
                    threeNames32.add("武陵山大熊猫的简介");
                    threeNames32.add("古爪哇魁人的简介");
                    threeNames32.add("大熊猫的鼎盛期");
                    threeNames32.add("巴氏大熊猫的简介");
                    threeNames32.add("郧县人化石的发现");
                    threeNames32.add("北京人的特点");
                    threeNames32.add("大熊猫的衰败期");
                    threeNames32.add("和县人化石的发现");
                    threeNames32.add("柳江人化石的发现");
                    threeNames32.add("简单介绍下巴氏大熊猫的体态特征");
                    threeNames32.add("为什么大熊猫是稀有动物");
                    threeNames32.add("大熊猫的存留现状");
                    threeNames32.add("大熊猫的现存数量");
                    threeNames32.add("大熊猫的天敌");
                    threeNames32.add("大熊猫的演化过程");
                    threeNames32.add("介绍一下小种大熊猫");
                    secondItem32.threeNames = threeNames32;
                    PandaBean.SecondItem secondItem33 = new PandaBean.SecondItem();
                    secondItem33.secondName = "3.与人类同行";
                    ArrayList<String> threeNames331 = new ArrayList<>();
                    threeNames331.add("人类兴起与大熊猫衰退的关系");
                    secondItem33.threeNames = threeNames331;
                    secondItems3.add(secondItem31);
                    secondItems3.add(secondItem32);
                    secondItems3.add(secondItem33);
                    pandaBean3.secondItems = secondItems3;
                    pandaBeans.add(pandaBean3);

                    PandaBean pandaBean4 = new PandaBean();
                    pandaBean4.oneName = "第三单元 保护大熊猫";
                    ArrayList<PandaBean.SecondItem> secondItems4 = new ArrayList<>();
                    PandaBean.SecondItem secondItem41 = new PandaBean.SecondItem();
                    secondItem41.secondName = "1.大熊猫的生存状况";
                    ArrayList<String> threeNames411 = new ArrayList<>();
                    threeNames411.add("野生大熊猫现阶段面临的威胁");
                    threeNames411.add("大熊猫的朋友与天敌");
                    secondItem41.threeNames = threeNames411;
                    PandaBean.SecondItem secondItem42 = new PandaBean.SecondItem();
                    secondItem42.secondName = "2.大熊猫会灭绝吗";
                    ArrayList<String> threeNames42 = new ArrayList<>();
                    threeNames42.add("大熊猫的栖息地与自然保护区");
                    threeNames42.add("大熊猫的遗传多样性");
                    threeNames42.add("什么是竹子开花");
                    threeNames42.add("气候变化对大熊猫的影响");
                    threeNames42.add("人为因素对大熊猫的影响");
                    secondItem42.threeNames = threeNames42;
                    PandaBean.SecondItem secondItem43 = new PandaBean.SecondItem();
                    secondItem43.secondName = "3.如何保护大熊猫";
                    ArrayList<String> threeNames43 = new ArrayList<>();
                    threeNames43.add("大熊猫的自然保护区建设");
                    threeNames43.add("野生大熊猫的分布");
                    threeNames43.add("中国大熊猫数量增长趋势");
                    threeNames43.add("大熊猫的人工饲养和繁育");
                    threeNames43.add("介绍一下中国大熊猫保护研究中心");
                    threeNames43.add("如何处罚偷猎或走私大熊猫的人");
                    threeNames43.add("介绍一下大熊猫国家公园");
                    secondItem43.threeNames = threeNames43;
                    PandaBean.SecondItem secondItem44 = new PandaBean.SecondItem();
                    secondItem44.secondName = "4.大熊猫的身影";
                    ArrayList<String> threeNames44 = new ArrayList<>();
                    threeNames44.add("保护大熊猫，我们能做什么");
                    secondItem44.threeNames = threeNames44;
                    secondItems4.add(secondItem41);
                    secondItems4.add(secondItem42);
                    secondItems4.add(secondItem43);
                    secondItems4.add(secondItem44);
                    pandaBean4.secondItems = secondItems4;
                    pandaBeans.add(pandaBean4);

                    PandaBean pandaBean5 = new PandaBean();
                    pandaBean5.oneName = "第四单元 熊猫大事件";
                    ArrayList<PandaBean.SecondItem> secondItems5 = new ArrayList<>();
                    PandaBean.SecondItem secondItem51 = new PandaBean.SecondItem();
                    secondItem51.secondName = "1.现生熊猫大事纪";
                    ArrayList<String> threeNames51 = new ArrayList<>();
                    threeNames51.add("请介绍下阿尔芒.戴维神父");
                    threeNames51.add("“黑白熊”信件");
                    threeNames51.add("第一个把大熊猫活体带出中国的人");
                    threeNames51.add("请介绍下“中华白熊”");
                    threeNames51.add("简单介绍下“熙熙”");
                    threeNames51.add("美国总统尼克松的夫人参观大熊猫");
                    threeNames51.add("“团团”和“圆圆”");
                    threeNames51.add("大熊猫国家公园管理局");
                    secondItem51.threeNames = threeNames51;
                    PandaBean.SecondItem secondItem52 = new PandaBean.SecondItem();
                    secondItem52.secondName = "2.大熊猫会灭绝吗";
                    ArrayList<String> threeNames52 = new ArrayList<>();
                    threeNames52.add("请介绍下巴氏大熊猫的头骨");
                    threeNames52.add("巴氏大熊猫的论文/研究中国第一件大熊猫化石的论文");
                    threeNames52.add("小种大熊猫的下颌骨");
                    threeNames52.add("有关小种大熊猫论文");
                    threeNames52.add("有关武陵山大熊猫的上颌骨");
                    threeNames52.add("有关武陵山大熊猫论文");
                    threeNames52.add("请介绍下禄丰始熊猫的颊齿");
                    threeNames52.add("有关禄丰始熊猫的论文");
                    threeNames52.add("有关巴氏大熊猫的头骨（亚化石）");
                    threeNames52.add("有关都督大熊猫书籍");
                    secondItem52.threeNames = threeNames52;
                    PandaBean.SecondItem secondItem53 = new PandaBean.SecondItem();
                    secondItem53.secondName = "3.剑齿象动物群";
                    ArrayList<String> threeNames53 = new ArrayList<>();
                    threeNames53.add("大熊猫—剑齿象动物群简介");
                    threeNames53.add("东方剑齿象的地质年代");
                    threeNames53.add("东方剑齿象产地");
                    threeNames53.add("东方剑齿象简介");
                    threeNames53.add("巴氏大熊猫地质年代");
                    threeNames53.add("巴氏大熊猫产地");
                    threeNames53.add("巴氏大熊猫的简介");
                    threeNames53.add("似巴氏剑齿虎的地质年代");
                    threeNames53.add("似巴氏剑齿虎的产地");
                    threeNames53.add("似巴氏剑齿虎的简介");
                    secondItem53.threeNames = threeNames53;
                    PandaBean.SecondItem secondItem54 = new PandaBean.SecondItem();
                    secondItem54.secondName = "4.化石珍品室";
                    ArrayList<String> threeNames54 = new ArrayList<>();
                    threeNames54.add("介绍一下巴氏大熊猫骨架化石");
                    threeNames54.add("都督大熊猫头骨亚化石的地质年代");
                    threeNames54.add("都督大熊猫头骨亚化石的产地");
                    threeNames54.add("都督大熊猫头骨亚化石的简介");
                    threeNames54.add("江东大熊猫头骨亚化石的地质年代");
                    threeNames54.add("江东大熊猫头骨亚化石的产地");
                    threeNames54.add("江东大熊猫头骨亚化石的简介");
                    secondItem54.threeNames = threeNames54;
                    secondItems5.add(secondItem51);
                    secondItems5.add(secondItem52);
                    secondItems5.add(secondItem53);
                    secondItems5.add(secondItem54);
                    pandaBean5.secondItems = secondItems5;
                    pandaBeans.add(pandaBean5);

                    PandaBean pandaBean6 = new PandaBean();
                    pandaBean6.oneName = "第五单元 大熊猫基本信息";
                    ArrayList<PandaBean.SecondItem> secondItems6 = new ArrayList<>();
                    PandaBean.SecondItem secondItem61 = new PandaBean.SecondItem();
                    secondItem61.secondName = "1.基本简介";
                    ArrayList<String> threeNames61 = new ArrayList<>();
                    threeNames61.add("大熊猫“新妮儿”");
                    threeNames61.add("大熊猫像熊？像猫？");
                    threeNames61.add("大熊猫—剑齿象动物群简介");
                    threeNames61.add("请简单介绍下东方剑齿象");
                    threeNames61.add("请简单介绍下巴氏大熊猫");
                    threeNames61.add("请简单介绍下似巴氏剑齿虎");
                    threeNames61.add("请简单介绍下始熊猫");
                    threeNames61.add("请简单介绍下小种大熊猫");
                    threeNames61.add("请简单介绍下武陵山大熊猫");
                    threeNames61.add("请简单介绍下巴氏大熊猫");
                    threeNames61.add("请简单介绍下巴氏大熊猫骨架化石");
                    threeNames61.add("请简单介绍下都督大熊猫头骨亚化石");
                    threeNames61.add("请简单介绍下江东大熊猫头骨亚化石");
                    threeNames61.add("请简单介绍下大熊猫、棕熊骨骼塑化标本");
                    threeNames61.add("请简单介绍下大熊猫、棕熊肌肉塑化标本");
                    threeNames61.add("请简单介绍下竹子");
                    threeNames61.add("请简单介绍下头骨");
                    threeNames61.add("请简单介绍下大熊猫内脏塑化标本");
                    threeNames61.add("请简单介绍下大熊猫前肢骨骼模型");
                    threeNames61.add("请简单介绍下大熊猫幼仔");
                    threeNames61.add("请简单介绍下大熊猫粪便");
                    threeNames61.add("请简单介绍下禄丰始熊猫骨架模型");
                    threeNames61.add("请简单介绍下禄丰古猿头骨模型");
                    threeNames61.add("请简单介绍下小种大熊猫骨架模型");
                    threeNames61.add("请简单介绍下巫山人牙齿");
                    threeNames61.add("请简单介绍下武陵山大熊猫骨架模型");
                    threeNames61.add("请简单介绍下古爪哇魁人臼齿");
                    threeNames61.add("请简单介绍下巴氏大熊猫骨架模型");
                    threeNames61.add("请简单介绍下郧县人头盖骨");
                    threeNames61.add("请简单介绍下北京人头盖骨");
                    threeNames61.add("请简单介绍下现生大熊猫骨架模型");
                    threeNames61.add("请简单介绍下和县人头骨");
                    threeNames61.add("请简单介绍下柳江人头骨");
                    threeNames61.add("请简单介绍下大熊猫及伴生动物场景");
                    secondItem61.threeNames = threeNames61;
                    secondItems6.add(secondItem61);
                    pandaBean6.secondItems = secondItems6;
                    pandaBeans.add(pandaBean6);

                    mRecyclerViewData.setLayoutManager(new LinearLayoutManager(this));
                    PandaQuestionAdapter pandaQuestionAdapter = new PandaQuestionAdapter(this, pandaBeans, mPresenter);
                    mRecyclerViewData.setAdapter(pandaQuestionAdapter);
                    break;
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        mAnswerItems.clear();
        destroyTimer();
    }

    /**
     * 点击事件
     * @param view
     */
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_main_title1:
            case R.id.tv_main_title2:
            case R.id.tv_main_title3:
            case R.id.tv_main_title4:
            case R.id.tv_main_title5:
            case R.id.tv_main_title6:
            case R.id.tv_main_title7:
                String question = ((TextView) view).getText().toString();
                mPresenter.sendQuestion(question);
                break;
        }
    }

    /**
     * 设置问题
     * @param questions
     */
    private void setQuestion(String [] questions){
        ArrayList<String> results = RandomUtils.getRandomArray(7, questions);
        mTextViewTitle1.setText(results.get(0));
        mTextViewTitle2.setText(results.get(1));
        mTextViewTitle3.setText(results.get(2));
        mTextViewTitle4.setText(results.get(3));
        mTextViewTitle5.setText(results.get(4));
        mTextViewTitle6.setText(results.get(5));
        mTextViewTitle7.setText(results.get(6));
    }

    /**
     * 添加问题跟答案到集合后刷新适配器
     * @param question
     * @param answerItem
     */
    @Override
    public synchronized void notifyChatData(final String question, AnswerBean.AnswerItem answerItem) {
        synchronized (mAnswerItems){
            if (mAnswerItems.size() > 51){
                mAnswerItems.clear();
                addDefaultWelcome();
            }
        }
        //刷新适配器
        AnswerBean.AnswerItem questionItem = new AnswerBean.AnswerItem();
        questionItem.viewType = ChatViewTypeConstant.VIEWTYPE_QUESTION;
        questionItem.Question = question;
        mAnswerItems.add(questionItem);
        mAnswerItems.add(answerItem);
        if (mAnswerItems.size() > 2){
            mChatAdapter.notifyItemRangeInserted(mAnswerItems.size() - 2,2);
        }else{
            mChatAdapter.notifyDataSetChanged();
        }
        mRecyclerViewChat.post(new Runnable() {
            @Override
            public void run() {
                if (mAnswerItems.size() != 0){
                    linearSmoothScroller.setTargetPosition(mAnswerItems.size() - 1);
                    mChatLayoutManager.startSmoothScroll(linearSmoothScroller);
                }
            }
        });
        //语音播报
        mPresenter.speechResult(answerItem);
        //如果是文物问答，则弹出窗口
        if (answerItem.viewType == ChatViewTypeConstant.VIEWTYPE_COLLECTION){
            new CollectionDialog(this, answerItem).show();
        }
        //提交有无问题答案
        if (answerItem.viewType == ChatViewTypeConstant.VIEWTYPE_ROBOT && !answerItem.orignalQuestion.contains("天气")){
            MuseumApi.getInstance().submitQa(mPresenter, this, answerItem.orignalQuestion, "no");
        }else{
            MuseumApi.getInstance().submitQa(mPresenter, this, answerItem.orignalQuestion, "yes");
        }
        MuseumApi.getInstance().submit(mPresenter,this, answerItem.orignalQuestion);
    }

    /**
     * 滚动到指定position
     * @param position
     */
    public void smoothToPosition(final int position){
        mRecyclerViewChat.post(new Runnable() {
            @Override
            public void run() {
                if (mAnswerItems.size() > position){
                    linearSmoothScroller.setTargetPosition(position);
                    mChatLayoutManager.startSmoothScroll(linearSmoothScroller);
                }
            }
        });
    }

    @Override
    public void onEvenBusCallBack(BaseEvenBusBean baseEvenBusBean) {
        super.onEvenBusCallBack(baseEvenBusBean);
        if (baseEvenBusBean != null && !TextUtils.isEmpty(baseEvenBusBean.getTag())){
            switch (baseEvenBusBean.getTag()){
                case EvenBusConstant.EVENBUS_ROBOTQUESTION://接收到来自机器人的问答
                    String question = (String) baseEvenBusBean.getObject();
                    if (!TextUtils.isEmpty(question)){
                        startTimer();
                        //发送问题
                        mPresenter.sendQuestion(question);
                    }
                    break;
            }
        }
    }

    /**
     * 添加默认欢迎
     */
    public void addDefaultWelcome(){
        if (mAnswerItems != null && mAnswerItems.size() == 0){
            AnswerBean.AnswerItem questionItem = new AnswerBean.AnswerItem();
            questionItem.viewType = ChatViewTypeConstant.VIEWTYPE_DEFAULT;
            questionItem.Answer = "您好，有什么可以帮助您的。";
            mAnswerItems.add(questionItem);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //点击了屏幕，将计时器置为初始状态
        switch (ev.getAction()){
            case MotionEvent.ACTION_UP://手指抬起
                startTimer();
                break;
            default:
                closeTimer();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 开启定时器
     */
    public void startTimer(){
        if (mTimer != null){
            mTimer.start();
        }
    }

    /**
     * 关闭定时器
     */
    public void closeTimer(){
        if (mTimer != null){
            mTimer.cancel();
        }
    }

    /**
     * 销毁定时器
     */
    public void destroyTimer(){
        if (mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
    }
}
