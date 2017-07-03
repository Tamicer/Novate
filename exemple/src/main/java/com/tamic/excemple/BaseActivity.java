package com.tamic.excemple;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by LIUYONGKUI726 on 2017-07-03.
 */

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    String baseUrl = "http://ip.taobao.com/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initProgress(this);
    }

    /**
     * 初始化进度条
     */
    public void initProgress(Context aContext) {

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(aContext);
        }

        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle("温馨提示");
        mProgressDialog.setMax(100);
        mProgressDialog.setMessage("novate正在续传中...");
        mProgressDialog.setCancelable(true);

    }

    public void showPressDialog() {

        if (mProgressDialog == null || this.isFinishing()) {
            return;
        }

        mProgressDialog.show();
        mProgressDialog.setProgress(0);
    }


    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public  void updateProgressDialog(int progress) {

        if (mProgressDialog != null) {
            if (!mProgressDialog.isShowing()) {
                showPressDialog();
            }
            mProgressDialog.setProgress(progress);
        }
    }

}
