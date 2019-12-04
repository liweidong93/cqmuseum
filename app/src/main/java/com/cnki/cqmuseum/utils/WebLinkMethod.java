package com.cnki.cqmuseum.utils;

import android.content.Context;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.widget.TextView;

import com.cnki.cqmuseum.ui.chat.ChatActivity;
import com.cnki.cqmuseum.view.PhotoViewDialog;
import com.cnki.cqmuseum.view.WebViewDialog;


public class WebLinkMethod extends LinkMovementMethod {

    private Context mContext;
    private WebViewDialog webViewDialog;
    private String baseUrl;

    public WebLinkMethod(Context context, String baseUrl) {
        this.mContext = context;
        this.baseUrl = baseUrl;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);
            URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
            if (link.length > 0) {
                webViewDialog = new WebViewDialog(mContext, link[0].getURL(), true);
            }
            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    if(mContext != null) {
                        if (!((ChatActivity)mContext).isFinishing()) {
                            if (!TextUtils.isEmpty(link[0].getURL()) && !link[0].getURL().contains(".jpg") && !link[0].getURL().contains(".png")
                                    && !link[0].getURL().contains(".apk")){
                                webViewDialog.show();
                            }else if (link[0].getURL().toLowerCase().endsWith(".png") || link[0].getURL().toLowerCase().endsWith(".jpg")){
                                //如果url是以图片后缀结尾显示photoview
                                LogUtils.e("显示图片：" + link[0].getURL());
                                new PhotoViewDialog(mContext, baseUrl + link[0].getURL()).show();
                            }
                        }
                    }
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]));
                }
                return true;
            } else {
                Selection.removeSelection(buffer);
            }
        }
        return super.onTouchEvent(widget, buffer, event);
    }
}