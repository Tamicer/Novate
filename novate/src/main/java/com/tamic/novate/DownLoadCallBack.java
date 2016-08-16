package com.tamic.novate;

/**
 * Created by Tamic on 2016-08-02.
 */
public abstract class DownLoadCallBack {

    public void onStart(){}

    public void onCancel(){}

    public void onCompleted(){}

    abstract public void onError(Throwable e);

    public void onProgress(long fileSizeDownloaded){}

    abstract public void onSucess(String path, String name, long fileSize);
}
