package com.cnki.cqmuseum.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.manager.RobotManager;
import com.cnki.cqmuseum.ui.chat.ChatActivity;

/**
 * 装载webview的dialog
 * Created by admin on 2019/6/10.
 */

public class WebViewDialog extends Dialog {

    private Context mContext;
    private String url;
    private RelativeLayout mLayout;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private ImageView mImageView;
    private boolean isStartSpeek;
    private boolean originalListen;

    public WebViewDialog(@NonNull Context context, String url, boolean isStartSpeek) {
        super(context);
        this.mContext = context;
        this.url = url;
        this.isStartSpeek = isStartSpeek;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);//加了才有圆角
        super.onCreate(savedInstanceState);
        originalListen = RobotManager.isListen;
        if (isStartSpeek && originalListen){
            RobotManager.isListen = false;
        }
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.dialog_web, null);
        setContentView(inflate);
        mLayout = inflate.findViewById(R.id.rl_dialog_web);
        mWebView = inflate.findViewById(R.id.wv_webviewdialog_load);
        mProgressBar = inflate.findViewById(R.id.pb_webviewdialog_progress);
        mImageView = inflate.findViewById(R.id.iv_webview_error);
        initWebView();
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.9); // 宽度设置为屏幕的0.8
        lp.height = (int) (d.heightPixels * 0.85); // 高度设置为屏幕的0.4
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
        setCancelable(true);
        //点击图片，重新加载网页
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("about:blank");
                mWebView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                mLayout.setBackgroundResource(R.drawable.chat_bg_left);
                mImageView.setVisibility(View.GONE);
                mWebView.loadUrl(url);
            }
        });
    }

    private void initWebView() {
        //声明WebSettings子类
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webSettings.setSupportZoom(true);
        // 设置出现缩放工具
        webSettings.setBuiltInZoomControls(true);
        //扩大比例的缩放
        webSettings.setUseWideViewPort(true);
        //自适应屏幕
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        // 清除缓存
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.clearFormData();

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress==100){
                    mProgressBar.setVisibility(View.GONE);//加载完网页进度条消失
                }
                else{
                    mProgressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    mProgressBar.setProgress(newProgress);//设置进度值
                }
            }

        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }


            //6.0以上执行
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                mWebView.loadUrl("about:blank");//先加载空白页，防止显示默认错误页面
                mImageView.setVisibility(View.VISIBLE);
                mLayout.setBackgroundResource(R.drawable.coner35_white);
                mWebView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);

            }
            //6.0以下执行
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                mWebView.loadUrl("about:blank");
                mImageView.setVisibility(View.VISIBLE);
                mLayout.setBackgroundResource(R.drawable.coner35_white);
                mWebView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
            }
        });

        mWebView.loadUrl(url);
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_UP:
                ((ChatActivity)mContext).startTimer();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (isStartSpeek && originalListen){
            RobotManager.isListen = true;
        }
    }

}
