package com.cnki.cqmuseum.ui.collection;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.adapter.CollectionListAdapter;
import com.cnki.cqmuseum.adapter.ColletionTypeAdapter;
import com.cnki.cqmuseum.base.BaseActivity;
import com.cnki.cqmuseum.bean.BaseEvenBusBean;
import com.cnki.cqmuseum.bean.CollectionList;
import com.cnki.cqmuseum.constant.EvenBusConstant;
import com.cnki.cqmuseum.constant.IntentActionConstant;
import com.cnki.cqmuseum.manager.FloatButtonManager;
import com.cnki.cqmuseum.ui.home.HomeActivity;
import com.cnki.cqmuseum.utils.LogUtils;
import com.cnki.cqmuseum.utils.StatuBarUtils;
import com.cnki.cqmuseum.utils.ToastUtils;
import com.cnki.cqmuseum.view.VolumeDialogUtils;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 精品馆藏界面
 * Created by liweidong on 2019/11/6.
 */

public class CollectionActivity extends BaseActivity<CollectionPresenter> implements ICollectionView{

    private CollectionPresenter mPresenter;
    private RecyclerView mRecyclerViewType;
    public static String mSelectType = "";
    private ColletionTypeAdapter mTypeAdapter;
    private ImageView mImageViewBack;
    private EditText mEditTextInput;
    private ImageView mImageViewVoice;
    private VolumeDialogUtils mVolumeDialogUtils;
    private ImageView mImageViewDelete;
    private EasyRecyclerView mEasyRecyclerView;
    private int curPage;//当前页
    private CollectionListAdapter mCollectionAdapter;
    public static boolean isPressDown = false;
    private String tempResult = "";
    private int volumn;
    private static final int MSG_SETVALUE = 1;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_SETVALUE:
                    isPressDown = false;
                    LogUtils.e("robot","识别到的文物结果为：" + tempResult + "\nispressdown:" + isPressDown);
                    mEditTextInput.setText(tempResult);
                    break;
            }
        }
    };
    private ExecutorService cachedThreadPool;

    @Override
    public CollectionPresenter initPresenter() {
        mPresenter = new CollectionPresenter(this);
        return mPresenter;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_collection;
    }

    @Override
    public void initView() {
        mRecyclerViewType = findViewById(R.id.rv_collection_type);
        mImageViewBack = findViewById(R.id.iv_collection_back);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CollectionActivity.this.finish();
            }
        });
        mEditTextInput = findViewById(R.id.et_collection_input);
        mEditTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                LogUtils.e("robot","输入的文字:" + editable.toString());
                if (mImageViewDelete.getVisibility() == View.GONE && !TextUtils.isEmpty(editable.toString())){
                    mImageViewDelete.setVisibility(View.VISIBLE);
                }else if (mImageViewDelete.getVisibility() == View.VISIBLE && TextUtils.isEmpty(editable.toString())){
                    mImageViewDelete.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(mSelectType) && !TextUtils.isEmpty(editable.toString())){
                    mSelectType = "";
                    mTypeAdapter.notifyDataSetChanged();
                }
                if (!TextUtils.isEmpty(editable.toString().trim())){
                    mCollectionAdapter.clear();
//                    mCollectionAdapter.notifyDataSetChanged();
                    mPresenter.askQuestion(editable.toString().trim());
                }else if (TextUtils.isEmpty(editable.toString().trim()) && TextUtils.isEmpty(mSelectType)){
                    //获取全部
                    curPage = 0;
                    mPresenter.getCollectionListByType("", curPage);
                }
            }
        });
        mVolumeDialogUtils = new VolumeDialogUtils(this);
        mImageViewVoice = findViewById(R.id.iv_collection_voice);
        mImageViewVoice.setOnTouchListener(new View.OnTouchListener() {

            private volatile boolean isStart;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //移除消息
                        mHandler.removeMessages(MSG_SETVALUE);
                        isPressDown = true;
                        tempResult = "";
                        isStart = true;
                        //按下事件
                        if (mVolumeDialogUtils == null){
                            mVolumeDialogUtils = new VolumeDialogUtils(CollectionActivity.this);
                        }
                        mVolumeDialogUtils.show();
                        //开启线程进行音量控制
                        cachedThreadPool.submit(new Runnable() {
                            @Override
                            public void run() {
                                volumn = 0;
                                while (isStart){
                                    volumn++;
                                    try {
                                        Thread.sleep(300);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mVolumeDialogUtils.setVolumeIcon(volumn);
                                        }
                                    });
                                }
                            }
                        });
                        break;
                    case MotionEvent.ACTION_UP:
                        mHandler.sendEmptyMessageDelayed(MSG_SETVALUE,2000);
                        isStart = false;
                        //抬起事件
                        mVolumeDialogUtils.dismiss();
                        break;
                }
                return true;
            }
        });
        mImageViewDelete = findViewById(R.id.iv_collection_delete);
        mImageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditTextInput.setText("");
                mImageViewDelete.setVisibility(View.GONE);
                curPage = 0;
                mPresenter.getCollectionListByType("", curPage);
            }
        });
        mEasyRecyclerView = findViewById(R.id.easyrv_collection_view);
    }

    @Override
    public void initData() {
        cachedThreadPool = Executors.newCachedThreadPool();
        FloatButtonManager.getInstance().show();
        isPressDown = false;
        //设置文物列表适配器
        mEasyRecyclerView.setLayoutManager(new GridLayoutManager(mContext,3));
        mEasyRecyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                curPage = 0;
                if (TextUtils.isEmpty(mEditTextInput.getText().toString())){
                    mPresenter.getCollectionListByType(mSelectType, curPage);
                }else{
                    mCollectionAdapter.stopMore();
                    mCollectionAdapter.clear();
//                    mCollectionAdapter.notifyDataSetChanged();
                    mPresenter.askQuestion(mEditTextInput.getText().toString().trim());
                }
            }
        });
        mCollectionAdapter = new CollectionListAdapter(mContext);
        mCollectionAdapter.setMore(R.layout.view_more, new RecyclerArrayAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                curPage++;
                mPresenter.getCollectionListByType(mSelectType, curPage);
            }
        });
        mCollectionAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                CollectionList.CollectionItem collectionItem = mCollectionAdapter.getAllData().get(position);
                Intent intent = new Intent(CollectionActivity.this, CollectDetailActivity.class);
                intent.putExtra(IntentActionConstant.ACTION_COLLECTION_DATA, collectionItem);
                startActivity(intent);
            }
        });
