package com.tamic.novate.exception;

/**
 * Created by  Tamic on 2016-11-04.
 */

public class FormatException extends RuntimeException {

    public int code = -200;
    public String message = "服务端返回数据格式异常";

    public FormatException() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
