package com.tamic.novate;


import java.io.UnsupportedEncodingException;

import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * Created by TAMIC on 2017-11-16.
 */

public abstract class AbsRequestInterceptor implements Interceptor {

    public enum Type {
        ADD, UPDATE, REMOVE
    }

    public Type control;

    public Type getControlType() {
        return control;
    }

    public void setControlType(Type control) {
        this.control = control;
    }

    abstract Request interceptor(Request request) throws UnsupportedEncodingException;
}
