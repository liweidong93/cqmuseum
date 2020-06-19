package com.cnki.cqmuseum.ui.home;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.adapter.PandaHomeQuestionAdapter;
import com.cnki.cqmuseum.base.BaseActivity;
import com.cnki.cqmuseum.bean.BaseEvenBusBean;
import com.cnki.cqmuseum.bean.Guide;
import com.cnki.cqmuseum.bean.UpdateApkBean;
import com.cnki.cqmuseum.constant.EvenBusConstant;
import com.cnki.cqmuseum.constant.IntentActionConstant;
import com.cnki.cqmuseum.constant.QuestionListConstant;
import com.cnki.cqmuseum.manager.ActivityViewManager;
import com.cnki.cqmuseum.manager.FloatButtonManager;
import com.cnki.cqmuseum.manager.LooperLayoutManager;
import com.cnki.cqmuseum.manager.RobotManager;
import com.cnki.cqmuseum.permission.FloatPermissionManager;
import com.cnki.cqmuseum.ui.chat.ChatActivity;
import com.cnki.cqmuseum.ui.collection.CollectDetailActivity;
import com.cnki.cqmuseum.ui.collection.CollectionActivity;
import com.cnki.cqmuseum.ui.guide.GuideActivity;
import com.cnki.cqmuseum.ui.speechserver.SpeechServerActivity;
import com.cnki.cqmuseum.ui.updateapk.UpdateApkActivity;
import com.cnki.cqmuseum.updateapk.ApkConstant;
import com.cnki.cqmuseum.updateapk.UpdateApkService;
import com.cnki.cqmuseum.utils.ImageUtils;
import com.cnki.cqmuseum.utils.LogUtils;
import com.cnki.cqmuseum.utils.Md5Utils;
import com.cnki.cqmuseum.utils.NetUtils;
import com.cnki.cqmuseum.utils.RandomUtils;
import com.cnki.cqmuseum.utils.TextStyleUtils;
import com.cnki.cqmuseum.view.PasswordDialog;
import com.cnki.cqmuseum.view.VolumeDialogUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * 重庆自然博物馆首页
 * create by liweidong at 2019.10.28
 */
public class HomeActivity extends BaseActivity<HomePresenter> implements IHomeView {

    private HomePresenter mPresenter;
    private Handler mHandler = new Handler();
    //讲解服务图标
    private ImageView mImageViewSpeakServer;
    //左侧自动轮播问题列表
    private RecyclerView mRecyclerViewQueations;
    private ArrayList<String> mQuestions;//问题集合
    private LooperLayoutManager mLayoutManager;//自定义布局管理器,可回收不可见的view
    private PandaHomeQuestionAdapter mAdapter;//问题适配器
    private volatile boolean isNeedStop = false;//判断轮训示例问题线程是否需要停止
    private volatile boolean isFinishThread = false;//判断轮训示例问题线程是否需要停止
    //控制自动轮播线程
    private QuestionLoopThread mQueationLoopThread;
    //大熊猫图标
    private ImageView mImageViewPanda;
    public static String localhostIp;


