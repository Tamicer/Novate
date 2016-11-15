package com.tamic.novate;

/**
 * Created by Tamic on 2016-11-03.
 */

public class Throwable extends Exception {

    private int code;
    private String message;

    public Throwable(java.lang.Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
