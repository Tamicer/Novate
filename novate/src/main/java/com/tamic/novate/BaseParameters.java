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


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

/**
 * BaseHeaderInterceptor，use set okhttp call header
 * Created by Tamic on 2016-06-30.
 */
public class BaseParameters<T> extends AbsRequestInterceptor {

    private Map<String, T> parameters;


    public BaseParameters(Map<String, T> headers) {
        this(headers, Type.ADD);
    }

    public BaseParameters(Map<String, T> headers, Type type) {
        this.parameters = headers;
        control = type;
    }
    
    @Override
    public Response intercept(Chain chain) throws IOException {

        return chain.proceed(interceptor(chain.request()));

    }

    @Override
    Request interceptor(Request original) throws UnsupportedEncodingException {
        Request.Builder builder1 = original.newBuilder();
        HttpUrl originalHttpUrl = original.url();
        HttpUrl.Builder builder = originalHttpUrl.newBuilder();

        if (parameters != null && parameters.size() > 0) {
            Set<String> keys = parameters.keySet();
            switch (super.control) {
                case ADD:
                    for (String headerKey : keys) {
                        builder.addQueryParameter(headerKey, parameters.get(headerKey) == null ? "" : (String) parameters.get(headerKey)).build();
                    }
                    break;
                case UPDATE:
                    for (String headerKey : keys) {
                        builder.setQueryParameter(headerKey, parameters.get(headerKey) == null ? "" : (String) parameters.get(headerKey)).build();
                    }
                case REMOVE:
                    for (String headerKey : keys) {
                        builder.removeAllQueryParameters(headerKey).build();
                    }
                    break;
            }

        }
        HttpUrl url = builder.build();
        Request.Builder requestBuilder = original.newBuilder()
                .url(url);
        return requestBuilder.build();
    }
}
