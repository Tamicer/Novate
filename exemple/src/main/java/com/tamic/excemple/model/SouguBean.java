package com.tamic.excemple.model;

import java.util.List;

/**
 * Created by Tamic on 2016-08-09.
 */
public class SouguBean {
    private String error;
    private String message;
    private String charge_time;
    private String charge_moeny;
    private String charge_else_moeny;
    private String contract_moeny;
    private String ad_time;
    private String ad_on;
    private String ad_url;
    private String theme_url;
    private String session;

    private List<AppTopImgBean> app_top_img;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCharge_time() {
        return charge_time;
    }

    public void setCharge_time(String charge_time) {
        this.charge_time = charge_time;
    }

    public String getCharge_moeny() {
        return charge_moeny;
    }

    public void setCharge_moeny(String charge_moeny) {
        this.charge_moeny = charge_moeny;
    }

    public String getCharge_else_moeny() {
        return charge_else_moeny;
    }

    public void setCharge_else_moeny(String charge_else_moeny) {
        this.charge_else_moeny = charge_else_moeny;
    }

    public String getContract_moeny() {
        return contract_moeny;
    }

    public void setContract_moeny(String contract_moeny) {
        this.contract_moeny = contract_moeny;
    }

    public String getAd_time() {
        return ad_time;
    }

    public void setAd_time(String ad_time) {
        this.ad_time = ad_time;
    }

    public String getAd_on() {
        return ad_on;
    }

    public void setAd_on(String ad_on) {
        this.ad_on = ad_on;
    }

    public String getAd_url() {
        return ad_url;
    }

    public void setAd_url(String ad_url) {
        this.ad_url = ad_url;
    }

    public String getTheme_url() {
        return theme_url;
    }

    public void setTheme_url(String theme_url) {
        this.theme_url = theme_url;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public List<AppTopImgBean> getApp_top_img() {
        return app_top_img;
    }

    public void setApp_top_img(List<AppTopImgBean> app_top_img) {
        this.app_top_img = app_top_img;
    }

    public static class AppTopImgBean {
        private String IMG_URL;
        private String IMG_NAME;
        private String IMG_HTTP;

        public String getIMG_URL() {
            return IMG_URL;
        }

        public void setIMG_URL(String IMG_URL) {
            this.IMG_URL = IMG_URL;
        }

        public String getIMG_NAME() {
            return IMG_NAME;
        }

        public void setIMG_NAME(String IMG_NAME) {
            this.IMG_NAME = IMG_NAME;
        }

        public String getIMG_HTTP() {
            return IMG_HTTP;
        }

        public void setIMG_HTTP(String IMG_HTTP) {
            this.IMG_HTTP = IMG_HTTP;
        }
    }

    @Override
    public String toString() {
        return "SouguBean{" +
                "error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", charge_time='" + charge_time + '\'' +
                ", charge_moeny='" + charge_moeny + '\'' +
                ", charge_else_moeny='" + charge_else_moeny + '\'' +
                ", contract_moeny='" + contract_moeny + '\'' +
                ", ad_time='" + ad_time + '\'' +
                ", ad_on='" + ad_on + '\'' +
                ", ad_url='" + ad_url + '\'' +
                ", theme_url='" + theme_url + '\'' +
                ", session='" + session + '\'' +
                ", app_top_img=" + app_top_img +
                '}';
    }
}
