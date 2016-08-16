package com.tamic.novate;

import android.content.Context;
import android.util.Log;

import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * DownSubscriber
 * Created by Tamic on 2016-08-03.
 */
public class DownSubscriber <ResponseBody extends okhttp3.ResponseBody> extends Subscriber<ResponseBody> {
    private DownLoadCallBack callBack;
    private Context context;

    public DownSubscriber(DownLoadCallBack callBack, Context context) {
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
    public void onError(Throwable e) {
        Log.d( NovateDownLoadManager.TAG, "DownSubscriber:>>>> onError:" + e.getMessage());
        if (callBack != null) {
            callBack.onError(e);
        }
    }

    @Override
    public void onNext(ResponseBody responseBody) {

        Log.d(NovateDownLoadManager.TAG, "DownSubscriber:>>>> onNext");

        NovateDownLoadManager.getInstance(callBack).writeResponseBodyToDisk(context, responseBody);

    }
}
