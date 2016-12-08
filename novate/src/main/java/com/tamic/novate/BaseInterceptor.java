package com.tamic.novate;


import java.io.IOException;
import java.lang.*;
import java.util.Map;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * BaseInterceptor，use set okhttp call header
 * Created by Tamic on 2016-06-30.
 */
public class BaseInterceptor<T> implements Interceptor{

    private Map<String, T> headers;

    public BaseInterceptor(Map<String, T> headers) {
       this.headers = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request.Builder builder = chain.request()
                .newBuilder();
        if (headers != null && headers.size() > 0) {
            Set<String> keys = headers.keySet();
            for (String headerKey : keys) {
                builder.addHeader(headerKey, headers.get(headerKey) == null? "": (String)headers.get(headerKey)).build();
            }
        }
        return chain.proceed(builder.build());

    }
}
