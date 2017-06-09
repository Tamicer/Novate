package com.tamic.novate.config;


import java.util.HashMap;
import java.util.List;

/**
 * Created by LIUYONGKUI726 on 2017-06-01.
 */

public class Config{


    /**
     * isFormat : false
     * sucessCode : ["1","0","1001"]
     * errorInfo : {"0":"1000","1":"0000"}
     */

    private String isFormat;
    HashMap<String, String> error;
    //private HashMap<String, String> errorInfo;
    private List<String> sucessCode;

    public String getIsFormat() {
        return isFormat;
    }

    public void setIsFormat(String isFormat) {
        this.isFormat = isFormat;
    }

    /*public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }*/

    public HashMap<String, String> getErrorInfo() {
        return error;
    }

    public void setErrorInfo(HashMap<String, String> errorInfo) {
        this.error = errorInfo;
    }

    public List<String> getSucessCode() {
        return sucessCode;
    }

    public void setSucessCode(List<String> sucessCode) {
        this.sucessCode = sucessCode;
    }
}