    @Override
    public HomePresenter initPresenter() {
        mPresenter = new HomePresenter(this);
        return mPresenter;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public void initView() {
        //初始化控件
        mImageViewSpeakServer = findViewById(R.id.iv_home_speakserver);
        mRecyclerViewQueations = findViewById(R.id.rv_home_questionlist);
        mImageViewPanda = findViewById(R.id.iv_home_panda);
        //给熊猫icon设置光晕
//        Bitmap bitmap = ImageUtils.addHaloToImage(this, R.mipmap.icon_panda, R.color.color_65059B76, 0);
//        if (bitmap != null){
//            mImageViewPanda.setImageBitmap(bitmap);
//        }
        mImageViewPanda.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new PasswordDialog(HomeActivity.this, new PasswordDialog.OnPasswordCallBack() {
                    @Override
                    public void exit() {
                        showRobotBtn();
                    }
                }).show();
                return true;
            }
        });
        //跳转到讲解服务
        mImageViewSpeakServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SpeechServerActivity.class));
            }
        });
    }

    @Override
    public void initData() {
        //隐藏系统菜单
        hideRobotBtn();
        //设置示例问题列表数据
        mQuestions = RandomUtils.getRandomArray(5, QuestionListConstant.pandaQuestions);
        mAdapter = new PandaHomeQuestionAdapter(this, mQuestions, new PandaHomeQuestionAdapter.OnQuestionItemClickListener() {
            @Override
            public void onClick(String question) {
                Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                intent.putExtra(IntentActionConstant.ACTION_QUESTION,question);
                startActivity(intent);
            }
        });
        //给轮播列表添加适配器
        mRecyclerViewQueations.setAdapter(mAdapter);
        mLayoutManager = new LooperLayoutManager();
        mLayoutManager.setLooperEnable(true);//开启
        mRecyclerViewQueations.setLayoutManager(mLayoutManager);
        //禁止手动滑动
        mRecyclerViewQueations.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        //开启线程,使recycleView拥有跑马灯效果
        mQueationLoopThread = new QuestionLoopThread();
        mQueationLoopThread.start();
        //开启讲解服务动画
        mPresenter.setSpeakServerAnim(mImageViewSpeakServer);
        //开启检查apk更新服务
        startService(new Intent(this, UpdateApkService.class));
        //延迟2s开启悬浮窗服务
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isPermission = FloatPermissionManager.getInstance().applyFloatWindow(mContext);
                //有对应权限或者系统版本小于7.0
                if (isPermission || Build.VERSION.SDK_INT < 24) {
                    //开启悬浮窗
                    FloatButtonManager.getInstance().startFloatServer(mContext);
                }
            }
        },2000);
        //获取机器人ip
        localhostIp = NetUtils.getIPAddress(this);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        RobotManager.isListen = true;
        //隐藏系统菜单
        hideRobotBtn();
        isNeedStop = false;
        //每次进入，获取随机常见问题
        mQuestions = RandomUtils.getRandomArray(5, QuestionListConstant.pandaQuestions);
        mAdapter.updateData(mQuestions);
        //隐藏掉悬浮窗窗口
        FloatButtonManager.getInstance().hide();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isNeedStop = true;
    }

    /**
     * button的点击事件
     * @param view
     */
    public void onClick(View view){
        Intent intent = null;
        switch (view.getId()){
            case R.id.iv_home_pandatime://熊猫时代的点击事件
                intent = new Intent(HomeActivity.this, ChatActivity.class);
                intent.putExtra(IntentActionConstant.ACTIONTYPE_PANDAFROMTYPE, IntentActionConstant.PANDATYPE_PANDATIME);
                break;
            case R.id.iv_home_guide://导览的点击事件
                intent = new Intent(HomeActivity.this, GuideActivity.class);
                break;
            case R.id.iv_home_collection://文物精品的点击事件
                intent = new Intent(HomeActivity.this, CollectionActivity.class);
                break;
            case R.id.iv_home_help://馆厅助手的点击事件
                intent = new Intent(HomeActivity.this, ChatActivity.class);
                intent.putExtra(IntentActionConstant.ACTIONTYPE_PANDAFROMTYPE, IntentActionConstant.PANDATYPE_GUIDE);
                break;
            case R.id.iv_home_speakserver://讲解服务的点击事件
                intent = new Intent(HomeActivity.this, SpeechServerActivity.class);
                break;
        }
        if (intent != null){
            startActivity(intent);
        }
    }

    @Override
    public void onEvenBusCallBack(BaseEvenBusBean baseEvenBusBean) {
        if (baseEvenBusBean != null && !TextUtils.isEmpty(baseEvenBusBean.getTag())){
            switch (baseEvenBusBean.getTag()){
                case EvenBusConstant.EVENBUS_UPDATEAPK://更新apk通知处理
                    UpdateApkBean updateApkBean = (UpdateApkBean) baseEvenBusBean.getObject();
                    if (updateApkBean != null){
                        String apkName = ApkConstant.APK_STORAGEPATH + TextStyleUtils.isStrEmpty(updateApkBean.result.projectName) + TextStyleUtils.isStrEmpty(updateApkBean.result.appId)
                                + TextStyleUtils.isStrEmpty(updateApkBean.result.versionName) + ".apk";
                        //获取下载到文件的md5值
                        String fileMD5 = Md5Utils.getFileMD5(new File(apkName));
                        //判断如果下载的文件md5值跟服务器原始的md5值不同的话，则重新下载
                        if (!TextUtils.isEmpty(fileMD5) && fileMD5.equals(updateApkBean.result.md5)){
                            //进入更新界面，并且显示系统菜单
                            showRobotBtn();
                            Intent intent = new Intent(HomeActivity.this, UpdateApkActivity.class);
                            intent.putExtra(IntentActionConstant.ACTIONTYPE_UPDATECONTENT, updateApkBean.result.projectName + "\n版本号：" + updateApkBean.result.versionCode);
                            intent.putExtra(IntentActionConstant.ACTIONTYPE_UPDATEFILENAME,apkName);
                            startActivity(intent);
                        }else{
                            //重新开启服务进行下载
                            LogUtils.e("下载apk包不完整");
                            startService(new Intent(this, UpdateApkService.class));
                        }
                    }
                    break;
                case EvenBusConstant.EVENBUS_STARTGUIDE://跳转到导览界面
                    String topActivity = ActivityViewManager.getInstance().getTopActivity(this);
                    if (!topActivity.equals("com.cnki.cqmuseum.ui.guide.GuideActivity")){
                        startActivity(new Intent(HomeActivity.this, GuideActivity.class));
                    }
                    break;
            }
        }
    }

    @Override
    public void finish() {
        //取消动画
        mPresenter.cancelAnimator();
        //界面关闭，关闭线程执行
        isFinishThread = true;
        //注销悬浮窗服务
        FloatButtonManager.getInstance().stopFloatServer(this);
//        RobotManager.destorySpeachService();
        super.finish();
    }

    /**
     * 轮询示例问题线程
     */
    class QuestionLoopThread extends Thread {

        public QuestionLoopThread() {
        }
        @Override
        public void run() {
            super.run();
            while (!isFinishThread){
                try {
                    Thread.sleep(5000);
                    if (!isNeedStop){
                        HomeActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerViewQueations.smoothScrollBy(0,80);
                                mRecyclerViewQueations.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdapter.changeData();
                                        mAdapter.isChange(true);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
