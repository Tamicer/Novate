package com.tamic.novate;

/**
 * Created by Tamic on 2016-08-02.
 */
public abstract class DownLoadCallBack {

    public void onStart(String key){}

    public void onCancel(){}

    public void onCompleted(){}


    /** Note : the Fun run not MainThred
     * @param e
     */
    abstract public void onError(Throwable e);

    public void onProgress(String key, long fileSizeDownloaded, long  totalSize ){}

    /**  Note : the Fun run UIThred
     * @param path
     * @param name
     * @param fileSize
     */
    abstract public void onSucess(String key, String path, String name, long fileSize);
}
