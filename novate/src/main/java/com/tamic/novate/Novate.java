package com.tamic.novate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.tamic.novate.util.Utils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
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
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.BaseUrl;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
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
    private NovateSubscriber novateSubscriber;
    private Observable<ResponseBody> downObservable;
    private Map<String, Observable<ResponseBody>> downMaps = new HashMap<String, Observable<ResponseBody>>(){};
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
    public <T> T call(Observable<T> observable, Subscriber<T> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return null;
    }


    /**
     * Retroift execute get
     *
     * return parsed data
     *
     *  you don't need to parse ResponseBody
     *
     */
    public <T> T executeGet(final String url, final Map<String, String> maps, final ResponseCallBack<T> callBack) {

        final Type[] types = callBack.getClass().getGenericInterfaces();

        if (MethodHandler(types) == null && MethodHandler(types).size() == 0) {
            return null;

        }
        final Type finalNeedType = MethodHandler(types).get(0);
        Log.d("dd", "-->:" + "Type:" + types[0]);


        apiManager.executeGet(url, maps)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NovateSubscriber<T>(mContext, finalNeedType, callBack));
        return null;

    }

    /**
     * MethodHandler
     */
    private List<Type> MethodHandler(Type[] types) {
        Log.d(TAG, "types size: " + types.length);
        List<Type> needtypes = new ArrayList<>();
        Type needParentType = null;

        for (Type paramType : types) {
            System.out.println("  " + paramType);
            // if Type is T
            if (paramType instanceof ParameterizedType) {
                Type[] parentypes = ((ParameterizedType) paramType).getActualTypeArguments();
                Log.d(TAG, "TypeArgument: ");
                for (Type childtype : parentypes) {
                    Log.d(TAG, "childtype:" + childtype);
                    needtypes.add(childtype);
                    //needParentType = childtype;
                    if (childtype instanceof ParameterizedType) {
                        Type[] childtypes = ((ParameterizedType) childtype).getActualTypeArguments();
                        for (Type type : childtypes) {
                            needtypes.add(type);
                            //needChildType = type;
                            Log.d(TAG, "type:" + childtype);
                        }
                    }
                }
            }
        }
        return needtypes;
    }


    /**
     * Retroift get
     * @param url
     * @param maps
     * @param subscriber
     * @param <T>
     * @return  no parse data
     */
    public <T> T get(String url, Map<String, String> maps, BaseSubscriber<ResponseBody> subscriber) {

        apiManager.executeGet(url, maps)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        return null;
    }
    /**
     * /**
     * Retroift executePost
     *
     * @return  no parse data
     *
     * you must to be parse ResponseBody
     *
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
    public void post(String url, Map<String, String> parameters, Subscriber<ResponseBody> subscriber) {
        apiManager.executePost(url, parameters)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * Retroift executePost
     *
     * @return parsed data
     *  you don't need to   parse ResponseBody
     *
     */
    public <T> T executePost(final String url, final Map<String, String> maps, final ResponseCallBack<T> callBack) {
        final Type[] types = callBack.getClass().getGenericInterfaces();


        if (MethodHandler(types) == null && MethodHandler(types).size() == 0) {
            return null;
        }
        final Type finalNeedType = MethodHandler(types).get(0);
        Log.d(TAG, "-->:" + "Type:" + types[0]);
        apiManager.executePost(url, maps)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NovateSubscriber<T>(mContext, finalNeedType, callBack));
        return null;
    }

    /**
     * Execute http by Delete
     * @return parsed data
     *  you don't need to   parse ResponseBody
     *
     */
    public <T> T executeDelete(final String url, final Map<String, String> maps, final ResponseCallBack<T> callBack) {
        final Type[] types = callBack.getClass().getGenericInterfaces();


        if (MethodHandler(types) == null && MethodHandler(types).size() == 0) {
            return null;
        }
        final Type finalNeedType = MethodHandler(types).get(0);
        Log.d(TAG, "-->:" + "Type:" + types[0]);
        apiManager.executeDelete(url, maps)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NovateSubscriber<T>(mContext, finalNeedType, callBack));
        return null;
    }

    /**
     * Execute  Http by Put
     * @return parsed data
     *  you don't need to parse ResponseBody
     *
     */
    public <T> T executePut(final String url, final Map<String, String> maps, final ResponseCallBack<T> callBack) {
        final Type[] types = callBack.getClass().getGenericInterfaces();


        if (MethodHandler(types) == null && MethodHandler(types).size() == 0) {
            return null;
        }
        final Type finalNeedType = MethodHandler(types).get(0);
        Log.d(TAG, "-->:" + "Type:" + types[0]);
        apiManager.executePut(url, maps)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NovateSubscriber<T>(mContext, finalNeedType, callBack));
        return null;
    }


    /**
     * Test
     * @param url url
     * @param maps maps
     * @param subscriber subscriber
     * @param <T> T
     * @return
     */
    public <T> T test(String url, Map<String, String> maps, Subscriber<ResponseBody> subscriber) {

        apiManager.getTest(url, maps)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        return null;
    }

    /**
     * upload
     * @param url
     * @param requestBody requestBody
     * @param subscriber subscriber
     * @param <T> T
     * @return
     */
    public <T> T upload(String url, RequestBody requestBody, Subscriber<ResponseBody> subscriber) {
        apiManager.upLoadFile(url, requestBody)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(subscriber);
        return null;
    }


    /**
     * download
     * @param url
     * @param callBack
     */
    public void download(String url, DownLoadCallBack callBack) {

        if (downMaps.get(url) == null) {
            downObservable = apiManager.downloadFile(url);
            downMaps.put(url, downObservable);
        } else {
            downObservable = downMaps.get(url);
        }

        if (NovateDownLoadManager.isDownLoading) {
            downObservable.unsubscribeOn(Schedulers.io());
            NovateDownLoadManager.isDownLoading = false;
            NovateDownLoadManager.isCancel = true;
            return;
        }
        NovateDownLoadManager.isDownLoading = true;
        downObservable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new DownSubscriber<ResponseBody>(callBack, mContext));

    }

    /**
     * Mandatory Builder for the Builder
     */
    public static final class Builder {

        private static final int DEFAULT_TIMEOUT = 5;
        private static final int DEFAULT_MAXIDLE_CONNECTIONS = 5;
        private static final long  DEFAULT_KEEP_ALIVEDURATION = 8;
        private static final long caheMaxSize = 10 * 1024 * 1024;

        private okhttp3.Call.Factory callFactory;
        private String baseUrl;
        private Boolean isLog = false;
        private List<InputStream> certificateList;
        private HostnameVerifier hostnameVerifier;
        private CertificatePinner certificatePinner;
        private List<Converter.Factory> converterFactories = new ArrayList<>();
        private List<CallAdapter.Factory> adapterFactories = new ArrayList<>();
        private Executor callbackExecutor;
        private boolean validateEagerly;
        private Context context;
        private NovateCookieManger cookieManager;
        private Cache cache = null;
        private Proxy proxy;
        private File httpCacheDirectory;
        private SSLSocketFactory sslSocketFactory;
        private ConnectionPool connectionPool;
        private Converter.Factory converterFactory;
        private CallAdapter.Factory callAdapterFactory;

        public Builder(Context context) {
            // Add the base url first. This prevents overriding its behavior but also
            // ensures correct behavior when using novate that consume all types.
            okhttpBuilder = new OkHttpClient.Builder();
            retrofitBuilder = new Retrofit.Builder();
            this.context = context;

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

        public Builder addLog(boolean isLog) {
            this.isLog = isLog;
            return this;
        }

        public Builder proxy(Proxy proxy) {
            okhttpBuilder.proxy(Utils.checkNotNull(proxy, "proxy == null"));
            return this;
        }
        /**
         * Sets the default write timeout for new connections. A value of 0 means no timeout,
         * otherwise values must be between 1 and {@link TimeUnit #MAX_VALUE} when converted to
         * milliseconds.
         */
        public Builder writeTimeout(int timeout, TimeUnit unit) {
            if (timeout != -1) {
                okhttpBuilder.writeTimeout(timeout, unit);
            } else {
                okhttpBuilder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return this;
        }

        /**
         * Sets the connection pool used to recycle HTTP and HTTPS connections.
         *
         * <p>If unset, a new connection pool will be used.
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
         */
        public Builder connectTimeout(int timeout, TimeUnit unit) {
            if (timeout != -1) {
                okhttpBuilder.connectTimeout(timeout, unit);
            } else {
                okhttpBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return this;
        }


        /**
         * Set an API base URL which can change over time.
         *
         * @see BaseUrl(HttpUrl)
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
        public Builder addHeader(Map<String, String> headers) {
            okhttpBuilder.addInterceptor(new BaseInterceptor((Utils.checkNotNull(headers, "header == null"))));
            return this;
        }

        /**
         * Add parameters for serialization and deserialization of objects.
         */
        public Builder addParameters(Map<String, String> parameters) {
            okhttpBuilder.addInterceptor(new BaseInterceptor((Utils.checkNotNull(parameters, "parameters == null"))));
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
         * <p>If unset, {@linkplain NovateCookieManger#NO_COOKIES no cookies} will be accepted nor provided.
         */
        public Builder cookieManager(NovateCookieManger cookie) {
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
         * <p>If unset, {@linkplain NovateCookieManger#NO_COOKIES no cookies} will be accepted nor provided.
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
         * @param cache cahe
         * @return  Builder
         */
        public Builder addCache(Cache cache) {
            int maxStale = 60 * 60 * 24 * 3;
            return addCacheAge(cache, maxStale);
        }

        /**
         * @return
         */
        public Builder addCacheAge(Cache cache, final int cacheTime) {
            addCache(cache, String.format("max-age=%d", cacheTime));
            return this;
        }

        /**
         * @param cache
         * @param cacheTime ms
         * @return
         */
        public Builder addCacheStale(Cache cache, final int cacheTime) {
            addCache(cache, String.format("max-stale=%d", cacheTime));
            return this;
        }

        /**
         * @param cache
         * @param cacheControlValue Cache-Control值
         * @return
         */
        public Builder addCache(Cache cache, final String cacheControlValue) {
            Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new CaheInterceptor(mContext, cacheControlValue);
            addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR);
            addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR);
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

            if (isLog) {
                okhttpBuilder.addNetworkInterceptor(
                        new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS));
            }

            if(sslSocketFactory != null) {
                okhttpBuilder.sslSocketFactory(sslSocketFactory);
            }

            if (hostnameVerifier != null) {
                okhttpBuilder.hostnameVerifier(hostnameVerifier);
            }


            if ( httpCacheDirectory == null) {
                httpCacheDirectory = new File(mContext.getCacheDir(), "Novate_Http_cache");
            }

            try {
                if (cache == null) {
                    cache = new Cache(httpCacheDirectory, caheMaxSize);
                }
            } catch (Exception e) {
                Log.e("OKHttp", "Could not create http cache", e);
            }


            okhttpBuilder.cache(cache);

            addCache(cache);

            /**
             * Sets the connection pool used to recycle HTTP and HTTPS connections.
             *
             * <p>If unset, a new connection pool will be used.
             */
            if (connectionPool == null) {

                connectionPool = new ConnectionPool(DEFAULT_MAXIDLE_CONNECTIONS, DEFAULT_KEEP_ALIVEDURATION, TimeUnit.SECONDS);
            }
            okhttpBuilder.connectionPool(connectionPool);

            /**
             * Sets the HTTP proxy that will be used by connections created by this client. This takes
             * precedence over {@link #proxySelector}, which is only honored when this proxy is null (which
             * it is by default). To disable proxy use completely, call {@code setProxy(Proxy.NO_PROXY)}.
             */
            if (proxy == null) {
                okhttpBuilder.proxy(proxy);
            }

            /**
             * Sets the handler that can accept cookies from incoming HTTP responses and provides cookies to
             * outgoing HTTP requests.
             *
             * <p>If unset, {@link Novate CookieManager#NO_COOKIES no cookies} will be accepted nor provided.
             */
            if (cookieManager == null) {
                cookieManager = new NovateCookieManger(context);
            }
            okhttpBuilder.cookieJar(new NovateCookieManger(context));

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
     * NovateSubscriber
     * @param <T>
     */
    class NovateSubscriber<T> extends BaseSubscriber<ResponseBody> {

        private ResponseCallBack<T> callBack;

        private Type finalNeedType;

        public NovateSubscriber(Context context, Type finalNeedType, ResponseCallBack<T> callBack) {
            super(context);
            this.callBack = callBack;

            this.finalNeedType = finalNeedType;
        }

        @Override
        public void onError(Throwable e) {
            Log.e("novate", "-->:" + e.getMessage());
            if (callBack != null) {
                callBack.onError(e);
            }
        }

        @Override
        public void onNext(ResponseBody responseBody) {
            try {
                byte[] bytes = responseBody.bytes();
                String jsStr = new String(bytes);
                Log.d("OkHttp", "ResponseBody:" + jsStr);
                if (callBack != null) {
                    try {
                        /**
                         * if need parse baseRespone<T> use ParentType, if parse T use childType . defult parse baseRespone<T>
                         *
                         *  callBack.onSuccee((T) JSON.parseArray(jsStr, (Class<Object>) finalNeedType));
                         *  Type finalNeedType = needChildType;
                         */

                        NovateResponse<T> baseResponse = new Gson().fromJson(jsStr, finalNeedType);

                        if (baseResponse.isOk()) {
                            callBack.onSuccee((T) new Gson().fromJson(jsStr, finalNeedType));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (callBack != null) {
                            callBack.onError(e);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (callBack != null) {
                    callBack.onError(e);
                }
            }
        }
    }

    /**
     * ResponseCallBack <T> Support your custom data model
     *
     *
     */
    public interface ResponseCallBack<T> {

        public void onStart();

        public void onCompleted();

        public abstract void onError(Throwable e);

        public abstract void onSuccee(T response);

    }
}


