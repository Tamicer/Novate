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

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.tamic.novate.callback.ResponseCallback;
import com.tamic.novate.config.ConfigLoader;
import com.tamic.novate.cookie.NovateCookieManager;
import com.tamic.novate.cache.CookieCacheImpl;
import com.tamic.novate.download.DownLoadCallBack;
import com.tamic.novate.download.DownSubscriber;
import com.tamic.novate.cookie.SharedPrefsCookiePersistor;
import com.tamic.novate.exception.NovateException;
import com.tamic.novate.request.NovateRequest;
import com.tamic.novate.request.NovateRequestBody;
import com.tamic.novate.request.RequestInterceptor;
import com.tamic.novate.response.NovateResponseBody;
import com.tamic.novate.util.FileUtil;
import com.tamic.novate.util.LogWraper;
import com.tamic.novate.util.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.Cache;
import okhttp3.CertificatePinner;
import okhttp3.ConnectionPool;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.Part;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Novate adapts a Java interface to Retrofit call by using annotations on the declared methods to
 * define how requests are made. Create instances using {@linkplain Builder
 * the builder} and pass your interface to {@link #} to generate an implementation.
 * <p/>
 * For example,
 * <pre>{@code
 * Novate novate = new Novate.Builder()
 *     .baseUrl("http://api.example.com")
 *     .addConverterFactory(GsonConverterFactory.create())
 *     .build();
 * <p/>
 * MyApi api = Novate.create(MyApi.class);
 * Response<User> user = api.getUser().execute();
 * }</pre>
 *
 * @author Tamic (skay5200@163.com)
 */
public final class Novate {

    private static Map<String, String> headers;
    private static Map<String, String> parameters;
    private static Retrofit.Builder retrofitBuilder;
    private static Retrofit retrofit;
    private static OkHttpClient.Builder okhttpBuilder;
    public static BaseApiService apiManager;
    private static OkHttpClient okHttpClient;
    private static Context mContext;
    private final okhttp3.Call.Factory callFactory;
    private final String baseUrl;
    private final List<Converter.Factory> converterFactories;
    private final List<CallAdapter.Factory> adapterFactories;
    private final Executor callbackExecutor;
    private final boolean validateEagerly;
    private Observable<ResponseBody> downObservable;
    private Map<Object, Observable<ResponseBody>> downMaps = new HashMap<Object, Observable<ResponseBody>>() {
    };
    private Observable.Transformer exceptTransformer = null;
    private static final int DEFAULT_TIMEOUT = 15;
    private static final int DEFAULT_MAXIDLE_CONNECTIONS = 5;
    private static final long DEFAULT_KEEP_ALIVEDURATION = 8;
    private static final long DEFAULT_CACHEMAXSIZE = 10 * 1024 * 1024;
    public static final String TAG = "Novate";

    /**
     * Mandatory constructor for the Novate
     */
    Novate(okhttp3.Call.Factory callFactory, String baseUrl, Map<String, String> headers,
           Map<String, String> parameters, BaseApiService apiManager,
           List<Converter.Factory> converterFactories, List<CallAdapter.Factory> adapterFactories,
           Executor callbackExecutor, boolean validateEagerly) {
        this.callFactory = callFactory;
        this.baseUrl = baseUrl;
        this.headers = headers;
        this.parameters = parameters;
        this.apiManager = apiManager;
        this.converterFactories = converterFactories;
        this.adapterFactories = adapterFactories;
        this.callbackExecutor = callbackExecutor;
        this.validateEagerly = validateEagerly;
    }

    /**
     * create ApiService
     */
    public <T> T create(final Class<T> service) {

        return retrofit.create(service);
    }

    /**
     * @param subscriber
     */
    public <T> T call(Observable<T> observable, BaseSubscriber<T> subscriber) {
        return (T) observable.compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * @param subscriber
     */
    public <T> T execute(NovateRequest request,  BaseSubscriber<T> subscriber) {
        return call(request, subscriber);
    }

    private <T> T call(NovateRequest request,  BaseSubscriber<T> subscriber) {
        //todo dev
        //okHttpClient.newCall().execute();
     return null;
    }

    /**
     * Novate execute get
     * <p>
     * return parsed data
     * <p>
     * you don't need to parse ResponseBody
     */
    public <T> T executeGet(final String url, final Map<String, Object> maps, final ResponseCallBack<T> callBack) {
        return (T) apiManager.executeGet(url, maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new NovateSubscriber<T>(mContext, callBack));
    }

    /**
     * Novate execute get request
     * @param url path or url
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxGet(final String url,  ResponseCallback<T, ResponseBody> callBack) {
        return rxGet(url, url, null, callBack);
    }
    /**
     * Novate execute get request
     * @param url path or url
     * @param maps parameters  maps
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxGet(final String url, final Map<String, Object> maps, ResponseCallback<T, ResponseBody> callBack) {
        return rxGet(url, url, maps, callBack);
    }

    /**
     * Novate execute get request
     * @param tag request tag
     * @param url path or url
     * @param maps parameters  maps
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxGet(final String tag, final String url, @NonNull final Map<String, Object> maps, final ResponseCallback<T, ResponseBody> callBack) {

        if(maps == null) {
            throw new NullPointerException(" maps is not null!");
        }
        return (T) apiManager.executeGet(url, maps)
                .compose(new OndoTransformer(tag, callBack))
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack).addContext(mContext));
    }

    /**
     * Novate execute post request
     * @param url path or url
     * @param maps parameters  maps
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxPost(final String url, @FieldMap(encoded = true) Map<String, Object> maps, ResponseCallback<T, ResponseBody> callBack) {
        return rxPost(url, url, maps, callBack);
    }

    /**
     * Novate execute Post request
     * @param tag request tag
     * @param url path or url
     * @param maps parameters  maps
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxPost(String tag, final String url, @FieldMap(encoded = true) Map<String, Object> maps, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.executePost(url, maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack).addContext(mContext));
    }


    /**
     * Novate execute Put request
     * @param url path or url
     * @param maps parameters  maps
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxPut(final String url, final @FieldMap(encoded = true) Map<String, T>  maps, ResponseCallback<T, ResponseBody> callBack) {
        return rxPut(url, url, maps, callBack);
    }


    /**
     * Novate execute Put request
     * @param tag request tag
     * @param url path or url
     * @param maps parameters  maps
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxPut(String tag, final String url, final @FieldMap(encoded = true) Map<String, T>  maps, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.executePut(url, (Map<String, Object>) maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack).addContext(mContext));
    }

    /**
     * Novate execute Delete request
     * @param url path or url
     * @param maps parameters  maps
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxDelete(final String url, final Map<String, T> maps, ResponseCallback<T, ResponseBody> callBack) {
        return rxDelete(url, url, maps, callBack);
    }


    /**
     * Novate execute Delete request
     * @param tag request tag
     * @param url path or url
     * @param maps parameters  maps
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxDelete(String tag, final String url, final Map<String, T> maps, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.executeDelete(url, (Map<String, Object>) maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack).addContext(mContext));
    }


    /**
     *  Novate rxUpload by post With Part
     *
     * 默认上传图片，
     * @param url url
     * @param file file
     * @param <T>
     * @return Rxjava Subscription
     */
    public <T> T rxUploadWithPart(String tag, String url, File file, ResponseCallback<T, ResponseBody> callBack) {

        return rxUploadWithPart(tag, url, ContentType.IMAGE, file, callBack);
    }

    /**
     *  Novate rxUpload by post With Part
     *
     * 默认上传图片，
     * @param url url
     * @param file file
     * @param <T>
     * @return Rxjava Subscription
     */
    public <T> T rxUploadWithPart(String url, File file, ResponseCallback<T, ResponseBody> callBack) {

        return rxUploadWithPart(url, url, ContentType.IMAGE, file, callBack);
    }


    /**
     * @param tag request tag
     * @param url   request url
     * @param type request ContentType See {@link ContentType}
     * @param file  file
     * @param callBack
     * @param <T> T
     * @return Rxjava Subscription
     */
    public <T> T rxUploadWithPart(Object tag, String url, ContentType type, File file, ResponseCallback<T, ResponseBody> callBack) {
        if (!file.exists()) {
            throw new Resources.NotFoundException(file.getPath() + "file 路径无法找到");
        }
        if (callBack == null) {
            callBack = ResponseCallback.CALLBACK_DEFAULT;
        }
        NovateRequestBody requestBody = Utils.createRequestBody(file, type, callBack);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("image", file.getName(), requestBody);

        return rxUploadWithPart(tag, url, body, callBack);    }


    /**
     *  Novate rxUpload by post With Part
     * @param url path or url
     * @param requestBody  requestBody
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxUploadWithPart(String url, MultipartBody.Part requestBody, ResponseCallback<T, ResponseBody> callBack) {
        return rxUploadWithPart(url, url, requestBody, callBack);
    }

    /**
     * Novate rxUpload by post With Part
     * @param tag request tag
     * @param url path or url
     * @param requestBody requestBody
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxUploadWithPart(Object tag, String url, MultipartBody.Part requestBody, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.uploadFlieWithPart(url, requestBody)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack).addContext(mContext));
    }

    /**
     *  Novate rxUpload by post
     * @param url path or url
     * @param description description
     * @param requestBody  requestBody
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxUpload(String url, RequestBody description, MultipartBody.Part requestBody, ResponseCallback<T, ResponseBody> callBack) {
        return rxUpload(url, url, description, requestBody, callBack);
    }

    /**
     * Novate rxUpload by post
     * @param tag request tag
     * @param url path or url
     * @param description description
     * @param requestBody     MultipartBody.Part
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Rxjava Subscription
     */
    public <T> T rxUpload(Object tag, String url, RequestBody description, MultipartBody.Part requestBody, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.uploadFlie(url, description, requestBody)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack).addContext(mContext));
    }


    /**
     *  Novate rxUpload by post With Body
     *
     * 默认上传图片，更多UploadWithBody(Object tag, String url, ContentType type, File file, ResponseCallback<T, ResponseBody> callBack)
     * @param url url
     * @param file file
     * @param <T>
     * @return Rxjava Subscription
     */
    public <T> T rxUploadWithBody(String url, File file, ResponseCallback<T, ResponseBody> callBack) {

        return rxUploadWithBody(url, url, ContentType.IMAGE, file, callBack);
    }

    /**
     *  Novate rxUpload by post With Body
     *
     *
     * 默认上传图片，更多UploadWithBody(Object tag, String url, ContentType type, File file, ResponseCallback<T, ResponseBody> callBack)
     * @param tag tag
     * @param url url
     * @param file file
     * @param <T>
     * @return Rxjava Subscription
     */
    public <T> T rxUploadWithBody(Object tag, String url, File file, ResponseCallback<T, ResponseBody> callBack) {

        return rxUploadWithBody(tag, url, ContentType.IMAGE, file, callBack);
    }


    /**
     * @param tag
     * @param url
     * @param type  request ContentType See {@link ContentType}
     * @param file
     * @param callBack ResponseCallback
     * @param <T> T
     * @return RxJava 1.X Subscription
     */
    public <T> T rxUploadWithBody(Object tag, String url, ContentType type, File file, ResponseCallback<T, ResponseBody> callBack) {
        if (!file.exists()) {
            throw new Resources.NotFoundException(file.getPath() + "file 路径无法找到");
        }
        //NovateRequestBody  requestFile = Utils.createRequestBody(file, type, callBack);
        if (callBack == null) {
            callBack = ResponseCallback.CALLBACK_DEFAULT;
        }
        callBack.setTag(tag);
        return rxUploadWithBody(tag, url, Utils.createRequestBody(file, type, callBack), callBack);
    }


    /**
     *  Novate rxUpload by post With Body
     * @param url url
     * @param requestBody requestBody
     * @param callBack back
     * @param <T>
     * @return Rxjava Subscription
     */
    public <T> T rxUploadWithBody(String url, NovateRequestBody requestBody, ResponseCallback<T, ResponseBody> callBack) {
        return rxUploadWithBody(url, url, requestBody, callBack);
    }

    /**
     *  Novate rxUpload by post With Body
     * @param tag tag
     * @param url url
     * @param requestBody requestBody
     * @param callBack back
     * @param <T>
     * @return Rxjava Subscription
     */
    public <T> T rxUploadWithBody(Object tag, String url, RequestBody requestBody, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.postRequestBody(url, requestBody)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack).addContext(mContext));
    }

    /**
     *  Novate rxUpload by post Body Maps
     * @param url url
     * @param maps File files
     * @param callBack back
     * @param <T>
     * @return Rxjava Subscription
     */
    public <T> T rxUploadWithPartMapByFile(String url, Map<String, File> maps, ResponseCallback<T, ResponseBody> callBack) {
        return rxUploadWithPartMapByFile(url, url, ContentType.IMAGE, maps, callBack);
    }


    /**
     *  Novate rxUpload by post Body Maps
     * @param url url
     * @param maps RequestBody files
     * @param callBack back
     * @param <T>
     * @return Rxjava Subscription
     */
    public <T> T rxUploadWithBodyMap(String url, Map<String, RequestBody> maps, ResponseCallback<T, ResponseBody> callBack) {
        return rxUploadWithBodyMap(url, url,maps, callBack);
    }


    /**
     * Novate rxUpload by post With PartMaps
     * @param tag tag
     * @param url url
     * @param maps File files
     * @param callBack ResponseCallback
     * @param <T>
     * @return Rxjava 1.x Subscription
     */
    public <T> T rxUploadWithPartMapByFile(Object tag, String url, ContentType type, Map<String, File> maps, ResponseCallback<T, ResponseBody> callBack) {

        if (callBack == null) {
            callBack = ResponseCallback.CALLBACK_DEFAULT;
        }
        return (T) apiManager.uploadFlieWithPartMap(url, Utils.createParts("image", maps, type, callBack))
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack).addContext(mContext));
    }

    /**
     *  Novate rxUpload by post PartBody List
     * @param url url
     * @param list File files
     * @param callBack back
     * @param <T>
     * @return Rxjava Subscription
     */
    public <T> T rxUploadWithPartListByFile(String url, List<File> list, ResponseCallback<T, ResponseBody> callBack) {
        return rxUploadWithPartListByFile(url, url, ContentType.IMAGE, list, callBack);
    }


    /**
     *  Novate rxUpload by post PartBody List
     * @param url url
     * @param list RequestBody files
     * @param callBack back
     * @param <T>
     * @return Rxjava Subscription
     */
    public <T> T rxUploadWithPartListByFile(Object tag, String url, List<File> list, ResponseCallback<T, ResponseBody> callBack) {
        return rxUploadWithPartListByFile(tag, url, ContentType.IMAGE, list, callBack);
    }

    /**
     * Novate Novate rxUpload by post PartBody List
     * @param tag tag
     * @param url url
     * @param list File files
     * @param callBack ResponseCallback
     * @param <T>
     * @return Rxjava 1.x Subscription
     */
    public <T> T rxUploadWithPartListByFile(Object tag, String url, ContentType type, List<File> list, ResponseCallback<T, ResponseBody> callBack) {

        if (callBack == null) {
            callBack = ResponseCallback.CALLBACK_DEFAULT;
        }
        return (T) apiManager.uploadFlieWithPartList(url, Utils.createPartLists("image", list, type, callBack))
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack).addContext(mContext));
    }

    /**
     *  Novate rxUpload by post Body Maps
     * @param url url
     * @param maps RequestBody files
     * @param callBack ResponseCallback
     * @param <T>
     * @return Rxjava Subscription
     */
    public <T> T rxUploadWithBodyMapByFile(String url, Map<String, File> maps, ResponseCallback<T, ResponseBody> callBack) {
        return rxUploadWithBodyMapByFile(url, url, ContentType.IMAGE, maps, callBack);
    }

    /**
     *  Novate rxUploadWithBodyMapByFile
     * @param tag tag
     * @param url  url
     * @param maps maps
     * @param callBack  ResponseCallback
     * @param <T>
     * @return Rxjava Subscription
     */
    public <T> T rxUploadWithBodyMapByFile(Object tag, String url, Map<String, File> maps, ResponseCallback<T, ResponseBody> callBack) {
        return rxUploadWithBodyMapByFile(tag, url, ContentType.IMAGE, maps, callBack);
    }

    /**
     * Novate rxUpload by post With BodyMaps
     * @param tag tag
     * @param url url
     * @param maps File files
     * @param callBack ResponseCallback
     * @param <T>
     * @return Rxjava 1.x Subscription
     */
    public <T> T rxUploadWithBodyMapByFile(Object tag, String url, ContentType type, Map<String, File> maps, ResponseCallback<T, ResponseBody> callBack) {
        Map<String, RequestBody> bodys = new HashMap<>();

        if (callBack == null) {
            callBack = ResponseCallback.CALLBACK_DEFAULT;
        }

        if (maps != null && maps.size() > 0) {
            Iterator<String> keys = maps.keySet().iterator();
            NovateRequestBody requestBody = null;
            while(keys.hasNext()){
                String i = keys.next();
                File file = maps.get(i);
                if (FileUtil.exists(file)) {
                    throw new Resources.NotFoundException(file.getPath() + "file 路径无法找到");
                } else {
                    requestBody = Utils.createRequestBody(file, type, callBack);
                    bodys.put(i, requestBody);
                }
            }
        }

        return (T) apiManager.uploadFiles(url, bodys)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack).addContext(mContext));
    }


    /**
     * Novate rxUpload by post With BodyMaps
     * @param tag tag
     * @param url url
     * @param maps RequestBody files
     * @param callBack ResponseCallback
     * @param <T>
     * @return Rxjava 1.x Subscription
     */
    public <T> T rxUploadWithBodyMap(Object tag, String url, Map<String, RequestBody> maps, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.uploadFiles(url, maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack).addContext(mContext));
    }

    /**
     * Novate rxUpload by post With BodyMaps
     * @param url url
     * @param files  MultipartBody.Part files
     * @param callBack
     * @param <T>
     * @return Rxjava 1.x Subscription
     */
    public <T> T rxUploadWithPartMap(String url, Map<String, MultipartBody.Part> files, ResponseCallback<T, ResponseBody> callBack) {
        return rxUploadWithPartMap(url, url, files, callBack);
    }

    /**
     * Novate rxUpload by post With BodyMaps
     * @param tag tag
     * @param url url
     * @param files MultipartBody.Part files
     * @param callBack ResponseCallback
     * @param <T>
     * @return Rxjava 1.x Subscription
     */
    public <T> T rxUploadWithPartMap(Object tag, String url, Map<String, MultipartBody.Part> files, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.uploadFlieWithPartMap(url, files)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack).addContext(mContext));
    }

    /**
     * Rx Download file
     * @param callBack
     */
    public <T> T rxDownload(String url, final ResponseCallback callBack) {
       return rxDownload(url, url, callBack);
    }

    /**
     * Rx Download file
     * @param tag request Tag
     * @param callBack
     */
    public <T> T rxDownload(Object tag, String url, final ResponseCallback callBack) {
       /* okhttpBuilder.networkInterceptors().add(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder().body(
                        new NovateResponseBody(originalResponse.body(), callBack))
                        .build();
            }
        });

        Retrofit retrofit = retrofitBuilder.client(okhttpBuilder.build()).build();
        BaseApiService apiManager = retrofit.create(BaseApiService.class);*/

        if (downMaps.get(tag) == null) {
            downObservable = apiManager.downloadFile(url);
        } else {
            downObservable = downMaps.get(tag);
        }
        downMaps.put(tag, downObservable);
        return (T) downObservable.compose(schedulersTransformerDown)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack));

    }

    /**
     * Novate Post by Form
     * @param url path or url
     * @param parameters  parameters  maps
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Rxjav 1.x Subscription
     */
    public <T> T rxForm(String url, @FieldMap(encoded = true) Map<String, Object> parameters, ResponseCallback<T, ResponseBody> callBack) {
        return rxForm(url, url, parameters, callBack);
    }

    /**
     * Novate Post by Form
     * @param tag request tag
     * @param url path or url
     * @param parameters  parameters  maps
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxForm(Object tag, String url, @FieldMap(encoded = true) Map<String, Object> parameters, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.postForm(url, parameters)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack));
    }

    /**
     * Novate  Post by Body
     * @param url path or url
     * @param bean Object bean
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxBody(String url, Object bean, ResponseCallback<T, ResponseBody> callBack) {
        return rxBody(url, url, bean, callBack);
    }

    /**
     * Novate  Post by Body
     * @param tag request tag
     * @param url path or url
     * @param bean Object bean
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxBody(Object tag, String url, Object bean,  ResponseCallback<T, ResponseBody> callBack) {
       return (T) apiManager.executePostBody(url, bean)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack).addContext(mContext));
    }


    /**
     * Novate  Post by Json
     * @param url path or url
     * @param jsonString   jsonString
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxJson(String url, String jsonString, ResponseCallback<T, ResponseBody> callBack) {
        return rxJson(url, url, jsonString, callBack);
    }


    /**
     * Novate  Post by Json
     * @param tag request tag
     * @param url path or url
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxJson(Object tag, String url, String jsonString, ResponseCallback<T, ResponseBody> callBack) {
        return (T)apiManager.postRequestBody(url, Utils.createJson(jsonString))
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack).addContext(mContext));
    }


    /**
     * Novate execute get
     * <p>
     * return parsed data
     * <p>
     * you don't need to parse ResponseBody
     */
    public <T> T executeGet(Class<T> entityClass, final String url, final Map<String, Object> maps, final ResponseCallBack<T> callBack) {
        return (T) apiManager.executeGet(url, maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new NovateSubscriber<T>(mContext,callBack));
    }


    /**
     * RXJAVA schedulersTransformer
     * AndroidSchedulers.mainThread()
     */
    final Observable.Transformer onDdoTransformer = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).doOnUnsubscribe(new Action0() {
                @Override
                public void call() {

                }
            });
        }
    };

    private class OndoTransformer implements Observable.Transformer{


        private ResponseCallback callback;
        private Object tag;

        public OndoTransformer(Object tag, ResponseCallback callback) {
            this.tag = tag;
            this.callback = callback;
        }

        @Override
        public Object call(Object observable) {
            return ((Observable) observable).doOnUnsubscribe(new Action0() {
                @Override
                public void call() {
                  /*  if (callback != null) {
                        callback.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onCancel(tag, new Throwable(null, -100, "请求被取消了！"));
                            }
                        });
                    }*/
                }
            });
        }
    }

    /**
     * RXJAVA schedulersTransformer
     * AndroidSchedulers.mainThread()
     */
    final Observable.Transformer schedulersTransformer = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    /**
     * RXJAVA schedulersTransformer
     *
     * Schedulers.io()
     */
    final Observable.Transformer schedulersTransformerDown = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io());
        }
    };

    /**
     * handleException Transformer
     * @param <T>
     * @return  Transformer
     */
    public <T> Observable.Transformer<NovateResponse<T>, T> handleErrTransformer() {

        if (exceptTransformer != null) return exceptTransformer;
        else return exceptTransformer = new Observable.Transformer() {
            @Override
            public Object call(Object observable) {
                return ((Observable) observable)/*.map(new HandleFuc<T>())*/.onErrorResumeNext(new HttpResponseFunc<T>());
            }
        };
    }


    /**
     * HttpResponseFunc
     * @param <T> Observable
     */
    private static class HttpResponseFunc<T> implements Func1<java.lang.Throwable, Observable<T>> {
        @Override
        public Observable<T> call(java.lang.Throwable t) {
            return Observable.error(NovateException.handleException(t));
        }
    }

    /**  T
     * @param <T> response
     */
    private class HandleFuc<T> implements Func1<NovateResponse<T>, T> {
        @Override
        public T call(NovateResponse<T> response) {
            if (response == null || (response.getData() == null && response.getResult() == null)) {
                throw new JsonParseException("后端数据不对");
            }
            /*if (!response.isOk()) {
                throw new RuntimeException(response.getCode() + "" + response.getMsg() != null ? response.getMsg() : "");
            }
*/
            return response.getData();
        }
    }

    /**
     * Retroift get
     *
     * @param url
     * @param maps
     * @param subscriber
     * @param <T>
     * @return no parse data
     */
    public <T> T get(String url, Map<String, Object> maps, BaseSubscriber<ResponseBody> subscriber) {
        return (T) apiManager.executeGet(url, maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * /**
     * Novate executePost
     *
     * @return no parse data
     * <p>
     * you must to be parse ResponseBody
     * <p>
     * <p/>
     * For example,
     * <pre>{@code
     * Novate novate = new Novate.Builder()
     *     .baseUrl("http://api.example.com")
     *     .addConverterFactory(GsonConverterFactory.create())
     *     .build();
     *
     * novate.post("url", parameters, new BaseSubscriber<ResponseBody>(context) {
     *    @Override
     *   public void onError(Throwable e) {
     *
     *   }
     *
     *  @Override
     *  public void onNext(ResponseBody responseBody) {
     *
     *   // todo you need to parse responseBody
     *
     *  }
     *  });
     * <p/>
     *
     * }</pre>
     */
    public <T> T post(String url, @FieldMap(encoded = true) Map<String, Object> parameters, BaseSubscriber<ResponseBody> subscriber) {
        return (T) apiManager.executePost(url, (Map<String, Object>) parameters)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * Novate executePost
     *
     * @return parsed data
     * you don't need to   parse ResponseBody
     */
    public <T> T executePost(final String url, @FieldMap(encoded = true) Map<String, Object> parameters, final ResponseCallBack<T> callBack) {

        return (T) apiManager.executePost(url, parameters)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new NovateSubscriber<T>(mContext, callBack));
    }


    /**
     * Novate Post by Form
     *
     * @param url
     * @param subscriber
     */
    public <T> T form(String url, @FieldMap(encoded = true) Map<String, Object> fields, Subscriber<ResponseBody> subscriber) {
        return (T) apiManager.postForm(url, fields)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }


    /**
     * Novate execute Post by Form
     *
     * @return parsed data
     * you don't need to   parse ResponseBody
     */
    public <T> T executeForm(final String url, final @FieldMap(encoded = true) Map<String, Object> fields, final ResponseCallBack<T> callBack) {
        return (T) apiManager.postForm(url, fields)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new NovateSubscriber<T>(mContext, callBack));
    }


    /**
     * http Post by Body
     * you  need to parse ResponseBody
     *
     * @param url
     * @param subscriber
     */
    public void body(String url, Object body, Subscriber<ResponseBody> subscriber) {
        apiManager.executePostBody(url, body)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * http execute Post by body
     *
     * @return parsed data
     * you don't need to   parse ResponseBody
     */
    public <T> T executeBody(final String url, final Object body, final ResponseCallBack<T> callBack) {
        final Type[] types = callBack.getClass().getGenericInterfaces();
            return (T) apiManager.executePostBody(url, body)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new NovateSubscriber<T>(mContext, callBack));
    }


    /**
     * http Post by json
     * you  need to parse ResponseBody
     *
     * @param url
     * @param jsonStr    Json String
     * @param subscriber
     */
    public<T> T  json(String url, String jsonStr, Subscriber<ResponseBody> subscriber) {
        return (T) apiManager.postRequestBody(url, Utils.createJson(jsonStr))
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * http execute Post by Json
     *
     * @param url
     * @param jsonStr Json String
     * @return parsed data
     * you don't need to   parse ResponseBody
     */
    public <T> T executeJson(final String url, final String jsonStr, final ResponseCallBack<T> callBack) {
            return (T) apiManager.postRequestBody(url, Utils.createJson(jsonStr))
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new NovateSubscriber<T>(mContext, callBack));
    }

    /**
     * Novate delete
     *
     * @param url
     * @param maps
     * @param subscriber
     * @param <T>
     * @return no parse data
     */
    public <T> T delete(String url, Map<String, T> maps, BaseSubscriber<ResponseBody> subscriber) {
        return (T) apiManager.executeDelete(url, (Map<String, Object>) maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * Novate Execute http by Delete
     *
     * @return parsed data
     * you don't need to   parse ResponseBody
     */
    public <T> T executeDelete(final String url, final Map<String, T> maps, final ResponseCallBack<T> callBack) {
        final Type[] types = callBack.getClass().getGenericInterfaces();

        return (T) apiManager.executeDelete(url, (Map<String, Object>) maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new NovateSubscriber<T>(mContext, callBack));
    }

    /**
     * Novate put
     *
     * @param url
     * @param parameters
     * @param subscriber
     * @param <T>
     * @return no parse data
     */
    public <T> T put(String url, final @FieldMap(encoded = true) Map<String, T> parameters, BaseSubscriber<ResponseBody> subscriber) {
        return (T) apiManager.executePut(url, (Map<String, Object>) parameters)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * Novate Execute  Http by Put
     *
     * @return parsed data
     * you don't need to parse ResponseBody
     */
    public <T> T executePut(final String url, final @FieldMap(encoded = true) Map<String, T> parameters, final ResponseCallBack<T> callBack) {

        return (T) apiManager.executePut(url, (Map<String, Object>) parameters)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new NovateSubscriber<T>(mContext, callBack));
    }


    /**
     * Novate Test
     *
     * @param url        url
     * @param maps       maps
     * @param subscriber subscriber
     * @param <T>        T
     * @return
     */
    public <T> T test(String url, Map<String, T> maps, Subscriber<ResponseBody> subscriber) {
        return (T) apiManager.getTest(url, (Map<String, Object>) maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * Novate upload
     *
     * @param url
     * @param requestBody requestBody
     * @param subscriber  subscriber
     * @param <T>         T
     * @return
     */
    public <T> T upload(String url, RequestBody requestBody, Subscriber<ResponseBody> subscriber) {
        return (T) apiManager.postRequestBody(url, requestBody)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * uploadImage
     *
     * @param url        url
     * @param file       file
     * @param subscriber
     * @param <T>
     * @return
     */
    public <T> T uploadImage(String url, File file, Subscriber<ResponseBody> subscriber) {
        return (T) apiManager.upLoadImage(url, Utils.createImage(file))
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * Novate upload Flie
     *
     * @param url
     * @param file       file
     * @param subscriber subscriber
     * @param <T>        T
     * @return
     */
    public <T> T uploadFlie(String url, RequestBody file, Subscriber<ResponseBody> subscriber) {
        return (T) apiManager.postRequestBody(url, file)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * Novate upload Flie
     *
     * @param url
     * @param file       file
     * @param subscriber subscriber
     * @param <T>        T
     * @return
     */
    public <T> T uploadFlie(String url, RequestBody description, MultipartBody.Part file, Subscriber<ResponseBody> subscriber) {
        return (T) apiManager.uploadFlie(url, description, file)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * Novate upload Flies
     *
     * @param url
     * @param subscriber subscriber
     * @param <T>        T
     * @return
     */
    public <T> T uploadFlies(String url, Map<String, RequestBody> files, Subscriber<ResponseBody> subscriber) {
        return (T) apiManager.uploadFiles(url, files)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }


    /**
     * Novate upload Flies WithPartMap
     * @param url
     * @param partMap
     * @param file
     * @param subscriber
     * @param <T>
     * @return
     */
    public <T> T uploadFileWithPartMap(String url, Map<String, RequestBody> partMap,
                                       @Part("file") MultipartBody.Part file, Subscriber<ResponseBody> subscriber) {
        return (T) apiManager.uploadFileWithPartMap(url, partMap, file)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }


    /**
     * Novate download
     *
     * @param url
     * @param callBack
     */
    public <T> T download(String url, DownLoadCallBack callBack) {

        return download(url, FileUtil.getFileNameWithURL(url), callBack);
    }

    /**
     * @param url
     * @param name
     * @param callBack
     */
    public <T> T download(String url, String name, DownLoadCallBack callBack) {
        return download(FileUtil.generateFileKey(url, name), url, null, name, callBack);
    }

    /**
     * downloadMin
     *
     * @param url
     * @param callBack
     */
    public <T> T  downloadMin(String url, DownLoadCallBack callBack) {
        return downloadMin(FileUtil.generateFileKey(url, FileUtil.getFileNameWithURL(url)), url, callBack);
    }

    /**
     * downloadMin
     * @param key  key
     * @param url url
     * @param callBack CallBack
     */
    public <T> T downloadMin(String key, String url, DownLoadCallBack callBack) {
        return downloadMin(key, url, FileUtil.getFileNameWithURL(url), callBack);
    }

    /**
     * downloadMin
     * @param key key
     * @param url down url
     * @param name name
     * @param callBack callBack
     */
    public <T> T downloadMin(String key, String url, String name, DownLoadCallBack callBack) {
        return downloadMin(key, url, null, name, callBack);
    }

    /**
     * download small file
     * @param key
     * @param url
     * @param savePath
     * @param name
     * @param callBack
     */
    public <T> T  downloadMin(String key, String url, String savePath, String name, DownLoadCallBack callBack) {

        if(TextUtils.isEmpty(key)) {
            key = FileUtil.generateFileKey(url, FileUtil.getFileNameWithURL(url));
        }

        if (downMaps.get(key) == null) {
            downObservable = apiManager.downloadSmallFile(url);
        } else {
            downObservable = downMaps.get(key);
        }
        downMaps.put(key, downObservable);
        return executeDownload(key, url, savePath, name, callBack);
    }


    /**
     * @param key
     * @param url
     * @param savePath
     * @param name
     * @param callBack
     */
    public <T> T download(String key, String url, String savePath, String name, DownLoadCallBack callBack) {
        if(TextUtils.isEmpty(key)) {
            key = FileUtil.generateFileKey(url, FileUtil.getFileNameWithURL(url));
        }
        return executeDownload(key, url, savePath, name, callBack);
    }

    /**
     * executeDownload
     * @param key
     * @param savePath
     * @param name
     * @param callBack
     */
    private <T> T executeDownload(String key, String url, String savePath, String name, final DownLoadCallBack callBack) {
        /*if (NovateDownLoadManager.isDownLoading) {
            downMaps.get(key).unsubscribeOn(Schedulers.io());
            NovateDownLoadManager.isDownLoading = false;
            NovateDownLoadManager.isCancel = true;
            return;
        }*/
        //NovateDownLoadManager.isDownLoading = true;

        if (downMaps.get(key) == null) {
            downObservable = apiManager.downloadFile(url);
        } else {
            downObservable = downMaps.get(key);
        }
        downMaps.put(key, downObservable);

      /*  okhttpBuilder.networkInterceptors().add(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder().body(
                        new NovateResponseBody(originalResponse.body(), callBack))
                        .build();

            }
        });*/

        return (T) downObservable.compose(schedulersTransformerDown)
                    .compose(handleErrTransformer())
                    .subscribe(new DownSubscriber<ResponseBody>(key, savePath, name, callBack, mContext));

    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    /**
     * Mandatory Builder for the Builder
     */
    public static final class Builder {
        private int connectTimeout = DEFAULT_TIMEOUT;
        private int writeTimeout = DEFAULT_TIMEOUT;
        private int readTimeout = DEFAULT_TIMEOUT;
        private int default_maxidle_connections = DEFAULT_MAXIDLE_CONNECTIONS;
        private long default_keep_aliveduration = DEFAULT_MAXIDLE_CONNECTIONS;
        private long caheMaxSize = DEFAULT_CACHEMAXSIZE;
        private okhttp3.Call.Factory callFactory;
        private String baseUrl;
        private Boolean isLog = false;
        private Object tag;
        private Boolean isCookie = false;
        private Boolean isCache = true;
        private List<InputStream> certificateList;
        private HostnameVerifier hostnameVerifier;
        private CertificatePinner certificatePinner;
        private List<Converter.Factory> converterFactories = new ArrayList<>();
        private List<CallAdapter.Factory> adapterFactories = new ArrayList<>();
        private Executor callbackExecutor;
        private boolean validateEagerly;
        private Context context;
        private NovateCookieManager cookieManager;
        private Cache cache = null;
        private Proxy proxy;
        private File httpCacheDirectory;
        private SSLSocketFactory sslSocketFactory;
        private ConnectionPool connectionPool;
        private Converter.Factory converterFactory;
        private CallAdapter.Factory callAdapterFactory;
        private Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR;
        private Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR_OFFLINE;

        public Builder(Context context) {
            // Add the base url first. This prevents overriding its behavior but also
            // ensures correct behavior when using novate that consume all types.
            okhttpBuilder = new OkHttpClient.Builder();
            retrofitBuilder = new Retrofit.Builder();
            if(context instanceof Activity) {
                this.context  = ((Activity) context).getApplicationContext();
            } else {
                this.context = context;
            }
        }

        public Builder(Novate novate) {

        }

        /**
         * The HTTP client used for requests. default OkHttpClient
         * <p/>
         * This is a convenience method for calling {@link #callFactory}.
         * <p/>
         * Note: This method <b>does not</b> make a defensive copy of {@code client}. Changes to its
         * settings will affect subsequent requests. Pass in a {@linkplain OkHttpClient#clone() cloned}
         * instance to prevent this if desired.
         */
        @NonNull
        public Builder client(OkHttpClient client) {
            retrofitBuilder.client(Utils.checkNotNull(client, "client == null"));
            return this;
        }

        /**
         * Add ApiManager for serialization and deserialization of objects.
         *//*
        public Builder addApiManager(final Class<ApiManager> service) {

            apiManager = retrofit.create((Utils.checkNotNull(service, "apiManager == null")));
            //return retrofit.create(service);
            return this;
        }*/

        /**
         * Specify a custom call factory for creating {@link } instances.
         * <p/>
         * Note: Calling {@link #client} automatically sets this value.
         */
        public Builder callFactory(okhttp3.Call.Factory factory) {
            this.callFactory = Utils.checkNotNull(factory, "factory == null");
            return this;
        }

        /**
         * Sets the default connect timeout for new connections. A value of 0 means no timeout,
         * otherwise values must be between 1 and {@link Integer#MAX_VALUE} when converted to
         * milliseconds.
         */
        public Builder connectTimeout(int timeout) {
            return connectTimeout(timeout, TimeUnit.SECONDS);
        }

        /**
         * Sets the default connect timeout for new connections. A value of 0 means no timeout,
         * otherwise values must be between 1 and {@link Integer#MAX_VALUE} when converted to
         * milliseconds.
         */
        public Builder writeTimeout(int timeout) {
            return writeTimeout(timeout, TimeUnit.SECONDS);
        }


        /**
         * Sets the default read timeout for new connections. A value of 0 means no timeout, otherwise
         * values must be between 1 and {@link Integer#MAX_VALUE} when converted to milliseconds.
         */
        public Builder readTimeout(int timeout) {
            return readTimeout(timeout, TimeUnit.SECONDS);
        }

        /**
         * Attaches {@code tag} to the request. It can be used later to cancel the request. If the tag
         * is unspecified or null, the request is canceled by using the request itself as the tag.
         */
        public Builder tag(Object tag) {
            this.tag = tag;
            return this;
        }

        /**
         * open default logcat
         *
         * @param isLog
         * @return
         */
        public Builder addLog(boolean isLog) {
            this.isLog = isLog;
            return this;
        }

        /**
         * open sync default Cookie
         *
         * @param isCookie
         * @return
         */
        public Builder addCookie(boolean isCookie) {
            this.isCookie = isCookie;
            return this;
        }

        /**
         * open default Cache
         *
         * @param isCache
         * @return
         */
        public Builder addCache(boolean isCache) {
            this.isCache = isCache;
            return this;
        }

        public Builder proxy(Proxy proxy) {
            this.proxy = proxy;
            okhttpBuilder.proxy(Utils.checkNotNull(proxy, "proxy == null"));
            return this;
        }

        /**
         * Sets the default write timeout for new connections. A value of 0 means no timeout,
         * otherwise values must be between 1 and {@link TimeUnit #MAX_VALUE} when converted to
         * milliseconds.
         * TimeUnit {@link TimeUnit}
         */
        public Builder writeTimeout(int timeout, TimeUnit unit) {
            this.writeTimeout = Utils.checkDuration("timeout", timeout, unit);
            if (timeout != -1) {
                okhttpBuilder.writeTimeout(timeout, unit);
            }
            return this;
        }


        /**
         * Sets the default read timeout for new connections. A value of 0 means no timeout, otherwise
         * values must be between 1 and {@link Integer#MAX_VALUE} when converted to milliseconds.
         * TimeUnit {@link TimeUnit}
         */
        public Builder readTimeout(int timeout, TimeUnit unit) {
            this.readTimeout = Utils.checkDuration("timeout", timeout, unit);
            if (timeout != -1) {
                okhttpBuilder.readTimeout(readTimeout, unit);
            }
            return this;
        }

        /**
         * Sets the connection pool used to recycle HTTP and HTTPS connections.
         * <p>
         * <p>If unset, a new connection pool will be used.
         * <p>
         *  connectionPool =
         *   new ConnectionPool(DEFAULT_MAXIDLE_CONNECTIONS, DEFAULT_KEEP_ALIVEDURATION, TimeUnit.SECONDS);
         * <p>
         */
        public Builder connectionPool(ConnectionPool connectionPool) {
            if (connectionPool == null) throw new NullPointerException("connectionPool == null");
            this.connectionPool = connectionPool;
            return this;
        }

        /**
         * Sets the default connect timeout for new connections. A value of 0 means no timeout,
         * otherwise values must be between 1 and {@link TimeUnit #MAX_VALUE} when converted to
         * milliseconds.
         * TimeUnit {@link TimeUnit}
         */
        public Builder connectTimeout(int timeout, TimeUnit unit) {
            this.readTimeout = Utils.checkDuration("timeout", timeout, unit);;
            if (timeout != -1) {
                okhttpBuilder.connectTimeout(readTimeout, unit);
            }
            return this;
        }


        /**
         * Set an API base URL which can change over time.
         *
         * @see (HttpUrl)
         */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = Utils.checkNotNull(baseUrl, "baseUrl == null");
            return this;
        }

        /**
         * Add converter factory for serialization and deserialization of objects.
         */
        public Builder addConverterFactory(Converter.Factory factory) {
            this.converterFactory = factory;
            return this;
        }

        /**
         * Add a call adapter factory for supporting service method return types other than {@link CallAdapter
         * }.
         */
        public Builder addCallAdapterFactory(CallAdapter.Factory factory) {
            this.callAdapterFactory = factory;
            return this;
        }

        /**
         * Add Header for serialization and deserialization of objects.
         */
        public <T> Builder addHeader(Map<String, T> headers) {
            okhttpBuilder.addInterceptor(new BaseInterceptor(Utils.checkNotNull(headers, "header == null")));
            return this;
        }

        /**
         * Add parameters for serialization and deserialization of objects.
         */
        public <T> Builder addParameters(Map<String, T> parameters) {
            okhttpBuilder.addInterceptor(new BaseInterceptor(Utils.checkNotNull(parameters, "parameters == null")));
            return this;
        }

        /**
         * Returns a modifiable list of interceptors that observe a single network request and response.
         * These interceptors must call {@link Interceptor.Chain#proceed} exactly once: it is an error
         * for a network interceptor to short-circuit or repeat a network request.
         */
        public Builder addInterceptor(Interceptor interceptor) {
            okhttpBuilder.addInterceptor(Utils.checkNotNull(interceptor, "interceptor == null"));
            return this;
        }

        /**
         * The executor on which {@link Call} methods are invoked when returning {@link Call} from
         * your service method.
         * <p/>
         * Note: {@code executor} is not used for {@linkplain #addCallAdapterFactory custom method
         * return types}.
         */
        public Builder callbackExecutor(Executor executor) {
            this.callbackExecutor = Utils.checkNotNull(executor, "executor == null");
            return this;
        }

        /**
         * When calling {@link #create} on the resulting {@link Retrofit} instance, eagerly validate
         * the configuration of all methods in the supplied interface.
         */
        public Builder validateEagerly(boolean validateEagerly) {
            this.validateEagerly = validateEagerly;
            return this;
        }

        /**
         * Sets the handler that can accept cookies from incoming HTTP responses and provides cookies to
         * outgoing HTTP requests.
         * <p/>
         * <p>If unset, {@linkplain NovateCookieManager#NO_COOKIES no cookies} will be accepted nor provided.
         */
        public Builder cookieManager(NovateCookieManager cookie) {
            if (cookie == null) throw new NullPointerException("cookieManager == null");
            this.cookieManager = cookie;
            return this;
        }

        /**
         *
         */
        public Builder addSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }

        public Builder addHostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }

        public Builder addCertificatePinner(CertificatePinner certificatePinner) {
            this.certificatePinner = certificatePinner;
            return this;
        }


        /**
         * Sets the handler that can accept cookies from incoming HTTP responses and provides cookies to
         * outgoing HTTP requests.
         * <p/>
         * <p>If unset, {@linkplain NovateCookieManager#NO_COOKIES no cookies} will be accepted nor provided.
         */
        public Builder addSSL(String[] hosts, int[] certificates) {
            if (hosts == null) throw new NullPointerException("hosts == null");
            if (certificates == null) throw new NullPointerException("ids == null");


            addSSLSocketFactory(NovateHttpsFactroy.getSSLSocketFactory(context, certificates));
            addHostnameVerifier(NovateHttpsFactroy.getHostnameVerifier(hosts));
            return this;
        }

        public Builder addNetworkInterceptor(Interceptor interceptor) {
            okhttpBuilder.addNetworkInterceptor(interceptor);
            return this;
        }

        /**
         * setCache
         *
         * @param cache cahe
         * @return Builder
         */
        public Builder addCache(Cache cache) {
            int maxStale = 60 * 60 * 24 * 3;
            return addCache(cache, maxStale);
        }

        /**
         * @param cache
         * @param cacheTime ms
         * @return
         */
        public Builder addCache(Cache cache, final int cacheTime) {
            addCache(cache, String.format("max-age=%d", cacheTime));
            return this;
        }

        /**
         * @param cache
         * @param cacheControlValue Cache-Control
         * @return
         */
        private Builder addCache(Cache cache, final String cacheControlValue) {
            REWRITE_CACHE_CONTROL_INTERCEPTOR = new CacheInterceptor(mContext, cacheControlValue);
            REWRITE_CACHE_CONTROL_INTERCEPTOR_OFFLINE = new CacheInterceptorOffline(mContext, cacheControlValue);
            addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR);
            addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR_OFFLINE);
            addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR_OFFLINE);
            this.cache = cache;
            return this;
        }

        /**
         * Create the {@link Retrofit} instance using the configured values.
         * <p/>
         * Note: If neither {@link #client} nor {@link #callFactory} is called a default {@link
         * OkHttpClient} will be created and used.
         */
        public Novate build() {

            if (baseUrl == null) {
                throw new IllegalStateException("Base URL required.");
            }

            if (okhttpBuilder == null) {
                throw new IllegalStateException("okhttpBuilder required.");
            }

            if (retrofitBuilder == null) {
                throw new IllegalStateException("retrofitBuilder required.");
            }
            /** set Context. */
            mContext = context;
           /**
            * ConfigLoader.init.
            * */
            ConfigLoader.init(context);
            /**
             * Set a fixed API base URL.
             *
             * @see #baseUrl(HttpUrl)
             */
            retrofitBuilder.baseUrl(baseUrl);

            /** Add converter factory for serialization and deserialization of objects. */
            if (converterFactory == null) {
                converterFactory = GsonConverterFactory.create();
            }
            ;

            retrofitBuilder.addConverterFactory(converterFactory);
            /**
             * Add a call adapter factory for supporting service method return types other than {@link
             * Call}.
             */
            if (callAdapterFactory == null) {
                callAdapterFactory = RxJavaCallAdapterFactory.create();
            }
            retrofitBuilder.addCallAdapterFactory(callAdapterFactory);

            LogWraper.setDebug(isLog && !BuildConfig.DEBUG);

            if (tag != null) {
                okhttpBuilder.addInterceptor(new RequestInterceptor<>(tag));
            }

            if (isLog) {
                okhttpBuilder.addNetworkInterceptor(
                        new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS));

                okhttpBuilder.addNetworkInterceptor(
                        new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
            }

            if (sslSocketFactory != null) {
                okhttpBuilder.sslSocketFactory(sslSocketFactory);
            }

            if (hostnameVerifier != null) {
                okhttpBuilder.hostnameVerifier(hostnameVerifier);
            }


            if (httpCacheDirectory == null) {
                httpCacheDirectory = new File(mContext.getCacheDir(), "Novate_Http_cache");
            }

            if (isCache) {
                try {
                    if (cache == null) {
                        cache = new Cache(httpCacheDirectory, caheMaxSize);
                    }
                    addCache(cache);

                } catch (Exception e) {
                    Log.e("OKHttp", "Could not create http cache", e);
                }
                if (cache == null) {
                    cache = new Cache(httpCacheDirectory, caheMaxSize);
                }
            }

            if (cache != null) {
                okhttpBuilder.cache(cache);
            }
            /**
             * Sets the default write timeout for new connections. A value of 0 means no timeout, otherwise
             * values must be between 1 and {@link Integer#MAX_VALUE} when converted to milliseconds.
             */

            //okhttpBuilder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
            /**
             * Sets the default connect timeout for new connections. A value of 0 means no timeout,
             * otherwise values must be between 1 and {@link Integer#MAX_VALUE} when converted to
             * milliseconds.
             */
            //okhttpBuilder.connectTimeout(connectTimeout, TimeUnit.SECONDS);
            /**
             * Sets the default read timeout for new connections. A value of 0 means no timeout, otherwise
             * values must be between 1 and {@link Integer#MAX_VALUE} when converted to milliseconds.
             */
            //okhttpBuilder.readTimeout(readTimeout, TimeUnit.SECONDS);
            /**
             * Sets the connection pool used to recycle HTTP and HTTPS connections.
             *
             * <p>If unset, a new connection pool will be used.
             */
            if (connectionPool == null) {
                connectionPool = new ConnectionPool(default_maxidle_connections, default_maxidle_connections, TimeUnit.SECONDS);
            }
            okhttpBuilder.connectionPool(connectionPool);

            /**
             * Sets the HTTP proxy that will be used by connections created by this client. This takes
             * precedence over {@link #proxySelector}, which is only honored when this proxy is null (which
             * it is by default). To disable proxy use completely, call {@code setProxy(Proxy.NO_PROXY)}.
             */
            if (proxy != null) {
                okhttpBuilder.proxy(proxy);
            }

            /**
             * Sets the handler that can accept cookies from incoming HTTP responses and provides cookies to
             * outgoing HTTP requests.
             *
             * <p>If unset, {@link Novate NovateCookieManager#NO_COOKIES no cookies} will be accepted nor provided.
             */
            if (isCookie && cookieManager == null) {
                //okhttpBuilder.cookieJar(new NovateCookieManger(context));
                okhttpBuilder.cookieJar(new NovateCookieManager(new CookieCacheImpl(), new SharedPrefsCookiePersistor(context)));

            }

            if (cookieManager != null) {
                okhttpBuilder.cookieJar(cookieManager);
            }

            /**
             *okhttp3.Call.Factory callFactory = this.callFactory;
             */
            if (callFactory != null) {
                retrofitBuilder.callFactory(callFactory);
            }


            /**
             * create okHttpClient
             */
            okHttpClient = okhttpBuilder.build();
            /**
             * set Retrofit client
             */

            retrofitBuilder.client(okHttpClient);

            /**
             * create Retrofit
             */
            retrofit = retrofitBuilder.build();

            /**
             *create BaseApiService;
             */
            apiManager = retrofit.create(BaseApiService.class);

            return new Novate(callFactory, baseUrl, headers, parameters, apiManager, converterFactories, adapterFactories,
                    callbackExecutor, validateEagerly);
        }
    }





   /**
     * ResponseCallBack <T> Support your custom data model
    * 兼容1.3.3.2以下版本 更高以上版本已过时
     */
   @Deprecated
    public interface ResponseCallBack<T> {

        public void onStart();

        public void onCompleted();

        public abstract void onError(Throwable e);

        @Deprecated
        public abstract void onSuccee(NovateResponse<T> response);

        public void onsuccess(int code, String msg, T response, String originalResponse);

    }
}


