package com.tamic.novate;

import android.content.Context;
import android.util.Log;

/**
 * DownSubscriber
 * Created by Tamic on 2016-08-03.
 */
public class DownSubscriber <ResponseBody extends okhttp3.ResponseBody> extends BaseSubscriber<ResponseBody> {
    private DownLoadCallBack callBack;
    private Context context;
    private String path;
    private String name;

    public DownSubscriber(String path, String name, DownLoadCallBack callBack, Context context) {
        super(context);
        this.path = path;
        this.name = name;
        this.callBack = callBack;
        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (callBack != null) {
            callBack.onStart();
        }
    }

    @Override
    public void onCompleted() {
        if (callBack != null) {
            callBack.onCompleted();
        }
    }

    @Override
    public void onError(final Throwable e) {
        Log.d( NovateDownLoadManager.TAG, "DownSubscriber:>>>> onError:" + e.getMessage());
        callBack.onError(e);
    }

    @Override
    public void onNext(ResponseBody responseBody) {

        Log.d(NovateDownLoadManager.TAG, "DownSubscriber:>>>> onNext");

        NovateDownLoadManager.getInstance(callBack).writeResponseBodyToDisk(path, name, context, responseBody);

    }
}
