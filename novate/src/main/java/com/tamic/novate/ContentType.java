package com.tamic.novate;

import static okhttp3.MultipartBody.FORM;

/**
 * Created by Tamic on 2017-06-23.
 */
public enum ContentType {

    /**
     * application/json
     */
    JSON,
    /**
     *text
     */
    TEXT,
    /**
     *AUDIO
     */
    AUDIO,
    /**
     *
     */
    VIDEO,
    /**
     *image
     */
    IMAGE,
    /**
     *java
     */
    JAVA,
    /**
     *msg
     */
    MESSAGE,
    /**
     *application/vnd.android.package-archive
     */
    APK,
    /**
     *multipart/form-data
     */
    FORM
}
