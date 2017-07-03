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
package com.tamic.novate.request;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.tamic.novate.util.Utils;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.CacheControl;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.internal.http.HttpMethod;

/**
 * Created by Tamic on 2016-12-02.
 */
public final class NovateRequest{
    private static NovateRequest.Builder builder;
    private static Map<String, RequestBody> params;
    private final String url;
    private final String method;
    private final Headers headers;
    private final RequestBody body;
    private final Object tag;

    private volatile CacheControl cacheControl; // Lazily initialized.

    private NovateRequest(NovateRequest.Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers.build();
        this.body = builder.body;
        this.tag = builder.tag != null ? builder.tag : this;
    }

    public String url() {
        return url;
    }

    public String method() {
        return method;
    }

    public Headers headers() {
        return headers;
    }

    public String header(String name) {
        return headers.get(name);
    }

    public List<String> headers(String name) {
        return headers.values(name);
    }

    public RequestBody body() {
        return body;
    }

    public Object tag() {
        return tag;
    }

    public NovateRequest.Builder newBuilder() {
        return new NovateRequest.Builder(this);
    }

    /**
     * Returns the cache control directives for this response. This is never null, even if this
     * response contains no {@code Cache-Control} header.
     */
    public CacheControl cacheControl() {
        CacheControl result = cacheControl;
        return result != null ? result : (cacheControl = CacheControl.parse(headers));
    }

    public boolean isHttps() {
        return HttpUrl.parse(url).isHttps();
    }

    @Override
    public String toString() {
        return "Request{method="
                + method
                + ", url="
                + url
                + ", tag="
                + (tag != this ? tag : null)
                + '}';
    }

    public static class Builder {
        private String url;
        private String method;
        private Headers.Builder headers;
        private RequestBody body;
        private Object tag;

        public Builder() {
            this.method = "GET";
            this.headers = new Headers.Builder();
        }

        private Builder(NovateRequest request) {
            this.url = request.url;
            this.method = request.method;
            this.body = request.body;
            this.tag = request.tag;
            this.headers = request.headers.newBuilder();
        }

        public NovateRequest.Builder url(HttpUrl url) {
            if (url == null) throw new NullPointerException("url == null");
            this.url = url.url().toString();
            return this;
        }

        /**
         * Sets the URL target of this request.
         *
         * @throws IllegalArgumentException if {@code url} is not a valid HTTP or HTTPS URL. Avoid this
         *                                  exception by calling {@link HttpUrl#parse}; it returns null for invalid URLs.
         */
        public NovateRequest.Builder url(String url) {
            if (url == null) throw new NullPointerException("url == null");

            // Silently replace websocket URLs with HTTP URLs.
            if (url.regionMatches(true, 0, "ws:", 0, 3)) {
                url = "http:" + url.substring(3);
            } else if (url.regionMatches(true, 0, "wss:", 0, 4)) {
                url = "https:" + url.substring(4);
            }

            HttpUrl parsed = HttpUrl.parse(url);
            if (parsed == null) throw new IllegalArgumentException("unexpected url: " + url);
            return url(parsed);
        }

        /**
         * Sets the URL target of this request.
         *
         * @throws IllegalArgumentException if the scheme of {@code url} is not {@code http} or {@code
         *                                  https}.
         */
        public NovateRequest.Builder url(URL url) {
            if (url == null) throw new NullPointerException("url == null");
            HttpUrl parsed = HttpUrl.get(url);
            if (parsed == null) throw new IllegalArgumentException("unexpected url: " + url);
            return url(parsed);
        }

        /**
         * Sets the header named {@code name} to {@code value}. If this request already has any headers
         * with that name, they are all replaced.
         */
        public NovateRequest.Builder header(String name, String value) {
            headers.set(name, value);
            return this;
        }

        /**
         * Adds a header with {@code name} and {@code value}. Prefer this method for multiply-valued
         * headers like "Cookie".
         * <p>
         * <p>Note that for some headers including {@code Content-Length} and {@code Content-Encoding},
         * OkHttp may replace {@code value} with a header derived from the request body.
         */
        public NovateRequest.Builder addHeader(String name, String value) {
            headers.add(name, value);
            return this;
        }

