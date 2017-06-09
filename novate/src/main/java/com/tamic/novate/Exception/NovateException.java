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
package com.tamic.novate.exception;

import android.net.ParseException;
import android.util.Log;

import com.google.gson.JsonParseException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import com.tamic.novate.Throwable;
import com.tamic.novate.config.ConfigLoader;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.net.ssl.SSLPeerUnverifiedException;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by Tamic on 2016-08-12.
 */
public class NovateException {

    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;
    private static final int ACCESS_DENIED = 302;
    private static final int HANDEL_ERRROR = 417;

    public static Throwable handleException(java.lang.Throwable e) {

        Log.e("Novate", e.getMessage());
        Throwable ex;

        if (!(e instanceof ServerException) && e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new Throwable(e, ERROR.HTTP_ERROR);
            switch (httpException.code()) {

                case UNAUTHORIZED:
                    ex.setMessage("未授权的请求");
                case FORBIDDEN:
                    ex.setMessage("禁止访问");
                case NOT_FOUND:
                    ex.setMessage("服务器地址未找到");
                case REQUEST_TIMEOUT:
                    ex.setMessage("请求超时");
                case GATEWAY_TIMEOUT:
                    ex.setMessage("网关响应超时");
                case INTERNAL_SERVER_ERROR:
                    ex.setMessage("服务器出错");
                case BAD_GATEWAY:
                    ex.setMessage("无效的请求");
                case SERVICE_UNAVAILABLE:
                    ex.setMessage("服务器不可用");
                case ACCESS_DENIED:
                    ex.setMessage("网络错误");
                case HANDEL_ERRROR:
                    ex.setMessage("接口处理失败");

                default:
                    if (e.getMessage() != null ) {
                        ex.setMessage(e.getMessage());
                        break;
                    }

                    if (e.getLocalizedMessage() != null) {
                        ex.setMessage(e.getLocalizedMessage());
                        break;
                    }
                    ex.setMessage("未知错误");
                    break;
            }
            ex.setCode(httpException.code());
            return ex;
        } else if (e instanceof ServerException) {
            ServerException resultException = (ServerException) e;
            ex = new Throwable(resultException, resultException.code);
            HashMap<String, String> errorConfigs = ConfigLoader.getErrorConfig();
            if (errorConfigs != null && errorConfigs.containsKey(String.valueOf(resultException.code))) {
                ex.setMessage(errorConfigs.get(String.valueOf(resultException.code)));
                return ex;
            }
            ex.setMessage(resultException.getMessage());
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            ex = new Throwable(e, ERROR.PARSE_ERROR);
            ex.setMessage("解析错误");
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new Throwable(e, ERROR.NETWORD_ERROR);
            ex.setMessage("连接失败");
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new Throwable(e, ERROR.SSL_ERROR);
            ex.setMessage("证书验证失败");
            return ex;
        } else if (e instanceof java.security.cert.CertPathValidatorException) {
            Log.e("Novate", e.getMessage());
            ex = new Throwable(e, ERROR.SSL_NOT_FOUND);
            ex.setMessage("证书路径没找到");

            return ex;
        } else if (e instanceof SSLPeerUnverifiedException) {
            Log.e("Novate", e.getMessage());
            ex = new Throwable(e, ERROR.SSL_NOT_FOUND);
            ex.setMessage("无有效的SSL证书");
            return ex;

        } else if (e instanceof ConnectTimeoutException){
            ex = new Throwable(e, ERROR.TIMEOUT_ERROR);
            ex.setMessage("连接超时");
            return ex;
        } else if (e instanceof java.net.SocketTimeoutException) {
            ex = new Throwable(e, ERROR.TIMEOUT_ERROR);
            ex.setMessage("连接超时");
            return ex;
        } else if (e instanceof java.lang.ClassCastException) {
            ex = new Throwable(e, ERROR.FORMAT_ERROR);
            ex.setMessage("类型转换出错");
            return ex;
        } else if (e instanceof NullPointerException) {
            ex = new Throwable(e, ERROR.NULL);
            ex.setMessage("数据有空");
            return ex;
        } else if (e instanceof FormatException) {
            FormatException resultException = (FormatException) e;
            ex = new Throwable(e, resultException.code);
            ex.setMessage(resultException.message);
            return ex;
        } else if (e instanceof UnknownHostException){
            Log.e("Novate", e.getMessage());
            ex = new Throwable(e, NOT_FOUND);
            ex.setMessage("服务器地址未找到,请检查网络或Url");
            return ex;
        } else {
            Log.e("Novate", e.getMessage());
            ex = new Throwable(e, ERROR.UNKNOWN);
            return ex;
        }
    }


    /**
     * 约定异常
     */
    public class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1001;
        /**
         * 网络错误
         */
        public static final int NETWORD_ERROR = 1002;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 1003;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1005;

        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = 1006;

        /**
         * 证书未找到
         */
        public static final int SSL_NOT_FOUND = 1007;

        /**
         * 出现空值
         */
        public static final int NULL = -100;

        /**
         * 格式错误
         */
        public static final int FORMAT_ERROR = 1008;
    }

}