//        mCollectionAdapter.setNoMore(R.layout.view_nomore);
        mEasyRecyclerView.setAdapter(mCollectionAdapter);
        //重置选中类型
        mSelectType = "";
        //将分类放到适配器中
        String[] types = new String[]{"古生物","古人类","恐龙","哺乳类","鸟类","两栖爬行类","鱼类","昆虫","无脊椎动物","植物","岩矿类"};
        mTypeAdapter = new ColletionTypeAdapter(this, types, new ColletionTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick() {
                mEditTextInput.setText("");
                mCollectionAdapter.clear();
//                mCollectionAdapter.notifyDataSetChanged();
                curPage = 0;
                mPresenter.getCollectionListByType(mSelectType, curPage);
            }
        });
        mRecyclerViewType.setLayoutManager(new GridLayoutManager(this,2));
        mRecyclerViewType.setAdapter(mTypeAdapter);

        //初次获取所有文物
        mPresenter.getCollectionListByType("",0);
    }

    @Override
    public void onSuccess(int total, ArrayList<CollectionList.CollectionItem> collectionItems) {
        if (curPage == 0){
            //代表刷新
            mCollectionAdapter.clear();
        }
        mCollectionAdapter.addAll(collectionItems);
        if (!TextUtils.isEmpty(mEditTextInput.getText().toString())){
            mCollectionAdapter.pauseMore();
//            mCollectionAdapter.resumeMore();
        }
    }

    @Override
    public void onFailed() {
        curPage--;
        mCollectionAdapter.pauseMore();
        mCollectionAdapter.resumeMore();
    }

    @Override
    public void noEmpty() {
        mCollectionAdapter.pauseMore();
        ToastUtils.toast(CollectionActivity.this,"没有查到相关结果！！！");
    }

    @Override
    public void onFinish() {
        mCollectionAdapter.stopMore();
    }

    @Override
    public void onEvenBusCallBack(BaseEvenBusBean baseEvenBusBean) {
        super.onEvenBusCallBack(baseEvenBusBean);
        switch (baseEvenBusBean.getTag()){
            case EvenBusConstant.EVENBUS_COLLECTIONTEXT:
                String result = (String) baseEvenBusBean.getObject();
                tempResult += result;
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        FloatButtonManager.getInstance().hide();
    }

    @Override
    public void paddingStatusBar() {
        findViewById(R.id.rl_collection_stub1).setPadding(0, StatuBarUtils.getStatusBarHeight(this), 0 , 0);
        findViewById(R.id.ll_collection_stub2).setPadding(0, StatuBarUtils.getStatusBarHeight(this), 0 , 0);
    }
}
