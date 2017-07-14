package com.tamic.novate.cookie;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.tamic.novate.util.LogWraper;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.functions.Action1;

/**
 * copy by 孟家敏 on 16/12/8 15:51
 * <p>
 * 邮箱：androidformjm@sina.com
 * Created by Tamic on 2016-12-08.
 *
 */
public class AddCookiesInterceptor implements Interceptor {
    private Context context;
    private String lang;

    public AddCookiesInterceptor(Context context, String language) {
        super();
        this.context = context;
        this.lang = language;

    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (chain == null)
            LogWraper.d("novate", "Addchain == null");
        final Request.Builder builder = chain.request().newBuilder();
        SharedPreferences sharedPreferences = context.getSharedPreferences("cookie", Context.MODE_PRIVATE);
        Observable.just(sharedPreferences.getString("cookie", ""))
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String cookie) {
                        if (cookie.contains("lang=ch")){
                            cookie = cookie.replace("lang=ch","lang="+lang);
                        }
                        if (cookie.contains("lang=en")){
                            cookie = cookie.replace("lang=en","lang="+lang);
                        }
                        LogWraper.d("novate", "AddCookiesInterceptor: "+cookie);
                        builder.addHeader("cookie", cookie);
                    }
                });
        return chain.proceed(builder.build());
    }
}