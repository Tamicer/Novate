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
import java.lang.*;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import okhttp3.Request;
import okhttp3.Response;

import static com.tamic.novate.AbsRequestInterceptor.Type.ADD;

/**
 * BaseHeaderInterceptor，use set okhttp call header
 * Created by Tamic on 2016-06-30.
 */
public class BaseHeaderInterceptor<T> extends AbsRequestInterceptor {

    private Map<String, T> headers;


    public BaseHeaderInterceptor(Map<String, T> headers) {
        this(headers, ADD);

    }

    public BaseHeaderInterceptor(Map<String, T> headers, Type type) {
        this.headers = headers;
        super.control = type;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        return chain.proceed(interceptor(chain.request()));

    }

    private static String getValueEncoded(String value) throws UnsupportedEncodingException {
        if (value == null) {
            return "null";
        }
        String newValue = value.replace("\n", "");
        for (int i = 0, length = newValue.length(); i < length; i++) {
            char c = newValue.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                return URLEncoder.encode(newValue, "UTF-8");
            }
        }
        return newValue;
    }


    @Override
    Request interceptor(Request request) throws UnsupportedEncodingException {

        Request.Builder builder = request.newBuilder();
        if (headers != null && headers.size() > 0) {
            Set<String> keys = headers.keySet();
            switch (super.control) {
                case ADD:
                    for (String headerKey : keys) {
                        builder.addHeader(headerKey, headers.get(headerKey) == null ? "" : getValueEncoded((String) headers.get(headerKey))).build();
                    }
                    break;
                case UPDATE:
                    for (String headerKey : keys) {
                        builder.header(headerKey, headers.get(headerKey) == null ? "" : getValueEncoded((String) headers.get(headerKey))).build();
                    }
                case REMOVE:
                    break;
            }
        }
        return builder.build();
    }
}
