package com.cnki.cqmuseum.ui.updateapk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnki.cqmuseum.R;
import com.cnki.cqmuseum.constant.IntentActionConstant;
import com.cnki.cqmuseum.utils.PackageUtils;

import java.io.File;

public class UpdateApkActivity extends Activity {

    private TextView mTextViewUpdate;
    private ImageView mImageViewUpdate;
    private ImageView mImageViewClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_apk);
        mTextViewUpdate = findViewById(R.id.tv_updateapk_updatecontent);
        mImageViewUpdate = findViewById(R.id.iv_updateapk_update);
        mImageViewClose = findViewById(R.id.iv_updateapk_close);
        Intent intent = getIntent();
        String updateContent = intent.getStringExtra(IntentActionConstant.ACTIONTYPE_UPDATECONTENT);
        if (!TextUtils.isEmpty(updateContent)){
            mTextViewUpdate.setText(updateContent);
        }
        final String fileName = intent.getStringExtra(IntentActionConstant.ACTIONTYPE_UPDATEFILENAME);
        mImageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateApkActivity.this.finish();
            }
        });
        mImageViewUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageUtils.installApk(UpdateApkActivity.this, new File(fileName));
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

}
