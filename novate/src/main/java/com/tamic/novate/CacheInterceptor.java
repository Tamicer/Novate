
/*
 *    Copyright (C) 2016 Tamic
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

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tamic.novate.util.NetworkUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * caheInterceptor
 * Created by Tamic on 2016-08-09.
 */
public class CacheInterceptor implements Interceptor {

    protected Context context;
    protected String cacheControlValue_Offline;
    protected String cacheControlValue_Online;
    //set cahe times is 3 days
    protected static final int maxStale = 60 * 60 * 24 * 3;
    // read from cache for 60 s
    protected static final int maxStaleOnline = 60;

    public CacheInterceptor(Context context) {
        this(context, String.format("max-age=%d", maxStaleOnline));
    }

    public CacheInterceptor(Context context, String cacheControlValue) {
        this(context, cacheControlValue, String.format("max-age=%d", maxStale));
    }

    public CacheInterceptor(Context context, String cacheControlValueOffline, String cacheControlValueOnline) {
        this.context = context;
        this.cacheControlValue_Offline = cacheControlValueOffline;
        this.cacheControlValue_Online = cacheControlValueOnline;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();


        okhttp3.Response originalResponse = chain.proceed(chain.request());
        String cacheControl = originalResponse.header("Cache-Control");
        //String cacheControl = request.cacheControl().toString();
        Log.e("Novate", maxStaleOnline + "s load cache:" + cacheControl);
        if (TextUtils.isEmpty(cacheControl) || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                cacheControl.contains("must-revalidate") || cacheControl.contains("max-age") || cacheControl.contains("max-stale")) {
            return originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + maxStale)
                    .build();

        } else {
            return originalResponse;
        }
    }
           /* Response response = chain.proceed(request);
            String cacheControl = request.cacheControl().toString();

            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, " + cacheOnlineControlValue)
                    .build();
        } */

       /*else {
            *//*((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, R.string.load_cache, Toast.LENGTH_SHORT).show();
                }
            });*//*
            Log.e("Novate", " no network load cache");
          *//*  request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build();*//*
            Response response = chain.proceed(request);
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, only-if-cached, " + cacheControlValue)
                    .build();
        }
    }*/
}
