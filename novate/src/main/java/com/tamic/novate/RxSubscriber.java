/*
 *    Copyright (C) 2017 Tamic
 *
 *    link :https://github.com/Tamicer/Novate
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.tamic.novate;

import com.tamic.novate.callback.ResponseCallback;
import com.tamic.novate.exception.NovateException;

import okhttp3.ResponseBody;

/**
 * RXSubscriber RxSubscribe
 * Created by Tamic on 2017/5/23.
 */

public class RxSubscriber<T, E> extends BaseSubscriber<ResponseBody>{

    private ResponseCallback<T, E> callBack;
    private Object tag = null;

    public RxSubscriber(Object tag, ResponseCallback<T, E> callBack) {
        super();
        if (callBack == null) {
            this.callBack = ResponseCallback.CALLBACK_DEFAULT;
        }
        callBack.setTag(tag);
        this.callBack = callBack;
        this.tag = tag;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (callBack != null) {
            callBack.onStart(tag);
        }

    }

    @Override
    public void onCompleted() {
        if (callBack != null) {
            callBack.onCompleted(tag);
            callBack.onRelease();
        }
    }

    @Override
    public void onError(Throwable e) {
        if (callBack != null) {
            callBack.onError(tag, e);
            callBack.onRelease();
        }
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        try {
            callBack.onNext(tag, null, callBack.onHandleResponse(responseBody));

        } catch (Exception e) {
            e.printStackTrace();
            if (callBack != null) {
                callBack.onError(tag, NovateException.handleException(e));
            }
        }
    }
}
