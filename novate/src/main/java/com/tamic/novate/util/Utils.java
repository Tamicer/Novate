package com.tamic.novate.util;

import android.net.Uri;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.tamic.novate.Novate;
import com.tamic.novate.download.UpLoadCallback;
import com.tamic.novate.request.NovateRequestBody;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * copy  Copyright (C) 2007 The Guava Authors by from retrofit2
 *
 * @author Tamic(https://github.com/NeglectedByBoss)
 */
public class Utils {

    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    public static boolean checkMain() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    public static RequestBody createJson(String jsonString) {
        checkNotNull(jsonString, "json not null!");
        return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonString);
    }

    public static RequestBody createText(String text) {
        checkNotNull(text, "text not null!");
        return RequestBody.create(MediaType.parse("text/plain"), text);
    }

    /**
     * @param name
     * @return
     */
    public static RequestBody createFile(String name) {
        checkNotNull(name, "name not null!");
        return RequestBody.create(okhttp3.MediaType.parse("multipart/form-data; charset=utf-8"), name);
    }

    /**
     * @param file
     * @return
     */
    public static RequestBody createFile(File file) {
        checkNotNull(file, "file not null!");
        return RequestBody.create(okhttp3.MediaType.parse("multipart/form-data; charset=utf-8"), file);
    }

    /**
     * @param file
     * @return
     */
    @NonNull
    public static RequestBody createImage(File file) {
        checkNotNull(file, "file not null!");
        return RequestBody.create(okhttp3.MediaType.parse("image/jpg; charset=utf-8"), file);
    }

    @NonNull
    public static RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                MediaType.parse(MULTIPART_FORM_DATA), descriptionString);
    }

    @NonNull
    public static MultipartBody.Part createPart(String partName ,File file) {
        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);
        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        File file = FileUtil.getUirFile(fileUri);
        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }


    @NonNull
    public static NovateRequestBody createNovateRequestBody(RequestBody requestBody, UpLoadCallback callback) {
        return new NovateRequestBody(requestBody, callback);
    }
}