        /**
         * Sets the header named {@code name} to {@code value}. If this request already has any headers
         * with that name, they are all replaced.
         */
        public NovateRequest.Builder headers(String name, String value) {
            headers.set(name, value);
            return this;
        }

        public NovateRequest.Builder removeHeader(String name) {
            headers.removeAll(name);
            return this;
        }

        /**
         * Removes all headers on this builder and adds {@code headers}.
         */
        public NovateRequest.Builder headers(Map<String, String> headers) {
            if (headers != null && headers.size() > 0) {
                Set<String> keys = headers.keySet();
                for (String headerKey : keys) {
                    this.headers.add(headerKey, headers.get(headerKey) == null? "": headers.get(headerKey));
                }
            }

            return this;
        }

        @NonNull
        public Builder addParameter(String key, Object o) {
           /* RequestBody requestFile =
                    RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), (File)o);
            new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart(key, )
                    .addFormDataPart("password", "12345")
                    .addFormDataPart("atavr", file.getName(), RequestBody.create(MediaType.parse("image*//*"), file))
                    .build();

            if (o instanceof String) {

                bodyput(key, Utils.createText((String) o));
            } else if (o instanceof File) {
                params.put(key + "\"; filename=\"" + ((File) o).getName() + "", Utils.createFile((File) o));
            }*/
            return this;
        }

        /**
         * Sets this request's {@code Cache-Control} header, replacing any cache control headers already
         * present. If {@code cacheControl} doesn't define any directives, this clears this request's
         * cache-control headers.
         */
        public NovateRequest.Builder cacheControl(CacheControl cacheControl) {
            String value = cacheControl.toString();
            if (value.isEmpty()) return removeHeader("Cache-Control");
            return header("Cache-Control", value);
        }

        public NovateRequest.Builder get() {
            return method("GET", null);
        }

        public NovateRequest.Builder head() {
            return method("HEAD", null);
        }

        public NovateRequest.Builder post(RequestBody body) {
            return method("POST", body);
        }

        public NovateRequest.Builder delete(RequestBody body) {
            return method("DELETE", body);
        }

        public NovateRequest.Builder delete() {
            return delete(RequestBody.create(null, new byte[0]));
        }

        public NovateRequest.Builder put(@NonNull RequestBody body) {
            return method("PUT", body);
        }

        public NovateRequest.Builder patch(RequestBody body) {
            return method("PATCH", body);
        }

        public NovateRequest.Builder method(String method, RequestBody body) {
            if (method == null) throw new NullPointerException("method == null");
            if (method.length() == 0) throw new IllegalArgumentException("method.length() == 0");
            if (body != null && !HttpMethod.permitsRequestBody(method)) {
                throw new IllegalArgumentException("method " + method + " must not have a request body.");
            }
            if (body == null && HttpMethod.requiresRequestBody(method)) {
                throw new IllegalArgumentException("method " + method + " must have a request body.");
            }
            this.method = method;
            this.body = body;
            return this;
        }

        /**
         * Attaches {@code tag} to the request. It can be used later to cancel the request. If the tag
         * is unspecified or null, the request is canceled by using the request itself as the tag.
         */
        public NovateRequest.Builder tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public NovateRequest build() {
            if (url == null) throw new IllegalStateException("url == null");
            return new NovateRequest(this);
        }
    }

    private NovateRequest(String url, String method, Headers headers, RequestBody body, Object tag) {
        //private constructor to enforce Singleton pattern
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
        this.tag = tag;
    }


    @NonNull
    public NovateRequest addParameter(String key, Object o) {
        if (o instanceof String) {

            params.put(key, Utils.createText((String) o));
        } else if (o instanceof File) {
            params.put(key + "\"; filename=\"" + ((File) o).getName() + "", Utils.createFile((File) o));
        }

        return this;
    }

    /**
     * @param key
     * @param uris
     * @return
     */
    @NonNull
    public NovateRequest addFilesByUri(String key, List<Uri> uris) {

        for (int i = 0; i < uris.size(); i++) {
            File file = new File(uris.get(i).getPath());
            params.put(key + i + "\"; filename=\"" + file.getName() + "", Utils.createImage(file));
        }

        return this;
    }

    public void cleanParams() {
        if (params != null) {
            params.clear();
        }
    }

}
