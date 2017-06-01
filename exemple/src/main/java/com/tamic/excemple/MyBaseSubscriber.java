package com.tamic.excemple;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.tamic.novate.BaseSubscriber;
import com.tamic.novate.Throwable;
import com.tamic.novate.util.NetworkUtil;

/**
 * Created by LIUYONGKUI726 on 2017-06-01.
 */

public abstract class MyBaseSubscriber<T>  extends BaseSubscriber<T> {

    private ProgressDialog progress;

    private Context context;

    public MyBaseSubscriber(Context context) {
        super(context);
        this.context = context;
        progress = new ProgressDialog(context);
        progress.setMessage("novate拼命加载中....");

    }


    @Override
    public void onStart() {
        super.onStart();

        if (!NetworkUtil.isNetworkAvailable(context)) {
            Toast.makeText( context, "似乎没网O",Toast.LENGTH_SHORT).show();
            onCompleted();
            return;
        }
        if (progress != null){
            if (progress.isShowing()) {
                progress.dismiss();
            }
            progress.show();
        }


    }

    @Override
    public void onCompleted() {
        super.onCompleted();

        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }
}
